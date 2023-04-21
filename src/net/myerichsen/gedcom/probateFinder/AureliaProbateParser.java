package net.myerichsen.gedcom.probateFinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gedcom4j.exception.GedcomParserException;

/**
 * List all probates in a file transcribed by Aurelia
 * <p>
 * Parameters:
 * <ul>
 * <li>Location name</li>
 * <li>Full path to probate transcript file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each probate found
 * <p>
 *
 * @author Michael Erichsen
 * @version 8. jan. 2023
 *
 */
public class AureliaProbateParser {
	/**
	 * Test method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: AureliaProbateParser location probatetranscript outputdirectory\n");
			System.exit(4);
		}

		final AureliaProbateParser pf = new AureliaProbateParser();

		String outfile = "";

		try {
			outfile = pf.execute(args[0].toLowerCase(), args[1], args[2]);
			System.out.println("Saved to " + outfile);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}

	}

	private List<Probate> listP = new ArrayList<>();

	/**
	 * Worker method
	 *
	 * @param filename
	 * @param args
	 * @param outputdir
	 * @return Full path of the output file
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	private String execute(String location, String filename, String outputdirectory)
			throws IOException, GedcomParserException {
		int counter = 0;

		listP = readProbate(filename);

		Collections.sort(listP, new ProbateParserComparator());

		final String outfile = outputdirectory + "\\transcript_" + location + ".csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		final String outline = "\"Person\";\"Skiftedato\";\"Sted\";\"Skifteekstrakt\"";
		writer.write(outline + "\n");

		location = location.replace("æ", ".*");
		location = location.replace("ø", ".*");
		location = location.replace("å", ".*");
		location = location.replace("Æ", ".*");
		location = location.replace("Ø", ".*");
		location = location.replace("Å", ".*").toLowerCase();
		final String pattern = ".*" + location + ".*";

		for (final Probate probate : listP) {
			if (location.equalsIgnoreCase("all") || probate.getLocation().matches(pattern)) {
				writer.write(probate + "\n");
				System.out.println(probate);
				counter++;
			}
		}

		writer.flush();
		writer.close();
		System.out.println(counter + " records written");
		return outfile;
	}

	/**
	 * Read the extract file
	 *
	 * @param probate extract filename
	 * @return List of probate objects
	 * @throws IOException
	 */
	private List<Probate> readProbate(String filename) throws IOException {
		final List<Probate> listP = new ArrayList<>();
		Probate probate = null;

		final BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder content = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {

			if (line.startsWith("*") || line.startsWith("//")) {
				if (content.length() > 0) {
					probate = new Probate(content.toString());

					if (probate.getProbateDate() != null) {
						listP.add(probate);
					}

				}
				content = new StringBuilder();
			}

			content.append(line);
		}

		reader.close();

		return listP;
	}

}
