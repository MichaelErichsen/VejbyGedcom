package net.myerichsen.gedcom.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gedcom4j.exception.GedcomParserException;

/**
 * Class to list relocations from a GEDCOM Derby database.
 *
 * @author Michael Erichsen
 * @version 2023-01-24
 *
 */
public class DBRelocation {
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: DBRelocation derbydatabasepath");
			System.exit(4);
		}

		logger = Logger.getLogger("DBRelocation");
		logger.setLevel(Level.FINE);

		final DBRelocation dbr = new DBRelocation();

		try {
			dbr.execute(args);
		} catch (IOException | GedcomParserException | SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	private Statement stmt;

	/**
	 * Connect to the Derby database
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String url) throws SQLException {
		final String dbURL1 = "jdbc:derby:C:/Users/michael/VejbyDB";
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		System.out.println("Connected to database " + dbURL1);
		stmt = conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws GedcomParserException
	 * @throws IOException
	 * @throws SQLException
	 */
	private void execute(String[] args) throws IOException, GedcomParserException, SQLException {
		connectToDB(args[0]);
		final String query = "SELECT VEJBY.INDIVIDUAL.GIVENNAME, VEJBY.INDIVIDUAL.SURNAME, VEJBY.INDIVIDUAL.PHONNAME, "
				+ "VEJBY.EVENT.DATE, VEJBY.EVENT.PLACE, VEJBY.EVENT.NOTE "
				+ "FROM VEJBY.INDIVIDUAL, VEJBY.FAMILY, VEJBY.EVENT WHERE VEJBY.EVENT.SUBTYPE = 'Flytning' "
				+ "AND ( VEJBY.INDIVIDUAL.ID = VEJBY.EVENT.INDIVIDUAL OR VEJBY.EVENT.FAMILY IS NOT NULL "
				+ "AND ( VEJBY.INDIVIDUAL.ID = VEJBY.FAMILY.HUSBAND OR VEJBY.INDIVIDUAL.ID = VEJBY.FAMILY.WIFE ) ) "
				+ "ORDER BY VEJBY.INDIVIDUAL.SURNAME, VEJBY.INDIVIDUAL.GIVENNAME";

		final ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			System.out.println(rs.getString(1) + ";" + rs.getString(2) + ";" + rs.getString(3) + ";" + rs.getString(4)
					+ ";" + rs.getString(5) + ";" + rs.getString(6));
		}

		System.out.println("Program ended.");
	}

}
