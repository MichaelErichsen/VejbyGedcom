package net.myerichsen.vejby.census;

import java.util.Arrays;
import java.util.List;

/**
 * @author michael
 *
 */
public class Household {
	private List<Family> families;
	private List<Person> singles;
	private static List<String> relationKeywords = Arrays.asList("Barn", "Børn", "Broder", "Broderdatter", "Brodersøn",
			"Brødre", "Datter", "Datterdatter", "Forældre", "Hustru", "Kone", "Manden", "Moder", "Pleiebarn",
			"Pleiebørn", "Pleiedatter", "Pleiesøn", "Søn", "Sønnesøn", "Søster", "Søsterdatter", "Stedfader",
			"Svigerfader", "Svigerfar", "Svigerforældre", "Svigermoder", "Svigersøn");

	/**
	 * @return message
	 */
	public String identifyFamilies() {
		// For each household
		// The first person is the primary person, father or mother according to
		// sex
		// If no children, then it is not a family
		// Identify father, mother and children from the position in household
		// column - or trade column
		// Create a family
		// Find other relations and add further families

		return "OK";
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * @param families
	 *            the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * @return the singles
	 */
	public List<Person> getSingles() {
		return singles;
	}

	/**
	 * @param singles
	 *            the singles to set
	 */
	public void setSingles(List<Person> singles) {
		this.singles = singles;
	}
}
