package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.RelocationModel;

/**
 * @author Michael Erichsen
 * @version 4. maj 2023
 *
 */
public class RelocationPopulator implements ASPopulator {

	@Override
	public RelocationModel[] load(String[] args) throws Exception {
		final RelocationModel[] relocationRecords = RelocationModel.load(args[0], args[1], args[2], args[3]);
		return relocationRecords;
	}

}
