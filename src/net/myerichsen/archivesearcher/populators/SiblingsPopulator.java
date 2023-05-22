package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.SiblingsModel;

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
