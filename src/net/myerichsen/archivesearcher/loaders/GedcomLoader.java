package net.myerichsen.archivesearcher.loaders;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;
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
import org.gedcom4j.model.thirdpartyadapters.FamilyHistorianAdapter;
import org.gedcom4j.parser.GedcomParser;

import net.myerichsen.archivesearcher.models.IndividualModel;
import net.myerichsen.archivesearcher.util.Fonkod;
import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * Read and analyze a GEDCOM file and load the data into a Derby database
 *
 * @author Michael Erichsen
 * @version 20. jun. 2023
 */

public class GedcomLoader {
	/**
	 * Static constants and variables
	 */
	private static final String[] filter = new String[] { "af", "bager", "gamle", "gmd", "hustru", "i", "inds",
			"junior", "kirkesanger", "pige", "pigen", "portner", "propriet�r", "sadelmager", "skolel�rer", "skovfoged",
			"slagter", "smed", "smedesvend", "snedker", "s�n", "ugift", "ugifte", "unge", "ungkarl", "u�gte", "�r" };

	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String DELETE_EVENT = "DELETE FROM EVENT";
	private static final String DELETE_INDIVIDUAL = "DELETE FROM INDIVIDUAL";
	private static final String DELETE_FAMILY = "DELETE FROM FAMILY";
	private static final String DELETE_PARENTS = "DELETE FROM PARENTS";

	private static final String INSERT_INDIVIDUAL_EVENT = "INSERT INTO EVENT (TYPE, SUBTYPE, DATE, INDIVIDUAL, "
			+ "FAMILY, PLACE, NOTE, SOURCEDETAIL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_INDIVIDUAL = "INSERT INTO INDIVIDUAL (ID, GIVENNAME, SURNAME, SEX, PHONNAME) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY_EVENT = "INSERT INTO EVENT (TYPE, SUBTYPE, DATE, FAMILY, PLACE, "
			+ "NOTE, SOURCEDETAIL, INDIVIDUAL) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_FAMILY = "INSERT INTO FAMILY (ID, HUSBAND, WIFE) VALUES (?, NULL, NULL)";
	private static final String INSERT_PARENTS = "INSERT INTO PARENTS (INDIVIDUALKEY, BIRTHYEAR, NAME, "
			+ "PARENTS, FATHERPHONETIC, MOTHERPHONETIC, PLACE) VALUES(?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_INDIVIDUAL_BDP = "UPDATE INDIVIDUAL SET BIRTHDATE = ?, "
			+ "BIRTHPLACE = ?, DEATHDATE = ?, DEATHPLACE = ? WHERE ID = ?";
	private static final String UPDATE_INDIVIDUAL_FAMC = "UPDATE INDIVIDUAL SET FAMC = ?, PARENTS = ? WHERE ID = ?";
	private static final String UPDATE_INDIVIDUAL_PARENTS = "UPDATE INDIVIDUAL SET PARENTS = ? WHERE ID = ?";
	private static final String UPDATE_FAMILY_WIFE = "UPDATE FAMILY SET WIFE=? WHERE ID = ?";
	private static final String UPDATE_FAMILY_HUSBAND = "UPDATE FAMILY SET HUSBAND = ? WHERE ID = ?";

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

	private static Fonkod fonkod = new Fonkod();
	private static FamilyHistorianAdapter fha = new FamilyHistorianAdapter();
	private static Gedcom gedcom;

