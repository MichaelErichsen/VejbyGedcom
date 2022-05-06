package net.myerichsen.gedcom.parentFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
 * <li>Location name (e. g. village), where each character of [Ê¯Â∆ÿ≈] must be
 * replaced with a "."</li>
 * <li>Full path to GEDCOM file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each person found.
 * <p>
 * Parents are either extracted from the GEDCOM family record or from the
 * citation source detail for the Christening event. When not using the family
 * record, the first line of the citation detail must contain the location and
 * the names of one or both parents.
 * 
 * @author Michael Erichsen
 * @version 06-05-2022
 *
 */

public class ParentFinder {
	private String sted;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: ParentFinder location gedcomfile outputdirectory");
			System.exit(4);
		}

		ParentFinder pf = new ParentFinder();

		try {
			pf.execute(args[0].toLowerCase(), args[1], args[2]);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}
	}

	/**
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

	/**
	 * @param location
	 * @param filename
	 * @param outputdirectory
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private void execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		Gedcom gedcom = readGedcom(filename);
		boolean found = false;
		StringBuilder sb;
		String outline = "";

		String outfile = outputdirectory + "\\" + location + ".csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Entry<String, Individual> individual : individuals.entrySet()) {
			found = false;

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
				if (sb.length() == 0) {
					outline = individual.getKey() + ";" + value.getFormattedName().trim() + ";Source;"
							+ getParentsFromSource(value) + ";" + sted;
				} else {
					outline = individual.getKey() + ";" + value.getFormattedName().trim() + ";Tree;" + sb + ";" + sted;

				}

				System.out.println(outline);
				writer.write(outline + "\n");
			}
		}

		writer.flush();
		writer.close();
	}

	/**
	 * @param value
	 * @return
	 */
	private String getParentsFromSource(Individual value) {
		try {
			IndividualEvent event = value.getEventsOfType(IndividualEventType.CHRISTENING).get(0);
			CitationWithSource citation = (CitationWithSource) event.getCitations().get(0);
			String string = citation.getWhereInSource().toString();
			return string;
		} catch (Exception e) {
		}

		return "";
	}

	/**
	 * @param type
	 * @return
	 */
	private boolean testLocation(Individual value, IndividualEventType type, String location) {
		try {
			IndividualEvent event = value.getEventsOfType(type).get(0);
			sted = event.getPlace().getPlaceName().toLowerCase();

			Pattern pattern = Pattern.compile(location);
			Matcher matcher = pattern.matcher(sted);
			return matcher.find();
		} catch (Exception e) {
		}
		return false;
	}

	/**
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