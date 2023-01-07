package net.myerichsen.gedcom.probateFinder;

import java.util.Comparator;

/**
 * Helper class inplementing Comparator interface
 * 
 * @author Michael Erichsen
 * @version 07. jan. 2023
 *
 */
public class ProbateFinderComparator implements Comparator<ProbatePerson> {

	/**
	 * Sort in ascending order of name and birth date
	 */
	@Override
	public int compare(ProbatePerson o1, ProbatePerson o2) {
		String key1 = o1.getName() + " " + o1.getIsoBirthDate();
		String key2 = o2.getName() + " " + o2.getIsoBirthDate();

		return key1.compareToIgnoreCase(key2);
	}

}
