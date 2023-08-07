package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.archivesearcher.comparators.HouseholdHeadSetComparator;

/**
 * Class representing a HouseholdHead relocation or census event or military
 * roll record
 *
 * @author Michael Erichsen
 * @version 18. maj 2023
 */

/*
 * Constants
 */
public class HouseholdHeadModel extends ASModel implements Cloneable {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_NAME = "SELECT GIVENNAME, SURNAME FROM INDIVIDUAL WHERE ID = ?";
	private static final String SELECT_RELOCATION_EVENT = "SELECT DATE, INDIVIDUAL, PLACE, NOTE, SOURCEDETAIL, GIVENNAME, SURNAME "
			+ "FROM EVENT, INDIVIDUAL WHERE SUBTYPE = 'Flytning' "
			+ "AND EVENT.INDIVIDUAL = INDIVIDUAL.ID AND SOURCEDETAIL LIKE ?";
	private static final String KIPNR_PATTERN_1 = "[A-Z]\\d{4}, FT-1855, \\d+";
	private static final String KIPNR_PATTERN_2 = "[A-Z]\\d{4}, \\d+";
	private static final String SELECT_1 = "SELECT * FROM EVENT WHERE INDIVIDUAL = ? AND TYPE = 'Census'";
	private static final String SELECT_2 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND LOEBENR = ?";
	private static final String SELECT_3 = "SELECT MIN(LOEBENR) AS MINLNR FROM CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ? "
			+ "AND HUSSTANDS_FAMILIENR = ? AND MATR_NR_ADRESSE = ?";
	private static final String SELECT_6 = "SELECT GIVENNAME, SURNAME FROM INDIVIDUAL WHERE ID = ?";
	private static final String SELECT_M = "SELECT * FROM LAEGD.LAEGD, LAEGD.RULLE WHERE GEDCOMID = ? AND LAEGD.LAEGD.LAEGDID = LAEGD.RULLE.LAEGDID";

	/**
	 * @param p
	 * @param sourceDetailString
	 * @return
	 */
	private static String[] findParts(Pattern p, String sourceDetailString) {
		final Matcher m = p.matcher(sourceDetailString);
		final String[] returnString = new String[2];

		if (!m.find()) {
			return new String[0];
		}

		final String find = m.group(0);
		final String[] findParts = find.split(",");

		if (findParts.length < 2) {
			return new String[0];
		}

		returnString[0] = findParts[0];

		final int last = findParts.length - 1;

		try {
			Integer.parseInt(findParts[last].trim());
			returnString[1] = findParts[last].trim();
		} catch (final NumberFormatException e) {
			return new String[0];
		}

		return returnString;
	}

