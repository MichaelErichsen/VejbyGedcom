package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */
public class MilrollIDFilter extends ViewerFilter {
	private static MilrollIDFilter filter = null;

	/**
	 * @return
	 */
	public static MilrollIDFilter getInstance() {
		if (filter == null) {
			filter = new MilrollIDFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private MilrollIDFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final MilRollEntryModel cr = (MilRollEntryModel) element;

		if (cr.getGedcomId().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
