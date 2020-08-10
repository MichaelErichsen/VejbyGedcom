package net.myerichsen.vejby.census;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Read a KIP file and create a table. Split the table into households. Split
 * the household into families and single persons
 * 
 * @author michael
 *
 */
public class CensusReader {
	private static int year;
	private static List<Integer> yearList = Arrays.asList(1771, 1787, 1801, 1834, 1840, 1845, 1850, 1855, 1860, 1870,
			1880, 1885, 1890, 1901, 1906, 1911, 1916, 1921, 1925, 1930, 1940);
	private static String kipFileName;
	private static Table censusTable;

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boolean validArgs = false;

			if (args.length == 2) {
				year = Integer.parseInt(args[0]);
				kipFileName = args[1];
				File kipFile = new File(kipFileName);
				validArgs = kipFile.exists() && !kipFile.isDirectory() && yearList.contains(year);
			} else {
				year = 1845;
				kipFileName = "C:\\Users\\michael\\Documents\\The Master Genealogist v9\\Kilder\\DDD\\c4529.csv";
				validArgs = true;
			}

			if (!validArgs) {
				printUsageMessage();
			} else {
				processKipFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process the KIP file
	 */
	private static void processKipFile() {
		String message = "";

		censusTable = new Table(year, kipFileName);
		message = censusTable.readKipfile();
		System.out.println(message);
		message = censusTable.clearEmptyFields();
		System.out.println(message);

		int householdFieldNumber;
		switch (year) {
		case 1845:
			householdFieldNumber = 3;
			break;

		default:
			householdFieldNumber = 3;
			break;
		}

		message = censusTable.createHouseholds(householdFieldNumber);
		System.out.println(message);
		System.out.println(censusTable);
		System.exit(0);
	}

	/**
	 * Print usage message
	 */
	private static void printUsageMessage() {
		System.err.println("Usage: CensusReader <År> <Fuld sti til csv fil>");
	}
}
