package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.SiblingsRecord;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SiblingsPopulator implements ASPopulator {

	@Override
	public SiblingsRecord[] loadFromDatabase(String[] args) throws SQLException {
		return SiblingsRecord.loadFromDatabase(args);
	}
}