	/**
	 * @param parishSchema
	 * @param parishDbPath
	 * @param censusSchema
	 * @param censusDbPath
	 * @param relocatorId
	 * @return
	 * @throws Exception
	 */
	private static List<HouseholdHeadModel> getCensusEvents(String parishSchema, String parishDbPath,
			String censusSchema, String censusDbPath, String relocatorId) throws Exception {

		final Connection connV = DriverManager.getConnection("jdbc:derby:" + parishDbPath);
		PreparedStatement statementV = connV.prepareStatement(SET_SCHEMA);
		statementV.setString(1, parishSchema);
		statementV.execute();

		final Connection connC = DriverManager.getConnection("jdbc:derby:" + censusDbPath);
		PreparedStatement statementC = connC.prepareStatement(SET_SCHEMA);
		statementC.setString(1, censusSchema);
		statementC.execute();

		// Get a list of census events for this individual
		statementV = connV.prepareStatement(SELECT_1);
		statementV.setString(1, relocatorId);
		ResultSet rs = statementV.executeQuery();

		HouseholdHeadModel hhm0;
		HouseholdHeadModel hhm2;
		final List<HouseholdHeadModel> lhhm0 = new ArrayList<>();
		final List<HouseholdHeadModel> lhhm1 = new ArrayList<>();
		String sd;

		while (rs.next()) {
			hhm0 = new HouseholdHeadModel();
			hhm0.setRelocatorId(relocatorId);
			hhm0.setEventDate(rs.getDate("DATE"));
			hhm0.setPlace(rs.getString("PLACE"));

			// Use list of witnesses if present
			sd = rs.getString("NOTE");
			if (sd.length() == 0) {
				sd = rs.getString("SOURCEDETAIL");
			}
			hhm0.setSourceDetail(sd);

			hhm0.setEventType("Folketælling");
			lhhm0.add(hhm0);
		}

		statementV = connV.prepareStatement(SELECT_6);

		for (final HouseholdHeadModel hhm : lhhm0) {
			statementV.setString(1, relocatorId);
			rs = statementV.executeQuery();

			if (rs.next()) {
				hhm.setRelocatorName(rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim());
			}
		}

		final Pattern p1 = Pattern.compile(KIPNR_PATTERN_1);
		final Pattern p2 = Pattern.compile(KIPNR_PATTERN_2);
		String[] findParts = new String[0];
		String[] witnesses;

		// Handle each census event for this individual
		ltrLoop: for (final HouseholdHeadModel hhm : lhhm0) {

			// If we have a list of witnesses
			if (hhm.getSourceDetail().startsWith("@I")) {
				witnesses = hhm.getSourceDetail().split(" ");

				for (final String id : witnesses) {
					statementV.setString(1, id);
					rs = statementV.executeQuery();

					if (rs.next()) {
						hhm2 = (HouseholdHeadModel) hhm.clone();
						hhm2.setRelocatorId(id);
						hhm2.setRelocatorName(rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim());
						lhhm1.add(hhm2);
					}
				}
			} else {
				// If we do not have a list of witnesses
				findParts = findParts(p2, hhm.getSourceDetail());

				if (findParts.length != 2) {
					findParts = findParts(p1, hhm.getSourceDetail());

					if (findParts.length != 2) {
						lhhm1.add(hhm);
						continue ltrLoop;
					}
				}

				hhm.setKipNr(findParts[0]);
				hhm.setLoebeNr(Integer.parseInt(findParts[1]));

				statementC = connC.prepareStatement(SELECT_2);
				statementC.setString(1, hhm.getKipNr());
				statementC.setInt(2, hhm.getLoebeNr());
				rs = statementC.executeQuery();

				if (rs.next()) {
					hhm.setHeadName(rs.getString("KILDENAVN"));
					hhm.setKildestednavn(rs.getString("KILDESTEDNAVN"));
					hhm.setHusstandsFamilieNr(rs.getString("HUSSTANDS_FAMILIENR"));
					hhm.setMatrNrAdresse(rs.getString("MATR_NR_ADRESSE"));
				}

				statementC = connC.prepareStatement(SELECT_3);
				statementC.setString(1, hhm.getKipNr());
				statementC.setString(2, hhm.getKildestednavn());
				statementC.setString(3, hhm.getHusstandsFamilieNr());
				statementC.setString(4, hhm.getMatrNrAdresse());
				rs = statementC.executeQuery();

				if (!rs.next() || rs.getInt("MINLNR") != hhm.getLoebeNr()) {
					continue ltrLoop;
				}

				lhhm1.add(hhm);
			}
		}
		return lhhm1;
	}

	/**
	 * @param milrollDbPath
	 * @param milRollSchema
	 * @param relocatorId
	 * @return
	 * @throws Exception
	 */
	private static List<HouseholdHeadModel> getMilRollEvents(String milrollPath, String milrollSchema,
			String relocatorId) throws Exception {
		final Connection connM = DriverManager.getConnection("jdbc:derby:" + milrollPath);
		PreparedStatement statementM = connM.prepareStatement(SET_SCHEMA);
		statementM.setString(1, milrollSchema);
		statementM.execute();

		// Get a list of military roll records for this individual
		statementM = connM.prepareStatement(SELECT_M);
		statementM.setString(1, relocatorId);
		final ResultSet rs = statementM.executeQuery();

		HouseholdHeadModel hhm;
		final List<HouseholdHeadModel> lhhm = new ArrayList<>();
		String s = "";

		while (rs.next()) {
			hhm = new HouseholdHeadModel();
			hhm.setHeadId("");
			hhm.setHeadName(rs.getString("FADER"));
			hhm.setEventDate(Date.valueOf(rs.getString("AAR") + "-01-01"));
			hhm.setPlace(rs.getString("OPHOLD"));
			hhm.setNote("");
			s = rs.getString("ANMAERKNINGER").trim();
			s = s.length() > 0 ? ", " + s : "";
			hhm.setSourceDetail(
					"Alder " + rs.getString("ALDER") + ", fødested " + rs.getString("FOEDESTED").trim() + s);
			hhm.setRelocatorId(relocatorId);
			hhm.setRelocatorName(rs.getString("SOEN"));
			hhm.setEventType("Lægdsrulle");
			lhhm.add(hhm);
		}

		return lhhm;
	}

