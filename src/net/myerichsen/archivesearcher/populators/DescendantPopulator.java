package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.DescendantModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class DescendantPopulator implements ASPopulator {

	@Override
	public DescendantModel[] load(String[] args) throws Exception {
		final DescendantModel[] descendantRecords = DescendantModel.load(args[0]);
		return descendantRecords;
	}

}
