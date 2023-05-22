package net.myerichsen.archivesearcher.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.archivesearcher.models.BurregModel;

/**
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public final class BurregComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final BurregModel t1 = (BurregModel) e1;
		final BurregModel t2 = (BurregModel) e2;
		return (t1.getYearOfBirth() + t1.getLastName() + t1.getFirstNames())
				.compareToIgnoreCase(t2.getYearOfBirth() + t2.getLastName() + t2.getFirstNames());
	}
}