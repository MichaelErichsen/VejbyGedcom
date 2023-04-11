package net.myerichsen.gedcom.db.loaders;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class LoadBurialPersonComplete extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "BURIAL_PERSON_COMPLETE";
	static final String DELETE = "DELETE FROM BURIAL_PERSON_COMPLETE";
	static final String INSERT = "INSERT INTO BURIAL_PERSON_COMPLETE (ID, NUMBER, FIRSTNAMES, LASTNAME, "
			+ "BIRTHNAME, AGEYEARS, AGEMONTH, AGEWEEKS, AGEDAYS, AGEHOURS, DATEOFBIRTH, DATEOFDEATH, YEAROFBIRTH, "
			+ "DEATHPLACE, CIVILSTATUS, ADRESSOUTSIDECPH, SEX, COMMENT, CEMETARY, CHAPEL, PARISH, STREET, HOOD, "
			+ "STREET_NUMBER, LETTER, FLOOR, INSTITUTION, INSTITUTION_STREET, INSTITUTION_HOOD, "
			+ "INSTITUTION_STREET_NUMBER, OCCUPTATIONS, OCCUPATION_RELATION_TYPES, DEATHCAUSES, DEATHCAUSES_DANISH, "
			+ "PHONNAME) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static String loadCsvFiles(String[] args) {
		final LoadBurialPersonComplete lba = new LoadBurialPersonComplete();

		try {
			lba.execute(args);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Begravelsesregister er indlæst";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.gedcom.cpharch.LoadCphArch#getDelete()
	 */
	@Override
	public String getDelete() {
		return DELETE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.gedcom.cpharch.LoadCphArch#getInsert()
	 */
	@Override
	public String getInsert() {
		return INSERT;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.gedcom.cpharch.LoadCphArch#getTablename()
	 */
	@Override
	public String getTablename() {
		return TABLENAME;
	}
}
