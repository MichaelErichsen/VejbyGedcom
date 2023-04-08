package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * Filter for age column in census table (Singleton)
 *
 * @author Michael Erichsen
 * @version 5. apr. 2023
 *
 */
public class CensusAgeFilter extends ViewerFilter {
	private static CensusAgeFilter filter = null;

	/**
	 * @return
	 */
	public static CensusAgeFilter getInstance() {
		if (filter == null) {
			filter = new CensusAgeFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private CensusAgeFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final CensusModel cr = (CensusModel) element;

		int diff = cr.getAlder() - Integer.parseInt(searchString);
		if (Math.abs(diff) < 2) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = s;
	}

}
