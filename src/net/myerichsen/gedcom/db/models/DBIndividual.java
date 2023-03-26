package net.myerichsen.gedcom.db.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.gedcom.db.Fonkod;

/**
 * Class representing the individual data
 *
 * @author Michael Erichsen
 * @version 26. mar. 2023
 *
 */
public class DBIndividual {
	private static final String SELECT_INDIVIDUAL = "SELECT * FROM VEJBY.INDIVIDUAL";
	private static final String SELECT_BIRTH_EVENT = "SELECT * FROM VEJBY.EVENT WHERE INDIVIDUAL = "
			+ "'%s' AND (TYPE = 'Birth' OR TYPE = 'Christening') ORDER BY DATE";
	private static final String SELECT_DEATH_EVENT = "SELECT * FROM VEJBY.EVENT WHERE INDIVIDUAL = "
			+ "'%s' AND (TYPE = 'Death' OR TYPE = 'Burial') ORDER BY DATE";
	private static final String SELECT_PARENTS = "SELECT * FROM VEJBY.FAMILY WHERE ID = '%s'";
	private static final String SELECT_INDIVIDUAL_FROM_ID = "SELECT * FROM VEJBY.INDIVIDUAL WHERE ID = '%s'";
	private static final String SELECT_PARENTS_FROM_CHRISTENING = "SELECT * FROM VEJBY.EVENT WHERE TYPE = 'Christening' AND INDIVIDUAL = '%s'";

