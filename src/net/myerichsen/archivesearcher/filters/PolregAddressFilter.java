package net.myerichsen.archivesearcher.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.archivesearcher.models.PolregModel;

/**
 * Filter for address column in polreg table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class PolregAddressFilter extends ViewerFilter {
	private static PolregAddressFilter filter = null;

	/**
	 * @return
	 */
	public static PolregAddressFilter getInstance() {
		if (filter == null) {
			filter = new PolregAddressFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private PolregAddressFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		final PolregModel cr = (PolregModel) element;

		if (cr.getFullAddress().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
