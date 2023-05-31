package net.myerichsen.archivesearcher.comparators;

import java.util.Comparator;

import net.myerichsen.archivesearcher.models.ProbateModel;

/**
 * @author Michael Erichsen
 * @version 1. jun. 2023
 *
 */
public class ProbateComparator implements Comparator<ProbateModel> {

	@Override
	public int compare(ProbateModel o1, ProbateModel o2) {
		final String s1 = o1.getSource() + o1.getPlace() + o1.getFromDate();
		final String s2 = o2.getSource() + o2.getPlace() + o2.getFromDate();
		return s1.compareTo(s2);
	}

}
