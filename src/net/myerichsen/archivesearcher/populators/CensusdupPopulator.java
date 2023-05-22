package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.CensusdupModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class CensusdupPopulator implements ASPopulator {

	@Override
	public CensusdupModel[] load(String[] args) throws Exception {
		final CensusdupModel[] censusdupRecords = CensusdupModel.load(args[0], args[1]);
		return censusdupRecords;
	}

}
