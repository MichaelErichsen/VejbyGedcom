package net.myerichsen.gedcom.db.loaders;

import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 *
 * @author Michael Erichsen
 * @version 12. feb. 2023
 *
 */
public class LoadBurialPersonComplete extends LoadCphArch {
	// TODO Return message string
	/**
	 *
	 */
	static final String TABLENAME = "BURIAL_PERSON_COMPLETE";
	static final String DELETE = "DELETE FROM CPH.BURIAL_PERSON_COMPLETE";
	static final String INSERT = "INSERT INTO CPH.BURIAL_PERSON_COMPLETE (ID, NUMBER, FIRSTNAMES, LASTNAME, "
			+ "BIRTHNAME, AGEYEARS, AGEMONTH, AGEWEEKS, AGEDAYS, AGEHOURS, DATEOFBIRTH, DATEOFDEATH, YEAROFBIRTH, "
			+ "DEATHPLACE, CIVILSTATUS, ADRESSOUTSIDECPH, SEX, COMMENT, CEMETARY, CHAPEL, PARISH, STREET, HOOD, "
			+ "STREET_NUMBER, LETTER, FLOOR, INSTITUTION, INSTITUTION_STREET, INSTITUTION_HOOD, "
			+ "INSTITUTION_STREET_NUMBER, OCCUPTATIONS, OCCUPATION_RELATION_TYPES, DEATHCAUSES, DEATHCAUSES_DANISH, "
			+ "PHONNAME) VALUES (";
	static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadBurialPersonComplete derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadBurialPersonComplete");

		final LoadBurialPersonComplete lba = new LoadBurialPersonComplete();

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
