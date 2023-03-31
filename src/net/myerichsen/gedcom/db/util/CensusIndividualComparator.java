package net.myerichsen.gedcom.db.util;

import java.util.Comparator;

import net.myerichsen.gedcom.db.models.CensusRecord;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */
public class CensusIndividualComparator implements Comparator<CensusRecord> {

	/**
	 * Sort in ascending order of birth year and location
	 */
	@Override
	public int compare(CensusRecord o1, CensusRecord o2) {
		final String key1 = o1.getCompString();
		final String key2 = o2.getCompString();
		return key1.compareToIgnoreCase(key2);
	}
}
