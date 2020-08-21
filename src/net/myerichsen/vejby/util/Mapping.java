package net.myerichsen.vejby.util;

import java.util.Arrays;
import java.util.List;

/**
 * Singleton class containing census mapping pairs and name stubs.
 * 
 * @version 21. aug. 2020
 * @author Michael Erichsen
 *
 */
public class Mapping {
	private static Mapping single_instance = null;

	/**
	 * Static method to create instance of Singleton class.
	 * 
	 * @return An instance of the class
	 */
	public static Mapping getInstance() {
		if (single_instance == null) {
			single_instance = new Mapping();
		}

		return single_instance;
	}
	private int[] mappingKeys = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private List<String> nameStubs = Arrays.asList("Dat", "Datter", "Dat.", "Dtr", "Dr");

	/**
	 * Constructor
	 *
	 */
	private Mapping() {
		super();
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