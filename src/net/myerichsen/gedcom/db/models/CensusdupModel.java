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
 * @author Michael Erichsen
 * @version 19. apr. 2023
 *
 */
public class CensusdupModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM VEJBY.EVENT WHERE TYPE = 'Census'";

	/**
	 * Get a list of objects from the database
	 *
	 * @param schema
	 * @param dbPath
	 * @return
	 * @throws SQLException
	 */
	public static CensusdupModel[] load(String schema, String dbPath) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT);
		final ResultSet rs = statement.executeQuery();
		CensusdupModel cd;
		final List<CensusdupModel> lcd = new ArrayList<>();

		while (rs.next()) {
			cd = new CensusdupModel();
			cd.setIndividual(rs.getString("INDIVIDUAL").trim());
			cd.setDate(rs.getDate("DATE"));
			cd.setPlace(rs.getString("PLACE"));
			cd.setSourceDetail(rs.getString("SOURCEDETAIL"));
			lcd.add(cd);
		}

		statement.close();
		conn.close();

		final List<CensusdupModel> lcd2 = new ArrayList<>();

		for (int i = 0; i < lcd.size(); i++) {
			for (int j = i + 1; j < lcd.size(); j++) {
				if (lcd.get(i).equals(lcd.get(j))) {
					lcd2.add(lcd.get(i));
				}
			}
		}

		final CensusdupModel[] cda = new CensusdupModel[lcd2.size()];

		for (int i = 0; i < lcd2.size(); i++) {
			cda[i] = lcd2.get(i);
		}

		return cda;
	}

	private String individual;
	private Date date;
	private String place;
	private String sourceDetail;

	@Override
	public boolean equals(Object obj) {
		final CensusdupModel cd = (CensusdupModel) obj;
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
		return "CensusData [individual=" + individual + ", date=" + date + ", place=" + place + ", sourceDetail="
				+ sourceDetail + "]";
	}

	@Override
	public String[] toStringArray() {
		final String[] sa = new String[4];
		sa[0] = individual;
		sa[1] = date.toString();
		sa[2] = place;
		sa[3] = sourceDetail;
		return sa;
	}

}
