package net.myerichsen.gedcom.cpharch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Abstract superclass for Cph archive loader programs
 *
 * @author Michael Erichsen
 * @version 3. feb. 2023
 */
public abstract class LoadCphArch {
	protected static Logger logger;
	protected static int counter;

	/**
	 * No arg c:tor
	 */
	public LoadCphArch() {
		super();
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	protected Statement connectToDB(String url) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + url;
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		logger.info("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Convert a string according to column type and value
	 *
	 * @param columnType
	 * @param strNum
	 * @return
	 * @throws Exception
	 */
	protected String convertString(String columnType, String strNum) throws Exception {
		if (strNum == null) {
			return "NULL";
		}

		final String string = strNum.replace("\"", "").replace("'", "").replace("Ã˜", "Ø");

		if (string.contains("NULL")) {
			return "NULL";
		}

		if (columnType.startsWith("INTEGER") || columnType.startsWith("DECIMAL")) {
			return string;
		} else if (columnType.startsWith("CHAR") || columnType.startsWith("VARCHAR") || columnType.startsWith("DATE")) {
			return "'" + string + "'";
		} else if (columnType.startsWith("BOOLEAN")) {
			if (string.equals("b'\\x00'")) {
				return "TRUE";
			} else {
				return "FALSE";
			}
		} else {
			throw new Exception("Unknown column type: " + columnType);
		}
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	protected void execute(String[] args) throws Exception {
		final Statement statement = connectToDB(args[1]);
		loadTable(statement, args);

		logger.info(counter + " rows added to " + getTablename() + " in " + args[0]);
		statement.close();

	}

	/**
	 * Get column types from database catalog
	 *
	 * @param statement
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	protected List<String> getColumnTypes(Statement statement, String tableName) throws SQLException {
		final String query = "SELECT SYS.SYSCOLUMNS.REFERENCEID, SYS.SYSCOLUMNS.COLUMNNUMBER, "
				+ "SYS.SYSCOLUMNS.COLUMNDATATYPE FROM SYS.SYSCOLUMNS "
				+ "INNER JOIN SYS.SYSTABLES ON SYS.SYSCOLUMNS.REFERENCEID = SYS.SYSTABLES.TABLEID "
				+ "WHERE SYS.SYSTABLES.TABLENAME = '" + tableName + "' ORDER BY SYS.SYSCOLUMNS.COLUMNNUMBER";

		statement.execute(query);

		final List<String> ls = new ArrayList<>();

		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			ls.add(rs.getString("COLUMNDATATYPE"));
		}

		return ls;

	}

	/**
	 * @return
	 */
	protected abstract String getDelete();

	/**
	 * @return
	 */
	protected abstract String getInsert();

	/**
	 * @return
	 */
	protected abstract String getTablename();

	/**
	 * Load lines into Derby table
	 *
	 * @param statement
	 * @param args
	 * @throws Exception
	 */
	protected void loadTable(Statement statement, String[] args) throws Exception {
		String[] columns;
		StringBuffer sb = new StringBuffer();
		String query = "";
		String previousLine = "";
		String thisLine = "";

		final List<String> columnTypes = getColumnTypes(statement, getTablename());
		logger.fine("Count: " + columnTypes.size());

		statement.execute(getDelete());

		final BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
		String line;

		// Ignore header line
		line = br.readLine();

		while ((line = br.readLine()) != null) {
			while (!line.endsWith("\"")) {
				line = line + br.readLine();
			}

			thisLine = line;

			if (line.endsWith("\";\"")) {
				line = line.substring(0, line.length() - 2);
				logger.info("Shortened line: " + line);
			}

			sb = new StringBuffer(getInsert());

			line = line.replace(";", ",");

			columns = line.replace("\",\"", "\";\"").split(";");

			if (columns[0].equals("\"id\"")) {
				continue;
			}

			for (int i = 0; i < columnTypes.size(); i++) {
				sb.append(convertString(columnTypes.get(i), columns[i]));

				if (i < columnTypes.size() - 1) {
					sb.append(", ");
				}
			}

			try {
				sb.append(")");
				query = sb.toString();
				logger.fine(query);
				statement.execute(query);
				counter++;
				if (counter % 100000 == 0) {
					logger.info("Counter: " + counter);
				}
				previousLine = line;
			} catch (final SQLException e) {
				if (e.getSQLState().equals("42821")) {
					logger.warning(e.getSQLState() + ", " + e.getMessage() + ", " + query);
				} else {
					logger.info("Previous: " + previousLine);
					logger.info("This : " + thisLine);
					logger.severe(e.getSQLState() + ", " + e.getMessage() + ", " + query);
					br.close();
					throw new SQLException(e);
				}
			}

		}

		br.close();

	}

}