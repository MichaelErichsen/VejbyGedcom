package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.ASModel;
import net.myerichsen.gedcom.db.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 6. maj 2023
 *
 */
public class MilrollPopulator implements ASPopulator {

	@Override
	public ASModel[] load(String[] args) throws Exception {
		final MilRollEntryModel[] milrollRecords = MilRollEntryModel.load(args[0], args[1]);
		return milrollRecords;
	}

}
