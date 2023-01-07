package net.myerichsen.gedcom.probateFinder;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.Place;
import org.gedcom4j.model.enumerations.IndividualEventType;

/**
 * A class to represent a person to be sorted
 * 
 * @author Michael Erichsen
 * @version 7. jan. 2023
 *
 */
public class ProbatePerson {
	private static DateTimeFormatter formatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);
	private String key;
	private String name;
	private String birthDate;
	private String lastPlace;

	/**
	 * Constructor
	 *
	 * @param key
	 * @param name
	 * @param birthDate
	 */
	public ProbatePerson(Entry<String, Individual> individual) {
		super();
		key = individual.getKey();
		Individual value = individual.getValue();
		name = value.getNames().get(0).getBasic();
		List<IndividualEvent> births = value.getEventsOfType(IndividualEventType.BIRTH);

		if (births.isEmpty() || (births.size() == 0)) {
			births = value.getEventsOfType(IndividualEventType.CHRISTENING);
		}

		birthDate = births.get(0).getDate().getValue();

		/**
		 * Last place Get death place Get burial place Get last event with a place Get
		 * place of that event
		 */

		List<IndividualEvent> evList = value.getEventsOfType(IndividualEventType.DEATH);

		if (evList.isEmpty() || (evList.size() == 0)) {
			evList = value.getEventsOfType(IndividualEventType.BURIAL);
		}

		LocalDate lastDate = LocalDate.of(1, 1, 1);
		lastPlace = "";
		LocalDate eventDate;
		Place place;

		if (evList.isEmpty() || (evList.size() == 0)) {
			evList = value.getEvents();
		}

		for (IndividualEvent individualEvent : evList) {
			eventDate = parseProbateDate(individualEvent);

			if ((eventDate != null) && eventDate.isAfter(lastDate) && (individualEvent.getPlace() != null)) {
				lastDate = eventDate;

				place = individualEvent.getPlace();

				if (place != null) {
					lastPlace = individualEvent.getPlace().getPlaceName();
				}

			}

		}

	}

	/**
	 * @param month
	 * @return
	 */
	private String convertMonth(String month) {
		if (month.equals("JAN")) {
			return "01";
		} else if (month.equals("FEB")) {
			return "02";
		} else if (month.equals("MAR")) {
			return "03";
		} else if (month.equals("APR")) {
			return "04";
		} else if (month.equals("MAY")) {
			return "05";
		} else if (month.equals("JUN")) {
			return "06";
		} else if (month.equals("JUL")) {
			return "07";
		} else if (month.equals("AUG")) {
			return "08";
		} else if (month.equals("SEP")) {
			return "09";
		} else if (month.equals("OCT")) {
			return "10";
		} else if (month.equals("NOV")) {
			return "11";
		} else if (month.equals("DEV")) {
			return "12";
		} else {
			return "00";
		}
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
		if (birthDate.length() == 4) {
			System.out.println(birthDate + "-01-01");
			return birthDate + "-01-01";
		} else if (birthDate.length() == 8) {
			System.out.println(birthDate.substring(4) + "-" + convertMonth(birthDate.substring(4)) + "-01");
			return birthDate.substring(4) + "-" + convertMonth(birthDate.substring(4)) + "-01";
		} else if (birthDate.length() == 11) {
			System.out.println(birthDate.substring(7, 11) + "-" + convertMonth(birthDate.substring(3, 6)) + "-"
					+ birthDate.substring(0, 2));
			return birthDate.substring(7, 11) + "-" + convertMonth(birthDate.substring(3, 6)) + "-"
					+ birthDate.substring(0, 2);
		}
		return "";
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

	/**
	 * @param births
	 * @return
	 */
	protected LocalDate parseProbateDate(IndividualEvent anEvent) {
		if (anEvent.getDate() == null) {
			return null;
		}
		String date = anEvent.getDate().getValue();
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter1);
		} catch (Exception e) {
			try {
				int l = date.length();
				String d2 = date.substring(l - 4);

				Year year = Year.parse(d2);
				localDate = year.atDay(1);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		return localDate;

	}

	@Override
	public String toString() {
		return name.replace("/", "").trim() + ";" + birthDate + ";" + lastPlace + ";" + key;
	}
}
