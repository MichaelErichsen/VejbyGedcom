package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */
public class MilrollYearFilter extends ViewerFilter {
	private static MilrollYearFilter filter = null;

	/**
	 * @return
	 */
	public static MilrollYearFilter getInstance() {
		if (filter == null) {
			filter = new MilrollYearFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private MilrollYearFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final MilRollEntryModel cr = (MilRollEntryModel) element;

		if (Integer.toString(cr.getAar()).matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
