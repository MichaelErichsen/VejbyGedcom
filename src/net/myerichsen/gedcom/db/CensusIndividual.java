package net.myerichsen.gedcom.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Erichsen
 * @version 27. feb. 2023
 *
 */
public class CensusIndividual {
	private static final String INSERT = "INSERT INTO VEJBY.CENSUS (KIPNR, LOEBENR, AMT, HERRED, SOGN, "
			+ "KILDESTEDNAVN, HUSSTANDS_FAMILIENR, MATR_NR_ADRESSE, KILDENAVN, FONNAVN, "
			+ "KOEN, ALDER, CIVILSTAND, KILDEERHVERV, STILLING_I_HUSSTANDEN, "
			+ "KILDEFOEDESTED, FOEDT_KILDEDATO, FOEDEAAR, ADRESSE, MATRIKEL, GADE_NR, "
			+ "FTAAR, KILDEHENVISNING, KILDEKOMMENTAR) VALUES ('%s','%s','%s','%s', '%s', '%s', '%s', '%s', "
			+ "'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s')";

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List<CensusIndividual> getFromDb(ResultSet rs) throws SQLException {
		CensusIndividual ci;
		final List<CensusIndividual> cil = new ArrayList<>();

		while (rs.next()) {
			ci = new CensusIndividual();
			ci.setKIPnr(rs.getString("kipNr"));
			ci.setLoebenr(rs.getString("Loebenr"));
			ci.setKildestednavn(rs.getString("Kildestednavn"));
			ci.setAmt(rs.getString("Amt"));
			ci.setHerred(rs.getString("Herred"));
			ci.setSogn(rs.getString("Sogn"));
			ci.setHusstands_familienr(rs.getString("Husstands_familienr"));
			ci.setMatr_nr_Adresse(rs.getString("Matr_nr_Adresse"));
			ci.setKildenavn(rs.getString("Kildenavn"));
			ci.setKoen(rs.getString("Koen"));
			ci.setAlder(rs.getString("Alder"));
			ci.setCivilstand(rs.getString("Civilstand"));
			ci.setKildeerhverv(rs.getString("Kildeerhverv"));
			ci.setStilling_i_husstanden(rs.getString("Stilling_i_husstanden"));
			ci.setKildefoedested(rs.getString("Kildefoedested"));
			ci.setFoedt_kildedato(rs.getString("Foedt_kildedato"));
			ci.setFoedeaar(rs.getString("Foedeaar"));
			ci.setAdresse(rs.getString("Adresse"));
			ci.setMatrikel(rs.getString("Matrikel"));
			ci.setGade_nr(rs.getString("Gade_nr"));
			ci.setFTaar(rs.getInt("FTaar"));
			ci.setKildehenvisning(rs.getString("Kildehenvisning"));
			ci.setKildekommentar(rs.getString("Kildekommentar"));
			cil.add(ci);
		}
		return cil;
	}

	private String KIPnr = "";
	private String Loebenr = "";
	private String Amt = "";
	private String Herred = "";
	private String Sogn = "";
	private String Kildestednavn = "";
	private String Husstands_familienr = "";
	private String Matr_nr_Adresse = "";
	private String Kildenavn = "";
	private String Fonnavn = "";
	private String Koen = "";
	private String Alder = "";
	private String Civilstand = "";
	private String Kildeerhverv = "";
	private String Stilling_i_husstanden = "";
	private String Kildefoedested = "";
	private String Foedt_kildedato = "";
	private String Foedeaar = "";
	private String Adresse = "";
	private String Matrikel = "";
	private String Gade_nr = "";
	private int FTaar = 0;
	private String Kildehenvisning = "";
	private String Kildekommentar = "";

	/**
	 * @return the adresse
	 */
	public String getAdresse() {
		return Adresse;
	}

	/**
	 * @return the alder
	 */
	public String getAlder() {
		return Alder;
	}

	/**
	 * @return the amt
	 */
	public String getAmt() {
		return Amt;
	}

	/**
	 * @return the civilstand
	 */
	public String getCivilstand() {
		return Civilstand;
	}

	/**
	 * @return the foedeaar
	 */
	public String getFoedeaar() {
		return Foedeaar;
	}

	/**
	 * @return the foedt_kildedato
	 */
	public String getFoedt_kildedato() {
		return Foedt_kildedato;
	}

	/**
	 * @return the fonnavn
	 */
	public String getFonnavn() {
		return Fonnavn;
	}

	/**
	 * @return the fTaar
	 */
	public int getFTaar() {
		return FTaar;
	}

	/**
	 * @return the gade_nr
	 */
	public String getGade_nr() {
		return Gade_nr;
	}

	/**
	 * @return the herred
	 */
	public String getHerred() {
		return Herred;
	}

	/**
	 * @return the husstands_familienr
	 */
	public String getHusstands_familienr() {
		return Husstands_familienr;
	}

	/**
	 * @return the kildeerhverv
	 */
	public String getKildeerhverv() {
		return Kildeerhverv;
	}

