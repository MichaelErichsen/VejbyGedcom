package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a census duplicate
 *
 * @author Michael Erichsen
 *
 */
public class CensusDupModel extends ASModel {
	/*
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM EVENT WHERE TYPE = 'Census'";

	/**
	 * Get a list of objects from the database
	 *
	 * @param schema
	 * @param dbPath
	 * @return
	 * @throws SQLException
	 */
	public static CensusDupModel[] load(String schema, String dbPath) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT);
		final ResultSet rs = statement.executeQuery();
		CensusDupModel model;
		final List<CensusDupModel> list = new ArrayList<>();

		while (rs.next()) {
			model = new CensusDupModel();
			model.setIndividual(rs.getString("INDIVIDUAL").trim());
			model.setDate(rs.getDate("DATE"));
			model.setPlace(rs.getString("PLACE"));
			model.setSourceDetail(rs.getString("SOURCEDETAIL"));
			list.add(model);
		}

		statement.close();

		final List<CensusDupModel> list2 = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(i).equals(list.get(j))) {
					list2.add(list.get(i));
				}
			}
		}

		final CensusDupModel[] array = new CensusDupModel[list2.size()];

		for (int i = 0; i < list2.size(); i++) {
			array[i] = list2.get(i);
		}

		return array;
	}

	private String individual;
	private Date date;
	private String place;
	private String sourceDetail;

	@Override
	public boolean equals(Object obj) {
		final CensusDupModel cd = (CensusDupModel) obj;
		if (this.individual.equals(cd.getIndividual()) && this.date.equals(cd.getDate())) {
			return true;
		}
		return false;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the individual
	 */
	public String getIndividual() {
		return individual;
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
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param individual the individual to set
	 */
	public void setIndividual(String individual) {
		this.individual = individual;
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

	@Override
	public String toString() {
		return (individual != null ? individual.trim() + ", " : "") + (date != null ? date + ", " : "")
				+ (place != null ? place.trim() + ", " : "") + (sourceDetail != null ? sourceDetail.trim() : "");
	}

}
