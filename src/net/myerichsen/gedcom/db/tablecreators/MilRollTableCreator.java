package net.myerichsen.gedcom.db.tablecreators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Michael Erichsen
 * @version 9. maj 2023
 *
 */
public class MilRollTableCreator {
	private static final String CREATE_SCHEMA = "CREATE SCHEMA ";
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String LAEGD = "CREATE TABLE LAEGD ( AMT CHAR(32) NOT NULL, AAR INT NOT NULL, "
			+ "LITRA CHAR(1) NOT NULL, LAEGDNR INT NOT NULL, GLAAR INT, GLLITRA CHAR(1), "
			+ "RULLETYPE CHAR(32), SOGN CHAR(32), LAEGDID INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY )";
	private static final String LAEGDIX = "CREATE UNIQUE INDEX LAEGDIX ON LAEGD ( AMT ASC, AAR ASC, "
			+ "LITRA ASC, LAEGDNR ASC )";
	private static final String RULLE = "CREATE TABLE RULLE ( LAEGDID INT NOT NULL, LOEBENR INT NOT NULL, "
			+ "GLLAEGDID INT, GLLOEBENR INT, FADER CHAR(32), SOEN CHAR(32), FOEDESTED CHAR(32), ALDER INT, "
			+ "STOERRELSEITOMMER DECIMAL(5,2), OPHOLD CHAR(32), ANMAERKNINGER CHAR(64), FOEDT DATE, "
			+ "GEDCOMID CHAR(32), NAVN CHAR(64), FADERFON CHAR(32), SOENFON CHAR(32) )";
	private static final String LAEGD_FK = "ALTER TABLE RULLE ADD CONSTRAINT LAEGD_FK FOREIGN KEY (LAEGDID) REFERENCES LAEGD(LAEGDID)";
	private static final String RULLE_IX = "CREATE INDEX LAEGDID_IX ON RULLE(LAEGDID)";
	private static final String LAEGDID_IX = "CREATE UNIQUE INDEX RULLEIX ON RULLE ( LAEGDID ASC, LOEBENR ASC )";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager
					.getConnection("jdbc:derby:" + props.getProperty("milrollPath") + ";create=true");
			PreparedStatement statement = conn.prepareStatement(CREATE_SCHEMA + props.getProperty("milrollSchema"));
			statement.execute();

			statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("milrollSchema"));
			statement.execute();

			statement = conn.prepareStatement(LAEGD);
			statement.execute();

			statement = conn.prepareStatement(LAEGDIX);
			statement.execute();

			statement = conn.prepareStatement(RULLE);
			statement.execute();

			statement = conn.prepareStatement(LAEGD_FK);
			statement.execute();

			statement = conn.prepareStatement(RULLE_IX);
			statement.execute();

			statement = conn.prepareStatement(LAEGDID_IX);
			statement.execute();

			return "Lægdsrulletabellerne er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

}
