package net.myerichsen.gedcom.cpharch;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 31. jan. 2023
 *
 */
public class LoadPoliceRelation extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_RELATION";
	static final String DELETE = "DELETE FROM CPH.POLICE_RELATION";
	static final String INSERT = "INSERT INTO CPH.POLICE_RELATION (PERSON_MAIN_ID, PERSON_RELATED_ID, RELATION_TYPE) "
			+ "VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadPoliceRelation derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadPoliceRelation");
		logger.info("Loading table " + TABLENAME + " from " + args[1]);

		final LoadPoliceRelation lba = new LoadPoliceRelation();

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
