package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.CensusModel;

/**
 * @author Michael Erichsen
 * @version 28. jun. 2023
 *
 */
public class CensusPopulator implements ASPopulator {

	@Override
	public CensusModel[] load(String[] args) throws Exception {
		return CensusModel.load(args[0], args[1], args[2], args[3], args[4]);
	}

}
