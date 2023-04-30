package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class representing a census household
 *
 * @author Michael Erichsen
 * @version 29. apr. 2023
 *
 */
public class CensusHousehold extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_CENSUS_HOUSEHOLD = "SELECT * FROM CENSUS "
			+ "WHERE KIPNR = ? AND KILDESTEDNAVN = ? AND HUSSTANDS_FAMILIENR = ? AND MATR_NR_ADRESSE = ? "
			+ "AND KILDEHENVISNING = ?";
	private final static String SELECT_HOUSEHOLD_HEAD_1 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND LOEBENR = ?";
	private final static String SELECT_HOUSEHOLD_HEAD_2 = "SELECT INDIVIDUAL FROM EVENT WHERE TYPE = 'Census' "
			+ "AND DATE = ? AND SOURCEDETAIL LIKE ?";

	/**
	 * Get the id for the head of the household
	 *
	 * @param censusModel
	 * @return
	 * @throws SQLException
	 */
	public static String getHeadOfHousehold(Properties props, CensusModel censusModel) throws SQLException {

		final Connection conn1 = DriverManager.getConnection("jdbc:derby:" + props.getProperty("censusPath"));
		final Connection conn2 = DriverManager.getConnection("jdbc:derby:" + props.getProperty("vejbyPath"));
		PreparedStatement statement1 = conn1.prepareStatement(SET_SCHEMA);
		statement1.setString(1, props.getProperty("censusSchema"));
		statement1.execute();

		statement1 = conn1.prepareStatement(SELECT_HOUSEHOLD_HEAD_1);
		statement1.setString(1, censusModel.getKIPnr());
		statement1.setInt(2, censusModel.getLoebenr());
		final ResultSet rs = statement1.executeQuery();

		PreparedStatement statement2;
		int year;
		String kipNr;
		int loebeNr;
		String ftDate;
		String result = "ikke fundet";

		if (rs.next()) {
			year = rs.getInt("FTAAR");
			kipNr = rs.getString("KIPNR");
			loebeNr = rs.getInt("LOEBENR");

			switch (year) {
			case 1787:
				ftDate = "1787-07-01";
				break;
			case 1834:
				ftDate = "1834-02-18";
				break;
			case 1925:
			case 1930:
			case 1940:
				ftDate = year + "-11-05";
				break;
			default:
				ftDate = year + "-02-01";
			}

			statement2 = conn2.prepareStatement(SET_SCHEMA);
			statement2.setString(1, props.getProperty("vejbySchema"));
			statement2.execute();
			statement2 = conn2.prepareStatement(SELECT_HOUSEHOLD_HEAD_2);
			statement2.setString(1, ftDate);
			statement2.setString(2, "%" + kipNr.trim() + ", " + loebeNr + "%");
			ResultSet rs2 = statement2.executeQuery();

			if (rs2.next()) {
				result = rs2.getString("INDIVIDUAL");
				conn2.close();
			} else {
				statement2.setString(2, "%" + loebeNr + ", " + kipNr.trim() + "%");
				rs2 = statement2.executeQuery();

				if (rs2.next()) {
					result = rs2.getString("INDIVIDUAL").replace("@", "").replace("I", "");
					conn2.close();
				}
			}
		}

		conn1.close();
		return result;
	}

	/**
	 * Get a list of household census records from the Derby table
	 *
	 * @param dbPath
	 * @param kipNr
	 * @param kildested
	 * @param nr
	 * @param matr
	 * @param kildeHenvisning
	 * @param schema
	 * @return
	 * @throws SQLException
	 */
	public static List<CensusModel> load(String dbPath, String kipNr, String kildested, String nr, String matr,
			String kildeHenvisning, String schema) throws SQLException {
		CensusModel ci;
		final List<CensusModel> cil = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);
		statement.setString(1, kipNr);
		statement.setString(2, kildested);
		statement.setString(3, nr);
		statement.setString(4, matr);
		statement.setString(5, kildeHenvisning);
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
			cil.add(ci);
		}

		statement.close();
		return cil;
	}

	private List<CensusModel> household;

	/**
	 * @return the household
	 */
	public List<CensusModel> getHousehold() {
		return household;
	}

	/**
	 * @param household the household to set
	 */
	public void setHousehold(List<CensusModel> household) {
		this.household = household;
	}
}
