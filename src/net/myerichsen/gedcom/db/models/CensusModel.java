package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Class representing an individual in the census table
 *
 * @author Michael Erichsen
 * @version 13. apr. 2023
 *
 */
public class CensusModel extends ASModel {
	/**
	 * 
	 */
	private static final String SET_SCHEMA = "SET SCHEMA =  ?";
	private static final String DASH_DATE = "\\d*-\\d{2}-\\d*";
	private static final String EIGHT_DIGITS = "\\d{8}";
	private static final String FOUR_DIGITS = "\\d{4}";
	private static final String DIGITS_ONLY = "\\d+";
	private static final String INSERT = "INSERT INTO CENSUS (KIPNR, LOEBENR, AMT, HERRED, SOGN, "
			+ "KILDESTEDNAVN, HUSSTANDS_FAMILIENR, MATR_NR_ADRESSE, KILDENAVN, FONNAVN, "
			+ "KOEN, ALDER, CIVILSTAND, KILDEERHVERV, STILLING_I_HUSSTANDEN, "
			+ "KILDEFOEDESTED, FOEDT_KILDEDATO, FOEDEAAR, ADRESSE, MATRIKEL, GADE_NR, "
			+ "FTAAR, KILDEHENVISNING, KILDEKOMMENTAR) VALUES ('%s',%d,'%s','%s', '%s', '%s', '%s', '%s', "
			+ "'%s', '%s', '%s', %d, '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s', '%s', %d, '%s', '%s')";
	private static final String SELECT_CENSUS = "SELECT * FROM CENSUS WHERE FONNAVN = ? "
			+ "AND FTAAR >= ? AND FTAAR <= ?";
	private static Logger logger = Logger.getLogger("CensusRecord");

	/**
	 * Get a list of census records from the Derby table
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static CensusModel[] load(String schema, String dbPath, String phonName, String birthYear, String deathYear)
			throws SQLException {
		CensusModel ci;
		final List<CensusModel> cil = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_CENSUS);
		statement.setString(1, phonName);
		statement.setString(2, birthYear);
		statement.setString(3, deathYear);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			ci = new CensusModel();
			ci.setKIPnr(rs.getString("kipNr"));
			ci.setLoebenr(rs.getInt("Loebenr"));
			ci.setKildestednavn(rs.getString("Kildestednavn"));
			ci.setAmt(rs.getString("Amt"));
			ci.setHerred(rs.getString("Herred"));
			ci.setSogn(rs.getString("Sogn"));
			ci.setHusstands_familienr(rs.getString("Husstands_familienr"));
			ci.setMatr_nr_Adresse(rs.getString("Matr_nr_Adresse"));
			ci.setKildenavn(rs.getString("Kildenavn"));
			ci.setKoen(rs.getString("Koen"));
			ci.setAlder(rs.getInt("Alder"));
			ci.setCivilstand(rs.getString("Civilstand"));
			ci.setKildeerhverv(rs.getString("Kildeerhverv"));
			ci.setStilling_i_husstanden(rs.getString("Stilling_i_husstanden"));
			ci.setKildefoedested(rs.getString("Kildefoedested"));
			ci.setFoedt_kildedato(rs.getString("Foedt_kildedato"));
			ci.setFoedeaar(rs.getInt("Foedeaar"));
			ci.setAdresse(rs.getString("Adresse"));
			ci.setMatrikel(rs.getString("Matrikel"));
			ci.setGade_nr(rs.getString("Gade_nr"));
			ci.setFTaar(rs.getInt("FTaar"));
			ci.setKildehenvisning(rs.getString("Kildehenvisning"));
			ci.setKildekommentar(rs.getString("Kildekommentar"));
			logger.fine(ci.toString());
			cil.add(ci);
		}

		final CensusModel[] cra = new CensusModel[cil.size()];

		for (int i = 0; i < cra.length; i++) {
			cra[i] = cil.get(i);
		}

		return cra;
	}

	private String KIPnr = "";
	private int Loebenr = 0;
	private String Amt = "";
	private String Herred = "";
	private String Sogn = "";
	private String Kildestednavn = "";
	private String Husstands_familienr = "";
	private String Matr_nr_Adresse = "";
	private String Kildenavn = "";
	private String Fonnavn = "";
	private String Koen = "";
	private int Alder = 0;
	private String Civilstand = "";
	private String Kildeerhverv = "";
	private String Stilling_i_husstanden = "";
	private String Kildefoedested = "";
	private String Foedt_kildedato = "";
	private int Foedeaar = 0;
	private String Adresse = "";
	private String Matrikel = "";
	private String Gade_nr = "";
	private int FTaar = 0;
	private String Kildehenvisning = "";
	private String Kildekommentar = "";
	private String Kildedetaljer = "";

	/**
	 * @return the adresse
	 */
	public String getAdresse() {
		return Adresse;
	}

