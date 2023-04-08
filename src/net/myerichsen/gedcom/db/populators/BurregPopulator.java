package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.BurregModel;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class BurregPopulator implements ASPopulator {

	@Override
	public BurregModel[] loadFromDatabase(String[] args) {
		try {
			final BurregModel[] BurregRecords = BurregModel.loadFromDatabase(args[0], args[1], args[2], args[3]);
			return BurregRecords;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
