package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing roll data for a military roll
 *
 * @author Michael Erichsen
 * @version 11. maj 2023
 *
 */

public class MilrollListModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String LOAD = "SELECT * FROM LAEGD ORDER BY AMT ASC, AAR ASC, LITRA ASC, LAEGDNR ASC";
	private static final String SELECT = "SELECT * FROM LAEGD.LAEGD WHERE LAEGDID = ? ";
	private static final String INSERT = "INSERT INTO LAEGD ( AMT, AAR, LITRA, RULLETYPE, LAEGDNR, PARISH, LAEGDID, "
			+ "NEXTLAEGDID, PREVLAEGDID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE LAEGD SET AMT = ?, AAR = ?, LITRA = ?, RULLETYPE = ?, "
			+ "LAEGDNR = ?, PARISH = ?, NEXTLAEGDID = ?, PREVLAEGDID =? WHERE LAEGDID = ?";
	private static final String DELETE = "DELETE FROM LAEGD WHERE LAEGDID = ?";

	/**
	 * @param dbPath
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public static MilrollListModel[] load(String dbPath, String schema) throws SQLException {
		final List<MilrollListModel> lmrlm = new ArrayList<>();
		MilrollListModel m;

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(LOAD);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			m = new MilrollListModel();
			m.setAmt(rs.getString("AMT").trim());
			m.setAar(rs.getInt("AAR"));
			m.setLitra(rs.getString("LITRA"));
			m.setRulleType(rs.getString("RULLETYPE").trim());
			m.setLaegdNr(rs.getInt("LAEGDNR"));
			m.setSogn(rs.getString("PARISH").trim());
			m.setLaegdId(rs.getInt("LAEGDID"));
			m.setNextLaegdId(rs.getInt("NEXTLAEGDID"));
			m.setPrevLaegdId(rs.getInt("PREVLAEGDID"));

			lmrlm.add(m);
		}

		final MilrollListModel[] mrlma = new MilrollListModel[lmrlm.size()];

		for (int i = 0; i < lmrlm.size(); i++) {
			mrlma[i] = lmrlm.get(i);

		}
		return mrlma;
	}

	/**
	 * @param dbPath
	 * @param schema
	 * @param laegdId
	 * @return
	 * @throws SQLException
	 */
	public static MilrollListModel select(String dbPath, String schema, int laegdId) throws SQLException {
		MilrollListModel m = new MilrollListModel();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(SELECT);
		statement.setInt(1, laegdId);
		final ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			m = new MilrollListModel();
			m.setAmt(rs.getString("AMT").trim());
			m.setAar(rs.getInt("AAR"));
			m.setLitra(rs.getString("LITRA"));
			m.setRulleType(rs.getString("RULLETYPE").trim());
			m.setLaegdNr(rs.getInt("LAEGDNR"));
			m.setSogn(rs.getString("PARISH").trim());
			m.setLaegdId(rs.getInt("LAEGDID"));
			m.setNextLaegdId(rs.getInt("NEXTLAEGDID"));
			m.setPrevLaegdId(rs.getInt("PREVLAEGDID"));
		}

		return m;
	}

	private String amt = "";
	private int aar = 0;
	private String litra = " ";
	private String rulleType = "Hovedrulle";
	private int laegdNr = 0;
	private String sogn = "";
	private int laegdId = 0;
	private int nextLaegdId = 0;
	private int prevLaegdId = 0;

	/**
	 * @param dbPath
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public int delete(String dbPath, String schema) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(DELETE);
		statement.setInt(1, laegdId);
		final int executeUpdate = statement.executeUpdate();
		return executeUpdate;
	}

	/**
	 * @return the aar
	 */
	public int getAar() {
		return aar;
	}

	/**
	 * @return the amt
	 */
	public String getAmt() {
		return amt;
	}

	/**
	 * @return the laegdId
	 */
	public int getLaegdId() {
		return laegdId;
	}

	/**
	 * @return the laegdNr
	 */
	public int getLaegdNr() {
		return laegdNr;
	}

	/**
	 * @return the litra
	 */
	public String getLitra() {
		return litra;
	}

	/**
	 * @return the nextLaegdId
	 */
	public int getNextLaegdId() {
		return nextLaegdId;
	}

	/**
	 * @return the prevLaegdId
	 */
	public int getPrevLaegdId() {
		return prevLaegdId;
	}

	/**
	 * @return the rulleType
	 */
	public String getRulleType() {
		return rulleType;
	}

	/**
	 * @return the sogn
	 */
	public String getSogn() {
		return sogn;
	}

	/**
	 * @param dbPath
	 * @param schema
	 * @throws SQLException
	 */
	public int insert(String dbPath, String schema) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(INSERT);
		statement.setString(1, amt);
		statement.setInt(2, aar);
		statement.setString(3, litra);
		statement.setString(4, rulleType);
		statement.setInt(5, laegdNr);
		statement.setString(6, sogn);
		statement.setInt(7, laegdId);
		statement.setInt(8, nextLaegdId);
		statement.setInt(9, nextLaegdId);

		final int executeUpdate = statement.executeUpdate();
		return executeUpdate;

	}

	/**
	 * @param aar the aar to set
	 */
	public void setAar(int aar) {
		this.aar = aar;
	}

	/**
	 * @param amt the amt to set
	 */
	public void setAmt(String amt) {
		this.amt = amt;
	}

	/**
	 * @param laegdId the laegdId to set
	 */
	public void setLaegdId(int laegdId) {
		this.laegdId = laegdId;
	}

	/**
	 * @param laegdNr the laegdNr to set
	 */
	public void setLaegdNr(int laegdNr) {
		this.laegdNr = laegdNr;
	}

	/**
	 * @param litra the litra to set
	 */
	public void setLitra(String litra) {
		this.litra = litra;
	}

	/**
	 * @param nextLaegdId the nextLaegdId to set
	 */
	public void setNextLaegdId(int nextLaegdId) {
		this.nextLaegdId = nextLaegdId;
	}

	/**
	 * @param prevLaegdId the prevLaegdId to set
	 */
	public void setPrevLaegdId(int prevLaegdId) {
		this.prevLaegdId = prevLaegdId;
	}

	/**
	 * @param rulleType the rulleType to set
	 */
	public void setRulleType(String rulleType) {
		this.rulleType = rulleType;
	}

	/**
	 * @param sogn the sogn to set
	 */
	public void setSogn(String sogn) {
		this.sogn = sogn;
	}

	/**
	 * @param dbPath
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public int update(String dbPath, String schema) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(UPDATE);
		statement.setString(1, amt);
		statement.setInt(2, aar);
		statement.setString(3, litra);
		statement.setString(4, rulleType);
		statement.setInt(5, laegdNr);
		statement.setString(6, sogn);
		statement.setInt(7, nextLaegdId);
		statement.setInt(8, nextLaegdId);
		statement.setInt(9, laegdId);
		final int executeUpdate = statement.executeUpdate();
		return executeUpdate;
	}
}
