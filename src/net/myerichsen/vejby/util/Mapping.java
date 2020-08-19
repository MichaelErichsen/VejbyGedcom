package net.myerichsen.vejby.util;

import java.util.Arrays;
import java.util.List;

/**
 * Contains mapping pairs between census file, mappingKeyss, census events and
 * birth events.
 * 
 * @author Michael Erichsen
 * @version 19. aug. 2020
 *
 */
public class Mapping {
	private int[] mappingKeys = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private List<String> nameStubs = Arrays.asList("Dat", "Datter");
//	private List<String> nameStubs = Arrays.asList("??", "!!", "Dat", "Datter");
	private List<String> childMap = Arrays.asList("barn", "børn", "datter", "søn");
	private List<String> wifeMap = Arrays.asList("hustru", "kone");
	private List<String> othersMap = Arrays.asList("broder", "broderdatter", "brodersøn", "brødre", "datterdatter",
			"forældre", "manden", "moder", "pleiebarn", "pleiebørn", "pleiedatter", "pleiesøn", "sønnesøn", "søster",
			"søsterdatter", "stedfader", "svigerfader", "svigerfar", "svigerforældre", "svigermoder", "svigersøn");

	/**
	 * @return the childMap
	 */
	public List<String> getChildMap() {
		return childMap;
	}

	/**
	 * @return mappingKeys An array of mapping keys
	 */
	public int[] getMappingKeys() {
		return mappingKeys;
	}

	/**
	 * @return the nameStubs
	 */
	public List<String> getNameStubs() {
		return nameStubs;
	}

	/**
	 * @return the othersMap
	 */
	public List<String> getOthersMap() {
		return othersMap;
	}

	/**
	 * @return the wifeMap
	 */
	public List<String> getWifeMap() {
		return wifeMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mapping keys: " + getMappingKeys()[0] + " " + getMappingKeys()[1] + " " + getMappingKeys()[2] + " "
				+ getMappingKeys()[3] + " " + getMappingKeys()[4] + " " + getMappingKeys()[5] + " "
				+ getMappingKeys()[6] + " " + getMappingKeys()[7] + " " + getMappingKeys()[8] + " "
				+ getMappingKeys()[9] + " " + getMappingKeys()[10] + " " + getMappingKeys()[11];
	}

}