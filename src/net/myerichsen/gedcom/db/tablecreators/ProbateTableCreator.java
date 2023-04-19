package net.myerichsen.gedcom.db.tablecreators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Michael Erichsen
 * @version 13. apr. 2023
 *
 */
public class ProbateTableCreator {
	/**
	 *
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String CR1 = "CREATE TABLE INDIVIDUAL ( ID CHAR(8) NOT NULL, NAME CHAR(100), FONKOD CHAR(50), EVENT_ID CHAR(8), SOURCE CHAR(100) NOT NULL )";
	private static final String CR2 = "CREATE TABLE EVENT ( ID CHAR(8) NOT NULL, FROMDATE DATE, TODATE DATE, PLACE CHAR(50), EVENTTYPE CHAR(30), VITALTYPE CHAR(30), COVERED_DATA VARCHAR(3600), SOURCE CHAR(100) NOT NULL, CAPTION CHAR(50) )";
	private static final String CR3 = "CREATE INDEX IX2 ON INDIVIDUAL (FONKOD ASC)";
	private static final String CR4 = "CREATE INDEX IX1 ON EVENT (FROMDATE ASC)";
	private static final String CR5 = "CREATE INDEX IX3 ON INDIVIDUAL (NAME ASC)";
	private static final String CR6 = "CREATE INDEX SQL111222132042201 ON INDIVIDUAL (EVENT_ID ASC, SOURCE ASC)";
	private static final String CR7 = "CREATE UNIQUE INDEX SQL111222132041940 ON EVENT (ID ASC, SOURCE ASC)";
	private static final String CR8 = "CREATE INDEX IX4 ON EVENT (PLACE ASC)";
	private static final String CR9 = "CREATE UNIQUE INDEX SQL111222132042200 ON INDIVIDUAL (ID ASC, SOURCE ASC)";
	private static final String CRA = "ALTER TABLE EVENT ADD CONSTRAINT SQL111222132041940 <PRIMARY KEY ( ID, SOURCE)";
	private static final String CRB = "ALTER TABLE INDIVIDUAL ADD CONSTRAINT SQL111222132042200 PRIMARY KEY ( ID, SOURCE)";
	private static final String CRC = "ALTER TABLE INDIVIDUAL ADD CONSTRAINT SQL111222132042201 FOREIGN KEY (EVENT_ID, SOURCE) REFERENCES EVENT (ID, SOURCE)";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("probatePath"));
			PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("probateSchema"));
			statement.execute();
			statement = conn.prepareStatement(CR1);
			statement.execute();
			statement = conn.prepareStatement(CR2);
			statement.execute();
			statement = conn.prepareStatement(CR3);
			statement.execute();
			statement = conn.prepareStatement(CR4);
			statement.execute();
			statement = conn.prepareStatement(CR5);
			statement.execute();
			statement = conn.prepareStatement(CR6);
			statement.execute();
			statement = conn.prepareStatement(CR7);
			statement.execute();
			statement = conn.prepareStatement(CR8);
			statement.execute();
			statement = conn.prepareStatement(CR9);
			statement.execute();
			statement = conn.prepareStatement(CRA);
			statement.execute();
			statement = conn.prepareStatement(CRB);
			statement.execute();
			statement = conn.prepareStatement(CRC);
			statement.execute();
			statement = conn.prepareStatement(CR3);
			statement.execute();
			statement = conn.prepareStatement(CR4);
			statement.execute();
			statement = conn.prepareStatement(CR5);
			statement.execute();
			return "Skifteprotokoltabellerne er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

}
