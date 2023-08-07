package net.myerichsen.archivesearcher.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Find all individuals with an event in a location or with a child born or
 * christened at this location to be removed from the database
 *
 * @author Michael Erichsen
 * @version 12. mar. 2023
 *
 */

public class FindLocationIndividuals {
	/**
	 * Private class representing an individual
	 *
	 * @author Michael Erichsen
	 * @version 12. mar. 2023
	 *
	 */
	private static class NvecIndividual {
		private String id = "";
		private String givenName = "";
		private String surName = "";
		private int birthYear;
		private int deathYear;
		private String birthPlace = "";

		/**
		 * Constructor
		 *
		 * @param id
		 * @param givenName
		 * @param surName
		 * @param birthYear
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

		/**
		 * @param birthPlace the birthPlace to set
		 */
		public void setBirthPlace(String birthPlace) {
			this.birthPlace = birthPlace;
		}

		/**
		 * @param date the birthYear to set
		 */
		public void setBirthYear(int date) {
			this.birthYear = date;
		}

		/**
		 * @param date the deathYear to set
		 */
		public void setDeathYear(int date) {
			this.deathYear = date;

		}

		@Override
		public String toString() {
			return (id != null ? id.replace("I", "").replace("@", "") : "") + ";" + (givenName != null ? givenName : "")
					+ ";" + (surName != null ? surName : "") + ";" + (birthYear != 0 ? birthYear : "") + ";"
					+ (birthPlace != null ? birthPlace : "") + ";" + (deathYear != 0 ? deathYear : "");
		}

	}

	private static final String SELECT_CHILDREN = "SELECT ID FROM VEJBY.INDIVIDUAL WHERE FAMC IN "
			+ "(SELECT ID FROM VEJBY.FAMILY WHERE HUSBAND = '%s' OR WIFE = '%s')";
	private static final String SELECT_INDIVIDUAL = "SELECT * FROM VEJBY.INDIVIDUAL";
	private static final String SELECT_EVENT = "SELECT * FROM VEJBY.EVENT WHERE INDIVIDUAL = '%s'";
	private static final String HEADER = "Id;Fornavn;Efternavn;Født;Fødested;Død";
	private static final Logger logger = Logger.getLogger("FindLocationIndividuals");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: FindLocationIndividuals location derbydatabasepath outputdirectory");
			System.exit(4);
		}

		final FindLocationIndividuals fli = new FindLocationIndividuals();

		try {
			fli.execute(args);
		} catch (final SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if any children are born or christened in the location
	 *
	 * @param statement
	 * @param individual
	 * @param location
	 * @return
	 * @throws SQLException
	 */
	private Boolean checkChildrenLocation(Connection conn, NvecIndividual individual, String location)
			throws SQLException {
		final List<String> ls = new ArrayList<>();
		PreparedStatement ps;
		ResultSet rs2;
		String place = "";
		String query = String.format(SELECT_CHILDREN, individual.getId(), individual.getId());
		final Statement statement = conn.createStatement();
		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			ls.add(rs.getString("ID"));
		}

		for (final String childId : ls) {
			query = String.format(SELECT_EVENT, childId);
			ps = conn.prepareStatement(query);
			rs2 = ps.executeQuery();

			while (rs2.next()) {
				if ("Birth".equals(rs2.getString("TYPE").trim())
						|| "Christening".equals(rs2.getString("TYPE").trim())) {
					place = rs2.getString("PLACE");

					if (place != null && place.toLowerCase().contains(location)) {
						return true;
					}
				}

			}
		}
		return false;
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
		String note;
		Date year;

		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn1 = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn1.createStatement();
		final String location = args[0].toLowerCase();

		// For each individual

		final ResultSet rs = statement.executeQuery(SELECT_INDIVIDUAL);

		while (rs.next()) {
			individual = new NvecIndividual(rs.getString("ID").trim(), rs.getString("GIVENNAME").trim(),
					rs.getString("SURNAME").trim());
			counter++;

			query = String.format(SELECT_EVENT, individual.getId());
			ps = conn1.prepareStatement(query);
			rs2 = ps.executeQuery();

			while (rs2.next()) {
				// Add birth year and place to individual
				if ("Birth".equals(rs2.getString("TYPE").trim())
						|| "Christening".equals(rs2.getString("TYPE").trim())) {
					individual.setBirthYear(rs2.getDate("DATE").toLocalDate().getYear());
					place = rs2.getString("PLACE");

					if (place != null) {
						individual.setBirthPlace(rs2.getString("PLACE").trim());
					}
				}

				// Add death year to individual
				if ("Death".equals(rs2.getString("TYPE").trim()) || "Burial".equals(rs2.getString("TYPE").trim())) {
					year = rs2.getDate("DATE");

					if (year != null) {
						individual.setDeathYear(rs2.getDate("DATE").toLocalDate().getYear());
					}
				}

				// Add if any location matches
				place = rs2.getString("PLACE");

				if (place != null && place.toLowerCase().contains(location)) {
					hsni.add(individual);
					break;
				}
				// Add if any note matches
				note = rs2.getString("NOTE");

				// Add if birth of any children matches
				if (note != null && note.toLowerCase().contains(location)
						|| checkChildrenLocation(conn1, individual, location)) {
					hsni.add(individual);
					break;
				}
			}

			if (counter % 1000 == 0) {
				logger.info("Analysed individuals: " + counter);
			}
		}

		logger.info(counter + " analysed individuals");

		// Print output
		final String outfile = args[2] + "\\" + location + "Events.csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outfile)));
		writer.write(HEADER + "\n");

		for (final NvecIndividual ni : hsni) {
			writer.write(ni.toString() + "\n");
		}

		writer.flush();
		writer.close();
		statement.close();
		logger.info(hsni.size() + " individuals with events in " + location + " written to " + outfile);
	}
}