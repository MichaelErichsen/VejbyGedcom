package net.myerichsen.gedcom.db.loaders;

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
import java.util.logging.Logger;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.AbstractCitation;
import org.gedcom4j.model.CitationWithSource;
import org.gedcom4j.model.CustomFact;
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

import net.myerichsen.gedcom.db.Fonkod;
import net.myerichsen.gedcom.db.models.DBIndividual;

/**
 * Read a GEDCOM and load data into a Derby database to use for analysis.
 *
 * @author Michael Erichsen
 * @version 26. mar. 2023
 */
public class DBLoader {
	private static final String INSERT_INDIVIDUAL_EVENT_START = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, INDIVIDUAL, "
			+ "FAMILY, PLACE, NOTE, SOURCEDETAIL) VALUES('";
	private static final String UPDATE_INDIVIDUAL_FAMC = "UPDATE VEJBY.INDIVIDUAL SET FAMC = '%s' WHERE ID = '%s'";
	private static final String DELETE_EVENT = "DELETE FROM VEJBY.EVENT";
	private static final String DELETE_INDIVIDUAL = "DELETE FROM VEJBY.INDIVIDUAL";
	private static final String DELETE_FAMILY = "DELETE FROM VEJBY.FAMILY";
	private static final String UPDATE_FAMILY_WIFE = "UPDATE VEJBY.FAMILY SET WIFE='%s'	WHERE ID = '%s'";
	private static final String UPDATE_FAMILY_HUSBAND = "UPDATE VEJBY.FAMILY SET HUSBAND = '%s' WHERE ID = '%s'";
	private static final String INSERT_INDIVIDUAL = "INSERT INTO VEJBY.INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES ('%s', '%s', '%s', '%s', '%s')";
	private static final String INSERT_FAMILY_EVENT_START = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, FAMILY, PLACE, "
			+ "NOTE, SOURCEDETAIL, INDIVIDUAL) VALUES('";
	private static final String INSERT_FAMILY = "INSERT INTO VEJBY.FAMILY (ID, HUSBAND, WIFE) VALUES ('%s', NULL, NULL)";
	private static Logger logger;
	private static Fonkod fonkod = new Fonkod();
	private static Gedcom gedcom;
	private static int familyCounter = 0;
	private static int individualCounter = 0;
	private static int eventCounter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			logger.info("Usage: DBLoader gedcomfile derbydatabasepath");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLoader");

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
	 * Add birth and death dates and places
	 *
	 * @throws SQLException
	 */
	private void updateBirthDeathParentsData() throws SQLException {
		// Read all individuals into a list
		final List<DBIndividual> ldbi = DBIndividual.loadFromDB(stmt);

		// Update all individual records in Derby
		updateIndividualsBDP(stmt, ldbi);
	}

	/**
	 * @param family
	 * @param familyEvent
	 * @return
	 */
	private StringBuilder buildFamilyEventQuery(Family family, final FamilyEvent familyEvent) {
		StringBuilder sb;
		StringWithCustomFacts subtype;
		StringWithCustomFacts date;
		List<NoteStructure> noteStructures;
		List<AbstractCitation> citations;
		CitationWithSource cfs;
		StringWithCustomFacts whereInSource;
		List<String> lines;
		StringBuilder lineBuffer;
		Place place;
		List<CustomFact> customFacts;
		StringBuffer sb2;
		sb = new StringBuilder(INSERT_FAMILY_EVENT_START);
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
			sb.append("NULL, ");
		} else {
			lines = noteStructures.get(0).getLines();
			lineBuffer = new StringBuilder();

			for (final String string : lines) {
				lineBuffer.append(string + " ");
			}
			sb.append("'" + lineBuffer.toString().replace("'", "¤") + "', ");
		}

		citations = familyEvent.getCitations();

