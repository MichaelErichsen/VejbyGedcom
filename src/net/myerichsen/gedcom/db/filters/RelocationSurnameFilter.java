package net.myerichsen.gedcom.db.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import net.myerichsen.gedcom.db.models.RelocationRecord;

/**
 * Filter for surname column in relocation table (Singleton)
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class RelocationSurnameFilter extends ViewerFilter {
	private static RelocationSurnameFilter filter = null;

	/**
	 * @return
	 */
	public static RelocationSurnameFilter getInstance() {
		if (filter == null) {
			filter = new RelocationSurnameFilter();
		}

		return filter;

	}

	private String searchString;

	/**
	 * Constructor
	 *
	 */
	private RelocationSurnameFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ((searchString == null) || (searchString.length() == 0)) {
			return true;
		}

		final RelocationRecord cr = (RelocationRecord) element;

		if (cr.getSurName().toLowerCase().matches(searchString)) {
			return true;
		}

		return false;
	}

	public void setSearchText(String s) {
		this.searchString = ".*" + s.toLowerCase() + ".*";
	}

}
