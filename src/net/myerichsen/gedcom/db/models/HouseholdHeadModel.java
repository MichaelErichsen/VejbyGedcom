package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a HouseholdHead relocation event
 *
 * @author Michael Erichsen
 * @version 22. apr. 2023
 */

/*
 * Constants
 */
public class HouseholdHeadModel extends ASModel implements Cloneable {
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_NAME = "SELECT GIVENNAME, SURNAME FROM INDIVIDUAL WHERE ID = ?";
	private static final String SELECT_RELOCATION_EVENT = "SELECT DATE, INDIVIDUAL, PLACE, NOTE, SOURCEDETAIL, GIVENNAME, SURNAME "
			+ "FROM VEJBY.EVENT, VEJBY.INDIVIDUAL WHERE SUBTYPE = 'Flytning' "
			+ "AND VEJBY.EVENT.INDIVIDUAL = VEJBY.INDIVIDUAL.ID AND SOURCEDETAIL LIKE ?";
	private static final String KIPNR_PATTERN_1 = "[A-Z]\\d{4}, FT-1855, \\d+";
	private static final String KIPNR_PATTERN_2 = "[A-Z]\\d{4}, \\d+";
	private static final String SELECT_1 = "SELECT * FROM EVENT WHERE INDIVIDUAL = ? AND TYPE = 'Census'";
	private static final String SELECT_2 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND LOEBENR = ?";
	private static final String SELECT_3 = "SELECT MIN(LOEBENR) AS MINLNR FROM CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ? "
			+ "AND HUSSTANDS_FAMILIENR = ?";
	private static final String SELECT_4 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ?"
			+ "AND HUSSTANDS_FAMILIENR = ? AND LOEBENR != ?";
//	private static final String SELECT_5 = "SELECT * FROM CENSUS WHERE KIPNR = ? AND KILDESTEDNAVN = ? "
//			+ "AND HUSSTANDS_FAMILIENR = ?";
//	private static final String SELECT_6 = "SELECT * FROM INDIVIDUAL WHERE PHONNAME = ? AND ID IN "
//			+ "( SELECT INDIVIDUAL FROM VEJBY.EVENT WHERE TYPE = 'Census' AND DATE = ? AND SOURCEDETAIL LIKE ? )";
//	private static final String SELECT_6 = "SELECT * FROM INDIVIDUAL WHERE ID IN "
//			+ "( SELECT INDIVIDUAL FROM VEJBY.EVENT WHERE TYPE = 'Census' AND DATE = ? AND SOURCEDETAIL LIKE ? )";
	private static final String SELECT_7 = "SELECT INDIVIDUAL, SOURCEDETAIL FROM EVENT WHERE TYPE = 'Census' "
			+ "AND DATE = ? AND PLACE = ?";

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
			@SuppressWarnings("unused")
			final int parseInt = Integer.parseInt(findParts[last].trim());
			returnString[1] = findParts[last].trim();
		} catch (final NumberFormatException e) {
			return new String[0];
		}

		return returnString;
	}

	/**
	 * @param vejbySchema
	 * @param vejbyDbPath
	 * @param censusSchema
	 * @param censusDbPath
	 * @param headId
	 * @return
	 * @throws Exception
	 */
	private static List<HouseholdHeadModel> getCensusEvents(String vejbySchema, String vejbyDbPath, String censusSchema,
			String censusDbPath, String headId) throws Exception {
//		final Fonkod fk = new Fonkod();

		Connection conn = DriverManager.getConnection("jdbc:derby:" + vejbyDbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, vejbySchema);
		statement.execute();

		statement = conn.prepareStatement(SELECT_1);
		statement.setString(1, headId);
		ResultSet rs = statement.executeQuery();

		HouseholdHeadModel hhm0, hhm1;
		final List<HouseholdHeadModel> lhhm0 = new ArrayList<>();
		final List<HouseholdHeadModel> lhhm1 = new ArrayList<>();

		while (rs.next()) {
			hhm0 = new HouseholdHeadModel();
			hhm0.setHeadId(headId);
			hhm0.setEventDate(rs.getDate("DATE"));
			hhm0.setPlace(rs.getString("PLACE"));
			hhm0.setSourceDetail(rs.getString("SOURCEDETAIL"));
			hhm0.setEventType("Folketælling");
			lhhm0.add(hhm0);
		}

		final Pattern p1 = Pattern.compile(KIPNR_PATTERN_1);
		final Pattern p2 = Pattern.compile(KIPNR_PATTERN_2);
		String[] findParts = new String[0];

		nextLtr: for (final HouseholdHeadModel hhm : lhhm0) {
			findParts = findParts(p2, hhm.getSourceDetail());

			if (findParts.length != 2) {
				findParts = findParts(p1, hhm.getSourceDetail());

				if (findParts.length != 2) {
					continue nextLtr;
				}
			}

			hhm.setKipNr(findParts[0]);
			hhm.setLoebeNr(Integer.parseInt(findParts[1]));

			conn = DriverManager.getConnection("jdbc:derby:" + censusDbPath);
			statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, censusSchema);
			statement.execute();

			statement = conn.prepareStatement(SELECT_2);
			statement.setString(1, hhm.getKipNr());
			statement.setInt(2, hhm.getLoebeNr());
			rs = statement.executeQuery();

			if (rs.next()) {
				hhm.setKildestednavn(rs.getString("KILDESTEDNAVN"));
				hhm.setHusstandsFamilieNr(rs.getString("HUSSTANDS_FAMILIENR"));
			}

			statement = conn.prepareStatement(SELECT_3);
			statement.setString(1, hhm.getKipNr());
			statement.setString(2, hhm.getKildestednavn());
			statement.setString(3, hhm.getHusstandsFamilieNr());
			rs = statement.executeQuery();

			if (!rs.next() || rs.getInt("MINLNR") != hhm.getLoebeNr()) {
				continue nextLtr;
			}

//			System.out.println(hhm.kipNr + ";" + hhm.loebeNr + ";" + hhm.place + ";" + hhm.husstandsFamilieNr);

			statement = conn.prepareStatement(SELECT_4);
			statement.setString(1, hhm.getKipNr());
			statement.setString(2, hhm.getKildestednavn());
			statement.setString(3, hhm.getHusstandsFamilieNr());
			statement.setInt(4, hhm.getLoebeNr());
			rs = statement.executeQuery();

			while (rs.next()) {
				hhm1 = (HouseholdHeadModel) hhm.clone();
				hhm1.setLoebeNr(rs.getInt("LOEBENR"));
				hhm1.setRelocatorName(rs.getString("KILDENAVN"));
				hhm1.setSourceDetail(hhm1.getKipNr() + "; " + hhm1.getLoebeNr() + "; " + hhm1.getPlace() + "; "
						+ hhm1.getHusstandsFamilieNr());
//				
				lhhm1.add(hhm1);
			}
		}

		// Get list of id's in the household
