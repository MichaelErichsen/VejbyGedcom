package net.myerichsen.gedcom.db.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.ProbateRecord;

/**
 * Filter for place column in prov´bate table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class ProbatePlaceFilter extends ViewerFilter {
	private static ProbatePlaceFilter filter = null;

	/**
	 * @return
	 */
	public static ProbatePlaceFilter getInstance() {
		if (filter == null) {
			filter = new ProbatePlaceFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private ProbatePlaceFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final ProbateRecord cr = (ProbateRecord) element;

		if (cr.getPlace().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
