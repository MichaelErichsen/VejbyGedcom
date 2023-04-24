package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.BurregModel;

/**
 * Filter for birth date column in burreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 8. apr. 2023
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
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final BurregModel cr = (BurregModel) element;

		if (cr.getYearOfBirth() == null || cr.getYearOfBirth().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