	/**
	 * Load and analyze GEDCOM file
	 *
	 * @param args
	 * @param shells
	 * @return
	 */
	public static String main(String[] args, ArchiveSearcher as) {
		final GedcomLoader ir = new GedcomLoader();

		try {
			ir.execute(args, as);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return "GEDCOM tabeller er indl�st";

	}

	private int birthYear;
	private String sted;

	/**
	 * Add child birth events
	 *
	 * @throws SQLException
	 *
	 */
	private void addChildBirthEvents(Connection conn, String schema) throws SQLException {
		Family family;

		final Map<String, Family> families = gedcom.getFamilies();

		for (final Entry<String, Family> familyNode : families.entrySet()) {
			family = familyNode.getValue();

			final List<IndividualReference> children = family.getChildren();

			if (children == null) {
				continue;
			}

			for (final IndividualReference individualReference : children) {
				if (family.getHusband() != null) {
					final String xref = individualReference.getIndividual().getXref();
					final IndividualModel model = IndividualModel.getIndividualFromId(conn, schema, xref);

					psINSERT_INDIVIDUAL_EVENT.setString(1, "Child");
					psINSERT_INDIVIDUAL_EVENT.setString(2, "Birth");
					psINSERT_INDIVIDUAL_EVENT.setDate(3, model.getBirthDate());
					psINSERT_INDIVIDUAL_EVENT.setString(4, family.getHusband().getIndividual().getXref());
					psINSERT_INDIVIDUAL_EVENT.setString(5, model.getFamc());
					psINSERT_INDIVIDUAL_EVENT.setString(6, model.getBirthPlace());
					psINSERT_INDIVIDUAL_EVENT.setString(7, "");
					psINSERT_INDIVIDUAL_EVENT.setString(8, model.getName());
					psINSERT_INDIVIDUAL_EVENT.executeUpdate();
				}

				if (family.getWife() != null) {
					final String xref = individualReference.getIndividual().getXref();
					final IndividualModel model = IndividualModel.getIndividualFromId(conn, schema, xref);

					psINSERT_INDIVIDUAL_EVENT.setString(1, "Child");
					psINSERT_INDIVIDUAL_EVENT.setString(2, "Birth");
					psINSERT_INDIVIDUAL_EVENT.setDate(3, model.getBirthDate());
					psINSERT_INDIVIDUAL_EVENT.setString(4, family.getWife().getIndividual().getXref());
					psINSERT_INDIVIDUAL_EVENT.setString(5, model.getFamc());
					psINSERT_INDIVIDUAL_EVENT.setString(6, model.getBirthPlace());
					psINSERT_INDIVIDUAL_EVENT.setString(7, "");
					psINSERT_INDIVIDUAL_EVENT.setString(8, model.getName());
					psINSERT_INDIVIDUAL_EVENT.executeUpdate();
				}

			}
		}
	}

	/**
	 * Add census events marked as shared
	 *
	 * @throws SQLException
	 *
	 */
	private void addSharedCensusEvents() throws SQLException {
		Family family;
		String string;

		final Map<String, Family> families = gedcom.getFamilies();

		for (final Entry<String, Family> familyNode : families.entrySet()) {
			family = familyNode.getValue();

			final List<FamilyEvent> events = family.getEvents();

			if (events == null) {
				continue;
			}

			for (final FamilyEvent familyEvent : events) {
				final String type = familyEvent.getType().getDisplay();

				if (!type.equals("Census")) {
					continue;
				}

				for (final FamilyEvent familyEvent2 : events) {
					if (!familyEvent2.getType().getDisplay().equals("Census") || familyEvent2.getPlace() == null) {
						continue;
					}

					final String place = familyEvent2.getPlace().getPlaceName();
					final List<CustomFact> customFacts2 = familyEvent2.getCustomFacts();

					for (final CustomFact cf3 : customFacts2) {
						if (cf3.getTag().equals("_SHAR")) {
							psINSERT_INDIVIDUAL_EVENT.setString(1, "Census");
							psINSERT_INDIVIDUAL_EVENT.setString(2, "Witness");
							psINSERT_INDIVIDUAL_EVENT.setDate(3, formatDate(familyEvent2.getDate()));
							psINSERT_INDIVIDUAL_EVENT.setString(4, cf3.getDescription().getValue());
							psINSERT_INDIVIDUAL_EVENT.setString(5, familyNode.getKey());
							psINSERT_INDIVIDUAL_EVENT.setString(6, place);
							psINSERT_INDIVIDUAL_EVENT.setString(7, "");

							final List<AbstractCitation> citations = familyEvent2.getCitations();
							if (citations == null) {
								psINSERT_INDIVIDUAL_EVENT.setString(8, "");
							} else {
								final CitationWithSource cfs = (CitationWithSource) citations.get(0);
								final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

								if (whereInSource == null) {
									psINSERT_INDIVIDUAL_EVENT.setString(8, "");
								} else {
									final List<CustomFact> customFacts1 = whereInSource.getCustomFacts();

									if (customFacts1 == null) {
										if (whereInSource.getValue() != null) {
											psINSERT_INDIVIDUAL_EVENT.setString(8, whereInSource.getValue());
										} else {
											psINSERT_INDIVIDUAL_EVENT.setString(8, "");
										}
									} else {
										final StringBuilder sb2 = new StringBuilder(whereInSource.getValue() + ", ");

										for (final CustomFact customFact : customFacts1) {
											sb2.append(customFact.getDescription().getValue());
										}

										string = sb2.toString().replace("'", "�");
										string = string.length() > 256 ? string.substring(0, 255) : string;
										string = string + "', '";
										psINSERT_INDIVIDUAL_EVENT.setString(8, string);
									}
								}
							}

							psINSERT_INDIVIDUAL_EVENT.executeUpdate();
						}
					}
				}
			}
		}

		Individual individual;

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualNode : individuals.entrySet()) {
			individual = individualNode.getValue();

			final List<IndividualEvent> individualEvents = individual.getEvents();

			if (individualEvents == null) {
				continue;
			}

			for (final IndividualEvent individualEvent : individualEvents) {
				final String type = individualEvent.getType().getDisplay();

				if (!type.equals("Census")) {
					continue;
				}

				for (final IndividualEvent individualEvent2 : individualEvents) {
					if (!individualEvent2.getType().getDisplay().equals("Census")) {
						continue;
					}

					final String place = individualEvent2.getPlace().getPlaceName();
					final List<CustomFact> customFacts2 = individualEvent2.getCustomFacts();

					for (final CustomFact cf3 : customFacts2) {
						if (cf3.getTag().equals("_SHAR")) {
							psINSERT_INDIVIDUAL_EVENT.setString(1, "Census");
							psINSERT_INDIVIDUAL_EVENT.setString(2, "Witness");
							psINSERT_INDIVIDUAL_EVENT.setDate(3, formatDate(individualEvent2.getDate()));
							psINSERT_INDIVIDUAL_EVENT.setString(4, cf3.getDescription().getValue());
							psINSERT_INDIVIDUAL_EVENT.setString(5, individualNode.getKey());
							psINSERT_INDIVIDUAL_EVENT.setString(6, place);
							psINSERT_INDIVIDUAL_EVENT.setString(7, "");

							final List<AbstractCitation> citations = individualEvent2.getCitations();
							if (citations == null) {
								psINSERT_INDIVIDUAL_EVENT.setString(8, "");
							} else {
								final CitationWithSource cfs = (CitationWithSource) citations.get(0);
								final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

								if (whereInSource == null) {
									psINSERT_INDIVIDUAL_EVENT.setString(8, "");
								} else {
									final List<CustomFact> customFacts1 = whereInSource.getCustomFacts();

									if (customFacts1 == null) {
										if (whereInSource.getValue() != null) {
											psINSERT_INDIVIDUAL_EVENT.setString(8, whereInSource.getValue());
										} else {
											psINSERT_INDIVIDUAL_EVENT.setString(8, "");
										}
									} else {
										final StringBuilder sb2 = new StringBuilder(whereInSource.getValue() + ", ");

										for (final CustomFact customFact : customFacts1) {
											sb2.append(customFact.getDescription().getValue());
										}

										string = sb2.toString().replace("'", "�");
										string = string.length() > 256 ? string.substring(0, 255) : string;
										string = string + "', '";
										psINSERT_INDIVIDUAL_EVENT.setString(8, string);
									}
								}
							}

							psINSERT_INDIVIDUAL_EVENT.executeUpdate();
						}
					}
				}
			}
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
		statement = conn.prepareStatement(DELETE_FAMILY);
		statement.execute();
		statement = conn.prepareStatement(DELETE_PARENTS);
		statement.execute();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @param shells
	 * @throws Exception
	 */
	private void execute(String[] args, ArchiveSearcher as) throws Exception {
		final String dbURL = "jdbc:derby:" + args[1];
		final String schema = args[2];
		try {
			DriverManager.getConnection(dbURL + ";shutdown=true");
		} catch (final SQLException e) {
			// Shutdown message is expected
			Display.getDefault().asyncExec(() -> as.setMessage(e.getMessage()));
		}
		final Connection conn = DriverManager.getConnection(dbURL);
		final PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		prepareStatements(conn);

		readGedcom(args[0]);
		Display.getDefault().asyncExec(() -> as.setMessage("GEDCOM fil analyseres"));

		clearTables(conn);
		Display.getDefault().asyncExec(() -> as.setMessage("Tabellerne er ryddet"));

		parseAllFamilies();
		Display.getDefault().asyncExec(() -> as.setMessage("Familier er analyseret"));

		parseAllIndividuals();
		Display.getDefault().asyncExec(() -> as.setMessage("Personer er analyseret"));

		updateBirthDeathData(conn, args[2]);
		Display.getDefault().asyncExec(() -> as.setMessage("F�dsels- og d�dsdata er opdateret"));

		parseParents();
		Display.getDefault().asyncExec(() -> as.setMessage("For�ldre er analyseret"));

		addSharedCensusEvents();
		Display.getDefault().asyncExec(() -> as.setMessage("Yderligere folket�llingsh�ndelser er indsat"));

		addChildBirthEvents(conn, schema);
		Display.getDefault().asyncExec(() -> as.setMessage("Barnef�dselsh�ndelser er indsat"));

		conn.close();

	}

	/**
	 * Extract birth year from date string
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
	 * Find all family events for husband and wife
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
	 * Find all individual events
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
	 *
	 * @param key
	 * @throws SQLException
	 */
	private void insertEmptyFamily(String key) throws SQLException {
		psINSERT_FAMILY.setString(1, key);
		psINSERT_FAMILY.execute();
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
		String sbString;

		final StringWithCustomFacts subtype = familyEvent.getSubType();
		if (subtype == null) {
			psINSERT_FAMILY_EVENT.setString(2, "");
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
			psINSERT_FAMILY_EVENT.setString(5, "");
		} else {
			psINSERT_FAMILY_EVENT.setString(5, place.getPlaceName().replace("'", ""));
		}

		final List<NoteStructure> noteStructures = familyEvent.getNoteStructures();
		if (noteStructures == null) {
			psINSERT_FAMILY_EVENT.setString(6, "");
		} else {
			final List<String> lines = noteStructures.get(0).getLines();
			final StringBuilder lineBuffer = new StringBuilder();

			for (final String string : lines) {
				lineBuffer.append(string + " ");
			}
			psINSERT_FAMILY_EVENT.setString(6, lineBuffer.toString().replace("'", "�"));
		}

		final List<AbstractCitation> citations = familyEvent.getCitations();
		if (citations == null) {
			psINSERT_FAMILY_EVENT.setString(7, "");
		} else {
			final CitationWithSource cfs = (CitationWithSource) citations.get(0);
			final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				psINSERT_FAMILY_EVENT.setString(7, "");
			} else {
				final List<CustomFact> customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						psINSERT_FAMILY_EVENT.setString(7, whereInSource.getValue());
					} else {
						psINSERT_FAMILY_EVENT.setString(7, "");
					}
				} else {
					final StringBuilder sb2 = new StringBuilder(whereInSource.getValue() + ", ");

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					sbString = sb2.toString().replace("'", "�");
					sbString = sbString.length() > 256 ? sbString.substring(0, 255) : sbString;
					psINSERT_FAMILY_EVENT.setString(7, sbString);
				}
			}
		}

