package net.myerichsen.archivesearcher.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Læs GEDCOM record for en person
 *
 * Find alle folketællingerne i den record
 *
 * Læs alle folketællingerne fra FT basen
 *
 * Find fader og moder i hver
 *
 * Hvis ingen findes, så stå af
 *
 * List alle fader/moder par med FT år og sted
 *
 * For hvert par
 *
 * Læs alle folketællinger senere end denne og end 1845
 *
 * List alle søskende med fødeår og evt fødested
 *
 * For hver søskende
 *
 * Se om der er en GEDCOM record og list denne
 *
 * ...
 *
 *
 * Skille ved FT-kode X9999
 *
 * Finde køn (M eller F)
 *
 *
 *
 * enke
 *
 * Husfader Husmoder kone hustru
 *
 * @author Michael Erichsen
 * @version 15. feb. 2026
 *
 */
public class SiblingFinder {

	public record Parametre(String kipnr, String place) {
	}

	private static final String GEDCOM_URL = "C:\\Users\\micha\\Blistrup2Db";
	private static final String GEDCOM_SCHEMA = "BLISTRUP";
	private static final String CENSUS_URL = "C:\\Users\\micha/VEJBYDB";
	private static final String CENSUS_SCHEMA = "VEJBY";

	private static final String SELECT1 = "SELECT * FROM " + GEDCOM_SCHEMA + ".INDIVIDUAL WHERE ID = '%s'";
	private static final String SELECT2 = "SELECT DATE, PLACE, SOURCEDETAIL FROM " + CENSUS_SCHEMA
			+ ".EVENT WHERE TYPE = 'Census' AND INDIVIDUAL = '%s'";
	private static final Pattern pattern1 = Pattern.compile("[A-Z]\\d{4}");

	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: SiblingFinder individId");
			System.exit(4);
		}

		logger = Logger.getLogger("SiblingFinder");
		logger.setLevel(Level.FINE);
		final SiblingFinder sf = new SiblingFinder();

		try {
			sf.execute(args);
		} catch (final SQLException e) {
			logger.severe(e.getMessage());
		}

	}

	private Statement gedcomStmt;
	private Statement censusStmt;
	private final static String SELECT3 = "SELECT * FROM " + CENSUS_SCHEMA
			+ ".CENSUS WHERE KIPNR = '%s' AND KILDESTEDNAVN = '%s'";

	/**
	 * @throws SQLException
	 *
	 */
	private void connectToDatabases() throws SQLException {
		final String gedcomURL = "jdbc:derby:" + GEDCOM_URL;
		final Connection gedcomConn = DriverManager.getConnection(gedcomURL);
		logger.info("Connected to database " + gedcomURL);
		gedcomStmt = gedcomConn.createStatement();

		final String censusURL = "jdbc:derby:" + CENSUS_URL;
		final Connection censusConn = DriverManager.getConnection(censusURL);
		logger.info("Connected to database " + censusURL);
		censusStmt = censusConn.createStatement();
	}

	/**
	 * @throws SQLException
	 *
	 */
	private void disconnectFromDatabases() throws SQLException {
		censusStmt.close();
		gedcomStmt.close();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 */
	private void execute(String[] args) throws SQLException {
		logger.info(args[0]);
		connectToDatabases();

		query1(args);

		final Parametre parametre = query2(args);

		logger.info(parametre.toString());

		query3(parametre);

		disconnectFromDatabases();

	}

	/**
	 * Query GEDCOM database by individual
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public void query1(String[] args) throws SQLException {
		final String query = String.format(SELECT1, args[0]);
		logger.info(query);
		final ResultSet rs = gedcomStmt.executeQuery(query);

		if (rs.next()) {
			logger.info(rs.getString("SURNAME"));
			logger.info(rs.getString("GIVENNAME"));
		} else {
			logger.info("Ikke fundet");
		}
	}

	/**
	 * Query census database by individual
	 * 
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Parametre query2(String[] args) throws SQLException {
		Matcher matcher;
		final String query = String.format(SELECT2, args[0]);
		logger.info(query);
		final ResultSet rs = censusStmt.executeQuery(query);

		if (rs.next()) {
			matcher = pattern1.matcher(rs.getString("SOURCEDETAIL"));

			if (matcher.find()) {
				Parametre parametre = new Parametre(matcher.group(0), rs.getString("PLACE"));
				return parametre;
			}

		}
		logger.info("Ikke fundet");

		return new Parametre("", "");
	}

	/**
	 * Query census databse by kipnr
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public void query3(Parametre parametre) throws SQLException {
		final String query = String.format(SELECT3, parametre.kipnr, parametre.place);
		logger.info(query);
		final ResultSet rs = censusStmt.executeQuery(query);

		while (rs.next()) {
			logger.info(rs.getString("KILDENAVN"));
			logger.info(rs.getString("FONNAVN"));
		}
	}

}