	/**
	 * @return the kildefoedested
	 */
	public String getKildefoedested() {
		return Kildefoedested;
	}

	/**
	 * @return the kildehenvisning
	 */
	public String getKildehenvisning() {
		return Kildehenvisning;
	}

	/**
	 * @return the kildekommentar
	 */
	public String getKildekommentar() {
		return Kildekommentar;
	}

	/**
	 * @return the kildenavn
	 */
	public String getKildenavn() {
		return Kildenavn;
	}

	/**
	 * @return the kildestednavn
	 */
	public String getKildestednavn() {
		return Kildestednavn;
	}

	/**
	 * @return the kIPnr
	 */
	public String getKIPnr() {
		return KIPnr;
	}

	/**
	 * @return the koen
	 */
	public String getKoen() {
		return Koen;
	}

	/**
	 * @return the loebenr
	 */
	public String getLoebenr() {
		return Loebenr;
	}

	/**
	 * @return the matr_nr_Adresse
	 */
	public String getMatr_nr_Adresse() {
		return Matr_nr_Adresse;
	}

	/**
	 * @return the matrikel
	 */
	public String getMatrikel() {
		return Matrikel;
	}

	/**
	 * @return the sogn
	 */
	public String getSogn() {
		return Sogn;
	}

	/**
	 * @return the stilling_i_husstanden
	 */
	public String getStilling_i_husstanden() {
		return Stilling_i_husstanden;
	}

	/**
	 * Insert into a Derby database
	 *
	 * @param statement
	 * @throws SQLException
	 */
	public void insertIntoDb(Statement statement) throws SQLException {
		final String query = String.format(INSERT, KIPnr, Loebenr, Amt, Herred, Sogn, Kildestednavn,
				Husstands_familienr, Matr_nr_Adresse, Kildenavn, Fonnavn, Koen, Alder, Civilstand, Kildeerhverv,
				Stilling_i_husstanden, Kildefoedested, Foedt_kildedato, Foedeaar, Adresse, Matrikel, Gade_nr, FTaar,
				Kildehenvisning, Kildekommentar);

		statement.execute(query);
	}

	/**
	 * @param adresse
	 *            the adresse to set
	 */
	public void setAdresse(String adresse) {
		Adresse = adresse.replace("'", "").trim();
	}

	/**
	 * @param alder
	 *            the alder to set
	 */
	public void setAlder(String alder) {
		Alder = alder.replace("'", "").trim();
	}

	/**
	 * @param amt
	 *            the amt to set
	 */
	public void setAmt(String amt) {
		Amt = amt;
	}

	/**
	 * @param civilstand
	 *            the civilstand to set
	 */
	public void setCivilstand(String civilstand) {
		Civilstand = civilstand.replace("'", "").trim();
	}

	/**
	 * @param foedeaar
	 *            the foedeaar to set
	 */
	public void setFoedeaar(String foedeaar) {
		Foedeaar = foedeaar.replace("'", "").trim();
	}

	/**
	 * @param foedt_kildedato
	 *            the foedt_kildedato to set
	 */
	public void setFoedt_kildedato(String foedt_kildedato) {
		Foedt_kildedato = foedt_kildedato.replace("'", "").trim();
	}

	/**
	 * @param fTaar
	 *            the fTaar to set
	 */
	public void setFTaar(int fTaar) {
		FTaar = fTaar;

	}

