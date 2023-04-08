package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * Filter for year column in census table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class CensusYearFilter extends ViewerFilter {
	private static CensusYearFilter filter = null;

	/**
	 * @return
	 */
	public static CensusYearFilter getInstance() {
		if (filter == null) {
			filter = new CensusYearFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private CensusYearFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final CensusModel cr = (CensusModel) element;

		if (Integer.toString(cr.getFTaar()).matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
	}

}
