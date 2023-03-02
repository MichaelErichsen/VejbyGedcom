package net.myerichsen.gedcom.db;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Find all census entries that match a given phonetic name
 *
 * Find phonetic name and birth or christening from the INDIVIDUAL Derby table.
 *
 * For each individual in the CENSUS Derby table within the life span of the
 * individual.
 *
 * If the name matches phonetically and the birth or christening date are within
 * two years of the given age then output in csv format
 *
 * @author Michael Erichsen
 * @version 03. mar. 2023
 *
 */
public class CensusFinderDbByName {
	private static Logger logger;
	private static BufferedWriter bw = null;
	private static int counter = 0;

	private static final String header = "FTaar;KIPnr;Loebenr;Kildestednavn;Amt;Herred;Sogn;"
			+ "Husstands_familienr;Matr_nr_Adresse;Kildenavn;Fonnavn;Koen;Alder;Civilstand;"
			+ "Kildeerhverv;Stilling_i_husstanden;Kildefoedested;Foedt_kildedato;Foedeaar;"
			+ "Adresse;Matrikel;Gade_nr;Kildehenvisning;Kildekommentar\n";

	private static final String SELECT = "SELECT * FROM VEJBY.CENSUS WHERE FONNAVN = '%s'";

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: CensusFinderDbByName name derbydatabasepath outputdirectory");
			System.exit(4);
		}

		try {
			new CensusFinderDbByName(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * C:tor
	 *
	 * @param args
	 * @throws Exception
	 */
	public CensusFinderDbByName(String[] args) throws Exception {
		logger = Logger.getLogger("CensusFinderDbByName");

		execute(args);
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		return conn.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		final Statement statement = connectToDB(args);

		String phonName = new Fonkod().generateKey(args[0]);
		final ResultSet rs = statement.executeQuery(String.format(SELECT, phonName));

		List<CensusIndividual> cil = null;

		while (rs.next()) {
			cil = CensusIndividual.getFromDb(rs);
		}

		statement.close();

		if ((cil != null) && (cil.size() > 0)) {
			writeOutput(cil, args);
		}

	}

	/**
	 * @param cil
	 * @param individual
	 * @throws IOException
	 */
	private void writeOutput(List<CensusIndividual> cil, String[] args) throws IOException {
		String outName = args[2] + "/" + args[0] + "db_census.csv";

		for (final CensusIndividual ci : cil) {
			if (counter == 0) {
				bw = new BufferedWriter(new FileWriter(new File(outName)));
				bw.write(header);
			}

			bw.write(ci.toString());
			counter++;
		}

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of census data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}
	}
}
