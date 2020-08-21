package net.myerichsen.vejby.gedcom;

import java.util.List;

import net.myerichsen.vejby.census.Household;

/**
 * A census event as extracted from a KIP file.
 * 
 * @author Michael Erichsen
 * @version 20. aug. 2020
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
	 * @param censusYear the censusYear to set
	 */
	public void setCensusYear(int censusYear) {
		this.censusYear = censusYear;
	}

	/**
	 * @param household the household to set
	 */
	public void setHousehold(Household household) {
		this.household = household;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @return String A string containing GEDCOM output for a census event
	 */
	public String toGedcom() {
		StringBuilder sb = new StringBuilder();
		sb.append("1 CENS\n");
		sb.append("2 DATE " + censusYear + "\n");
		sb.append("2 PLAC " + place + "\n");
		sb.append("2 SOUR @S1@\n");
		sb.append("3 PAGE Folketælling " + censusYear + "\n");

		List<List<String>> rows = household.getRows();
		for (List<String> list : rows) {
			sb.append("4 CONT ");
			for (String element : list) {
				sb.append(element + ", ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
