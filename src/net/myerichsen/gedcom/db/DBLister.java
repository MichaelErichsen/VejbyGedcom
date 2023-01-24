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
 * Class to list contents of a GEDCOM Derby database.
 *
 * @author Michael Erichsen
 * @version 2023-01-24
 *
 */
public class DBLister {
	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: DBLister derbydatabasepath");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLister");
		logger.setLevel(Level.FINE);

		final DBLister dbl = new DBLister();

		try {
			dbl.execute(args);
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

		System.out.println("\nFamily");
		String query = "SELECT * FROM VEJBY.FAMILY FETCH FIRST 20 ROWS ONLY";
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			System.out.println(rs.getString("ID") + ";" + rs.getString("HUSBAND") + ";" + rs.getString("WIFE"));
		}

		System.out.println("\nIndividual");
		query = "SELECT * FROM VEJBY.INDIVIDUAL FETCH FIRST 20 ROWS ONLY";
		rs = stmt.executeQuery(query);

		while (rs.next()) {
			System.out.println(rs.getString("ID") + ";" + rs.getString("GIVENNAME") + ";" + rs.getString("SURNAME")
					+ ";" + rs.getString("SEX") + ";" + rs.getString("PHONNAME") + ";" + rs.getString("FAMC"));
		}

		System.out.println("\nEvent");
		query = "SELECT * FROM VEJBY.EVENT FETCH FIRST 20 ROWS ONLY";
		rs = stmt.executeQuery(query);

		while (rs.next()) {
			System.out.println(rs.getInt("ID") + ";" + rs.getString("TYPE") + ";" + rs.getString("SUBTYPE") + ";"
					+ rs.getString("DATE") + ";" + rs.getString("INDIVIDUAL") + ";" + rs.getString("FAMILY") + ";"
					+ rs.getString("PLACE") + ";" + rs.getString("NOTE"));
		}

		System.out.println("\nCitation");
		query = "SELECT * FROM VEJBY.CITATION FETCH FIRST 20 ROWS ONLY";
		rs = stmt.executeQuery(query);

		while (rs.next()) {
			System.out.println(rs.getInt("ID") + ";" + rs.getString("EVENT") + ";" + rs.getString("TEXT"));
		}

		System.out.println("Program ended.");
	}

}
