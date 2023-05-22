package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.ProbateModel;

/**
 * Filter for source column in probate table (Singleton)
 *
 * @author Michael Erichsen
 * @version 26. apr. 2023
 *
 */
public class ProbateSourceFilter extends ViewerFilter {
	private static ProbateSourceFilter filter = null;

	/**
	 * @return
	 */
	public static ProbateSourceFilter getInstance() {
		if (filter == null) {
			filter = new ProbateSourceFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private ProbateSourceFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final ProbateModel cr = (ProbateModel) element;

		if (cr.getSource().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
