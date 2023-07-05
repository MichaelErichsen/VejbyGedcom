package net.myerichsen.archivesearcher.loaders;

import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
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
	 * @param args Csv file directory, db path, schema, LoadPoliceAddress
	 * @param as   ArchiveSearcher
	 * @return Message
	 */
	public static String main(String[] args, ArchiveSearcher as) {
		final LoadPoliceAddress lba = new LoadPoliceAddress();

		try {
			lba.execute(args, as);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Police Address er indlæst";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.archivesearcher.cpharch.LoadCphArch#getDelete()
	 */
	@Override
	public String getDelete() {
		return DELETE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.archivesearcher.cpharch.LoadCphArch#getInsert()
	 */
	@Override
	public String getInsert() {
		return INSERT;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.myerichsen.archivesearcher.cpharch.LoadCphArch#getTablename()
	 */
	@Override
	public String getTablename() {
		return TABLENAME;
	}
}
