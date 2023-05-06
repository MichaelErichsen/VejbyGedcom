package net.myerichsen.gedcom.db.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class representing an entry in a military roll
 *
 * @author Michael Erichsen
 * @version 5. maj 2023
 *
 */
public class MilRollModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String INSERT = "INSERT INTO RULLE ( LAEGDID, GLLAEGDID, GLLOEBENR, LOEBENR, FADER, SOEN, FOEDESTED, ALDER, "
			+ "STOERRELSEITOMMER, OPHOLD, ANMAERKNINGER, FOEDT, GEDCOMID, NAVN, FADERFON, "
			+ "SOENFON) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE RULLE SET GLLAEGDID = ?, GLLOEBENR = ?, FADER = ?, SOEN = ?, FOEDESTED = ?, ALDER = ?, "
			+ "STOERRELSEITOMMER = ?, OPHOLD = ?, ANMAERKNINGER = ?, FOEDT = ?, GEDCOMID = ?, NAVN = ?, FADERFON = ?, SOENFON = ? "
			+ "WHERE LAEGDID = ? AND LOEBENR = ?";
	private static final String DELETE = "DELETE FROM RULLE WHERE LAEGDID = ? AND LOEBENR = ?";
//	private static final String SELECT = "SELECT * FROM RULLE ORDER BY LAEGDID ASC, LOEBENR ASC";
//	private static final String SELECT_PREVIOUS = "SELECT * FROM RULLE WHERE GLLAEGDID = ? AND GLLOEBENR = ?";

	private int laegdId = 0;
	private int glLaegdId = 0;
	private int glLoebeNr = 0;
	private int loebeNr = 0;
	private String fader = "";
	private String soen = "";
	private String foedeSted = "";
	private int alder = 0;
	private BigDecimal stoerrelseITommer = new BigDecimal(0);
	private String ophold = "";
	private String anmaerkninger = "";
	private Date foedt = null;
	private String gedcomId = "";
	private String navn = "";
	private String faderFon = "";
	private String soenFon = "";

	/**
	 * @return the alder
	 */
	public int getAlder() {
		return alder;
	}

	/**
	 * @return the anmaerkninger
	 */
	public String getAnmaerkninger() {
		return anmaerkninger;
	}

	/**
	 * @return the fader
	 */
	public String getFader() {
		return fader;
	}

	/**
	 * @return the faderFon
	 */
	public String getFaderFon() {
		return faderFon;
	}

	/**
	 * @return the foedeSted
	 */
	public String getFoedeSted() {
		return foedeSted;
	}

	/**
	 * @return the foedt
	 */
	public Date getFoedt() {
		return foedt;
	}

	/**
	 * @return the gedcomId
	 */
	public String getGedcomId() {
		return gedcomId;
	}

	/**
	 * @return the glLaegdId
	 */
	public int getGlLaegdId() {
		return glLaegdId;
	}

	/**
	 * @return the glLoebeNr
	 */
	public int getGlLoebeNr() {
		return glLoebeNr;
	}

	/**
	 * @return the laegdId
	 */
	public int getLaegdId() {
		return laegdId;
	}

	/**
	 * @return the loebeNr
	 */
	public int getLoebeNr() {
		return loebeNr;
	}

	/**
	 * @return the navn
	 */
	public String getNavn() {
		return navn;
	}

	/**
	 * @return the ophold
	 */
	public String getOphold() {
		return ophold;
	}

	/**
	 * @return the soen
	 */
	public String getSoen() {
		return soen;
	}

	/**
	 * @return the soenFon
	 */
	public String getSoenFon() {
		return soenFon;
	}

	/**
	 * @return the stoerrelseITommer
	 */
	public BigDecimal getStoerrelseITommer() {
		return stoerrelseITommer;
	}

	/**
	 * @param props
	 * @return
	 * @throws SQLException
	 */
	public String delete(Properties props) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("milrollPath"));
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, props.getProperty("milrollSchema"));
		statement.execute();

		statement = conn.prepareStatement(INSERT);
		statement.setInt(1, laegdId);
		statement.setInt(2, loebeNr);
		statement.execute();

		return "Indtastning " + loebeNr + " er rettet";
	}

	/**
	 * Save entry to Derby database
	 *
	 * @param props
	 *
	 * @throws SQLException
	 *
	 */
	public String saveToDb(Properties props) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("milrollPath"));
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, props.getProperty("milrollSchema"));
		statement.execute();

		statement = conn.prepareStatement(DELETE);
		statement.setInt(1, laegdId);
		statement.setInt(2, glLaegdId);
		statement.setInt(3, glLoebeNr);
		statement.setInt(4, loebeNr);
		statement.setString(5, fader);
		statement.setString(6, soen);
		statement.setString(7, foedeSted);
		statement.setInt(8, alder);
		statement.setBigDecimal(9, stoerrelseITommer);
		statement.setString(10, ophold);
		statement.setString(11, anmaerkninger);
		statement.setDate(12, foedt);
		statement.setString(13, gedcomId);
		statement.setString(14, navn);
		statement.setString(15, faderFon);
		statement.setString(16, soenFon);
		statement.execute();

		return "Indtastning " + loebeNr + " er slettet";
	}

	/**
	 * @param props
	 * @return
	 * @throws SQLException
	 */
	public String update(Properties props) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("milrollPath"));
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, props.getProperty("milrollSchema"));
		statement.execute();

		statement = conn.prepareStatement(UPDATE);

		statement.setInt(1, glLaegdId);
		statement.setInt(2, glLoebeNr);
		statement.setString(3, fader);
		statement.setString(4, soen);
		statement.setString(5, foedeSted);
		statement.setInt(6, alder);
		statement.setBigDecimal(7, stoerrelseITommer);
		statement.setString(8, ophold);
		statement.setString(9, anmaerkninger);
		statement.setDate(10, foedt);
		statement.setString(11, gedcomId);
		statement.setString(12, navn);
		statement.setString(13, faderFon);
		statement.setString(14, soenFon);
		statement.setInt(15, laegdId);
		statement.setInt(16, loebeNr);
		statement.execute();

		return "Indtastning " + loebeNr + " er rettet";
	}

	/**
	 * @param alder the alder to set
	 */
	public void setAlder(int alder) {
		this.alder = alder;
	}

	/**
	 * @param anmaerkninger the anmaerkninger to set
	 */
	public void setAnmaerkninger(String anmaerkninger) {
		this.anmaerkninger = anmaerkninger;
	}

	/**
	 * @param fader the fader to set
	 */
	public void setFader(String fader) {
		this.fader = fader;
	}

	/**
	 * @param faderFon the faderFon to set
	 */
	public void setFaderFon(String faderFon) {
		this.faderFon = faderFon;
	}

	/**
	 * @param foedeSted the foedeSted to set
	 */
	public void setFoedeSted(String foedeSted) {
		this.foedeSted = foedeSted;
	}

	/**
	 * @param foedt the foedt to set
	 */
	public void setFoedt(Date foedt) {
		this.foedt = foedt;
	}

	/**
	 * @param gedcomId the gedcomId to set
	 */
	public void setGedcomId(String gedcomId) {
		this.gedcomId = gedcomId;
	}

	/**
	 * @param glLaegdId the glLaegdId to set
	 */
	public void setGlLaegdId(int glLaegdId) {
		this.glLaegdId = glLaegdId;
	}

	/**
	 * @param glLoebeNr the glLoebeNr to set
	 */
	public void setGlLoebeNr(int glLoebeNr) {
		this.glLoebeNr = glLoebeNr;
	}

	/**
	 * @param laegdId the laegdId to set
	 */
	public void setLaegdId(int laegdId) {
		this.laegdId = laegdId;
	}

	/**
	 * @param loebeNr the loebeNr to set
	 */
	public void setLoebeNr(int loebeNr) {
		this.loebeNr = loebeNr;
	}

	/**
	 * @param navn the navn to set
	 */
	public void setNavn(String navn) {
		this.navn = navn;
	}

	/**
	 * @param ophold the ophold to set
	 */
	public void setOphold(String ophold) {
		this.ophold = ophold;
	}

	/**
	 * @param soen the soen to set
	 */
	public void setSoen(String soen) {
		this.soen = soen;
	}

	/**
	 * @param soenFon the soenFon to set
	 */
	public void setSoenFon(String soenFon) {
		this.soenFon = soenFon;
	}

	/**
	 * @param stoerrelseITommer the stoerrelseITommer to set
	 */
	public void setStoerrelseITommer(BigDecimal stoerrelseITommer) {
		this.stoerrelseITommer = stoerrelseITommer;
	}
}
