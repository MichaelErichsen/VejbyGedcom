package net.myerichsen.gedcom.db.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an entry in a military roll
 *
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */

public class MilRollEntryModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM LAEGD.LAEGD, LAEGD.RULLE WHERE LAEGD.LAEGD.LAEGDID = LAEGD.RULLE.LAEGDID";

	private String amt = "";
	private int aar = 0;
	private String litra = " ";
	private int laegdnr = 0;
	private int glaar = 0;
	private String gllitra = " ";
	private String rulletype = "Hovedrulle";
	private String sogn = "";
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
	 * @return the aar
	 */
	public int getAar() {
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
	 * @return the glaar
	 */
	public int getGlaar() {
		return glaar;
	}

	/**
	 * @return the glLaegdId
	 */
	public int getGlLaegdId() {
		return glLaegdId;
	}

	/**
	 * @return the gllitra
	 */
	public String getGllitra() {
		return gllitra;
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
	 * @return the laegdnr
	 */
	public int getLaegdnr() {
		return laegdnr;
	}

	/**
	 * @return the litra
	 */
	public String getLitra() {
		return litra;
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
	 * @return the rulletype
	 */
	public String getRulletype() {
		return rulletype;
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
	 * Load list of entries from data base
	 *
	 * @param props
	 *
	 * @throws SQLException
	 *
	 */
	public static MilRollEntryModel[] load(String path, String schema) throws SQLException {
		MilRollEntryModel m;
		final List<MilRollEntryModel> lm = new ArrayList<MilRollEntryModel>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + path);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(SELECT);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			m = new MilRollEntryModel();
			m.setAmt(rs.getString("AMT").trim());
			m.setAar(rs.getInt("AAR"));
			m.setLitra(rs.getString("LITRA"));
			m.setLaegdnr(rs.getInt("LAEGDNR"));
			m.setGlaar(rs.getInt("GLAAR"));
			m.setGllitra(rs.getString("GLLITRA"));
			m.setRulletype(rs.getString("RULLETYPE").trim());
			m.setSogn(rs.getString("SOGN").trim());
			m.setLaegdId(rs.getInt("LAEGDID"));
			m.setGlLaegdId(rs.getInt("GLLAEGDID"));
			m.setGlaar(rs.getInt("GLLOEBENR"));
			m.setLoebeNr(rs.getInt("LOEBENR"));
			m.setFader(rs.getString("FADER"));
			m.setSoen(rs.getString("SOEN"));
			m.setFoedeSted(rs.getString("FOEDESTED"));
			m.setAlder(rs.getInt("ALDER"));
			m.setStoerrelseITommer(rs.getBigDecimal("STOERRELSEITOMMER"));
			m.setOphold(rs.getString("OPHOLD"));
			m.setAnmaerkninger(rs.getString("ANMAERKNINGER"));
			m.setFoedt(rs.getDate("FOEDT"));
			m.setGedcomId(rs.getString("GEDCOMID"));
			m.setNavn(rs.getString("NAVN"));
			m.setFaderFon(rs.getString("FADERFON"));
			m.setSoenFon(rs.getString("SOENFON"));

			lm.add(m);
		}

		final MilRollEntryModel[] ma = new MilRollEntryModel[lm.size()];

		for (int i = 0; i < lm.size(); i++) {
			ma[i] = lm.get(i);

		}

		return ma;
	}

	/**
	 * @param aar the aar to set
	 */
	public void setAar(int aar) {
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
	 * @param glaar the glaar to set
	 */
	public void setGlaar(int glaar) {
		this.glaar = glaar;
	}

	/**
	 * @param glLaegdId the glLaegdId to set
	 */
	public void setGlLaegdId(int glLaegdId) {
		this.glLaegdId = glLaegdId;
	}

	/**
	 * @param gllitra the gllitra to set
	 */
	public void setGllitra(String gllitra) {
		this.gllitra = gllitra;
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
	 * @param laegdnr the laegdnr to set
	 */
	public void setLaegdnr(int laegdnr) {
		this.laegdnr = laegdnr;
	}

	/**
	 * @param litra the litra to set
	 */
	public void setLitra(String litra) {
		this.litra = litra;
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
	 * @param rulletype the rulletype to set
	 */
	public void setRulletype(String rulletype) {
		this.rulletype = rulletype;
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

}