	/**
	 * Find parent names from christening event
	 *
	 * @param statement
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private static String findParentsFromChristeningEvent(Statement statement, String id) throws SQLException {
		String query = String.format(SELECT_PARENTS_FROM_CHRISTENING, id);
		ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			return rs.getString("SOURCEDETAIL");
		}

		return "";
	}

	/**
	 * Find name of individual from its id
	 *
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private static String getNameFromId(Statement statement, String id) throws SQLException {
		final String query = String.format(SELECT_INDIVIDUAL_FROM_ID, id);
		final ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			return (rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME")).trim();
		}

		return "";
	}

	/**
	 * Load list of individuals with added data from Derby
	 *
	 * @param statement
	 * @throws SQLException
	 */
	public static List<DBIndividual> loadFromDB(Statement statement) throws SQLException {
		DBIndividual individual;
		final List<DBIndividual> ldbi = new ArrayList<>();
		String givenName = "";
		String surName = "";
		String query;
		String husbandId = "";
		String wifeId = "";
		String husbandName = "";
		String wifeName = "";

		// Read all individuals into a list
		ResultSet rs = statement.executeQuery(SELECT_INDIVIDUAL);

		while (rs.next()) {
			individual = new DBIndividual();

			if (rs.getString("ID") != null) {
				individual.setId(rs.getString("ID"));
			}
			if (rs.getString("GIVENNAME") != null) {
				givenName = rs.getString("GIVENNAME");
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
		for (final DBIndividual dbi : ldbi) {
			query = String.format(SELECT_BIRTH_EVENT, dbi.getId());
			rs = statement.executeQuery(query);

			if (rs.next()) {
				if (rs.getDate("DATE") != null) {
					dbi.setBirthYear(rs.getDate("DATE").toLocalDate().getYear());
				}
				if (rs.getString("PLACE") != null) {
					dbi.setBirthPlace(rs.getString("PLACE"));
				}
			}
		}

		// Add death data
		for (final DBIndividual dbi : ldbi) {
			query = String.format(SELECT_DEATH_EVENT, dbi.getId());
			rs = statement.executeQuery(query);

			if (rs.next()) {
				if (rs.getDate("DATE") != null) {
					dbi.setDeathYear(rs.getDate("DATE").toLocalDate().getYear());
				}
				if (rs.getString("PLACE") != null) {
					dbi.setDeathPlace(rs.getString("PLACE"));
				}
			}
		}

		// Add parents
		for (final DBIndividual dbi : ldbi) {
			query = String.format(SELECT_PARENTS, dbi.getFamc());

			rs = statement.executeQuery(query);

			if (rs.next()) {
				// Find names from INDIVIDUAL TABLE
				husbandId = rs.getString("HUSBAND");
				wifeId = rs.getString("WIFE");

				if (husbandId != null) {
					husbandName = getNameFromId(statement, husbandId);
				}

				if (wifeId != null) {
					wifeName = getNameFromId(statement, wifeId);
				}

				dbi.setParents((husbandName + " og " + wifeName));
			} else {
				// Find names from christening event source detail
				dbi.setParents(findParentsFromChristeningEvent(statement, dbi.getId()));
			}
		}
		return ldbi;
	}

	private String id = "";
	private String name = "";
	private String sex = "";
	private String famc = "";
	private String phonName = "";
	private int birthYear = 0;
	private String birthPlace = "";
	private int deathYear = 9999;
	private String deathPlace = "";
	private String parents = "";

	/**
	 * No-arg constructor
	 *
	 */
	private DBIndividual() {
	}

	/**
	 * Constructor from ID
	 *
	 * @param statement
	 * @param id
	 * @throws SQLException
	 */
	public DBIndividual(Statement statement, String id) throws SQLException {
		if (id.contains("@")) {
			this.setId(id);
		} else {
			this.setId("@I" + id + "@");
		}

		String query = "SELECT * from VEJBY.INDIVIDUAL WHERE ID ='" + this.id + "'";

		ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			name = rs.getString("GIVENNAME").trim() + " " + rs.getString("SURNAME").trim();
			phonName = rs.getString("PHONNAME");
		}

		query = "SELECT * FROM VEJBY.EVENT WHERE INDIVIDUAL = '" + this.id
				+ "' AND ( TYPE = 'Birth' OR TYPE = 'Christening') ORDER BY DATE";

		rs = statement.executeQuery(query);

		if (rs.next()) {
			birthYear = rs.getDate("DATE").toLocalDate().getYear();
			birthPlace = rs.getString("PLACE");
		}

		query = "SELECT ID, TYPE, DATE, INDIVIDUAL FROM VEJBY.EVENT WHERE INDIVIDUAL = '" + this.id
				+ "' AND ( TYPE = 'Death' OR TYPE = 'Burial') ORDER BY DATE";

		rs = statement.executeQuery(query);

		if (rs.next()) {
			deathYear = rs.getDate("DATE").toLocalDate().getYear();
			deathPlace = rs.getString("PLACE");
		}
	}

	/**
	 * Constructor from name
	 *
	 * @param string
	 * @param birthYear2
	 * @param deathYear2
	 */
	public DBIndividual(String name, String birthYear, String deathYear) {
		this.name = name;
		this.birthYear = Integer.parseInt(birthYear);
		this.deathYear = Integer.parseInt(deathYear);
		try {
			this.phonName = new Fonkod().generateKey(name).trim();
		} catch (final Exception e) {
		}

	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @return the birthYear
	 */
	public int getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @return the deathYear
	 */
	public int getDeathYear() {
		return deathYear;
	}

	/**
	 * @return the famc
	 */
	public String getFamc() {
		return famc;
	}

//	public String getHeader() {
//		return "Id;Navn;Fødeår;Dødsår;Fødested;Forældre;Fonetisk navn";
//	}

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
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @param birthYear the birthYear to set
	 */
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	/**
	 * @param deathPlace the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @param deathYear the deathYear to set
	 */
	public void setDeathYear(int deathYear) {
		this.deathYear = deathYear;
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
		String dy = "";
		if (deathYear < 9999) {
			dy = Integer.toString(deathYear);
		}

		return id.replace("I", "").replace("@", "") + ";" + name + ";" + birthYear + ";" + dy + ";" + birthPlace + ";"
				+ parents + ";" + phonName;
	}
}
