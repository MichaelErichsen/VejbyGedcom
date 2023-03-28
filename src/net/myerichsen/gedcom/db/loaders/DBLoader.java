package net.myerichsen.gedcom.db.loaders;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
 * @version 28. mar. 2023
 */
public class DBLoader {

	// TODO Parents in relocations from christening missing

	/**
	 * Static constants and variables
	 */
	private static final String UPDATE_INDIVIDUAL_BDP = "UPDATE VEJBY.INDIVIDUAL SET BIRTHDATE = ?, "
			+ "BIRTHPLACE = ?, DEATHDATE = ?, DEATHPLACE = ?, PARENTS = ? WHERE ID = ?";
	private static final String INSERT_INDIVIDUAL_EVENT = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, INDIVIDUAL, "
			+ "FAMILY, PLACE, NOTE, SOURCEDETAIL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_INDIVIDUAL_FAMC = "UPDATE VEJBY.INDIVIDUAL SET FAMC = ? WHERE ID = ?";
	private static final String DELETE_EVENT = "DELETE FROM VEJBY.EVENT";
	private static final String DELETE_INDIVIDUAL = "DELETE FROM VEJBY.INDIVIDUAL";
	private static final String DELETE_FAMILY = "DELETE FROM VEJBY.FAMILY";
	private static final String UPDATE_FAMILY_WIFE = "UPDATE VEJBY.FAMILY SET WIFE=? WHERE ID = ?";
	private static final String UPDATE_FAMILY_HUSBAND = "UPDATE VEJBY.FAMILY SET HUSBAND = ? WHERE ID = ?";
	private static final String INSERT_INDIVIDUAL = "INSERT INTO VEJBY.INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY_EVENT = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, FAMILY, PLACE, "
			+ "NOTE, SOURCEDETAIL, INDIVIDUAL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY = "INSERT INTO VEJBY.FAMILY (ID, HUSBAND, WIFE) VALUES (?, NULL, NULL)";

	private static PreparedStatement psUPDATE_INDIVIDUAL_BDP;
	private static PreparedStatement psINSERT_INDIVIDUAL_EVENT;
	private static PreparedStatement psUPDATE_INDIVIDUAL_FAMC;
	private static PreparedStatement psUPDATE_FAMILY_WIFE;
	private static PreparedStatement psUPDATE_FAMILY_HUSBAND;
	private static PreparedStatement psINSERT_INDIVIDUAL;
	private static PreparedStatement psINSERT_FAMILY_EVENT;
	private static PreparedStatement psINSERT_FAMILY;
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

	/**
	 * Delete all rows from all tables
	 *
	 * @param conn
	 *
	 * @throws SQLException
	 *
	 */
	private void clearTables(Connection conn) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(DELETE_FAMILY);
		statement.execute();
		statement = conn.prepareStatement(DELETE_INDIVIDUAL);
		statement.execute();
		statement = conn.prepareStatement(DELETE_EVENT);
		statement.execute();
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Connection connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		return conn;
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		final Connection conn = connectToDB(args);

		logger.info("Preparing SQL statements");
		prepareStatements(conn);

		logger.info("Reading " + args[0]);
		readGedcom(args[0]);

		logger.info("Clearing tables");
		clearTables(conn);

		logger.info("Parsing families");
		parseAllFamilies();

		logger.info("Parsing individuals");
		parseAllIndividuals();

		logger.info("Add birth and death data and parents");
		updateBirthDeathParentsData(conn);

		conn.close();

