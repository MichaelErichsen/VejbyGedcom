package net.myerichsen.gedcomAnalysis;

import java.util.Set;

/**
 * Census record part of an individual record
 *
 * @author Michael Erichsen, 2026
 */
public class CensusRecord {
	private static final Set<String> faedre = Set.of("hosbonde", "husbonde", "huusfader");

	private static final Set<String> moedre = Set.of("kone");

	/**
	 * @return
	 */
	public static Set<String> getFaedre() {
		return faedre;
	}

	/**
	 * @return
	 */
	public static Set<String> getMoedre() {
		return moedre;
	}

	/**
	 * @param line
	 * @return
	 */
	public static boolean isFather(String line) {
		return faedre.contains(line.toLowerCase());
	}

	/**
	 * @param line
	 * @return
	 */
	public static boolean isMother(String line) {
		return moedre.contains(line.toLowerCase());
	}

	private String dateString;

	private String page;

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
	public String getPage() {
		return page;
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
	 * @param page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @param place
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "CensusRecord [" + (dateString != null ? "dateString=" + dateString + ", " : "")
				+ (page != null ? "page=" + page + ", " : "") + (place != null ? "place=" + place : "") + "]";
	}
}
