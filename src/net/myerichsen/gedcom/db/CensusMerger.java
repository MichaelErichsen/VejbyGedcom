package net.myerichsen.gedcom.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Find primary census record for each census record for each individual in the
 * Derby database
 *
 * @author Michael Erichsen
 * @version 14. mar. 2023
 *
 */
public class CensusMerger {
	private class CensusRecord {
		private String kipNr = "";
		private String husstandsFamilieNr = "";
		private Date ftAar = null;
		private String currentIndividual = "";
		private String primaryIndividual = "";
		private String place = "";
		private String sourceDetail = "";
		private String phonName = "";
		private String name = "";

		/**
		 * @return the currentIndividual
		 */
		public String getCurrentIndividual() {
			return currentIndividual;
		}

		/**
		 * @return the phonName
		 */
		public String getFonnavn() {
			return phonName;
		}

		/**
		 * @return the ftAar
		 */
		public Date getFtAar() {
			return ftAar;
		}

		/**
		 * @return the place
		 */
		public String getPlace() {
			return place;
		}

		/**
		 * @return the sourceDetail
		 */
		public String getSourceDetail() {
			return sourceDetail;
		}

		/**
		 * @param string the currentIndividual to set
		 */
		public void setCurrentIndividual(String string) {
			this.currentIndividual = string.trim();
		}

		/**
		 * @param phonName the phonName to set
		 */
		public void setFonnavn(String fonnavn) {
			this.phonName = fonnavn.trim();
		}

		/**
		 * @param date the ftAar to set
		 */
		public void setFtAar(Date date) {
			this.ftAar = date;
		}

		/**
		 * @param husstandsFamilieNr the husstandsFamilieNr to set
		 */
		public void setHusstandsFamilieNr(String husstandsFamilieNr) {
			this.husstandsFamilieNr = husstandsFamilieNr.trim();
		}

		/**
		 * @param kipNr the kipNr to set
		 */
		public void setKipNr(String kipNr) {
			this.kipNr = kipNr.trim();
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @param place the place to set
		 */
		public void setPlace(String place) {
			this.place = place;
		}

		/**
		 * @param sourceDetail the sourceDetail to set
		 */
		public void setSourceDetail(String sourceDetail) {
			this.sourceDetail = sourceDetail;
		}

		@Override
		public String toString() {
			return (name != null ? name : "") + ";" + (kipNr != null ? kipNr : "") + ";"
					+ (husstandsFamilieNr != null ? husstandsFamilieNr : "") + ";" + (ftAar != null ? ftAar : "") + ";"
					+ (currentIndividual != null ? currentIndividual : "") + ";"
					+ (primaryIndividual != null ? primaryIndividual : "") + ";" + (place != null ? place : "") + ";"
					+ (sourceDetail != null ? sourceDetail : "") + ";" + (phonName != null ? phonName : "");
		}

	}

	private static final String SELECT_FROM_VEJBY_INDIVIDUAL_WHERE_ID_S = "SELECT * FROM VEJBY.INDIVIDUAL WHERE ID = '%s'";
	private static final String SELECT_INDIVIDUAL_FONNAVN = SELECT_FROM_VEJBY_INDIVIDUAL_WHERE_ID_S;
	private static final String SELECT_EVENT = "SELECT * FROM VEJBY.EVENT WHERE TYPE ='Census' ORDER BY INDIVIDUAL";
	private static final String SELECT_CENSUS = "SELECT * FROM VEJBY.CENSUS WHERE AMT = '%s' AND HERRED = '%s' AND SOGN = '%s' and FTAAR = %d";

	private static final String HEADER = "Navn;Kip nr.;familie nr.;FT år;Person;Hovedperson;Sted;Detaljer;Fonetisk navn";
	private static final Logger logger = Logger.getLogger("CensusMerger");

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: CensusMerger derbydatabasepath outputdirectory");
			System.exit(4);
		}

		final CensusMerger cm = new CensusMerger();

