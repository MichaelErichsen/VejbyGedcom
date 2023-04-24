package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.SiblingsModel;

/**
 * Filter for place column in siblings table (Singleton)
 *
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class SiblingsPlaceFilter extends ViewerFilter {
	private static SiblingsPlaceFilter filter = null;

	/**
	 * @return
	 */
	public static SiblingsPlaceFilter getInstance() {
		if (filter == null) {
			filter = new SiblingsPlaceFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private SiblingsPlaceFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final SiblingsModel cr = (SiblingsModel) element;

		if (cr.getPlace().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";

	}

}
