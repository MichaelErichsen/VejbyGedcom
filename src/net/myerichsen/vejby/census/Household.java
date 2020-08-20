package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.gedcom.CensusEvent;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * A household as extracted from a census file.
 * 
 * @author Michael Erichsen
 * @version 19. aug. 2020
 *
 */
public class Household {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<Family> families;
	private List<Individual> singles;
	private int id;
	private CensusEvent censusEvent;

	/**
	 * Constructor
	 * 
	 * @param id Id of this household in the census
	 */
	public Household(int id) {
		super();
		rows = new ArrayList<List<String>>();
		families = new ArrayList<Family>();
		singles = new ArrayList<Individual>();
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
	public List<Individual> getSingles() {
		return singles;
	}

	/**
	 * Separate a household into families, defined as father, mother, and children.
	 * Each household contains either a single or one or more families and perhaps
	 * further singles (tenants, lodgers, servants, etc.)
	 * 
	 * Currently only the first family is identified
	 * 
	 * @param mappingKeys The array of columns in the table
	 * @return message
	 */
	public String identifyFamilies(int[] mappingKeys) {
		String sex = "";
		Individual person;
		boolean first = true;

		String sYear = rows.get(0).get(mappingKeys[11]);
		int iYear = Integer.parseInt(sYear.replaceAll("[^0-9]", ""));
		censusEvent = new CensusEvent(iYear, rows.get(0).get(mappingKeys[2]), this);

		// The first person is the primary person, father or mother according to
		// sex

		LOGGER.log(Level.FINE, "---------------------------------------------------\n");

		// Create a family
		Family family = new Family(id, 1);

		for (List<String> row : rows) {
			LOGGER.log(Level.FINE,
					row.get(mappingKeys[1]) + " " + row.get(mappingKeys[4]) + " " + row.get(mappingKeys[5]));

			sex = row.get(mappingKeys[5]);

			// Create a person from the row
			person = new Individual(Integer.parseInt(row.get(mappingKeys[1])));
			person.setName(row.get(mappingKeys[4]));
			person.setSex(sex);
			String trade = row.get(mappingKeys[9]);
			person.setTrades(trade);

			if (mappingKeys[6] != 0) {
				person.setBirthDate(row.get(mappingKeys[6]));
			} else if (mappingKeys[7] != 0) {
				try {
					// Calculate difference between age and census year
					int birthDate = (iYear - Integer.parseInt(row.get(mappingKeys[7])));
					person.setBirthDate("Abt. " + birthDate);
				} catch (NumberFormatException e) {
					person.setBirthDate(row.get(mappingKeys[6]) + "??");
				}
			}

			person.setBirthPlace(row.get(mappingKeys[10]));

			if (first) {
				if (sex.startsWith("M")) {
					family.setFather(person);
				} else {
					family.setMother(person);
				}
				first = false;
			} else {
				setFamilyRole(family, row.get(mappingKeys[9]), person);
			}

			person.setCensusEvent(censusEvent);

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
	 * @param families the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * @param family
	 * @param position
	 * @return
	 */
	private void setFamilyRole(Family family, String position, Individual person) {
		position = position.toLowerCase();

		if ((position.contains("broder")) || (position.contains("broderdatter")) || (position.contains("broders�n"))
				|| (position.contains("br�dre")) || (position.contains("datterdatter"))
				|| (position.contains("for�ldre")) || (position.contains("manden")) || (position.contains("moder"))
				|| (position.contains("pleiebarn")) || (position.contains("pleieb�rn"))
				|| (position.contains("pleiedatter")) || (position.contains("pleies�n"))
				|| (position.contains("s�nnes�n")) || (position.contains("s�ster"))
				|| (position.contains("s�sterdatter")) || (position.contains("stedfader"))
				|| (position.contains("svigerfader")) || (position.contains("svigerfar"))
				|| (position.contains("svigerfor�ldre")) || (position.contains("svigermoder"))
				|| (position.contains("svigers�n"))) {
			return;
		} else if ((position.contains("husmoder")) || (position.contains("hustru")) || (position.contains("kone"))) {
			family.setMother(person);
		} else if ((position.contains("barn")) || (position.contains("b�rn")) || (position.contains("datter"))
				|| (position.contains("s�n"))) {
			family.getChildren().add(person);
		}
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	/**
	 * @param singles the singles to set
	 */
	public void setSingles(List<Individual> singles) {
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
