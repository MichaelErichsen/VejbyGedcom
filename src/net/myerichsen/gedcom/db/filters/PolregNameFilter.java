package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.PolregModel;

/**
 * Filter for name column in polreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 1. maj 2023
 *
 */
public class PolregNameFilter extends ViewerFilter {
	private static PolregNameFilter filter = null;

	/**
	 * @return
	 */
	public static PolregNameFilter getInstance() {
		if (filter == null) {
			filter = new PolregNameFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private PolregNameFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final PolregModel cr = (PolregModel) element;

		if (cr.getName().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
