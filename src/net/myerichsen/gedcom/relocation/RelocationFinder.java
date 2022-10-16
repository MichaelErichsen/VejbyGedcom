package net.myerichsen.gedcom.relocation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gedcom4j.exception.GedcomParserException;
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
			System.out.println("Usage: RelocationFinder location gedcomfile outputdirectory");
			System.exit(4);
		}

		RelocationFinder rf = new RelocationFinder();

		try {
			rf.execute(args[0], args[1], args[2]);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Worker method
	 * 
	 * @param string
	 * @param string2
	 * @param string3
	 */
	private void execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {

		location = location.replace("æ", ".");
		location = location.replace("ø", ".");
		location = location.replace("å", ".");
		location = location.replace("Æ", ".");
		location = location.replace("Ø", ".");
		location = location.replace("Å", ".").toLowerCase();

		Gedcom gedcom = readGedcom(filename);

		String outfile = outputdirectory + "\\" + location + ".csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String outline = "Person;Flyttedato;Til;Fra";
		writer.write(outline + "\n");

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Map.Entry<String, Individual> entry : individuals.entrySet()) {
			Individual value = entry.getValue();

			List<IndividualEvent> relocations = value.getEventsOfType(IndividualEventType.EVENT);
			IndividualEvent ie = null;
			String eventSubType = "";
			String date = "";
			String placeName = "";
			String note = "";

			for (Iterator<IndividualEvent> iterator = relocations.iterator(); iterator.hasNext();) {
				ie = iterator.next();
				eventSubType = ie.getSubType().getValue();

				if (eventSubType.equals("Flytning")) {
					date = ie.getDate().getValue();

					if (ie.getPlace() != null)
						placeName = ie.getPlace().getPlaceName();
					else
						placeName = "";

					note = ie.getNoteStructures().get(0).getLines().get(0);

					if ((placeName.toLowerCase().indexOf(location) > 0) || (note.toLowerCase().indexOf(location) > 0)) {
						outline = value.toString() + ";" + date + ";" + placeName + ";" + note;
						writer.write(outline + "\n");
//						System.out.println(outline);
					}
				}

			}

		}
		writer.flush();
		writer.close();

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

}
