package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.CensusdupModel;

/**
 * @author Michael Erichsen
 * @version 19. apr. 2023
 *
 */
public class CensusdupPopulator implements ASPopulator {

	@Override
	public CensusdupModel[] load(String[] args) {
		try {
			final CensusdupModel[] censusdupRecords = CensusdupModel.load(args[0], args[1]);
			return censusdupRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
