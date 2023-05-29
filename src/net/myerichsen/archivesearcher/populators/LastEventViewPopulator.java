package net.myerichsen.archivesearcher.populators;

import net.myerichsen.archivesearcher.models.ASModel;
import net.myerichsen.archivesearcher.models.LastEventModel;

/**
 * @author Michael Erichsen
 * @version 26. maj 2023
 *
 */
public class LastEventViewPopulator implements ASPopulator {

	@Override
	public ASModel[] load(String[] args) throws Exception {
		final LastEventModel[] array = LastEventModel.load(args[0], args[1], args[2]);
		return array;
	}

}
