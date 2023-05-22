package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.BurregModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class BurregPopulator implements ASPopulator {

	@Override
	public BurregModel[] load(String[] args) throws Exception {
		final BurregModel[] burregRecords = BurregModel.load(args[0], args[1], args[2], args[3], args[4]);
		return burregRecords;
	}

}
