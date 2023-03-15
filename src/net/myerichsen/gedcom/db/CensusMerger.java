package net.myerichsen.gedcom.db;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find primary census record for each census record for each individual in the
 * Derby database
 *
 * @author Michael Erichsen
 * @version 16. mar. 2023
 *
 */

public class CensusMerger {
	/**
	 * Class representing enriched data from the census table
	 *
	 * @author Michael Erichsen
	 * @version 16. mar. 2023
	 *
	 */
	private class CensusRecord {
		private String kipNr = "";
		private String husstandsFamilieNr = "";
		private Date ftDato = null;
		private String currentId = "";
		private String primaryName = "";
		private String place = "";
		private String currentPhonName = "";
		private String name = "";
		private int loebenr = 0;
		private String matrNrAdresse = "";
		private int foedeAar = 0;
		private int primaryFoedeAar = 0;

		/**
		 * @return the currentId
		 */
		public String getCurrentId() {
			return currentId;
		}

		/**
		 * @return the ftDato
		 */
		public Date getFtDato() {
			return ftDato;
		}

		/**
		 * @return the husstandsFamilieNr
		 */
		public String getHusstandsFamilieNr() {
			return husstandsFamilieNr;
		}

		/**
		 * @return the kipNr
		 */
		public String getKipNr() {
			return kipNr;
		}

		/**
		 * @return the loebenr
		 */
		public int getLoebenr() {
			return loebenr;
		}

		/**
		 * @return the matrNrAdresse
		 */
		public String getMatrNrAdresse() {
			return matrNrAdresse;
		}

		/**
		 * @return the currentPhonName
		 */
		public String getPhonName() {
			return currentPhonName;
		}

		/**
		 * @return the place
		 */
		public String getPlace() {
			return place;
		}

		/**
		 * @return the primaryName
		 */
		public String getPrimaryName() {
			return primaryName;
		}

		/**
		 * @param string the currentId to set
		 */
		public void setCurrentId(String string) {
			this.currentId = string.trim();
		}

		/**
		 * @param currentPhonName the currentPhonName to set
		 */
		public void setCurrentPhonName(String fonnavn) {
			this.currentPhonName = fonnavn.trim();
		}

		/**
		 * @param foedeAar the foedeAar to set
		 */
		public void setFoedeAar(int foedeAar) {
			this.foedeAar = foedeAar;
		}

