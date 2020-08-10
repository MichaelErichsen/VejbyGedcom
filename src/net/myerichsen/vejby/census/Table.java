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

/**
 * Implement a census table as loaded from a KIP file
 * 
 * @author michael
 *
 */
public class Table {
	private int year;
	private String kipFileName;
	private List<String> headers;
	private List<List<String>> persons;
	// private List<Household> households;

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
	public String clearEmptyFields() {
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
			System.out.println("Col " + j + " is empty");
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
	 * Split the table into households
	 * 
	 * @param householdFieldNumber
	 *            The column in the table that contains the household number
	 * @return message
	 */
	public String createHouseholds(int householdFieldNumber) {

		// Get each person
		// If first then create a new household. Add household to households
		// list
		// If contents of householdField has changed, then create a new
		// household. Add household to households list
		// Add person to current household

		return "OK";

	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the headers
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the fields
	 */
	public List<List<String>> getPersons() {
		return persons;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setPersons(List<List<String>> persons) {
		this.persons = persons;
	}

	/**
	 * @return the kipFileName
	 */
	public String getKipFileName() {
		return kipFileName;
	}

	/**
	 * @param kipFileName
	 *            the kipFileName to set
	 */
	public void setKipFileName(String kipFileName) {
		this.kipFileName = kipFileName;
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
