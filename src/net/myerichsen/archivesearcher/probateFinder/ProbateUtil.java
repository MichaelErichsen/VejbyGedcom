package net.myerichsen.archivesearcher.probateFinder;

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
		final DateTimeFormatter formatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);

		if (anEvent.getDate() == null) {
			return null;
		}
		final String date = anEvent.getDate().getValue();
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter1);
		} catch (final Exception e) {
			try {
				final int l = date.length();
				final String d2 = date.substring(l - 4);

				final Year year = Year.parse(d2);
				localDate = year.atDay(1);
			} catch (final Exception e2) {
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
		final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);
		final IndividualEvent birth = anEvent.get(0);
		final String date = birth.getDate().getValue();
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter);
		} catch (final Exception e) {
			try {
				final int l = date.length();
				final String d2 = date.substring(l - 4);

				final Year year = Year.parse(d2);
				localDate = year.atDay(1);
			} catch (final Exception e2) {
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
		final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);
		LocalDate localDate = null;

		try {
			localDate = LocalDate.parse(date, formatter);
		} catch (final Exception e) {
			try {
				final int l = date.length();
				final String d2 = date.substring(l - 4);

				final Year year = Year.parse(d2);
				localDate = year.atDay(1);
			} catch (final Exception e2) {
				e2.printStackTrace();
			}

		}

		return localDate;

	}
}
