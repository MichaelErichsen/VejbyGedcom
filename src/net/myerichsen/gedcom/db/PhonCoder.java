package net.myerichsen.gedcom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Read each individual in a table. Insert phonetic coding of FIRSTNAMES and
 * LASTNAME and store the result in PHONNAME.
 *
 * C:\Users\michael\CPHDB CPH.BURIAL_PERSON_COMPLETE FIRSTNAMES LASTNAME
 * PHONNAME
 *
 * @author Michael Erichsen
 * @version 13. feb. 2023
 *
 */

public class PhonCoder {
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println(
					"Usage: PhonCoder derbydatabasepath tablename firstnamecolumn lastnamecolumn phoncodedcolumn");
			System.exit(4);
		}

		logger = Logger.getLogger("PhonCoder");
		final PhonCoder pc = new PhonCoder();

		try {
			pc.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws Exception
	 *
	 */
	private void execute(String[] args) throws SQLException {
		final String COUNT = "SELECT COUNT(*) FROM %s";
		final String SELECT = "SELECT %s, %s FROM %s FOR UPDATE OF %s";
		final String UPDATE = "UPDATE %s SET %s = '%s' WHERE CURRENT OF PHONCURSOR";
		final Fonkod fk = new Fonkod();
		final String dbURL = "jdbc:derby:" + args[0];
		int counter = 0;
		Connection conn;
		Statement statement;
		PreparedStatement ps;
		String phonName;

		// Connect to Derby database

		try {
			conn = DriverManager.getConnection(dbURL);
			conn.setAutoCommit(false);
			logger.info("Connected to database " + dbURL);
			statement = conn.createStatement();
			statement.setCursorName("PHONCURSOR");
		} catch (final SQLException e) {
			throw new SQLException(e);
		}

		// Get row count

		String query = String.format(COUNT, args[1]);
		ResultSet rs = statement.executeQuery(query);
		if (rs.next()) {
			logger.info("Tabellen indeholder " + rs.getInt(1) + " rækker");
		}

		// Select each row in the individual table

		query = String.format(SELECT, args[2], args[3], args[1], args[4]);
		logger.info(query);

		rs = statement.executeQuery(query);

		while (rs.next()) {
			// Generate a phonetic key for the name

			try {
				phonName = fk.generateKey(rs.getString(args[2]).trim() + " " + rs.getString(args[3]).trim());
			} catch (final Exception e) {
				phonName = "";
			}

			// Update the individual with a phonetic name

			query = String.format(UPDATE, args[1], args[4], phonName);
			ps = conn.prepareStatement(query);
			ps.execute();
			counter++;

			if ((counter % 10000) == 0) {
				logger.info("Opdateret " + counter);
			}
		}

		conn.commit();
		statement.close();
		logger.info(counter + " rækker er opdateret");
	}
}
