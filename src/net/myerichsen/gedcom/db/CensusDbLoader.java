package net.myerichsen.gedcom.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This program needs a kipdata.txt and a set of KIP csv files as input.
 * <p>
 * It loads all KIP files into a Derby database table
 *
 * @author Michael Erichsen
 * @version 27. feb. 2023
 */
public class CensusDbLoader {
	private static final String DROP_INDEX = "DROP INDEX VEJBY.CENSUS_UI;";
	private static final String CLEAR_TABLE = "DELETE FROM VEJBY.CENSUS";
	private static final String DROP_PK = "ALTER TABLE VEJBY.CENSUS DROP CONSTRAINT CENSUS_PK";
	private static final String DROP_TABLE = "DROP TABLE VEJBY.CENSUS";
	private static final String CREATE_TABLE = "CREATE TABLE VEJBY.CENSUS ( KIPNR CHAR(8) NOT NULL, "
			+ "LOEBENR CHAR(8) NOT NULL, AMT VARCHAR(256), HERRED VARCHAR(256), "
			+ "SOGN VARCHAR(256), KILDESTEDNAVN VARCHAR(256), HUSSTANDS_FAMILIENR VARCHAR(256), "
			+ "MATR_NR_ADRESSE VARCHAR(256), KILDENAVN VARCHAR(256), FONNAVN "
			+ "VARCHAR(256), KOEN VARCHAR(256), ALDER VARCHAR(256), CIVILSTAND "
			+ "VARCHAR(256), KILDEERHVERV VARCHAR(4096), STILLING_I_HUSSTANDEN "
			+ "VARCHAR(256), KILDEFOEDESTED VARCHAR(256), FOEDT_KILDEDATO VARCHAR(256), "
			+ "FOEDEAAR INTEGER, ADRESSE VARCHAR(256), MATRIKEL VARCHAR(512), GADE_NR "
			+ "VARCHAR(256), FTAAR INTEGER, KILDEHENVISNING VARCHAR(256), KILDEKOMMENTAR VARCHAR(512) )";
	private static final String CREATE_PK = "ALTER TABLE VEJBY.CENSUS "
			+ "ADD CONSTRAINT CENSUS_PK PRIMARY KEY (KIPNR, LOEBENR)";
	private static final String CREATE_INDEX = "CREATE UNIQUE INDEX VEJBY.CENSUS_UI "
			+ "ON VEJBY.CENSUS (KIPNR ASC, LOEBENR ASC)";
	private static Logger logger;
	private static Statement statement;
	private static int counter = 0;
	private static CensusIndividual lastCi = null;

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: CensusDbLoader kiptextfilename csvfiledirectory derbydatabasepath");
			System.exit(4);
		}

		logger = Logger.getLogger("CensusDbLoader");

		final CensusDbLoader censusDbLoader = new CensusDbLoader();

		try {
			// CensusDbLoader.newTable();
			// System.exit(0);
			censusDbLoader.execute(args);

			logger.info(counter + " rækker indsat i VEJBY.CENSUS");
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			logger.info(lastCi.toString());
			e.printStackTrace();
		}

	}

	/**
	 * Drop and create the table and primary key
	 */
	public static void newTable() {
		final String dbURL = "jdbc:derby:c:\\Users\\michael\\VEJBYDB";
		try {
			final Connection conn = DriverManager.getConnection(dbURL);
			statement = conn.createStatement();
		} catch (final SQLException e) {
			e.printStackTrace();
			return;
		}
		try {
			statement.execute(DROP_PK);
			statement.execute(DROP_INDEX);
			statement.execute(DROP_TABLE);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		try {
			statement.execute(CREATE_TABLE);
			statement.execute(CREATE_INDEX);
			statement.execute(CREATE_PK);
			statement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param args
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[2];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		statement = conn.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		// Connect to Derby
		connectToDB(args);

		// Clear table
		final String query = CLEAR_TABLE;
		statement.execute(query);

		// Find all census csv files from the index file
		final List<KipTextEntry> lkte = parseKipText(args);

		// Remove header line
		lkte.remove(0);

		// Parse and store each census file
		for (final KipTextEntry kipTextEntry : lkte) {
			parseCensusFile(args, kipTextEntry);
		}

	}

	/**
	 * Find the column name in the header line
	 *
	 * @param key
	 * @param columnNames
	 * @return
	 */
	private int findIndex(String key, String[] columnNames) {

		for (int i = 0; i < columnNames.length; i++) {
			if (key.equals(columnNames[i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Get all lines in a census file
	 *
	 * @param csvfiledirectory
	 * @param kipNr
	 * @return
	 * @throws IOException
	 */
	private List<String> getCensusFileLines(String csvfiledirectory, String kipNr) throws IOException {
		final List<String> kipLines = new ArrayList<>();
		String line;

		final File file = new File(csvfiledirectory + "/" + kipNr + ".csv");
		final BufferedReader br = new BufferedReader(new FileReader(file));

		while ((line = br.readLine()) != null) {
			kipLines.add(line);
		}

		br.close();
		return kipLines;
	}

	/**
	 * Parse a census file and store into a Derby table
	 *
	 * @param args
	 * @param kipTextEntry
	 * @throws Exception
	 */
	private void parseCensusFile(String[] args, KipTextEntry kipTextEntry) throws Exception {
		logger.info("Parsing " + kipTextEntry.getKipNr());

		final List<String> censusFileLines = getCensusFileLines(args[1], kipTextEntry.getKipNr());
		CensusIndividual ci;
		String[] fields;

		final String[] columnNames = censusFileLines.get(0).split(";");

		final int kipNr = findIndex("KIPnr", columnNames);
		final int Loebenr = findIndex("Løbenr", columnNames);
		final int Kildestednavn = findIndex("Kildestednavn", columnNames);
		final int Husstands_familienr = findIndex("Husstands/familienr.", columnNames);
		final int Matr_nr_Adresse = findIndex("Matr.nr./Adresse", columnNames);
		final int Kildenavn = findIndex("Kildenavn", columnNames);
		final int Koen = findIndex("Køn", columnNames);
		final int Alder = findIndex("Alder", columnNames);
		final int Civilstand = findIndex("Civilstand", columnNames);
		final int Kildeerhverv = findIndex("Kildeerhverv", columnNames);
		final int Stilling_i_husstanden = findIndex("Stilling_i_husstanden", columnNames);
		final int Kildefoedested = findIndex("Kildefødested", columnNames);
		final int Foedt_kildedato = findIndex("Født kildedato", columnNames);
		final int Foedeaar = findIndex("Fødeår", columnNames);
		final int Adresse = findIndex("Adresse", columnNames);
		final int Matrikel = findIndex("Matrikel", columnNames);
		final int Gade_nr = findIndex("Gade nr.", columnNames);
		final int FTaar = findIndex("FTår", columnNames);
		final int Kildehenvisning = findIndex("Kildehenvisning", columnNames);
		final int Kildekommentar = findIndex("Kildekommentar", columnNames);

		// Remove header line
		censusFileLines.remove(0);

		for (final String line : censusFileLines) {
			fields = line.split(";");

			ci = new CensusIndividual();
			try {
				ci.setKIPnr(fields[kipNr]);
				ci.setLoebenr(fields[Loebenr]);
				ci.setKildestednavn(fields[Kildestednavn]);
				ci.setAmt(kipTextEntry.getAmt());
				ci.setHerred(kipTextEntry.getHerred());
				ci.setSogn(kipTextEntry.getSogn());
				ci.setHusstands_familienr(fields[Husstands_familienr]);
				ci.setMatr_nr_Adresse(fields[Matr_nr_Adresse]);
				ci.setKildenavn(fields[Kildenavn]);
				ci.setKoen(fields[Koen]);
				ci.setAlder(fields[Alder]);
				ci.setCivilstand(fields[Civilstand]);
				ci.setKildeerhverv(fields[Kildeerhverv]);
				ci.setStilling_i_husstanden(fields[Stilling_i_husstanden]);
				ci.setKildefoedested(fields[Kildefoedested]);
				ci.setFoedt_kildedato(fields[Foedt_kildedato]);
				ci.setFoedeaar(fields[Foedeaar]);
				ci.setAdresse(fields[Adresse]);
				ci.setMatrikel(fields[Matrikel]);
				ci.setGade_nr(fields[Gade_nr]);
				ci.setFTaar(fields[FTaar]);
				ci.setKildehenvisning(fields[Kildehenvisning]);
				ci.setKildekommentar(fields[Kildekommentar]);
			} catch (final Exception e) {
				// Ignore
			}

			lastCi = ci;

			try {
				ci.insertIntoDb(statement);
			} catch (final SQLException e) {
				// Handle duplicates
				if (!e.getSQLState().equals("23505")) {
					throw new Exception("SQL Error Code: " + e.getErrorCode() + ", SQL State: " + e.getSQLState());
				}
			}

			counter++;
		}
	}

	/**
	 * Parse all lines in the kipdata text file. Read each line into an object
	 * and return them in a list.
	 *
	 * @param args
	 * @return
	 * @throws IOException
	 */
	private List<KipTextEntry> parseKipText(String[] args) throws IOException {
		final List<KipTextEntry> kipLines = new ArrayList<>();
		final File file = new File(args[1] + "/" + args[0]);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		KipTextEntry kte;

		while ((line = br.readLine()) != null) {
			kte = new KipTextEntry(line);
			kipLines.add(kte);
		}

		br.close();

		return kipLines;
	}

}
