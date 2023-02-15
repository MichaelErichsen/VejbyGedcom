package net.myerichsen.gedcom.DDDParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This program needs a kipdata.txt and a set of KIP csv files as input.
 * <p>
 * It uses a hardcoded list of villages within a parish to find all persons in
 * the csv files, who were born in one of these villages. You can update the
 * list in the initVillages() method.
 * <p>
 * The output is sent to a text file.
 *
 * @author Michael Erichsen
 * @version 2022-03-27
 *
 */
public class Parser {
	private static List<String> kipLines;
	private static List<String> villages = new ArrayList<>();
	private static FileWriter fw;
	private static BufferedWriter bw;

	/**
	 * Create a list of villages in the parish
	 */
	private static void initVillages() {
		villages.add("Vejby");
		villages.add("Veiby");
		villages.add("Wejby");
		villages.add("Weiby");
		villages.add("Vejby sogn");
		villages.add("Veiby sogn");
		villages.add("Wejby sogn");
		villages.add("Weiby sogn");
		villages.add("Holløse");
		villages.add("Unnerup");
		villages.add("Mønge");
		villages.add("Ørby");
	}

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: Cleaner kiptextfilename csvfiledirectory outputfilename");
			System.exit(4);
		}

		final Parser parser = new Parser();

		parser.setKipTextFileName(args[0]);
		parser.setCsvFileDirectory(args[1]);
		initVillages();

		try {
			fw = new FileWriter(args[2]);
			bw = new BufferedWriter(fw);
		} catch (final IOException e1) {
			e1.printStackTrace();
			System.exit(16);
		}

		try {
			kipLines = parser.parseKipText();
			kipLines.remove(0); // Remove header line

			for (final String line : kipLines) {
				parser.processKipTextLine(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(16);
		}

		System.out.println("Output saved in " + args[2]);
		System.exit(0);
	}

	private String kipTextFileName = "";
	private String csvFileName = "";
	private String csvFileDirectory = "";
	private String kipLine = "";

	/**
	 * @return the csvFileDirectory
	 */
	public String getCsvFileDirectory() {
		return csvFileDirectory;
	}

	/**
	 * @return the csvFileName
	 */
	public String getCsvFileName() {
		return csvFileName;
	}

	/**
	 * @return the kipLine
	 */
	public String getKipLine() {
		return kipLine;
	}

	/**
	 * @return the kipTextFileName
	 */
	public String getKipTextFileName() {
		return kipTextFileName;
	}

	/**
	 * Parse all lines in the kipdata text file. Read each line into a String list.
	 *
	 * @return kipLines A List of Strings
	 * @throws IOException
	 */
	private List<String> parseKipText() throws IOException {
		final List<String> kipLines = new ArrayList<>();
		final File file = new File(kipTextFileName);
		final FileReader fr = new FileReader(file);
		final BufferedReader br = new BufferedReader(fr);
		String line;

		while ((line = br.readLine()) != null) {
			kipLines.add(line);
		}

		fr.close();

		return kipLines;
	}

	/**
	 * Print the entire household for this person
	 *
	 * @param personColumn
	 * @param householdColumn
	 * @param csvLine
	 * @param csvLines
	 * @throws IOException
	 */
	private void printHousehold(int personColumn, int householdColumn, String csvLine, List<String> csvLines)
			throws IOException, IndexOutOfBoundsException {
		bw.newLine();
		bw.newLine();
		bw.write("Searching " + getKipLine());
		bw.newLine();
		bw.write("Found " + csvLine);
		bw.newLine();
		bw.newLine();
		String[] fields = csvLine.split(";");

		// Find household for this person

		final String person = fields[personColumn];
		final int iPerson = Integer.parseInt(person);
		int firstInHousehold = iPerson - 1;
		final String household = fields[householdColumn];

		// While previous person has same household backtrack one person

		String prevLine = csvLines.get(firstInHousehold);
		String[] prevFields = prevLine.split(";");

		while (prevFields[householdColumn].equals(household)) {
			firstInHousehold--;
			prevLine = csvLines.get(firstInHousehold);
			prevFields = prevLine.split(";");
		}

		firstInHousehold++;

		// Print each person in the same household

		String thisLine = csvLines.get(firstInHousehold);
		firstInHousehold++;
		fields = thisLine.split(";");

		while (fields[householdColumn].equals(household)) {
			printHouseholdMember(thisLine);
			thisLine = csvLines.get(firstInHousehold++);
			fields = thisLine.split(";");
		}

	}

	/**
	 * Print line while omitting all blank fields
	 *
	 * @param csvLine
	 * @throws IOException
	 */
	private void printHouseholdMember(String csvLine) throws IOException {
		final String[] fields = csvLine.split(";");
		final StringBuilder sb = new StringBuilder();

		for (final String string : fields) {
			if (string.trim().length() > 0) {
				sb.append(string.trim() + ", ");
			}
		}
		bw.write(sb.toString());
		bw.newLine();
	}

	/**
	 * Open a csv file and find any persons born in Vejby
	 *
	 * @param csvFileName
	 * @throws IOException
	 */
	private void processCsvFile(String csvFileName) throws IOException {
		final List<String> csvLines = new ArrayList<>();
		final File file = new File(csvFileName);
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (final FileNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}
		final BufferedReader br = new BufferedReader(fr);
		String line;

		while ((line = br.readLine()) != null) {
			csvLines.add(line);
		}

		fr.close();

		// First find column with title "Kildefødested" in the header line

		final String header = csvLines.get(0);
		String[] fields = header.split(";");
		int personColumn = 0;
		int householdColumn = 0;
		int birthPlaceColumn = 0;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equals("Løbenr")) {
				personColumn = i;
			}
			if (fields[i].equals("Husstands/familienr.")) {
				householdColumn = i;
			}

			if (fields[i].equals("Kildefødested")) {
				birthPlaceColumn = i;
			}
		}
		if (personColumn == 0) {
			System.out.println("Løbenr ikke fundet i " + header + " for " + getKipLine());
			return;
		}
		if (householdColumn == 0) {
			System.out.println("Husstand ikke fundet i " + header + " for " + getKipLine());
			return;
		}

		csvLines.remove(0);

		// Read each following line and compare contents of this column of each
		// line to the village list

		for (final String csvLine : csvLines) {
			fields = csvLine.split(";");

			try {
				if (villages.contains(fields[birthPlaceColumn])) {
					printHousehold(personColumn, householdColumn, csvLine, csvLines);
				}
			} catch (final IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Process each line in the kip text file.
	 * <p>
	 * Each line has the format
	 * <p>
	 * Aabenraa;Lundtoft;Adsbøl;1803;B8351
	 * <p>
	 * If year < 1845 then ignore
	 * <p>
	 * Process the csv file
	 *
	 * @param line
	 * @throws IOException
	 */
	private void processKipTextLine(String line) throws IOException {
		setKipLine(line);
		final String[] fields = line.split(";");

		String year = fields[3];

		if (year.startsWith("FT")) {
			year = year.substring(2);
		}

		if (Integer.parseInt(year) < 1845) {
			return;
		}

		csvFileName = csvFileDirectory + fields[4] + ".csv";

		processCsvFile(csvFileName);
	}

	/**
	 * @param csvFileDirectory the csvFileDirectory to set
	 */
	public void setCsvFileDirectory(String csvFileDirectory) {
		if (!csvFileDirectory.endsWith("/")) {
			csvFileDirectory = csvFileDirectory + "/";
		}

		this.csvFileDirectory = csvFileDirectory;
	}

	/**
	 * @param csvFileName the csvFileName to set
	 */
	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	/**
	 * @param kipLine the kipLine to set
	 */
	public void setKipLine(String kipLine) {
		this.kipLine = kipLine;
	}

	/**
	 * @param kipTextFileName the kipTextFileName to set
	 */
	public void setKipTextFileName(String kipTextFileName) {
		this.kipTextFileName = kipTextFileName;
	}

}
