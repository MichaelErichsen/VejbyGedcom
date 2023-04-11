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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.StringWithCustomFacts;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

import net.myerichsen.gedcom.db.models.IndividualModel;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * Read a GEDCOM and load data into a Derby database to use for analysis.
 *
 * @author Michael Erichsen
 * @version 10. apr. 2023
 */
public class DBLoader {
	// TODO " + props.getProperty("vejbySchema") + "
	// TODO Return message string
	/**
	 * Static constants and variables
	 */
	private static final String DELETE_EVENT = "DELETE FROM VEJBY.EVENT";
	private static final String DELETE_INDIVIDUAL = "DELETE FROM VEJBY.INDIVIDUAL";
	private static final String DELETE_FAMILY = "DELETE FROM VEJBY.FAMILY";
	private static final String DELETE_PARENTS = "DELETE FROM VEJBY.PARENTS";

	private static final String INSERT_INDIVIDUAL_EVENT = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, INDIVIDUAL, "
			+ "FAMILY, PLACE, NOTE, SOURCEDETAIL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_INDIVIDUAL = "INSERT INTO VEJBY.INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY_EVENT = "INSERT INTO VEJBY.EVENT (TYPE, SUBTYPE, DATE, FAMILY, PLACE, "
			+ "NOTE, SOURCEDETAIL, INDIVIDUAL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY = "INSERT INTO VEJBY.FAMILY (ID, HUSBAND, WIFE) VALUES (?, NULL, NULL)";
	private static final String INSERT_PARENTS = "INSERT INTO VEJBY.PARENTS (INDIVIDUALKEY, BIRTHYEAR, NAME, "
			+ "PARENTS, FATHERPHONETIC, MOTHERPHONETIC, PLACE) VALUES(?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_INDIVIDUAL_BDP = "UPDATE VEJBY.INDIVIDUAL SET BIRTHDATE = ?, "
			+ "BIRTHPLACE = ?, DEATHDATE = ?, DEATHPLACE = ? WHERE ID = ?";
	private static final String UPDATE_INDIVIDUAL_FAMC = "UPDATE VEJBY.INDIVIDUAL SET FAMC = ?, PARENTS = ? WHERE ID = ?";
	private static final String UPDATE_INDIVIDUAL_PARENTS = "UPDATE VEJBY.INDIVIDUAL SET PARENTS = ? WHERE ID = ?";
	private static final String UPDATE_FAMILY_WIFE = "UPDATE VEJBY.FAMILY SET WIFE=? WHERE ID = ?";
	private static final String UPDATE_FAMILY_HUSBAND = "UPDATE VEJBY.FAMILY SET HUSBAND = ? WHERE ID = ?";

	private static PreparedStatement psINSERT_INDIVIDUAL_EVENT;
	private static PreparedStatement psINSERT_INDIVIDUAL;
	private static PreparedStatement psINSERT_FAMILY_EVENT;
	private static PreparedStatement psINSERT_FAMILY;
	private static PreparedStatement psINSERT_PARENTS;

	private static PreparedStatement psUPDATE_INDIVIDUAL_BDP;
	private static PreparedStatement psUPDATE_INDIVIDUAL_FAMC;
	private static PreparedStatement psUPDATE_INDIVIDUAL_PARENTS;
	private static PreparedStatement psUPDATE_FAMILY_WIFE;
	private static PreparedStatement psUPDATE_FAMILY_HUSBAND;

	private static Logger logger;
	private static Fonkod fonkod = new Fonkod();
	private static Gedcom gedcom;
	private static int familyCounter = 0;
	private static int individualCounter = 0;
	private static int eventCounter = 0;
	private static int parentsCounter = 0;

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

		logger = Logger.getLogger("ArchiveSearcher");

		final DBLoader ir = new DBLoader();

		try {
			ir.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	private int birthYear;

	private String sted;

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
		statement = conn.prepareStatement(DELETE_FAMILY);
		statement.execute();
		statement = conn.prepareStatement(DELETE_PARENTS);
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
		updateBirthDeathData(conn);

		logger.info("Parsing parents");
		parseParents();

		conn.close();

		logger.info("Program ended.\n" + familyCounter + " families inserted.\n" + individualCounter
				+ " individuals inserted.\n" + eventCounter + " events inserted\n" + parentsCounter
				+ " parent pairs inserted");
	}

