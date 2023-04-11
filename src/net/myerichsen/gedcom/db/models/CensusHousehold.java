package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class CensusHousehold extends ASModel {
	private static final String SELECT_CENSUS_HOUSEHOLD = "SELECT * FROM CENSUS "
			+ "WHERE KIPNR = ? AND HUSSTANDS_FAMILIENR = ? ";

	/**
	 * Get a list of household census records from the Derby table
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List<CensusModel> loadFromDatabase(String dbPath, String kipNr, String nr, String schema)
			throws SQLException {
		CensusModel ci;
		final List<CensusModel> cil = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);
		statement.setString(1, kipNr);
		statement.setString(2, nr);
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

	@Override
	public String[] toStringArray() {
		final List<String> ls = new ArrayList<>();

		for (final CensusModel censusRecord : household) {
			ls.add(censusRecord.toString());
		}

		return (String[]) ls.toArray();
	}
}
