package net.myerichsen.vejby.census;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.gedcom.CensusEvent;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.Individual;
import net.myerichsen.vejby.util.Mapping;

/**
 * A household as extracted from a census file. This object has no direct
 * counterpart in GEDCOM.
 *
 * @version 04-09-2020
 * @author Michael Erichsen
 *
 */
public class Household {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private List<List<String>> rows;
	private List<Family> families;
	private List<Individual> singles;
	private List<Individual> persons;
	private int id;
	private CensusEvent censusEvent;
	private final int[] mappingKeys;

	/**
	 * Constructor
	 *
	 * @param id Id of this household in the census
	 */
	public Household(int id) {
		rows = new ArrayList<>();
		families = new ArrayList<>();
		setPersons(new ArrayList<Individual>());
		this.id = id;
		mappingKeys = Mapping.getInstance().getMappingKeys();
	}

	/**
	 * Create the census event matching this household.
	 *
	 * @param year
	 */
	public void createCensusEvent(int year) {
		censusEvent = new CensusEvent(year, rows.get(0).get(mappingKeys[2]), this);
	}

	/**
	 * Separate a household into families, defined as father, mother, and children.
	 * Each household contains either one or more families and perhaps further
	 * singles (tenants, lodgers, servants, etc.). Only the main family is
	 * identified by the application. The others (up to two more) must be done
	 * manually.
	 *
	 * @param sexMappingKey
	 *
	 * @return message The return message
	 */
	public String createFamilies(int sexMappingKey) {
		Individual individual;

		boolean first = true;

		// Create a dummy family 0 for singles and a first family from trade or
		// position
		// fields
		final Family family0 = new Family(id, 0);
		final Family family1 = new Family(id, 1);

		for (final List<String> row : rows) {
			individual = createIndividual(row);
			individual.setCensusEvent(censusEvent);

			if (first) {
				if (sexMappingKey == -1) {
					if ("".equals(individual.getFamilyRole1())) {
						individual.setFamilyRole1("Fader");
						individual.setSex("M");
						family1.setFather(individual);
					}
				} else

				// The first person is the primary person, either father or
				// mother according to
				// sex
				if (individual.getSex().startsWith("M")) {
					if ("".equals(individual.getFamilyRole1())) {
						individual.setFamilyRole1("Fader");
					}

					family1.setFather(individual);
				} else {
					if ("".equals(individual.getFamilyRole1())) {
						individual.setFamilyRole1("Moder");
					}

					family1.setMother(individual);
				}
				first = false;

			} else if ("Fader".equals(individual.getFamilyRole1())) {
				family1.setFather(individual);
			} else if ("Moder".equals(individual.getFamilyRole1())) {
				family1.setMother(individual);
			} else if ("Barn".equals(individual.getFamilyRole1())) {
				family1.getChildren().add(individual);
			} else if ("".equals(individual.getFamilyRole1())) {
				family0.getSingles().add(individual);
			}

			getPersons().add(individual);
			LOGGER.log(Level.FINE, "Individual added to households.persons: " + individual.getId() + ", " + individual);
		}

		families.clear();
		families.add(family0);
		families.add(family1);

		return "Familie udskilt";
	}

	/**
	 * Create an individual from a row in the census table.
	 *
	 * @param row A row in the census table
	 * @return A GEDCOM individual
	 */
	public Individual createIndividual(List<String> row) {
		final Individual individual = new Individual(Integer.parseInt(row.get(mappingKeys[1])));
		individual.setName(row.get(mappingKeys[4]));
		if (mappingKeys[5] != 0) {
			individual.setSex(row.get(mappingKeys[5]));
		}

		// Birth date
		if (mappingKeys[6] != 0) {
			String bd = row.get(mappingKeys[6]);

			// MilRollEntryDialog for 18540304 format
			if (bd.matches("[0-9]{8}")) {
				final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
				final SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");

				try {
					final Date date = inputFormat.parse(bd);
					bd = outputFormat.format(date);
				} catch (final ParseException ignoredException) {
				}

				LOGGER.log(Level.FINE, "Birth date: " + bd);
			}
			individual.setBirthDate(bd);
		} else if (mappingKeys[7] != 0) {
			// Or age
			try {
				// Calculate difference between age and census year
				final String sYear = row.get(mappingKeys[12]);
				final int iYear = Integer.parseInt(sYear.replaceAll("[^0-9]", ""));

				final int birthDate = iYear - Integer.parseInt(row.get(mappingKeys[7]));
				individual.setBirthDate("Abt. " + birthDate);
			} catch (final NumberFormatException e) {
				// Or just copy the input string
				individual.setBirthDate(row.get(mappingKeys[7]) + "??");
			}
		}

		individual.setMaritalStatus(row.get(mappingKeys[8]));
		final String trade = row.get(mappingKeys[9]);
		individual.setTrade(trade);

		final int positionKey = mappingKeys[10];
		if (positionKey > 0) {
			individual.setPosition(row.get(positionKey));
			individual.setFamilyRole1(getFamilyRole(row.get(positionKey)));
		} else {
			individual.setFamilyRole1(getFamilyRole(trade));
		}

		if (mappingKeys[11] != 0) {
			individual.setBirthPlace(row.get(mappingKeys[11]));
		}

		return individual;
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * Get the role of the person in the family from contents of the trade field
	 *
	 * @param position
	 * @return Position string
	 */
	private String getFamilyRole(String position) {
		position = position.toLowerCase();

		if (position.contains("aft�gtskone") || position.contains("barnepige") || position.contains("broder")
				|| position.contains("broderdatter") || position.contains("broders�n") || position.contains("br�dre")
				|| position.contains("datterdatter") || position.contains("for�ldre") || position.contains("manden")
				|| position.contains("pleiebarn") || position.contains("pleieb�rn") || position.contains("pleiedatter")
				|| position.contains("pleies�n") || position.contains("plejebarn") || position.contains("plejeb�rn")
				|| position.contains("plejedatter") || position.contains("plejes�n") || position.contains("s�nnes�n")
				|| position.contains("s�ster") || position.contains("s�sterdatter") || position.contains("stedfader")
				|| position.contains("svigerfader") || position.contains("svigerfar")
				|| position.contains("svigerfor�ldre") || position.contains("svigermoder")
				|| position.contains("svigers�n") || position.contains("undertagskone")) {
			return "";
		}

		if (position.contains("husfader") || position.contains("huusfader")) {
			return "Fader";
		}

		if (position.contains("husmoder") || position.contains("hustru") || position.contains("huusmoder")
				|| position.contains("kone") || position.contains("madmoder")) {
			return "Moder";
		}

		if (position.contains("barn") || position.contains("b�rn") || position.contains("datter")
				|| position.contains("s�n")) {
			return "Barn";
		}

		return "";
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param i
	 * @return
	 */
	public Individual getPerson(int i) {
		return persons.get(i);
	}

	/**
	 * Get the number of persons in this household
	 *
	 * @return Person count
	 */
	public int getPersonCount() {
		return persons.size();
	}

	/**
	 * @return the persons
	 */
	public List<Individual> getPersons() {
		return persons;
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
	 * @param families the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param persons the persons to set
	 */
	public void setPersons(List<Individual> persons) {
		this.persons = persons;
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
