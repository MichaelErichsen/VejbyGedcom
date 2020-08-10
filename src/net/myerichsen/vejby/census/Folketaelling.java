/**
 * 
 */
package net.myerichsen.vejby.census;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * @author michael
 *
 */

// TODO Split into families. fields[3] is Husstands/familienr.
// TODO Separate into father - mother - children and singles

public class Folketaelling {
	private static int year;
	private static List<Integer> yearList = Arrays.asList(1771, 1801, 1834, 1840, 1845, 1850, 1855, 1860, 1870, 1880,
			1885, 1890, 1901, 1906, 1911, 1916, 1921, 1925, 1930, 1940);
	private static File kipFile;
	private static List<String> relationKeywords = Arrays.asList("Barn", "Børn", "Broder", "Broderdatter", "Brodersøn",
			"Brødre", "Datter", "Datterdatter", "Forældre", "Hustru", "Kone", "Manden", "Moder", "Pleiebarn",
			"Pleiebørn", "Pleiedatter", "Pleiesøn", "Søn", "Sønnesøn", "Søster", "Søsterdatter", "Stedfader",
			"Svigerfader", "Svigerfar", "Svigerforældre", "Svigermoder", "Svigersøn");
	/**
	 * @return the kipFile
	 */
	public static File getKipFile() {
		return kipFile;
	}

	/**
	 * @param nextLine
	 */
	private static void handle1845(String nextLine) {
		String[] fields = nextLine.split(";");
		String address = fields[2];
		String name = fields[5];
		String sex = fields[6];
		// TODO Extract date from object
		String birthDate;

		try {
			birthDate = "abt. " + (1845 - Integer.parseInt(fields[7]));
		} catch (NumberFormatException e) {
			birthDate = fields[7];
		}

		String trades = fields[10];
		String position = fields[12];
		String birthPlace = fields[14];
		// System.out.println(
		// address + " " + name + " " + sex + " " + birthDate + " " + trades + "
		// " + position + " " + birthPlace);

		Individual father = new Individual();
		father.setAddress(address);
		// father.setBirthDate(birthDate);
		father.setBirthPlace(birthPlace);
		father.setName(name);
		father.setSex(sex);
		father.setTrades(trades);

		Family family = new Family();
		family.setFather(father);
		GedcomFile file = GedcomFile.getInstance();
		file.addFamily(family);

		try {
			file.print(new File("c://temp//vejby.ged"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			boolean validArgs = false;

			if (args.length == 2) {
				year = Integer.parseInt(args[0]);
				kipFile = new File(args[1]);
				validArgs = kipFile.exists() && !kipFile.isDirectory() && yearList.contains(year);
			} else {
				year = 1845;
				kipFile = new File("C:\\Users\\michael\\Documents\\The Master Genealogist v9\\Kilder\\DDD\\c4529.csv");
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
	 * Prints usage message.
	 */
	private static void printUsageMessage() {
		System.err.println("Usage: Folketaelling <År> <Fuld sti til csv fil>");
	}

	/**
	 * @throws IOException
	 * 
	 */
	private static void processKipFile() throws IOException {
		FileInputStream fis = new FileInputStream(kipFile);
		Scanner sc = new Scanner(fis);
		String headerLine = sc.nextLine();
		String[] headerFields = headerLine.split(";");

		for (int i = 0; i < headerFields.length; i++) {
			System.out.println(i + ": " + headerFields[i]);
		}

		while (sc.hasNextLine()) {
			processLine(year, sc.nextLine());
		}
		sc.close();
		fis.close();
	}

	/**
	 * @param year2
	 * @param nextLine
	 */
	private static void processLine(int ftyear, String nextLine) {
		switch (year) {
		case 1845:
			handle1845(nextLine);
			break;

		default:
			System.out.println("Year " + ftyear + " not yet implemented");
			break;
		}

	}

	/**
	 * @param kipFile
	 *            the kipFile to set
	 */
	public static void setKipFile(File kipFile) {
		Folketaelling.kipFile = kipFile;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public static void setYear(int year) {
		Folketaelling.year = year;
	}

	private List<List<String>> kipLines;

	/**
	 * @return the kipLines
	 */
	public List<List<String>> getKipLines() {
		return kipLines;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * If only one person in family then father
	 * 
	 * Else First person: If sex is M then father If sex is F then mother
	 * 
	 * Next persons: Scan for keywords
	 * 
	 * 
	 * @param trade
	 */
	private void parseRelations(String trade) {

	}

	// Barn
	// Børn
	// Broder
	// Broderdatter
	// Brodersøn
	// Brødre
	// Datter
	// Datterdatter
	// Forældre
	// Hustru
	// Kone
	// Manden
	// Moder
	// Pleiebarn
	// Pleiebørn
	// Pleiedatter
	// Pleiesøn
	// Søn
	// Sønnesøn
	// Søster
	// Søsterdatter
	// Stedfader
	// Svigerfader
	// Svigerfar
	// Svigerforældre
	// Svigermoder
	// Svigersøn

	/**
	 * @param kipLines
	 *            the kipLines to set
	 */
	public void setKipLines(List<List<String>> kipLines) {
		this.kipLines = kipLines;
	}
}
