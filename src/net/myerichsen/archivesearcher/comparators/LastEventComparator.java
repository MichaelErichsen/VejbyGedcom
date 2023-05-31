package net.myerichsen.archivesearcher.comparators;

import java.util.Comparator;

import net.myerichsen.archivesearcher.models.LastEventModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 31. maj 2023
 *
 */
public final class LastEventComparator implements Comparator<LastEventModel> {

	@Override
	public int compare(LastEventModel o1, LastEventModel o2) {
		return o1.getDate().toString().compareToIgnoreCase(o2.getDate().toString());
	}

}