package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.PotentialSpouseModel;

/**
 * @author Michael Erichsen
 * @version 29. jun. 2023
 */
public class PotentialSpousePopulator implements ASPopulator {

	@Override
	public PotentialSpouseModel[] load(String[] args) throws Exception {
		return PotentialSpouseModel.load(args[0], args[1], args[2], args[3], args[4]);

	}

}
