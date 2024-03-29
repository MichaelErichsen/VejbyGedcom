package net.myerichsen.archivesearcher.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.archivesearcher.models.PolregModel;

/**
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class PolregComparator extends ViewerComparator {

	/**
	 * Sort in ascending order of birth date and name
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final PolregModel o1 = (PolregModel) e1;
		final PolregModel o2 = (PolregModel) e2;
		final String key1 = o1.getBirthDate().toString() + o1.getName();
		final String key2 = o2.getBirthDate().toString() + o2.getName();
		return key1.compareToIgnoreCase(key2);
	}
}
