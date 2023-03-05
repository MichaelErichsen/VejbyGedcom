package net.myerichsen.gedcom.db.loaders;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 31. jan. 2023
 *
 */
public class LoadPolicePersonWOccupations extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_PERSON_W_OCCUPATIONS";
	static final String DELETE = "DELETE FROM CPH.POLICE_PERSON_W_OCCUPATIONS";
	static final String INSERT = "INSERT INTO CPH.POLICE_PERSON_W_OCCUPATIONS (ID, FIRSTNAMES, LASTNAME, MAIDENNAME, "
			+ "MARRIED, TYPE, GENDER, BIRTHPLACE, BIRTHDAY, BIRTHMONTH, BIRTHYEAR, DEATHDAY, DEATHMONTH, "
			+ "DEATHYEAR, OCCUPATIONS, OCCUPATIONS_ISCO_MAJOR_GROUPS, OCCUPATIONS_ISCO_SUBMAJOR_GROUPS, "
			+ "OCCUPATIONS_ISCO_MINOR_GROUPS) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadPolicePersonWOccupations derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadPolicePersonWOccupations");
		logger.info("Loading table " + TABLENAME + " from " + args[1]);

		final LoadPolicePersonWOccupations lba = new LoadPolicePersonWOccupations();

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
