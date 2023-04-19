package net.myerichsen.gedcom.db.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.gedcom.db.models.DescendantModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 12. apr. 2023
 *
 */
public class DescendantComparator extends ViewerComparator {
	/**
	 * Sort in ascending order of descendant count
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final DescendantModel o1 = (DescendantModel) e1;
		final DescendantModel o2 = (DescendantModel) e2;

		return o2.getDescendantCount() - o1.getDescendantCount();
	}
}
