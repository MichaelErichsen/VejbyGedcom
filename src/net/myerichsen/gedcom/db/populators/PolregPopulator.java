package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.PolregModel;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class PolregPopulator implements ASPopulator {

	@Override
	public PolregModel[] load(String[] args) {
		try {
			final PolregModel[] PolregRecords = PolregModel.load(args[0], args[1], args[2], args[3], args[4]);
			return PolregRecords;
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
