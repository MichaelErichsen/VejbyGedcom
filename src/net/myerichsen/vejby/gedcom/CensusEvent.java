package net.myerichsen.vejby.gedcom;

import java.util.List;

import net.myerichsen.vejby.census.Household;

/**
 * A census event as extracted from a KIP file.
 * 
 * @author Michael Erichsen
 * @version 17. aug. 2020
 *
 */
public class CensusEvent {
	private int censusYear;
	private String place;
	private Household household;

	/**
	 * Constructor
	 *
	 * @param censusYear
	 * @param place
	 * @param household
	 */
	public CensusEvent(int censusYear, String place, Household household) {
		super();
		this.censusYear = censusYear;
		this.place = place;
		this.household = household;
	}

	/**
	 * @return the censusYear
	 */
	public int getCensusYear() {
		return censusYear;
	}

	/**
	 * @return the household
	 */
	public Household getHousehold() {
		return household;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param censusYear
	 *            the censusYear to set
	 */
	public void setCensusYear(int censusYear) {
		this.censusYear = censusYear;
	}

	/**
	 * @param household
	 *            the household to set
	 */
	public void setHousehold(Household household) {
		this.household = household;
	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @return
	 */
	public String toGedcom() {
		StringBuilder sb = new StringBuilder();
		sb.append("1 CENS\n");
		sb.append("2 DATE " + censusYear + "\n");
		sb.append("2 PLAC " + place + "\n");
		sb.append("2 SOUR @S1@\n");
		sb.append("3 PAGE \n");

		List<List<String>> rows = household.getRows();
		for (List<String> list : rows) {
			sb.append("4 CONT ");
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i) + ", ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
