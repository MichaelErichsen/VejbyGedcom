package net.myerichsen.gedcom.db.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class representing an entry in a military roll
 *
 * @author Michael Erichsen
 * @version 3. maj 2023
 *
 */
public class MilRollModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String INSERT = "INSERT INTO RULLER ( AMT, AAR, RULLETYPE, "
			+ "LAEGDNR, SOGN, LITRA, GLLOEBENR, NYLOEBENR, FADER, SOEN, FOEDESTED, ALDER, "
			+ "STOERRELSEITOMMER, OPHOLD, ANMAERKNINGER, FOEDT, GEDCOMID, NAVN, FADERFON, "
			+ "SOENFON) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private String dbSchema = "";
	private String dbPath = "";

	/**
	 * @return the dbSchema
	 */
	public String getDbSchema() {
		return dbSchema;
	}

	/**
	 * @param dbSchema the dbSchema to set
	 */
	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	/**
	 * @return the dbPath
	 */
	public String getDbPath() {
		return dbPath;
	}

	/**
	 * @param dbPath the dbPath to set
	 */
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	private String amt = "";
	private String aar = "";
	private String rulleType = "";
	private int laegdNr = 0;
	private String sogn = "";
	private String litra = "";
	private int glLoebeNr = 0;
	private int nyLoebeNr = 0;
	private String fader = "";
	private String soen = "";
	private String foedeSted = "";
	private int alder = 0;
	private BigDecimal stoerrelseITommer = new BigDecimal(0);
	private String ophold = "";
	private String anmaerkninger = "";
	private Date foedt;
	private String gedcomId = "";
	private String navn = "";
	private String faderFon = "";
	private String soenFon = "";

	/**
	 * @return the aar
	 */
	public String getAar() {
		return aar;
	}

	/**
	 * @return the alder
	 */
	public int getAlder() {
		return alder;
	}

	/**
	 * @return the amt
	 */
	public String getAmt() {
		return amt;
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
	 * @return the glLoebeNr
	 */
	public int getGlLoebeNr() {
		return glLoebeNr;
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
	 * @return the navn
	 */
	public String getNavn() {
		return navn;
	}

	/**
	 * @return the nyLoebeNr
	 */
	public int getNyLoebeNr() {
		return nyLoebeNr;
	}

	/**
	 * @return the ophold
	 */
	public String getOphold() {
		return ophold;
	}

	/**
	 * @return the rulleType
	 */
	public String getRulleType() {
		return rulleType;
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
	 * @return the sogn
	 */
	public String getSogn() {
		return sogn;
	}

	/**
	 * @return the stoerrelseITommer
	 */
	public BigDecimal getStoerrelseITommer() {
		return stoerrelseITommer;
	}

	/**
	 * @param aar the aar to set
	 */
	public void setAar(String aar) {
		this.aar = aar;
	}

	/**
	 * @param alder the alder to set
	 */
	public void setAlder(int alder) {
		this.alder = alder;
	}

	/**
	 * @param amt the amt to set
	 */
	public void setAmt(String amt) {
		this.amt = amt;
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
	 * @param glLoebeNr the glLoebeNr to set
	 */
	public void setGlLoebeNr(int glLoebeNr) {
		this.glLoebeNr = glLoebeNr;
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
	 * @param navn the navn to set
	 */
	public void setNavn(String navn) {
		this.navn = navn;
	}

	/**
	 * @param nyLoebeNr the nyLoebeNr to set
	 */
	public void setNyLoebeNr(int nyLoebeNr) {
		this.nyLoebeNr = nyLoebeNr;
	}

	/**
	 * @param ophold the ophold to set
	 */
	public void setOphold(String ophold) {
		this.ophold = ophold;
	}

	/**
	 * @param rulleType the rulleType to set
	 */
	public void setRulleType(String rulleType) {
		this.rulleType = rulleType;
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
	 * @param sogn the sogn to set
	 */
	public void setSogn(String sogn) {
		this.sogn = sogn;
	}

	/**
	 * @param stoerrelseITommer the stoerrelseITommer to set
	 */
	public void setStoerrelseITommer(BigDecimal stoerrelseITommer) {
		this.stoerrelseITommer = stoerrelseITommer;
	}

	/**
	 * Save entry to Derby database
	 * 
	 * @throws SQLException
	 * 
	 */
	public String saveToDb() throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, dbSchema);
		statement.execute();

		statement = conn.prepareStatement(INSERT);
		statement.setString(1, amt);
		statement.setString(2, aar);
		statement.setString(3, rulleType);
		statement.setInt(4, laegdNr);
		statement.setString(5, sogn);
		statement.setString(6, litra);
		statement.setInt(7, glLoebeNr);
		statement.setInt(8, nyLoebeNr);
		statement.setString(9, fader);
		statement.setString(10, soen);
		statement.setString(11, foedeSted);
		statement.setInt(12, alder);
		statement.setBigDecimal(13, stoerrelseITommer);
		statement.setString(14, ophold);
		statement.setString(15, anmaerkninger);
		statement.setDate(16, foedt);
		statement.setString(17, gedcomId);
		statement.setString(18, navn);
		statement.setString(19, faderFon);
		statement.setString(20, soenFon);
		statement.execute();
		return "Indtastning " + nyLoebeNr + " er gemt";
	}
}
