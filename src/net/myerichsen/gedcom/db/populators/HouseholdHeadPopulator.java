package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.HouseholdHeadModel;

/**
 * @author Michael Erichsen
 * @version 14. apr. 2023
 *
 */
public class HouseholdHeadPopulator implements ASPopulator {

	@Override
	public HouseholdHeadModel[] load(String[] args) throws Exception {
		try {
			final HouseholdHeadModel[] HouseholdHeadRecords = HouseholdHeadModel.load(args[0], args[1], args[2],
					args[3], args[4]);

			return HouseholdHeadRecords;
		} catch (final SQLException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
