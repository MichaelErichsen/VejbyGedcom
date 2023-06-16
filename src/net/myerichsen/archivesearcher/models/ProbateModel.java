package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.archivesearcher.comparators.ProbateComparator;

/**
 * Class representing a probate event
 *
 * @author Michael Erichsen
 * @version 16. jun. 2023
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
		ProbateModel model;
		final List<ProbateModel> list = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		// Add five years to death date as limit for probate date
		final Pattern p = Pattern.compile("\\d{4}");
		final Matcher m = p.matcher(deathDate);

		if (m.find()) {
			final String group = m.group(0);
			int year = Integer.parseInt(group);

			if (year < 9000) {
				year = year += 5;
				final String newYear = Integer.toString(year);
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
			model = new ProbateModel();
			name = rs.getString("NAME").trim();
			model.setName(name);
			data = rs.getString("COVERED_DATA").replaceAll("\\r\\n", " ¤ ");
			model.setData(data);
			source = rs.getString("SOURCE").trim();
			model.setSource(source);

			if (data.contains(name)) {
				model.setFromDate(rs.getString("FROMDATE").trim());
				model.setToDate(rs.getString("TODATE").trim());
				model.setPlace(rs.getString("PLACE").trim());
				list.add(model);
			}
		}

		statement.close();

		final SortedSet<ProbateModel> set = new TreeSet<>(new ProbateComparator());
		set.addAll(list);

		final ProbateModel[] array = new ProbateModel[set.size()];
		int i = 0;

		for (final Iterator<ProbateModel> iterator = set.iterator(); iterator.hasNext();) {
			array[i++] = iterator.next();
		}

		return array;
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

	@Override
	public String toString() {
		return (name != null ? name.trim() + ", " : "") + (fromDate != null ? fromDate.trim() + ", " : "")
				+ (toDate != null ? toDate.trim() + ", " : "") + (place != null ? place.trim() + ", " : "")
				+ (data != null ? data.trim() + ", " : "") + (source != null ? source : "");
	}
}
