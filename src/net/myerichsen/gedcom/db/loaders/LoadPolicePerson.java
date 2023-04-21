package net.myerichsen.gedcom.db.loaders;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 11. apr. feb. 2023
 *
 */
public class LoadPolicePerson extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_PERSON";
	static final String DELETE = "DELETE FROM POLICE_PERSON";
	static final String INSERT = "INSERT INTO POLICE_PERSON (ID, FIRSTNAMES, LASTNAME, "
			+ "MAIDENNAME, MARRIED, TYPE, GENDER, BIRTHPLACE, BIRTHDAY, BIRTHMONTH, "
			+ "BIRTHYEAR, DEATHDAY, DEATHMONTH, DEATHYEAR, PHONNAME) VALUES (";
	static int counter = 0;

	/**
	 * Test method
	 *
	 * @param args
	 */
	public static String loadCsvFiles(String[] args) {
		final LoadCphArch lba = new LoadPolicePerson();

		try {
			lba.execute(args);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Police person indlæst";
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
