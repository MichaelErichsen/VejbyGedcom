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
 * Find all census entries that match a given individual ID.
 *
 * Find name and birth or christening from the INDIVIDUAL Derby table.
 *
 * For each individual in the CENSUS Derby table within the life span of the
 * individual.
 *
 * If the name matches completely and the birth or christening date are within
 * two years of the given age then output in csv format
 *
 *
 * @author Michael Erichsen
 * @version 27. feb. 2023
 *
 */
public class CensusFinderDb {
	private static Logger logger;
	private static BufferedWriter bw = null;
	private static int counter = 0;

	private static final String header = "KIPnr;Loebenr;Kildestednavn;Amt;Herred;Sogn;"
			+ "Husstands_familienr;Matr_nr_Adresse;Kildenavn;Fonnavn;Koen;Alder;Civilstand;"
			+ "Kildeerhverv;Stilling_i_husstanden;Kildefoedested;Foedt_kildedato;Foedeaar;"
			+ "Adresse;Matrikel;Gade_nr;FTaar;Kildehenvisning;Kildekommentar\n";

	private static final String SELECT = "SELECT * FROM VEJBY.CENSUS "
			+ "WHERE KILDENAVN = '%s' AND FTAAR >= %s AND FTAAR <= %s";

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: CensusFinderDb individualId derbydatabasepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("CensusFinderDb");

		try {
			new CensusFinderDb(args);
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
	public CensusFinderDb(String[] args) throws Exception {
		logger = Logger.getLogger("CensusFinder");
		execute(args);
	}

	/**
	 * @param ci
	 * @param location
	 * @return
	 */
	private boolean compareLocation(CensusIndividual ci, String location) {

		final String[] locationParts = location.split(",");

		for (final String part : locationParts) {
			if (ci.getAmt().contains(part.trim())) {
				return true;
			}
			if (ci.getHerred().contains(part.trim())) {
				return true;
			}
			if (ci.getSogn().contains(part.trim())) {
				return true;
			}
			if (ci.getKildestednavn().contains(location.trim())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + args[1];
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		logger.fine("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		List<CensusIndividual> cil = null;
		final Statement statement = connectToDB(args);

		// Get data for the individual
		final DBIndividual individual = new DBIndividual(statement, args[0]);

		// Select records for the individual within its lifetime
		logger.info("Searching for censuses for ID " + individual.getId() + ", " + individual.getName() + ", born "
				+ individual.getBirthYear() + " in " + individual.getBirthPlace());

		final String query = String.format(SELECT, individual.getName(), individual.getBirthYear(),
				individual.getDeathYear());
		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			cil = CensusIndividual.getFromDb(rs);
		}

		statement.close();

		if ((cil != null) && (cil.size() > 0)) {
			writeOutput(cil, individual, args[2] + "/" + individual.getName() + "_census.csv");
		}
		if (counter == 0) {
			logger.info("No census records found");
		}

	}

	/**
	 * @param cil
	 * @param individual
	 * @throws IOException
	 */
	private void writeOutput(List<CensusIndividual> cil, final DBIndividual individual, String outName)
			throws IOException {
		int foedeAar;
		int diff;

		bw = new BufferedWriter(new FileWriter(outName));
		bw.write(header);

		final String location = individual.getBirthPlace().trim();

		for (final CensusIndividual ci : cil) {
			if (compareLocation(ci, location)) {
				foedeAar = ci.getFoedeaar();

				if (foedeAar == 0) {
					if (ci.getAlder().equals("")) {
						foedeAar = Integer.parseInt(ci.getFoedt_kildedato().substring(0, 4));
					} else {
						foedeAar = ci.getFTaar() - Integer.parseInt(ci.getAlder());
					}
				}

				diff = individual.getBirthYear() - foedeAar;

				if ((diff > -2) && (diff < 2)) {
					bw.write(ci.toString());
					counter++;
				}
			}
		}

		bw.flush();
		bw.close();
		logger.info(counter + " lines of census data written to " + outName);

		if (counter > 0) {
			Desktop.getDesktop().open(new File(outName));
		}
	}
}
