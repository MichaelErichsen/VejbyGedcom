package net.myerichsen.gedcom.db.loaders;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 31. jan. 2023
 *
 */
public class LoadPolicePosition extends LoadCphArch {
	/**
	 *
	 */
	// TODO Return message string
	static final String TABLENAME = "POLICE_POSITION";
	static final String DELETE = "DELETE FROM CPH.POLICE_POSITION";
	static final String INSERT = "INSERT INTO CPH.POLICE_POSITION (ID, PERSON_ID, POSITION_DANISH, POSITION_ENGLISH, "
			+ "ISCO_MAJOR_GROUP, ISCO_SUBMAJOR_GROUP, ISCO_MINOR_GROUP, ISCO_UNIT)  VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadPolicePosition derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadPolicePosition");
		logger.info("Loading table " + TABLENAME + " from " + args[1]);

		final LoadPolicePosition lba = new LoadPolicePosition();

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
