package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.CensusModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class CensusPopulator implements ASPopulator {

	@Override
	public CensusModel[] load(String[] args) throws Exception {
		final CensusModel[] censusRecords = CensusModel.load(args[0], args[1], args[2], args[3], args[4]);
		return censusRecords;
	}

}
