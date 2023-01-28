package net.myerichsen.gedcom.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing some individual data
 *
 * @author Michael Erichsen
 * @version 2023-01-28
 *
 */
public class DBIndividual {
	private String id = "";
	private String name = "";
	private int birthYear = 0;
	private int deathYear = 9999;
	private String birthPlace = "";

	/**
	 * Constructor
	 *
	 * @param statement
	 * @param id
	 * @throws SQLException
	 */
	public DBIndividual(Statement statement, String id) throws SQLException {
		super();
		this.setId("@I" + id + "@");

		String query = "SELECT GIVENNAME, SURNAME from VEJBY.INDIVIDUAL WHERE ID ='" + this.id + "'";

		ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			name = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();
		}

		query = "SELECT ID, TYPE, DATE, PLACE,INDIVIDUAL FROM VEJBY.EVENT WHERE INDIVIDUAL = '" + this.id
				+ "' AND ( TYPE = 'Birth' OR TYPE = 'Christening') ORDER BY DATE";

		rs = statement.executeQuery(query);

		if (rs.next()) {
			birthYear = getYearFromDate(rs.getString("DATE"));
			birthPlace = rs.getString("PLACE");
		}

		query = "SELECT ID, TYPE, DATE, INDIVIDUAL FROM VEJBY.EVENT WHERE INDIVIDUAL = '" + this.id
				+ "' AND ( TYPE = 'Death' OR TYPE = 'Burial') ORDER BY DATE";

		rs = statement.executeQuery(query);

		if (rs.next()) {
			deathYear = getYearFromDate(rs.getString("DATE"));
		}

	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @return the birthYear
	 */
	public int getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the deathYear
	 */
	public int getDeathYear() {
		return deathYear;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get year in integer format
	 *
	 * @param date
	 * @return
	 */
	private int getYearFromDate(String date) {
		final Pattern r = Pattern.compile("\\d{4}");

		final Matcher m = r.matcher(date);

		if (m.find()) {
			return Integer.parseInt(m.group(0));
		}
		return 1;
	}

	/**
	 * @param birthPlace
	 *            the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @param birthYear
	 *            the birthYear to set
	 */
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * @param deathYear
	 *            the deathYear to set
	 */
	public void setDeathYear(int deathYear) {
		this.deathYear = deathYear;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
