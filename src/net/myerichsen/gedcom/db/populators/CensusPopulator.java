package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class CensusPopulator implements ASPopulator {

	@Override
	public CensusModel[] loadFromDatabase(String[] args) {
		try {
			final CensusModel[] CensusRecords = CensusModel.loadFromDatabase(args[0], args[1], args[2], args[3],
					args[4]);
			return CensusRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
