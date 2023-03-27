package net.myerichsen.gedcom.db.models;

import java.sql.Date;

/**
 * Class representing a relocation event
 *
 * @author Michael Erichsen
 * @version 27. mar. 2023
 *
 */
public class Relocation {
	private String id = "";
	private String givenName = "";
	private String surName = "";
	private Date relocationDate = null;
	private String place = "";
	private String note = "";
	private String sourceDetail = "";
	private int relocationYear = 0;
	private Date birthDate = null;
	private String parents = "";

	/**
	 * Constructor
	 *
	 * @param id
	 * @param givenName
	 * @param surName
	 * @param relocationDate
	 * @param place
	 * @param note
	 * @param sourceDetail
	 */
	public Relocation(String id, String givenName, String surName, Date relocationDate, String place, String note,
			String sourceDetail, String parents) {
		this.id = id;
		this.givenName = givenName;
		this.surName = surName;
		this.relocationDate = relocationDate;
		this.place = place;
		this.note = note;
		this.sourceDetail = sourceDetail;
		this.parents = parents;

		if (relocationDate != null) {
			relocationYear = relocationDate.toLocalDate().getYear();
		}
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the relocationDate
	 */
	public Date getDate() {
		return relocationDate;
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
	 * @return the parents
	 */
	public String getParents() {
		return parents;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the relocationDate
	 */
	public Date getRelocationDate() {
		return relocationDate;
	}

	/**
	 * @return the relocationYear
	 */
	public int getRelocationYear() {
		return relocationYear;
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
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param relocationDate the relocationDate to set
	 */
	public void setDate(Date date) {
		this.relocationDate = date;
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
	 * @param parents the parents to set
	 */
	public void setParents(String parents) {
		this.parents = parents;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param relocationDate the relocationDate to set
	 */
	public void setRelocationDate(Date relocationDate) {
		this.relocationDate = relocationDate;
	}

	/**
	 * @param relocationYear the relocationYear to set
	 */
	public void setRelocationYear(int year) {
		this.relocationYear = year;
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

	@Override
	public String toString() {
		return id + ";" + givenName + ";" + surName + ";" + relocationDate + ";" + place + ";" + note + ";"
				+ sourceDetail + ";" + birthDate + ";" + parents;

	}
}
