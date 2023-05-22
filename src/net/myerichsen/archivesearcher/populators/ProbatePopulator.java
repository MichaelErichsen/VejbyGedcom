package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.ProbateModel;

/**
 * @author Michael Erichsen
 * @version 26. apr. 2023
 *
 */
public class ProbatePopulator implements ASPopulator {

	@Override
	public ProbateModel[] load(String[] args) throws Exception {
		final ProbateModel[] probateRecords = ProbateModel.load(args[0], args[1], args[2], args[3], args[4]);
		return probateRecords;
	}

}
