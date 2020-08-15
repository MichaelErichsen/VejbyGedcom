package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A household as extracted from a census file
 * 
 * @author Michael Erichsen
 * @version 15. aug. 2020
 *
 */
public class Household {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<Family> families;
	private List<Person> singles;
	private int id;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Id of this household in the census
	 */
	public Household(int id) {
		super();
		rows = new ArrayList<List<String>>();
		families = new ArrayList<Family>();
		singles = new ArrayList<Person>();
		this.id = id;
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the rows
	 */
	public List<List<String>> getRows() {
		return rows;
	}

	/**
	 * @return the singles
	 */
	public List<Person> getSingles() {
		return singles;
	}

	/**
	 * Separate a household into families, defined as father, mother, and
	 * children. Each household contains either a single or one or more families
	 * and perhaps further singles (tenants, lodgers, servants, etc.)
	 * 
	 * Currently only the first family is identified
	 * 
	 * @param individual
	 *            The array of columns in the table
	 * @return message
	 */
	public String identifyFamilies(int[] individual) {
		String sex = "";
		Person person;
		boolean first = true;

		// The first person is the primary person, father or mother according to
		// sex

		LOGGER.log(Level.FINE, "---------------------------------------------------\n");

		// Create a family
		Family family = new Family(id, 1);

		for (List<String> row : rows) {
			LOGGER.log(Level.FINE, row.get(2) + " " + row.get(3) + " " + row.get(5));

			sex = row.get(individual[4]);

			// Create a person from the row
			// Bruges_ikke, Personid, Husstandsnr, Navn, Køn, Fødselsår, Alder,
			// Fødested, Civilstand
			person = new Person(Integer.parseInt(row.get(individual[1])));
			person.setName(row.get(individual[3]));
			person.setSex(sex);
			// String trade = row.get(9);
			// person.setTrades(trade);

			if (individual[5] != 0) {
				person.setBirthDate(row.get(individual[5]));
			} else if (individual[6] != 0) {
				try {
					// Calculate difference between age and census year
					int birthDate = (Integer.parseInt(row.get(individual[10]))
							- Integer.parseInt(row.get(individual[6])));
					person.setBirthDate("Abt. " + birthDate);
				} catch (NumberFormatException e) {
					person.setBirthDate(row.get(individual[6]));
				}
			}

			person.setBirthPlace(row.get(11));

			if (first) {
				if (sex.startsWith("M")) {
					family.setFather(person);
				} else {
					family.setMother(person);
				}
				first = false;
			} else {
				setFamilyRole(family, row.get(individual[8]), person);
			}

			// TODO Add an event for census including a source. Add first person
			// as principal, all others at witnesses

			// TODO Should test next row for family membership

			// If no marriage or children, then it is not a family
			// Identify father, mother and children from the position in
			// household
			// column - or trade column

			// Find other relations and add further families

		}

		families.add(family);

		return "Familie udskilt";
	}

	/**
	 * @param families
	 *            the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * @param family
	 * @param position
	 * @return
	 */
	private void setFamilyRole(Family family, String position, Person person) {
		position = position.toLowerCase();

		if ((position.contains("broder")) || (position.contains("broderdatter")) || (position.contains("brodersøn"))
				|| (position.contains("brødre")) || (position.contains("datterdatter"))
				|| (position.contains("forældre")) || (position.contains("manden")) || (position.contains("moder"))
				|| (position.contains("pleiebarn")) || (position.contains("pleiebørn"))
				|| (position.contains("pleiedatter")) || (position.contains("pleiesøn"))
				|| (position.contains("sønnesøn")) || (position.contains("søster"))
				|| (position.contains("søsterdatter")) || (position.contains("stedfader"))
				|| (position.contains("svigerfader")) || (position.contains("svigerfar"))
				|| (position.contains("svigerforældre")) || (position.contains("svigermoder"))
				|| (position.contains("svigersøn"))) {
			return;
		} else if ((position.contains("hustru")) || (position.contains("kone"))) {
			family.setMother(person);
		} else if ((position.contains("barn")) || (position.contains("børn")) || (position.contains("datter"))
				|| (position.contains("søn"))) {
			family.getChildren().add(person);
		}
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	/**
	 * @param singles
	 *            the singles to set
	 */
	public void setSingles(List<Person> singles) {
		this.singles = singles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getRows().get(0).get(2) + " " + getRows().get(0).get(3);
	}
}