		if (family.getHusband() != null) {
			psINSERT_FAMILY_EVENT.setString(8, family.getHusband().getIndividual().getXref());
			psINSERT_FAMILY_EVENT.executeUpdate();
		}

		if (family.getWife() != null) {
			psINSERT_FAMILY_EVENT.setString(8, family.getWife().getIndividual().getXref());
			psINSERT_FAMILY_EVENT.executeUpdate();
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
	 * Insert an individual event
	 *
	 * @param individual
	 * @param individualEvent
	 * @return
	 * @throws SQLException
	 */
	private void insertIndividualEvent(Individual individual, final IndividualEvent individualEvent)
			throws SQLException {
		String string;

		psINSERT_INDIVIDUAL_EVENT.setString(1, individualEvent.getType().toString());

		final StringWithCustomFacts subtype = individualEvent.getSubType();
		if (subtype == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(2, "");
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

		psINSERT_INDIVIDUAL_EVENT.setString(5, "");

		final Place place = individualEvent.getPlace();
		if (place == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(6, "");
		} else {
			psINSERT_INDIVIDUAL_EVENT.setString(6, place.getPlaceName().replace("'", ""));
		}

		final IndividualEventType type = individualEvent.getType();
		if (type.getDisplay().equals("Census")) {
			final StringBuilder sb = new StringBuilder();
			final List<CustomFact> witnessReferences = fha.getWitnessReferences(individualEvent);

			for (final CustomFact cf : witnessReferences) {
				sb.append(cf.getDescription().getValue() + " ");
			}

			psINSERT_INDIVIDUAL_EVENT.setString(7, sb.toString());
		} else {
			final List<NoteStructure> noteStructures = individualEvent.getNoteStructures();
			if (noteStructures == null) {
				psINSERT_INDIVIDUAL_EVENT.setString(7, "");
			} else {
				final List<String> lines = noteStructures.get(0).getLines();
				final StringBuilder lineBuffer = new StringBuilder();

				for (final String string2 : lines) {
					lineBuffer.append(string2 + " ");
				}
				psINSERT_INDIVIDUAL_EVENT.setString(7, lineBuffer.toString().replace("'", "�"));
			}
		}

		final List<AbstractCitation> citations = individualEvent.getCitations();
		if (citations == null) {
			psINSERT_INDIVIDUAL_EVENT.setString(8, "");
		} else {
			final CitationWithSource cfs = (CitationWithSource) citations.get(0);
			final StringWithCustomFacts whereInSource = cfs.getWhereInSource();

			if (whereInSource == null) {
				psINSERT_INDIVIDUAL_EVENT.setString(8, "");
			} else {
				final List<CustomFact> customFacts = whereInSource.getCustomFacts();

				if (customFacts == null) {
					if (whereInSource.getValue() != null) {
						psINSERT_INDIVIDUAL_EVENT.setString(8, whereInSource.getValue());
					} else {
						psINSERT_INDIVIDUAL_EVENT.setString(8, "");
					}
				} else {
					final StringBuilder sb2 = new StringBuilder(whereInSource.getValue() + ", ");

					for (final CustomFact customFact : customFacts) {
						sb2.append(customFact.getDescription().getValue());
					}

					string = sb2.toString().replace("'", "�");
					string = string.length() > 256 ? string.substring(0, 255) : string;
					string = string + "', '";
					psINSERT_INDIVIDUAL_EVENT.setString(8, string);
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
	 * Load the parent table
	 *
	 * @throws SQLException
	 *
	 */

	private void parseParents() throws SQLException {
		StringBuilder sb;
		String parents = "";
		IndividualEvent christening;
		boolean husb = false;
		boolean wife = false;

		// For each individual
		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualMapEntry : individuals.entrySet()) {
			birthYear = 0;
			sted = "";
			parents = "";

			final Individual individual = individualMapEntry.getValue();

			sb = new StringBuilder();
			husb = false;
			wife = false;

			// Get all families where the individual is a child
			try {
				final List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild(false);

				for (final FamilyChild familyWhereChild : familiesWhereChild) {
					final Family family = familyWhereChild.getFamily();

					// Get the father
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
						wife = true;
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
					try {
						christening = individual.getEventsOfType(IndividualEventType.BIRTH).get(0);
						birthYear = extractBirthYear(christening.getDate().getValue());
						sted = christening.getPlace().getPlaceName().toString();
					} catch (final Exception e1) {
					}
				}
			}

			if (!husb || !wife) {
				parents = getParentsFromSource(individual);
			}

			if (parents.isBlank()) {
				continue;
			}

			psUPDATE_INDIVIDUAL_PARENTS.setString(1, parents);
			psUPDATE_INDIVIDUAL_PARENTS.setString(2, individual.getXref());
			psUPDATE_INDIVIDUAL_PARENTS.executeUpdate();

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
		}
	}

	/**
	 * Prepare all SQL statements
	 *
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
	 * Split a string with a parent pair into two parents while removing noise words
	 *
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
	private void updateBirthDeathData(Connection conn, String schema) throws SQLException {
		// Read all individuals into a list
		final List<IndividualModel> ldbi = IndividualModel.load(conn, schema);

		// Update all individual records in Derby
		updateIndividualsBirthDeath(ldbi);
	}

	/**
	 * Update a family by inserting the husband
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
	 * Update a family by inserting the wife
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
	 * Update an individual by adding the family in which it is a child
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
			} else {
				parents = (father + mother).trim();
			}

			psUPDATE_INDIVIDUAL_FAMC.setString(1, individual.getFamiliesWhereChild().get(0).getFamily().getXref());
			psUPDATE_INDIVIDUAL_FAMC.setString(2, parents);
			psUPDATE_INDIVIDUAL_FAMC.setString(3, individual.getXref());

			try {
				psUPDATE_INDIVIDUAL_FAMC.execute();
			} catch (final SQLException e) {

				// Handle family not yet inserted
				if (!e.getSQLState().equals("23503")) {
					throw new Exception("sql Error Code: " + e.getErrorCode() + ", sql State: " + e.getSQLState());
				}
			}
		}
	}

	/**
	 * Update an individual by adding parents
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

				psUPDATE_INDIVIDUAL_PARENTS.setString(1, parents);
				psUPDATE_INDIVIDUAL_PARENTS.setString(2, individual.getXref());
				psUPDATE_INDIVIDUAL_PARENTS.execute();
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * Update birth and death dates and places for all individuals
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
