package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.SiblingsRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class SiblingsPopulator implements ASPopulator {

	@Override
	public SiblingsRecord[] loadFromDatabase(String[] args) {
		// FIXME Exception in thread "Thread-6"
		// java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1
		final SiblingsRecord[] SiblingRecords = SiblingsRecord.loadFromDatabase(args[0], args[1], args[2], args[3]);
		return SiblingRecords;
	}
}
