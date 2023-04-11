package net.myerichsen.gedcom.db.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for Cph archive loader programs
 *
 * @author Michael Erichsen
 * @version 11. apr. 2023
 */
public abstract class LoadCphArch {
	protected static int counter;
	private Connection conn;

	/**
	 * No arg c:tor
	 */
	public LoadCphArch() {
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
		}
		if (columnType.startsWith("CHAR") || columnType.startsWith("VARCHAR") || columnType.startsWith("DATE")) {
			return "'" + string + "'";
		}
		if (!columnType.startsWith("BOOLEAN")) {
			throw new Exception("Unknown column type: " + columnType);
		}
		if (string.equals("b'\\x00'")) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	protected void execute(String[] args) throws Exception {
		final String dbURL1 = "jdbc:derby:" + args[1];
		conn = DriverManager.getConnection(dbURL1);
		final PreparedStatement statement = conn.prepareStatement("SET SCHEMA = " + args[2]);
		statement.execute();
		loadTable(statement, args);
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
	 * @return
	 * @throws Exception
	 */
	protected void loadTable(PreparedStatement statement, String[] args) throws Exception {
		String[] columns;
		StringBuilder sb = new StringBuilder();
		String query = "";

		final List<String> columnTypes = getColumnTypes(statement, getTablename());

		statement = conn.prepareStatement(getDelete());
		statement.execute();

		final BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
		String line;

		// Ignore header line
		line = br.readLine();

		while ((line = br.readLine()) != null) {
			while (!line.endsWith("\"")) {
				line = line + br.readLine();
			}

			if (line.endsWith("\";\"")) {
				line = line.substring(0, line.length() - 2);
			}

			sb = new StringBuilder(getInsert());

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
				statement = conn.prepareStatement(query);
				statement.execute();
				counter++;
			} catch (final SQLException e) {
				if (!e.getSQLState().equals("42821")) {
					br.close();
					throw new SQLException(e);
				}
			}

		}

		br.close();

	}

}