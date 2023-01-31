package net.myerichsen.gedcom.descendantcounter;

import org.gedcom4j.model.Individual;

/**
 * An individual with count of descendants
 *
 * @author michael
 *
 */
public class IndividualWithChildCount extends Individual implements Comparable<IndividualWithChildCount> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int descendantCount = 0;
	private String ID;

	/**
	 * @param other
	 * @param deep
	 */
	public IndividualWithChildCount(Individual other, boolean deep) {
		super(other, deep);
	}

	/**
	 * @param other
	 */
	public IndividualWithChildCount(String ID, Individual other, int count) {
		super(other);
		this.ID = ID;
		descendantCount = count;
	}

	@Override
	public int compareTo(IndividualWithChildCount iwcc) {
		final int c = iwcc.getDescendantCount();

		if (descendantCount > c) {
			return 1;
		}

		if (descendantCount < c) {
			return -1;
		}

		return 0;
	}

	/**
	 * @return the descendantCount
	 */
	public int getDescendantCount() {
		return descendantCount;
	}

	/**
	 * @param descendantCount
	 *            the descendantCount to set
	 */
	public void setDescendantCount(int descendantCount) {
		this.descendantCount = descendantCount;
	}

	@Override
	public String toString() {
		return String.format("%08d, %s, %s", descendantCount, ID, super.toString());
	}

}
