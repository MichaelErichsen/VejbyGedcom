package net.myerichsen.gedcom.db.tablecreators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class to create Derby tables for the program
 *
 * @author Michael Erichsen
 * @version 25. apr. 2023
 *
 */
public class GedcomTableCreator {
	/**
	 *
	 */
	private static final String SET_SCHEMA2 = "SET SCHEMA = ?";
	/**
	 *
	 */
	private static final String SET_SCHEMA = SET_SCHEMA2;
	private static String TABLE_EVENT = "CREATE TABLE EVENT ("
			+ " ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, TYPE CHAR(12),"
			+ " SUBTYPE VARCHAR(32), DATE DATE, INDIVIDUAL CHAR(12), FAMILY CHAR(12),"
			+ " PLACE VARCHAR(256), NOTE VARCHAR(16000), SOURCEDETAIL VARCHAR(16000) )";
	private static String TABLE_INDIVIDUAL = "CREATE TABLE INDIVIDUAL ( ID CHAR(12) NOT NULL, "
			+ " GIVENNAME CHAR(64), SURNAME CHAR(64), SEX CHAR(1), "
			+ " FAMC CHAR(12), PHONNAME CHAR(64), BIRTHDATE DATE, "
			+ " BIRTHPLACE VARCHAR(256), DEATHDATE DATE, DEATHPLACE VARCHAR(256), PARENTS VARCHAR(256) )";
	private static String TABLE_PARENTS = "CREATE TABLE PARENTS ( INDIVIDUALKEY CHAR(12) NOT NULL, "
			+ " BIRTHYEAR INT, NAME VARCHAR(128), PARENTS VARCHAR(256), "
			+ " FATHERPHONETIC CHAR(64), MOTHERPHONETIC CHAR(64), PLACE VARCHAR(256) )";

	private static final String CRA = "CREATE INDEX EVENT_TYPE ON EVENT (TYPE ASC)";
	private static final String CRB = "CREATE INDEX EVENT_SUBTYPE ON EVENT (SUBTYPE ASC)";
	private static final String CRC = "CREATE INDEX IND_PHONNAME ON INDIVIDUAL (PHONNAME ASC)";
	private static final String CRD = "CREATE INDEX SQL230124103427890 ON INDIVIDUAL (FAMC ASC)";
	private static final String CRE = "CREATE UNIQUE INDEX SQL230124103427450 ON EVENT (ID ASC)";
	private static final String CRF = "CREATE UNIQUE INDEX SQL230124103426740 ON INDIVIDUAL (ID ASC)";
	private static final String CRG = "CREATE UNIQUE INDEX CENSUS_UI ON CENSUS (KIPNR ASC, LOEBENR ASC)";
	private static final String CRH = "CREATE UNIQUE INDEX SQL230124103427660 ON FAMILY (ID ASC)";
	private static final String CRI = "CREATE INDEX SQL230124103428330 ON EVENT (INDIVIDUAL ASC)";
	private static final String CRJ = "CREATE INDEX SQL230124103429560 ON FAMILY (HUSBAND ASC)";
	private static final String CRK = "CREATE INDEX SQL230124103429780 ON EVENT (FAMILY ASC)";
	private static final String CRL = "CREATE INDEX SQL230124103428540 ON FAMILY (WIFE ASC)";
	private static final String CRM = "ALTER TABLE EVENT ADD CONSTRAINT Event_PK PRIMARY KEY (ID)";
	private static final String CRN = "ALTER TABLE INDIVIDUAL ADD CONSTRAINT Individual_PK PRIMARY KEY (ID)";
	private static final String CRO = "ALTER TABLE FAMILY ADD CONSTRAINT Family_PK PRIMARY KEY (ID)";
	private static final String CRP = "ALTER TABLE FAMILY ADD CONSTRAINT Famil_Individua_F1 FOREIGN KEY (HUSBAND) REFERENCES INDIVIDUAL (ID) ON DELETE CASCADE";
	private static final String CRQ = "ALTER TABLE INDIVIDUAL ADD CONSTRAINT INDIVIDUA_FAMIL_FK FOREIGN KEY (FAMC) REFERENCES FAMILY (ID) ON DELETE CASCADE";
	private static final String CRR = "ALTER TABLE EVENT ADD CONSTRAINT EVEN_INDIVIDUA_FK FOREIGN KEY (INDIVIDUAL) REFERENCES INDIVIDUAL (ID) ON DELETE CASCADE";
	private static final String CRS = "ALTER TABLE FAMILY ADD CONSTRAINT Famil_Individua_F2 FOREIGN KEY (WIFE) REFERENCES INDIVIDUAL (ID)ON DELETE CASCADE";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager
					.getConnection("jdbc:derby:" + props.getProperty("vejbyPath") + ";create=true");
			PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("vejbySchema"));
			statement.execute();
			statement = conn.prepareStatement(TABLE_EVENT);
			statement.execute();
			statement = conn.prepareStatement(TABLE_INDIVIDUAL);
			statement.execute();
			statement = conn.prepareStatement(TABLE_PARENTS);
			statement.execute();
			statement = conn.prepareStatement(CRA);
			statement.execute();
			statement = conn.prepareStatement(CRB);
			statement.execute();
			statement = conn.prepareStatement(CRC);
			statement.execute();
			statement = conn.prepareStatement(CRD);
			statement.execute();
			statement = conn.prepareStatement(CRE);
			statement.execute();
			statement = conn.prepareStatement(CRF);
			statement.execute();
			statement = conn.prepareStatement(CRG);
			statement.execute();
			statement = conn.prepareStatement(CRH);
			statement.execute();
			statement = conn.prepareStatement(CRI);
			statement.execute();
			statement = conn.prepareStatement(CRJ);
			statement.execute();
			statement = conn.prepareStatement(CRK);
			statement.execute();
			statement = conn.prepareStatement(CRL);
			statement.execute();
			statement = conn.prepareStatement(CRM);
			statement.execute();
			statement = conn.prepareStatement(CRN);
			statement.execute();
			statement = conn.prepareStatement(CRO);
			statement.execute();
			statement = conn.prepareStatement(CRP);
			statement.execute();
			statement = conn.prepareStatement(CRQ);
			statement.execute();
			statement = conn.prepareStatement(CRR);
			statement.execute();
			statement = conn.prepareStatement(CRS);
			statement.execute();
			return "GEDCOM-tabellerne er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
