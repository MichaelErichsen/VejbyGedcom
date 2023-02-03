package net.myerichsen.gedcom.cpharch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import net.myerichsen.gedcom.db.CensusFinder;
import net.myerichsen.gedcom.db.DBIndividual;

/**
 * Find all Copenhagen registry entries that match a given individual ID in the
 * derby database
 *
 * @author Michael Erichsen
 * @version 3. feb. 2023
 *
 */
public class GetCphArchives {
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println(
					"Usage: GetCphArchives individualId vejbydatabasepath cphdatabasepath dddcsvfilepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("GetCphArchives");

		final GetCphArchives gca = new GetCphArchives();

		try {
			gca.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param vejbydbpath
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String vejbydbpath) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + vejbydbpath;
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		logger.fine("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void execute(String[] args) throws SQLException {
		final Statement statement = connectToDB(args[1]);
		final DBIndividual individual = new DBIndividual(statement, args[0]);
		statement.close();

		// individualId vejbydatabasepath cphdatabasepath dddcsvfilepath
		// outputdirectory");

		final String[] cphArgs = new String[5];
		cphArgs[0] = args[2];
		cphArgs[1] = args[4];

		final String name = individual.getName();
		final String lastName = name.substring(name.lastIndexOf(" ") + 1);

		cphArgs[2] = name.replace(lastName, "").trim();
		cphArgs[3] = lastName;
		cphArgs[4] = Integer.toString(individual.getBirthYear());

		try {
			final GetPolReg gpr = new GetPolReg(cphArgs);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		try {
			final GetBurReg gbr = new GetBurReg(cphArgs);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// individualId derbydatabasepath csvfilepath outputdirectory");
		final String[] vejbyArgs = new String[4];
		vejbyArgs[0] = args[0];
		vejbyArgs[1] = args[1];
		vejbyArgs[2] = args[3];
		vejbyArgs[3] = args[4];

		try {
			final CensusFinder cf = new CensusFinder(vejbyArgs);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		logger.info("Program ended");
	}
}
