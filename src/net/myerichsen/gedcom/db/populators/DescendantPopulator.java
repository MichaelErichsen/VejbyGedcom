package net.myerichsen.gedcom.db.populators;

import net.myerichsen.gedcom.db.models.DescendantModel;

/**
 * @author Michael Erichsen
 * @version 12. apr. 2023
 *
 */
public class DescendantPopulator implements ASPopulator {

	@Override
	public DescendantModel[] load(String[] args) throws Exception {
		try {
			final DescendantModel[] descendantRecords = DescendantModel.load(args[0]);
			return descendantRecords;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
