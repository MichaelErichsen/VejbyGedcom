package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.SiblingsRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class SiblingsPopulator implements ASPopulator {

	@Override
	public SiblingsRecord[] loadFromDatabase(String[] args) throws SQLException {
		switch (args.length) {
		case 2: {
			return SiblingsRecord.loadFromDatabase(args[0], args[1]);
		}
		case 3: {
			return SiblingsRecord.loadFromDatabase(args[0], args[1], args[2]);
		}
		case 4: {
			return SiblingsRecord.loadFromDatabase(args[0], args[1], args[2], args[3]);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + args.length + ": '" + args[0] + "'");
		}
	}
}
