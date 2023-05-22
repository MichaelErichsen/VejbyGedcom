package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.HouseholdHeadModel;

/**
 * Filter for event type column in relocation table (Singleton)
 *
 * @author Michael Erichsen
 * @version 14. apr. 2023
 *
 */
public class HouseholdHeadTypeFilter extends ViewerFilter {
	private static HouseholdHeadTypeFilter filter = null;

	/**
	 * @return
	 */
	public static HouseholdHeadTypeFilter getInstance() {
		if (filter == null) {
			filter = new HouseholdHeadTypeFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private HouseholdHeadTypeFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final HouseholdHeadModel hhr = (HouseholdHeadModel) element;

		if (hhr.getEventType().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = s;
	}

}
