package net.myerichsen.gedcom.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
 * @version 1. mar. 2023
 */
public class CensusDbLoader {
	// private static final String CLEAR_TABLE = "DELETE FROM VEJBY.CENSUS";
	private static final String SELECT_COUNT = "SELECT COUNT(*) AS COUNT FROM VEJBY.CENSUS WHERE KIPNR = '%s'";
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
			censusDbLoader.execute(args);

			logger.info(counter + " rækker indsat i VEJBY.CENSUS");
		} catch (final Exception e) {
			logger.severe(e.getMessage());

			if (lastCi != null) {
				logger.info(lastCi.toString());
			}

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
		conn.setAutoCommit(false);
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
		// statement.execute(CLEAR_TABLE);

		// Find all census csv files from the index file
		final List<KipTextEntry> lkte = parseKipText(args);

		// Remove header line
		lkte.remove(0);

		// Parse and store contens of each census file in a single table
		for (final KipTextEntry kipTextEntry : lkte) {
			if (!kipTextEntry.getAar().equals("1771")) {
				parseCensusFile(args, kipTextEntry);
			}
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

		final BufferedReader br = new BufferedReader(new FileReader(new File(csvfiledirectory + "/" + kipNr + ".csv")));

		while ((line = br.readLine()) != null) {
			kipLines.add(line);
		}

		br.close();
		return kipLines;
	}

	/**
	 * Parse a census file and store contents into a Derby table
	 *
	 * @param args
	 * @param kipTextEntry
	 * @throws Exception
	 */
	private void parseCensusFile(String[] args, KipTextEntry kipTextEntry) throws Exception {
		final String query = String.format(SELECT_COUNT, kipTextEntry.getKipNr());
		final ResultSet rs = statement.executeQuery(query);

		// Skip if already loaded
		if (rs.next()) {
			if (rs.getInt("COUNT") > 0) {
				logger.info("Skipping " + kipTextEntry.getKipNr());
				return;
			}
		}

		logger.info("Parsing " + kipTextEntry.getKipNr());
		statement.getConnection().commit();

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

			ci.setKIPnr(fields[kipNr]);
			try {
				ci.setLoebenr(fields[Loebenr]);
			} catch (final Exception e) {
			}
			try {
				ci.setKildestednavn(fields[Kildestednavn]);
			} catch (final Exception e1) {

			}
			try {
				ci.setAmt(kipTextEntry.getAmt());
			} catch (final Exception e2) {

			}
			try {
				ci.setHerred(kipTextEntry.getHerred());
			} catch (final Exception e3) {
			}
			try {
				ci.setSogn(kipTextEntry.getSogn());
			} catch (final Exception e4) {
			}
			try {
				ci.setHusstands_familienr(fields[Husstands_familienr]);
			} catch (final Exception e5) {
			}
			try {
				ci.setMatr_nr_Adresse(fields[Matr_nr_Adresse]);
			} catch (final Exception e6) {
			}
			try {
				ci.setKildenavn(fields[Kildenavn]);
			} catch (final Exception e7) {
			}
			try {
				ci.setKoen(fields[Koen]);
			} catch (final Exception e8) {
			}
			try {
				ci.setAlder(fields[Alder]);
			} catch (final Exception e9) {
			}
			try {
				ci.setCivilstand(fields[Civilstand]);
			} catch (final Exception e10) {

			}
			try {
				ci.setKildeerhverv(fields[Kildeerhverv]);
			} catch (final Exception e11) {
			}
			try

			{
				ci.setStilling_i_husstanden(fields[Stilling_i_husstanden]);
			} catch (final Exception e12) {
			}
			try

			{
				ci.setKildefoedested(fields[Kildefoedested]);
			} catch (final Exception e13) {
			}
			try {
				ci.setFoedt_kildedato(fields[Foedt_kildedato]);
			} catch (final Exception e14) {
			}
			try {
				ci.setFoedeaar(fields[Foedeaar]);
			} catch (final Exception e15) {
			}
			try {
				ci.setAdresse(fields[Adresse]);
			} catch (final Exception e16) {
			}
			try {
				ci.setMatrikel(fields[Matrikel]);
			} catch (final Exception e17) {
			}
			try {
				ci.setGade_nr(fields[Gade_nr]);
			} catch (final Exception e18) {

			}
			try {
				ci.setFTaar(fields[FTaar]);
			} catch (final Exception e19) {

			}
			try {
				ci.setKildehenvisning(fields[Kildehenvisning]);
			} catch (final Exception e20) {

			}
			try {
				ci.setKildekommentar(fields[Kildekommentar]);
			} catch (final Exception e21) {

			}

			lastCi = ci;

			try {
				ci.insertIntoDb(statement);
			} catch (final SQLException e30) {
				// Handle duplicates
				if (!e30.getSQLState().equals("23505")) {
					throw new Exception("sql Error Code: " + e30.getErrorCode() + ", sql State: " + e30.getSQLState());
				}
			}

			counter++;
		}
	}

	/**
	 * Parse all lines in the kipdata text file. Read each line into an object and
	 * return them all in a list.
	 *
	 * @param args
	 * @return
	 * @throws IOException
	 */
	private List<KipTextEntry> parseKipText(String[] args) throws IOException {
		final List<KipTextEntry> kipLines = new ArrayList<>();
		final BufferedReader br = new BufferedReader(new FileReader(new File(args[1] + "/" + args[0])));
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
