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
 * Class representing a burial registry record
 *
 * @author Michael Erichsen
 * @version 18, maj 2023
 *
 */
public class BurregModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT = "SELECT * FROM BURIAL_PERSON_COMPLETE "
			+ "WHERE CPH.BURIAL_PERSON_COMPLETE.PHONNAME = ?";

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @return
	 * @throws SQLException
	 */
	public static BurregModel[] load(String schema, String dbPath, String phonName, String birthDate, String deathDate)
			throws SQLException {
		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT);
		statement.setString(1, phonName);
		final ResultSet rs = statement.executeQuery();

		BurregModel br;
		final List<BurregModel> lbr = new ArrayList<>();

		String name;
		final Fonkod fk = new Fonkod();

		while (rs.next()) {
			name = rs.getString("FIRSTNAMES").trim() + " " + rs.getString("LASTNAME").trim();

			try {
				if (!fk.generateKey(name).equals(phonName)) {
					continue;
				}
			} catch (final Exception e) {
				System.out.println(e.getMessage() + ": " + name + ", " + phonName);
			}

			br = new BurregModel();

			br.setFirstNames(rs.getString("FIRSTNAMES"));
			br.setLastName(rs.getString("LASTNAME"));
			br.setDateOfDeath(rs.getString("DATEOFDEATH"));
			br.setYearOfBirth(rs.getString("YEAROFBIRTH"));
			br.setDeathPlace(rs.getString("DEATHPLACE"));
			br.setCivilStatus(rs.getString("CIVILSTATUS"));
			br.setAdressOutsideCph(rs.getString("ADRESSOUTSIDECPH"));
			br.setSex(rs.getString("SEX"));
			br.setComment(rs.getString("COMMENT"));
			br.setCemetary(rs.getString("CEMETARY"));
			br.setChapel(rs.getString("CHAPEL"));
			br.setParish(rs.getString("PARISH"));
			br.setStreet(rs.getString("STREET"));
			br.setHood(rs.getString("HOOD"));
			br.setStreetNumber(rs.getString("STREET_NUMBER"));
			br.setLetter(rs.getString("LETTER"));
			br.setFloor(rs.getString("FLOOR"));
			br.setInstitution(rs.getString("INSTITUTION"));
			br.setInstitutionStreet(rs.getString("INSTITUTION_STREET"));
			br.setInstitutionHood(rs.getString("INSTITUTION_HOOD"));
			br.setInstitutionStreetNumber(rs.getString("INSTITUTION_STREET_NUMBER"));
			br.setOccuptations(rs.getString("OCCUPATIONS"));
			br.setOccupationRelationTypes(rs.getString("OCCUPATION_RELATION_TYPES"));
			br.setDeathCauses(rs.getString("DEATHCAUSES"));
			br.setDeathCausesDanish(rs.getString("DEATHCAUSES_DANISH"));

			lbr.add(br);
		}

		statement.close();

		final BurregModel[] bra = new BurregModel[lbr.size()];

		for (int i = 0; i < lbr.size(); i++) {
			bra[i] = lbr.get(i);
		}

		return bra;
	}

	private String firstNames = "";
	private String lastName = "";
	private String dateOfDeath = "";
	private String yearOfBirth = "";
	private String deathPlace = "";
	private String civilStatus = "";
	private String adressOutsideCph = "";
	private String sex = "";
	private String comment = "";
	private String cemetary = "";
	private String chapel = "";
	private String parish = "";
	private String street = "";
	private String hood = "";
	private String streetNumber = "";
	private String letter = "";
	private String floor = "";
	private String institution = "";
	private String institutionStreet = "";
	private String institutionHood = "";
	private String institutionStreetNumber = "";
	private String occuptations = "";
	private String occupationRelationTypes = "";
	private String deathCauses = "";

	private String deathCausesDanish = "";

	/**
	 * @return the adressOutsideCph
	 */
	public String getAdressOutsideCph() {
		return adressOutsideCph;
	}

	/**
	 * @return the cemetary
	 */
	public String getCemetary() {
		return cemetary;
	}

	/**
	 * @return the chapel
	 */
	public String getChapel() {
		return chapel;
	}

	/**
	 * @return the civilStatus
	 */
	public String getCivilStatus() {
		return civilStatus;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the dateOfDeath
	 */
	public String getDateOfDeath() {
		return dateOfDeath;
	}

	/**
	 * @return the deathCauses
	 */
	public String getDeathCauses() {
		return deathCauses;
	}

	/**
	 * @return the deathCausesDanish
	 */
	public String getDeathCausesDanish() {
		return deathCausesDanish;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @return the firstNames
	 */
	public String getFirstNames() {
		return firstNames;
	}

	/**
	 * @return the floor
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * @return the hood
	 */
	public String getHood() {
		return hood;
	}

	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @return the institutionHood
	 */
	public String getInstitutionHood() {
		return institutionHood;
	}

	/**
	 * @return the institutionStreet
	 */
	public String getInstitutionStreet() {
		return institutionStreet;
	}

	/**
	 * @return the institutionStreetNumber
	 */
	public String getInstitutionStreetNumber() {
		return institutionStreetNumber;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the letter
	 */
	public String getLetter() {
		return letter;
	}

	/**
	 * @return the occupationRelationTypes
	 */
	public String getOccupationRelationTypes() {
		return occupationRelationTypes;
	}

	/**
	 * @return the occuptations
	 */
	public String getOccuptations() {
		return occuptations;
	}

	/**
	 * @return the parish
	 */
	public String getParish() {
		return parish;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @return the streetNumber
	 */
	public String getStreetNumber() {
		return streetNumber;
	}

	/**
	 * @return the yearOfBirth
	 */
	public String getYearOfBirth() {
		return yearOfBirth;
	}

	/**
	 * @param adressOutsideCph the adressOutsideCph to set
	 */
	public void setAdressOutsideCph(String adressOutsideCph) {
		this.adressOutsideCph = adressOutsideCph;
	}

	/**
	 * @param cemetary the cemetary to set
	 */
	public void setCemetary(String cemetary) {
		this.cemetary = cemetary;
	}

	/**
	 * @param chapel the chapel to set
	 */
	public void setChapel(String chapel) {
		this.chapel = chapel;
	}

	/**
	 * @param civilStatus the civilStatus to set
	 */
	public void setCivilStatus(String civilStatus) {
		this.civilStatus = civilStatus;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param dateOfDeath the dateOfDeath to set
	 */
	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	/**
	 * @param deathCauses the deathCauses to set
	 */
	public void setDeathCauses(String deathCauses) {
		this.deathCauses = deathCauses;
	}

	/**
	 * @param deathCausesDanish the deathCausesDanish to set
	 */
	public void setDeathCausesDanish(String deathCausesDanish) {
		this.deathCausesDanish = deathCausesDanish;
	}

	/**
	 * @param deathPlace the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @param firstNames the firstNames to set
	 */
	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * @param hood the hood to set
	 */
	public void setHood(String hood) {
		this.hood = hood;
	}

	/**
	 * @param institution the institution to set
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * @param institutionHood the institutionHood to set
	 */
	public void setInstitutionHood(String institutionHood) {
		this.institutionHood = institutionHood;
	}

	/**
	 * @param institutionStreet the institutionStreet to set
	 */
	public void setInstitutionStreet(String institutionStreet) {
		this.institutionStreet = institutionStreet;
	}

	/**
	 * @param institutionStreetNumber the institutionStreetNumber to set
	 */
	public void setInstitutionStreetNumber(String institutionStreetNumber) {
		this.institutionStreetNumber = institutionStreetNumber;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @param letter the letter to set
	 */
	public void setLetter(String letter) {
		this.letter = letter;
	}

	/**
	 * @param occupationRelationTypes the occupationRelationTypes to set
	 */
	public void setOccupationRelationTypes(String occupationRelationTypes) {
		this.occupationRelationTypes = occupationRelationTypes;
	}

	/**
	 * @param occuptations the occuptations to set
	 */
	public void setOccuptations(String occuptations) {
		this.occuptations = occuptations;
	}

	/**
	 * @param parish the parish to set
	 */
	public void setParish(String parish) {
		this.parish = parish;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @param streetNumber the streetNumber to set
	 */
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	/**
	 * @param yearOfBirth the yearOfBirth to set
	 */
	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	@Override
	public String toString() {
		return (firstNames != null ? firstNames.trim() + ", " : "") + (lastName != null ? lastName.trim() + ", " : "")
				+ (dateOfDeath != null ? dateOfDeath.trim() + ", " : "")
				+ (yearOfBirth != null ? yearOfBirth.trim() + ", " : "")
				+ (deathPlace != null ? deathPlace.trim() + ", " : "")
				+ (civilStatus != null ? civilStatus.trim() + ", " : "")
				+ (adressOutsideCph != null ? adressOutsideCph.trim() + ", " : "")
				+ (sex != null ? sex.trim() + ", " : "") + (comment != null ? comment.trim() + ", " : "")
				+ (cemetary != null ? cemetary.trim() + ", " : "") + (chapel != null ? chapel.trim() + ", " : "")
				+ (parish != null ? parish.trim() + ", " : "") + (street != null ? street.trim() + ", " : "")
				+ (hood != null ? "hood=" + hood.trim() + ", " : "")
				+ (streetNumber != null ? streetNumber.trim() + ", " : "")
				+ (letter != null ? letter.trim() + ", " : "") + (floor != null ? floor.trim() + ", " : "")
				+ (institution != null ? institution.trim() + ", " : "")
				+ (institutionStreet != null ? institutionStreet.trim() + ", " : "")
				+ (institutionHood != null ? institutionHood.trim() + ", " : "")
				+ (institutionStreetNumber != null ? institutionStreetNumber.trim() + ", " : "")
				+ (occuptations != null ? occuptations.trim() + ", " : "")
				+ (occupationRelationTypes != null ? occupationRelationTypes.trim() + ", " : "")
				+ (deathCauses != null ? deathCauses.trim() + ", " : "")
				+ (deathCausesDanish != null ? deathCausesDanish : "");
	}

}
