package net.myerichsen.gedcom.cpharch;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 30. jan. 2023
 *
 */
public class LoadPoliceAddress extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "POLICE_ADDRESS";
	static final String DELETE = "DELETE FROM CPH.POLICE_ADDRESS";
	static final String INSERT = "INSERT INTO CPH.POLICE_ADDRESS (ID, PERSON_ID, STREET, NUMBER, LETTER, FLOOR, "
			+ "SIDE, PLACE, HOST, LATITUDE, LONGITUDE, DAY, MONTH, XYEAR, FULL_ADDRESS) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadPoliceAddress derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadPoliceAddress");

		final LoadPoliceAddress lba = new LoadPoliceAddress();

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