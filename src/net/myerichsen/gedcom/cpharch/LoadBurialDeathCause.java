package net.myerichsen.gedcom.cpharch;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @bversion 30. jan. 2023
 *
 */
public class LoadBurialDeathCause extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "BURIAL_DEATHCAUSE";
	static final String DELETE = "DELETE FROM CPH.BURIAL_DEATHCAUSE";
	static final String INSERT = "INSERT INTO CPH.BURIAL_DEATHCAUSE (ID, PERSON_ID, DEATHCAUSE, "
			+ "DEATHCAUSE_DANISH, XORDER, PRIORITY) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadBurialDeathCause derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadBurialDeathCause");

		final LoadBurialDeathCause lba = new LoadBurialDeathCause();

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
