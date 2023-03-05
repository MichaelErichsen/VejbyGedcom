package net.myerichsen.gedcom.db.loaders;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 30. jan. 2023
 *
 */
public class LoadBurialAddress extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "BURIAL_ADDRESS";
	static final String DELETE = "DELETE FROM CPH.BURIAL_ADDRESS";
	static final String INSERT = "INSERT INTO CPH.BURIAL_ADDRESS (ID, PERSON_ID, STREET, HOOD, STREET_UNIQUE, STREET_NUMBER, "
			+ "LETTER, FLOOR, INSTITUTION, INSTITUTION_STREET, INSTITUTION_HOOD, "
			+ "INSTITUTION_STREET_UNIQUE, INSTITUTION_STREET_NUMBER) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadBurialAddress derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadBurialAddress");

		final LoadBurialAddress lba = new LoadBurialAddress();

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
