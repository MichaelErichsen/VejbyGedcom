package net.myerichsen.archivesearcher.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import net.myerichsen.archivesearcher.models.CensusModel;
import net.myerichsen.archivesearcher.models.KipTextEntry;
import net.myerichsen.archivesearcher.views.ArchiveSearcher;

/**
 * This program needs a kipdata.txt and a set of KIP csv files as input.
 * <p>
 * It loads all KIP files into a Derby database table
 *
 * @author Michael Erichsen
 * @version 20. jun. 2023
 */
public class CensusLoader {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_COUNT = "SELECT COUNT(*) AS COUNT FROM CENSUS WHERE KIPNR = ?";
	private static PreparedStatement statement;
	private static int counter = 0;

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public static String main(String[] args, ArchiveSearcher as) {
		final CensusLoader censusLoader = new CensusLoader();

		try {
			censusLoader.execute(args, as);

		} catch (final Exception e) {

			e.printStackTrace();
			return e.getMessage();
		}

		return "Folketællinger er indlæst. " + counter + " rækker indsat i " + args[3] + ".CENSUS";
	}

	private Connection conn;

	/**
	 * Connect to the Derby database
	 *
	 * @param args
	 *
	 * @throws SQLException
	 *
	 */
	private void connectToDB(String[] args, ArchiveSearcher as) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[2];
		try {
			DriverManager.getConnection(dbURL + ";shutdown=true");
		} catch (final SQLException e) {
			// Shutdown message is expected
			Display.getDefault().asyncExec(() -> as.setMessage(e.getMessage()));
		}
		conn = DriverManager.getConnection(dbURL);
		conn.setAutoCommit(false);
		statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, args[3]);
		statement.execute();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @param as
	 * @throws Exception
	 */
	private void execute(String[] args, ArchiveSearcher as) throws Exception {

		// Connect to Derby
		connectToDB(args, as);

		// Find all census csv files from the index file
		Display.getDefault().asyncExec(() -> as.setMessage("Finder alle folketællinger i " + args[1] + "/" + args[0]));
		final List<KipTextEntry> lkte = parseKipText(args);

		// Remove header line
		lkte.remove(0);

		// Parse and store contents of each census file in a single table
		Display.getDefault().asyncExec(() -> as.setMessage("Hver folketællingsfil behandles"));
		for (final KipTextEntry kipTextEntry : lkte) {
			if (!kipTextEntry.getAar().equals("1771")) {
				parseCensusFile(args, kipTextEntry, as);
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
	 * @param as
	 * @throws Exception
	 */
	private void parseCensusFile(String[] args, KipTextEntry kipTextEntry, ArchiveSearcher as) throws Exception {
		statement = conn.prepareStatement(SELECT_COUNT);
		statement.setString(1, kipTextEntry.getKipNr());
		final ResultSet rs = statement.executeQuery();

		int count = 0;

		// Skip if already loaded
		if (rs.next()) {
			count = rs.getInt("COUNT");
			if (count > 0) {
				return;
			}
		}

		statement.getConnection().commit();

		Display.getDefault().asyncExec(() -> as.setMessage("Behandler " + kipTextEntry.getAar() + ", "
				+ kipTextEntry.getAmt() + ", " + kipTextEntry.getHerred() + ", " + kipTextEntry.getSogn()));
		final List<String> censusFileLines = getCensusFileLines(args[1], kipTextEntry.getKipNr());
		CensusModel ci;
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

			ci = new CensusModel();

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
			try {
				ci.setStilling_i_husstanden(fields[Stilling_i_husstanden]);
			} catch (final Exception e12) {
			}
			try {
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

			try {
				ci.insertIntoDb(statement.getConnection());
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
