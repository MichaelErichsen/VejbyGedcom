package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.ProbateModel;

/**
 * Filter for place column in probate table (Singleton)
 *
 * @author Michael Erichsen
 * @version 18-08-2025 Filter fixed for "["
 *
 */
public class ProbatePlaceFilter extends ViewerFilter {
	private static ProbatePlaceFilter filter = null;

	/**
	 * @return
	 */
	public static ProbatePlaceFilter getInstance() {
		if (filter == null) {
			filter = new ProbatePlaceFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private ProbatePlaceFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final ProbateModel cr = (ProbateModel) element;

		if (cr.getPlace().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.replace("[", "").replace("]", "").toLowerCase() + ".*";
	}

}
