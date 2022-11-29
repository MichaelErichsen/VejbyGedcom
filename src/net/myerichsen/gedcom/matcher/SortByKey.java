package net.myerichsen.gedcom.matcher;

import java.util.Comparator;

/**
 * Helper class inplementing Comparator interface
 * 
 * @author Michael Erichsen
 * @version 29. nov. 2022
 *
 */
public class SortByKey implements Comparator<MatchPerson> {

	/**
	 * Sort in ascending order of birth year, phonetized surname and sex
	 */
	@Override
	public int compare(MatchPerson o1, MatchPerson o2) {
		String key1 = o1.getBirthYear() + " " + o1.getPhoneticName() + " " + o1.getSex();
		String key2 = o2.getBirthYear() + " " + o2.getPhoneticName() + " " + o2.getSex();

		return key1.compareToIgnoreCase(key2);
	}

}
