package net.myerichsen.vejby.census;

/**
 * Contains mapping pairs between census file, individuals, census events and
 * birth events.
 * 
 * @author Michael Erichsen
 * @version 15. aug. 2020
 *
 */
public class Mapping {
	int[] individual;
	int[] census;
	int[] birth;
	int[] trade;

	/**
	 * Constructor
	 *
	 */
	public Mapping() {
		super();
		// Bruges_ikke, Personid, Husstandsnr, Navn, Køn, Fødselsår, Alder,
		// Civilstand, Erhverv, Fødested, FTÅr
		individual = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// Bruges_ikke, Alder, FTÅr, Sted
		census = new int[] { 0, 0, 0, 0 };
		// Bruges_ikke, Alder, Fødselsår, Fødested
		birth = new int[] { 0, 0, 0, 0 };
		// Bruges_ikke, FTÅr, Erhverv
		trade = new int[] { 0, 0, 0 };
	}

	/**
	 * @return the birth
	 */
	public int[] getBirth() {
		return birth;
	}

	/**
	 * @return the census
	 */
	public int[] getCensus() {
		return census;
	}

	/**
	 * @return the individual
	 */
	public int[] getIndividual() {
		return individual;
	}

	/**
	 * @return the trade
	 */
	public int[] getTrade() {
		return trade;
	}

	/**
	 * @param index
	 * @param value
	 */
	public void setBirth(int index, int value) {
		birth[index] = value;
	}

	/**
	 * @param birth
	 *            the birth to set
	 */
	public void setBirth(int[] birth) {
		this.birth = birth;
	}

	/**
	 * @param index
	 * @param value
	 */
	public void setCensus(int index, int value) {
		census[index] = value;
	}

	/**
	 * @param census
	 *            the census to set
	 */
	public void setCensus(int[] census) {
		this.census = census;
	}

	/**
	 * @param index
	 * @param value
	 */
	public void setIndividual(int index, int value) {
		individual[index] = value;
	}

	/**
	 * @param individual
	 *            the individual to set
	 */
	public void setIndividual(int[] individual) {
		this.individual = individual;
	}

	/**
	 * @param index
	 * @param value
	 */
	public void setTrade(int index, int value) {
		trade[index] = value;
	}

	/**
	 * @param trade
	 *            the trade to set
	 */
	public void setTrade(int[] trade) {
		this.trade = trade;
	}
}