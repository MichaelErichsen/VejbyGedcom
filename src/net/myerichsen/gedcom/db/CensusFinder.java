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

/**
 * Find all census entries that match a given individual ID.
 *
 * Find name and birth or christening from the Derby database.
 *
 * For each csv file in the DDD census folder
 *
 * For each person
 *
 * If the name matches completely then check the age or bith date in the census
 * with the birth or christening date in the data base. If it is within two
 * years then output in csv format
 *
 *
 * @author Michael Erichsen
 * @version 2320-01-27
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
	 * Connect to the Derby database
	 *
	 * @throws SQLException
	 *
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
		logger.info("Searching for censuses for " + individual.getName());

		parseCsvFiles(individual, args);
		logger.info("Program ended");

	}

	/**
	 * Parse each csv file in the directory
	 *
	 * @param individual
	 * @param args
	 * @throws IOException
	 */
	private void parseCsvFiles(DBIndividual individual, String[] args) throws IOException {
		String outName = args[3] + "/" + individual.getName() + ".csv";
		BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

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
	 * Parse all lines in the kipdata text file. Read each line into a String
	 * list.
	 *
	 * @return kipLines A List of Strings
	 * @throws IOException
	 */
	private List<String> parseKipText(String kipTextFileName) throws IOException {
		final List<String> kipLines = new ArrayList<>();
		final File file = new File(kipTextFileName);
		final FileReader fr = new FileReader(file);
		final BufferedReader br = new BufferedReader(fr);
		String line;

		while ((line = br.readLine()) != null) {
			kipLines.add(line);
		}

		fr.close();

		kipLines.remove(0);

		return kipLines;
	}

	/**
	 * @param individual
	 * @param csvFileName
	 * @param location
	 * @param bw
	 * @param args
	 * @throws IOException
	 */
	private void processCsvFile(DBIndividual individual, String csvFileName, String location, String outfilepath,
			BufferedWriter bw) throws IOException {
		String line = "";
		final BufferedReader br = new BufferedReader(new FileReader(new File(csvFileName)));

		while ((line = br.readLine()) != null) {
			if (line.contains(individual.getName())) {
				bw.write(location + ";" + line.replace(";;", ";") + "\n");
				counter++;
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

		String year = fields[3];

		if (year.startsWith("FT")) {
			year = year.substring(2);
		}

		final int intYear = Integer.parseInt(year);

		if (intYear < individual.getBirthYear() || intYear > individual.getDeathYear()) {
			return;
		}

		final String csvFileName = args[2] + "/" + fields[4] + ".csv";
		final String location = fields[0] + ";" + fields[1] + ";" + fields[2] + ";";

		processCsvFile(individual, csvFileName, location, args[3], bw);
	}
}
