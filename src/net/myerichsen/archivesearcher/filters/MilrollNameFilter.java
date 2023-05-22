package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */
public class MilrollNameFilter extends ViewerFilter {
	private static MilrollNameFilter filter = null;

	/**
	 * @return
	 */
	public static MilrollNameFilter getInstance() {
		if (filter == null) {
			filter = new MilrollNameFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private MilrollNameFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final MilRollEntryModel cr = (MilRollEntryModel) element;

		if (cr.getNavn().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
