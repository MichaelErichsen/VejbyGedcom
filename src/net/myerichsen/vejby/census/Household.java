package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author michael
 *
 */
public class Household {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<Family> families;
	private List<Person> singles;

	/**
	 * Constructor
	 */
	public Household() {
		super();
		rows = new ArrayList<List<String>>();
		families = new ArrayList<Family>();
		singles = new ArrayList<Person>();
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
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
	 * @param sexFieldNumber
	 *            The column in the table that contains the sex
	 * @return message
	 */
	public String identifyFamilies(int sexFieldNumber) {
		String sex = "";
		Person person;
		boolean first = true;

		// The first person is the primary person, father or mother according to
		// sex

		LOGGER.log(Level.FINE, "---------------------------------------------------\n");

		// Create a family
		Family family = new Family();

		for (List<String> row : rows) {
			LOGGER.log(Level.FINE, row.get(2) + " " + row.get(3) + " " + row.get(5));

			sex = row.get(sexFieldNumber);

			// Create a person from the row
			// TODO 1845 specific columns
			person = new Person();
			person.setName(row.get(5));
			person.setSex(sex);
			String trade = row.get(9);
			person.setTrades(trade);

			try {
				int birthDate = (1845 - Integer.parseInt(row.get(7)));
				person.setBirthDate("Abt" + birthDate);
			} catch (NumberFormatException e) {
				person.setBirthDate(row.get(7));
			}

			person.setBirthPlace(row.get(11));

			if (first) {

				if (sex.startsWith("M")) {
					family.setFather(person);
				} else {
					family.setMother(person);
				}
			} else {
				setFamilyRole(family, trade, person);
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

		return "Familie udskilt (under udvikling)";
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
}
