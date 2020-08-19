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
 * Implement a census table as loaded from a KIP file
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class Table {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private int year;
	private String kipFileName;
	private List<String> headers;
	private List<List<String>> persons;
	private List<Household> households;

	/**
	 * Constructor
	 * 
	 * @param year
	 * @param kipFileName
	 */
	public Table(int year, String kipFileName) {
		super();
		this.year = year;
		this.kipFileName = kipFileName;
		persons = new ArrayList<>();
	}

	/**
	 * Split the table into households
	 * 
	 * @param householdFieldNumber
	 *            The column in the table that contains the household number
	 * @return message
	 */
	public String createHouseholds(int householdFieldNumber) {
		setHouseholds(new ArrayList<Household>());
		Household currentHousehold = null;
		String currentHouseholdNumber = "";
		String newHouseholdNumber = "";
		String currentSourceLocation = "";
		String newSourceLocation = "";
		int id = 0;

		// Get each person
		for (List<String> currentRow : persons) {
			newHouseholdNumber = currentRow.get(householdFieldNumber);
			LOGGER.log(Level.FINE, "Household: " + newHouseholdNumber + ", was: " + currentHouseholdNumber);

			// If contents of household or source location has changed, then
			// create a new household. Add household to households list
			if ((!currentHouseholdNumber.equals(newHouseholdNumber))
					|| (!currentSourceLocation.equals(newSourceLocation))) {
				currentHousehold = new Household(id++);
				getHouseholds().add(currentHousehold);
				currentHouseholdNumber = newHouseholdNumber;
			}

			// Add current person to household
			currentHousehold.getRows().add(currentRow);
		}
		return getHouseholds().size() + " husholdninger udskilt";
	}

	/**
	 * @return
	 */
	public List<Family> getFamilies() {
		List<Family> lf = new ArrayList<Family>();

		for (int i = 0; i < households.size(); i++) {
			List<Family> families = households.get(i).getFamilies();

			for (int j = 0; j < families.size(); j++) {
				lf.add(families.get(j));
			}
		}

		return lf;
	}

	/**
	 * @param householdId
	 * @param familyId
	 * @return The family
	 */
	public Family getFamily(int householdId, int familyId) {
		for (Household household : households) {
			if (household.getId() == householdId) {
				List<Family> families = household.getFamilies();
				for (Family family : families) {
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
	 * @param id
	 *            Id of the household
	 * @return The household
	 */
	public Household getHousehold(int id) {
		for (Household household : households) {
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
	 * @return the kipFileName
	 */
	public String getKipFileName() {
		return kipFileName;
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
	 * Read a KIP file into the table
	 * 
	 * @param kipFileName
	 * @return message
	 */
	public String readKipfile() {
		try {
			FileInputStream fis = new FileInputStream(new File(kipFileName));
			Scanner sc = new Scanner(fis);
			String headerLine = sc.nextLine();
			headers = new LinkedList<String>(Arrays.asList(headerLine.split(";")));

			while (sc.hasNextLine()) {
				List<String> fields = new LinkedList<String>(Arrays.asList(sc.nextLine().split(";")));
				persons.add(fields);
			}
			sc.close();
			fis.close();
			return "Filen " + kipFileName + " er indlæst";
		} catch (FileNotFoundException e) {
			return "Filen " + kipFileName + " kunne ikke findes";
		} catch (IOException e) {
			return "Filen " + kipFileName + " kunne ikke læses";
		}

	}

	/**
	 * Remove all empty columns from both lists by checking each field in each
	 * column
	 * 
	 * @return message
	 */
	public String removeEmptyColumns() {
		List<Integer> emptyColumns = new ArrayList<>();
		boolean found;

		for (int col = 0; col < headers.size(); col++) {
			found = false;

			try {
				for (int row = 0; row < persons.size(); row++) {
					if (!persons.get(row).get(col).isEmpty()) {
						found = true;
					}
				}
			} catch (Exception e) {
				found = false;
			}

			if (!found) {
				emptyColumns.add(col);
			}
		}

		for (int i = emptyColumns.size() - 1; i >= 0; i--) {
			int j = emptyColumns.get(i);
			LOGGER.log(Level.FINE, "Col " + j + " is empty");
			headers.remove(j);

			for (List<String> ls : persons) {
				try {
					ls.remove(j);
				} catch (Exception ignoredExcpetion) {
					// If last column is empty
				}
			}
		}

		return emptyColumns.size() + " tomme kolonner blev fjernet";
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * @param households
	 *            the households to set
	 */
	public void setHouseholds(List<Household> households) {
		this.households = households;
	}

	/**
	 * @param kipFileName
	 *            the kipFileName to set
	 */
	public void setKipFileName(String kipFileName) {
		this.kipFileName = kipFileName;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setPersons(List<List<String>> persons) {
		this.persons = persons;
	}

	/**
	 * @param year
	 *            the year to set
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
		StringBuilder sb = new StringBuilder();

		for (String string : headers) {
			sb.append(string + ";");
		}
		sb.append("\n");

		for (List<String> ls : persons) {
			for (String string : ls) {
				sb.append(string + ";");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
