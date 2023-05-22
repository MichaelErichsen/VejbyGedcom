package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.SiblingsModel;

/**
 * Filter for place column in siblings table (Singleton)
 *
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class SiblingsParentsFilter extends ViewerFilter {
	private static SiblingsParentsFilter filter = null;

	/**
	 * @return
	 */
	public static SiblingsParentsFilter getInstance() {
		if (filter == null) {
			filter = new SiblingsParentsFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private SiblingsParentsFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final SiblingsModel cr = (SiblingsModel) element;

		if (cr.getParents().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";

	}

}
