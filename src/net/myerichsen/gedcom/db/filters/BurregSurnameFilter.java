package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.BurregModel;

/**
 * Filter for surname column in burreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class BurregSurnameFilter extends ViewerFilter {
	private static BurregSurnameFilter filter = null;

	/**
	 * @return
	 */
	public static BurregSurnameFilter getInstance() {
		if (filter == null) {
			filter = new BurregSurnameFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private BurregSurnameFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final BurregModel cr = (BurregModel) element;

		if (cr.getLastName().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
