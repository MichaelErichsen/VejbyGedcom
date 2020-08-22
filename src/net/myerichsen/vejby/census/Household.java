package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;

import net.myerichsen.vejby.gedcom.CensusEvent;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.Individual;
import net.myerichsen.vejby.util.Mapping;

/**
 * A household as extracted from a census file. This object has no direct
 * counterpart in GEDCOM.
 * 
 * @version 21. aug. 2020
 * @author Michael Erichsen
 * 
 */
public class Household {
//	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<List<String>> familyRoleList;
	private List<Family> families;
	private List<Individual> singles;
	private int id;
	private CensusEvent censusEvent;
	private int[] mappingKeys;
	private int iYear;

	/**
	 * Constructor
	 * 
	 * @param id Id of this household in the census
	 */
	public Household(int id) {
		super();
		rows = new ArrayList<>();
		families = new ArrayList<>();
		familyRoleList = new ArrayList<>();
		this.id = id;
		mappingKeys = Mapping.getInstance().getMappingKeys();
	}

	/**
	 * 
	 */
	public void createCensusEvent() {
		censusEvent = new CensusEvent(iYear, rows.get(0).get(mappingKeys[2]), this);
	}

	/**
	 * Create an individual from a row in the census table.
	 * 
	 * @param row A row in the census table
	 * @return A GEDCOM individual
	 */
	public Individual createIndividual(List<String> row) {
		String sYear = row.get(mappingKeys[12]);
		iYear = Integer.parseInt(sYear.replaceAll("[^0-9]", ""));

		Individual person = new Individual(Integer.parseInt(row.get(mappingKeys[1])));
		person.setName(row.get(mappingKeys[4]));
		person.setTrade(row.get(mappingKeys[9]));

		// Birth date
		if (mappingKeys[6] != 0) {
			person.setBirthDate(row.get(mappingKeys[6]));

			// Age
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

		person.setSex(row.get(mappingKeys[5]));

		// Position (F, M, B or 0)
		if (mappingKeys[10] > 0) {
			person.setPosition(getFamilyRole(row.get(mappingKeys[10])));
		} else {
			// Trade
			person.setPosition(getFamilyRole(row.get(mappingKeys[9])));
		}

		return person;
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * Get the role of the person in the family
	 * 
	 * @param position
	 * @return F, M, B or 0 flag
	 */
	private String getFamilyRole(String position) {
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
			return "0";
		}

		if ((position.contains("husmoder")) || (position.contains("hustru")) || (position.contains("kone"))) {
			return "M";
		}

		if ((position.contains("barn")) || (position.contains("børn")) || (position.contains("datter"))
				|| (position.contains("søn"))) {
			return "B";
		}

		return "0";
	}

	/**
	 * Populate and return a family role list
	 * 
	 * @param size Number of persons in the household
	 * @return the familyRoleList
	 */
	public List<List<String>> getFamilyRoleList(int size) {
		List<String> personRole;
		for (int i = 0; i < size; i++) {
			personRole = new ArrayList<>();
			familyRoleList.add(personRole);
		}
		return familyRoleList;
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
	public String identifyFamilies() {
		Individual person;

		boolean first = true;
		// The first person is the primary person, father or mother according to
		// sex

		// Create a dummy family 0 for singles and a first family from trade or position
		// fields
		Family family0 = new Family(id, 0);
		Family family1 = new Family(id, 1);

		for (List<String> row : rows) {
			person = createIndividual(row);
			person.setCensusEvent(censusEvent);

			if (first) {
				if (person.getSex().startsWith("M")) {
					person.setPosition("Fader");
					family1.setFather(person);
				} else {
					person.setPosition("Moder");
					family1.setMother(person);
				}
				first = false;

			} else {
				if (person.getPosition().equals("M")) {
					person.setPosition("Moder");
					family1.setMother(person);
				} else if (person.getPosition().equals("B")) {
					person.setPosition("Barn");
					family1.getChildren().add(person);
				} else if (person.getPosition().equals("0")) {
					person.setPosition("");
					family0.getSingles().add(person);
				}
			}
		}

		families.clear();
		families.add(family0);
		families.add(family1);

		return "Familie udskilt";
	}

	/**
	 * @param families the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * @param familyRoleList the familyRoleList to set
	 */
	public void setFamilyRoleList(List<List<String>> familyRoleList) {
		this.familyRoleList = familyRoleList;
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