		/**
		 * @param date the ftDato to set
		 */
		public void setFtDato(Date date) {
			this.ftDato = date;
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
		 * @param loebenr the loebenr to set
		 */
		public void setLoebenr(int loebenr) {
			this.loebenr = loebenr;
		}

		/**
		 * @param matrNrAdresse the matrNrAdresse to set
		 */
		public void setMatrNrAdresse(String matrNrAdresse) {
			this.matrNrAdresse = matrNrAdresse;
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
		 * @param primaryFoedeAar the primaryFoedeAar to set
		 */
		public void setPrimaryFoedeAar(int primaryFoedeAar) {
			this.primaryFoedeAar = primaryFoedeAar;
		}

		/**
		 * @param primaryName the primaryName to set
		 */
		public void setPrimaryName(String primaryIndividual) {
			this.primaryName = primaryIndividual;
		}

		@Override
		public String toString() {
			return (currentId != null ? currentId : "") + ";" + (name != null ? name : "") + ";" + foedeAar + ";"
					+ (ftDato != null ? ftDato : "") + ";" + loebenr + ";" + (place != null ? place : "") + ";"
					+ (matrNrAdresse != null ? matrNrAdresse : "") + ";"
					+ (husstandsFamilieNr != null ? husstandsFamilieNr : "") + ";"
					+ (primaryName != null ? primaryName : "") + ";" + primaryFoedeAar + ";" + kipNr;
		}
	}

	/**
	 * Helper class implementing Comparator interface
	 *
	 * @author Michael Erichsen
	 * @version 16. mar. 2023
	 *
	 */
	public class CensusRecordComparator implements Comparator<CensusRecord> {

		/**
		 * Sort in ascending order of id and census date
		 */
		@Override
		public int compare(CensusRecord o1, CensusRecord o2) {
			final String key1 = o1.getCurrentId() + o1.getFtDato().toLocalDate().getYear();
			final String key2 = o2.getCurrentId() + o2.getFtDato().toLocalDate().getYear();

			return key1.compareToIgnoreCase(key2);
		}
	}

	private static final String FOUR_DIGITS = "\\d{4}";
	private static final String SELECT_MIN_LOEBENR = "SELECT MIN(LOEBENR) AS MINLBNR "
			+ "FROM VEJBY.CENSUS WHERE KIPNR = '%s' AND HUSSTANDS_FAMILIENR = '%s' " + "AND MATR_NR_ADRESSE = '%s'";
	private static final String SELECT_PRIMARY_KILDENAVN = "SELECT * FROM VEJBY.CENSUS WHERE KIPNR = '%s' AND LOEBENR = %d";
	private static final String SELECT_INDIVIDUAL_FONNAVN = "SELECT * FROM VEJBY.INDIVIDUAL WHERE ID = '%s'";
	private static final String SELECT_EVENT = "SELECT * FROM VEJBY.EVENT WHERE TYPE ='Census'";
	private static final String SELECT_CENSUS = "SELECT * FROM VEJBY.CENSUS WHERE AMT = '%s' AND HERRED = '%s' AND SOGN = '%s' "
			+ "AND FTAAR = %d AND FONNAVN = '%s'";
	private static final String HEADER = "Id;Navn;Fødeår;FT år;Løbenr.;Sted;Adresse;Familie nr.;Hovedperson;Dennes fødeår;Kip nr.";
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
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return birth year
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private int combineBirthFields(ResultSet rs) throws SQLException {
		try {
			if (rs.getInt("FOEDEAAR") > 0) {
				return rs.getInt("FOEDEAAR");
			}

			final String string = rs.getString("FOEDT_KILDEDATO");

			if ((string != null) && (string.length() > 0)) {
				final Pattern p = Pattern.compile(FOUR_DIGITS);
				final Matcher m = p.matcher(rs.getString("FOEDT_KILDEDATO"));

				if (m.find()) {
					return Integer.parseInt(m.group(0));
				}
			}

			if (rs.getInt("ALDER") > 0) {
				final Pattern p = Pattern.compile(FOUR_DIGITS);
				final Matcher m = p.matcher(rs.getString("FTAAR"));

				if (m.find()) {
					return Integer.parseInt(m.group(0)) - rs.getInt("ALDER");
				}
			}
		} catch (final SQLException e) {
			logger.warning(e.getMessage() + e.getSQLState());
			throw e;
		}

		return 0;
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		final String dbURL = "jdbc:derby:" + args[0];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn.createStatement();

		logger.info("Fetch census events");
		List<CensusRecord> lcr = fetchEvents(statement);

		logger.info("Add names for each individual");
		lcr = fetchIndividualData(statement, lcr);

		logger.info("Match events with census records");
		fetchCensusData(conn, statement, lcr);
		statement.close();

		logger.info("Print output");
		printOutput(args, lcr);
	}

	/**
	 * Add data from the census table
	 *
	 * @param conn
	 * @param statement
	 * @param lcr
	 * @throws Exception
	 */
	private List<CensusRecord> fetchCensusData(Connection conn, final Statement statement, List<CensusRecord> lcr)
			throws Exception {
		String[] placeParts;
		String amt, herred, sogn, query;
		int year, length;
		int counter = 0;
		ResultSet rs, rs2, rs3;
		PreparedStatement ps2, ps3;
		final List<CensusRecord> lcrAdd = new ArrayList<>();

		logger.info("Found " + lcr.size() + " event records");

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
			year = cr.getFtDato().toLocalDate().getYear();

			// Find census record to match

			query = String.format(SELECT_CENSUS, amt, herred, sogn, year, cr.getPhonName());
			rs = statement.executeQuery(query);

			while (rs.next()) {
				cr.setKipNr(rs.getString("KIPNR"));
				cr.setLoebenr(rs.getInt("LOEBENR"));
				cr.setMatrNrAdresse(rs.getString("MATR_NR_ADRESSE"));
				cr.setHusstandsFamilieNr(rs.getString("HUSSTANDS_FAMILIENR"));
				cr.setFoedeAar(combineBirthFields(rs));

				// Find lowest loebenr with this familynr

				final String query2 = String.format(SELECT_MIN_LOEBENR, cr.getKipNr(), cr.getHusstandsFamilieNr(),
						cr.getMatrNrAdresse());

				ps2 = conn.prepareStatement(query2);
				rs2 = ps2.executeQuery();

				int minlbnr = 0;
				boolean found = false;

				if (rs2.next()) {
					minlbnr = rs2.getInt("MINLBNR");

					if (cr.getLoebenr() != minlbnr) {
						final String query3 = String.format(SELECT_PRIMARY_KILDENAVN, cr.getKipNr(), minlbnr);
						ps3 = conn.prepareStatement(query3);
						rs3 = ps3.executeQuery();

						while (rs3.next()) {
							cr.setPrimaryName(rs3.getString("KILDENAVN"));
							cr.setPrimaryFoedeAar(combineBirthFields(rs3));

							if (found) {
								lcrAdd.add(cr);
							}

							found = true;
						}
					}
				}
			}
		}

		lcr.addAll(lcrAdd);

		logger.info("Found " + lcr.size() + " records");
		return lcr;
	}

