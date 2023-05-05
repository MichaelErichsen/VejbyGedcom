package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a relocation event
 *
 * @author Michael Erichsen
 * @version 4. maj 2023
 *
 */
public class RelocationModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_RELOCATION = "SELECT INDIVIDUAL.ID, INDIVIDUAL.GIVENNAME, "
			+ "INDIVIDUAL.SURNAME, EVENT.DATE, EVENT.PLACE, EVENT.NOTE, EVENT.SOURCEDETAIL, "
			+ "INDIVIDUAL.PARENTS FROM INDIVIDUAL, EVENT WHERE EVENT.SUBTYPE = 'Flytning' "
			+ "AND INDIVIDUAL.ID = EVENT.INDIVIDUAL AND INDIVIDUAL.PHONNAME = ? "
			+ "AND INDIVIDUAL.BIRTHDATE > ? AND INDIVIDUAL.BIRTHDATE < ?";
	private static final String SELECT_BIRTHDATE = "SELECT DATE FROM EVENT WHERE INDIVIDUAL = ? "
			+ "AND (TYPE = 'Birth' OR TYPE = 'Christening')";

	/**
	 * @param dbPath
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @return
	 * @throws SQLException
	 */
	public static RelocationModel[] load(String schema, String dbPath, String phonName, String birthDate)
			throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		RelocationModel relocationRecord;
		final List<RelocationModel> lr = new ArrayList<>();

		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_RELOCATION);
		statement.setString(1, phonName);

		String a = birthDate.toString();
		final String a1 = a.substring(0, 4);
		final int b = Integer.parseInt(a1);

		String e = "";
		if (!a.equals("0001-01-01")) {
			e = a.replace(a1, Integer.toString(b - 2));
		} else {
			e = a;
		}
		if (a.equals("0001-01-01")) {
			a = "9999-12-31";
		}

		final String f = a.replace(a1, Integer.toString(b + 2));

		statement.setDate(2, Date.valueOf(e));
		statement.setDate(3, Date.valueOf(f));

		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			relocationRecord = new RelocationModel(
					rs.getString(1) == null ? "" : rs.getString(1).replace("I", "").replace("@", "").trim(),
					rs.getString(2) == null ? "" : rs.getString(2).trim(),
					rs.getString(3) == null ? "" : rs.getString(3).trim(), rs.getDate(4),
					rs.getString(5) == null ? "" : rs.getString(5).trim(),
					rs.getString(6) == null ? "" : rs.getString(6).trim(),
					rs.getString(7) == null ? "" : rs.getString(7).trim(),
					rs.getString(8) == null ? "" : rs.getString(8).trim());
			lr.add(relocationRecord);
		}

		statement.close();

		// Add birth date to each record

		statement = conn.prepareStatement(SELECT_BIRTHDATE);

		for (final RelocationModel relocation2 : lr) {
			statement.setString(1, "@I" + relocation2.getId() + "@");
			rs = statement.executeQuery();

			if (rs.next()) {
				relocation2.setBirthDate(rs.getDate("DATE"));
			}

		}

		statement.close();

		final RelocationModel[] rra = new RelocationModel[lr.size()];

		for (int i = 0; i < lr.size(); i++) {
			rra[i] = lr.get(i);
		}

		return rra;
	}

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
	public RelocationModel(String id, String givenName, String surName, Date relocationDate, String place, String note,
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
		return sourceDetail.equals("NULL") ? "" : sourceDetail;
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
