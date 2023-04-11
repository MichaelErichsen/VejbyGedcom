package net.myerichsen.gedcom.db.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.gedcom.util.Fonkod;

/**
 * Class representing the individual data
 *
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class IndividualModel extends ASModel {
	private static final String SELECT_INDIVIDUAL = "SELECT * FROM INDIVIDUAL";
	private static final String SELECT_BIRTH_EVENT = "SELECT * FROM EVENT WHERE INDIVIDUAL = "
			+ "? AND (TYPE = 'Birth' OR TYPE = 'Christening') ORDER BY DATE";
	private static final String SELECT_DEATH_EVENT = "SELECT * FROM EVENT WHERE INDIVIDUAL = "
			+ "? AND (TYPE = 'Death' OR TYPE = 'Burial') ORDER BY DATE";
	private static final String SELECT_PARENTS = "SELECT * FROM FAMILY WHERE ID = ?";
	private static final String SELECT_INDIVIDUAL_FROM_ID = "SELECT * FROM INDIVIDUAL WHERE ID = ?";
	private static final String SELECT_PARENTS_FROM_CHRISTENING = "SELECT * FROM EVENT WHERE TYPE = 'Christening' AND INDIVIDUAL = ?";

	/**
	 * Find parent names from christening event
	 *
	 * @param conn
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private static String findParentsFromChristeningEvent(Connection conn, String id, String schema)
			throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_PARENTS_FROM_CHRISTENING);
		statement.setString(1, id);
		final ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			final String string = rs.getString("NOTE");

			if (string == null) {
				return "";
			}
			return string;
		}

		return "";
	}

	/**
	 * Find name of individual from its id
	 *
	 * @param conn
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private static String getNameFromId(Connection conn, String id, String schema) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_INDIVIDUAL_FROM_ID);
		statement.setString(1, id);
		final ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			return (rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME")).trim();
		}

		return "";
	}

	/**
	 * Load list of individuals with added data from Derby
	 *
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static List<IndividualModel> loadFromDB(Connection conn, String schema) throws SQLException {
		IndividualModel individual;
		final List<IndividualModel> ldbi = new ArrayList<>();
		String givenName = "";
		String surName = "";
		String husbandId = "";
		String wifeId = "";
		String husbandName = "";
		String wifeName = "";

		// Read all individuals into a list
		PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_INDIVIDUAL);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			individual = new IndividualModel();

			if (rs.getString("ID") != null) {
				individual.setId(rs.getString("ID"));
			}
			if (rs.getString("GIVENNAME") != null) {
				givenName = rs.getString("GIVENNAME").trim();
			}
			if (rs.getString("SURNAME") != null) {
				surName = rs.getString("SURNAME");
			}
			individual.setName((givenName + " " + surName).trim());

			if (rs.getString("SEX") != null) {
				individual.setSex(rs.getString("SEX"));
			}
			if (rs.getString("FAMC") != null) {
				individual.setFamc(rs.getString("FAMC"));
			}
			if (rs.getString("PHONNAME") != null) {
				individual.setPhonName(rs.getString("PHONNAME"));
			}

			ldbi.add(individual);
		}

		// Add birth data
		for (final IndividualModel dbi : ldbi) {
			final PreparedStatement statement2 = conn.prepareStatement(SELECT_BIRTH_EVENT);
			statement2.setString(1, dbi.getId());
			rs = statement2.executeQuery();

			if (rs.next()) {
				if (rs.getDate("DATE") != null) {
					dbi.setBirthDate(rs.getDate("DATE"));
				}
				if (rs.getString("PLACE") != null) {
					dbi.setBirthPlace(rs.getString("PLACE"));
				}
			}
		}

		// Add death data
		for (final IndividualModel dbi : ldbi) {
			final PreparedStatement statement3 = conn.prepareStatement(SELECT_DEATH_EVENT);
			statement3.setString(1, dbi.getId());
			rs = statement3.executeQuery();

			if (rs.next()) {
				if (rs.getDate("DATE") != null) {
					dbi.setDeathDate(rs.getDate("DATE"));
				}
				if (rs.getString("PLACE") != null) {
					dbi.setDeathPlace(rs.getString("PLACE"));
				}
			}
		}

		// Add parents
		for (final IndividualModel dbi : ldbi) {
			// Try to find from FAMC record
			final PreparedStatement statement4 = conn.prepareStatement(SELECT_PARENTS);
			statement4.setString(1, dbi.getFamc());
			rs = statement4.executeQuery();

			if (rs.next()) {
				// Find names from INDIVIDUAL TABLE
				husbandId = rs.getString("HUSBAND");
				wifeId = rs.getString("WIFE");

				if (husbandId != null) {
					husbandName = getNameFromId(conn, husbandId, schema);
				}

				if (wifeId != null) {
					wifeName = getNameFromId(conn, wifeId, schema);
				}

				dbi.setParents(husbandName + " og " + wifeName);
			} else {
				// Find names from christening event source detail
				dbi.setParents(findParentsFromChristeningEvent(conn, dbi.getId(), schema));
			}
		}
		return ldbi;
	}

	private String id = "";
	private String name = "";
	private String sex = "";
	private String famc = "";
	private String phonName = "";
	private Date birthDate = null;
	private String birthPlace = "";
	private Date deathDate = null;
	private String deathPlace = "";
	private String parents = "";

	/**
	 * No-arg constructor
	 *
	 */
	private IndividualModel() {
	}

	/**
	 * Constructor
	 *
	 * @param conn
	 * @param id
	 * @throws SQLException
	 */
	public IndividualModel(Connection conn, String id, String schema) throws SQLException {
		if (id.contains("@")) {
			this.setId(id);
		} else {
			this.setId("@I" + id + "@");
		}

		PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_INDIVIDUAL_FROM_ID);
		statement.setString(1, this.id);
		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			name = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();
			sex = rs.getString("SEX");
			phonName = rs.getString("PHONNAME");
			famc = rs.getString("FAMC");
			parents = rs.getString("PARENTS");
		}

		statement = conn.prepareStatement(SELECT_BIRTH_EVENT);
		statement.setString(1, this.id);
		rs = statement.executeQuery();

		if (rs.next())

		{
			birthDate = rs.getDate("DATE");
			birthPlace = rs.getString("PLACE");
		}

		statement = conn.prepareStatement(SELECT_DEATH_EVENT);
		statement.setString(1, this.id);
		rs = statement.executeQuery();

		if (rs.next()) {
			deathDate = rs.getDate("DATE");
			deathPlace = rs.getString("PLACE");
		}

		statement.close();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param birthYear
	 * @param deathYear
	 */
	public IndividualModel(String name, String birthYear, String deathYear) {
		this.name = name;
		this.birthDate = Date.valueOf(birthYear + "-01-01");
		this.deathDate = Date.valueOf(deathYear + "-12-31");
		try {
			this.phonName = new Fonkod().generateKey(name).trim();
		} catch (final Exception e) {
		}

	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @return the deathDate
	 */
	public Date getDeathDate() {
		return deathDate;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @return the famc
	 */
	public String getFamc() {
		return famc;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
	 * @return the phonName
	 */
	public String getPhonName() {
		return phonName;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param birthDate the birthDeat to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @param deathdater the deathdate to set
	 */
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * @param deathPlace the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @param famc the famc to set
	 */
	public void setFamc(String famc) {
		this.famc = famc;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @param phonName the phonName to set
	 */
	public void setPhonName(String phonName) {
		this.phonName = phonName;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		final String dy = deathDate.before(Date.valueOf("9999-12-31")) ? deathDate.toString() : "";

		return id.replace("I", "").replace("@", "") + ";" + name + ";" + birthDate + ";" + dy + ";" + birthPlace + ";"
				+ parents + ";" + phonName;
	}

	@Override
	public String[] toStringArray() {
		return null;
	}

	/**
	 * @param parents2
	 * @return
	 */
	public static String[] splitParents(String parents2) {
		String s = parents2.replaceAll("\\d", "").replace(".", "");
		s = s.replace(", f.", "");
		String[] sa = s.split(",");
		final String[] words = sa[0].split(" ");

		final String[] filter = { "af", "bager", "gamle", "gmd", "i", "inds", "junior", "kirkesanger", "pige", "pigen",
				"portner", "propriet�r", "sadelmager", "skolel�rer", "skovfoged", "slagter", "smed", "smedesvend",
				"snedker", "s�n", "ugift", "ugifte", "unge", "ungkarl", "u�gte", "�r" };
		final StringBuilder sb = new StringBuilder();

		for (final String word : words) {
			for (String filterword : filter) {
				if (word.equalsIgnoreCase(filterword)) {
					continue;
				}
			}
			sb.append(word + " ");
		}

		s = sb.toString();
		sa = s.split(" og ");

		return sa;
	}
}
