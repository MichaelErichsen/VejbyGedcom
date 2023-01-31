package net.myerichsen.gedcom.birthdatematcher;

import java.util.List;
import java.util.Map.Entry;

import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.model.enumerations.IndividualEventType;

/**
 * A class to represent a person to be matched
 *
 * @author Michael Erichsen
 * @version 01. dec. 2022
 *
 */
public class MatchPerson {
	private static Fonkod fonkod = new Fonkod();
	private final String patternd = "\\d{2}\\s[A-Z]{3}\\s\\d{4}";
	private final String patternm = "[A-Z]{3}\\s\\d{4}";

	private final String patterny = "\\d{4}";
	private String ID = "";
	private String fullName = "";
	private String phoneticName = "";
	private String birthDate = "";
	private String birthYear = "";
	private String sex = "?";
	private String birtChrFlag = "?";
	private String fullDateFlag = "N";

	private Entry<String, Individual> individual;

	/**
	 * Constructor
	 *
	 * @param individual
	 */
	public MatchPerson(Entry<String, Individual> individual) {
		super();
		populate(individual);
	}

	/**
	 * Compare birth year, phonetic name, and sex for two persons
	 *
	 * @param o
	 * @return True if equal
	 */
	public boolean equals(MatchPerson o) {
		if (birthDate.contains("BEF ")) {
			return false;
		}

		if (o.getBirthDate().contains("BEF ")) {
			return false;
		}

		if (fullDateFlag.equals("F") && o.getFullDateFlag().equals("F") && !birthDate.equals(o.getFullDateFlag())
				&& birtChrFlag.equals(o.getBirtChrFlag())) {
			return false;
		}

		if (birthYear.equals(o.getBirthYear()) && phoneticName.equals(o.getPhoneticName()) && sex.equals(o.getSex())) {
			return true;
		}

		return false;
	}

	/**
	 * Convert PersonalName object to first character and fonkoded surname
	 *
	 * @param name
	 * @return Fonkoded name
	 */
	private String fonkodName(PersonalName name) {
		final String[] nameParts = name.getBasic().split("/");
		String givenName = nameParts[0].toLowerCase();

		if (givenName.length() > 1) {
			givenName = givenName.substring(0, 2);
		}

		String surName;

		try {
			surName = fonkod.generateKey(nameParts[1]);
		} catch (final Exception e) {
			surName = "";
		}

		return givenName + " " + surName;
	}

	/**
	 * @return the birtChrFlag
	 */
	public String getBirtChrFlag() {
		return birtChrFlag;
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the birthYear
	 */
	public String getBirthYear() {
		return birthYear;
	}

	/**
	 * @return the fullDateFlag
	 */
	public String getDateNameFlag() {
		return fullDateFlag;
	}

	/**
	 * @return the fullDateFlag
	 */
	public String getFullDateFlag() {
		return fullDateFlag;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the individual
	 */
	public Entry<String, Individual> getIndividual() {
		return individual;
	}

	/**
	 * @return the phoneticName
	 */
	public String getPhoneticName() {
		return phoneticName;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * Return the year from the date string
	 *
	 * @param date
	 * @return year
	 */
	private String getYear(String date) {

		if (date.matches(patternd)) {
			return date.substring(6).trim();
		}

		if (date.matches(patternm)) {
			return date.substring(4).replace(" ", "-").trim();
		}

		if (date.matches(patterny)) {
			return date.replace(" ", "-").trim();
		}

		return date.trim();
	}

	/**
	 * Populate the object
	 *
	 * @param individual
	 */
	private void populate(Entry<String, Individual> individual) {
		this.individual = individual;
		ID = individual.getKey();
		final Individual value = individual.getValue();
		fullName = value.toString();
		final List<PersonalName> name = value.getNames();
		phoneticName = fonkodName(name.get(0));
		sex = value.getSex().getValue();
		List<IndividualEvent> event = value.getEventsOfType(IndividualEventType.BIRTH);

		if (event != null && event.size() > 0) {
			birthDate = event.get(0).getDate().getValue();
			birthYear = getYear(birthDate);
			birtChrFlag = "B";
		} else {
			event = value.getEventsOfType(IndividualEventType.CHRISTENING);

			if (event != null && event.size() > 0) {
				birthDate = event.get(0).getDate().getValue();
				birthYear = getYear(birthDate);
				birtChrFlag = "C";
			}
		}

		if (birthDate.matches(patternd)) {
			fullDateFlag = "F";
		}
	}

	/**
	 * @param individual
	 *            the individual to set
	 */
	public void setIndividual(Entry<String, Individual> individual) {
		this.individual = individual;
	}

	/**
	 * String representation of the object
	 */
	@Override
	public String toString() {
		return ID + ";" + birtChrFlag + ";" + birthYear + ";" + phoneticName + ";" + birthDate + ";" + fullName;
	}
}