	/**
	 * Get a list of relocation data
	 *
	 * @param schema
	 * @param dbPath
	 * @param headId
	 * @return
	 * @throws SQLException
	 */
	private static List<HouseholdHeadModel> getRelocationEvents(String schema, String dbPath, String headId)
			throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		HouseholdHeadModel hhm;
		final List<HouseholdHeadModel> lhhm = new ArrayList<>();

		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_NAME);
		statement.setString(1, headId);
		ResultSet rs = statement.executeQuery();

		String headName;

		if (!rs.next()) {
			return lhhm;
		}

		headName = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();

		statement = conn.prepareStatement(SELECT_RELOCATION_EVENT);
		statement.setString(1, "%" + headName + "%");
		rs = statement.executeQuery();

		while (rs.next()) {
			hhm = new HouseholdHeadModel();
			hhm.setHeadId(headId);
			hhm.setHeadName(headName);
			hhm.setEventDate(rs.getDate("DATE"));
			hhm.setPlace(rs.getString("PLACE"));
			hhm.setNote(rs.getString("NOTE"));
			hhm.setSourceDetail(rs.getString("SOURCEDETAIL"));
			hhm.setRelocatorId(rs.getString("INDIVIDUAL"));
			hhm.setEventType("Flytning");
			lhhm.add(hhm);
		}

		statement = conn.prepareStatement(SELECT_NAME);

		for (final HouseholdHeadModel householdHeadModel : lhhm) {
			statement.setString(1, householdHeadModel.getRelocatorId());
			rs = statement.executeQuery();

			if (rs.next()) {
				householdHeadModel
						.setRelocatorName(rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim());
			}
		}

		statement.close();
		conn.close();

		return lhhm;
	}

	/**
	 * Load from database
	 *
	 * @param parishDbPath
	 * @param parishSchema
	 * @param censusDbPath
	 * @param censusSchema
	 * @param milrollPath
	 * @param milrollSchema
	 * @param headId
	 * @return
	 * @throws Exception
	 */
	public static HouseholdHeadModel[] load(String parishDbPath, String parishSchema, String censusDbPath,
			String censusSchema, String milrollPath, String milrollSchema, String headId) throws Exception {
		final List<HouseholdHeadModel> list = getRelocationEvents(parishSchema, parishDbPath, headId);
		final List<HouseholdHeadModel> lhhm1 = getCensusEvents(parishSchema, parishDbPath, censusSchema, censusDbPath,
				headId);
		final List<HouseholdHeadModel> lhhm2 = getMilRollEvents(milrollPath, milrollSchema, headId);

		list.addAll(lhhm1);
		list.addAll(lhhm2);

		final SortedSet<HouseholdHeadModel> set = new TreeSet<>(new HouseholdHeadSetComparator());
		set.addAll(list);

		final HouseholdHeadModel[] array = new HouseholdHeadModel[set.size()];
		int i = 0;

		for (final Iterator<HouseholdHeadModel> iterator = set.iterator(); iterator.hasNext();) {
			array[i] = iterator.next();
			i++;

		}

		return array;
	}

	private String headId = "";
	private String headName = "";
	private Date eventDate = null;
	private String place = "";
	private String note = "";
	private String sourceDetail = "";
	private String relocatorId = "";
	private String relocatorName = "";
	private String eventType = "";
	private String kildestednavn = "";
	private String husstandsFamilieNr = "";
	private String kipNr = "";
	private int loebeNr = 0;
	private String matrNrAdresse = "";

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @return the headId
	 */
	public String getHeadId() {
		return headId;
	}

	/**
	 * @return the headName
	 */
	public String getHeadName() {
		return headName;
	}

	/**
	 * @return the husstandsFamilieNr
	 */
	public String getHusstandsFamilieNr() {
		return husstandsFamilieNr;
	}

	/**
	 * @return the kildestednavn
	 */
	public String getKildestednavn() {
		return kildestednavn;
	}

	/**
	 * @return the kipNr
	 */
	public String getKipNr() {
		return kipNr;
	}

	/**
	 * @return the loebeNr
	 */
	public int getLoebeNr() {
		return loebeNr;
	}

	/**
	 * @return the matrNrAdresse
	 */
	public String getMatrNrAdresse() {
		return matrNrAdresse;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the relocatorId
	 */
	public String getRelocatorId() {
		return relocatorId;
	}

	/**
	 * @return the relocatorName
	 */
	public String getRelocatorName() {
		return relocatorName;
	}

	/**
	 * @return the sourceDetail
	 */
	public String getSourceDetail() {
		return "NULL".equals(sourceDetail) ? "" : sourceDetail;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @param headId the headId to set
	 */
	public void setHeadId(String headId) {
		this.headId = headId;
	}

	/**
	 * @param headName the headName to set
	 */
	public void setHeadName(String headName) {
		this.headName = headName;
	}

	/**
	 * @param husstandsFamilieNr the husstandsFamilieNr to set
	 */
	public void setHusstandsFamilieNr(String husstandsFamilieNr) {
		this.husstandsFamilieNr = husstandsFamilieNr;
	}

	/**
	 * @param kildestednavn the kildestednavn to set
	 */
	public void setKildestednavn(String kildestednavn) {
		this.kildestednavn = kildestednavn;
	}

	/**
	 * @param kipNr the kipNr to set
	 */
	public void setKipNr(String kipNr) {
		this.kipNr = kipNr;
	}

	/**
	 * @param loebeNr the loebeNr to set
	 */
	public void setLoebeNr(int loebeNr) {
		this.loebeNr = loebeNr;
	}

	/**
	 * @param matrNrAdresse the matrNrAdresse to set
	 */
	public void setMatrNrAdresse(String matrNrAdresse) {
		this.matrNrAdresse = matrNrAdresse;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param relocatorId the relocatorId to set
	 */
	public void setRelocatorId(String relocatorId) {
		this.relocatorId = relocatorId;
	}

	/**
	 * @param relocatorName the relocatorName to set
	 */
	public void setRelocatorName(String relocatorName) {
		this.relocatorName = relocatorName;
	}

	/**
	 * @param sourceDetail the sourceDetail to set
	 */
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	@Override
	public String toString() {
		return (headId.length() > 0 ? headId.trim() + ", " : "") + (headName.length() > 0 ? headName.trim() + ", " : "")
				+ (eventDate != null ? eventDate + ", " : "") + (place.length() > 0 ? place.trim() + ", " : "")
				+ (note.length() > 0 ? note.trim() + ", " : "")
				+ (relocatorId.length() > 0 ? relocatorId.trim() + ", " : "")
				+ (relocatorName.length() > 0 ? relocatorName.trim() + ", " : "")
				+ (eventType.length() > 0 ? eventType.trim() + ", " : "")
				+ (kildestednavn.length() > 0 ? kildestednavn.trim() + ", " : "")
				+ (husstandsFamilieNr.length() > 0 ? husstandsFamilieNr.trim() + ", " : "")
				+ (kipNr.length() > 0 ? kipNr.trim() + ", " : "") + loebeNr + ", "
				+ (matrNrAdresse.length() > 0 ? matrNrAdresse : "")
				+ (sourceDetail.length() > 0 ? "\n\n" + sourceDetail.trim() : "");
	}

}
