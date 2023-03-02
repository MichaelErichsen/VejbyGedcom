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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find all registry entries that match the phonetic name and life span of a
 * given individual in the derby database
 *
 * @author Michael Erichsen
 * @version 2. mar. 2023
 *
 */
public class SearchArchives {
	private static final String KRONBORG = "Kronborg";
	private static final String SELECT_PROBATE = "SELECT * FROM GEDCOM.EVENT "
			+ "JOIN GEDCOM.INDIVIDUAL ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID "
			+ "WHERE GEDCOM.INDIVIDUAL.FONKOD = '%s' AND GEDCOM.EVENT.FROMDATE >= '%s' AND TODATE <= '%s'";
	private static final String CENSUS_HEADER = "FTaar;KIPnr;Loebenr;Kildestednavn;Amt;Herred;Sogn;"
			+ "Husstands_familienr;Matr_nr_Adresse;Kildenavn;Fonnavn;Koen;Alder;Civilstand;"
			+ "Kildeerhverv;Stilling_i_husstanden;Kildefoedested;Foedt_kildedato;Foedeaar;"
			+ "Adresse;Matrikel;Gade_nr;Kildehenvisning;Kildekommentar\n";
	private static final String SELECT_CENSUS = "SELECT * FROM VEJBY.CENSUS WHERE FONNAVN = '%s' "
			+ "AND FTAAR >= %s AND FTAAR <= %s";
	private static final String PROBATE_HEADER = "GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;VITALTYPE;COVERED_DATA;SOURCE;FONKOD";
	private static Logger logger;
	private static int counter = 0;
	private static BufferedWriter bw;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if ((args.length < 4) || (args.length > 6)) {
			System.out.println(
					"Usage: SearchArchives censusdatabasepath probatedatabasepath outputdirectory name [birthyear [deathyear]]");
			System.exit(4);
		}

		logger = Logger.getLogger("SearchArchives");

		final SearchArchives sa = new SearchArchives();

		try {
			sa.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param dbpath
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String dbpath) throws SQLException {
		final String dbURL = "jdbc:derby:" + dbpath;
		final Connection conn1 = DriverManager.getConnection(dbURL);
		logger.fine("Connected to database " + dbURL);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		// Search Census Derby table
		searchCensusTable(args);

		// Search for probates
		searchProbates(args);

		logger.info("Program ended");
	}

	/**
	 * Search the census table for the individual
	 *
	 * @param args
	 * @throws Exception
	 */
	private void searchCensusTable(String[] args) throws Exception {
		final Statement statement = connectToDB(args[0]);

		final String phonName = new Fonkod().generateKey(args[3]);
		final String birthYear = (args.length < 5 ? "0" : args[4]);
		final String deathYear = (args.length < 6 ? "9999" : args[5]);
		final String query = String.format(SELECT_CENSUS, phonName, birthYear, deathYear);
		logger.info(query);
		final ResultSet rs = statement.executeQuery(query);

		List<CensusIndividual> cil = null;

		while (rs.next()) {
			cil = CensusIndividual.getFromDb(rs);
		}

		statement.close();

		if ((cil != null) && (cil.size() > 0)) {
			writeCensusOutput(cil, args);
		}

	}

	/**
	 * Find probates mentioning the individual
	 *
	 * @param individual
	 * @param args
	 * @throws Exception
	 */
	private void searchProbates(String[] args) throws Exception {
		final Statement statement = connectToDB(args[1]);

		final String phonName = new Fonkod().generateKey(args[3]);
		final String birthYear = (args.length < 5 ? "0001" : args[4]);
		final String deathYear = (args.length < 6 ? "9999" : args[5]);
		final String query = String.format(SELECT_PROBATE, phonName, birthYear + "-01-01", deathYear + "-12-31");
		logger.info(query);
		final ResultSet rs = statement.executeQuery(query);

		String singleLine;
		final HashSet<String> outLines = new HashSet<>();
		String coveredData;
		String source;
		String name;
		int counter = 0;

		while (rs.next()) {
			coveredData = rs.getString("COVERED_DATA");
			source = rs.getString("SOURCE");
			name = rs.getString("NAME").trim();

			if (source.contains(KRONBORG) && coveredData.contains(name)) {
				singleLine = name + ";" + rs.getString("ID").trim() + ";" + rs.getString("FROMDATE").trim() + ";"
						+ rs.getString("TODATE").trim() + ";" + rs.getString("PLACE").trim() + ";"
						+ rs.getString("EVENTTYPE").trim() + ";" + rs.getString("VITALTYPE").trim() + ";"
						+ coveredData.replace(";", ".").trim() + ";" + source.replace(";", ".").trim() + ";"
						+ rs.getString("FONKOD").trim();
				singleLine = singleLine.replaceAll("\\r\\n", " ¤ ");
				outLines.add(singleLine);
				counter++;
			}
		}

		statement.close();

		if (counter > 0) {
			writeProbateOutput(args, outLines, counter);
		}

	}

	/**
	 * Write output for the census search
	 *
	 * @param cil
	 * @param args
	 * @throws IOException
	 */
	private void writeCensusOutput(List<CensusIndividual> cil, String[] args) throws IOException {
		final String outName = args[2] + "/" + args[3] + "db_census.csv";
		int diff = 0;
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher;

		for (final CensusIndividual ci : cil) {
			if (counter == 0) {
				bw = new BufferedWriter(new FileWriter(new File(outName)));
				bw.write(CENSUS_HEADER);
			}

			diff = 0;

			if (args.length > 4) {
				if (ci.getFoedeaar() != 0) {
					diff = Integer.getInteger(args[4]) - ci.getFoedeaar();
				} else {
					if (ci.getFoedt_kildedato().length() > 0) {
						matcher = pattern.matcher(ci.getFoedt_kildedato());

						if (matcher.find()) {
							diff = Integer.getInteger(args[4]) - Integer.getInteger(matcher.group(1));
						}
					}
				}
			}

			if ((diff > -2) && (diff < 2)) {
//				if ((diff > -2) && (diff > 2) && (compareLocation(ci, birthPlace))) {
				bw.write(ci.toString());
				counter++;
			}

		}

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of census data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}
	}

	/**
	 * @param args
	 * @param outLines
	 * @param counter
	 * @throws IOException
	 */
	private void writeProbateOutput(String[] args, final HashSet<String> outLines, int counter) throws IOException {
		final String outName = args[2] + "/" + args[3] + "_probates.csv";
		bw = new BufferedWriter(new FileWriter(outName));
		bw.write(PROBATE_HEADER + "\n");

		for (final String string : outLines) {
			bw.write(string + "\n");
		}

		bw.flush();
		bw.close();

		logger.info(counter + " records written to " + outName);
		Desktop.getDesktop().open(new File(outName));
	}

	/**
	 * @param ci
	 * @param location
	 * @return
	 */
//	private boolean compareLocation(CensusIndividual ci, String location) {
//		final String[] locationParts = location.split(",");
//
//		for (String part : locationParts) {
//			part = part.trim();
//
//			if (ci.getAmt().contains(part)) {
//				return true;
//			}
//			if (ci.getHerred().contains(part)) {
//				return true;
//			}
//			if (ci.getSogn().contains(part)) {
//				return true;
//			}
//			if (ci.getKildestednavn().contains(part)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
}
