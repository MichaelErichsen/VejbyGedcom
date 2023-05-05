package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.MilrollListModel;

/**
 * @author Michael Erichsen
 * @version 5. maj 2023
 *
 */
public class MilrollListPopulator implements ASPopulator {

	@Override
	public MilrollListModel[] load(String[] args) throws Exception {
		final MilrollListModel[] milrolllistRecords = MilrollListModel.load(args[0], args[1]);
		return milrolllistRecords;
	}

}