		try {
			cm.execute(args);
		} catch (final SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add data from the individual table
	 * 
	 * @param statement
	 * @param lcr
	 * @throws SQLException
	 */
	private List<CensusRecord> addIndividualData(Statement statement, List<CensusRecord> lcr) throws SQLException {
		String query;
		ResultSet rs;

		for (final CensusRecord cr : lcr) {
			query = String.format(SELECT_INDIVIDUAL_FONNAVN, cr.getCurrentIndividual());
			rs = statement.executeQuery(query);

			if (rs.next()) {
				cr.setName(rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim());
				cr.setFonnavn(rs.getString("PHONNAME"));
			}
		}
		return lcr;
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + args[0];
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
		final Statement statement = connectToDB(args);

		logger.info("Fetch census events");
		List<CensusRecord> lcr = fetchEvents(statement);

		logger.info("Add names for each individual");
		lcr = addIndividualData(statement, lcr);

		logger.info("Match events with census records");
		fetchCensusData(statement, lcr);
		statement.close();

		logger.info("Print output");
		printOutput(args, lcr);
	}

	/**
	 * Print the output to a .csv file
	 * 
	 * @param args
	 * @param lcr
	 * @throws IOException
	 */
	private void printOutput(String[] args, List<CensusRecord> lcr) throws IOException {
		final String outfile = args[1] + "\\CensusMerger.csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outfile)));
		writer.write(HEADER + "\n");

		for (CensusRecord cr : lcr) {
			writer.write(cr.toString() + "\n");
		}

		writer.flush();
		writer.close();
		logger.info(lcr.size() + " census records written to " + outfile);
	}

	/**
	 * Fetch data from the census table
	 * 
	 * @param statement
	 * @param lcr
	 * @throws SQLException
	 */
	private List<CensusRecord> fetchCensusData(final Statement statement, List<CensusRecord> lcr) throws SQLException {
		String[] placeParts;
		int length;
		String amt;
		String herred;
		String sogn;
		int year;
		String query;
		ResultSet rs;
		String[] detailParts;
		String generateKey;
		int counter = 0;

		final Fonkod fk = new Fonkod();

		logger.info("Found " + lcr.size() + " records");

		for (final CensusRecord cr : lcr) {
			counter++;

			if ((counter % 100) == 0) {
				logger.info("Counter: " + counter);
			}

			if (cr.getPlace() == null) {
				continue;
			}

			// TODO Handle places without herred and sogn
			placeParts = cr.getPlace().split(", ");
			length = placeParts.length;
			amt = placeParts[length - 1];
			herred = (length > 1 ? placeParts[length - 2] : "");
			sogn = (length > 2 ? placeParts[length - 3] : "");
			year = cr.getFtAar().toLocalDate().getYear();

			// Now find census record to match

			query = String.format(SELECT_CENSUS, amt, herred, sogn, year);
			rs = statement.executeQuery(query);

			if (rs.next()) {
				// TODO If first person in family then add as primary individual
				cr.setKipNr(rs.getString("KIPNR"));

				if (cr.getSourceDetail() == null) {
					continue;
				}

				detailParts = cr.getSourceDetail().split(", ");

				generateKey = "";

				try {
					generateKey = fk.generateKey(detailParts[0]);
				} catch (final Exception e) {
				}

				if (generateKey.trim().equals(cr.getFonnavn().trim())) {
					cr.setHusstandsFamilieNr(rs.getString("HUSSTANDS_FAMILIENR"));
				}
			}

			logger.fine(cr.toString());
		}
		return lcr;
	}

	/**
	 * Fetch events and store data in a list of records
	 * 
	 * @param lcr
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<CensusRecord> fetchEvents(Statement statement) throws SQLException {
		final ResultSet rs = statement.executeQuery(SELECT_EVENT);
		final List<CensusRecord> lcr = new ArrayList<>();
		int counter = 0;

		while (rs.next()) {
			final CensusRecord cr = new CensusRecord();
			cr.setCurrentIndividual(rs.getString("INDIVIDUAL"));
			cr.setFtAar(rs.getDate("DATE"));
			cr.setPlace(rs.getString("PLACE"));
			cr.setSourceDetail(rs.getString("SOURCEDETAIL"));
			logger.fine(cr.toString());
			lcr.add(cr);

			counter++;

			if (counter >= 50) {
				return lcr;
			}
		}
		return lcr;
	}
}
