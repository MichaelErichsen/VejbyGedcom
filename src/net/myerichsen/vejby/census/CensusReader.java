package net.myerichsen.vejby.census;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read a KIP file and create a table. Split the table into households. Split
 * the household into families and single persons
 * 
 * @author michael
 *
 */
public class CensusReader {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
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
	 * Print usage message
	 */
	private static void printUsageMessage() {
		System.err.println("Usage: CensusReader <�r> <Fuld sti til csv fil>");
	}

	/**
	 * Process the KIP file
	 */
	private static void processKipFile() {
		String message = "";

		censusTable = new Table(year, kipFileName);
		message = censusTable.readKipfile();
		LOGGER.log(Level.INFO, message);
		message = censusTable.removeEmptyColumns();
		LOGGER.log(Level.INFO, message);

		// TODO Add other field numbers to pass to families and persons

		int householdFieldNumber;
		int sexFieldNumber;
		switch (year) {
		case 1845:
			householdFieldNumber = 3;
			sexFieldNumber = 6;
			break;

		default:
			householdFieldNumber = 3;
			sexFieldNumber = 6;
			break;
		}

		message = censusTable.createHouseholds(householdFieldNumber);
		LOGGER.log(Level.INFO, message);

		for (Household household : censusTable.getHouseholds()) {
			message = household.identifyFamilies(sexFieldNumber);
			LOGGER.log(Level.INFO, message + " " + household.getFamilies().get(0).toString());
		}

		// System.out.println(censusTable);
		System.exit(0);
	}
}
