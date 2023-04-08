package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.BurregModel;

/**
 * Filter for given name column in burreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class BurregGivenFilter extends ViewerFilter {
	private static BurregGivenFilter filter = null;

	/**
	 * @return
	 */
	public static BurregGivenFilter getInstance() {
		if (filter == null) {
			filter = new BurregGivenFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private BurregGivenFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final BurregModel cr = (BurregModel) element;

		if (cr.getFirstNames().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