//		final List<String> idList = new ArrayList<>();
//		statement = conn.prepareStatement(SELECT_5);

//		for (HouseholdHeadModel a : lhhm1)
//			System.out.println(
//					a.kipNr + ";" + a.loebeNr + ";" + a.place + ";" + a.husstandsFamilieNr + ";" + a.relocatorName);

//		for (final HouseholdHeadModel hhm2 : lhhm1) {
//			statement.setString(1, hhm2.getKipNr());
//			statement.setString(2, hhm2.getKildestednavn());
//			statement.setString(3, hhm2.getHusstandsFamilieNr());
//			rs = statement.executeQuery();
//
//			while (rs.next()) {
//				idList.add(rs.getString("FONNAVN"));
//			}
//		}

//		conn = DriverManager.getConnection("jdbc:derby:" + vejbyDbPath);
//		statement = conn.prepareStatement(SET_SCHEMA);
//		statement.setString(1, vejbySchema);
//		statement.execute();
//
//		statement = conn.prepareStatement(SELECT_6);
//
//		for (final HouseholdHeadModel hhm2 : lhhm1) {
//
////			statement.setString(1, fk.generateKey(hhm2.getRelocatorName()));
//			statement.setDate(1, hhm2.getEventDate());
//			statement.setString(2, hhm2.getSourceDetail());
//			rs = statement.executeQuery();
//
////			if (rs.next()) {
////				hhm2.setRelocatorId(rs.getString("ID"));
////				hhm2.setSourceDetail(hhm2.getKipNr() + ", " + hhm2.getLoebeNr() + ", " + hhm2.getPlace());
//////				break;
////			}
//		}
		return lhhm1;
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
	 * @param dbPath
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @return
	 * @throws Exception
	 */
	public static HouseholdHeadModel[] load(String vejbySchema, String vejbyDbPath, String censusSchema,
			String censusDbPath, String headId) throws Exception {
		final List<HouseholdHeadModel> lhhm = getRelocationEvents(vejbySchema, vejbyDbPath, headId);
		final List<HouseholdHeadModel> lhhm2 = getCensusEvents(vejbySchema, vejbyDbPath, censusSchema, censusDbPath,
				headId);
		lhhm.addAll(lhhm2);

		final HouseholdHeadModel[] hhma = new HouseholdHeadModel[lhhm.size()];

		for (int i = 0; i < lhhm.size(); i++) {
			hhma[i] = lhhm.get(i);
		}

		return hhma;
	}

	/**
	 * Find individual ID's in the household
	 * 
	 * @param vejbyDbPath
	 * @param vejbySchema
	 * @param id
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public static List<String> populatePopup(String vejbyDbPath, String vejbySchema, String date, String place,
			String detail) throws SQLException {
		List<String> ls = new ArrayList<>();
		final List<String[]> lsa = new ArrayList<String[]>();
		String[] sa;

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + vejbyDbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, vejbySchema);
		statement.execute();

		statement = conn.prepareStatement(SELECT_7);
		statement.setString(1, date);
		statement.setString(2, place);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			sa = new String[2];
			sa[0] = rs.getString("INDIVIDUAL").trim();
			sa[1] = rs.getString("SOURCEDETAIL").toLowerCase();
			lsa.add(sa);
		}

		statement.close();
		conn.close();

		String[] detailparts = detail.toLowerCase().split("; ");

		for (String[] sa1 : lsa) {
			if (sa1[1].contains(detailparts[1] + ",") && sa1[1].contains(detailparts[3] + ",")) {
				System.out.println(sa1[0] + ";" + detail + ";" + sa1[1]);
				ls.add(sa1[0]);
			}
		}

		return ls;
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
		return sourceDetail.equals("NULL") ? "" : sourceDetail;
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
		return "HouseholdHeadModel [headId=" + headId + ", headName=" + headName + ", eventDate=" + eventDate
				+ ", place=" + place + ", note=" + note + ", sourceDetail=" + sourceDetail + ", relocatorId="
				+ relocatorId + ", relocatorName=" + relocatorName + ", eventType=" + eventType + ", kildestednavn="
				+ kildestednavn + ", husstandsFamilieNr=" + husstandsFamilieNr + ", kipNr=" + kipNr + ", loebeNr="
				+ loebeNr + "]";
	}

}
