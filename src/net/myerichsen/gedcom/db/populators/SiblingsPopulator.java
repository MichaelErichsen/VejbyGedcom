package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.SiblingsModel;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SiblingsPopulator implements ASPopulator {

	@Override
	public SiblingsModel[] loadFromDatabase(String[] args) throws SQLException {
		return SiblingsModel.loadFromDatabase(args);
	}
}
