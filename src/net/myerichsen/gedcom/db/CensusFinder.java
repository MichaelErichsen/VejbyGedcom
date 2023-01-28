package net.myerichsen.gedcom.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find all census entries that match a given individual ID.
 *
 * Find name and birth or christening from the Derby database.
 *
 * For each csv file in the DDD census folder within the life span of the
 * individual
 *
 * For each person
 *
 * If the name matches completely and the birth or christening date are within
 * two years of the given age then output in csv format
 *
 *
 * @author Michael Erichsen
 * @version 2023-01-28
 *
 */
public class CensusFinder {
	private static Logger logger;
	private static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Usage: DBLister individualId derbydatabasepath csvfilepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLister");

		final CensusFinder cf = new CensusFinder();

		try {
			cf.execute(args);
		} catch (SQLException | IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Find part of indivudual's birth place in the census line
	 *
	 * @param needle
	 * @param haystack
	 * @return
	 */
	private boolean compareBirthPlace(String needle, String haystack) {
		final String h = haystack.toLowerCase();
		final String[] split = needle.toLowerCase().split(",");

		for (final String element : split) {
			if (h.contains(element)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Connect to the Derby database
	 *
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String url) throws SQLException {
		final String dbURL1 = "jdbc:derby:C:/Users/michael/VejbyDB";
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		logger.info("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		final Statement statement = connectToDB(args[1]);
		final DBIndividual individual = new DBIndividual(statement, args[0]);
		logger.info("Searching for censuses for " + individual.getName() + ", born " + individual.getBirthYear()
				+ " in " + individual.getBirthPlace());

		parseCsvFiles(individual, args);
		logger.info("Program ended");
	}

	/**
	 * Get year as integer
	 *
	 * @param date
	 * @return
	 */
	private int getYearFromDate(String date) {
		final Pattern r = Pattern.compile("\\d{4}");

		final Matcher m = r.matcher(date);

		if (m.find()) {
			return Integer.parseInt(m.group(0));
		}
		return 1;
	}

	/**
	 * Parse each csv file in the directory
	 *
	 * @param individual
	 * @param args
	 * @throws IOException
	 */
	private void parseCsvFiles(DBIndividual individual, String[] args) throws IOException {
		final String outName = args[3] + "/" + individual.getName() + ".csv";
		final BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

		try {
			final List<String> kipLines = parseKipText(args[2] + "/kipdata.txt");

			for (final String xline : kipLines) {
				processKipTextLine(individual, xline, args, bw);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(16);
		}

		bw.flush();
		bw.close();
		logger.info(counter + " lines of census data written to " + outName);
	}

	/**
	 * Read each line in the kipdata text file into a String list.
	 *
	 * @return kipLines A List of Strings
	 * @throws IOException
	 */
	private List<String> parseKipText(String kipTextFileName) throws IOException {
		final List<String> kipLines = new ArrayList<>();
		final BufferedReader br = new BufferedReader(new FileReader(new File(kipTextFileName)));
		String line;

		while ((line = br.readLine()) != null) {
			kipLines.add(line);
		}

		br.close();

		// Remove header line
		kipLines.remove(0);

		return kipLines;
	}

	/**
	 * Extract data from the census csv file into the output csv file
	 *
	 * @param individual
	 * @param csvFileName
	 * @param location
	 * @param outfilepath
	 * @param bw
	 * @param ftYear
	 * @throws IOException
	 */
	private void processCsvFile(DBIndividual individual, String csvFileName, String location, String outfilepath,
			BufferedWriter bw, int ftYear) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(new File(csvFileName)));

		// Read first line to get headers for later use
		final String headerLine = br.readLine();

		int diff = 0;

		String line;

		while ((line = br.readLine()) != null) {
			if (line.contains(individual.getName())) {
				int col = -1;

				// Ignore empty file
				if (headerLine == null) {
					br.close();
					return;
				}

				String[] columns = headerLine.split(";");

				for (int i = 0; i < columns.length; i++) {
					if (columns[i].equals("Alder") || columns[i].equals("Født kildedato")) {
						col = i;
						break;
					}
				}

				if (col < 0) {
					br.close();
					return;
				}

				columns = line.split(";");

				final Pattern r = Pattern.compile("\\d*");
				final Matcher m = r.matcher(columns[col]);

				if (m.find() && !m.group(0).equals("")) {
					diff = Integer.parseInt(m.group(0));
				}

				diff = diff + individual.getBirthYear() - ftYear;

				if (diff < 5 && diff > -5) {
					if ((ftYear < 1845) || (compareBirthPlace(individual.getBirthPlace(), line))) {
						bw.write(ftYear + ";" + location + ";" + line.replace(";;", ";") + "\n");
						counter++;
					}
				}

			}
		}

		br.close();

	}

	/**
	 * Process each line in the kip text file.
	 * <p>
	 * Each line has the format
	 * <p>
	 * Aabenraa;Lundtoft;Adsbøl;1803;B8351
	 * <p>
	 * If year is outside the life span then ignore
	 * <p>
	 * Process the csv file
	 *
	 * @param individual
	 * @param line
	 * @param args
	 * @param bw
	 * @throws IOException
	 */
	private void processKipTextLine(DBIndividual individual, String line, String[] args, BufferedWriter bw)
			throws IOException {
		final String[] fields = line.split(";");

		final int intYear = getYearFromDate(fields[3]);

		if (intYear < individual.getBirthYear() || intYear > individual.getDeathYear()) {
			return;
		}

		final String csvFileName = args[2] + "/" + fields[4] + ".csv";
		final String location = fields[0] + ";" + fields[1] + ";" + fields[2] + ";";

		processCsvFile(individual, csvFileName, location, args[3], bw, intYear);
	}

}
