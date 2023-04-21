package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.RelocationModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class RelocationPopulator implements ASPopulator {

	@Override
	public RelocationModel[] load(String[] args) throws Exception {
		final RelocationModel[] relocationRecords = RelocationModel.load(args[0], args[1], args[2], args[3], args[4]);
		return relocationRecords;
	}

}
