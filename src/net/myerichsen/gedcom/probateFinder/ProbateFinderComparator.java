package net.myerichsen.gedcom.probateFinder;

import java.util.Comparator;

/**
 * Helper class inplementing Comparator interface
 * 
 * @author Michael Erichsen
 * @version 8. jan. 2023
 *
 */
public class ProbateFinderComparator implements Comparator<ProbatePerson> {

	/**
	 * Sort in ascending order of birth date
	 */
	@Override
	public int compare(ProbatePerson o1, ProbatePerson o2) {
		String key1 = o1.getIsoBirthDate() + " " + o1.getName();
		String key2 = o2.getIsoBirthDate() + " " + o2.getName();

		return key1.compareToIgnoreCase(key2);
	}

}