	/**
	 * @param fTaar
	 *            the fTaar to set
	 */
	public void setFTaar(String fTaar) {
		final Pattern pattern = Pattern.compile("\\d{4}");
		final Matcher matcher = pattern.matcher(fTaar);

		if (matcher.find()) {
			FTaar = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @param gade_nr
	 *            the gade_nr to set
	 */
	public void setGade_nr(String gade_nr) {
		Gade_nr = gade_nr.replace("'", "").trim();
	}

	/**
	 * @param herred
	 *            the herred to set
	 */
	public void setHerred(String herred) {
		Herred = herred;
	}

	/**
	 * @param husstands_familienr
	 *            the husstands_familienr to set
	 */
	public void setHusstands_familienr(String husstands_familienr) {
		Husstands_familienr = husstands_familienr.replace("'", "").trim();
	}

	/**
	 * @param kildeerhverv
	 *            the kildeerhverv to set
	 */
	public void setKildeerhverv(String kildeerhverv) {
		Kildeerhverv = kildeerhverv.replace("'", "").trim();
	}

	/**
	 * @param kildefoedested
	 *            the kildefoedested to set
	 */
	public void setKildefoedested(String kildefoedested) {
		Kildefoedested = kildefoedested.replace("'", "").trim();
	}

	/**
	 * @param kildehenvisning
	 *            the kildehenvisning to set
	 */
	public void setKildehenvisning(String kildehenvisning) {
		Kildehenvisning = kildehenvisning.replace("'", "").trim();
	}

	/**
	 * @param kildekommentar
	 *            the kildekommentar to set
	 */
	public void setKildekommentar(String kildekommentar) {
		Kildekommentar = kildekommentar.replace("'", "").trim();
	}

	/**
	 * @param kildenavn
	 *            the kildenavn to set
	 */
	public void setKildenavn(String kildenavn) {
		Kildenavn = kildenavn.replace("'", "").trim();
		try {
			Fonnavn = new Fonkod().generateKey(kildenavn).replace("'", "").trim();
		} catch (final Exception e) {
			// Do nothing
		}
	}

	/**
	 * @param kildestednavn
	 *            the kildestednavn to set
	 */
	public void setKildestednavn(String kildestednavn) {
		Kildestednavn = kildestednavn.replace("'", "").trim();
	}

	/**
	 * @param kIPnr
	 *            the kIPnr to set
	 */
	public void setKIPnr(String kIPnr) {
		KIPnr = kIPnr.replace("'", "").trim();
	}

	/**
	 * @param koen
	 *            the koen to set
	 */
	public void setKoen(String koen) {
		Koen = koen.replace("'", "").trim();
	}

	/**
	 * @param loebenr
	 *            the loebenr to set
	 */
	public void setLoebenr(String loebenr) {
		Loebenr = loebenr.replace("'", "").trim();
	}

	/**
	 * @param matr_nr_Adresse
	 *            the matr_nr_Adresse to set
	 */
	public void setMatr_nr_Adresse(String matr_nr_Adresse) {
		Matr_nr_Adresse = matr_nr_Adresse.replace("'", "").trim();
	}

	/**
	 * @param matrikel
	 *            the matrikel to set
	 */
	public void setMatrikel(String matrikel) {
		Matrikel = matrikel.replace("'", "").trim();
	}

	/**
	 * @param sogn
	 *            the sogn to set
	 */
	public void setSogn(String sogn) {
		Sogn = sogn;
	}

	/**
	 * @param stilling_i_husstanden
	 *            the stilling_i_husstanden to set
	 */
	public void setStilling_i_husstanden(String stilling_i_husstanden) {
		Stilling_i_husstanden = stilling_i_husstanden.replace("'", "").trim();
	}

	@Override
	public String toString() {
		return KIPnr + ";" + Loebenr + ";" + Amt + ";" + Herred + ";" + Sogn + ";" + Kildestednavn + ";"
				+ Husstands_familienr + ";" + Matr_nr_Adresse + ";" + Kildenavn + ";" + Fonnavn + ";" + Koen + ";"
				+ Alder + ";" + Civilstand + ";" + Kildeerhverv + ";" + Stilling_i_husstanden + ";" + Kildefoedested
				+ ";" + Foedt_kildedato + ";" + Foedeaar + ";" + Adresse + ";" + Matrikel + ";" + Gade_nr + ";" + FTaar
				+ ";" + Kildehenvisning + ";" + Kildekommentar;
	}

	/**
	 * @return
	 */
	public String toStringX() {
		return "CensusIndividual [" + (KIPnr != null ? "KIPnr=" + KIPnr + ", " : "")
				+ (Loebenr != null ? "Loebenr=" + Loebenr + ", " : "") + (Amt != null ? "Amt=" + Amt + ", " : "")
				+ (Herred != null ? "Herred=" + Herred + ", " : "") + (Sogn != null ? "Sogn=" + Sogn + ", " : "")
				+ (Kildestednavn != null ? "Kildestednavn=" + Kildestednavn + ", " : "")
				+ (Husstands_familienr != null ? "Husstands_familienr=" + Husstands_familienr + ", " : "")
				+ (Matr_nr_Adresse != null ? "Matr_nr_Adresse=" + Matr_nr_Adresse + ", " : "")
				+ (Kildenavn != null ? "Kildenavn=" + Kildenavn + ", " : "")
				+ (Fonnavn != null ? "Fonnavn=" + Fonnavn + ", " : "") + (Koen != null ? "Koen=" + Koen + ", " : "")
				+ (Alder != null ? "Alder=" + Alder + ", " : "")
				+ (Civilstand != null ? "Civilstand=" + Civilstand + ", " : "")
				+ (Kildeerhverv != null ? "Kildeerhverv=" + Kildeerhverv + ", " : "")
				+ (Stilling_i_husstanden != null ? "Stilling_i_husstanden=" + Stilling_i_husstanden + ", " : "")
				+ (Kildefoedested != null ? "Kildefoedested=" + Kildefoedested + ", " : "")
				+ (Foedt_kildedato != null ? "Foedt_kildedato=" + Foedt_kildedato + ", " : "")
				+ (Foedeaar != null ? "Foedeaar=" + Foedeaar + ", " : "")
				+ (Adresse != null ? "Adresse=" + Adresse + ", " : "")
				+ (Matrikel != null ? "Matrikel=" + Matrikel + ", " : "")
				+ (Gade_nr != null ? "Gade_nr=" + Gade_nr + ", " : "") + "FTaar=" + FTaar + ", "
				+ (Kildehenvisning != null ? "Kildehenvisning=" + Kildehenvisning + ", " : "")
				+ (Kildekommentar != null ? "Kildekommentar=" + Kildekommentar : "") + "]";
	}
}
