package net.myerichsen.gedcom.probateFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
 * @version 7. jan. 2023
 *
 */
public class ProbateFinder {
	private static DateTimeFormatter formatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println(
					"Usage: ProbateFinder location gedcomfile outputdirectory\n" + "\"all\" as location selects all");
			System.exit(4);
		}

		ProbateFinder pf = new ProbateFinder();

		String outfile = "";

		try {
			outfile = pf.execute(args[0], args[1], args[2]);
			System.out.println("Saved to " + outfile);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param location
	 * @param filename
	 * @param outputdir
	 * @return
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private String execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		Individual individual;
		List<IndividualEvent> births;
		LocalDate localDate = null;

		ArrayList<ProbatePerson> listPp = new ArrayList<>();

		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		Gedcom gedcom = readGedcom(filename);

		String outfile = outputdirectory + "\\prob_" + location + ".csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String outline = "\"Person\";\"Fødselsår\";\"Sidste sted\";\"ID\"";
		writer.write(outline + "\n");

		LocalDate ld1860 = LocalDate.parse("1860-01-01");

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Map.Entry<String, Individual> entry : individuals.entrySet()) {
			individual = entry.getValue();
			births = individual.getEventsOfType(IndividualEventType.BIRTH);

			if (births.isEmpty() || (births.size() == 0)) {
				births = individual.getEventsOfType(IndividualEventType.CHRISTENING);
			}

			if (births.isEmpty() || (births.size() == 0)) {
				System.err.println("No Birth: " + entry.getKey() + ";" + individual);
				continue;
			}

			localDate = parseProbateDate(births);

			if (localDate.isAfter(ld1860)) {
				System.err.println("Too late: " + entry.getKey() + ";" + individual);
				continue;
			}

			ProbatePerson pp = new ProbatePerson(entry);
			listPp.add(pp);
			System.out.println(pp);
		}

		Collections.sort(listPp, new ProbateFinderComparator());

		for (ProbatePerson probatePerson : listPp) {
			System.out.println(probatePerson);

			writer.write(probatePerson.toString() + "\n");
		}

		writer.flush();
		writer.close();
		return outfile;
	}

	/**
	 * @param anEvent
	 * @return
	 */
	protected LocalDate parseProbateDate(List<IndividualEvent> anEvent) {
		IndividualEvent birth = anEvent.get(0);
		String date = birth.getDate().getValue();
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter1);
		} catch (Exception e) {
			try {
				int l = date.length();
				String d2 = date.substring(l - 4);

				Year year = Year.parse(d2);
				localDate = year.atDay(1);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return localDate;

	}

	/**
	 * Read a GEDCOM file using org.gedcomj package
	 * 
	 * @param filename
	 * @return
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private Gedcom readGedcom(String filename) throws IOException, GedcomParserException {
		GedcomParser gp = new GedcomParser();
		gp.load(filename);
		return gp.getGedcom();
	}
}
