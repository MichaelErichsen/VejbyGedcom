package net.myerichsen.vejby.gedcom;

/**
 * A source as extracted from a KIP file.
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class Source {
	private int id;
	private int censusYear;

	/**
	 * Constructor
	 *
	 * @param year
	 */
	private Source(int id, int censusYear) {
		super();
		this.setId(id);
		this.censusYear = censusYear;
	}

	/**
	 * @return the censusYear
	 */
	public int getCensusYear() {
		return censusYear;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param censusYear the censusYear to set
	 */
	public void setCensusYear(int censusYear) {
		this.censusYear = censusYear;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("0 @S" + id + "@ SOUR\n");
		sb.append("1 TITL Folketælling " + censusYear + "\n");
		sb.append("1 ABBR Folketælling " + censusYear + "\n");
		sb.append("1 AUTH Dansk Demografisk Database\n");
		return sb.toString();
	}

}
