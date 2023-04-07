package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Class representing siblings from the parents table
 *
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SiblingsRecord extends ASModel {
	private static String SELECT = "SELECT * FROM VEJBY.PARENTS WHERE FATHERPHONETIC = ? " + "AND MOTHERPHONETIC = ?";

	/**
	 * Get a list of objects from the database
	 * 
	 * @param sa
	 * @return
	 * @throws SQLException
	 */
	public static SiblingsRecord[] loadFromDatabase(String[] args) throws SQLException {
		switch (args.length) {
		case 2: {
			return loadFromDatabase(args[0], args[1]);
		}
		case 3: {
			return loadFromDatabase(args[0], args[1], args[2]);
		}
		case 4: {
			return loadFromDatabase(args[0], args[1], args[2], args[3]);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + args.length + ": '" + args[0] + "'");
		}
	}

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param parents
	 * @return
	 * @throws SQLException
	 */
	private static SiblingsRecord[] loadFromDatabase(String dbPath, String parents) throws SQLException {
		if (parents.length() == 0) {
			return new SiblingsRecord[0];
		}

		final String[] p = splitParents(parents);

		return loadFromDatabase(dbPath, p[0], p[1]);
	}

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param fathersName
	 * @param mothersName
	 * @return
	 * @throws SQLException
	 */
	private static SiblingsRecord[] loadFromDatabase(String dbPath, String fathersName, String mothersName)
			throws SQLException {
		String fatherPhonetic;
		String motherPhonetic;
		SiblingsRecord pr;
		final List<SiblingsRecord> lpr = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);

		final Fonkod fk = new Fonkod();

		try {
			fatherPhonetic = fk.generateKey(fathersName);
			motherPhonetic = fk.generateKey(mothersName);
		} catch (final Exception e) {
			System.out.println("Invalid parents: " + fathersName + "," + mothersName);
			return new SiblingsRecord[0];
		}

		PreparedStatement statement = conn.prepareStatement(SELECT);
		statement.setString(1, fatherPhonetic);
		statement.setString(2, motherPhonetic);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			pr = new SiblingsRecord();
			pr.setIndividualKey(rs.getString("INDIVIDUALKEY"));
			pr.setName(rs.getString("NAME"));
			pr.setLocation(rs.getString("PLACE"));
			pr.setParents(rs.getString("PARENTS"));
			pr.setBirthDate(rs.getInt("BIRTHYEAR"));
			lpr.add(pr);
		}

		final SiblingsRecord[] sra = new SiblingsRecord[lpr.size()];

		for (int i = 0; i < lpr.size(); i++) {
			sra[i] = lpr.get(i);
		}

		return sra;
	}

	/**
	 * @param dbPath
	 * @param phonName
	 * @param birthDate2
	 * @param deathDate
	 * @return
	 */
	private static SiblingsRecord[] loadFromDatabase(String dbPath, String phonName, String birthDate2,
			String deathDate) {
		System.out.println("Not yet implemented");
		return null;
	}

	/**
	 * Split into two names and phonetisize
	 *
	 * @param parents2
	 * @return
	 */
	private static String[] splitParents(String parents2) {
		String s = parents2.replaceAll("\\d", "").replaceAll("\\.", "").toLowerCase();
		s = s.replace(", f.", "");
		String[] sa = s.split(",");
		final String[] words = sa[0].split(" ");

		final String filter = "af bager gamle gmd i inds junior kirkesanger pige pigen portner proprietær sadelmager "
				+ "skolelærer skovfoged slagter smed smedesvend snedker søn ugift ugifte unge ungkarl " + "uægte år";
		final StringBuffer sb = new StringBuffer();

		for (final String word : words) {
			if (!filter.contains(word)) {
				sb.append(word + " ");
			}
		}

		s = sb.toString();

		sa = s.split(" og ");

		return sa;
	}

	private String individualKey = "";
	private int birthYear;
	private String name = "";
	private String parents = "";
	private String fatherPhonetic = "";
	private String motherPhonetic = "";

	private String place = "";

	/**
	 * @return the birthDate
	 */
	public int getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the fatherPhonetic
	 */
	public String getFatherPhonetic() {
		return fatherPhonetic;
	}

	/**
	 * @return the individualKey
	 */
	public String getIndividualKey() {
		return individualKey;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return place;
	}

	/**
	 * @return the motherPhonetic
	 */
	public String getMotherPhonetic() {
		return motherPhonetic;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the parents
	 */
	public String getParents() {
		return parents;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param year the birth date to set
	 */
	public void setBirthDate(int j) {
		this.birthYear = j;
	}

	/**
	 * @param fatherPhonetic the fatherPhonetic to set
	 */
	public void setFatherPhonetic(String fatherPhonetic) {
		this.fatherPhonetic = fatherPhonetic;
	}

	/**
	 * @param individualKey the individualKey to set
	 */
	public void setIndividualKey(String individualKey) {
		this.individualKey = individualKey;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.place = location;
	}

	/**
	 * @param motherPhonetic the motherPhonetic to set
	 */
	public void setMotherPhonetic(String motherPhonetic) {
		this.motherPhonetic = motherPhonetic;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param parents the parents to set
	 */
	public void setParents(String parents) {
		this.parents = parents;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "SiblingsRecord [" + (individualKey != null ? "individualKey=" + individualKey + ", " : "") + birthYear
				+ (name != null ? "name=" + name + ", " : "") + (parents != null ? "parents=" + parents + ", " : "")
				+ (fatherPhonetic != null ? "fatherPhonetic=" + fatherPhonetic + ", " : "")
				+ (motherPhonetic != null ? "motherPhonetic=" + motherPhonetic + ", " : "")
				+ (place != null ? "place=" + place : "") + "]";
	}

	@Override
	public String[] toStringArray() {
		final String[] sa = new String[5];

		sa[0] = individualKey;
		sa[1] = birthYear + "";
		sa[2] = name;
		sa[3] = parents;
		sa[4] = place;

		return sa;
	}

}
