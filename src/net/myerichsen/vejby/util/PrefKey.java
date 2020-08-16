package net.myerichsen.vejby.util;

/**
 * Constants for preference keys
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class PrefKey {

	/**
	 * Dummy constructor to prevent instrantiation
	 *
	 */
	private PrefKey() {
	}

	public static final String KIPFILENAME = ".";
	public static final String GEDCOMFILENAME = "c:/temp/vejby.ged";

	public static final String INDIVIDUAL_0 = "IBruges_ikke";
	public static final String INDIVIDUAL_1 = "IPersonid";
	public static final String INDIVIDUAL_2 = "IHusstandsnr";
	public static final String INDIVIDUAL_3 = "INavn";
	public static final String INDIVIDUAL_4 = "IK�n";
	public static final String INDIVIDUAL_5 = "IF�dsels�r";
	public static final String INDIVIDUAL_6 = "IAlder,";
	public static final String INDIVIDUAL_7 = "ICivilstand";
	public static final String INDIVIDUAL_8 = "IErhverv";
	public static final String INDIVIDUAL_9 = "IF�dested";
	public static final String INDIVIDUAL_10 = "IFT�r";

	public static final String CENSUS_0 = "CBruges_ikke";
	public static final String CENSUS_1 = "CAlder";
	public static final String CENSUS_2 = "CFT�r";
	public static final String CENSUS_3 = "CSted";

	public static final String BIRTH_0 = "BBruges_ikke";
	public static final String BIRTH_1 = "BAlder";
	public static final String BIRTH_2 = "BF�dsels�r";
	public static final String BIRTH_3 = "BF�dested";

	public static final String OCCUPATION_0 = "TBruges_ikke";
	public static final String OCCUPATION_1 = "TFT�r";
	public static final String OCCUPATION_2 = "TErhverv";

}
