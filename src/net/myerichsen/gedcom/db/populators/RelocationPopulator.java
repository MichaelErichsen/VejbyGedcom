package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.RelocationRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class RelocationPopulator implements ASPopulator {

	@Override
	public RelocationRecord[] loadFromDatabase(String[] args) {
		try {
			final RelocationRecord[] relocationRecords = RelocationRecord.loadFromDatabase(args[0], args[1], args[2],
					args[3]);
			return relocationRecords;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
