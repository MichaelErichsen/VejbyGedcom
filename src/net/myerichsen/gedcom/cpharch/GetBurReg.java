package net.myerichsen.gedcom.cpharch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Erichsen
 * @version 1. feb. 2023
 *
 */
public class GetBurReg {
	private static Logger logger;
	private static String template = "SELECT * FROM CPH.BURIAL_PERSON_COMPLETE "
			+ "WHERE CPH.BURIAL_PERSON_COMPLETE.FIRSTNAMES LIKE '%s' "
			+ "AND CPH.BURIAL_PERSON_COMPLETE.LASTNAME LIKE '%s'";

	private static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out.println("Usage: GetBurReg derbydatabasepath outputdirectory firstNames lastName [birthyear]\n"
					+ "Names can be truncated");
			System.exit(4);
		}

		logger = Logger.getLogger("GetBurReg");

		final GetBurReg gpr = new GetBurReg();

		try {
			gpr.execute(args);
		} catch (final SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Connect to the Derby database
	 *
	 * @return
	 * @throws SQLException
	 *
	 */
	private Statement connectToDB(String url) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + url;
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		System.out.println("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		String result = "";
		int calcYear = 0;

		final Statement stmt = connectToDB(args[0]);

		final String outName = args[1] + "/" + args[2] + " " + args[3] + "_burreg.csv";
		final BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

		String query = String.format(template, args[2] + "%", args[3] + "%");
		logger.fine(query);
		ResultSet rs = stmt.executeQuery(query);
		int birthYear = 0;

		while (rs.next()) {

			if (args.length > 4) {
				try {
					birthYear = rs.getInt("YEAROFBIRTH");
					calcYear = getYearFromDate(args[4]) - birthYear;
				} catch (Exception e) {
					calcYear = 0;
				}

				if (calcYear > 2 || calcYear < -2) {
					continue;
				}
			}

			result = getField(rs, "FIRSTNAMES") + ";" + getField(rs, "LASTNAME") + ";" + getField(rs, "BIRTHNAME") + ";"
					+ getFieldInt(rs, "AGEYEARS") + ";" + getFieldInt(rs, "YEAROFBIRTH") + ";"
					+ getField(rs, "DEATHPLACE") + ";" + getField(rs, "CIVILSTATUS") + ";"
					+ getField(rs, "ADRESSOUTSIDECPH") + ";" + getField(rs, "SEX") + ";" + getField(rs, "COMMENT") + ";"
					+ getField(rs, "CEMETARY") + ";" + getField(rs, "CHAPEL") + ";" + getField(rs, "PARISH") + ";"
					+ getField(rs, "STREET") + ";" + getField(rs, "HOOD") + ";" + getFieldInt(rs, "STREET_NUMBER") + ";"
					+ getField(rs, "LETTER") + ";" + getField(rs, "FLOOR") + ";" + getField(rs, "INSTITUTION") + ";"
					+ getField(rs, "INSTITUTION_STREET") + ";" + getField(rs, "INSTITUTION_HOOD") + ";"
					+ getField(rs, "INSTITUTION_STREET_NUMBER") + ";" + getField(rs, "OCCUPTATIONS") + ";"
					+ getField(rs, "OCCUPATION_RELATION_TYPES)") + ";" + getField(rs, "DEATHCAUSES") + ";"
					+ getField(rs, "DEATHCAUSES_DANISH") + "\n";

			logger.fine(result);
			bw.write(result);
			counter++;
		}

		bw.flush();
		bw.close();
		logger.info(counter + " lines of Copenhagen burial registry data written to " + outName);

	}

	/**
	 * @param string
	 * @return
	 */
	private String getField(ResultSet rs, String field) {
		try {
			return rs.getString(field).trim();
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private String getFieldInt(ResultSet rs, String field) {
		try {
			return Integer.toString(rs.getInt(field));
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * Get year as integer
	 *
	 * @param date
	 * @return
	 */
	private int getYearFromDate(String date) {
		final Pattern r = Pattern.compile("\\d{4}");

		final Matcher m = r.matcher(date);

		if (m.find()) {
			return Integer.parseInt(m.group(0));
		}
		return 1;
	}
}
