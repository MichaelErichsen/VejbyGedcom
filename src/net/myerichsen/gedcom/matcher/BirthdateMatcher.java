package net.myerichsen.gedcom.matcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find persons with the same bith date
 * 
 * @author Michael Erichsen
 * @version 26. nov. 2022
 *
 */
public class BirthdateMatcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: BirthdateMatcher gedcomfile outputdirectory");
			System.exit(4);
		}
		BirthdateMatcher bm = new BirthdateMatcher();

		try {
			String outfile = bm.execute(args[0], args[1]);
			System.out.println("Saved to " + outfile);

		} catch (Exception e) {
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

	/**
	 * Read a GEDCOM file and find all persons with mathcing birthdates
	 * 
	 * @param gedcomfile
	 * @param outputdirectory
	 * @return outfile
	 * @throws IOException
	 * @throws GedcomParserException
	 */
	private String execute(String gedcomfile, String outputdirectory) throws IOException, GedcomParserException {
		String outfile = outputdirectory + "\\BirthdatePairs.csv";
		int counter = 0;
		String ID1;
		Individual value1;
		List<IndividualEvent> event1;
		String birthDate1;
		String ID2;
		Individual value2;
		List<IndividualEvent> event2;
		String birthDate2;
		Pattern p = Pattern.compile("\\d{2}\\s[A-Z]{3}\\s\\d{4}");
		Matcher m;

		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		Gedcom gedcom = readGedcom(gedcomfile);

		Map<String, Individual> individuals = gedcom.getIndividuals();

		for (Entry<String, Individual> individual1 : individuals.entrySet()) {
			ID1 = individual1.getKey();
			value1 = individual1.getValue();
			event1 = value1.getEventsOfType(IndividualEventType.BIRTH);

			if ((event1 != null) && (event1.size() > 0)) {
				birthDate1 = event1.get(0).getDate().getValue();
				m = p.matcher(birthDate1);

				if (!m.matches()) {
					continue;
				}

				for (Entry<String, Individual> individual2 : individuals.entrySet()) {
					ID2 = individual2.getKey();
					value2 = individual2.getValue();
					event2 = value2.getEventsOfType(IndividualEventType.BIRTH);

					if ((event2 != null) && (event2.size() > 0)) {
						birthDate2 = event2.get(0).getDate().getValue();
						m = p.matcher(birthDate2);

						if (!m.matches()) {
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