	/**
	 * @return the alder
	 */
	public int getAlder() {
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
	public int getFoedeaar() {
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
	 * @return the kildedetaljer
	 */
	public String getKildedetaljer() {
		return Kildedetaljer;
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
	public int getLoebenr() {
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
	 * Insert a census record into a Derby database
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
	 * @param adresse the adresse to set
	 */
	public void setAdresse(String adresse) {
		Adresse = adresse.replace("'", "").trim();
	}

	/**
	 * @param alder the alder to set
	 */
	public void setAlder(int alder) {
		Alder = alder;
	}

	/**
	 * @param alder the alder to set
	 */
	public void setAlder(String alder) {
		final Pattern pattern = Pattern.compile(DIGITS_ONLY);
		final Matcher matcher = pattern.matcher(alder);

		if (matcher.find()) {
			Alder = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @param amt the amt to set
	 */
	public void setAmt(String amt) {
		Amt = amt;
	}

	/**
	 * @param civilstand the civilstand to set
	 */
	public void setCivilstand(String civilstand) {
		Civilstand = civilstand.replace("'", "").trim();
	}

	/**
	 * @param aar the foedeaar to set
	 */
	public void setFoedeaar(int aar) {
		Foedeaar = aar;
	}

	/**
	 * @param aar the foedeaar to set
	 */
	public void setFoedeaar(String aar) {
		final Pattern pattern = Pattern.compile(FOUR_DIGITS);
		final Matcher matcher = pattern.matcher(aar);

		if (matcher.find()) {
			Foedeaar = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @param foedt_kildedato the foedt_kildedato to set
	 */
	public void setFoedt_kildedato(String foedt_kildedato) {
		Foedt_kildedato = foedt_kildedato.replace("'", "").trim();

		if (Foedt_kildedato.length() > 0) {
			Pattern pattern = Pattern.compile(EIGHT_DIGITS);
			Matcher matcher = pattern.matcher(Foedt_kildedato);

			if (!matcher.find()) {
				pattern = Pattern.compile(DASH_DATE);
				matcher = pattern.matcher(Foedt_kildedato);

				if (!matcher.find()) {
					pattern = Pattern.compile(FOUR_DIGITS);
					matcher = pattern.matcher(Foedt_kildedato);

					if (matcher.find()) {
						Foedt_kildedato = "";
						Foedeaar = Integer.parseInt(matcher.group(0));
					}
				}
			}
		}
	}

	/**
	 * @param fTaar the fTaar to set
	 */
	public void setFTaar(int fTaar) {
		FTaar = fTaar;

	}

	/**
	 * @param fTaar the fTaar to set
	 */
	public void setFTaar(String fTaar) {
		final Pattern pattern = Pattern.compile(FOUR_DIGITS);
		final Matcher matcher = pattern.matcher(fTaar);

		if (matcher.find()) {
			FTaar = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @param gade_nr the gade_nr to set
	 */
	public void setGade_nr(String gade_nr) {
		Gade_nr = gade_nr.replace("'", "").trim();
	}

	/**
	 * @param herred the herred to set
	 */
	public void setHerred(String herred) {
		Herred = herred;
	}

	/**
	 * @param husstands_familienr the husstands_familienr to set
	 */
	public void setHusstands_familienr(String husstands_familienr) {
		Husstands_familienr = husstands_familienr.replace("'", "").trim();
	}

	/**
	 * @param kildedetaljer the kildedetaljer to set
	 */
	public void setKildedetaljer(String kildedetaljer) {
		Kildedetaljer = kildedetaljer;
	}

	/**
	 * @param kildeerhverv the kildeerhverv to set
	 */
	public void setKildeerhverv(String kildeerhverv) {
		Kildeerhverv = kildeerhverv.replace("'", "").trim();
	}

	/**
	 * @param kildefoedested the kildefoedested to set
	 */
	public void setKildefoedested(String kildefoedested) {
		Kildefoedested = kildefoedested.replace("'", "").trim();
	}

	/**
	 * @param kildehenvisning the kildehenvisning to set
	 */
	public void setKildehenvisning(String kildehenvisning) {
		Kildehenvisning = kildehenvisning.replace("'", "").trim();
	}

	/**
	 * @param kildekommentar the kildekommentar to set
	 */
	public void setKildekommentar(String kildekommentar) {
		Kildekommentar = kildekommentar.replace("'", "").trim();
	}

	/**
	 * @param kildenavn the kildenavn to set
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
	 * @param kildestednavn the kildestednavn to set
	 */
	public void setKildestednavn(String kildestednavn) {
		Kildestednavn = kildestednavn.replace("'", "").trim();
	}

	/**
	 * @param kIPnr the kIPnr to set
	 */
	public void setKIPnr(String kIPnr) {
		KIPnr = kIPnr.replace("'", "").trim();
	}

	/**
	 * @param koen the koen to set
	 */
	public void setKoen(String koen) {
		Koen = koen.replace("'", "").trim();
	}

	/**
	 * @param loebenr the loebenr to set
	 */
	public void setLoebenr(int loebenr) {
		Loebenr = loebenr;
	}

	/**
	 * @param loebenr the loebenr to set
	 */
	public void setLoebenr(String loebenr) {
		final Pattern pattern = Pattern.compile(DIGITS_ONLY);
		final Matcher matcher = pattern.matcher(loebenr);

		if (matcher.find()) {
			Loebenr = Integer.parseInt(matcher.group(0));
		}
	}

	/**
	 * @param matr_nr_Adresse the matr_nr_Adresse to set
	 */
	public void setMatr_nr_Adresse(String matr_nr_Adresse) {
		Matr_nr_Adresse = matr_nr_Adresse.replace("'", "").trim();
	}

	/**
	 * @param matrikel the matrikel to set
	 */
	public void setMatrikel(String matrikel) {
		Matrikel = matrikel.replace("'", "").trim();
	}

	/**
	 * @param sogn the sogn to set
	 */
	public void setSogn(String sogn) {
		Sogn = sogn;
	}

	/**
	 * @param stilling_i_husstanden the stilling_i_husstanden to set
	 */
	public void setStilling_i_husstanden(String stilling_i_husstanden) {
		Stilling_i_husstanden = stilling_i_husstanden.replace("'", "").trim();
	}

	@Override
	public String toString() {
		return (KIPnr.length() > 0 ? KIPnr + ", " : "") + (Loebenr > 0 ? Loebenr + ", " : "")
				+ (Amt.length() > 0 ? Amt + ", " : "") + (Herred.length() > 0 ? Herred + ", " : "")
				+ (Sogn.length() > 0 ? Sogn + ", " : "") + (Kildestednavn.length() > 0 ? Kildestednavn + ", " : "")
				+ (Husstands_familienr.length() > 0 ? Husstands_familienr + ", " : "")
				+ (Matr_nr_Adresse.length() > 0 ? Matr_nr_Adresse + ", " : "")
				+ (Kildenavn.length() > 0 ? Kildenavn + ", " : "") + (Koen.length() > 0 ? Koen + ", " : "")
				+ (Alder > 0 ? Alder + ", " : "") + (Civilstand.length() > 0 ? Civilstand + ", " : "")
				+ (Kildeerhverv.length() > 0 ? Kildeerhverv + ", " : "")
				+ (Stilling_i_husstanden.length() > 0 ? Stilling_i_husstanden + ", " : "")
				+ (Kildefoedested.length() > 0 ? Kildefoedested + ", " : "")
				+ (Foedt_kildedato.length() > 0 ? Foedt_kildedato + ", " : "") + (Foedeaar > 0 ? Foedeaar + ", " : "")
				+ (Adresse.length() > 0 ? Adresse + ", " : "") + (Matrikel.length() > 0 ? Matrikel + ", " : "")
				+ (Gade_nr.length() > 0 ? Gade_nr + ", " : "") + (FTaar > 0 ? FTaar + ", " : "")
				+ (Kildehenvisning.length() > 0 ? Kildehenvisning + ", " : "")
				+ (Kildekommentar.length() > 0 ? Kildekommentar + ", " : "")
				+ (Kildedetaljer.length() > 0 ? Kildedetaljer + ", " : "");
	}

	/**
	 * Return the object as a String Array
	 *
	 * @return
	 */
	@Override
	public String[] toStringArray() {
		final String[] sa = new String[24];

		sa[0] = FTaar + "";
		sa[1] = Amt;
		sa[2] = Herred;
		sa[3] = Sogn;
		sa[4] = Kildestednavn;
		sa[5] = Husstands_familienr;
		sa[6] = Matr_nr_Adresse;
		sa[7] = Kildenavn;
		sa[8] = Koen;
		sa[9] = Alder + "";
		sa[10] = Civilstand;
		sa[11] = Kildeerhverv;
		sa[12] = Stilling_i_husstanden;
		sa[13] = Kildefoedested;
		sa[14] = Foedt_kildedato;
		sa[15] = Foedeaar + "";
		sa[16] = Adresse;
		sa[17] = Matrikel;
		sa[18] = Gade_nr;
		sa[19] = Kildehenvisning;
		sa[20] = Kildekommentar;
		sa[21] = KIPnr;
		sa[22] = Loebenr + "";
		sa[23] = Kildedetaljer;

		return sa;
	}
}