	/**
	 * Fetch census records from the EVENTS table
	 *
	 * @param lcr
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<CensusRecord> fetchEvents(Statement statement) throws SQLException {
		final ResultSet rs = statement.executeQuery(SELECT_EVENT);
		final List<CensusRecord> lcr = new ArrayList<>();

		while (rs.next()) {
			final CensusRecord cr = new CensusRecord();
			cr.setCurrentId(rs.getString("INDIVIDUAL"));
			cr.setFtDato(rs.getDate("DATE"));
			cr.setPlace(rs.getString("PLACE"));

			logger.fine(cr.toString());
			lcr.add(cr);
		}

		return lcr;
	}

	/**
	 * Add data from the individual table
	 *
	 * @param statement
	 * @param lcr
	 * @throws SQLException
	 */
	private List<CensusRecord> fetchIndividualData(Statement statement, List<CensusRecord> lcr) throws SQLException {
		String query;
		ResultSet rs;

		for (final CensusRecord cr : lcr) {
			query = String.format(SELECT_INDIVIDUAL_FONNAVN, cr.getCurrentId());
			rs = statement.executeQuery(query);

			while (rs.next()) {
				cr.setName(rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim());
				cr.setCurrentPhonName(rs.getString("PHONNAME"));
			}
		}

		return lcr;
	}

//	/**
//	 * Return all columns in a row from a query
//	 *
//	 * @param rs
//	 * @return
//	 * @throws SQLException
//	 */
//	private String getRow(ResultSet rs) throws SQLException {
//		final StringBuffer sb = new StringBuffer();
//
//		for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//			sb.append((rs.getString(i) != null ? rs.getString(i).trim() : "") + ";");
//		}
//
//		return sb.toString();
//	}

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
		int counter = 0;

		Collections.sort(lcr, new CensusRecordComparator());

		for (final CensusRecord cr : lcr) {
			if (cr.getPrimaryName().length() > 0) {
				writer.write(cr.toString() + "\n");
				counter++;
			}
		}

		writer.flush();
		writer.close();
		logger.info(counter + " census records written to " + outfile);
	}
}
