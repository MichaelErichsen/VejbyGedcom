package net.myerichsen.gedcom.probateFinder;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

import org.gedcom4j.model.IndividualEvent;

/**
 * Shared utility methods
 * 
 * @author Michael Erichsen
 * @version 8. jan. 2023
 *
 */
public class ProbateUtil {
	/**
	 * Convert GEDCOM date to ISO date
	 * 
	 * @param anEvent
	 * @return The date of the event in ISO format
	 */
	protected static LocalDate parseProbateDate(IndividualEvent anEvent) {
		DateTimeFormatter formatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);

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

	/**
	 * Parse a date in GEDCOM format into ISO
	 * 
	 * @param anEvent
	 * @return The date of the event in ISO format
	 */
	protected static LocalDate parseProbateDate(List<IndividualEvent> anEvent) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy")
				.toFormatter(Locale.ENGLISH);
		IndividualEvent birth = anEvent.get(0);
		String date = birth.getDate().getValue();
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter);
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

	/**
	 * Parse a date in GEDCOM format into ISO
	 * 
	 * @param anEvent
	 * @return The date of the event in ISO format
	 */
	protected static LocalDate parseProbateDate(String date) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy")
				.toFormatter(Locale.ENGLISH);
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter);
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
}