		logger.info("Program ended.\n" + familyCounter + " families inserted.\n" + individualCounter
				+ " individuals inserted.\n" + eventCounter + " events inserted");
	}

	/**
	 * Insert a family event into Derby for husband and wife
	 *
	 * @param family
	 * @throws SQLException
	 */
	private void findFamilyEvents(Family family) throws SQLException {
		final List<FamilyEvent> events = family.getEvents();

		if (events == null) {
			return;
		}

		for (final FamilyEvent familyEvent : events) {
			insertFamilyEvents(family, familyEvent);
		}
	}

	/**
	 * Insert an individual event into Derby
	 *
	 * @param individual
	 * @throws SQLException
	 */
	private void findIndividualEvents(Individual individual) throws SQLException {
		final List<IndividualEvent> events = individual.getEvents();

		if (events == null) {
			return;
		}

		for (final IndividualEvent individualEvent : events) {
			insertIndividualEvent(individual, individualEvent);
			eventCounter++;
		}
	}

	/**
	 * Format a GEDCOM date into Derby format
	 *
	 * @param swcf
	 * @return
	 */
	private Date formatDate(StringWithCustomFacts swcf) {
		String date = swcf.getValue();
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
		return Date.valueOf(outDate);
	}

	/**
	 * Insert a family into Derby
	 *
	 * @param key
	 * @throws SQLException
	 */
	private void insertFamily(String key) throws SQLException {
		psINSERT_FAMILY.setString(1, key);
		psINSERT_FAMILY.execute();
		familyCounter++;
	}

	/**
	 * Insert family events
	 *
	 * @param family
	 * @param familyEvent
	 * @return
	 * @throws SQLException
	 */
	private void insertFamilyEvents(Family family, final FamilyEvent familyEvent) throws SQLException {
		psINSERT_FAMILY_EVENT.setString(1, familyEvent.getType().toString());

		final StringWithCustomFacts subtype = familyEvent.getSubType();
		if (subtype == null) {
			psINSERT_FAMILY_EVENT.setString(2, "NULL");
		} else {
			psINSERT_FAMILY_EVENT.setString(2, subtype.getValue());
		}

		final StringWithCustomFacts date = familyEvent.getDate();
		if (date == null) {
			psINSERT_FAMILY_EVENT.setObject(3, null);
		} else {
			psINSERT_FAMILY_EVENT.setDate(3, formatDate(date));
		}

		psINSERT_FAMILY_EVENT.setString(4, family.getXref());

		final Place place = familyEvent.getPlace();
		if (place == null) {
			psINSERT_FAMILY_EVENT.setString(5, "NULL");
		} else {
			psINSERT_FAMILY_EVENT.setString(5, place.getPlaceName().replace("'", ""));
		}

		final List<NoteStructure> noteStructures = familyEvent.getNoteStructures();
		if (noteStructures == null) {
			psINSERT_FAMILY_EVENT.setString(6, "NULL");
		} else {
			final List<String> lines = noteStructures.get(0).getLines();
			final StringBuilder lineBuffer = new StringBuilder();

			for (final String string : lines) {
				lineBuffer.append(string + " ");
			}
			psINSERT_FAMILY_EVENT.setString(6, lineBuffer.toString().replace("'", "¤"));
		}

		final List<AbstractCitation> citations = familyEvent.getCitations();
		if (citations == null) {
			psINSERT_FAMILY_EVENT.setString(7, "NULL");
		} else {
			final CitationWithSource cfs = (CitationWithSource) citations.get(0);
			final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				psINSERT_FAMILY_EVENT.setString(7, "NULL");
			} else {
				final List<CustomFact> customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						psINSERT_FAMILY_EVENT.setString(7, whereInSource.getValue());
					} else {
						psINSERT_FAMILY_EVENT.setString(7, "NULL");
					}
				} else {
					final StringBuffer sb2 = new StringBuffer();

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					psINSERT_FAMILY_EVENT.setString(7,
							(sb2.length() > 256 ? sb2.toString().substring(0, 255).replace("'", "¤")
									: sb2.toString().replace("'", "¤")) + "', '");
				}
			}
		}

		if (family.getHusband() != null) {
			psINSERT_FAMILY_EVENT.setString(8, family.getHusband().getIndividual().getXref());
			psINSERT_FAMILY_EVENT.executeUpdate();
			eventCounter++;
		}

		if (family.getWife() != null) {
			psINSERT_FAMILY_EVENT.setString(8, family.getWife().getIndividual().getXref());
			psINSERT_FAMILY_EVENT.executeUpdate();
			eventCounter++;
		}
	}

	/**
	 * Insert an individual into Derby
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void insertIndividual(Individual individual) throws Exception {
		try {
			final String[] split = individual.getNames().get(0).getBasic().replace("'", "").split("/");
			final String given = split[0].trim();
			final String surname = split[1];

			psINSERT_INDIVIDUAL.setString(1, individual.getXref());
			psINSERT_INDIVIDUAL.setString(2, given);
			psINSERT_INDIVIDUAL.setString(3, surname);
			psINSERT_INDIVIDUAL.setString(4, individual.getSex().getValue());
			psINSERT_INDIVIDUAL.setString(5, fonkod.generateKey(given) + " " + fonkod.generateKey(surname));
			psINSERT_INDIVIDUAL.execute();
			individualCounter++;
		} catch (final SQLException e) {
			// Handle duplicates
			if (!e.getSQLState().equals("23505")) {
				throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
			}
			logger.fine("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
			updateIndividual(individual);
		}
	}

	/**
	 * Insert an individual event
	 *
	 * @param individual
	 * @param individualEvent
	 * @return
	 * @throws SQLException
	 */
	private void insertIndividualEvent(Individual individual, final IndividualEvent individualEvent)
			throws SQLException {

		psINSERT_INDIVIDUAL_EVENT.setString(1, individualEvent.getType().toString());

		final StringWithCustomFacts subtype = individualEvent.getSubType();
		if (subtype == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(2, "NULL");
		} else {
			psINSERT_INDIVIDUAL_EVENT.setString(2, subtype.getValue());
		}

		final StringWithCustomFacts date = individualEvent.getDate();
		if (date == null) {
			psINSERT_INDIVIDUAL_EVENT.setObject(3, null);
		} else {
			psINSERT_INDIVIDUAL_EVENT.setDate(3, formatDate(date));
		}

		psINSERT_INDIVIDUAL_EVENT.setString(4, individual.getXref());

		psINSERT_INDIVIDUAL_EVENT.setString(5, "NULL");

		final Place place = individualEvent.getPlace();
		if (place == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(6, "NULL");
		} else {
			psINSERT_INDIVIDUAL_EVENT.setString(6, place.getPlaceName().replace("'", ""));
		}

		final List<NoteStructure> noteStructures = individualEvent.getNoteStructures();
		if (noteStructures == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(7, "NULL");
		} else {
			final List<String> lines = noteStructures.get(0).getLines();
			final StringBuilder lineBuffer = new StringBuilder();

			for (final String string : lines) {
				lineBuffer.append(string + " ");
			}
			psINSERT_INDIVIDUAL_EVENT.setString(7, lineBuffer.toString().replace("'", "¤"));
		}

		final List<AbstractCitation> citations = individualEvent.getCitations();
		if (citations == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(8, "NULL");
		} else {
			final CitationWithSource cfs = (CitationWithSource) citations.get(0);
			final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				psINSERT_INDIVIDUAL_EVENT.setString(8, "NULL");
			} else {
				final List<CustomFact> customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						psINSERT_INDIVIDUAL_EVENT.setString(7, whereInSource.getValue());
					} else {
						psINSERT_INDIVIDUAL_EVENT.setString(7, "NULL");
					}
				} else {
					final StringBuffer sb2 = new StringBuffer();

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					psINSERT_INDIVIDUAL_EVENT.setString(7,
							(sb2.length() > 256 ? sb2.toString().substring(0, 255).replace("'", "¤")
									: sb2.toString().replace("'", "¤")) + "', '");
				}
			}
		}

		psINSERT_INDIVIDUAL_EVENT.executeUpdate();
	}

	/**
	 * Insert an individual into Derby
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void insertIndividualWithFamily(Individual individual) throws Exception {
		try {
			final String[] split = individual.getNames().get(0).getBasic().replace("'", "").split("/");
			final String given = split[0].trim();
			final String surname = split[1];

			psINSERT_INDIVIDUAL.setString(1, individual.getXref());
			psINSERT_INDIVIDUAL.setString(2, given);
			psINSERT_INDIVIDUAL.setString(3, surname);
			psINSERT_INDIVIDUAL.setString(4, individual.getSex().getValue());
			psINSERT_INDIVIDUAL.setString(5, fonkod.generateKey(given) + " " + fonkod.generateKey(surname));

			if (individual.getFamiliesWhereChild() == null) {
				updateIndividual(individual);
			}

			psINSERT_INDIVIDUAL.execute();
			individualCounter++;
		} catch (final SQLException e) {
			// Handle duplicates
			if (!e.getSQLState().equals("23505")) {
				throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
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

			findFamilyEvents(family);
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
	 */
	private void parseAllIndividuals() throws Exception {
		Individual individual;

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualNode : individuals.entrySet()) {
			individual = individualNode.getValue();
			insertIndividualWithFamily(individual);
			findIndividualEvents(individual);
		}

	}

	/**
	 * @param conn
	 * @throws SQLException
	 */
	private void prepareStatements(Connection conn) throws SQLException {
		psUPDATE_INDIVIDUAL_BDP = conn.prepareStatement(UPDATE_INDIVIDUAL_BDP);
		psINSERT_INDIVIDUAL_EVENT = conn.prepareStatement(INSERT_INDIVIDUAL_EVENT);
		psUPDATE_INDIVIDUAL_FAMC = conn.prepareStatement(UPDATE_INDIVIDUAL_FAMC);
		psUPDATE_FAMILY_WIFE = conn.prepareStatement(UPDATE_FAMILY_WIFE);
		psUPDATE_FAMILY_HUSBAND = conn.prepareStatement(UPDATE_FAMILY_HUSBAND);
		psINSERT_INDIVIDUAL = conn.prepareStatement(INSERT_INDIVIDUAL);
		psINSERT_FAMILY_EVENT = conn.prepareStatement(INSERT_FAMILY_EVENT);
		psINSERT_FAMILY = conn.prepareStatement(INSERT_FAMILY);
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
	 * Add birth and death dates and places
	 *
	 * @param conn
	 *
	 * @throws SQLException
	 */
	private void updateBirthDeathParentsData(Connection conn) throws SQLException {
		// Read all individuals into a list
		final List<DBIndividual> ldbi = DBIndividual.loadFromDB(conn);

		// Update all individual records in Derby
		updateIndividualsBDP(ldbi);
	}

	/**
	 * UPDATE VEJBY.FAMILY SET HUSBAND = '' WHERE ID = ''
	 *
	 * @param husband
	 * @throws Exception
	 * @throws SQLException
	 */
	private void updateFamilyHusband(String ID, Individual husband) throws SQLException {
		psUPDATE_FAMILY_HUSBAND.setString(1, husband.getXref());
		psUPDATE_FAMILY_HUSBAND.setString(2, ID);
		psUPDATE_FAMILY_HUSBAND.executeUpdate();
	}

	/**
	 * UPDATE VEJBY.FAMILY SET WIFE = '' WHERE ID = ''
	 *
	 * @param statement
	 * @param ID
	 * @param wife
	 * @throws SQLException
	 */
	private void updateFamilyWife(String ID, Individual wife) throws SQLException {
		psUPDATE_FAMILY_WIFE.setString(1, wife.getXref());
		psUPDATE_FAMILY_WIFE.setString(2, ID);
		psUPDATE_FAMILY_WIFE.executeUpdate();
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
			psUPDATE_INDIVIDUAL_FAMC.setString(1, individual.getFamiliesWhereChild().get(0).getFamily().getXref());
			psUPDATE_INDIVIDUAL_FAMC.setString(2, individual.getXref());

			try {
				psUPDATE_INDIVIDUAL_FAMC.execute();
			} catch (final SQLException e) {
				// Handle family not yet inserted
				if (!e.getSQLState().equals("23503")) {
					throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
				}
				logger.fine("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());

			}
		}

	}

	/**
	 * Update birth and death data and parents for all individuals
	 *
	 * @param ldbi
	 * @throws SQLException
	 */
	private void updateIndividualsBDP(List<DBIndividual> ldbi) throws SQLException {
		Date bd;
		Date dd;
		String parents;

		for (final DBIndividual dbi : ldbi) {
			parents = dbi.getParents();

			if (parents == null) {
				parents = "";
			} else {
				parents = (parents.length() > 256 ? parents.substring(0, 255) : parents);
			}

			bd = dbi.getBirthDate();
			dd = dbi.getDeathDate();

			if (bd == null) {
				psUPDATE_INDIVIDUAL_BDP.setObject(1, null);
			} else {
				psUPDATE_INDIVIDUAL_BDP.setString(1, bd.toString());
			}

			psUPDATE_INDIVIDUAL_BDP.setString(2, dbi.getBirthPlace());

			if (dd == null) {
				psUPDATE_INDIVIDUAL_BDP.setObject(3, null);
			} else {
				psUPDATE_INDIVIDUAL_BDP.setString(3, dd.toString());
			}

			psUPDATE_INDIVIDUAL_BDP.setString(4, dbi.getDeathPlace());
			psUPDATE_INDIVIDUAL_BDP.setString(5, parents);
			psUPDATE_INDIVIDUAL_BDP.setString(6, dbi.getId());
			psUPDATE_INDIVIDUAL_BDP.executeUpdate();
		}
	}
}