		if (citations == null) {
			sb.append("NULL, '");
		} else {
			cfs = (CitationWithSource) citations.get(0);
			whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				sb.append("NULL, '");
			} else {
				customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						sb.append("'" + whereInSource.getValue() + "', '");
					} else {
						sb.append("NULL, '");
					}
				} else {
					sb2 = new StringBuffer();

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					sb.append("'" + (sb2.length() > 256 ? sb2.toString().substring(0, 255).replace("'", "¤")
							: sb2.toString().replace("'", "¤")) + "', '");
				}
			}
		}

		return sb;
	}

	/**
	 * @param individual
	 * @param individualEvent
	 * @return
	 */
	private StringBuilder buildIndividualEventQuery(Individual individual, final IndividualEvent individualEvent) {
		StringBuilder sb;
		StringWithCustomFacts subtype;
		StringWithCustomFacts date;
		Place place;
		List<NoteStructure> noteStructures;
		List<AbstractCitation> citations;
		CitationWithSource cfs;
		StringWithCustomFacts whereInSource;
		List<String> lines;
		StringBuilder lineBuffer;
		List<CustomFact> customFacts;
		StringBuffer sb2;
		sb = new StringBuilder(INSERT_INDIVIDUAL_EVENT_START);
		sb.append(individualEvent.getType());
		subtype = individualEvent.getSubType();

		if (subtype == null) {
			sb.append("', NULL, ");
		} else {
			if (subtype.getValue().equals("Faeste1")) {
				sb.append("', '" + "Lægdsrulle" + "', ");
			} else {
				sb.append("', '" + subtype.getValue() + "', ");
			}
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
			sb.append("NULL, ");
		} else {
			lines = noteStructures.get(0).getLines();
			lineBuffer = new StringBuilder();

			for (final String string : lines) {
				lineBuffer.append(string + " ");
			}
			sb.append("'" + lineBuffer.toString().replace("'", "¤") + "', ");
		}

		citations = individualEvent.getCitations();

		if (citations == null) {
			sb.append("NULL)");
		} else {
			cfs = (CitationWithSource) citations.get(0);
			whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				sb.append("NULL)");
			} else {
				customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						sb.append("'" + whereInSource.getValue() + "')");
					} else {
						sb.append("NULL)");
					}
				} else {
					sb2 = new StringBuffer();

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue().replaceAll("\\s{2,}", " ").trim());
					}

					sb.append("'"
							+ (sb2.length() > 256 ? sb2.toString().substring(0, 255).replace("'", "¤") : sb2.toString())
									.replace("'", "¤")
							+ "')");
				}
			}
		}

		return sb;

	}

	/**
	 * Delete all rows from all tables
	 *
	 * @throws SQLException
	 *
	 */
	private void clearTables() throws SQLException {
		String query = DELETE_FAMILY;
		stmt.execute(query);
		query = DELETE_INDIVIDUAL;
		stmt.execute(query);
		query = DELETE_EVENT;
		stmt.execute(query);
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param args
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		stmt = conn.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		connectToDB(args);

		logger.info("Reading " + args[0]);
		readGedcom(args[0]);

		logger.info("Clearing tables");
		clearTables();

		logger.info("Parsing families");
		parseAllFamilies();

		logger.info("Parsing individuals");
		parseAllIndividuals();

		logger.info("Add birth and death data and parents");
		updateBirthDeathParentsData();

		stmt.close();

		logger.info("Program ended.\n" + familyCounter + " families inserted.\n" + individualCounter
				+ " individuals inserted.\n" + eventCounter + " events inserted");
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
		final String query = String.format(INSERT_FAMILY, key);
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
		StringBuilder sb;
		String query;
		final List<FamilyEvent> events = family.getEvents();

		if (events == null) {
			return;
		}

		for (final FamilyEvent familyEvent : events) {
			sb = buildFamilyEventQuery(family, familyEvent);

			if (family.getHusband() != null) {
				query = sb.toString() + family.getHusband().getIndividual().getXref() + "')";

				try {
					logger.fine(query);
					stmt.execute(query);
					eventCounter++;
				} catch (final SQLException e) {
					throw new Exception(
							"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
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
							"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
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
			query = String.format(INSERT_INDIVIDUAL, xref, given, surname, sex, phonName);
			logger.fine(query);
			stmt.execute(query);
			individualCounter++;
		} catch (final SQLException e) {
			// Handle duplicates
			if (!e.getSQLState().equals("23505")) {
				throw new Exception(
						"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
			}
			logger.fine("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
			updateIndividual(individual);
		}
	}

	/**
	 * Insert an individual event into Derby
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void insertIndividualEvent(Individual individual) throws Exception {
		StringBuilder sb;
		String query;
		final List<IndividualEvent> events = individual.getEvents();

		if (events == null) {
			return;
		}

		for (final IndividualEvent individualEvent : events) {
			sb = buildIndividualEventQuery(individual, individualEvent);

			query = sb.toString();

			try {
				logger.fine(query);
				stmt.execute(query);
				eventCounter++;
			} catch (final SQLException e) {
				throw new Exception(
						"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
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
			final String phonName = fonkod.generateKey(given) + " " + fonkod.generateKey(surname);

			query = String.format(INSERT_INDIVIDUAL, xref, given, surname, sex, phonName);
			logger.fine(query);

			if (individual.getFamiliesWhereChild() == null) {
				updateIndividual(individual);
			}

			logger.fine(query);
			stmt.execute(query);
			individualCounter++;
		} catch (

		final SQLException e) {
			// Handle duplicates
			if (!e.getSQLState().equals("23505")) {
				throw new Exception(
						"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
			}

			updateIndividual(individual);
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
		final String query = String.format(UPDATE_FAMILY_HUSBAND, husband.getXref(), ID);

		try {
			logger.fine(query);
			stmt.execute(query);
		} catch (final SQLException e) {
			throw new Exception(
					"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
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
		final String query = String.format(UPDATE_FAMILY_WIFE, wife.getXref(), ID);

		try {
			logger.fine(query);
			stmt.execute(query);
		} catch (final SQLException e) {
			throw new Exception(
					"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);

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
			final String query = String.format(UPDATE_INDIVIDUAL_FAMC,
					individual.getFamiliesWhereChild().get(0).getFamily().getXref(), individual.getXref());

			try {
				stmt.execute(query);
				logger.fine(query);
			} catch (final SQLException e) {
				// Handle family not yet inserted
				if (!e.getSQLState().equals("23503")) {
					throw new Exception(
							"sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);
				}
				logger.fine("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState() + ", " + query);

			}
		}

	}

	/**
	 * Update birth and death data and parents for all individuals
	 *
	 * @param statement
	 * @param ldbi
	 * @throws SQLException
	 */
	private void updateIndividualsBDP(Statement statement, List<DBIndividual> ldbi) throws SQLException {
		final String UPDATE_INDIVIDUAL_BDP = "UPDATE VEJBY.INDIVIDUAL SET BIRTHYEAR = %d, "
				+ "BIRTHPLACE = '%s', DEATHYEAR = %d, DEATHPLACE = '%s', PARENTS = '%s' WHERE ID = '%s'";
		String query;
		String parents;

		for (final DBIndividual dbi : ldbi) {
			parents = dbi.getParents();

			if (parents == null) {
				parents = "";
			} else {
				parents = (parents.length() > 256 ? parents.substring(0, 255) : parents);
			}

			query = String.format(UPDATE_INDIVIDUAL_BDP, dbi.getBirthYear(), dbi.getBirthPlace(), dbi.getDeathYear(),
					dbi.getDeathPlace(), parents, dbi.getId());

			statement.executeUpdate(query);
		}

	}
}
