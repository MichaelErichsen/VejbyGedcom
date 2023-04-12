package net.myerichsen.gedcom.db.loaders;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class LoadPoliceAddress extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_ADDRESS";
	static final String DELETE = "DELETE FROM POLICE_ADDRESS";
	static final String INSERT = "INSERT INTO POLICE_ADDRESS (ID, PERSON_ID, STREET, NUMBER, LETTER, FLOOR, "
			+ "SIDE, PLACE, HOST, LATITUDE, LONGITUDE, DAY, MONTH, XYEAR, FULL_ADDRESS) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static String loadCsvFiles(String[] args) {
		final LoadPoliceAddress lba = new LoadPoliceAddress();

		try {
			lba.execute(args);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Police Address er indlæst";
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
