package net.myerichsen.vejby.util;

import java.util.Arrays;
import java.util.List;

/**
 * Contains mapping pairs between census file, mappingKeyss, census events and
 * birth events.
 * 
 * @author Michael Erichsen
 * @version 20. aug. 2020
 *
 */
public class Mapping {
	private int[] mappingKeys = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private List<String> nameStubs = Arrays.asList("Dat", "Datter", "Dat.", "Dtr", "Dr");

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

//	/**
//	 * @return the othersMap
//	 */
//	public List<String> getOthersMap() {
//		return othersMap;
//	}
//
//	/**
//	 * @return the wifeMap
//	 */
//	public List<String> getWifeMap() {
//		return wifeMap;
//	}

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
				+ getMappingKeys()[9] + " " + getMappingKeys()[10] + " " + getMappingKeys()[11] + " "
				+ getMappingKeys()[12];
	}

}