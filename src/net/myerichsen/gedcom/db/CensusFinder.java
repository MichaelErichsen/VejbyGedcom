package net.myerichsen.gedcom.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 * @version 2320-01-26
 *
 */
public class CensusFinder {
	private static Logger logger;

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
		logger.setLevel(Level.FINE);

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
		final DBIndividual individual = getIndividual(statement, args[0]);

		parseCsvFiles(individual, args[2]);

	}

	/**
	 * Find individual in Derby database
	 *
	 * @param statement
	 *
	 * @param id
	 * @throws SQLException
	 */
	private DBIndividual getIndividual(Statement statement, String id) throws SQLException {
		DBIndividual individual = new DBIndividual();
		String name = "";
		final String query = "SELECT GIVENNAME, SURNAME from VEJBY.INDIVIDUAL WHERE ID ='" + id + "'";

		// TODO Move this into DBIndivudaul class

		final ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			name = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();
		}

		individual.setName(name);
		logger.info("Found name " + name);

		// TODO Find events for ID with type birth or christening. Extract date
		// and put into individual

		return individual;
	}

	/**
	 * Parse each csv file in the directory
	 *
	 * @param csvfilepath
	 * @param args
	 * @throws IOException
	 */
	private void parseCsvFiles(DBIndividual individual, String csvfilepath) throws IOException {
		BufferedReader br;
		String line;
		String name = individual.getName();

		final Set<String> fileList = Stream.of(new File(csvfilepath).listFiles()).filter(file -> !file.isDirectory())
				.map(File::getName).collect(Collectors.toSet());

		for (final String csvfile : fileList) {
			br = new BufferedReader(new FileReader(new File(csvfilepath + "/" + csvfile)));

			// TODO Ignore if before birth or after death

			while ((line = br.readLine()) != null) {
				if (line.contains(name)) {
					logger.info(line);

					// TODO check the age or birth date in the census with the
					// birth or christening date in the data base. If it is
					// within two years then output in csv format

					// TODO Find file metadata i kipfile and add place to output

					// TODO Save first line in csv file to find age or birth
					// date
				}
			}

			br.close();
		}
	}

}
