package net.myerichsen.archivesearcher.comparators;

import java.util.Comparator;

import net.myerichsen.archivesearcher.models.HouseholdHeadModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 31. maj 2023
 *
 */
public final class HouseholdHeadSetComparator implements Comparator<HouseholdHeadModel> {

	/**
	 * Sort in ascending order of date date
	 */
	@Override
	public int compare(HouseholdHeadModel o1, HouseholdHeadModel o2) {
		return o1.getEventDate().toString().compareTo(o2.getEventDate().toString());
	}
}
