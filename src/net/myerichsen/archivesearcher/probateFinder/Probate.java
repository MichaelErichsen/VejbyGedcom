package net.myerichsen.archivesearcher.probateFinder;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing a probate extract
 *
 * @author Michael Erichsen
 * @version 8. jan. 2023
 *
 */
public class Probate {
	private String name;
	private LocalDate probateDate;
	private String location;
	private String extract;

	/**
	 * Constructor
	 *
	 * @param extractPart
	 */
	public Probate(String extractPart) {
		if (!extractPart.contains("*")) {
			probateDate = null;
			return;
		}

		// Split between name and place - and the rest
		final String[] splitStar = extractPart.split("\\*");
		String[] split = null;

		if (splitStar[1].contains(" i ")) {
			split = splitStar[1].split(" i ");
		} else if (splitStar[1].contains(" fra ")) {
			split = splitStar[1].split(" fra ");
		} else if (splitStar[1].contains(" af ")) {
			split = splitStar[1].split(" af ");
		} else if (splitStar[1].contains(" in ")) {
			split = splitStar[1].split(" in ");
		} else if (splitStar[1].contains(", ")) {
			split = splitStar[1].split(", ");
		}

		try {
			name = split[0].trim();
		} catch (final Exception e) {
			probateDate = null;
			return;
		}

		location = split[1].toLowerCase().trim();

		final String tail = splitStar[2].trim();

		final Pattern p = Pattern.compile("\\d{1,2} [a-zA-Z]{3} \\d{4}");
		final Matcher m = p.matcher(tail);

		while (m.find()) {
			final String s = m.group(0);
			probateDate = ProbateUtil.parseProbateDate(s);
		}

		extract = extractPart;
	}

	/**
	 * @return the extract
	 */
	public String getExtract() {
		return extract;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the probateDate
	 */
	public LocalDate getProbateDate() {
		return probateDate;
	}

	/**
	 * @param extract the extract to set
	 */
	public void setExtract(String extract) {
		this.extract = extract;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String aName) {
		name = aName.replace("*", "").replace("hmd", "").replace("gmd", "").trim();
	}

	/**
	 * @param probateDate the probateDate to set
	 */
	public void setProbateDate(LocalDate probateDate) {
		this.probateDate = probateDate;
	}

	@Override
	public String toString() {
		return name + ";" + probateDate + ";" + location + ";" + extract;
	}
}
