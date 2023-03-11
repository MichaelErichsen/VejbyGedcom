package net.myerichsen.gedcom.birthdatematcher;

import java.util.Comparator;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 30. nov. 2022
 *
 */
public class BirthPhonNameComparator implements Comparator<MatchPerson> {

	/**
	 * Sort in ascending order of birth year and phonetized surname
	 */
	@Override
	public int compare(MatchPerson o1, MatchPerson o2) {
		final String key1 = o1.getBirthYear() + " " + o1.getPhoneticName();
		final String key2 = o2.getBirthYear() + " " + o2.getPhoneticName();

		return key1.compareToIgnoreCase(key2);
	}

}
