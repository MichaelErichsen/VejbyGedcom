package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.ProbateRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class ProbatePopulator implements ASPopulator {

	@Override
	public ProbateRecord[] loadFromDatabase(String[] args) {
		final ProbateRecord[] probateRecords = ProbateRecord.loadFromDatabase(args[0], args[1], args[2], args[3]);
		return probateRecords;
	}

}
