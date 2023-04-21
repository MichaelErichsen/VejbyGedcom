package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.SiblingsModel;

/**
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class SiblingsPopulator implements ASPopulator {

	@Override
	public SiblingsModel[] load(String[] args) throws Exception {
		return SiblingsModel.load(args);
	}
}
