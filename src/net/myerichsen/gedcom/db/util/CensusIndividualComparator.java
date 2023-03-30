package net.myerichsen.gedcom.db.util;

import java.util.Comparator;

import net.myerichsen.gedcom.db.models.CensusIndividual;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */
public class CensusIndividualComparator implements Comparator<CensusIndividual> {

	/**
	 * Sort in ascending order of birth year and location
	 */
	@Override
	public int compare(CensusIndividual o1, CensusIndividual o2) {
		final String key1 = o1.getCompString();
		final String key2 = o2.getCompString();
		return key1.compareToIgnoreCase(key2);
	}
}
