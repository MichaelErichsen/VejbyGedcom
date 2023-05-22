package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.ASModel;

/**
 * @author Michael Erichsen
 * @version 13. apr. 2023
 *
 */
public interface ASPopulator {
	/**
	 * @param args
	 * @return
	 * @throws Exception
	 */
	ASModel[] load(String[] args) throws Exception;
}
