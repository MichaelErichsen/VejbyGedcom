package net.myerichsen.gedcom.matcher;

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
 * @version 30. nov. 2022
 *
 */
public class MatchPerson {
	private static Fonkod fonkod = new Fonkod();
	private String ID = "";
	private String fullName = "";
	private String phoneticName = "";
	private String birthDate = "";
	private String birthYear = "";
	private String sex = "?";
	private String birtChrFlag = "?";

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
		if (birthYear.equals(o.getBirthYear()) && phoneticName.equals(o.getPhoneticName()) && sex.equals(o.getSex())) {
			return true;
		}

		return false;
	}

//	/**
//	 * Two objects are equal, if they share birth year, phonetic name and sex
//	 */
//	public boolean equals(MatchPerson o) {
//		if (birthYear.equals(o.getBirthYear()) && phoneticName.equals(o.getPhoneticName()) && sex.equals(o.getSex())) {
//			return true;
//		}
//
//		return false;
//	}

	/**
	 * Convert PersonalName object to first character and fonkoded surname
	 * 
	 * @param name
	 * @return Fonkoded name
	 */
	private String fonkodName(PersonalName name) {
		String[] nameParts = name.getBasic().split("/");
		String givenName = nameParts[0].toLowerCase();

		if (givenName.length() > 1) {
			givenName = givenName.substring(0, 2);
		}

		String surName;

		try {
			surName = fonkod.generateKey(nameParts[1]);
		} catch (Exception e) {
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
		String patternd = "\\d{2}\\s[A-Z]{3}\\s\\d{4}";
		String patternm = "[A-Z]{3}\\s\\d{4}";
		String patterny = "\\d{4}";

		if (date.matches(patternd)) {
			return date.substring(6);
		}

		if (date.matches(patternm)) {
			return date.substring(4).replace(" ", "-");
		}

		if (date.matches(patterny)) {
			return date.replace(" ", "-");
		}

		return date;
	}

	/**
	 * Populate the object
	 * 
	 * @param individual
	 */
	private void populate(Entry<String, Individual> individual) {
		this.individual = individual;
		ID = individual.getKey();
		Individual value = individual.getValue();
		fullName = value.toString();
		List<PersonalName> name = value.getNames();
		phoneticName = fonkodName(name.get(0));
		sex = value.getSex().getValue();
		List<IndividualEvent> event = value.getEventsOfType(IndividualEventType.BIRTH);

		if ((event != null) && (event.size() > 0)) {
			birthDate = event.get(0).getDate().getValue();
			birthYear = getYear(birthDate);
			birtChrFlag = "B";
		} else {
			event = value.getEventsOfType(IndividualEventType.CHRISTENING);

			if ((event != null) && (event.size() > 0)) {
				birthDate = event.get(0).getDate().getValue();
				birthYear = getYear(birthDate);
				birtChrFlag = "C";
			}
		}
	}

	/**
	 * @param individual the individual to set
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
