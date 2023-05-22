package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.archivesearcher.util.Fonkod;

/**
 * Class representing siblings from the parents table
 *
 * @author Michael Erichsen
 * @version 18. maj 2023
 *
 */

public class SiblingsModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM PARENTS WHERE FATHERPHONETIC = ? AND MOTHERPHONETIC = ?";

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param parents
	 * @return
	 * @throws SQLException
	 */
	private static SiblingsModel[] load(String schema, String dbPath, String parents) throws SQLException {
		if (parents.length() == 0) {
			return new SiblingsModel[0];
		}

		final String[] p = IndividualModel.splitParents(parents);

		if (p.length > 1) {
			return load(schema, dbPath, p[0], p[1]);
		}

		return load(schema, dbPath, p[0], "");
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
	private static SiblingsModel[] load(String schema, String dbPath, String fathersName, String mothersName)
			throws SQLException {
		String fatherPhonetic;
		String motherPhonetic;
		SiblingsModel pr;
		final List<SiblingsModel> lpr = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);

		final Fonkod fk = new Fonkod();

		try {
			fatherPhonetic = fk.generateKey(fathersName);
			motherPhonetic = fk.generateKey(mothersName);
		} catch (final Exception e) {
			System.out.println("Invalid parents: " + fathersName + "," + mothersName);
			return new SiblingsModel[0];
		}

		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();

		statement = conn.prepareStatement(SELECT);
		statement.setString(1, fatherPhonetic);
		statement.setString(2, motherPhonetic);
		final ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			pr = new SiblingsModel();
			pr.setIndividualKey(rs.getString("INDIVIDUALKEY"));
			pr.setName(rs.getString("NAME"));
			pr.setLocation(rs.getString("PLACE"));
			pr.setParents(rs.getString("PARENTS"));
			pr.setBirthDate(rs.getInt("BIRTHYEAR"));
			lpr.add(pr);
		}

		final SiblingsModel[] sra = new SiblingsModel[lpr.size()];

		for (int i = 0; i < lpr.size(); i++) {
			sra[i] = lpr.get(i);
		}

		return sra;
	}

	/**
	 * Generic method to get a list of objects from the database
	 *
	 * @param sa
	 * @return
	 * @throws SQLException
	 */
	public static SiblingsModel[] load(String[] args) throws SQLException {
		switch (args.length) {
		case 3: {
			return load(args[0], args[1], args[2]);
		}
		case 4: {
			return load(args[0], args[1], args[2], args[3]);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + args.length + ": '" + args[0] + "'");
		}
	}

	/**
	 * Split into two names and phonetisize
	 *
	 * @param parents2
	 * @return
	 */

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
		return (individualKey != null ? individualKey.trim() + ", " : "") + birthYear + ", "
				+ (name != null ? name.trim() + ", " : "") + (parents != null ? parents.trim() + ", " : "")
				+ (fatherPhonetic != null ? fatherPhonetic.trim() + ", " : "")
				+ (motherPhonetic != null ? motherPhonetic.trim() + ", " : "") + (place != null ? place : "");
	}

}
