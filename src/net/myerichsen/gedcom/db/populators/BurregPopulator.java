package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.BurregModel;

/**
 * @author Michael Erichsen
 * @version 19. apr. 2023
 *
 */
public class BurregPopulator implements ASPopulator {

	@Override
	public BurregModel[] load(String[] args) {
		try {
			final BurregModel[] burregRecords = BurregModel.load(args[0], args[1], args[2], args[3], args[4]);
			return burregRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
