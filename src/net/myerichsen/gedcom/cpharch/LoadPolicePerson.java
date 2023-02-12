package net.myerichsen.gedcom.cpharch;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 12. feb. 2023
 *
 */
public class LoadPolicePerson extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_PERSON";
	static final String DELETE = "DELETE FROM CPH.POLICE_PERSON";
	static final String INSERT = "INSERT INTO CPH.POLICE_PERSON (ID, FIRSTNAMES, LASTNAME, "
			+ "MAIDENNAME, MARRIED, TYPE, GENDER, BIRTHPLACE, BIRTHDAY, BIRTHMONTH, "
			+ "BIRTHYEAR, DEATHDAY, DEATHMONTH, DEATHYEAR, PHONNAME) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadPolicePerson derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadPolicePerson");
		logger.info("Loading table " + TABLENAME + " from " + args[1]);

		final LoadCphArch lba = new LoadPolicePerson();

		try {
			lba.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
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
