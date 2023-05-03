package net.myerichsen.gedcom.db.tablecreators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Michael Erichsen
 * @version 3. maj 2023
 *
 */
public class MilRollCreator {
	private static final String CREATE_SCHEMA = "CREATE SCHEMA ";
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String CREATE_TABLE = "CREATE TABLE RULLER ( AMT CHAR(32), AAR CHAR(32), "
			+ "RULLETYPE CHAR(32), LAEGDNR INT NOT NULL, SOGN CHAR(32), LITRA CHAR(32), GLLOEBENR INT, "
			+ "NYLOEBENR INT, FADER CHAR(32), SOEN CHAR(32), FOEDESTED CHAR(32), ALDER INT, "
			+ "STOERRELSEITOMMER DECIMAL(5,2), OPHOLD CHAR(32), ANMAERKNINGER CHAR(64), FOEDT DATE, "
			+ "GEDCOMID CHAR(32), NAVN CHAR(64), FADERFON CHAR(32), SOENFON CHAR(32) )";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager
					.getConnection("jdbc:derby:" + props.getProperty("milrollPath") + ";create=true");
			PreparedStatement statement = conn.prepareStatement(CREATE_SCHEMA + props.getProperty("milrollSchema"));
			statement.execute();
			statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("milrollSchema"));
			statement.execute();
			statement = conn.prepareStatement(CREATE_TABLE);
			statement.execute();
			return "Lægdsrulletabellen er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

}
