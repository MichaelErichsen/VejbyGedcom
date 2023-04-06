package net.myerichsen.gedcom.db.loaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.gedcom4j.model.CitationWithSource;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Read a GEDCOM and load data into a Derby database to use for analysis.
 *
 * @author Michael Erichsen
 * @version 6. apr. 2023
 */
public class DBParentLoader {
	/**
	 * Static constants and variables
	 */
	private static final String DELETE_PARENTS = "DELETE FROM VEJBY.PARENTS";
	private static final String INSERT_PARENTS = "INSERT INTO VEJBY.PARENTS (INDIVIDUALKEY, BIRTHYEAR, NAME, "
			+ "PARENTS, FATHERPHONETIC, MOTHERPHONETIC, PLACE) VALUES(?, ?, ?, ?, ?, ?, ?)";

	private static PreparedStatement psINSERT_PARENTS;

	private static Logger logger;
	private static Fonkod fonkod = new Fonkod();
	private static Gedcom gedcom;
	private static int parentsCounter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		logger = Logger.getLogger("DBParentLoader");

		if (args.length < 2) {
			logger.info("Usage: DBParentLoader gedcomfile derbydatabasepath");
			System.exit(4);
		}

		final DBParentLoader dbpl = new DBParentLoader();

		try {
			dbpl.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	private String sted;

	private int birthYear;

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);

		final PreparedStatement statement = conn.prepareStatement(DELETE_PARENTS);
		statement.execute();

		psINSERT_PARENTS = conn.prepareStatement(INSERT_PARENTS);

		final GedcomParser gp = new GedcomParser();
		gp.load(args[0]);
		gedcom = gp.getGedcom();

		parseParents(gedcom);

		conn.close();

		logger.info("Program ended.\n" + parentsCounter + " parent pairs inserted");
	}

	/**
	 * Extract birthYear from date string
	 *
	 * @param dateString
	 * @return
	 */
	private int extractYear(String dateString) {
		String year = dateString;

		if (year.length() > 4) {
			year = year.substring(dateString.length() - 4);
		}

		return Integer.parseInt(year);
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
			birthYear = extractYear(christening.getDate().getValue());
			sted = christening.getPlace().getPlaceName().toString();
			final CitationWithSource citation = (CitationWithSource) christening.getCitations().get(0);
			return citation.getWhereInSource().toString();
		} catch (final Exception e) {
		}

		return "";
	}

	/**
	 * Parse gedcom for parent pairs
	 *
	 * @param gedcom
	 * @throws SQLException
	 *
	 */
	private void parseParents(Gedcom gedcom) throws SQLException {
		StringBuilder sb;
		String parents = "";
		IndividualEvent christening;

		// For each individual
		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individualMapEntry : individuals.entrySet()) {
			birthYear = 2023;

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
					birthYear = extractYear(christening.getDate().getValue());
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
				a = (a.length() > 64 ? a.substring(0, 63) : a);
			} catch (final Exception e) {
			}

			psINSERT_PARENTS.setString(5, a);

			try {
				b = fonkod.generateKey(splitParents[1]);
				b = (b.length() > 64 ? b.substring(0, 63) : b);
			} catch (final Exception e) {
			}

			psINSERT_PARENTS.setString(6, b);
			psINSERT_PARENTS.setString(7, sted);
			psINSERT_PARENTS.executeUpdate();
			parentsCounter++;
		}
	}

	/**
	 * @param parents2
	 * @return
	 */
	private String[] splitParents(String parents2) {
		if ((parents2 == null) || (parents2.length() == 0)) {
			return new String[] { "", "" };
		}

		String s = parents2.replaceAll("\\d", "").replaceAll("\\.", "").toLowerCase();
		s = s.replace(", f.", "");
		String[] sa = s.split(",");
		final String[] words = sa[0].split(" ");

		final String[] filter = new String[] { "af", "bager", "gamle", "gmd", "i", "inds", "junior", "kirkesanger",
				"pige", "pigen", "portner", "proprietær", "sadelmager", "skolelærer", "skovfoged", "slagter", "smed",
				"smedesvend", "snedker", "søn", "ugift", "ugifte", "unge", "ungkarl", "uægte", "år" };

		final StringBuffer sb = new StringBuffer();

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
}
