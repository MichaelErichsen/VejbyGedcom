package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.CensusModel;

/**
 * Filter for sex column in census table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class CensusSexFilter extends ViewerFilter {
	private static CensusSexFilter filter = null;

	/**
	 * @return
	 */
	public static CensusSexFilter getInstance() {
		if (filter == null) {
			filter = new CensusSexFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private CensusSexFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final CensusModel cr = (CensusModel) element;

		if (cr.getKoen().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
