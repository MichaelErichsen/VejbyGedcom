package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import net.myerichsen.archivesearcher.comparators.LastEventComparator;

/**
 * Class representing the last event for a person in a location
 *
 * @author Michael Erichsen
 * @version 19. jun. 2023
 *
 */
public class LastEventModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT1 = "SELECT INDIVIDUAL, DATE FROM EVENT WHERE PLACE LIKE ?";
	private static final String SELECT2 = "SELECT MAX(DATE) AS MAXDATE FROM EVENT WHERE INDIVIDUAL = ?";
	private static final String SELECT3 = "SELECT * FROM EVENT WHERE INDIVIDUAL = ? AND DATE = ?";
	private static final String SELECT4 = "SELECT GIVENNAME, SURNAME FROM INDIVIDUAL WHERE ID = ?";

	/**
	 * @param schema
	 * @param dbPath
	 * @param location
	 * @return
	 * @throws SQLException
	 */
	public static LastEventModel[] load(String schema, String dbPath, String location) throws SQLException {
		String type = "";

		LastEventModel model;
		final List<LastEventModel> list = new ArrayList<>();

		Date maxDate;

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement1 = conn.prepareStatement(SET_SCHEMA);

		ResultSet rs1, rs2, rs3, rs4;

		statement1.setString(1, schema);
		statement1.execute();

		statement1 = conn.prepareStatement(SELECT1);
		final PreparedStatement statement2 = conn.prepareStatement(SELECT2);
		final PreparedStatement statement3 = conn.prepareStatement(SELECT3);
		final PreparedStatement statement4 = conn.prepareStatement(SELECT4);
		statement1.setString(1, "%" + location + "%");
		rs1 = statement1.executeQuery();

		while (rs1.next()) {
			model = new LastEventModel();
			model.setIndividualId(rs1.getString("INDIVIDUAL"));
			model.setDate(rs1.getDate("DATE"));

			statement2.setString(1, model.getIndividualId());
			rs2 = statement2.executeQuery();

			while (rs2.next()) {
				maxDate = rs2.getDate("MAXDATE");

				if (model.getDate() != null && model.getDate().compareTo(maxDate) == 0) {
					statement3.setString(1, model.getIndividualId());
					statement3.setDate(2, maxDate);
					rs3 = statement3.executeQuery();

					if (rs3.next()) {
						type = rs3.getString("TYPE").trim();

						if (!"Burial".equals(type)) {
							model.setType(type);
							model.setSubType(rs3.getString("SUBTYPE").trim());
							model.setSourceDetail(rs3.getString("SOURCEDETAIL").trim());

							statement4.setString(1, model.getIndividualId());
							rs4 = statement4.executeQuery();

							if (rs4.next()) {
								model.setName(
										rs4.getString("GIVENNAME").trim() + " " + rs4.getString("SURNAME").trim());
							}

							list.add(model);
						}
						break;
					}
				}

			}
		}

		final SortedSet<LastEventModel> set = new TreeSet<>(new LastEventComparator());
		set.addAll(list);

		final LastEventModel[] array = new LastEventModel[set.size()];
		int i = 0;

		for (final Iterator<LastEventModel> iterator = set.iterator(); iterator.hasNext();) {
			array[i] = iterator.next();
			i++;
		}

		return array;
	}

	private String individualId = "";

	private String name;

	private Date date = null;
	private String type = "";
	private String subType = "";
	private String sourceDetail = "";

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final LastEventModel other = (LastEventModel) obj;
		return Objects.equals(date, other.date) && Objects.equals(individualId, other.individualId)
				&& Objects.equals(name, other.name) && Objects.equals(sourceDetail, other.sourceDetail)
				&& Objects.equals(subType, other.subType) && Objects.equals(type, other.type);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the individualId
	 */
	public String getIndividualId() {
		return individualId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sourceDetail
	 */
	public String getSourceDetail() {
		return sourceDetail;
	}

	/**
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, individualId, name, sourceDetail, subType, type);
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param individualId the individualId to set
	 */
	public void setIndividualId(String individualId) {
		this.individualId = individualId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param sourceDetail the sourceDetail to set
	 */
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	/**
	 * @param subType the subType to set
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return (!individualId.isBlank() ? individualId.trim() + ", " : "") + (!name.isBlank() ? name + ", " : "")
				+ (date != null ? date + ", " : "") + (!type.isBlank() ? type + ", " : "")
				+ (!subType.isBlank() ? subType + ", " : "") + (!sourceDetail.isBlank() ? sourceDetail : "");
	}

}
