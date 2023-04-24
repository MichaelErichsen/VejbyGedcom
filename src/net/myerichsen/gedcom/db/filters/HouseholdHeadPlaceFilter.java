package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.HouseholdHeadModel;

/**
 * Filter for place column in relocation table (Singleton)
 *
 * @author Michael Erichsen
 * @version 14. apr. 2023
 *
 */
public class HouseholdHeadPlaceFilter extends ViewerFilter {
	private static HouseholdHeadPlaceFilter filter = null;

	/**
	 * @return
	 */
	public static HouseholdHeadPlaceFilter getInstance() {
		if (filter == null) {
			filter = new HouseholdHeadPlaceFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private HouseholdHeadPlaceFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final HouseholdHeadModel hhr = (HouseholdHeadModel) element;

		if (hhr.getPlace().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
