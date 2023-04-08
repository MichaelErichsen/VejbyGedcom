package net.myerichsen.gedcom.db.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class CensusComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		CensusModel t1 = (CensusModel) e1;
		CensusModel t2 = (CensusModel) e2;
		return (t1.getFTaar() + t1.getAmt() + t1.getHerred() + t1.getSogn())
				.compareTo((t2.getFTaar() + t2.getAmt() + t2.getHerred() + t2.getSogn()));
	}
}