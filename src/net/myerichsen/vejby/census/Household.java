package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.gedcom.CensusEvent;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * A household as extracted from a census file. This object has no direct
 * counterpart in GEDCOM.
 * <p>
 * The family role structure contains a row for each person in the house hold.
 * Each row contains one or more family roles, marked in the format:
 * <ul>
 * <li>[Fader|Moder|Barn] i familie [family id]</li>
 * </ul>
 * 
 * @version 21. aug. 2020
 * @author Michael Erichsen
 * 
 */
public class Household {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<List<String>> familyRoleList;
	private List<Family> families;
//	private List<Individual> singles;
	private int id;
	private CensusEvent censusEvent;

	/**
	 * Constructor
	 * 
	 * @param id Id of this household in the census
	 */
	public Household(int id) {
		super();
		rows = new ArrayList<>();
		families = new ArrayList<>();
//		singles = new ArrayList<>();
		familyRoleList = new ArrayList<>();
		this.id = id;
	}

	/**
	 * Create an individual using fields from the census table row.
	 * 
	 * @param mappingKeys The mapping of columns
	 * @param first       A flag to indicate whether this is the first person in the
	 *                    family
	 * @param iYear       Census year
	 * @param family      The family to add the individual to
	 * @param row         The row in the census table
	 * @param newRole     Posiiton in family
	 * @return A flag to indicate whether this is the first person in the family
	 */
	public Individual createIndividualFromRow(int[] mappingKeys, boolean first, int iYear, Family family,
			List<String> row, String newRole) {
		String sex;
		Individual person;
		LOGGER.log(Level.FINE, row.get(mappingKeys[1]) + " " + row.get(mappingKeys[4]) + " " + row.get(mappingKeys[5]));

		person = new Individual(Integer.parseInt(row.get(mappingKeys[1])));
		person.setName(row.get(mappingKeys[4]));

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

		person.setBirthPlace(row.get(mappingKeys[11]));

		sex = row.get(mappingKeys[5]);
		person.setSex(sex);

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

		person.setPosition(newRole);

		person.setCensusEvent(censusEvent);
		return person;
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

//	/**
//	 * @return the singles
//	 */
//	public List<Individual> getSingles() {
//		return singles;
//	}

	/**
	 * @return Boolean value signalling whether the household has families.
	 */
	public boolean hasFamilies() {
		if (getFamilies().isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * Separate a household into families, defined as father, mother, and children.
	 * Each household contains either a single or one or more families and perhaps
	 * further singles (tenants, lodgers, servants, etc.).
	 * <p>
	 * Currently only the first family is identified.
	 * 
	 * @param mappingKeys The array of columns in the table
	 * @return message
	 */
	public String identifyFamilies(int[] mappingKeys) {
		boolean first = true;

		String sYear = rows.get(0).get(mappingKeys[12]);
		int iYear = Integer.parseInt(sYear.replaceAll("[^0-9]", ""));
		censusEvent = new CensusEvent(iYear, rows.get(0).get(mappingKeys[2]), this);

		// The first person is the primary person, father or mother according to
		// sex

		LOGGER.log(Level.FINE, "---------------------------------------------------\n");

		// Create a family
		Family family = new Family(id, 1);

		for (List<String> row : rows) {
			createIndividualFromRow(mappingKeys, first, iYear, family, row, "");
			first = false;

			// TODO Should test next row for family membership

			// If no marriage or children, then it is not a family
			// Identify father, mother and children from the position in
			// household
			// column - or trade column
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
	 * Set the role of the person in the family
	 * 
	 * @param family
	 * @param position
	 * @return
	 */
	private void setFamilyRole(Family family, String position, Individual person) {
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
		} else if ((position.contains("husmoder")) || (position.contains("hustru")) || (position.contains("kone"))) {
			family.setMother(person);
		} else if ((position.contains("barn")) || (position.contains("børn")) || (position.contains("datter"))
				|| (position.contains("søn"))) {
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

//	/**
//	 * @param singles the singles to set
//	 */
//	public void setSingles(List<Individual> singles) {
//		this.singles = singles;
//	}

	/**
	 * Populate and return a family role list
	 * 
	 * @param size Number of persons in the household
	 * @return the familyRoleList
	 */
	public List<List<String>> getFamilyRoleList(int size) {
		List<String> personRole;
		for (int i = 0; i < size; i++) {
			personRole = new ArrayList<String>();
			familyRoleList.add(personRole);
		}
		return familyRoleList;
	}

	/**
	 * @param familyRoleList the familyRoleList to set
	 */
	public void setFamilyRoleList(List<List<String>> familyRoleList) {
		this.familyRoleList = familyRoleList;
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
