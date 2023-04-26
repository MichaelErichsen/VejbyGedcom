package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a probate event
 *
 * @author Michael Erichsen
 * @version 26. apr. 2023
 *
 */
public class ProbateModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM EVENT " + "JOIN INDIVIDUAL ON EVENT.ID = INDIVIDUAL.EVENT_ID "
			+ "WHERE INDIVIDUAL.FONKOD = ? AND EVENT.FROMDATE >= ? AND TODATE <= ?";

	/**
	 * @param schema
	 * @param dbPath
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @return
	 * @throws SQLException
	 */
	public static ProbateModel[] load(String schema, String dbPath, String phonName, String birthDate, String deathDate)
			throws SQLException {
		ProbateModel probateRecord;
		final List<ProbateModel> lp = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		// Add five years to death date as limit for probate date
		Pattern p = Pattern.compile("\\d{4}");
		Matcher m = p.matcher(deathDate);

		if (m.find()) {
			String group = m.group(0);
			int year = Integer.parseInt(group);

			if (year < 9000) {
				year = year += 5;
				String newYear = Integer.toString(year);
				deathDate = deathDate.replace(group, newYear);
			}
		}

		statement = conn.prepareStatement(SELECT);
		statement.setString(1, phonName);
		statement.setString(2, birthDate);
		statement.setString(3, deathDate);
		final ResultSet rs = statement.executeQuery();
		String name;
		String source;
		String data;

		while (rs.next()) {
			probateRecord = new ProbateModel();
			name = rs.getString("NAME").trim();
			probateRecord.setName(name);
			data = rs.getString("COVERED_DATA").replaceAll("\\r\\n", " ¤ ");
			probateRecord.setData(data);
			source = rs.getString("SOURCE");
			probateRecord.setSource(source);

			if (data.contains(name)) {
				probateRecord.setFromDate(rs.getString("FROMDATE").trim());
				probateRecord.setToDate(rs.getString("TODATE").trim());
				probateRecord.setPlace(rs.getString("PLACE").trim());
				lp.add(probateRecord);
			}
		}

		statement.close();

		final ProbateModel[] pra = new ProbateModel[lp.size()];

		for (int i = 0; i < lp.size(); i++) {
			pra[i] = lp.get(i);
		}

		return pra;
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
}