	/**
	 * Extract birthYear from date string
	 *
	 * @param dateString
	 * @return
	 */
	private int extractBirthYear(String dateString) {
		String year = dateString;
		final Pattern pattern = Pattern.compile("\\d{4}");
		final Matcher m = pattern.matcher(dateString);

		if (m.find()) {
			year = m.group(0);
		}

		return Integer.parseInt(year);

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
	 * Get parents from christening source citation
	 *
	 * @param value
	 * @return
	 */
	private String getParentsFromSource(Individual value) {
		try {
			final IndividualEvent christening = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			birthYear = extractBirthYear(christening.getDate().getValue());
			sted = christening.getPlace().getPlaceName().toString();
			final CitationWithSource citation = (CitationWithSource) christening.getCitations().get(0);
			return citation.getWhereInSource().toString();
		} catch (final Exception e) {
		}

		return "";
	}

	/**
	 * Insert an empty family into Derby
	 * <p>
	 * INSERT INTO VEJBY.FAMILY (ID, HUSBAND, WIFE) VALUES (?, NULL, NULL)
	 *
	 * @param key
	 * @throws SQLException
	 */
	private void insertEmptyFamily(String key) throws SQLException {
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
					final StringBuilder sb2 = new StringBuilder();

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

			updateIndividualFamc(individual);
			updateIndividualParents(individual);
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
						psINSERT_INDIVIDUAL_EVENT.setString(8, whereInSource.getValue());
					} else {
						psINSERT_INDIVIDUAL_EVENT.setString(8, "NULL");
					}
				} else {
					final StringBuilder sb2 = new StringBuilder();

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					psINSERT_INDIVIDUAL_EVENT.setString(8,
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
			psINSERT_INDIVIDUAL.execute();
			individualCounter++;
		} catch (final SQLException e) {
			// Handle duplicates
			if (!e.getSQLState().equals("23505")) {
				throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
			}

			updateIndividualFamc(individual);
			updateIndividualParents(individual);
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
			insertEmptyFamily(key);

			family = familyNode.getValue();

			if (family.getHusband() != null) {
				husband = family.getHusband().getIndividual();
				insertIndividual(husband);
				updateFamilyHusband(key, husband);
			}

			if (family.getWife() != null) {
				wife = family.getWife().getIndividual();
				insertIndividual(wife);
				updateFamilyWife(key, wife);
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
	 * INSERT INTO VEJBY.PARENTS (INDIVIDUALKEY, BIRTHDATE, NAME, PARENTS,
	 * FATHERPHONETIC, MOTHERPHONETIC, PLACE) VALUES(?, ?, ?, ?, ?, ?, ?)
	 *
	 * @throws SQLException
	 *
	 */
	private void parseParents() throws SQLException {
		StringBuilder sb;
		String parents = "";
		IndividualEvent christening;

		// For each individual
		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualMapEntry : individuals.entrySet()) {
			birthYear = 0;
			sted = "";
			parents = "";

			final Individual individual = individualMapEntry.getValue();

			sb = new StringBuilder();

			// Get all families where the individual is a child
			try {
				final List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild(false);

				for (final FamilyChild familyWhereChild : familiesWhereChild) {
					final Family family = familyWhereChild.getFamily();

					// Get the father
					boolean husb = false;

					try {
						sb.append(family.getHusband().getIndividual().getNames().get(0));
						husb = true;
					} catch (final Exception e) {
					}

					// Get the mother
					try {
						final PersonalName w = family.getWife().getIndividual().getNames().get(0);

						if (husb) {
							sb.append(" og ");
						}

						sb.append(w);
					} catch (final Exception e) {
					}
				}
			} catch (final Exception e) {
			}

			if (sb.length() > 0) {
				parents = sb.toString().replace("/", "");

				try {
					christening = individual.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
					birthYear = extractBirthYear(christening.getDate().getValue());
					sted = christening.getPlace().getPlaceName().toString();
				} catch (final Exception e) {
				}
			} else {
				parents = getParentsFromSource(individual);
			}

			final String[] splitParents = splitParents(parents);
			String a = "";
			String b = "";

			psINSERT_PARENTS.setString(1, individualMapEntry.getKey().replace("@I", "").replace("@", ""));
			psINSERT_PARENTS.setInt(2, birthYear);
			psINSERT_PARENTS.setString(3, individual.getFormattedName().replace("/", ""));
			psINSERT_PARENTS.setString(4, parents.replace("  ", " "));

			try {
				a = fonkod.generateKey(splitParents[0]);
				a = a.length() > 64 ? a.substring(0, 63) : a;
			} catch (final Exception e) {
			}

			psINSERT_PARENTS.setString(5, a);

			try {
				b = fonkod.generateKey(splitParents[1]);
				b = b.length() > 64 ? b.substring(0, 63) : b;
			} catch (final Exception e) {
			}

			psINSERT_PARENTS.setString(6, b);
			psINSERT_PARENTS.setString(7, sted);
			psINSERT_PARENTS.executeUpdate();
			parentsCounter++;
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
		psUPDATE_INDIVIDUAL_PARENTS = conn.prepareStatement(UPDATE_INDIVIDUAL_PARENTS);
		psUPDATE_FAMILY_WIFE = conn.prepareStatement(UPDATE_FAMILY_WIFE);
		psUPDATE_FAMILY_HUSBAND = conn.prepareStatement(UPDATE_FAMILY_HUSBAND);
		psINSERT_INDIVIDUAL = conn.prepareStatement(INSERT_INDIVIDUAL);
		psINSERT_FAMILY_EVENT = conn.prepareStatement(INSERT_FAMILY_EVENT);
		psINSERT_FAMILY = conn.prepareStatement(INSERT_FAMILY);
		psINSERT_PARENTS = conn.prepareStatement(INSERT_PARENTS);
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
	 * @param parents2
	 * @return
	 */
	private String[] splitParents(String parents2) {
		if (parents2 == null || parents2.length() == 0) {
			return new String[] { "", "" };
		}

		String s = parents2.replaceAll("\\d", "").replace(".", "").toLowerCase();
		s = s.replace(", f.", "");
		String[] sa = s.split(",");
		final String[] words = sa[0].split(" ");

		final String[] filter = new String[] { "af", "bager", "gamle", "gmd", "i", "inds", "junior", "kirkesanger",
				"pige", "pigen", "portner", "proprietær", "sadelmager", "skolelærer", "skovfoged", "slagter", "smed",
				"smedesvend", "snedker", "søn", "ugift", "ugifte", "unge", "ungkarl", "uægte", "år" };

		final StringBuilder sb = new StringBuilder();

		for (final String word : words) {
			for (final String element : filter) {
				if (element.equals(word)) {
					break;
				}
			}
			sb.append(word + " ");
		}

		s = sb.toString();

		sa = s.split(" og ");

		return sa;
	}

	/**
	 * Add birth and death dates and places
	 *
	 * @param conn
	 *
	 * @throws SQLException
	 */
	private void updateBirthDeathData(Connection conn) throws SQLException {
		// Read all individuals into a list
		final List<IndividualModel> ldbi = IndividualModel.loadFromDB(conn);

		// Update all individual records in Derby
		updateIndividualsBirthDeath(ldbi);
	}

	/**
	 * UPDATE VEJBY.FAMILY SET HUSBAND = ? WHERE ID = ?
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
	 * <p>
	 * UPDATE VEJBY.INDIVIDUAL SET FAMC = ? WHERE ID = ?
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void updateIndividualFamc(Individual individual) throws Exception {
		final List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();

		String father;
		String mother;
		String parents = "";

		if (familiesWhereChild != null) {
			try {
				father = familiesWhereChild.get(0).getFamily().getHusband().getIndividual().getFormattedName()
						.replace("/", "");
			} catch (final Exception e1) {
				father = "";
			}
			try {
				mother = familiesWhereChild.get(0).getFamily().getWife().getIndividual().getFormattedName().replace("/",
						"");
			} catch (final Exception e1) {
				mother = "";
			}

			if (father.length() > 0 && mother.length() > 0) {
				parents = father + " og " + mother;
				parentsCounter++;
			} else {
				parents = (father + mother).trim();
			}

			logger.fine("Parents: " + parents);
			psUPDATE_INDIVIDUAL_FAMC.setString(1, individual.getFamiliesWhereChild().get(0).getFamily().getXref());
			psUPDATE_INDIVIDUAL_FAMC.setString(2, parents);
			psUPDATE_INDIVIDUAL_FAMC.setString(3, individual.getXref());

			try {
				psUPDATE_INDIVIDUAL_FAMC.execute();
			} catch (final SQLException e) {
				logger.fine("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());

				// Handle family not yet inserted
				if (!e.getSQLState().equals("23503")) {
					throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
				}
			}
		}
	}

	/**
	 * Update an individual by adding parents
	 * <p>
	 * UPDATE VEJBY.INDIVIDUAL SET PARENTS = ? WHERE ID = ?
	 *
	 * @param individual
	 * @throws Exception
	 */
	private void updateIndividualParents(Individual individual) throws Exception {
		final List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();

		if (familiesWhereChild == null) {
			try {
				final List<IndividualEvent> eventsOfType = individual.getEventsOfType(IndividualEventType.CHRISTENING);
				final IndividualEvent event = eventsOfType.get(0);
				final List<AbstractCitation> citations = event.getCitations();
				final CitationWithSource citation = (CitationWithSource) citations.get(0);
				final StringWithCustomFacts whereInSource = citation.getWhereInSource();
				String parents = whereInSource.toString();
				parents = parents.length() > 256 ? parents.substring(0, 255) : parents;
				logger.fine("Parents: " + parents);

				psUPDATE_INDIVIDUAL_PARENTS.setString(1, parents);
				psUPDATE_INDIVIDUAL_PARENTS.setString(2, individual.getXref());
				psUPDATE_INDIVIDUAL_PARENTS.execute();
			} catch (final Exception e) {
				logger.fine(e.getMessage());
			}
		}
	}

	/**
	 * Update birth and death data for all individuals
	 * <p>
	 * UPDATE VEJBY.INDIVIDUAL SET BIRTHDATE = ?, BIRTHPLACE = ?, DEATHDATE = ?,
	 * DEATHPLACE = ? WHERE ID = ?
	 *
	 * @param ldbi
	 * @throws SQLException
	 */
	private void updateIndividualsBirthDeath(List<IndividualModel> ldbi) throws SQLException {
		Date bd;
		Date dd;

		for (final IndividualModel dbi : ldbi) {
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
			psUPDATE_INDIVIDUAL_BDP.setString(5, dbi.getId());
			psUPDATE_INDIVIDUAL_BDP.executeUpdate();
		}
	}
}
