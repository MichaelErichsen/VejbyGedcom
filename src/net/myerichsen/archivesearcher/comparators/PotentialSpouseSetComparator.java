package net.myerichsen.archivesearcher.comparators;

import java.util.Comparator;

import net.myerichsen.archivesearcher.models.PotentialSpouseModel;

/**
 * Helper class implementing Comparator interface
 *
 * @author Michael Erichsen
 * @version 29. jun. 2023
 *
 */
public final class PotentialSpouseSetComparator implements Comparator<PotentialSpouseModel> {

	/**
	 * Sort in ascending order of date date
	 */
	@Override
	public int compare(PotentialSpouseModel o1, PotentialSpouseModel o2) {
		final String s1 = o1.getKildenavn() + o1.getFoedt_kildedato();
		final String s2 = o2.getKildenavn() + o2.getFoedt_kildedato();
		return s1.compareTo(s2);
	}
}
