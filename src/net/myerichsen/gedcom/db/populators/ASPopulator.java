package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.ASModel;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public interface ASPopulator {
	/**
	 * @param args
	 * @return
	 */
	ASModel[] loadFromDatabase(String[] args);
}
