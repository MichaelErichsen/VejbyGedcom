package net.myerichsen.gedcom.matcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.enumerations.IndividualEventType;
import org.gedcom4j.parser.GedcomParser;

/**
 * Find persons with the same phonetized surname, first initial, sex, and birth
 * year
 * 
 * @author Michael Erichsen
 * @version 28. nov. 2022
 *
 */
public class BirthdateMatcher2 {
	private static Fonkod fonkod = new Fonkod();

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
		String outfile = outputdirectory + "\\BirthdatePairs.csv";
		int counter = 0;
		int person1 = 0;
		String ID1;
		Individual value1;
		List<IndividualEvent> event1;
		String birthDate1;
		String ID2;
		Individual value2;
		List<IndividualEvent> event2;
		String birthDate2;
		List<PersonalName> name1;
		String surName1;
		List<PersonalName> name2;
		String surName2;
		String sex1;
		String sex2;

		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		Gedcom gedcom = readGedcom(gedcomfile);

		Map<String, Individual> individuals = gedcom.getIndividuals();

		System.out.println("File " + gedcomfile + " read");

		for (Entry<String, Individual> individual1 : individuals.entrySet()) {
			person1++;
			ID1 = individual1.getKey();
			value1 = individual1.getValue();
			name1 = value1.getNames();
			surName1 = fonkodName(name1.get(0));

			event1 = value1.getEventsOfType(IndividualEventType.BIRTH);
			try {
				sex1 = value1.getSex().getValue();
			} catch (Exception e) {
				sex1 = "";
			}

			if ((event1 != null) && (event1.size() > 0)) {
				birthDate1 = getYear(event1.get(0).getDate().getValue());

				for (Entry<String, Individual> individual2 : individuals.entrySet()) {
					ID2 = individual2.getKey();
					value2 = individual2.getValue();
					name2 = value2.getNames();
					surName2 = fonkodName(name2.get(0));
					event2 = value2.getEventsOfType(IndividualEventType.BIRTH);
					try {
						sex2 = value2.getSex().getValue();
					} catch (Exception e) {
						sex2 = "";
					}

					if ((event2 != null) && (event2.size() > 0)) {
						birthDate2 = getYear(event2.get(0).getDate().getValue());

						if (birthDate1.equals(birthDate2) && !ID1.equals(ID2) && surName1.equals(surName2)
								&& sex1.equals(sex2)) {
							writer.write(person1 + ";" + ID1 + ";" + name1.get(0) + ";" + birthDate1 + ";" + ID2 + ";"
									+ name2.get(0) + ";" + birthDate2 + "\n");
							System.out.print(person1 + ";" + ID1 + ";" + name1.get(0) + ";" + birthDate1 + ";" + ID2
									+ ";" + name2.get(0) + ";" + birthDate2 + "\n");
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

	/**
	 * Convert PersonalName object to first character and fonkoded surname
	 * 
	 * @param name
	 * @return Fonkoded name
	 */
	private String fonkodName(PersonalName name) {
		String[] nameParts = name.getBasic().split("/");
		String surName;

		try {
			surName = fonkod.generateKey(nameParts[1]);
		} catch (Exception e) {
			surName = "";
		}

		return nameParts[0].substring(0, 1).toLowerCase() + " " + surName;
	}

	/**
	 * Return the year from the date string
	 * 
	 * @param date
	 * @return year
	 */
	private String getYear(String date) {
		String patternd = "\\d{2}\\s[A-Z]{3}\\s\\d{4}";
		String patternm = "[A-Z]{3}\\s\\d{4}";
		String patterny = "\\d{4}";

		if (date.matches(patternd)) {
			return date.substring(6);
		}

		if (date.matches(patternm)) {
			return date.substring(4);
		}

		if (date.matches(patterny)) {
			return date;
		}
		return "";
	}

}
