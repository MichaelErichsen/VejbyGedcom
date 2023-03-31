package net.myerichsen.gedcom.db.util;

import java.util.Comparator;

import net.myerichsen.gedcom.db.models.RelocationRecord;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */
public class RelocationComparator implements Comparator<RelocationRecord> {

	/**
	 * Sort in ascending order of given name, surname, birth date, and relocation
	 * date
	 */
	@Override
	public int compare(RelocationRecord o1, RelocationRecord o2) {
		final String key1 = o1.getGivenName() + o1.getSurName() + o1.getBirthDate().toString() + o1.getDate();
		final String key2 = o2.getGivenName() + o1.getSurName() + o2.getBirthDate().toString() + o2.getDate();
		return key1.compareToIgnoreCase(key2);
	}
}
