package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.PolregRecord;

/**
 * Filter for birth date column in polreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class PolregBirthdateFilter extends ViewerFilter {
	private static PolregBirthdateFilter filter = null;

	/**
	 * @return
	 */
	public static PolregBirthdateFilter getInstance() {
		if (filter == null) {
			filter = new PolregBirthdateFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private PolregBirthdateFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final PolregRecord cr = (PolregRecord) element;

		if (cr.getBirthDate().toString().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
