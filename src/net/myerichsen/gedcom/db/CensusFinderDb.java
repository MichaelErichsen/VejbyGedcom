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
	private static String outName = "";
	private static BufferedWriter bw = null;
	private static int counter = 0;

	private static final String header = "KIPnr;Loebenr;Kildestednavn;Husstands_familienr;"
			+ "Matr_nr_Adresse;Kildenavn;Fonnavn;Koen;Alder;Civilstand;Kildeerhverv;"
			+ "Stilling_i_husstanden;Kildefoedested;Foedt_kildedato;Foedeaar;Adresse;"
			+ "Matrikel;Gade_nr;FTaar;Kildehenvisning;Kildekommentar\n";

	private static final String SELECT = "SELECT * FROM VEJBY.CENSUS "
			+ "WHERE KILDENAVN = '%s' AND FTAAR >= %d AND FTAAR <= %d";

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
	 * Find part of individual's birth place in the census line
	 *
	 * @param needle
	 * @param haystack
	 * @return
	 */
	// private boolean compareBirthPlace(String needle, String haystack) {
	// final String h = haystack.toLowerCase();
	// String[] split;
	// try {
	// split = needle.toLowerCase().split(",");
	// } catch (final Exception e) {
	// return true;
	// }
	//
	// for (final String element : split) {
	// if (h.contains(element.trim())) {
	// return true;
	// }
	// }
	//
	// return false;
	//
	// }

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
		final String birthYear = Integer.toString(individual.getBirthYear());
		final String deathYear = Integer.toString(individual.getDeathYear());

		logger.info("Searching for censuses for ID " + individual.getId() + ", " + individual.getName() + ", born "
				+ birthYear + " in " + individual.getBirthPlace());

		final String query = String.format(SELECT, args[0], birthYear, deathYear);
		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			cil = CensusIndividual.getFromDb(rs);
		}

		statement.close();

		// TODO Compare birth year

		if (cil.size() > 0) {
			bw = new BufferedWriter(new FileWriter(outName));
			bw.write(header);

			String location = individual.getBirthPlace().trim();

			for (final CensusIndividual ci : cil) {
				if ((ci.getAmt().contains(location)) || (ci.getHerred().contains(location))
						|| (ci.getSogn().contains(location)) || (ci.getKildestednavn().contains(location))) {
					bw.write(ci.toString());
					counter++;
				}
			}

			bw.flush();
			bw.close();
			logger.info(counter + " lines of census data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}

	}

	/**
	 * Extract data from the census csv file into the output csv file
	 *
	 * @param individual
	 * @param csvFileName
	 * @param location
	 * @param outfilepath
	 * @param bw
	 * @param ftYear
	 * @throws IOException
	 */
	// private void processCsvFile(DBIndividual individual, String csvFileName,
	// String location, String outfilepath,
	// int ftYear) throws IOException {
	// final BufferedReader br = new BufferedReader(new FileReader(new
	// File(csvFileName)));
	//
	// // Read first line to get headers for later use
	// final String headerLine = br.readLine();
	//
	// int diff = 0;
	//
	// String line;
	//
	// while ((line = br.readLine()) != null) {
	// if (compareName(individual.getName(), line)) {
	// int col = -1;
	//
	// // Ignore empty file
	// if (headerLine == null) {
	// br.close();
	// return;
	// }
	//
	// String[] columns = headerLine.split(";");
	//
	// for (int i = 0; i < columns.length; i++) {
	// if (columns[i].equals("Alder") || columns[i].equals("Født kildedato")) {
	// col = i;
	// break;
	// }
	// }
	//
	// if (col < 0) {
	// br.close();
	// return;
	// }
	//
	// columns = line.split(";");
	//
	// final Pattern r = Pattern.compile("\\d*");
	// final Matcher m = r.matcher(columns[col]);
	//
	// if (m.find() && !m.group(0).equals("")) {
	// diff = Integer.parseInt(m.group(0));
	// }
	//
	// diff = (diff + individual.getBirthYear()) - ftYear;
	//
	// if (((diff < 2) && (diff > -2))
	// && ((ftYear < 1845) || compareBirthPlace(individual.getBirthPlace(),
	// line))) {
	//

	//
	// }

}
