package net.myerichsen.gedcom.probateFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * List all persons born before 1860 with locations of their deaths, burials or
 * last event with a place
 * <p>
 * Parameters:
 * <ul>
 * <li>Location name</li>
 * <li>Full path to GEDCOM file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each person found
 *
 * @author Michael Erichsen
 * @version 8. jan. 2023
 *
 */
public class ProbateFinder {

	/**
	 * MilRollView method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: ProbateFinder location gedcomfile outputdirectory\n");
			System.exit(4);
		}

		final ProbateFinder pf = new ProbateFinder();

		String outfile = "";

		try {
			outfile = pf.execute(args[0].toLowerCase(), args[1], args[2]);
			System.out.println("Saved to " + outfile);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Worker method
	 *
	 * @param location
	 * @param filename
	 * @param outputdir
	 * @return Full path of the output file
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private String execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		Individual individual;
		List<IndividualEvent> births;
		LocalDate localDate = null;
		int counter = 0;

		final ArrayList<ProbatePerson> listPp = new ArrayList<>();

		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		System.out.println("Reading file " + filename);

		final Gedcom gedcom = readGedcom(filename);

		System.out.println("Parsing GEDCOM");

		final String outfile = outputdirectory + "\\prob_" + location + ".csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		final String outline = "\"Person\";\"Fødselsår\";\"ISO Fødselsår\";\"Sidste sted\";\"ID\"";
		writer.write(outline + "\n");

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Map.Entry<String, Individual> entry : individuals.entrySet()) {
			individual = entry.getValue();
			births = individual.getEventsOfType(IndividualEventType.BIRTH);

			if (births.isEmpty() || births.size() == 0) {
				births = individual.getEventsOfType(IndividualEventType.CHRISTENING);
			}

			if (births.isEmpty() || births.size() == 0) {
				System.err.println("No Birth: " + entry.getKey() + ";" + individual);
				continue;
			}

			localDate = ProbateUtil.parseProbateDate(births);

			if (localDate.isAfter(LocalDate.parse("1840-01-01"))) {
				continue;
			}

			final ProbatePerson pp = new ProbatePerson(entry);

			listPp.add(pp);
		}

		System.out.println("Sorting GEDCOM");

		Collections.sort(listPp, new ProbateFinderComparator());

		System.out.println("Creating .csv file");

		final String pattern = ".*" + location + ".*";

		for (final ProbatePerson probatePerson : listPp) {
			if (location.equalsIgnoreCase("all") || probatePerson.getLastPlace().toLowerCase().matches(pattern)) {
				writer.write(probatePerson.toString() + "\n");
				counter++;
			}
		}

		writer.flush();
		writer.close();
		System.out.println(counter + " records for location " + location + " written");
		return outfile;
	}

	/**
	 * Read a GEDCOM file using org.gedcomj package
	 *
	 * @param filename
	 * @return A GEDCOM object
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private Gedcom readGedcom(String filename) throws IOException, GedcomParserException {
		final GedcomParser gp = new GedcomParser();
		gp.load(filename);
		return gp.getGedcom();
	}
}
