package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.ProbateModel;

/**
 * @author Michael Erichsen
 * @version 8. apr. 2023
 *
 */
public class ProbatePopulator implements ASPopulator {

	@Override
	public ProbateModel[] loadFromDatabase(String[] args) throws SQLException {
		final ProbateModel[] probateRecords = ProbateModel.loadFromDatabase(args[0], args[1], args[2], args[3],
				args[4]);
		return probateRecords;
	}

}
