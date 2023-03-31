package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Class representing potential parent pairs for an individual
 *
 * @author Michael Erichsen
 * @version 31. mar. 2023
 *
 */
public class ParentRecord extends ASModel {

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static List<ParentRecord> loadFromDatabase(String dbPath, String id) throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);

		final IndividualRecord current = new IndividualRecord(conn, id);
		ParentRecord pr;
		final List<ParentRecord> lpr = new ArrayList<>();
		String p = current.getParents();

		if ((p == null) || (p.length() == 0)) {
			return lpr;
		}

		final String[] cpp = splitPhonParents(current.getParents());

		final List<IndividualRecord> ldbi = IndividualRecord.loadFromDB(conn);

		String[] dpp;

		for (final IndividualRecord dbi : ldbi) {
			dpp = splitPhonParents(dbi.getParents());

			if (!comparePhoneticParentPairs(cpp, dpp)) {
				continue;
			}

			pr = new ParentRecord();

			pr.setIndividualKey(dbi.getId());
			pr.setName(dbi.getName());
			pr.setLocation(dbi.getBirthPlace());
			pr.setParents(dbi.getParents());
			pr.setBirthDate(dbi.getBirthDate());

			lpr.add(pr);
		}

		return lpr;
	}

	/**
	 * If only one name, it can be either father or mother
	 * 
	 * @param cpp
	 * @param dpp
	 * @return
	 */
	private static boolean comparePhoneticParentPairs(String[] cpp, String[] dpp) {
		if (dpp.length > 1) {
			if (dpp == cpp) {
				return true;
			}
			return false;
		}

		if (dpp.length == 1) {
			if (cpp[0].equals(dpp[0])) {
				return true;
			}

			if (cpp[1].equals(dpp[0])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Split into two names and phonetisize
	 *
	 * @param parents2
	 * @return
	 */
	private static String[] splitPhonParents(String parents2) {
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

		final Fonkod fk = new Fonkod();

		for (int i = 0; i < sa.length; i++) {
			try {
				sa[i] = fk.generateKey(sa[i]);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return sa;
	}

	private String individualKey = "";
	private Date birthDate;
	private String name = "";
	private String parents = "";
	private String location = "";

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
		return location;
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
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
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
		this.location = location;
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
	 * @param year the birth date to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "ParentRecord [" + (individualKey != null ? "individualKey=" + individualKey + ", " : "")
				+ (birthDate != null ? "birthDate=" + birthDate + ", " : "")
				+ (name != null ? "name=" + name + ", " : "") + (parents != null ? "parents=" + parents + ", " : "")
				+ (location != null ? "location=" + location : "") + "]";
	}

	@Override
	public String[] toStringArray() {
		final String[] sa = new String[5];

		sa[0] = individualKey;
		sa[1] = birthDate.toString();
		sa[2] = name;
		sa[3] = parents;
		sa[4] = location;

		return sa;
	}

}
