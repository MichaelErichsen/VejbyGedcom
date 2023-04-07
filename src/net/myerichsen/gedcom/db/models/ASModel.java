package net.myerichsen.gedcom.db.models;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract superclass for ArchiveSearcherX model classes
 * 
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public abstract class ASModel {

	/**
	 * Constructor
	 *
	 */
	public ASModel() {
		super();
	}

	/**
	 * @param string
	 * @return
	 */
	protected static String getField(ResultSet rs, String field) {
		try {
			return rs.getString(field).trim();
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * @param string
	 * @return
	 */
	protected static String getFieldInt(ResultSet rs, String field) {
		try {
			return Integer.toString(rs.getInt(field));
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * @param statement
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	protected void getTableRows(Statement statement, String tableName) throws SQLException {
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

	/**
	 * Return the object as a String Array
	 *
	 * @return
	 */
	abstract public String[] toStringArray();

}