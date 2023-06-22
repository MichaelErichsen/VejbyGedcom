package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.archivesearcher.util.Fonkod;

/**
 * Class representing an individual in the census table
 *
 * @author Michael Erichsen
 * @version 22. jun. 2023
 *
 */

public class CensusModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String DASH_DATE = "\\d*-\\d*-\\d{4}";
	private static final String EIGHT_DIGITS = "\\d{8}";
	private static final String FOUR_DIGITS = "\\d{4}";
	private static final String DIGITS_ONLY = "\\d+";
	private static final String INSERT = "INSERT INTO CENSUS (KIPNR, LOEBENR, AMT, HERRED, SOGN, "
			+ "KILDESTEDNAVN, HUSSTANDS_FAMILIENR, MATR_NR_ADRESSE, KILDENAVN, FONNAVN, "
			+ "KOEN, ALDER, CIVILSTAND, KILDEERHVERV, STILLING_I_HUSSTANDEN, "
			+ "KILDEFOEDESTED, FOEDT_KILDEDATO, FOEDEAAR, ADRESSE, MATRIKEL, GADE_NR, "
			+ "FTAAR, KILDEHENVISNING, KILDEKOMMENTAR) VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT = "SELECT * FROM CENSUS WHERE FONNAVN = ? " + "AND FTAAR >= ? AND FTAAR <= ?";
	private static final String SELECT_WIFE = "SELECT WIFE FROM FAMILY WHERE HUSBAND = ?";
	private static final String SELECT_HUSBAND = "SELECT HUSBAND FROM FAMILY WHERE WIFE = ?";
	private static final String SELECT_HOUSEHOLD = "SELECT * FROM CENSUS "
			+ "WHERE KIPNR = ? AND KILDESTEDNAVN = ? AND HUSSTANDS_FAMILIENR = ? AND MATR_NR_ADRESSE = ? "
			+ "AND KILDEHENVISNING = ?";
	private static Connection conn;
	private static String schema;

	/**
	 * Filter an array of census records by spouse
	 *
	 * @param dbPath
	 * @param schema
	 * @param ID
	 * @param sourceArray
	 * @return
	 * @throws SQLException
	 */
	public static CensusModel[] filterForSpouses(String dbPath, String schema, String ID, CensusModel[] sourceArray)
			throws SQLException {
		final List<CensusModel> list = new ArrayList<>();
		List<CensusModel> houseHold;
		final List<SpouseModel> spouseList = getSpouseList(ID);
		int fTaar = 0;

		for (final CensusModel censusModel : sourceArray) {
			fTaar = censusModel.getFTaar();
			houseHold = getHouseHold(dbPath, schema, censusModel);

			for (final CensusModel householdModel : houseHold) {
				for (final SpouseModel spouseModel : spouseList) {
					if (spouseModel.getStartYear() <= fTaar && spouseModel.getEndYear() >= fTaar
							&& householdModel.getFonnavn().equals(spouseModel.getPhoneticName().trim())) {
						list.add(censusModel);
					}
				}
			}
		}

		final CensusModel[] array = new CensusModel[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	/**
	 * Get a list of households of the primary person in a census
	 *
	 * @param dbPath
	 * @param schema
	 * @param sourceModel
	 * @return
	 * @throws SQLException
	 */
	private static List<CensusModel> getHouseHold(String dbPath, String schema, CensusModel sourceModel)
			throws SQLException {
		CensusModel model;
		final List<CensusModel> list = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(SELECT_HOUSEHOLD);
		statement.setString(1, sourceModel.getKIPnr());
		statement.setString(2, sourceModel.getKildestednavn());
		statement.setString(3, sourceModel.getHusstands_familienr());
		statement.setString(4, sourceModel.getMatr_nr_Adresse());
		statement.setString(5, sourceModel.getKildehenvisning());
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			model = new CensusModel();
			model.setKIPnr(rs.getString("kipNr"));
			model.setLoebenr(rs.getInt("Loebenr"));
			model.setKildestednavn(rs.getString("Kildestednavn"));
			model.setAmt(rs.getString("Amt"));
			model.setHerred(rs.getString("Herred"));
			model.setSogn(rs.getString("Sogn"));
			model.setHusstands_familienr(rs.getString("Husstands_familienr"));
			model.setMatr_nr_Adresse(rs.getString("Matr_nr_Adresse"));
			model.setKildenavn(rs.getString("Kildenavn"));
			model.setKoen(rs.getString("Koen"));
			model.setAlder(rs.getInt("ALDER"));
			model.setCivilstand(rs.getString("Civilstand"));
			model.setKildeerhverv(rs.getString("Kildeerhverv"));
			model.setStilling_i_husstanden(rs.getString("Stilling_i_husstanden"));
			model.setKildefoedested(rs.getString("Kildefoedested"));
			model.setFoedt_kildedato(rs.getString("Foedt_kildedato"));
			model.setFoedeaar(rs.getInt("Foedeaar"));
			model.setAdresse(rs.getString("Adresse"));
			model.setMatrikel(rs.getString("Matrikel"));
			model.setGade_nr(rs.getString("Gade_nr"));
			model.setFTaar(rs.getInt("FTAar"));
			model.setKildehenvisning(rs.getString("Kildehenvisning"));
			model.setKildekommentar(rs.getString("Kildekommentar"));
			list.add(model);
		}

		statement.close();
		conn.close();
		return list;

	}

	/**
	 * @return the schema
	 */
	public static String getSchema() {
		return schema;
	}

	/**
	 * Get list of spouses for primary person
	 *
	 * @param iD
	 * @return
	 * @throws SQLException
	 */
	private static List<SpouseModel> getSpouseList(String iD) throws SQLException {
		SpouseModel model;
		final List<SpouseModel> list = new ArrayList<>();

		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, getSchema());
		statement.execute();

		statement = conn.prepareStatement(SELECT_WIFE);
		statement.setString(1, iD);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			model = new SpouseModel(conn, getSchema(), iD, rs.getString("WIFE"));
			list.add(model);
		}

		statement = conn.prepareStatement(SELECT_HUSBAND);
		statement.setString(1, iD);
		rs = statement.executeQuery();

		while (rs.next()) {
			model = new SpouseModel(conn, getSchema(), iD, rs.getString("HUSBAND"));
			list.add(model);
		}

		return list;
	}

	/**
	 * Get a list of census records from the Derby table
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static CensusModel[] load(String schema, String dbPath, String phonName, String birthYear, String deathYear)
			throws Exception {
		setSchema(schema);
		CensusModel model;
		final List<CensusModel> list = new ArrayList<>();

		conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT);
		statement.setString(1, phonName);
		statement.setString(2, birthYear);
		statement.setString(3, deathYear);
		final ResultSet rs = statement.executeQuery();

		final int by = Integer.parseInt(birthYear);
		int alder = 0;
		int ftYear = 0;
		String fkd = "";
		final Pattern pattern8 = Pattern.compile(EIGHT_DIGITS);
		final Pattern patternD = Pattern.compile(DASH_DATE);
		Matcher matcher;
		int now = 0;
		int len = 0;

		while (rs.next()) {
			alder = rs.getInt("Alder");
			ftYear = rs.getInt("FTaar");
			fkd = rs.getString("Foedt_kildedato");

			if (by > 1) {
				if (alder > 0) {
					if (Math.abs(ftYear - alder - by) > 2) {
						continue;
					}
				}

				if (fkd.length() > 7) {
					matcher = pattern8.matcher(fkd);

					if (matcher.find()) {
						now = Integer.parseInt(fkd.substring(0, 4));

						if (Math.abs(now - by) > 2) {
							continue;
						}
					}

					matcher = patternD.matcher(fkd);

					if (matcher.find()) {
						final String group = matcher.group();
						len = group.length();
						now = Integer.parseInt(group.substring(len - 4, len));

						if (Math.abs(now - by) > 2) {
							continue;
						}
					}
				}
			}

			model = new CensusModel();
			model.setKIPnr(rs.getString("kipNr"));
			model.setLoebenr(rs.getInt("Loebenr"));
			model.setKildestednavn(rs.getString("Kildestednavn"));
			model.setAmt(rs.getString("Amt"));
			model.setHerred(rs.getString("Herred"));
			model.setSogn(rs.getString("Sogn"));
			model.setHusstands_familienr(rs.getString("Husstands_familienr"));
			model.setMatr_nr_Adresse(rs.getString("Matr_nr_Adresse"));
			model.setKildenavn(rs.getString("Kildenavn"));
			model.setKoen(rs.getString("Koen"));
			model.setAlder(alder);
			model.setCivilstand(rs.getString("Civilstand"));
			model.setKildeerhverv(rs.getString("Kildeerhverv"));
			model.setStilling_i_husstanden(rs.getString("Stilling_i_husstanden"));
			model.setKildefoedested(rs.getString("Kildefoedested"));
			model.setFoedt_kildedato(fkd);
			model.setFoedeaar(rs.getInt("Foedeaar"));
			model.setAdresse(rs.getString("Adresse"));
			model.setMatrikel(rs.getString("Matrikel"));
			model.setGade_nr(rs.getString("Gade_nr"));
			model.setFTaar(ftYear);
			model.setKildehenvisning(rs.getString("Kildehenvisning"));
			model.setKildekommentar(rs.getString("Kildekommentar"));
			list.add(model);
		}

		final CensusModel[] array = new CensusModel[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	/**
	 * @param schema the schema to set
	 */
	public static void setSchema(String schema) {
		CensusModel.schema = schema;
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
	public void insertIntoDb(Connection conn) throws SQLException {
		final PreparedStatement statement = conn.prepareStatement(INSERT);
		statement.setString(1, KIPnr);
		statement.setInt(2, Loebenr);
		statement.setString(3, Amt);
		statement.setString(4, Herred);
		statement.setString(5, Sogn);
		statement.setString(6, Kildestednavn);
		statement.setString(7, Husstands_familienr);
		statement.setString(8, Matr_nr_Adresse);
		statement.setString(9, Kildenavn);
		statement.setString(10, Fonnavn);
		statement.setString(11, Koen);
		statement.setInt(12, Alder);
		statement.setString(13, Civilstand);
		statement.setString(14, Kildeerhverv);
		statement.setString(15, Stilling_i_husstanden);
		statement.setString(16, Kildefoedested);
		statement.setString(17, Foedt_kildedato);
		statement.setInt(18, Foedeaar);
		statement.setString(19, Adresse);
		statement.setString(20, Matrikel);
		statement.setString(21, Gade_nr);
		statement.setInt(22, FTaar);
		statement.setString(23, Kildehenvisning);
		statement.setString(24, Kildekommentar);

		statement.execute();
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
		return (KIPnr.length() > 0 ? KIPnr.trim() + ", " : "") + (Loebenr > 0 ? Loebenr + ", " : "")
				+ (Amt.length() > 0 ? Amt.trim() + ", " : "") + (Herred.length() > 0 ? Herred.trim() + ", " : "")
				+ (Sogn.length() > 0 ? Sogn.trim() + ", " : "")
				+ (Kildestednavn.length() > 0 ? Kildestednavn.trim() + ", " : "")
				+ (Husstands_familienr.length() > 0 ? Husstands_familienr.trim() + ", " : "")
				+ (Matr_nr_Adresse.length() > 0 ? Matr_nr_Adresse.trim() + ", " : "")
				+ (Kildenavn.length() > 0 ? Kildenavn.trim() + ", " : "")
				+ (Koen.length() > 0 ? Koen.trim() + ", " : "") + (Alder > 0 ? Alder + ", " : "")
				+ (Civilstand.length() > 0 ? Civilstand.trim() + ", " : "")
				+ (Kildeerhverv.length() > 0 ? Kildeerhverv.trim() + ", " : "")
				+ (Stilling_i_husstanden.length() > 0 ? Stilling_i_husstanden.trim() + ", " : "")
				+ (Kildefoedested.length() > 0 ? Kildefoedested.trim() + ", " : "")
				+ (Foedt_kildedato.length() > 0 ? Foedt_kildedato.trim() + ", " : "")
				+ (Foedeaar > 0 ? Foedeaar + ", " : "") + (Adresse.length() > 0 ? Adresse.trim() + ", " : "")
				+ (Matrikel.length() > 0 ? Matrikel.trim() + ", " : "")
				+ (Gade_nr.length() > 0 ? Gade_nr.trim() + ", " : "") + (FTaar > 0 ? FTaar + ", " : "")
				+ (Kildehenvisning.length() > 0 ? Kildehenvisning.trim() + ", " : "")
				+ (Kildekommentar.length() > 0 ? Kildekommentar.trim() + ", " : "")
				+ (Kildedetaljer.length() > 0 ? Kildedetaljer.trim() + ", " : "");
	}
}
