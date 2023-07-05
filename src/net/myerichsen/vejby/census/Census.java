package net.myerichsen.vejby.census;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.gedcom.Family;

/**
 * Singleton class implementing a census table as loaded from a KIP file.
 *
 * @version 10-09-2020
 * @author Michael Erichsen
 */
/**
 * @author Michael Erichsen
 * @version 5. jul. 2023
 *
 */
public class Census {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static Census single_instance = null;

	/**
	 * Static method to create instance of Singleton class.
	 *
	 * @return An instance of the class
	 */
	public static Census getInstance(int year) {
		if (single_instance == null) {
			single_instance = new Census(year);
		}

		return single_instance;
	}

	private int year;
	private List<String> headers;
	private List<List<String>> persons;

	private List<Household> households;

	/**
	 * Constructor
	 *
	 * @param year
	 */
	private Census(int year) {
		this.year = year;
		persons = new ArrayList<>();
	}

	/**
	 * Split the table into households
	 *
	 * @param householdFieldNumber The column in the table that contains the
	 *                             household number
	 * @return message
	 */
	public String createHouseholds(int householdFieldNumber) {
		setHouseholds(new ArrayList<Household>());
		Household currentHousehold = null;
		String currentHouseholdNumber = "";
		String newHouseholdNumber = "";
		final String currentSourceLocation = "";
		final String newSourceLocation = "";
		int id = 0;

		// Get each person
		for (final List<String> currentRow : persons) {
			newHouseholdNumber = currentRow.get(householdFieldNumber);
			LOGGER.log(Level.FINE, "Household: " + newHouseholdNumber + ", was: " + currentHouseholdNumber);

			// If contents of household or source location has changed, then
			// create a new household. Add household to households list
			if (!currentHouseholdNumber.equals(newHouseholdNumber)
					|| !currentSourceLocation.equals(newSourceLocation)) {
				currentHousehold = new Household(id);
				id++;
				getHouseholds().add(currentHousehold);
				currentHouseholdNumber = newHouseholdNumber;
			}

			// Add current person to household
			currentHousehold.getRows().add(currentRow);
		}

		for (final Household household : households) {
			household.createCensusEvent(year);
		}

		return getHouseholds().size() + " husholdninger udskilt";
	}

	/**
	 * @return
	 */
	public List<Family> getFamilies() {
		final List<Family> lf = new ArrayList<>();

		for (final Household household : households) {
			final List<Family> families = household.getFamilies();

			lf.addAll(families);
		}

		return lf;
	}

	/**
	 * Get a family in a household.
	 *
	 * @param householdId
	 * @param familyId
	 * @return The family
	 */
	public Family getFamily(int householdId, int familyId) {
		for (final Household household : households) {
			if (household.getId() == householdId) {
				final List<Family> families = household.getFamilies();
				for (final Family family : families) {
					if (family.getFamilyId() == familyId) {
						return family;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @return the headers
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * Get a household.
	 *
	 * @param id Id of the household
	 * @return The household
	 */
	public Household getHousehold(int id) {
		for (final Household household : households) {
			if (household.getId() == id) {
				return household;
			}
		}
		return null;
	}

	/**
	 * @return the households
	 */
	public List<Household> getHouseholds() {
		return households;
	}

	/**
	 * @return the fields
	 */
	public List<List<String>> getPersons() {
		return persons;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Read a KIP file into the table.
	 *
	 * @param kipFile
	 * @return message
	 */
	public String readKipfile(File kipFile) {

		try {
			final FileInputStream fis = new FileInputStream(kipFile);
			final Scanner sc = new Scanner(fis);
			final String headerLine = sc.nextLine();
			headers = new LinkedList<>(Arrays.asList(headerLine.split(";")));

			while (sc.hasNextLine()) {
				final List<String> fields = new LinkedList<>(Arrays.asList(sc.nextLine().split(";")));
				persons.add(fields);
			}
			sc.close();
			fis.close();
			return "Filen " + kipFile + " er indlæst";
		} catch (final FileNotFoundException e) {
			return "Filen " + kipFile + " kunne ikke findes";
		} catch (final IOException e) {
			return "Filen " + kipFile + " kunne ikke læses";
		}

	}

	/**
	 * Remove all empty columns from both lists by checking each field in each
	 * column.
	 *
	 * @return message
	 */
	public String removeEmptyColumns() {
		final List<Integer> emptyColumns = new ArrayList<>();
		boolean found;

		for (int col = 0; col < headers.size(); col++) {
			found = false;

			try {
				for (final List<String> person : persons) {
					if (!person.get(col).isEmpty()) {
						found = true;
					}
				}
			} catch (final Exception e) {
				found = false;
			}

			if (!found) {
				emptyColumns.add(col);
			}
		}

		for (int i = emptyColumns.size() - 1; i >= 0; i--) {
			final int j = emptyColumns.get(i);
			LOGGER.log(Level.FINE, "Col " + j + " is empty");
			headers.remove(j);

			for (final List<String> ls : persons) {
				try {
					ls.remove(j);
				} catch (final Exception ignoredExcpetion) {
					// If last column is empty
				}
			}
		}

		return emptyColumns.size() + " tomme kolonner blev fjernet";
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * @param households the households to set
	 */
	public void setHouseholds(List<Household> households) {
		this.households = households;
	}

	/**
	 * @param persons the list of lists of persons to set
	 */
	public void setPersons(List<List<String>> persons) {
		this.persons = persons;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		for (final String string : headers) {
			sb.append(string + ";");
		}
		sb.append("\n");

		for (final List<String> ls : persons) {
			for (final String string : ls) {
				sb.append(string + ";");
			}
			sb.append("\n");
		}

		return sb.toString();
	}
}
