package net.myerichsen.archivesearcher.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;

/**
 * Class representing an individual with count of descendants
 *
 * @author Michael Erichsen
 * @version 22. apr. 2023
 *
 */
public class DescendantModel extends ASModel {
	/**
	 * @param string
	 * @return
	 * @throws GedcomParserException
	 * @throws IOException
	 */
	public static DescendantModel[] load(String gedcomFileName) throws IOException, GedcomParserException {
		final Gedcom gedcom = readGedcom(gedcomFileName);
		final Map<String, IndividualWithChildCount> iwccMap = populateAncestors(gedcom);

		DescendantModel dm;
		final DescendantModel[] dma = new DescendantModel[iwccMap.size()];
		int counter = 0;

		for (final Map.Entry<String, IndividualWithChildCount> iwcc : iwccMap.entrySet()) {
			dm = new DescendantModel();
			dm.setDescendantCount(iwcc.getValue().getDescendantCount());
			dm.setId(iwcc.getValue().getXref().replace("@", "").replace("I", ""));
			dm.setName(iwcc.getValue().getFormattedName().replace("/", ""));
			dma[counter] = dm;
			counter++;
		}

		return dma;
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
			final List<FamilyChild> familiesWhereChild = value.getFamiliesWhereChild();

			if (familiesWhereChild != null) {
				continue;
			}

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

	private int descendantCount = 0;
	private String id;
	private String name;

	/**
	 * @return the descendantCount
	 */
	public int getDescendantCount() {
		return descendantCount;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param descendantCount the descendantCount to set
	 */
	public void setDescendantCount(int descendantCount) {
		this.descendantCount = descendantCount;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("%08d %s; %s", descendantCount, id, name);
	}

}
