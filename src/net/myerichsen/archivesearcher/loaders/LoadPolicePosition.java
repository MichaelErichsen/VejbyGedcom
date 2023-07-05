package net.myerichsen.archivesearcher.loaders;

import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
 *
 */
public class LoadPolicePosition extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_POSITION";
	static final String DELETE = "DELETE FROM POLICE_POSITION";
	static final String INSERT = "INSERT INTO POLICE_POSITION (ID, PERSON_ID, POSITION_DANISH, POSITION_ENGLISH, "
			+ "ISCO_MAJOR_GROUP, ISCO_SUBMAJOR_GROUP, ISCO_MINOR_GROUP, ISCO_UNIT)  VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args Csv file directory, db path, schema, LoadPolicePosition
	 * @param as   ArchiveSearcher
	 * @return Message
	 */
	public static String main(String[] args, ArchiveSearcher as) {
		final LoadPolicePosition lba = new LoadPolicePosition();

		try {
			lba.execute(args, as);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Police position indlæst";
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
