package net.myerichsen.gedcom.cpharch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Superclass for Cph archive loader programs
 * 
 * @author Michael Erichsen
 * @bversion 30. jan. 2023
 *
 */
public class LoadCphArch {

	protected static Logger logger;

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
		final String dbURL1 = "jdbc:derby:C:/Users/michael/CPHDB";
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
	public String convertString(String columnType, String strNum) throws Exception {
		if (strNum == null) {
			return "NULL";
		}

		final String string = strNum.replace("\"", "").replace("'", "").replace("Ã˜", "Ø");

		if (string.contains("NULL")) {
			return "NULL";
		}

		if ((columnType.startsWith("INTEGER")) || (columnType.startsWith("DECIMAL"))) {
			return string;
		} else if ((columnType.startsWith("CHAR")) || (columnType.startsWith("VARCHAR"))
				|| (columnType.startsWith("DATE"))) {
			return "'" + string + "'";
		} else
			throw new Exception("Unknown column type: " + columnType);
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

		List<String> ls = new ArrayList<>();

		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			ls.add(rs.getString("COLUMNDATATYPE"));
		}

		return ls;

	}

}