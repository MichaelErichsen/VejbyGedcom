package net.myerichsen.gedcom.db.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.CensusRecord;

/**
 * Filter for year column in census table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class CensusTableViewerFilter extends ViewerFilter {
	private static CensusTableViewerFilter filter = null;

	/**
	 * @return
	 */
	public static CensusTableViewerFilter getInstance() {
		if (filter == null) {
			filter = new CensusTableViewerFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private CensusTableViewerFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final CensusRecord cr = (CensusRecord) element;

		if (Integer.toString(cr.getFTaar()).matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
	}

}
