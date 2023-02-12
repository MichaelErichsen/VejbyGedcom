package net.myerichsen.gedcom.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.FamilyEvent;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.IndividualReference;
import org.gedcom4j.model.NoteStructure;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.StringWithCustomFacts;
import org.gedcom4j.parser.GedcomParser;

/**
 * Class to read a GEDCOM and load data into a Derby database to use for
 * analysis.
 *
 * @author Michael Erichsen
 * @version 12. feb. 2023
 *
 */
public class DBLoader {
	private static Logger logger;
	private static Fonkod fonkod = new Fonkod();
	private static Gedcom gedcom;
	private static int familyCounter = 0;
	private static int individualCounter = 0;
	private static int eventCounter = 0;
	private static int citationCounter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: DBLoader gedcomfile derbydatabasepath");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLoader");
		logger.setLevel(Level.FINE);

		final DBLoader ir = new DBLoader();

		try {
			ir.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	private Statement stmt;

	/**
	 * Delete all rows from all tables
	 *
	 * @throws SQLException
	 *
	 */
	private void clearTables() throws SQLException {
		String query = "DELETE FROM VEJBY.FAMILY";
		stmt.execute(query);
		query = "DELETE FROM VEJBY.INDIVIDUAL";
		stmt.execute(query);
		query = "DELETE FROM VEJBY.EVENT";
		stmt.execute(query);
		query = "DELETE FROM VEJBY.CITATION";
		stmt.execute(query);
	}

	/**
	 * Connect to the Derby database
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String url) throws SQLException {
		final String dbURL1 = "jdbc:derby:C:/Users/michael/VejbyDB";
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		System.out.println("Connected to database " + dbURL1);
		stmt = conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		connectToDB(args[1]);

		System.out.println("Reading " + args[0]);
		readGedcom(args[0]);

		System.out.println("Clearing tables");
		clearTables();

		System.out.println("Parsing families");
		parseAllFamilies();

		System.out.println("Parsing individuals");
		parseAllIndividuals();

		stmt.close();

		System.out.println("Program ended.\n" + familyCounter + " families inserted.\n" + individualCounter
				+ " individuals inserted.\n" + eventCounter + " events inserted\n" + citationCounter
				+ " citations inserted");
	}

	/**
	 * Format a GEDCOM date into Derby format
	 *
	 * @param date
	 * @return
	 */
	private String formatDate(String date) {
		DateTimeFormatter formatter;
		LocalDate localDate;
		String outDate;

		date = date.replace("ABT ", "");
		date = date.replace("AFT ", "");
		date = date.replace("BEF ", "");
		date = date.replace("EST ", "");

		if (date.contains("BET") && date.contains("AND")) {
			final String[] split = date.split("AND");
			date = split[0].replace("BET ", "").trim();
		}

		if (date.length() == 8) {
			date = "01 " + date;
		}

		try {
			formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy")
					.toFormatter(Locale.ENGLISH);
			localDate = LocalDate.parse(date, formatter);
			outDate = localDate.toString();
		} catch (final Exception e) {
			outDate = date + "-01-01";
		}
		return outDate;
	}

	/**
	 * Insert a family into Derby
	 *
	 * @param key
	 * @throws SQLException
	 */
	private void insertFamily(String key) throws SQLException {
		final String query = "INSERT INTO VEJBY.FAMILY (ID, HUSBAND, WIFE) VALUES ('" + key + "', NULL, NULL)";
		logger.fine(query);
		stmt.execute(query);
		familyCounter++;
	}

	/**
	 * Insert a family event into Derby for husband and wife
	 *
	 * @param family
	 * @throws Exception
	 */
	private void insertFamilyEvent(Family family) throws Exception {
		StringBuffer sb;
		StringWithCustomFacts subtype;
		StringWithCustomFacts date;
		List<NoteStructure> noteStructures;
		List<String> lines;
		StringBuffer lineBuffer;
		Place place;

		String query;

		final List<FamilyEvent> events = family.getEvents();

		if (events == null) {
			return;
		}

		for (final FamilyEvent familyEvent : events) {
			sb = new StringBuffer(
					"INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, FAMILY, PLACE, NOTE, INDIVIDUAL) VALUES('");
			sb.append(familyEvent.getType());
			subtype = familyEvent.getSubType();

			if (subtype == null) {
				sb.append("', NULL, ");
			} else {
				sb.append("', '" + subtype.getValue() + "', ");
			}

			date = familyEvent.getDate();

			if (date == null) {
				sb.append("NULL, '" + family.getXref());
			} else {
				sb.append("'" + formatDate(date.getValue()) + "', '" + family.getXref());
			}

			place = familyEvent.getPlace();

			if (place == null) {
				sb.append("', NULL, ");
			} else {
				sb.append("', '" + place.getPlaceName().replace("'", "") + "', ");
			}

			noteStructures = familyEvent.getNoteStructures();

			if (noteStructures == null) {
				sb.append("NULL, '");
			} else {
				lines = noteStructures.get(0).getLines();
				lineBuffer = new StringBuffer();

				for (final String string : lines) {
					lineBuffer.append(string + " ");
				}
				sb.append("'" + lineBuffer.toString() + "', '");
			}

			if (family.getHusband() != null) {
				query = sb.toString() + family.getHusband().getIndividual().getXref() + "')";

				try {
					logger.fine(query);
					stmt.execute(query);
					eventCounter++;
				} catch (final SQLException e) {
					throw new Exception(
							"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				}
			}

			if (family.getWife() != null) {
				query = sb.toString() + family.getWife().getIndividual().getXref() + "')";

				try {
					logger.fine(query);
					stmt.execute(query);
					eventCounter++;
				} catch (final SQLException e) {
					throw new Exception(
							"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				}
			}
		}

	}

	/**
	 * Insert an individual into Derby
	 *
	 * @param individual
	 * @throws Exception
	 * @throws SQLException
	 */
	private void insertIndividual(Individual individual) throws Exception {
		final String xref = individual.getXref();
		String query = "";

		try {
			final String[] split = individual.getNames().get(0).getBasic().replace("'", "").split("/");
			final String given = split[0].trim();
			final String surname = split[1];
			final String sex = individual.getSex().getValue();
			final String phonName = fonkod.generateKey(given) + " " + fonkod.generateKey(surname);

			query = "INSERT INTO VEJBY.INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES ('" + xref + "', '"
					+ given + "', '" + surname + "', '" + sex + "', '" + phonName + "'	)";

			logger.fine(query);
			stmt.execute(query);
			individualCounter++;
		} catch (

		final SQLException e) {
			// Handle duplicates
			if (e.getSQLState().equals("23505")) {
				logger.fine("SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				updateIndividual(individual);
			} else {
				throw new Exception(
						"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
			}
		}
	}

	/**
	 * Insert an individual event into Derby
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void insertIndividualEvent(Individual individual) throws Exception {
		StringBuffer sb;
		StringWithCustomFacts subtype;
		StringWithCustomFacts date;
		Place place;
		List<NoteStructure> noteStructures;
		List<String> lines;
		StringBuffer lineBuffer;
		String query;

		final List<IndividualEvent> events = individual.getEvents();

		if (events == null) {
			return;
		}

		for (final IndividualEvent individualEvent : events) {
			sb = new StringBuffer(
					"INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, INDIVIDUAL, FAMILY, PLACE, NOTE) VALUES('");
			sb.append(individualEvent.getType());
			subtype = individualEvent.getSubType();

			if (subtype == null) {
				sb.append("', NULL, ");
			} else {
				sb.append("', '" + subtype.getValue() + "', ");
			}

			date = individualEvent.getDate();

			if (date == null) {
				sb.append("NULL, '" + individual.getXref() + "', NULL, ");
			} else {
				sb.append("'" + formatDate(date.getValue()) + "', '" + individual.getXref() + "', NULL, ");
			}

			place = individualEvent.getPlace();

			if (place == null) {
				sb.append("NULL, ");
			} else {
				sb.append("'" + place.getPlaceName().replace("'", "") + "', ");
			}

			noteStructures = individualEvent.getNoteStructures();

			if (noteStructures == null) {
				sb.append("NULL)");
			} else {
				lines = noteStructures.get(0).getLines();
				lineBuffer = new StringBuffer();

				for (final String string : lines) {
					lineBuffer.append(string + " ");
				}
				sb.append("'" + lineBuffer.toString() + "')");
			}

			query = sb.toString();
			logger.info(query);

			try {
				logger.info(query);
				stmt.execute(query);
				eventCounter++;
			} catch (final SQLException e) {
				throw new Exception(
						"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
			}
		}
	}

	/**
	 * Insert an individual into Derby
	 *
	 * @param individual
	 * @throws Exception
	 * @throws SQLException
	 */
	private void insertIndividualWithFamily(Individual individual) throws Exception {
		final String xref = individual.getXref();
		String query = "";

		try {
			final String[] split = individual.getNames().get(0).getBasic().replace("'", "").split("/");
			final String given = split[0].trim();
			final String surname = split[1];
			final String sex = individual.getSex().getValue();
			final String phonName = fonkod.generateKey(surname);

			query = "INSERT INTO VEJBY.INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES ('" + xref + "', '"
					+ given + "', '" + surname + "', '" + sex + "', '" + phonName + "'	)";

			if (individual.getFamiliesWhereChild() == null) {
				updateIndividual(individual);
			}

			logger.fine(query);
			stmt.execute(query);
			individualCounter++;
		} catch (

		final SQLException e) {
			// Handle duplicates
			if (e.getSQLState().equals("23505")) {
				logger.info("SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				updateIndividual(individual);
			} else {
				throw new Exception(
						"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
			}
		}
	}

	/**
	 * For each family in the gedcom object
	 * <ul>
	 * <li>Get key</li>
	 * <li>Insert family</li>
	 * <li>Get husband key and insert individual for husband</li>
	 * <li>Get wife key and insert individual for wife</li>
	 * <li>Update family</li>
	 * <li>Insert all family evenst</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	private void parseAllFamilies() throws Exception {
		String key;
		Family family;
		Individual husband, wife;

		final Map<String, Family> families = gedcom.getFamilies();

		for (final Entry<String, Family> familyNode : families.entrySet()) {
			key = familyNode.getKey();
			insertFamily(key);

			family = familyNode.getValue();

			try {
				husband = family.getHusband().getIndividual();
				insertIndividual(husband);
				updateFamilyHusband(key, husband);
			} catch (final NullPointerException e) {
				logger.fine(e.getMessage());
			}

			try {
				wife = family.getWife().getIndividual();
				insertIndividual(wife);
				updateFamilyWife(key, wife);
			} catch (final NullPointerException e) {
				logger.fine(e.getMessage());
			}

			final List<IndividualReference> children = family.getChildren();

			if (children != null) {
				for (final IndividualReference individualReference : children) {
					insertIndividualWithFamily(individualReference.getIndividual());
				}
			}

			insertFamilyEvent(family);
		}

		return;
	}

	/**
	 * For each individual in the gedcom object
	 * <ul>
	 * <li>Insert individual into Derby</li>
	 * <li>If it already exists the update it</li>
	 * <li>Insert all events for the individual</li>
	 * </ul>
	 *
	 * @throws Exception
	 *
	 */
	private void parseAllIndividuals() throws Exception {
		Individual individual;

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualNode : individuals.entrySet()) {
			individual = individualNode.getValue();
			insertIndividualWithFamily(individual);

			insertIndividualEvent(individual);
		}

	}

	/**
	 * Read a GEDCOM file using org.gedcomj package
	 *
	 * @param filename
	 * @return
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private void readGedcom(String filename) throws IOException, GedcomParserException {
		final GedcomParser gp = new GedcomParser();
		gp.load(filename);
		gedcom = gp.getGedcom();
	}

	/**
	 * UPDATE VEJBY.FAMILY SET HUSBAND = '' WHERE ID = ''
	 *
	 * @param husband
	 * @throws Exception
	 * @throws SQLException
	 */
	private void updateFamilyHusband(String ID, Individual husband) throws Exception {
		final String query = "UPDATE VEJBY.FAMILY SET HUSBAND = '" + husband.getXref() + "' WHERE ID = '" + ID + "'";
		try {
			logger.fine(query);
			stmt.execute(query);
		} catch (final SQLException e) {
			throw new Exception(
					"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
		}

	}

	/**
	 * UPDATE VEJBY.FAMILY SET WIFE = '' WHERE ID = ''
	 *
	 * @param husband
	 * @param wife
	 * @throws Exception
	 */
	private void updateFamilyWife(String ID, Individual wife) throws Exception {
		final String query = "UPDATE VEJBY.FAMILY SET WIFE = '" + wife.getXref() + "' WHERE ID = '" + ID + "'";
		try {
			logger.fine(query);
			stmt.execute(query);
		} catch (final SQLException e) {
			throw new Exception(
					"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);

		}

	}

	/**
	 * Update an individual by adding FAMC
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void updateIndividual(Individual individual) throws Exception {
		final List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();

		if (familiesWhereChild != null) {
			final String query = "UPDATE VEJBY.INDIVIDUAL SET FAMC = '"
					+ individual.getFamiliesWhereChild().get(0).getFamily().getXref() + "' WHERE ID = '"
					+ individual.getXref() + "'";
			try {
				stmt.execute(query);
				logger.fine(query);
			} catch (final SQLException e) {
				// Handle family not yet inserted
				if (e.getSQLState().equals("23503")) {
					logger.fine(
							"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				} else {
					throw new Exception(
							"SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState() + ", " + query);
				}

			}
		}

	}
}
