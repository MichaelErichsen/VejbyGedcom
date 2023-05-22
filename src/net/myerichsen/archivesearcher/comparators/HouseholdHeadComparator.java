package net.myerichsen.archivesearcher.comparators;

import java.sql.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.archivesearcher.models.HouseholdHeadModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 13. apr. 2023
 *
 */
public class HouseholdHeadComparator extends ViewerComparator {

	/**
	 * Sort in ascending order of date date
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final HouseholdHeadModel o1 = (HouseholdHeadModel) e1;
		final HouseholdHeadModel o2 = (HouseholdHeadModel) e2;

		final Date key1 = o1.getEventDate();
		final Date key2 = o2.getEventDate();
		return key1.toString().compareTo(key2.toString());
	}
}
