package net.myerichsen.gedcom.db.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.gedcom.db.Fonkod;

/**
 * Class representing some individual data
 *
 * @author Michael Erichsen
 * @version 25. mar. 2023
 *
 */
public class DBIndividual {
	private String id = "";
	private String name = "";
	private int birthYear = 0;
	private int deathYear = 9999;
	private String birthPlace = "";
	private String phonName = "";
	private String parents = "";

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
			birthYear = getYearFromDate(rs.getString("DATE"));
			birthPlace = rs.getString("PLACE");
		}

		query = "SELECT ID, TYPE, DATE, INDIVIDUAL FROM VEJBY.EVENT WHERE INDIVIDUAL = '" + this.id
				+ "' AND ( TYPE = 'Death' OR TYPE = 'Burial') ORDER BY DATE";

		rs = statement.executeQuery(query);

		if (rs.next()) {
			deathYear = getYearFromDate(rs.getString("DATE"));
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
	 * @return the deathYear
	 */
	public int getDeathYear() {
		return deathYear;
	}

	public String getHeader() {
		return "Id;Navn;Fødeår;Dødsår;Fødested;Forældre;Fonetisk navn";
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
	 * Get year in integer format
	 *
	 * @param date
	 * @return
	 */
	private int getYearFromDate(String date) {
		final Pattern r = Pattern.compile("\\d{4}");

		final Matcher m = r.matcher(date);

		if (m.find()) {
			return Integer.parseInt(m.group(0));
		}
		return 1;
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
	 * @param deathYear the deathYear to set
	 */
	public void setDeathYear(int deathYear) {
		this.deathYear = deathYear;
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
