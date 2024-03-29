package net.myerichsen.archivesearcher.probateFinder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;

import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.enumerations.IndividualEventType;

/**
 * A class to represent a person to be sorted
 *
 * @author Michael Erichsen
 * @version 9. jan. 2023
 *
 */
public class ProbatePerson {
	private final String key;
	private final String name;
	private final String birthDate;
	private String lastPlace;
	private String isoBirthDate;

	/**
	 * Constructor
	 *
	 * @param individual
	 */
	public ProbatePerson(Entry<String, Individual> individual) {
		key = individual.getKey().replace("@I", "").replace("@", "");
		final Individual value = individual.getValue();
		name = value.getNames().get(0).getBasic();
		List<IndividualEvent> births = value.getEventsOfType(IndividualEventType.BIRTH);

		if (births.isEmpty() || births.size() == 0) {
			births = value.getEventsOfType(IndividualEventType.CHRISTENING);
		}

		birthDate = births.get(0).getDate().getValue();

		if (birthDate.length() == 4) {
			isoBirthDate = birthDate + "-01-01";
		} else if (birthDate.length() == 8) {
			isoBirthDate = birthDate.substring(4) + "-" + convertMonth(birthDate.substring(4)) + "-01";
		} else if (birthDate.length() == 11) {
			isoBirthDate = birthDate.substring(7, 11) + "-" + convertMonth(birthDate.substring(3, 6)) + "-"
					+ birthDate.substring(0, 2);
		}

		List<IndividualEvent> evList = value.getEventsOfType(IndividualEventType.DEATH);

		if (evList.isEmpty() || evList.size() == 0) {
			evList = value.getEventsOfType(IndividualEventType.BURIAL);
		}

		LocalDate lastDate = LocalDate.of(1, 1, 1);
		lastPlace = "";
		LocalDate eventDate;
		Place place;

		if (evList.isEmpty() || evList.size() == 0) {
			evList = value.getEvents();
		}

		for (final IndividualEvent individualEvent : evList) {
			eventDate = ProbateUtil.parseProbateDate(individualEvent);

			if (eventDate != null && eventDate.isAfter(lastDate) && individualEvent.getPlace() != null) {
				lastDate = eventDate;

				place = individualEvent.getPlace();

				if (place != null) {
					lastPlace = individualEvent.getPlace().getPlaceName();
				}

			}

		}

	}

	/**
	 * Convert character month to decimal month
	 *
	 * @param month as 3 character string
	 * @return month as two digit string
	 */
	private String convertMonth(String month) {
		if ("JAN".equals(month)) {
			return "01";
		}
		if ("FEB".equals(month)) {
			return "02";
		}
		if ("MAR".equals(month)) {
			return "03";
		}
		if ("APR".equals(month)) {
			return "04";
		}
		if ("MAY".equals(month)) {
			return "05";
		}
		if ("JUN".equals(month)) {
			return "06";
		}
		if ("JUL".equals(month)) {
			return "07";
		}
		if ("AUG".equals(month)) {
			return "08";
		}
		if ("SEP".equals(month)) {
			return "09";
		}
		if ("OCT".equals(month)) {
			return "10";
		}
		if ("NOV".equals(month)) {
			return "11";
		}
		if ("DEC".equals(month)) {
			return "12";
		}
		return "01";
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the birth date so it can be compared
	 */
	public String getIsoBirthDate() {
		return isoBirthDate;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the lastPlace
	 */
	public String getLastPlace() {
		return lastPlace;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name.replace("/", "").trim() + ";" + birthDate + ";" + isoBirthDate + ";" + lastPlace + ";" + key;
	}
}
