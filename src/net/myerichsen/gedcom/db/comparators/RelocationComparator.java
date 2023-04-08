package net.myerichsen.gedcom.db.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.gedcom.db.models.RelocationModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class RelocationComparator extends ViewerComparator {

	/**
	 * Sort in ascending order of given name, surname, birth date, and relocation
	 * date
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		RelocationModel o1 = (RelocationModel) e1;
		RelocationModel o2 = (RelocationModel) e2;

		final String key1 = o1.getGivenName() + o1.getSurName() + o1.getBirthDate().toString() + o1.getDate();
		final String key2 = o2.getGivenName() + o1.getSurName() + o2.getBirthDate().toString() + o2.getDate();
		return key1.compareToIgnoreCase(key2);
	}
}
