package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * @author Michael Erichsen
 * @version 12. apr. 2023
 *
 */
public class CensusPopulator implements ASPopulator {

	@Override
	public CensusModel[] load(String[] args) {
		try {
			final CensusModel[] censusRecords = CensusModel.load(args[0], args[1], args[2], args[3],
					args[4]);
			return censusRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
