package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.RelocationModel;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class RelocationPopulator implements ASPopulator {

	@Override
	public RelocationModel[] load(String[] args) {
		try {
			final RelocationModel[] relocationRecords = RelocationModel.load(args[0], args[1], args[2],
					args[3], args[4]);
			return relocationRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
