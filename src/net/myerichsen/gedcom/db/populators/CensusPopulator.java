package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.CensusRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class CensusPopulator implements ASPopulator {

	@Override
	public CensusRecord[] loadFromDatabase(String[] args) {
		try {
			final CensusRecord[] CensusRecords = CensusRecord.loadFromDatabase(args[0], args[1], args[2], args[3]);
			return CensusRecords;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
