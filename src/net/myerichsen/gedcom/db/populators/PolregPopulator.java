package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.PolregRecord;

/**
 * @author Michael Erichsen
 * @version 6. apr. 2023
 *
 */
public class PolregPopulator implements ASPopulator {

	@Override
	public PolregRecord[] loadFromDatabase(String[] args) {
		try {
			final PolregRecord[] PolregRecords = PolregRecord.loadFromDatabase(args[0], args[1], args[2], args[3]);
			return PolregRecords;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
