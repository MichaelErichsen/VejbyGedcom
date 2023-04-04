package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Class representing siblings
 *
 * @author Michael Erichsen
 * @version 3. apr. 2023
 *
 */
public class SiblingRecord extends ASModel {

	/**
	 * @param dpp
	 * @param fatherPhonetic
	 * @param motherPhonetic
	 * @return
	 */
	private static boolean comparePhoneticParentPairs(String[] dpp, String fatherPhonetic, String motherPhonetic) {
		if (dpp.length > 1) {
			if ((dpp[0].equals(fatherPhonetic)) && (dpp[1].equals(motherPhonetic))) {
				return true;
			}
			return false;
		}

		if (dpp.length == 1) {
			if ((dpp[0].equals(fatherPhonetic)) || (dpp[0].equals(motherPhonetic))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param parents
	 * @return
	 * @throws SQLException
	 */
	public static SiblingRecord[] loadFromDatabase(String dbPath, String parents) throws SQLException {
		if (parents.length() == 0) {
			return new SiblingRecord[0];
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
	public static SiblingRecord[] loadFromDatabase(String dbPath, String fathersName, String mothersName)
			throws SQLException {
		String fatherPhonetic;
		String motherPhonetic;
		String[] dpp;
		SiblingRecord pr;
		final List<SiblingRecord> lpr = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);

		final Fonkod fk = new Fonkod();

		try {
			fatherPhonetic = fk.generateKey(fathersName);
			motherPhonetic = fk.generateKey(mothersName);
		} catch (final Exception e) {
			System.out.println("Invalid parents: " + fathersName + "," + mothersName);
			return new SiblingRecord[0];
		}

		final List<IndividualRecord> ldbi = IndividualRecord.loadFromDB(conn);

		for (final IndividualRecord dbi : ldbi) {
			dpp = splitParents(dbi.getParents());

			for (int i = 0; i < dpp.length; i++) {
				try {
					System.out.println(dpp[i]);
					dpp[i] = fk.generateKey(dpp[i]);
				} catch (final Exception e) {
					System.out.println("Failed: " + dpp[i]);
					e.printStackTrace();
				}
			}

			if (!comparePhoneticParentPairs(dpp, fatherPhonetic, motherPhonetic)) {
				continue;
			}

			pr = new SiblingRecord();

			pr.setIndividualKey(dbi.getId());
			pr.setName(dbi.getName());
			pr.setLocation(dbi.getBirthPlace());
			pr.setParents(dbi.getParents());
			pr.setBirthDate(dbi.getBirthDate());

			if (dbi.getParents().length() > 0) {
				lpr.add(pr);
			}
		}

		SiblingRecord[] sra = new SiblingRecord[lpr.size()];

		for (int i = 0; i < lpr.size(); i++) {
			sra[i] = lpr.get(i);
		}

		return sra;
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
	private Date birthDate;
	private String name = "";
	private String parents = "";
	private String fatherPhonetic = "";
	private String motherPhonetic = "";
	private String place = "";

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
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
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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
		return "SiblingRecord [" + (individualKey != null ? "individualKey=" + individualKey + ", " : "")
				+ (birthDate != null ? "birthDate=" + birthDate + ", " : "")
				+ (name != null ? "name=" + name + ", " : "") + (parents != null ? "parents=" + parents + ", " : "")
				+ (fatherPhonetic != null ? "fatherPhonetic=" + fatherPhonetic + ", " : "")
				+ (motherPhonetic != null ? "motherPhonetic=" + motherPhonetic + ", " : "")
				+ (place != null ? "place=" + place : "") + "]";
	}

	@Override
	public String[] toStringArray() {
		final String[] sa = new String[5];

		sa[0] = individualKey;
		sa[1] = (birthDate != null ? birthDate.toString() : "");
		sa[2] = name;
		sa[3] = parents;
		sa[4] = place;

		return sa;
	}

}
