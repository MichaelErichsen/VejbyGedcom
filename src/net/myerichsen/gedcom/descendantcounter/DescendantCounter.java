package net.myerichsen.gedcom.descendantcounter;

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
 * Main class for a GEDCOM decendant counter. It reads a GEDCOM file and finds
 * the ancestors with most descendants.
 *
 * @author Michael Erichsen
 * @version 07-05-2022
 *
 */
public class DescendantCounter {

	/**
	 * @param iwccMap
	 *
	 */
	private static void listAncestors(Map<String, IndividualWithChildCount> sorted) {
		for (final Entry<String, IndividualWithChildCount> entry : sorted.entrySet()) {
			final IndividualWithChildCount iwcc = entry.getValue();
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
			final Gedcom gedcom = readGedcom(args[0]);
			final Map<String, IndividualWithChildCount> iwccMap = populateAncestors(gedcom);
			final TreeMap<String, IndividualWithChildCount> sorted = new TreeMap<>(iwccMap);
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
		final Map<String, Individual> individuals = gedcom.getIndividuals();
		final Map<String, IndividualWithChildCount> iwccMap = new HashMap<>();

		for (final Entry<String, Individual> individual : individuals.entrySet()) {
			final String key = individual.getKey();
			final Individual value = individual.getValue();
			final int size = value.getDescendants().size();

			if (size > 50) {
				final IndividualWithChildCount iwcc = new IndividualWithChildCount(key, value, size);
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
		final GedcomParser gp = new GedcomParser();
		gp.load(filename);
		return gp.getGedcom();
	}

}
