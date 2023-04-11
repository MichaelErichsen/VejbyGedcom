package net.myerichsen.gedcom.parentFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.CitationWithSource;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find parents for each person born or christened in a given location.
 * <p>
 * Parameters:
 * <ul>
 * <li>Location name</li>
 * <li>Full path to GEDCOM file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each person found and
 * possible known parents.
 * <p>
 * Parents are either extracted from the GEDCOM family record or from the
 * citation source detail for the Christening event. When not using the family
 * record, the first line of the citation detail must contain the location and
 * the names of one or both parents.
 *
 * @author Michael Erichsen
 * @version 19-01-2023
 *
 */

public class ParentFinder {
	private static boolean pFlag = false;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println(
					"Usage: ParentFinder location gedcomfile outputdirectory [p], where p adds parent candidates\n"
							+ "\"all\" as location selects all");
			System.exit(4);
		}

		if (args.length > 3 && args[3].equalsIgnoreCase("p")) {
			pFlag = true;
		}

		final ParentFinder pf = new ParentFinder();

		try {
			final String outfile = pf.execute(args[0], args[1], args[2]);
			System.out.println("Saved to " + outfile);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
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
	private static Gedcom readGedcom(String filename) throws IOException, GedcomParserException {
		final GedcomParser gp = new GedcomParser();
		gp.load(filename);
		return gp.getGedcom();
	}

	private String sted;
	private String year;

	/**
	 * Worker method
	 *
	 * @param location
	 * @param filename
	 * @param outputdirectory
	 * @return
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private String execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		List<String> matchParentNames;
		final String outfile = outputdirectory + "\\par_" + location + ".csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String type = "";
		final String s = "";

		if (location.equals("all")) {
			location = "";
		}

		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		System.out.println("Reading " + filename);

		final Gedcom gedcom = readGedcom(filename);
		boolean found = false;
		StringBuilder sb;
		String parents = "";
		String outline = "";

		System.out.println("Parsing " + filename);

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individual : individuals.entrySet()) {
			found = false;
			year = "????";

			final Individual value = individual.getValue();

			final boolean found1 = testLocation(value, IndividualEventType.CHRISTENING, location);
			final boolean found2 = testLocation(value, IndividualEventType.BIRTH, location);
			found = found1 || found2;

			if (!found) {
				found = testSource(value, location);
			}

			sb = new StringBuilder();

			try {
				final List<FamilyChild> familiesWhereChild = value.getFamiliesWhereChild(false);

				for (final FamilyChild familyChild : familiesWhereChild) {
					final Family family = familyChild.getFamily();
					boolean husb = false;

					try {
						sb.append(family.getHusband().getIndividual().getNames().get(0));
						husb = true;
					} catch (final Exception e) {
					}
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

			if (found) {
				year = extractYear(year);

				if (sb.length() > 0) {
					type = "Tree";
					parents = sb.toString();
				} else {
					type = "Source";
					parents = getParentsFromSource(value);
				}

				if (pFlag && parents.length() > 0) {
					matchParentNames = matchParentNames(individuals, parents, year);

					if (matchParentNames.size() > 0) {
						for (final String string : matchParentNames) {
							outline = individual.getKey() + ";" + year + ";" + value.getFormattedName() + ";" + type
									+ ";" + parents + ";" + sted + ";" + string;
							outline = outline.replace("/", "");
							writer.write(outline + "\n");
						}
					}
				} else {
					outline = individual.getKey().replace("@I", "").replace("@", "") + ";" + year + ";"
							+ value.getFormattedName() + ";" + type + ";" + parents.replace("  ", " ") + ";" + sted
							+ ";" + s;
					outline = outline.replace("/", "");
					writer.write(outline + ";" + s + "\n");
				}
			}
		}

		writer.flush();
		writer.close();
		return outfile;
	}

	/**
	 * Extract year from date string
	 *
	 * @param dateString
	 * @return
	 */
	private String extractYear(String dateString) {
		String year = dateString;

		if (year.length() > 4) {
			year = year.substring(dateString.length() - 4);
		}

		return year;
	}

	/**
	 * Return list of parent candidates for the current parent in the format
	 * <ul>
	 * <li>ID</li>
	 * <li>birth year</li>
	 * <li>death year</li>
	 * <li>parent name</li>
	 * <li>birth place</li>
	 * </ul>
	 *
	 * @param individuals2
	 * @param parentName
	 * @param childBirthYear
	 * @return
	 */
	private List<String> findParentCandidates(Map<String, Individual> individuals, String parentName,
			String childBirthYear) {
		final List<String> parentList = new ArrayList<>();
		Individual value;
		Matcher matcher;
		String name;
		String id;
		String birthYear = "";
		String deathYear = "";
		String birthPlace = "";
		final Pattern pattern = Pattern.compile(parentName.toLowerCase());

		for (final Entry<String, Individual> individual : individuals.entrySet()) {
			value = individual.getValue();
			name = value.getFormattedName().replace("/", "");

			matcher = pattern.matcher(name.toLowerCase());

			if (matcher.find()) {
				id = individual.getKey();
				int by = 0;

				try {
					final IndividualEvent individualEvent = getBirthEvent(value);
					try {
						birthYear = extractYear(individualEvent.getDate().getValue());
						by = Integer.parseInt(birthYear);
					} catch (final Exception e) {
						birthYear = "";
					}
					try {
						birthPlace = individualEvent.getPlace().getPlaceName();
					} catch (final Exception e) {
						birthPlace = "";
					}
				} catch (final Exception e) {
					birthYear = "";
					birthPlace = "";
					by = 0;
				}

				int dy = 9999;
				try {
					deathYear = extractYear(
							value.getEventsOfType(IndividualEventType.DEATH).get(0).getDate().getValue());
					dy = Integer.parseInt(deathYear);
				} catch (final Exception e) {
					deathYear = "";
				}

				final String spouses = getSpouses(individuals, id);

				if (by < Integer.parseInt(childBirthYear) - 15 && dy > Integer.parseInt(childBirthYear)) {
					parentList.add(
							id + ";" + birthYear + ";" + deathYear + ";" + name + ";" + spouses + ";" + birthPlace);
				}
			}
		}

		return parentList;
	}

	/**
	 * Get either a birth or a christening event
	 *
	 * @param value
	 * @return
	 */
	private IndividualEvent getBirthEvent(Individual value) {
		try {
			return value.getEventsOfType(IndividualEventType.BIRTH).get(0);
		} catch (final Exception e) {
			try {
				return value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			} catch (final Exception e1) {
				return null;
			}
		}
	}

	/**
	 * Get parents from christening source citation
	 *
	 * @param value
	 * @return
	 */
	private String getParentsFromSource(Individual value) {
		try {
			final IndividualEvent event = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			year = extractYear(event.getDate().getValue());
			final CitationWithSource citation = (CitationWithSource) event.getCitations().get(0);
			return citation.getWhereInSource().toString();
		} catch (final Exception e) {
		}

		return "";
	}

	/**
	 * Return a string listing all spouses for a given individual
	 *
	 * @param id
	 * @return
	 */
	private String getSpouses(Map<String, Individual> individuals, String id) {
		final StringBuilder sb = new StringBuilder();
		final Individual individual = individuals.get(id);
		final Set<Individual> spouses = individual.getSpouses();

		for (final Individual spouse : spouses) {
			sb.append(spouse.getFormattedName().replace("/", "") + " " + spouse.getXref() + " ");
		}

		return sb.toString();
	}

	/**
	 * Get a list of strings identifying possible matches for the parents
	 *
	 * @param individuals
	 * @param parents
	 * @param year
	 * @return
	 */
	private List<String> matchParentNames(Map<String, Individual> individuals, String parents, String year) {
		final List<String> parentList = new ArrayList<>();

		final String[] sComma = parents.split(",");
		final String[] sOg = sComma[0].split("og");

		for (final String element : sOg) {
			parentList.addAll(findParentCandidates(individuals, element.trim(), year));
		}

		return parentList;
	}

	/**
	 * Filter for locations matching the input criteria in birth or christening
	 * event places, handling Danish national characters
	 *
	 * @param type
	 * @return
	 */
	private boolean testLocation(Individual value, IndividualEventType type, String location) {
		try {
			final IndividualEvent event = value.getEventsOfType(type).get(0);
			year = event.getDate().getValue();

			final Pattern pattern = Pattern.compile(location);
			sted = event.getPlace().getPlaceName().toLowerCase();
			final Matcher matcher = pattern.matcher(sted);
			return matcher.find();
		} catch (final Exception e) {
		}
		return false;
	}

	/**
	 * Filter for locations matching the input criteria in christening source
	 * citations, handling Danish national characters
	 *
	 * @param value
	 * @param location
	 * @return
	 */
	private boolean testSource(Individual value, String location) {
		try {
			final IndividualEvent event = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);

			final CitationWithSource citation = (CitationWithSource) event.getCitations().get(0);

			final Pattern pattern = Pattern.compile(location);
			sted = citation.getWhereInSource().toString();
			final Matcher matcher = pattern.matcher(sted);
			return matcher.find();
		} catch (final Exception e) {

		}
		return false;
	}
}