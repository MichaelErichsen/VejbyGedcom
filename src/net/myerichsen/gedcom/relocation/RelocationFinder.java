package net.myerichsen.gedcom.relocation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.CitationWithSource;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find all relocations to and from a given location in a GEDCOM file. Requires
 * the non-standard "Flytning" event with the location in the place fields (to)
 * or part of the note field (from).
 * <p>
 * Parameters:
 * <ul>
 * <li>Location name</li> *
 * <li>Full path to GEDCOM file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each relocation found.
 * 
 * @author Michael Erichsen
 * @version 16. okt. 2022
 *
 */
public class RelocationFinder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: RelocationFinder location gedcomfile outputdirectory\n"
					+ "\"all\" as location selects all");
			System.exit(4);
		}

		RelocationFinder rf = new RelocationFinder();

		try {
			String outfile = rf.execute(args[0], args[1], args[2]);
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
		GedcomParser gp = new GedcomParser();
		gp.load(filename);
		return gp.getGedcom();
	}

	private String wis = "";

	/**
	 * Worker method
	 * 
	 * @param string
	 * @param string2
	 * @param string3
	 */
	private String execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		Gedcom gedcom = readGedcom(filename);

		String outfile = outputdirectory + "\\flyt_" + location + ".csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String outline = "\"ID\";\"Person\";\"Flyttedato\";\"Til\";\"Fra\";\"Kildereferencedetalje\"";
		writer.write(outline + "\n");

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Map.Entry<String, Individual> entry : individuals.entrySet()) {
			Individual value = entry.getValue();

			List<IndividualEvent> relocations = value.getEventsOfType(IndividualEventType.EVENT);
			IndividualEvent ie = null;
			String eventSubType = "";
			String date = "";
			DateTimeFormatter formatter = null;
			LocalDate localDate = null;
			String placeName = "";
			String note = "";

			for (Iterator<IndividualEvent> iterator = relocations.iterator(); iterator.hasNext();) {
				ie = iterator.next();
				eventSubType = ie.getSubType().getValue();

				if (eventSubType.equals("Flytning")) {
					date = ie.getDate().getValue();

					try {
						CitationWithSource cit = (CitationWithSource) ie.getCitations().get(0);
						wis = cit.getWhereInSource().getValue();
					} catch (Exception e2) {
						wis = "";
					}

					try {
						formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy")
								.toFormatter(Locale.ENGLISH);
						localDate = LocalDate.parse(date, formatter);
						date = localDate.toString();
					} catch (Exception e) {
						try {
							formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM yyyy")
									.toFormatter(Locale.ENGLISH);
							localDate = LocalDate.parse(date, formatter);
							date = localDate.toString();
						} catch (Exception e1) {
							date = date + "-01-01";
						}
					}

					if (ie.getPlace() != null) {
						placeName = ie.getPlace().getPlaceName();
					} else {
						placeName = "";
					}

					if (ie.getNoteStructures() != null) {
						note = ie.getNoteStructures().get(0).getLines().get(0);
					} else {
						note = "";
					}

					if ((placeName.toLowerCase().indexOf(location) > 0) || (note.toLowerCase().indexOf(location) > 0)
							|| (location.equals("all"))) {
						outline = "\"" + entry.getKey() + "\";\"" + value.toString() + "\";\"" + date + "\";\""
								+ placeName + "\";\"" + note + "\";\"" + wis + "\";";
						writer.write(outline + "\n");
					}
				}
			}

		}
		writer.flush();
		writer.close();

		return outfile;
	}

}
