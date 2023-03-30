package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Erichsen
 * @version 30. mar. 2023
 *
 */
public class Probate {
	private static final String SELECT_PROBATE = "SELECT * FROM GEDCOM.EVENT "
			+ "JOIN GEDCOM.INDIVIDUAL ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID "
			+ "WHERE GEDCOM.INDIVIDUAL.FONKOD = ? AND GEDCOM.EVENT.FROMDATE >= ? AND TODATE <= ?";

	public static List<Probate> loadFromDatabase(String dbPath, String phonName, String birthDate, String deathDate,
			String probateSource) throws SQLException {
		Probate probate;
		final List<Probate> lp = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		final PreparedStatement statement = conn.prepareStatement(SELECT_PROBATE);
		statement.setString(1, phonName);
		statement.setString(2, birthDate);
		statement.setString(3, deathDate);
		final ResultSet rs = statement.executeQuery();
		String name;
		String source;
		String data;

		while (rs.next()) {
			probate = new Probate();
			name = rs.getString("NAME").trim();
			probate.setName(name);
			data = rs.getString("COVERED_DATA").replaceAll("\\r\\n", " ¤ ");
			probate.setData(data);
			source = rs.getString("SOURCE");
			probate.setSource(source);

			if ((source.contains(probateSource) && (data.contains(name)))) {
				probate.setFromDate(rs.getString("FROMDATE").trim());
				probate.setToDate(rs.getString("TODATE").trim());
				probate.setPlace(rs.getString("PLACE").trim());
				lp.add(probate);
			}
		}

		statement.close();

		return lp;
	}

	private String name = "";

	private String fromDate = "";

	private String toDate = "";

	private String place = "";

	private String data = "";

	private String source = "";

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	/**
	 * return the object as a String Array
	 * 
	 * @return
	 */
	public String[] toStringArray() {
		final String[] sa = new String[6];
		sa[0] = name;
		sa[1] = fromDate;
		sa[2] = toDate;
		sa[3] = place;
		sa[4] = data;
		sa[5] = source;
		return sa;
	}
}
