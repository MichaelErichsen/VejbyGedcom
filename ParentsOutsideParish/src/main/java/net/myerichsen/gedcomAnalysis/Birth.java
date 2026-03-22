package net.myerichsen.gedcomAnalysis;

/**
 * Birth part of an individual record
 *
 * @author Michael Erichsen, 2026
 */
public class Birth {
	private String dateString;

	private String place;

	/**
	 * @return
	 */
	public String getDateString() {
		return dateString;
	}

	/**
	 * @return
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param dateString
	 */
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	/**
	 * @param place
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "Birth [" + (dateString != null ? "dateString=" + dateString + ", " : "")
				+ (place != null ? "place=" + place : "") + "]";
	}
}
