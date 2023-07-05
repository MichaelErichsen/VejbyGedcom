package net.myerichsen.archivesearcher.loaders;

import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
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
	 * Main method
	 *
	 * @param args Csv file directory, db path, schema, LoadPolicePerson
	 * @param as   ArchiveSearcher
	 * @return Message
	 */
	public static String main(String[] args, ArchiveSearcher as) {
		final LoadCphArch lba = new LoadPolicePerson();

		try {
			lba.execute(args, as);
		} catch (final Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "Police person indlæst";
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
