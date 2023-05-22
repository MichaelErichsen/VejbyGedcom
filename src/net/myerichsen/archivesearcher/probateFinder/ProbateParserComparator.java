package net.myerichsen.archivesearcher.probateFinder;

import java.util.Comparator;

/**
 * Helper class inplementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 9. jan. 2023
 *
 */
public class ProbateParserComparator implements Comparator<Probate> {

	/**
	 * Sort in ascending order of name and probate date
	 */
	@Override
	public int compare(Probate o1, Probate o2) {
		final String key1 = o1.getName() + " " + o1.getProbateDate();
		final String key2 = o2.getName() + " " + o2.getProbateDate();

		return key1.compareToIgnoreCase(key2);
	}

}
