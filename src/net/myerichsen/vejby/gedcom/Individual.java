package net.myerichsen.vejby.gedcom;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.util.Mapping;

/**
 * Class representing an individual in GEDCOM.
 * 
 * @version 06-09-2020
 * @author Michael Erichsen
 */
public class Individual {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private int id;
	private String name = "";
	private String sex = "";
	private String trade = "";
	private String position = "";
	private String address = "";
	private String birthDate = "";
	private String birthPlace = "";
	private String christeningDate = "";
	private String christeningPlace = "";
	private String deathDate = "";
	private String deathPlace = "";
	private String place = "";
	private String maritalStatus = "";
	private String familyRole1 = "";
	private String familyRole2 = "";
	private String familyRole3 = "";
	private String familyRole4 = "";
	private int year;
	private Household household;
	private CensusEvent censusEvent;

	/**
	 * Constructor
	 *
	 * @param id
	 */
	public Individual(int id) {
		super();
		this.id = id;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @return the censusEvent
	 */
	public CensusEvent getCensusEvent() {
		return censusEvent;
	}

	/**
	 * @return the christeningDate
	 */
	public String getChristeningDate() {
		return christeningDate;
	}

	/**
	 * @return the christeningPlace
	 */
	public String getChristeningPlace() {
		return christeningPlace;
	}

	/**
	 * @return the deathDate
	 */
	public String getDeathDate() {
		return deathDate;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @return the familyRole1
	 */
	public String getFamilyRole1() {
		return familyRole1;
	}

	/**
	 * @return the familyRole2
	 */
	public String getFamilyRole2() {
		return familyRole2;
	}

	/**
	 * @return the familyRole3
	 */
	public String getFamilyRole3() {
		return familyRole3;
	}

	/**
	 * @return the familyRole4
	 */
	public String getFamilyRole4() {
		return familyRole4;
	}

	/**
	 * @return the household
	 */
	public Household getHousehold() {
		return household;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the maritalStatus
	 */
	public String getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @return the trade
	 */
	public String getTrade() {
		return trade;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;

	}

	/**
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @param censusEvent the censusEvent to set
	 */
	public void setCensusEvent(CensusEvent censusEvent) {
		this.censusEvent = censusEvent;
	}

	/**
	 * @param christeningDate the christeningDate to set
	 */
	public void setChristeningDate(String christeningDate) {
		this.christeningDate = christeningDate;
	}

	/**
	 * @param christeningPlace the christeningPlace to set
	 */
	public void setChristeningPlace(String christeningPlace) {
		this.christeningPlace = christeningPlace;
	}

	/**
	 * @param deathDate the deathDate to set
	 */
	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * @param deathPlace the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @param familyRole1 the familyRole1 to set
	 */
	public void setFamilyRole1(String familyRole1) {
		this.familyRole1 = familyRole1;
	}

	/**
	 * @param familyRole2 the familyRole2 to set
	 */
	public void setFamilyRole2(String familyRole2) {
		this.familyRole2 = familyRole2;
	}

	/**
	 * @param familyRole3 the familyRole3 to set
	 */
	public void setFamilyRole3(String familyRole3) {
		this.familyRole3 = familyRole3;
	}

	/**
	 * @param familyRole4 the familyRole4 to set
	 */
	public void setFamilyRole4(String familyRole4) {
		this.familyRole4 = familyRole4;
	}

	/**
	 * @param household the household to set
	 */
	public void setHousehold(Household household) {
		this.household = household;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param maritalStatus the maritalStatus to set
	 */
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @param trade the trade to set
	 */
	public void setTrade(String trade) {
		this.trade = trade;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Return a GEDCOM output.
	 * 
	 * @return
	 */
	public String toGedcom() {
		Mapping mapping = Mapping.getInstance();
		final StringBuffer sb = new StringBuffer();

		sb.append("0 @I" + getId() + "@ INDI\n");
		sb.append("1 NAME ");

		String[] nameParts = getName().split(" ");

		// Handle stubs like dat, dr, dtr, and datter
		String familyName = nameParts[nameParts.length - 1];
		LOGGER.log(Level.FINE, "Family name: " + familyName);

		if (familyName.endsWith("d.")) {
			familyName = familyName.replace("d.", "datter");
		} else if (familyName.endsWith("dat.")) {
			familyName = familyName.replace("dat.", "datter");
		} else if (familyName.endsWith("dat")) {
			familyName = familyName.replace("dat", "datter");
		} else if (familyName.endsWith("datt.")) {
			familyName = familyName.replace("datt.", "datter");
		} else if (familyName.endsWith("datt")) {
			familyName = familyName.replace("datt", "datter");
		} else if (familyName.endsWith("datr.")) {
			familyName = familyName.replace("datr.", "datter");
		} else if (familyName.endsWith("datr")) {
			familyName = familyName.replace("datr", "datter");
		} else if (familyName.endsWith("dtr.")) {
			familyName = familyName.replace("dtr.", "datter");
		} else if (familyName.endsWith("dtr")) {
			familyName = familyName.replace("dtr", "datter");
		}

		if (mapping.getDaughterNameStubs().contains(familyName.toLowerCase())) {
			LOGGER.log(Level.INFO, "Invalid family name: " + familyName);
			familyName = new String(nameParts[nameParts.length - 2] + "datter");

			for (int i = 0; i < (nameParts.length - 2); i++) {
				sb.append(nameParts[i] + " ");
			}
		} else {
			for (int i = 0; i < (nameParts.length - 1); i++) {
				sb.append(nameParts[i] + " ");
			}
		}

		// Surround with slashes to mark as family name
		sb.append("/" + familyName + "/\n");

		// Default to M rather than having to fix all entries with "?"
		if ((getSex().equals(Sex.F) || getSex().equals("F"))) {
			sb.append("1 SEX F\n");
		} else {
			sb.append("1 SEX M\n");
		}

		if ((getBirthDate() != null) && (!getBirthDate().equals(""))) {
			sb.append("1 BIRT\n");
			sb.append("2 DATE " + getBirthDate() + "\n");
			if ((getBirthPlace() != null) && (!getBirthPlace().equals(""))) {
				sb.append("2 PLAC " + getBirthPlace() + ",\n");
			}
		}

		if ((getChristeningDate() != null) && (!getChristeningDate().equals(""))) {
			sb.append("1 CHR\n");
			sb.append("2 DATE " + getChristeningDate() + "\n");
			if ((getChristeningPlace() != null) && (!getChristeningPlace().equals(""))) {
				sb.append("2 PLAC " + getChristeningPlace() + ",\n");
			}
			sb.append("2 SOUR @S1@\n");
		}

		if ((getDeathDate() != null) && (!getDeathDate().equals(""))) {
			sb.append("1 DEAT\n");
			sb.append("2 DATE " + getDeathDate() + "\n");

		}

		if ((getTrade() != null) && (!getTrade().equals(""))) {
			sb.append("1 OCCU " + getTrade() + "\n");
		}

		if (censusEvent != null) {
			sb.append(censusEvent.toGedcom());
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
