package net.myerichsen.archivesearcher.tablecreators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Michael Erichsen
 * @version 25. apr. 2023
 *
 */
public class CensusTableCreator {
	/**
	 *
	 */
	private static final String CREATE_SCHEMA = "CREATE SCHEMA";
	private static final String SET_SCHEMA = "SET SCHEMA =  ?";
	private static final String TABLE_CENSUS = "CREATE TABLE CENSUS ( KIPNR CHAR(8) NOT NULL,"
			+ " LOEBENR INTEGER NOT NULL, AMT VARCHAR(256), HERRED VARCHAR(256),"
			+ " PARISH VARCHAR(256), KILDESTEDNAVN VARCHAR(256),"
			+ " HUSSTANDS_FAMILIENR VARCHAR(256), MATR_NR_ADRESSE VARCHAR(256),"
			+ " KILDENAVN VARCHAR(256), FONNAVN VARCHAR(256), KOEN VARCHAR(256),"
			+ " ALDER INTEGER, CIVILSTAND VARCHAR(256), KILDEERHVERV VARCHAR(4096),"
			+ " STILLING_I_HUSSTANDEN VARCHAR(256), KILDEFOEDESTED VARCHAR(256),"
			+ " FOEDT_KILDEDATO VARCHAR(256), FOEDEAAR INTEGER, ADRESSE VARCHAR(256),"
			+ " MATRIKEL VARCHAR(512), GADE_NR VARCHAR(256), FTAAR INTEGER,"
			+ " KILDEHENVISNING VARCHAR(256), KILDEKOMMENTAR VARCHAR(512) )";
	private static final String CR1 = "CREATE INDEX CENSUS_FTAAR ON CENSUS (FTAAR ASC)";
	private static final String CR2 = "CREATE INDEX CENSUS_FON_IX ON CENSUS (FONNAVN ASC)";
	private static final String CR3 = "CREATE UNIQUE INDEX CENSUS_UI ON CENSUS (KIPNR ASC, LOEBENR ASC)";
	private static final String CR4 = "ALTER TABLE CENSUS ADD CONSTRAINT CENSUS_PK PRIMARY KEY ( KIPNR, LOEBENR)";
	private static final String CR5 = "CREATE INDEX CENSUS_HUSH_IX ON CENSUS ( KIPNR ASC, HUSSTANDS_FAMILIENR ASC)";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager
					.getConnection("jdbc:derby:" + props.getProperty("censusPath") + ";create=true");
			PreparedStatement statement = conn.prepareStatement(CREATE_SCHEMA + props.getProperty("censusSchema"));
			statement.execute();
			statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("censusSchema"));
			statement.execute();
			statement = conn.prepareStatement(TABLE_CENSUS);
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
			return "Folketællingstabellerne er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

}
