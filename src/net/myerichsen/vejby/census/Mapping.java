package net.myerichsen.vejby.census;

/**
 * Contains mapping pairs between census file, mappingKeyss, census events and
 * birth events.
 * 
 * @author Michael Erichsen
 * @version 17. aug. 2020
 *
 */
public class Mapping {
	private int[] mappingKeys = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/**
	 * @return mappingKeys An array of mapping keys
	 */
	public int[] getMappingKeys() {
		return mappingKeys;
	}

	/**
	 * @param mappingKeys
	 *            the mappingKeys to set
	 */
	public void setMappingKeys(int[] mappingKeys) {
		this.mappingKeys = mappingKeys;
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