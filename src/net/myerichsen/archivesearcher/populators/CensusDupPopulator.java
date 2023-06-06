package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.CensusDupModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class CensusDupPopulator implements ASPopulator {

	@Override
	public CensusDupModel[] load(String[] args) throws Exception {
		final CensusDupModel[] censusdupRecords = CensusDupModel.load(args[0], args[1]);
		return censusdupRecords;
	}

}
