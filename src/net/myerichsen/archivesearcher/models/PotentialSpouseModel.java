package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
 * @version 7. aug. 2023
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
	private static final String SELECT_CENSUS_2 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ? "
			+ "AND HUSSTANDS_FAMILIENR = ? AND MATR_NR_ADRESSE = ? AND KILDEHENVISNING = ? "
			+ "AND FONNAVN <> ? AND (CIVILSTAND LIKE 'G%' OR CIVILSTAND LIKE 'V%')";
	private static final String SELECT_HUSBAND = "SELECT HUSBAND FROM FAMILY WHERE WIFE = ?";
	private static final String SELECT_WIFE = "SELECT WIFE FROM FAMILY WHERE HUSBAND = ?";

	/**
	 * @param rs2
	 * @param list
	 * @param eventType
	 * @throws SQLException
	 */
	private static void addNewModel(ResultSet rs2, final List<PotentialSpouseModel> list, String eventType)
			throws SQLException {
		PotentialSpouseModel model;
		model = new PotentialSpouseModel();
		model.setKildenavn(rs2.getString("GIVENNAME").trim() + " " + rs2.getString("SURNAME").trim());
		model.setKoen(rs2.getString("SEX"));
		model.setFoedt_kildedato(rs2.getString("BIRTHDATE"));
		model.setKildefoedested(rs2.getString("BIRTHPLACE"));
		model.setFonnavn(rs2.getString("PHONNAME").trim());
		model.setSourceType(eventType);
		list.add(model);
	}

	/**
	 * Get an array of potential spouses
	 *
	 * @param gedcomSchema schema
	 * @param gedcomDbPath database path
	 * @param censusSchema schema
	 * @param censusDbPath database path
	 * @param individual   individual ID
	 * @return an array of models
	 * @throws Exception Various types of exceptions
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
		PreparedStatement gedcomStatement1;
		String string;

		final String gedcomDbURL = "jdbc:derby:" + gedcomDbPath;
		final Connection gedcomConn = DriverManager.getConnection(gedcomDbURL);

		PreparedStatement gedcomStatement = gedcomConn.prepareStatement(SET_SCHEMA);
		gedcomStatement.setString(1, gedcomSchema);
		gedcomStatement.execute();

		gedcomStatement = gedcomConn.prepareStatement(SELECT_HUSBAND);
		gedcomStatement.setString(1, individual);
		ResultSet rs1 = gedcomStatement.executeQuery();

		if (rs1.next()) {
			gedcomStatement1 = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);
			gedcomStatement1.setString(1, rs1.getString("HUSBAND"));
			rs2 = gedcomStatement1.executeQuery();

			if (rs2.next()) {
				addNewModel(rs2, list, "Familie");
			}
		}

		gedcomStatement = gedcomConn.prepareStatement(SELECT_WIFE);
		gedcomStatement.setString(1, individual);
		rs1 = gedcomStatement.executeQuery();

		if (rs1.next()) {
			gedcomStatement1 = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);
			gedcomStatement1.setString(1, rs1.getString("WIFE"));
			rs2 = gedcomStatement1.executeQuery();

			if (rs2.next()) {
				addNewModel(rs2, list, "Familie");
			}
		}

		gedcomStatement = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);
		gedcomStatement.setString(1, individual);
		rs1 = gedcomStatement.executeQuery();

		if (!rs1.next()) {
			return new PotentialSpouseModel[0];
		}

		final String primaryPhonetic = rs1.getString("PHONNAME");

		gedcomStatement = gedcomConn.prepareStatement(SELECT_INDIVIDUAL_SPOUSE);
		gedcomStatement.setString(1, individual);
		gedcomStatement.setString(2, individual);
		rs1 = gedcomStatement.executeQuery();

		gedcomStatement1 = gedcomConn.prepareStatement(SELECT_INDIVIDUAL);

		while (rs1.next()) {
			fonkod.generateKey(rs1.getString("INDIVIDUAL"));
			gedcomStatement1.setString(1, rs1.getString("INDIVIDUAL"));
			rs2 = gedcomStatement1.executeQuery();

			while (rs2.next()) {
				addNewModel(rs2, list, "Hændelse " + rs1.getString("DATE").substring(0, 4));
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

				int ftAar = rs4.getInt("FTAAR");

				if (birthDate.isBlank()) {
					birthDate = rs4.getInt("FOEDEAAR") + "";

					if ("0".equals(birthDate.trim())) {
						ftAar = ftAar == 0 ? Integer.parseInt(kildeHenvisning) : ftAar;
						birthDate = ftAar - rs4.getInt("ALDER") + "";
					}
				}

				model.setSourceType("Folketælling " + ftAar);
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
			final List<Integer> idList = new ArrayList<>();
			gedcomStatement.setString(1, psm.getFonnavn());
			rs1 = gedcomStatement.executeQuery();

			while (rs1.next()) {
				birthYear1 = 0;
				birthYear2 = 0;

				string = rs1.getString("BIRTHDATE");

				if (string != null) {
					matcher = yearPattern.matcher(string);

					if (matcher.find()) {
						birthYear1 = Integer.parseInt(matcher.group(0));
					}
				}

				matcher = yearPattern.matcher(psm.getFoedt_kildedato());

				if (matcher.find()) {
					birthYear2 = Integer.parseInt(matcher.group(0));
				}

				if (Math.abs(birthYear1 - birthYear2) > 5) {
					continue;
				}

				idList.add(Integer.parseInt(rs1.getString("ID").replace("I", "").replace("@", "").trim()));
			}

			Collections.sort(idList);

			for (final Integer anId : idList) {
				sb.append(anId + ", ");
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
	private String sourceType;

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
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
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

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public String toString() {
		return (Kildenavn != null ? Kildenavn + ", " : "") + (Koen != null ? Koen + ", " : "")
				+ (Fonnavn != null ? Fonnavn + ", " : "") + (Kildefoedested != null ? Kildefoedested + ", " : "")
				+ (Foedt_kildedato != null ? Foedt_kildedato + ", " : "") + (id != null ? id + ", " : "")
				+ (sourceType != null ? sourceType : "");
	}

}
