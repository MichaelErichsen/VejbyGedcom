package net.myerichsen.gedcom.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gedcom4j.exception.GedcomParserException;

/**
 * Class to list contents of a VEJBY Derby database.
 *
 * @author Michael Erichsen
 * @version 26. mar. 2023
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
	 * @param args
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[0];
		final Connection conn = DriverManager.getConnection(dbURL);
		System.out.println("Connected to database " + dbURL);
		stmt = conn.createStatement();
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
		connectToDB(args);

		System.out.println("\nFamily");
		getTableRows(stmt, "VEJBY.FAMILY");

		System.out.println("\nIndividual");
		getTableRows(stmt, "VEJBY.INDIVIDUAL");

		System.out.println("\nEvent");
		getTableRows(stmt, "VEJBY.EVENT");

		System.out.println("\nCensus");
		getTableRows(stmt, "VEJBY.CENSUS");

		stmt.close();
		System.out.println("Program ended.");
	}

	/**
	 * @param statement
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private void getTableRows(Statement statement, String tableName) throws SQLException {
		final String SELECT_METADATA = "SELECT * FROM %s FETCH FIRST 20 ROWS ONLY";
		final String query = String.format(SELECT_METADATA, tableName);
		final ResultSet rs = statement.executeQuery(query);
		final ResultSetMetaData rsmd = rs.getMetaData();
		StringBuffer sb = new StringBuffer();
		final int columnCount = rsmd.getColumnCount();

		for (int i = 1; i < (columnCount + 1); i++) {
			sb.append(rsmd.getColumnName(i).trim() + ";");
		}

		System.out.println(sb.toString());

		while (rs.next()) {
			sb = new StringBuffer();

			for (int i = 1; i < (columnCount + 1); i++) {
				if (rs.getString(i) == null) {
					sb.append(";");
				} else {
					sb.append(rs.getString(i).trim() + ";");
				}
			}

			System.out.println(sb.toString());
		}
	}
}