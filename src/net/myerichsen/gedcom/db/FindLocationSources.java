package net.myerichsen.gedcom.db;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Find all probate and census entries that match a given individual ID in the
 * Derby database
 *
 * @author Michael Erichsen
 * @version 17. feb. 2023
 */
public class FindLocationSources {
	/**
	 *
	 */
	private static final String KRONBORG = "Kronborg";
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("Usage: FindLocationSources id vejbydatabasepath "
					+ "probatedatabasepath dddcsvfilepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("FindLocationSources");

		final FindLocationSources fhs = new FindLocationSources();

		try {
			fhs.execute(args);
		} catch (final Exception e) {
			try {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} catch (final SQLException e1) {
			}
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		// Connect to Derby
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn.createStatement();

		final DBIndividual individual = new DBIndividual(statement, args[0]);
		findProbate(individual, args);

		final String[] vejbyArgs = new String[4];
		vejbyArgs[0] = args[0];
		vejbyArgs[1] = args[1];
		vejbyArgs[2] = args[3];
		vejbyArgs[3] = args[4];

		try {
			new CensusFinder(vejbyArgs);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		logger.info("Program ended");
	}

	/**
	 * Find probates mentioning the individual
	 *
	 * @param individual
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void findProbate(DBIndividual individual, String[] args) throws SQLException, IOException {
		final String dbURL = "jdbc:derby:" + args[2];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.fine("Connected to database " + dbURL);
		final Statement statement = conn.createStatement();
		int counter = 0;
		BufferedWriter bw;

		final String query = "SELECT * FROM GEDCOM.EVENT JOIN GEDCOM.INDIVIDUAL "
				+ "ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID WHERE GEDCOM.INDIVIDUAL.FONKOD = '"
				+ individual.getPhonName().trim() + "'";

		final ResultSet rs = statement.executeQuery(query);
		String singleLine;
		final HashSet<String> outLines = new HashSet<>();
		String coveredData;
		String source;
		String name;

		while (rs.next()) {
			coveredData = rs.getString("COVERED_DATA");
			source = rs.getString("SOURCE");
			name = rs.getString("NAME").trim();

			if (source.contains(KRONBORG) && coveredData.contains(name)) {
				singleLine = individual.getId() + ";" + rs.getString("ID").trim() + ";"
						+ rs.getString("FROMDATE").trim() + ";" + rs.getString("TODATE").trim() + ";"
						+ rs.getString("PLACE").trim() + ";" + rs.getString("EVENTTYPE").trim() + ";"
						+ rs.getString("VITALTYPE").trim() + ";" + coveredData.replace(";", ".").trim() + ";"
						+ source.replace(";", ".").trim() + ";" + name + ";" + rs.getString("FONKOD").trim();
				singleLine = singleLine.replaceAll("\\r\\n", " ¤ ");
				outLines.add(singleLine);
				counter++;
			}
		}

		statement.close();

		if (counter > 0) {
			final String outName = args[4] + "/" + individual.getName() + "_probates.csv";
			bw = new BufferedWriter(new FileWriter(outName));

			final String header = "GEDCOM ID;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;VITALTYPE;COVERED_DATA;SOURCE;NAME;FONKOD";
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
}
