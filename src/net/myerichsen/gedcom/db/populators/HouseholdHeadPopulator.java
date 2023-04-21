package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.HouseholdHeadModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class HouseholdHeadPopulator extends Thread implements ASPopulator {

	@Override
	public HouseholdHeadModel[] load(String[] args) throws Exception {
		final HouseholdHeadModel[] HouseholdHeadRecords = HouseholdHeadModel.load(args[0], args[1], args[2], args[3],
				args[4]);
		return HouseholdHeadRecords;
	}

}
