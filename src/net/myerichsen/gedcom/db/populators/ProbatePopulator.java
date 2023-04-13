package net.myerichsen.gedcom.db.populators;

import java.sql.SQLException;

import net.myerichsen.gedcom.db.models.ProbateModel;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class ProbatePopulator implements ASPopulator {

	@Override
	public ProbateModel[] load(String[] args) throws SQLException {
		final ProbateModel[] probateRecords = ProbateModel.load(args[0], args[1], args[2], args[3], args[4], args[5]);
		return probateRecords;
	}

}
