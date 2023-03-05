package net.myerichsen.gedcom.db.models;

/**
 * @author Michael Erichsen
 * @version 26. feb. 2023
 *
 */
public class KipTextEntry {
	private final String amt;
	private final String herred;
	private final String sogn;
	private final String aar;

	private final String kipNr;

	/**
	 * Constructor
	 *
	 * @param amt
	 * @param herred
	 * @param sogn
	 * @param kipNr
	 */
	public KipTextEntry(String line) {
		final String[] split = line.split(";");
		amt = split[0];
		herred = split[1];
		sogn = split[2];
		aar = split[3];
		kipNr = split[4];
	}

	/**
	 * @return the aar
	 */
	public String getAar() {
		return aar;
	}

	/**
	 * @return the amt
	 */
	public String getAmt() {
		return amt;
	}

	/**
	 * @return the herred
	 */
	public String getHerred() {
		return herred;
	}

	/**
	 * @return the kipNr
	 */
	public String getKipNr() {
		return kipNr;
	}

	/**
	 * @return the sogn
	 */
	public String getSogn() {
		return sogn;
	}
}
