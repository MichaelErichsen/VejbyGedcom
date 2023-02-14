package net.myerichsen.gedcom.db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Find all probate and census entries that match a given individual ID and
 * location in the derby database
 *
 * @author Michael Erichsen
 * @version 14. feb. 2023
 */
public class FindLocationSources {
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println("Usage: FindLocationSources location vejbydatabasepath "
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
	 * @throws Exception
	 */
	private void execute(String[] args) throws SQLException, IOException {
		// Connect to Derby
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn.createStatement();

		// Get ID's of all individuals in GEDCOM extract database with events in Holbo
		// shire

		final List<DBIndividual> ldbi = getIndividualIds(statement, args);

		// For each individual, get census records
//		String[] vejbyArgs;
//
//		for (final DBIndividual individual : ldbi) {
//			vejbyArgs = new String[4];
//			vejbyArgs[0] = individual.getId();
//			vejbyArgs[1] = args[1];
//			vejbyArgs[2] = args[3];
//			vejbyArgs[3] = args[4];
//
//			try {
//				new CensusFinder(vejbyArgs);
//			} catch (final Exception e) {
//				e.printStackTrace();
//			}
//		}

		// For each individual, get probate records

		for (final DBIndividual individual : ldbi) {
			findProbate(individual, args);
		}

		logger.info("Program ended");
	}

	/**
	 * @param indId
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
//						+ " FETCH FIRST 25 ROWS ONLY";

		final ResultSet rs = statement.executeQuery(query);
		String singleLine;
		List<String> outLines = new ArrayList<String>();

		while (rs.next()) {
			if (rs.getString("COVERED_DATA").toLowerCase().contains(args[0].toLowerCase())) {
				logger.fine(query);
				singleLine = individual.getId() + ";" + rs.getString("ID").trim() + ";"
						+ rs.getString("FROMDATE").trim() + ";" + rs.getString("TODATE").trim() + ";"
						+ rs.getString("PLACE").trim() + ";" + rs.getString("EVENTTYPE").trim() + ";"
						+ rs.getString("VITALTYPE").trim() + ";" + rs.getString("COVERED_DATA").trim() + ";"
						+ rs.getString("SOURCE").trim() + ";" + rs.getString("NAME").trim() + ";"
						+ rs.getString("FONKOD").trim();
				singleLine = singleLine.replaceAll("\\r\\n", " ¤ ");
				logger.fine(singleLine);

				if (singleLine.compareToIgnoreCase("Kronborg") >= 0) {
					outLines.add(singleLine);
					counter++;
				}

			}
		}

		logger.info(counter + " records read for " + individual.getName());

		statement.close();

		if (counter > 0) {
			String outName = args[4] + "/" + individual.getName() + "_probate.csv";
			bw = new BufferedWriter(new FileWriter(outName));

			String header = "GEDCOM ID;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;VITALTYPE;COVERED_DATA;SOURCE;NAME;FONKOD";
			bw.write(header + "\n");

			for (String string : outLines) {
				bw.write(string + "\n");
			}

			bw.flush();
			bw.close();

			logger.info(counter + " records written to " + outName);
		}

	}

	/**
	 * Get all individuals in the Derby database with events in the location
	 * 
	 * @param statement
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private List<DBIndividual> getIndividualIds(Statement statement, String[] args) throws SQLException {
		DBEvent dbe;
		DBIndividual individual;
		final HashSet<String> hss = new HashSet<>();
		final List<DBIndividual> ldbi = new ArrayList<>();
		int counter = 0;

		final ResultSet rs = statement.executeQuery("SELECT * FROM VEJBY.EVENT FETCH FIRST 100 ROWS ONLY");

		while (rs.next()) {
			dbe = new DBEvent(rs.getInt("ID"), rs.getString("INDIVIDUAL"), rs.getString("PLACE"));

			if (dbe.isInLocation(args[0])) {
				hss.add(dbe.getIndividual().trim());
			}
		}

		for (final String id : hss) {
			individual = new DBIndividual(statement, id);
			ldbi.add(individual);
			counter++;
		}

		logger.info(counter + " individuals with events in the location found");
		return ldbi;
	}
}
