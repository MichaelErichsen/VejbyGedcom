package net.myerichsen.vejby.gedcom;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.util.Mapping;

/**
 * Class representing an individual in GEDCOM.
 * 
 * @author Michael Erichsen
 * @version 20. aug. 2020
 *
 */
public class Individual {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private int id;
	private String name;
	private String sex;
	private String trades;
	private String position;
	private String address;
	private String birthDate;
	private Date deathDate;
	private String birthPlace;
	private String deathPlace;
	private int year;
	private String place;
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
	 * @return the trades
	 */
	public String getTrades() {
		return trades;
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
	 * @param deathDate the deathDate to set
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
	 * @param trades the trades to set
	 */
	public void setTrades(String trades) {
		this.trades = trades;
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
		Mapping mapping = new Mapping();
		final StringBuffer sb = new StringBuffer();

		sb.append("0 @I" + getId() + "@ INDI\n");

		sb.append("1 NAME ");

		String[] nameParts = getName().split(" ");

		// Handle stubs like dat, dr, dtr, and datter
		String familyName = nameParts[nameParts.length - 1];
		LOGGER.log(Level.FINE, "Family name: " + familyName);

		if (mapping.getNameStubs().contains(familyName)) {
			LOGGER.log(Level.FINE, "Invalid family name: " + familyName);
			familyName = new String(nameParts[nameParts.length - 2] + familyName);

			for (int i = 0; i < (nameParts.length - 2); i++) {
				sb.append(nameParts[i] + " ");
			}
		} else {
			for (int i = 0; i < (nameParts.length - 1); i++) {
				sb.append(nameParts[i] + " ");
			}
		}

		// Surround with slashes
		sb.append("/" + familyName + "/\n");

		if (getSex().equals(Sex.M)) {
			sb.append("1 SEX M\n");
		} else if (getSex().equals(Sex.F)) {
			sb.append("1 SEX F\n");
		}

		sb.append("1 BIRT\n");
		sb.append("2 DATE " + getBirthDate() + "\n");
		if (getBirthPlace() != null) {
			sb.append("2 PLAC " + getBirthPlace() + "\n");
		}

		if (getDeathDate() != null) {
			sb.append("1 DEAT\n");
			sb.append("2 DATE " + getDeathDate() + "\n");

		}

		if (getTrades() != null) {
			sb.append("1 OCCU " + getTrades() + "\n");
		}

		sb.append(censusEvent.toGedcom());

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
