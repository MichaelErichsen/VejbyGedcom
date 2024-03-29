package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.RelocationModel;

/**
 * Filter for given name column in relocation table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class RelocationGivenFilter extends ViewerFilter {
	private static RelocationGivenFilter filter = null;

	/**
	 * @return
	 */
	public static RelocationGivenFilter getInstance() {
		if (filter == null) {
			filter = new RelocationGivenFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private RelocationGivenFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final RelocationModel cr = (RelocationModel) element;

		if (cr.getGivenName().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
