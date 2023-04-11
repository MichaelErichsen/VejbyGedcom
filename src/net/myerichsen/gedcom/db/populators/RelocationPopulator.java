package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.RelocationModel;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class RelocationPopulator implements ASPopulator {

	@Override
	public RelocationModel[] loadFromDatabase(String[] args) {
		try {
			final RelocationModel[] relocationRecords = RelocationModel.loadFromDatabase(args[0], args[1], args[2],
					args[3]);
			return relocationRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
