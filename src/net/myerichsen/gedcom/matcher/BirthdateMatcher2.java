package net.myerichsen.gedcom.matcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find persons with the same phonetized surname, first initial, sex, and birth
 * year
 * 
 * @author Michael Erichsen
 * @version 30. nov. 2022
 *
 */
public class BirthdateMatcher2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: BirthdateMatcher2 gedcomfile outputdirectory");
			System.exit(4);
		}
		BirthdateMatcher2 bm = new BirthdateMatcher2();

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
	 * Read a GEDCOM file and find all persons with matching phonetic names and
	 * birth years
	 * 
	 * @param gedcomfile
	 * @param outputdirectory
	 * @return outfile
	 * @throws IOException
	 * @throws GedcomParserException
	 */
	private String execute(String gedcomfile, String outputdirectory) throws IOException, GedcomParserException {
		int counter = 0;

		String outfile = outputdirectory + "\\BirthdatePairs.csv";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		Gedcom gedcom = readGedcom(gedcomfile);

		Map<String, Individual> individuals = gedcom.getIndividuals();

		ArrayList<MatchPerson> listMp = new ArrayList<>();

		for (Entry<String, Individual> individual1 : individuals.entrySet()) {
			listMp.add(new MatchPerson(individual1));
		}

		Collections.sort(listMp, new BirthPhonNameComparator());

		for (int i = 0; i < listMp.size(); i++) {
			for (int j = i + 1; j < listMp.size(); j++) {
				if (listMp.get(i).equals(listMp.get(j))) {
					System.out.println(listMp.get(i) + ";" + listMp.get(j));
					writer.write(listMp.get(i).toString() + ";" + listMp.get(j) + "\n");
					counter++;
				}
			}
		}

		System.out.println("Finished writing " + counter + " matches");
		writer.flush();
		writer.close();
		return outfile;
	}
}
