package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.BurregRecord;

/**
 * Filter for birth date column in burreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class BurregBirthDateFilter extends ViewerFilter {
	private static BurregBirthDateFilter filter = null;

	/**
	 * @return
	 */
	public static BurregBirthDateFilter getInstance() {
		if (filter == null) {
			filter = new BurregBirthDateFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private BurregBirthDateFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final BurregRecord cr = (BurregRecord) element;

		if (cr.getYearOfBirth().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
