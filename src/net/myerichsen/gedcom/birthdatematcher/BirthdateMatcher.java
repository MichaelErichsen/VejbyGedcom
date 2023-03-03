package net.myerichsen.gedcom.birthdatematcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find persons with the same birth date
 *
 * @author Michael Erichsen
 * @version 26. nov. 2022
 *
 */
public class BirthdateMatcher {

	private static final Pattern pattern_pattern = Pattern.compile("\\d{2}\\s[A-Z]{3}\\s\\d{4}");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: BirthdateMatcher gedcomfile outputdirectory");
			System.exit(4);
		}
		final BirthdateMatcher bm = new BirthdateMatcher();

		try {
			final String outfile = bm.execute(args[0], args[1]);
			System.out.println("Saved to " + outfile);

		} catch (final Exception e) {
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

	/**
	 * Read a GEDCOM file and find all persons with matching birthdates
	 *
	 * @param gedcomfile
	 * @param outputdirectory
	 * @return outfile
	 * @throws IOException
	 * @throws GedcomParserException
	 */
	private String execute(String gedcomfile, String outputdirectory) throws IOException, GedcomParserException {
		final String outfile = outputdirectory + "\\BirthdatePairs.csv";
		int counter = 0;
		String ID1;
		Individual value1;
		List<IndividualEvent> event1;
		String birthDate1;
		String ID2;
		Individual value2;
		List<IndividualEvent> event2;
		String birthDate2;
		final Pattern pattern = pattern_pattern;

		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		final Gedcom gedcom = readGedcom(gedcomfile);

		final Map<String, Individual> individuals = gedcom.getIndividuals();

		for (final Entry<String, Individual> individual1 : individuals.entrySet()) {
			ID1 = individual1.getKey();
			value1 = individual1.getValue();
			event1 = value1.getEventsOfType(IndividualEventType.BIRTH);

			if ((event1 != null) && (event1.size() > 0)) {
				birthDate1 = event1.get(0).getDate().getValue();

				if (!pattern.matcher(birthDate1).matches()) {
					continue;
				}

				for (final Entry<String, Individual> individual2 : individuals.entrySet()) {
					ID2 = individual2.getKey();
					value2 = individual2.getValue();
					event2 = value2.getEventsOfType(IndividualEventType.BIRTH);

					if ((event2 != null) && (event2.size() > 0)) {
						birthDate2 = event2.get(0).getDate().getValue();

						if (!pattern.matcher(birthDate2).matches()) {
							continue;
						}

						if (birthDate1.equals(birthDate2) && !ID1.equals(ID2)) {
							writer.write(ID1 + ";" + value1 + ";" + birthDate1 + ";" + ID2 + ";" + value2 + ";"
									+ birthDate2 + "\n");
							counter++;
						}
					}
				}
			}
		}

		System.out.println("Finished writing " + counter + " matches");
		writer.flush();
		writer.close();
		return outfile;

	}

}
