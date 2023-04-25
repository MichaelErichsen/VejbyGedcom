package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * Filter for birth Date column in census table (Singleton)
 * 
 * @author Michael Erichsen
 * @version 25. apr. 2023
 *
 */
public class CensusBirthDateFilter extends ViewerFilter {
	private static CensusBirthDateFilter filter = null;

	/**
	 * @return
	 */
	public static CensusBirthDateFilter getInstance() {
		if (filter == null) {
			filter = new CensusBirthDateFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private CensusBirthDateFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final CensusModel cr = (CensusModel) element;

		if (cr.getFoedt_kildedato().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
	}

}
