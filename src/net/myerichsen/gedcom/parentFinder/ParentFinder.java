package net.myerichsen.gedcom.parentFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 * @version 19-05-2022
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
					"Usage: ParentFinder location gedcomfile outputdirectory [p], where p adds parent candidates");
			System.exit(4);
		}

		if ((args.length > 3) && args[3].equalsIgnoreCase("p")) {
			pFlag = true;
		}

		ParentFinder pf = new ParentFinder();

		try {
			pf.execute(args[0], args[1], args[2]);
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
		GedcomParser gp = new GedcomParser();
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
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private void execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		List<String> matchParentNames;
		String outfile = outputdirectory + "\\" + location + ".csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String type = "";
		String s = "";

		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		Gedcom gedcom = readGedcom(filename);
		boolean found = false;
		StringBuilder sb;
		String parents = "";
		String outline = "";

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Entry<String, Individual> individual : individuals.entrySet()) {
			found = false;
			year = "????";

			Individual value = individual.getValue();

			boolean found1 = testLocation(value, IndividualEventType.CHRISTENING, location);
			boolean found2 = testLocation(value, IndividualEventType.BIRTH, location);
			found = found1 || found2;

			if (!found) {
				found = testSource(value, location);
			}

			sb = new StringBuilder();

			try {
				List<FamilyChild> familiesWhereChild = value.getFamiliesWhereChild(false);

				for (FamilyChild familyChild : familiesWhereChild) {
					Family family = familyChild.getFamily();
					boolean husb = false;

					try {
						sb.append(family.getHusband().getIndividual().getNames().get(0));
						husb = true;
					} catch (Exception e) {
					}
					try {
						PersonalName w = family.getWife().getIndividual().getNames().get(0);

						if (husb) {
							sb.append(" og ");
						}

						sb.append(w);
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
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

				if ((pFlag == true) && (parents.length() > 0)) {
					matchParentNames = matchParentNames(gedcom, parents, year);

					if (matchParentNames.size() > 0) {
						for (String string : matchParentNames) {
							outline = individual.getKey() + ";" + year + ";" + value.getFormattedName() + ";" + type
									+ ";" + parents + ";" + sted + ";" + string;
							outline = outline.replace("/", "");

							System.out.println(outline);
							writer.write(outline + "\n");
						}
					}
				} else {
					outline = individual.getKey() + ";" + year + ";" + value.getFormattedName() + ";" + type + ";"
							+ parents + ";" + sted + ";" + s;
					outline = outline.replace("/", "");

					System.out.println(outline + ";" + s);
					writer.write(outline + ";" + s + "\n");
				}
			}
		}

		writer.flush();
		writer.close();
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
	 * @param gedcom
	 * @param parentName
	 * @param childBirthYear
	 * @return
	 */
	private List<String> findParentCandidates(Gedcom gedcom, String parentName, String childBirthYear) {
		List<String> parentList = new ArrayList<>();
		Individual value;
		Matcher matcher;
		String name;
		String id;
		String birthYear = "";
		String deathYear = "";
		String birthPlace = "";
		Pattern pattern = Pattern.compile(parentName.toLowerCase());

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Entry<String, Individual> individual : individuals.entrySet()) {
			value = individual.getValue();
			name = value.getFormattedName().replace("/", "");

			matcher = pattern.matcher(name.toLowerCase());

			if (matcher.find()) {
				id = individual.getKey();
				int by = 0;

				try {
					IndividualEvent individualEvent = getBirthEvent(value);
					try {
						birthYear = extractYear(individualEvent.getDate().getValue());
						by = Integer.parseInt(birthYear);
					} catch (Exception e) {
						birthYear = "";
					}
					try {
						birthPlace = individualEvent.getPlace().getPlaceName();
					} catch (Exception e) {
						birthPlace = "";
					}
				} catch (Exception e) {
					birthYear = "";
					birthPlace = "";
					by = 0;
				}

				int dy = 9999;
				try {
					deathYear = extractYear(
							value.getEventsOfType(IndividualEventType.DEATH).get(0).getDate().getValue());
					dy = Integer.parseInt(deathYear);
				} catch (Exception e) {
					deathYear = "";
				}

				if ((by < (Integer.parseInt(childBirthYear) - 15)) && (dy > (Integer.parseInt(childBirthYear)))) {
					parentList.add(id + ";" + birthYear + ";" + deathYear + ";" + name + ";" + birthPlace);
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
		} catch (Exception e) {
			try {
				return value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			} catch (Exception e1) {
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
			IndividualEvent event = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			year = extractYear(event.getDate().getValue());
			CitationWithSource citation = (CitationWithSource) event.getCitations().get(0);
			String string = citation.getWhereInSource().toString();
			return string;
		} catch (Exception e) {
		}

		return "";
	}

	/**
	 * Get a list of strings identifying possible matches for the parents
	 * 
	 * @param gedcom
	 * @param parents
	 * @param year
	 * @return
	 */
	private List<String> matchParentNames(Gedcom gedcom, String parents, String year) {
		List<String> parentList = new ArrayList<>();

		String[] sComma = parents.split(",");
		String[] sOg = sComma[0].split("og");

		for (String element : sOg) {
			parentList.addAll(findParentCandidates(gedcom, element.trim(), year));
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
			IndividualEvent event = value.getEventsOfType(type).get(0);
			year = event.getDate().getValue();
			sted = event.getPlace().getPlaceName().toLowerCase();

			Pattern pattern = Pattern.compile(location);
			Matcher matcher = pattern.matcher(sted);
			return matcher.find();
		} catch (Exception e) {
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
			IndividualEvent event = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);

			CitationWithSource citation = (CitationWithSource) event.getCitations().get(0);
			sted = citation.getWhereInSource().toString();

			Pattern pattern = Pattern.compile(location);
			Matcher matcher = pattern.matcher(sted);
			return matcher.find();
		} catch (Exception e) {

		}
		return false;
	}
}