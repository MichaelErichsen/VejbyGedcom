package net.myerichsen.gedcom.decendantcounter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;

/**
 * Main class for a GEDCOM decendant counter
 * 
 * @author michael
 *
 */
public class DescendantCounter {

	/**
	 * @param iwccMap
	 * 
	 */
	private static void listAncestors(Map<String, IndividualWithChildCount> sorted) {
		for (Entry<String, IndividualWithChildCount> entry : sorted.entrySet()) {
			IndividualWithChildCount iwcc = entry.getValue();
			System.out.println(iwcc);
		}
		
		System.out.println("Antal: " + sorted.size());
	}

	/**
	 * @param <T>
	 * @param args
	 */
	public static <T> void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: DescendantCounter gedcomfilename");
			System.exit(4);
		}

		try {
			Gedcom gedcom = readGedcom(args[0]);
			Map<String, IndividualWithChildCount> iwccMap = populateAncestors(gedcom);
			TreeMap<String, IndividualWithChildCount> sorted = new TreeMap<String, IndividualWithChildCount>(iwccMap);
			listAncestors(sorted);
		} catch (IOException | GedcomParserException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param gedcom
	 * @return
	 */
	private static Map<String, IndividualWithChildCount> populateAncestors(Gedcom gedcom) {
		Map<String, Individual> individuals = gedcom.getIndividuals();
		Map<String, IndividualWithChildCount> iwccMap = new HashMap<String, IndividualWithChildCount>();

		for (Entry<String, Individual> individual : individuals.entrySet()) {
			String key = individual.getKey();
			Individual value = individual.getValue();
			int size = value.getDescendants().size();

			if (size > 50) {
				IndividualWithChildCount iwcc = new IndividualWithChildCount(key, value, size);
				iwccMap.put(individual.getKey(), iwcc);
			}
		}

		return iwccMap;
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
		Gedcom gedcom = gp.getGedcom();
		return gedcom;
	}

}
