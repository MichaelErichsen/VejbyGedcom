package net.myerichsen.archivesearcher.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.archivesearcher.models.SiblingsModel;

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
		final SiblingsModel o1 = (SiblingsModel) e1;
		final SiblingsModel o2 = (SiblingsModel) e2;
		final String key1 = o1.getBirthYear() + o1.getName();
		final String key2 = o2.getBirthYear() + o2.getName();
		return key1.compareToIgnoreCase(key2);
	}
}
