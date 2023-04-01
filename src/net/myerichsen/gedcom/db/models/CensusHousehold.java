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
 * @version 1. apr. 2023
 *
 */
public class CensusHousehold extends ASModel {
	private static final String SELECT_CENSUS_HOUSEHOLD = "SELECT * FROM VEJBY.CENSUS "
			+ "WHERE KIPNR = ? AND HUSSTANDS_FAMILIENR = ? ";
	private List<CensusRecord> household;

	/**
	 * Get a list of household census records from the Derby table
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List<CensusRecord> loadFromDatabase(String dbPath, String kipNr, String nr) throws SQLException {
		CensusRecord ci;
		final List<CensusRecord> cil = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		final PreparedStatement statement = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);
		statement.setString(1, kipNr);
		statement.setString(2, nr);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			ci = new CensusRecord();
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
			ci.setCompString(ci.getFTaar() + ci.getAmt() + ci.getHerred() + ci.getSogn());
			cil.add(ci);
		}

		statement.close();
		return cil;
	}

	/**
	 * @param household the household to set
	 */
	public void setHousehold(List<CensusRecord> household) {
		this.household = household;
	}

	/**
	 * @return the household
	 */
	public List<CensusRecord> getHousehold() {
		return household;
	}

	@Override
	public String[] toStringArray() {
		List<String> ls = new ArrayList<String>();

		for (CensusRecord censusRecord : household) {
			ls.add(censusRecord.toString());
		}

		return (String[]) ls.toArray();
	}
}
