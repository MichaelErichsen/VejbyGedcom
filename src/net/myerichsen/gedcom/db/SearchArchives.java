package net.myerichsen.gedcom.db;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Find all registry entries that match the phonetic name of a given individual
 * in the derby database
 *
 * @author Michael Erichsen
 * @version 2. mar. 2023
 *
 */
public class SearchArchives {
	private static final String KRONBORG = "Kronborg";
	private static final String SELECT = "SELECT * FROM GEDCOM.EVENT "
			+ "JOIN GEDCOM.INDIVIDUAL ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID "
			+ "WHERE GEDCOM.INDIVIDUAL.FONKOD = '%s'";
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println(
					"Usage: SearchArchives name vejbydatabasepath probatedatabasepath dddcsvfilepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("SearchArchives");

		final SearchArchives sa = new SearchArchives();

		try {
			sa.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param dbpath
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String dbpath) throws SQLException {
		final String dbURL = "jdbc:derby:" + dbpath;
		final Connection conn1 = DriverManager.getConnection(dbURL);
		logger.fine("Connected to database " + dbURL);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {

		// Search DDD Census files
//		searchDDDFiles(args);

		// Search Census Derby table
		searchCensusTable(args);

		// Search for probates
		findProbate(args);

		logger.info("Program ended");
	}

	/**
	 * Find probates mentioning the individual
	 *
	 * @param individual
	 * @param args
	 * @throws Exception
	 */
	private void findProbate(String[] args) throws Exception {
		final Statement statement = connectToDB(args[2]);

		final String phonName = new Fonkod().generateKey(args[0]);
		final String query = String.format(SELECT, phonName);
		final ResultSet rs = statement.executeQuery(query);

		String singleLine;
		final HashSet<String> outLines = new HashSet<>();
		String coveredData;
		String source;
		String name;
		int counter = 0;

		while (rs.next()) {
			coveredData = rs.getString("COVERED_DATA");
			source = rs.getString("SOURCE");
			name = rs.getString("NAME").trim();

			if (source.contains(KRONBORG) && coveredData.contains(name)) {
				singleLine = name + ";" + rs.getString("ID").trim() + ";" + rs.getString("FROMDATE").trim() + ";"
						+ rs.getString("TODATE").trim() + ";" + rs.getString("PLACE").trim() + ";"
						+ rs.getString("EVENTTYPE").trim() + ";" + rs.getString("VITALTYPE").trim() + ";"
						+ coveredData.replace(";", ".").trim() + ";" + source.replace(";", ".").trim() + ";"
						+ rs.getString("FONKOD").trim();
				singleLine = singleLine.replaceAll("\\r\\n", " ¤ ");
				outLines.add(singleLine);
				counter++;
			}
		}

		statement.close();

		if (counter > 0) {
			final String outName = args[4] + "/" + args[0] + "_probates.csv";
			final BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

			final String header = "GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;VITALTYPE;COVERED_DATA;SOURCE;FONKOD";
			bw.write(header + "\n");

			for (final String string : outLines) {
				bw.write(string + "\n");
			}

			bw.flush();
			bw.close();

			logger.info(counter + " records written to " + outName);
			Desktop.getDesktop().open(new File(outName));
		}

	}

	/**
	 * @param args
	 */
	private void searchCensusTable(String[] args) {
		final String[] censusDbArgs = new String[3];
		censusDbArgs[0] = args[0];
		censusDbArgs[1] = args[1];
		censusDbArgs[2] = args[4];

		try {
			new CensusFinderDbByName(censusDbArgs);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
//	private void searchDDDFiles(String[] args) {
//		final String[] censusArgs = new String[4];
//		censusArgs[0] = args[0];
//		censusArgs[1] = args[1];
//		censusArgs[2] = args[3];
//		censusArgs[3] = args[4];
//
//		try {
//			new CensusFinder(censusArgs);
//		} catch (final Exception e) {
//			e.printStackTrace();
//		}
//	}
}
