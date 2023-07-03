package net.myerichsen.archivesearcher.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.myerichsen.archivesearcher.util.Fonkod;
import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * Abstract superclass for Cph archive loader programs
 *
 * @author Michael Erichsen
 * @version 3. jul. 2023
 *
 */
public abstract class LoadCphArch {
	/**
	 *
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_COLUMN_METADATA = "SELECT SYS.SYSCOLUMNS.REFERENCEID, SYS.SYSCOLUMNS.COLUMNNUMBER, "
			+ "SYS.SYSCOLUMNS.COLUMNDATATYPE FROM SYS.SYSCOLUMNS "
			+ "INNER JOIN SYS.SYSTABLES ON SYS.SYSCOLUMNS.REFERENCEID = SYS.SYSTABLES.TABLEID "
			+ "WHERE SYS.SYSTABLES.TABLENAME = ? ORDER BY SYS.SYSCOLUMNS.COLUMNNUMBER";
	protected static int counter;
	private static Fonkod fk = new Fonkod();
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
		}
		return "FALSE";
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	protected void execute(String[] args, ArchiveSearcher as) throws Exception {
		final String dbURL = "jdbc:derby:" + args[1];
		try {
			DriverManager.getConnection(dbURL + ";shutdown=true");
		} catch (final SQLException e) {
			// Shutdown message is expected
//			Display.getDefault().asyncExec(() -> as.setMessage(e.getMessage()));
		}
		conn = DriverManager.getConnection(dbURL);
		final PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, args[2]);
		statement.execute();
		loadTable(conn, args);
		statement.close();
		conn.close();

	}

	/**
	 * Generate phonetic name
	 *
	 * @param givenName
	 * @param lastname
	 * @return
	 * @throws Exception
	 */
	protected String generatePhonName(String[] column) throws Exception {
		final String s = column[2] + " " + column[3];
		return fk.generateKey(s.replace("\"", ""));
	}

	/**
	 * Get column types from database catalog
	 *
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	protected List<String> getColumnTypes(Connection conn, String tableName) throws SQLException {
		final List<String> ls = new ArrayList<>();

		final PreparedStatement ps = conn.prepareStatement(SELECT_COLUMN_METADATA);
		ps.setString(1, tableName);
		final ResultSet rs = ps.executeQuery();

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
	 * @param conn
	 * @param args
	 * @return
	 * @throws Exception
	 */
	protected void loadTable(Connection conn, String[] args) throws Exception {
		String[] columns;
		StringBuilder sb = new StringBuilder();
		String query = "";

		final List<String> columnTypes = getColumnTypes(conn, getTablename());

		PreparedStatement ps = conn.prepareStatement(getDelete());
		ps.execute();

		final BufferedReader br = new BufferedReader(new FileReader(new File(args[0] + "/" + args[3])));
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

			for (int i = 0; i < columns.length; i++) {
				sb.append(convertString(columnTypes.get(i), columns[i]));

				if (i < columns.length - 1) {
					sb.append(", ");
				}
			}

			if (columnTypes.size() > columns.length) {
				sb.append(", '" + generatePhonName(columns) + "'");
			}

			try {
				sb.append(")");
				query = sb.toString();
				ps = conn.prepareStatement(query);
				ps.execute();
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