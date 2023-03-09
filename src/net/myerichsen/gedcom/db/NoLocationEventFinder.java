package net.myerichsen.gedcom.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Find all individuals with no events in a location and with less than two
 * children, possibly to be removed from the database
 *
 * @author Michael Erichsen
 * @version 9. mar. 2023
 *
 */

public class NoLocationEventFinder {
	/**
	 * Private class representing an individual
	 *
	 * @author Michael Erichsen
	 * @version 9. mar. 2023
	 *
	 */
	private class NvecIndividual {
		private String id = "";
		private String givenName = "";
		private String surName = "";

		/**
		 * Constructor
		 *
		 * @param id
		 * @param givenName
		 * @param surName
		 */
		public NvecIndividual(String id, String givenName, String surName) {
			this.id = id;
			this.givenName = givenName;
			this.surName = surName;
		}

		/**
		 * @return the surName
		 */
		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return (id != null ? id.replace("I", "").replace("@", "") : "") + ";" + (givenName != null ? givenName : "")
					+ ";" + (surName != null ? surName : "");
		}

	}

	private static final String SELECT_CHILD_COUNT = "SELECT COUNT(*) AS COUNT FROM VEJBY.INDIVIDUAL WHERE FAMC IN "
			+ "( SELECT ID FROM VEJBY.FAMILY WHERE HUSBAND = '%s' OR WIFE = '%s')";
	private static final String SELECT_INDIVIDUAL = "SELECT * FROM VEJBY.INDIVIDUAL";
	private static final String SELECT_EVENT = "SELECT * FROM VEJBY.EVENT WHERE INDIVIDUAL = '%s'";

	private static final String HEADER = "Id;Fornavn;Efternavn;Antal børn";
	private static final Logger logger = Logger.getLogger("NoLocationEventFinder");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: NoLocationEventFinder location derbydatabasepath outputdirectory");
			System.exit(4);
		}

		final NoLocationEventFinder nvec = new NoLocationEventFinder();

		try {
			nvec.execute(args);
		} catch (final SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Worker method
	 * 
	 * @param args
	 *
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		String place = "";
		String query = "";
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		NvecIndividual individual = null;
		final HashSet<NvecIndividual> hsni = new HashSet<>();
		int counter = 0;

		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn1 = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn1.createStatement();
		String location = args[0].toLowerCase();

		// Get all individuals

		ResultSet rs = statement.executeQuery(SELECT_INDIVIDUAL);

		while (rs.next()) {
			individual = new NvecIndividual(rs.getString("ID").trim(), rs.getString("GIVENNAME").trim(),
					rs.getString("SURNAME").trim());
			hsni.add(individual);
			counter++;

			// If any event has the location as location then remove individual from hash
			// set

			query = String.format(SELECT_EVENT, individual.getId());
			ps = conn1.prepareStatement(query);
			rs2 = ps.executeQuery();

			while (rs2.next()) {
				place = rs2.getString("PLACE");

				if ((place != null) && (place.toLowerCase().contains(location))) {
					hsni.remove(individual);
					break;
				}
			}

			if ((counter % 1000) == 0) {
				logger.info("Analysed individuals: " + counter);
			}
		}

		logger.info(counter + " analysed individuals");
		logger.info(hsni.size() + " individuals have no events in " + location);

		// If children count less than two or more, then print output

		int count = 0;
		counter = 0;

		final String outfile = args[2] + "\\No" + location + "Events.csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outfile)));
		writer.write(HEADER + "\n");

		for (final NvecIndividual ni : hsni) {
			count = 0;
			query = String.format(SELECT_CHILD_COUNT, ni.getId(), ni.getId());
			rs = statement.executeQuery(query);

			if (rs.next()) {
				count = rs.getInt("COUNT");
			}

			if (count < 2) {
				writer.write(ni.toString() + ";" + count + "\n");
				counter++;
			}
		}

		writer.flush();
		writer.close();
		statement.close();
		logger.info(counter + " individuals with less than two children written to " + outfile);
	}
}
