package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.archivesearcher.comparators.PotentialSpouseSetComparator;
import net.myerichsen.archivesearcher.util.Fonkod;

/**
 * Class representing a potential spouse
 *
 * @author Michael Erichsen
 * @version 29. jun. 2023
 */

public class PotentialSpouseModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_INDIVIDUAL_SPOUSE = "SELECT * FROM EVENT WHERE TYPE = 'Census' "
			+ "AND SUBTYPE <> 'Witness' AND INDIVIDUAL <> ? AND FAMILY <> '' AND FAMILY IN "
			+ "(SELECT FAMILY FROM EVENT WHERE TYPE = 'Census' AND INDIVIDUAL = ? AND SUBTYPE <> 'Witness')";
	private static final String SELECT_INDIVIDUAL = "SELECT * FROM INDIVIDUAL WHERE ID = ?";
	private static final String SELECT_INDIVIDUAL_3 = "SELECT ID, BIRTHDATE FROM INDIVIDUAL WHERE PHONNAME = ?";
	private static final String SELECT_EVENT_1 = "SELECT * FROM EVENT WHERE TYPE = 'Census' AND INDIVIDUAL = ? AND SUBTYPE <> 'Witness'";
	private static final String SELECT_CENSUS_1 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND FONNAVN = ?";
	private static final String SELECT_CENSUS_2 = "SELECT * FROM VEJBY.CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ? "
			+ "AND HUSSTANDS_FAMILIENR = ? AND MATR_NR_ADRESSE = ? AND KILDEHENVISNING = ? "
			+ "AND FONNAVN <> ? AND (CIVILSTAND LIKE 'G%' OR CIVILSTAND LIKE 'V%')";

	/**
	 * @param gedcomSchema
	 * @param gedcomDbPath
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static PotentialSpouseModel[] load(String gedcomSchema, String gedcomDbPath, String censusSchema,
			String censusDbPath, String individual) throws Exception {
		final Fonkod fonkod = new Fonkod();
		ResultSet rs2;
		PotentialSpouseModel model;
		final List<PotentialSpouseModel> list = new ArrayList<>();
		final Pattern kipNrPattern = Pattern.compile("[A-Z]\\d{4}");
		final Pattern yearPattern = Pattern.compile("\\d{4}");
		Matcher matcher;
		String kipnr = "";
		String civilstand;
		String kildestednavn;
		String husstandsFamilieNr;
		String matrNrAdresse;
		String kildeHenvisning;
		ResultSet rs4;
		String birthDate;
		StringBuilder sb;
		int birthYear1;
		int birthYear2;

		final String gedcomDbURL = "jdbc:derby:" + gedcomDbPath;
		final Connection gedcomConn = DriverManager.getConnection(gedcomDbURL);

		PreparedStatement gedcomStatement = gedcomConn.prepareStatement(SET_SCHEMA);
		gedcomStatement.setString(1, gedcomSchema);
		gedcomStatement.execute();

		gedcomStatement = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);
		gedcomStatement.setString(1, individual);
		ResultSet rs1 = gedcomStatement.executeQuery();

		if (!rs1.next()) {
			return new PotentialSpouseModel[0];
		}
		String primaryPhonetic = rs1.getString("PHONNAME");

		gedcomStatement = gedcomConn.prepareStatement(SELECT_INDIVIDUAL_SPOUSE);
		gedcomStatement.setString(1, individual);
		gedcomStatement.setString(2, individual);
		rs1 = gedcomStatement.executeQuery();

		final PreparedStatement gedcomStatement1 = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);

		while (rs1.next()) {
			fonkod.generateKey(rs1.getString("INDIVIDUAL"));
			gedcomStatement1.setString(1, rs1.getString("INDIVIDUAL"));
			rs2 = gedcomStatement1.executeQuery();

			while (rs2.next()) {
				model = new PotentialSpouseModel();
				model.setKildenavn(rs2.getString("GIVENNAME").trim() + " " + rs2.getString("SURNAME").trim());
				model.setKoen(rs2.getString("SEX"));
				model.setFoedt_kildedato(rs2.getString("BIRTHDATE"));
				model.setKildefoedested(rs2.getString("BIRTHPLACE"));
				model.setFonnavn(rs2.getString("PHONNAME").trim());
				list.add(model);
			}
		}

		gedcomStatement = gedcomConn.prepareStatement(SELECT_EVENT_1);
		gedcomStatement.setString(1, individual);
		rs1 = gedcomStatement.executeQuery();

		while (rs1.next()) {
			matcher = kipNrPattern.matcher(rs1.getString("SOURCEDETAIL"));

			if (matcher.find()) {
				kipnr = matcher.group(0);
			}
		}

		final String censusDbURL = "jdbc:derby:" + censusDbPath;
		final Connection censusConn = DriverManager.getConnection(censusDbURL);

		PreparedStatement censusStatement = censusConn.prepareStatement(SET_SCHEMA);
		censusStatement.setString(1, censusSchema);
		censusStatement.execute();

		censusStatement = censusConn.prepareStatement(SELECT_CENSUS_1);
		censusStatement.setString(1, kipnr);
		censusStatement.setString(2, primaryPhonetic);
		final ResultSet rs3 = censusStatement.executeQuery();

		PreparedStatement censusStatement1 = censusConn.prepareStatement(SELECT_CENSUS_1); // NB

		while (rs3.next()) {
			civilstand = rs3.getString("CIVILSTAND");

			if (civilstand.startsWith("U")) {
				continue;
			}

			kipnr = rs3.getString("KIPNR").trim();
			kildestednavn = rs3.getString("KILDESTEDNAVN");
			husstandsFamilieNr = rs3.getString("HUSSTANDS_FAMILIENR");
			matrNrAdresse = rs3.getString("MATR_NR_ADRESSE");
			kildeHenvisning = rs3.getString("KILDEHENVISNING");

			censusStatement1 = censusConn.prepareStatement(SELECT_CENSUS_2);
			censusStatement1.setString(1, kipnr);
			censusStatement1.setString(2, kildestednavn);
			censusStatement1.setString(3, husstandsFamilieNr);
			censusStatement1.setString(4, matrNrAdresse);
			censusStatement1.setString(5, kildeHenvisning);
			censusStatement1.setString(6, primaryPhonetic);
			rs4 = censusStatement1.executeQuery();

			if (rs4.next()) {
				model = new PotentialSpouseModel();
				model.setKildenavn(rs4.getString("KILDENAVN"));
				model.setKoen(rs4.getString("KOEN"));
				birthDate = rs4.getString("FOEDT_KILDEDATO");

				if (birthDate.isBlank()) {
					birthDate = rs4.getInt("FOEDEAAR") + "";

					if (birthDate.trim().equals("0")) {
						birthDate = rs4.getInt("FTAAR") - rs4.getInt("ALDER") + "";
					}
				}
				model.setFoedt_kildedato(birthDate);

				model.setKildefoedested(rs4.getString("KILDEFOEDESTED"));
				model.setFonnavn(rs4.getString("FONNAVN"));
				list.add(model);
			}
		}

		censusConn.close();

		gedcomStatement = gedcomConn.prepareStatement(SELECT_INDIVIDUAL_3);

		for (final PotentialSpouseModel psm : list) {
			sb = new StringBuilder();
			gedcomStatement.setString(1, psm.getFonnavn());
			rs1 = gedcomStatement.executeQuery();

			while (rs1.next()) {
				birthYear1 = 0;
				birthYear2 = 0;

				matcher = yearPattern.matcher(rs1.getString("BIRTHDATE"));

				if (matcher.find()) {
					birthYear1 = Integer.parseInt(matcher.group(0));
				}

				matcher = yearPattern.matcher(psm.getFoedt_kildedato());

				if (matcher.find()) {
					birthYear2 = Integer.parseInt(matcher.group(0));
				}

				if (Math.abs(birthYear1 - birthYear2) > 5) {
					continue;
				}

				sb.append(rs1.getString("ID").replace("I", "").replace("@", "").trim() + ", ");
			}

			psm.setId(sb.toString());
		}

		gedcomConn.close();

		final SortedSet<PotentialSpouseModel> set = new TreeSet<>(new PotentialSpouseSetComparator());
		set.addAll(list);

		final PotentialSpouseModel[] array = new PotentialSpouseModel[set.size()];
		int i = 0;

		for (final Iterator<PotentialSpouseModel> iterator = set.iterator(); iterator.hasNext();) {
			array[i] = iterator.next();
			i++;
		}

		return array;
	}

	private String Kildenavn = "";
	private String Koen = "";
	private String Fonnavn = "";
	private String Kildefoedested = "";
	private String Foedt_kildedato = "";
	private String id = "";

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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the kildefoedested
	 */
	public String getKildefoedested() {
		return Kildefoedested;
	}

	/**
	 * @return the kildenavn
	 */
	public String getKildenavn() {
		return Kildenavn;
	}

	/**
	 * @return the koen
	 */
	public String getKoen() {
		return Koen;
	}

	/**
	 * @param foedt_kildedato the foedt_kildedato to set
	 */
	public void setFoedt_kildedato(String foedt_kildedato) {
		Foedt_kildedato = foedt_kildedato;
	}

	/**
	 * @param fonnavn the fonnavn to set
	 */
	public void setFonnavn(String fonnavn) {
		Fonnavn = fonnavn;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param kildefoedested the kildefoedested to set
	 */
	public void setKildefoedested(String kildefoedested) {
		Kildefoedested = kildefoedested;
	}

	/**
	 * @param kildenavn the kildenavn to set
	 */
	public void setKildenavn(String kildenavn) {
		Kildenavn = kildenavn;
	}

	/**
	 * @param koen the koen to set
	 */
	public void setKoen(String koen) {
		Koen = koen;
	}

	@Override
	public String toString() {
		return (Kildenavn != null ? Kildenavn + ", " : "") + (Koen != null ? Koen + ", " : "")
				+ (Fonnavn != null ? Fonnavn + ", " : "") + (Kildefoedested != null ? Kildefoedested + ", " : "")
				+ (Foedt_kildedato != null ? Foedt_kildedato + ", " : "") + (id != null ? id : "");
	}

}
