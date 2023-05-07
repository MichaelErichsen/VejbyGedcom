package net.myerichsen.gedcom.db.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.gedcom.db.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */
public class MilrollComparator extends ViewerComparator {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final MilRollEntryModel t1 = (MilRollEntryModel) e1;
		final MilRollEntryModel t2 = (MilRollEntryModel) e2;
		return t1.getLaegdId() * 1000 + t1.getLoebeNr() - (t2.getLaegdId() * 1000 + t2.getLoebeNr());
	}
}
