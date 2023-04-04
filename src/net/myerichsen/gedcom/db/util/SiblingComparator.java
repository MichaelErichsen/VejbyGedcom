package net.myerichsen.gedcom.db.util;

import java.util.Comparator;

import net.myerichsen.gedcom.db.models.SiblingRecord;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class SiblingComparator implements Comparator<SiblingRecord> {

	/**
	 * Sort in ascending order of given name, surname, birth date, and relocation
	 * date
	 */
	@Override
	public int compare(SiblingRecord o1, SiblingRecord o2) {
		final String key1 = o1.getBirthDate().toString() + o1.getName();
		final String key2 = o2.getBirthDate().toString() + o2.getName();
		return key1.compareToIgnoreCase(key2);
	}
}
