package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.HouseholdHeadModel;

/**
 * Filter for relocator name column in relocation table (Singleton)
 *
 * @author Michael Erichsen
 * @version 14. apr. 2023
 *
 */
public class HouseholdHeadNameFilter extends ViewerFilter {
	private static HouseholdHeadNameFilter filter = null;

	/**
	 * @return
	 */
	public static HouseholdHeadNameFilter getInstance() {
		if (filter == null) {
			filter = new HouseholdHeadNameFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private HouseholdHeadNameFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final HouseholdHeadModel hhr = (HouseholdHeadModel) element;

		if (hhr.getRelocatorName().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
