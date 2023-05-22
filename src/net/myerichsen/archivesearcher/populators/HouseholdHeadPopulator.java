package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.HouseholdHeadModel;

/**
 * @author Michael Erichsen
 * @version 9. maj 2023
 *
 */
public class HouseholdHeadPopulator extends Thread implements ASPopulator {

	@Override
	public HouseholdHeadModel[] load(String[] args) throws Exception {
		final HouseholdHeadModel[] HouseholdHeadRecords = HouseholdHeadModel.load(args[0], args[1], args[2], args[3],
				args[4], args[5], args[6]);
		return HouseholdHeadRecords;
	}

}
