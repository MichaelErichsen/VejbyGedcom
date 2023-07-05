package net.myerichsen.archivesearcher.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class representing an entry in a military roll
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
 *
 */

public class MilRollEntryModel extends ASModel {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";

	private static final String LOAD = "SELECT * FROM LAEGD, RULLE WHERE LAEGD.LAEGD.LAEGDID = LAEGD.RULLE.LAEGDID";
	private static final String SELECT = "SELECT * FROM LAEGD, RULLE WHERE LAEGD.LAEGDID = ? AND RULLE.LOEBENR = ? "
			+ "AND LAEGD.LAEGDID = RULLE.LAEGDID";
	private static final String SELECT_PREV = "SELECT * FROM LAEGD, RULLE WHERE LAEGD.PREVLAEGDID = ? "
			+ "AND RULLE.PREVLOEBENR = ? AND LAEGD.LAEGDID = RULLE.LAEGDID";

	/**
	 * Load list of entries from data base
	 *
	 * @param path
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public static MilRollEntryModel[] load(String path, String schema) throws SQLException {
		MilRollEntryModel model;
		final List<MilRollEntryModel> list = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + path);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(LOAD);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			model = new MilRollEntryModel();
			model.setAmt(rs.getString("AMT").trim());
			model.setAar(rs.getInt("AAR"));
			model.setLitra(rs.getString("LITRA"));
			model.setRulletype(rs.getString("RULLETYPE").trim());
			model.setLaegdnr(rs.getInt("LAEGDNR"));
			model.setSogn(rs.getString("SOGN").trim());
			model.setLaegdId(rs.getInt("LAEGDID"));
			model.setNextLaegdId(rs.getInt("NEXTLAEGDID"));
			model.setPrevLaegdId(rs.getInt("PREVLAEGDID"));
			model.setPrevLoebeNr(rs.getInt("PREVLOEBENR"));
			model.setLoebeNr(rs.getInt("LOEBENR"));
			model.setFader(rs.getString("FADER"));
			model.setSoen(rs.getString("SOEN"));
			model.setFoedeSted(rs.getString("FOEDESTED"));
			model.setAlder(rs.getInt("ALDER"));
			model.setStoerrelseITommer(rs.getBigDecimal("STOERRELSEITOMMER"));
			model.setOphold(rs.getString("OPHOLD"));
			model.setAnmaerkninger(rs.getString("ANMAERKNINGER"));
			model.setFoedt(rs.getDate("FOEDT"));
			model.setGedcomId(rs.getString("GEDCOMID"));
			model.setNavn(rs.getString("NAVN"));
			model.setFaderFon(rs.getString("FADERFON"));
			model.setSoenFon(rs.getString("SOENFON"));
			list.add(model);
		}

		final MilRollEntryModel[] ma = new MilRollEntryModel[list.size()];

		for (int i = 0; i < list.size(); i++) {
			ma[i] = list.get(i);

		}

		return ma;
	}

	/**
	 * @param props
	 * @param laegdId
	 * @param loebeNr
	 * @return
	 */
	public static MilRollEntryModel select(Properties props, int laegdId, int loebeNr) {
		MilRollEntryModel model = null;

		if (laegdId == 0 && loebeNr == 0) {
			return model;
		}

		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("milrollPath"));
			PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("milrollSchema"));
			statement.execute();

			statement = conn.prepareStatement(SELECT);
			statement.setInt(1, laegdId);
			statement.setInt(2, loebeNr);
			final ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return null;
			}

			model = new MilRollEntryModel();
			model.setAmt(rs.getString("AMT").trim());
			model.setAar(rs.getInt("AAR"));
			model.setLitra(rs.getString("LITRA"));
			model.setRulletype(rs.getString("RULLETYPE").trim());
			model.setLaegdnr(rs.getInt("LAEGDNR"));
			model.setSogn(rs.getString("SOGN").trim());
			model.setLaegdId(laegdId);
			model.setNextLaegdId(rs.getInt("NEXTLAEGDID"));
			model.setPrevLaegdId(rs.getInt("PREVLAEGDID"));
			model.setPrevLoebeNr(rs.getInt("PREVLOEBENR"));
			model.setLoebeNr(loebeNr);
			model.setFader(rs.getString("FADER"));
			model.setSoen(rs.getString("SOEN"));
			model.setFoedeSted(rs.getString("FOEDESTED"));
			model.setAlder(rs.getInt("ALDER"));
			model.setStoerrelseITommer(rs.getBigDecimal("STOERRELSEITOMMER"));
			model.setOphold(rs.getString("OPHOLD"));
			model.setAnmaerkninger(rs.getString("ANMAERKNINGER"));
			model.setFoedt(rs.getDate("FOEDT"));
			model.setGedcomId(rs.getString("GEDCOMID"));
			model.setNavn(rs.getString("NAVN"));
			model.setFaderFon(rs.getString("FADERFON"));
			model.setSoenFon(rs.getString("SOENFON"));
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return model;

	}

	/**
	 * @param props
	 * @param prevLaegdId
	 * @param prevLoebeNr
	 * @return
	 */
	public static MilRollEntryModel selectPrev(Properties props, int prevLaegdId, int prevLoebeNr) {
		MilRollEntryModel model = null;

		if (prevLaegdId == 0 && prevLoebeNr == 0) {
			return model;
		}

		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("milrollPath"));
			PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("milrollSchema"));
			statement.execute();

			statement = conn.prepareStatement(SELECT_PREV);
			statement.setInt(1, prevLaegdId);
			statement.setInt(2, prevLoebeNr);
			final ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return null;
			}

			model = new MilRollEntryModel();
			model.setAmt(rs.getString("AMT").trim());
			model.setAar(rs.getInt("AAR"));
			model.setLitra(rs.getString("LITRA"));
			model.setRulletype(rs.getString("RULLETYPE").trim());
			model.setLaegdnr(rs.getInt("LAEGDNR"));
			model.setSogn(rs.getString("SOGN").trim());
			model.setLaegdId(rs.getInt("LAEGDID"));
			model.setNextLaegdId(rs.getInt("NEXTLAEGDID"));
			model.setPrevLaegdId(rs.getInt("PREVLAEGDID"));
			model.setPrevLoebeNr(rs.getInt("PREVLOEBENR"));
			model.setLoebeNr(rs.getInt("LOEBENR"));
			model.setFader(rs.getString("FADER"));
			model.setSoen(rs.getString("SOEN"));
			model.setFoedeSted(rs.getString("FOEDESTED"));
			model.setAlder(rs.getInt("ALDER"));
			model.setStoerrelseITommer(rs.getBigDecimal("STOERRELSEITOMMER"));
			model.setOphold(rs.getString("OPHOLD"));
			model.setAnmaerkninger(rs.getString("ANMAERKNINGER"));
			model.setFoedt(rs.getDate("FOEDT"));
			model.setGedcomId(rs.getString("GEDCOMID"));
			model.setNavn(rs.getString("NAVN"));
			model.setFaderFon(rs.getString("FADERFON"));
			model.setSoenFon(rs.getString("SOENFON"));
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return model;

	}

	private String amt = "";

	private int aar = 0;
	private String litra = " ";
	private String rulletype = "Hovedrulle";
	private int laegdnr = 0;
	private String sogn = "";
	private int laegdId = 0;
	private int prevLaegdId = 0;
	private int nextLaegdId = 0;
	private int prevLoebeNr = 0;
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
	 * @return the nextLaegdId
	 */
	public int getNextLaegdId() {
		return nextLaegdId;
	}

	/**
	 * @return the ophold
	 */
	public String getOphold() {
		return ophold;
	}

	/**
	 * @return the prevLaegdId
	 */
	public int getPrevLaegdId() {
		return prevLaegdId;
	}

	/**
	 * @return the prevLoebeNr
	 */
	public int getPrevLoebeNr() {
		return prevLoebeNr;
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
	 * @param nextLaegdId the nextLaegdId to set
	 */
	public void setNextLaegdId(int nextLaegdId) {
		this.nextLaegdId = nextLaegdId;
	}

	/**
	 * @param ophold the ophold to set
	 */
	public void setOphold(String ophold) {
		this.ophold = ophold;
	}

	/**
	 * @param prevLaegdId the prevLaegdId to set
	 */
	public void setPrevLaegdId(int prevLaegdId) {
		this.prevLaegdId = prevLaegdId;
	}

	/**
	 * @param prevLoebeNr the prevLoebeNr to set
	 */
	public void setPrevLoebeNr(int prevLoebeNr) {
		this.prevLoebeNr = prevLoebeNr;
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

	@Override
	public String toString() {
		return (navn != null ? navn.trim() : "") + ", " + (amt != null ? amt.trim() + ", " : "") + aar + ", "
				+ (!litra.equals(" ") ? litra + ", " : "") + laegdnr + ", " + (sogn != null ? sogn.trim() + ", " : "")
				+ (fader != null ? fader.trim() + ", " : "") + (foedeSted != null ? foedeSted.trim() + ", " : "")
				+ alder + ", " + stoerrelseITommer + ", " + (ophold != null ? ophold.trim() + ", " : "")
				+ (anmaerkninger.length() > 0 ? anmaerkninger.trim() + ", " : "") + (foedt != null ? foedt + ", " : "")
				+ (gedcomId != null ? gedcomId.trim() + ", " : "") + laegdId + ", " + loebeNr;
	}

}
