package net.myerichsen.gedcom.db.tablecreators;

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
public class CphTableCreator {
	/**
	 *
	 */
	private static final String SET_SCHEMA = "SET SCHEMA =  ?";
	private static final String CRA = "CREATE TABLE BURIAL_PERSON_COMPLETE ( ID INTEGER NOT NULL, NUMBER INTEGER DEFAULT NULL, "
			+ "FIRSTNAMES CHAR(100) DEFAULT 'NULL', LASTNAME CHAR(100) DEFAULT 'NULL', BIRTHNAME CHAR(100) DEFAULT 'NULL', "
			+ "AGEYEARS INTEGER DEFAULT NULL, AGEMONTH DECIMAL(4 , 2) DEFAULT NULL, AGEWEEKS DECIMAL(4 , 2) DEFAULT NULL, "
			+ "AGEDAYS DECIMAL(4 , 2) DEFAULT NULL, AGEHOURS INTEGER DEFAULT NULL, DATEOFBIRTH DATE DEFAULT NULL, "
			+ "DATEOFDEATH DATE DEFAULT NULL, YEAROFBIRTH INTEGER DEFAULT NULL, DEATHPLACE VARCHAR(4096) DEFAULT 'NULL', "
			+ "CIVILSTATUS CHAR(25) DEFAULT 'NULL', ADRESSOUTSIDECPH VARCHAR(255) DEFAULT 'NULL', SEX CHAR(10) DEFAULT 'NULL', "
			+ "COMMENT VARCHAR(16384), CEMETARY CHAR(100) DEFAULT 'NULL', CHAPEL CHAR(100) DEFAULT 'NULL', "
			+ "PARISH CHAR(100) DEFAULT 'NULL', STREET CHAR(75) DEFAULT 'NULL', HOOD CHAR(25) DEFAULT 'NULL', "
			+ "STREET_NUMBER INTEGER DEFAULT NULL, LETTER CHAR(1) DEFAULT 'NULL', FLOOR CHAR(15) DEFAULT 'NULL', "
			+ "INSTITUTION CHAR(100) DEFAULT 'NULL', INSTITUTION_STREET CHAR(75) DEFAULT 'NULL', INSTITUTION_HOOD CHAR(25) DEFAULT 'NULL', "
			+ "INSTITUTION_STREET_NUMBER INTEGER DEFAULT NULL, OCCUPATIONS VARCHAR(4096) DEFAULT 'NULL', "
			+ "OCCUPATION_RELATION_TYPES VARCHAR(4096) DEFAULT 'NULL', DEATHCAUSES VARCHAR(4096) DEFAULT 'NULL', "
			+ "DEATHCAUSES_DANISH VARCHAR(4096) DEFAULT 'NULL', PHONNAME CHAR(64) )";
	private static final String CRB = "CREATE TABLE POLICE_POSITION ( ID INTEGER NOT NULL, PERSON_ID INTEGER NOT NULL, "
			+ "POSITION_DANISH VARCHAR(100) DEFAULT 'NULL', POSITION_ENGLISH VARCHAR(100) DEFAULT 'NULL', "
			+ "ISCO_MAJOR_GROUP CHAR(5) DEFAULT 'NULL', ISCO_SUBMAJOR_GROUP CHAR(5) DEFAULT 'NULL', "
			+ "ISCO_MINOR_GROUP CHAR(5) DEFAULT 'NULL', ISCO_UNIT CHAR(5) DEFAULT 'NULL' )";
	private static final String CRC = "CREATE TABLE POLICE_PERSON ( ID INTEGER NOT NULL, FIRSTNAMES CHAR(80) DEFAULT 'NULL', "
			+ "LASTNAME CHAR(50) DEFAULT 'NULL', MAIDENNAME CHAR(50) DEFAULT 'NULL', MARRIED null, TYPE CHAR(6) DEFAULT 'NULL', "
			+ "GENDER INTEGER DEFAULT NULL, BIRTHPLACE CHAR(100) DEFAULT 'NULL', BIRTHDAY INTEGER DEFAULT NULL, "
			+ "BIRTHMONTH INTEGER DEFAULT NULL, BIRTHYEAR INTEGER DEFAULT NULL, DEATHDAY INTEGER DEFAULT NULL, "
			+ "DEATHMONTH INTEGER DEFAULT NULL, DEATHYEAR INTEGER DEFAULT NULL, PHONNAME CHAR(64) DEFAULT 'NULL' )";
	private static final String CRD = "CREATE TABLE POLICE_ADDRESS ( ID INTEGER NOT NULL, PERSON_ID INTEGER NOT NULL, "
			+ "STREET VARCHAR(65) DEFAULT 'NULL', NUMBER VARCHAR(10) DEFAULT 'NULL', LETTER VARCHAR(3) DEFAULT 'NULL', "
			+ "FLOOR VARCHAR(10) DEFAULT 'NULL', SIDE VARCHAR(3) DEFAULT 'NULL', PLACE VARCHAR(90) DEFAULT 'NULL', "
			+ "HOST VARCHAR(255) DEFAULT 'NULL', LATITUDE DECIMAL(18 , 12) DEFAULT NULL, LONGITUDE DECIMAL(18 , 12) DEFAULT NULL, "
			+ "DAY INTEGER DEFAULT NULL, MONTH INTEGER DEFAULT NULL, XYEAR INTEGER DEFAULT NULL, FULL_ADDRESS VARCHAR(512) DEFAULT 'NULL' )";
	private static final String CRE = "CREATE UNIQUE INDEX SQL230130210151780 ON POLICE_ADDRESS (ID ASC)";
	private static final String CRF = "CREATE UNIQUE INDEX SQL230130190752240 ON BURIAL_PERSON_COMPLETE (ID ASC)";
	private static final String CRG = "CREATE UNIQUE INDEX SQL230129182253090 ON POLICE_POSITION (ID ASC)";
	private static final String CRH = "CREATE INDEX SQL230131235849940 ON POLICE_POSITION (PERSON_ID ASC)";
	private static final String CRI = "CREATE INDEX SQL230131235918140 ON POLICE_ADDRESS (PERSON_ID ASC)";
	private static final String CRJ = "CREATE UNIQUE INDEX SQL230129182252820 ON POLICE_PERSON (ID ASC)";
	private static final String CRK = "CREATE UNIQUE INDEX SQL230130192800720 ON BURIAL_PERSON_COMPLETE (ID ASC)";
	private static final String CRL = "ALTER TABLE POLICE_ADDRESS ADD CONSTRAINT SQL230129182252700 PRIMARY KEY (ID)";
	private static final String CRM = "ALTER TABLE BURIAL_PERSON_COMPLETE ADD CONSTRAINT SQL230129182252320 PRIMARY KEY (ID)";
	private static final String CRN = "ALTER TABLE POLICE_POSITION ADD CONSTRAINT SQL230129182253090 PRIMARY KEY (ID)";
	private static final String CRO = "ALTER TABLE POLICE_PERSON ADD CONSTRAINT SQL230129182252820 PRIMARY KEY (ID)";
	private static final String CRP = "ALTER TABLE POLICE_POSITION ADD CONSTRAINT POLICE_P_POLICE_FK FOREIGN KEY (PERSON_ID) REFERENCES POLICE_PERSON (ID) ON DELETE CASCADE";
	private static final String CRQ = "ALTER TABLE POLICE_ADDRESS ADD CONSTRAINT POLICE__POLICE_FK FOREIGN KEY (PERSON_ID) REFERENCES POLICE_PERSON (ID) ON DELETE CASCADE";
	private static final String CRR = "CREATE INDEX BURIAL_PHON_IX ON BURIAL_PERSON_COMPLETE (PHONNAME ASC)";
	private static final String CRS = "CREATE INDEX POLICE_PHON_IX ON POLICE_PERSON (PHONNAME ASC)";

	public static String createTables(Properties props) {
		try {
			final Connection conn = DriverManager
					.getConnection("jdbc:derby:" + props.getProperty("cphDbPath") + ";create=true");
			PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
			statement.setString(1, props.getProperty("cphSchema"));
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
			return "De Københavnske tabeller er dannet";
		} catch (final SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
