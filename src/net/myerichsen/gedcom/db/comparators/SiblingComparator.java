package net.myerichsen.gedcom.db.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.gedcom.db.models.SiblingsRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class SiblingComparator extends ViewerComparator {

	/**
	 * Sort in ascending order of birth date and name date
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		SiblingsRecord o1 = (SiblingsRecord) e1;
		SiblingsRecord o2 = (SiblingsRecord) e2;
		final String key1 = o1.getBirthYear() + o1.getName();
		final String key2 = o2.getBirthYear() + o2.getName();
		return key1.compareToIgnoreCase(key2);
	}
}
