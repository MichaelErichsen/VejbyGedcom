package net.myerichsen.gedcom.cpharch;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 30. jan. 2023
 *
 */
public class LoadBurialPerson extends LoadCphArch {
	/**
	 *
	 */
	static final String TABLENAME = "BURIAL_PERSON";
	static final String DELETE = "DELETE FROM CPH.BURIAL_PERSON";
	static final String INSERT = "INSERT INTO CPH.BURIAL_PERSON (ID, NUMBER, FIRSTNAMES, LASTNAME, "
			+ "BIRTHNAME, AGEYEARS, AGEMONTH, AGEWEEKS, AGEDAYS, AGEHOURS, DATEOFBIRTH, DATEOFDEATH, YEAROFBIRTH, "
			+ "DEATHPLACE, CIVILSTATUS, ADRESSOUTSIDECPH, SEX, COMMENT, CEMETARY, CHAPEL, PARISH ) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadBurialPerson derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadBurialPerson");

		final LoadBurialPerson lba = new LoadBurialPerson();

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
