package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a spouse in a marriage
 *
 * @author Michael Erichsen
 * @version 21. jun. 2023
 *
 */
public class SpouseModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_1 = "SELECT FAMILY FROM EVENT WHERE TYPE = 'Marriage' AND INDIVIDUAL = ?";
	private static final String SELECT_2 = "SELECT INDIVIDUAL, DATE FROM EVENT WHERE TYPE = 'Marriage' AND FAMILY = ? AND INDIVIDUAL = ?";
	private static final String SELECT_3 = "SELECT GIVENNAME, SURNAME, PHONNAME, DEATHDATE FROM INDIVIDUAL WHERE ID = ?";
	private static final String FOUR_DIGITS = "\\d{4}";
	private String primaryId = "";
	private String spouseId = "";
	private String name = "";
	private String phoneticName = "";
	private int startYear = 1;
	private int endYear = 9999;

	/**
	 * Constructor
	 *
	 * @param primaryId
	 * @param spouseId
	 * @throws SQLException
	 */
	public SpouseModel(Connection conn, String schema, String primaryId, String spouseId) throws SQLException {
		this.primaryId = primaryId;
		this.spouseId = spouseId;
		final List<String> familyList = new ArrayList<>();
		String date = "";

		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		// Find all marriages
		statement = conn.prepareStatement(SELECT_1);
		statement.setString(1, primaryId);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			familyList.add(rs.getString("FAMILY"));
		}

		statement = conn.prepareStatement(SELECT_2);
		final Pattern pattern = Pattern.compile(FOUR_DIGITS);
		Matcher matcher;

		// Find right marriage
		for (final String family : familyList) {
			statement.setString(1, family);
			statement.setString(2, spouseId);
			rs = statement.executeQuery();

			if (rs.next()) {
				date = rs.getString("DATE");
				matcher = pattern.matcher(date);

				if (matcher.find()) {
					setStartYear(Integer.parseInt(matcher.group()));
				}

				break;
			}
		}

		statement = conn.prepareStatement(SELECT_3);
		statement.setString(1, spouseId);
		rs = statement.executeQuery();

		if (rs.next()) {
			phoneticName = rs.getString("PHONNAME");
			name = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();

			try {
				date = rs.getString("DEATHYEAR");
				matcher = pattern.matcher(date);

				if (matcher.find()) {
					setEndYear(Integer.parseInt(matcher.group()));
				}
			} catch (final SQLException e) {
				setEndYear(9999);
			}
		}
	}

	/**
	 * @return the endYear
	 */
	public int getEndYear() {
		return endYear;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the phoneticName
	 */
	public String getPhoneticName() {
		return phoneticName;
	}

	/**
	 * @return the primaryId
	 */
	public String getPrimaryId() {
		return primaryId;
	}

	/**
	 * @return the spouseId
	 */
	public String getSpouseId() {
		return spouseId;
	}

	/**
	 * @return the startYear
	 */
	public int getStartYear() {
		return startYear;
	}

	/**
	 * @param endYear the endYear to set
	 */
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param phoneticName the phoneticName to set
	 */
	public void setPhoneticName(String phoneticName) {
		this.phoneticName = phoneticName;
	}

	/**
	 * @param primaryId the primaryId to set
	 */
	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	/**
	 * @param spouseId the spouseId to set
	 */
	public void setSpouseId(String spouseId) {
		this.spouseId = spouseId;
	}

	/**
	 * @param i the startYear to set
	 */
	public void setStartYear(int i) {
		this.startYear = i;
	}

}
