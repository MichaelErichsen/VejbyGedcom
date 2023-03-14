package net.myerichsen.gedcom.db.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a relocation
 *
 * @author Michael Erichsen
 * @version 13. mar. 2023
 *
 */
public class Relocation {
	private String id = "";
	private String givenName = "";
	private String surName = "";
	private String date = "";
	private String place = "";
	private String note = "";
	private String sourceDetail = "";
	private int year = 0;
	private String birthYear = "";

	/**
	 * Constructor
	 *
	 * @param id
	 * @param givenName
	 * @param surName
	 * @param date
	 * @param place
	 * @param note
	 * @param sourceDetail
	 */
	public Relocation(String id, String givenName, String surName, String date, String place, String note,
			String sourceDetail) {
		this.id = id;
		this.givenName = givenName;
		this.surName = surName;
		this.date = date;
		this.place = place;
		this.note = note;
		this.sourceDetail = sourceDetail;
		final Pattern pattern = Pattern.compile("\\d{4}");
		final Matcher matcher = pattern.matcher(date);

		if (matcher.find()) {
			year = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @return the birthYear
	 */
	public String getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the sourceDetail
	 */
	public String getSourceDetail() {
		return sourceDetail;
	}

	/**
	 * @return the surName
	 */
	public String getSurName() {
		return surName;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param birthYear the birthYear to set
	 */
	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param sourceDetail the sourceDetail to set
	 */
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	/**
	 * @param surName the surName to set
	 */
	public void setSurName(String surName) {
		this.surName = surName;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return id + ";" + givenName + ";" + surName + ";" + date + ";" + place + ";" + note + ";" + sourceDetail + ";"
				+ birthYear;

	}
}
