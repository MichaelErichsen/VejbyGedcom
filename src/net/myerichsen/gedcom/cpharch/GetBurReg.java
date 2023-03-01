package net.myerichsen.gedcom.cpharch;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
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
 * @version 1. mar. 2023
 *
 */
public class GetBurReg {
	private static Logger logger;
	private static String template = "SELECT * FROM CPH.BURIAL_PERSON_COMPLETE "
			+ "WHERE CPH.BURIAL_PERSON_COMPLETE.FIRSTNAMES LIKE '%s' "
			+ "AND CPH.BURIAL_PERSON_COMPLETE.LASTNAME LIKE '%s'";
	private static final String header = "FIRSTNAMES;LASTNAME;DATEOFDEATH;YEAROFBIRTH;DEATHPLACE;CIVILSTATUS;"
			+ "ADRESSOUTSIDECPH;SEX;COMMENT;CEMETARY;CHAPEL;PARISH;STREET;HOOD;STREET_NUMBER;LETTER;"
			+ "FLOOR;INSTITUTION;INSTITUTION_STREET;INSTITUTION_HOOD;INSTITUTION_STREET_NUMBER;"
			+ "OCCUPTATIONS;OCCUPATION_RELATION_TYPES;DEATHCAUSES;DEATHCAUSES_DANISH\n";
	private static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Usage: GetBurReg derbydatabasepath outputdirectory firstNames lastName [birthyear]\n"
					+ "Names can be truncated");
			System.exit(4);
		}

		logger = Logger.getLogger("GetBurReg");

		try {
			new GetBurReg(args);
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
	public GetBurReg(String[] args) throws Exception {
		logger = Logger.getLogger("GetPolReg");
		execute(args);
	}

	/**
	 * Connect to the Derby database
	 *
	 * @return
	 * @throws SQLException
	 *
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[0];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.fine("Connected to database " + dbURL);
		return conn.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		String outName = "";
		BufferedWriter bw = null;
		String result = "";
		int calcYear = 0;

		final Statement stmt = connectToDB(args);
		final String query = String.format(template, args[2] + "%", args[3] + "%");
		logger.fine(query);
		final ResultSet rs = stmt.executeQuery(query);
		int birthYear = 0;

		while (rs.next()) {

			if (args.length > 4) {
				try {
					birthYear = rs.getInt("YEAROFBIRTH");
					calcYear = getYearFromDate(args[4]) - birthYear;
				} catch (final Exception e) {
					calcYear = 0;
				}

				if ((calcYear > 2) || (calcYear < -2)) {
					continue;
				}
			}

			result = getField(rs, "FIRSTNAMES") + ";" + getField(rs, "LASTNAME") + ";" + getField(rs, "DATEOFDEATH")
					+ ";" + getFieldInt(rs, "YEAROFBIRTH") + ";" + getField(rs, "DEATHPLACE") + ";"
					+ getField(rs, "CIVILSTATUS") + ";" + getField(rs, "ADRESSOUTSIDECPH") + ";" + getField(rs, "SEX")
					+ ";" + getField(rs, "COMMENT") + ";" + getField(rs, "CEMETARY") + ";" + getField(rs, "CHAPEL")
					+ ";" + getField(rs, "PARISH") + ";" + getField(rs, "STREET") + ";" + getField(rs, "HOOD") + ";"
					+ getFieldInt(rs, "STREET_NUMBER") + ";" + getField(rs, "LETTER") + ";" + getField(rs, "FLOOR")
					+ ";" + getField(rs, "INSTITUTION") + ";" + getField(rs, "INSTITUTION_STREET") + ";"
					+ getField(rs, "INSTITUTION_HOOD") + ";" + getField(rs, "INSTITUTION_STREET_NUMBER") + ";"
					+ getField(rs, "OCCUPTATIONS") + ";" + getField(rs, "OCCUPATION_RELATION_TYPES)") + ";"
					+ getField(rs, "DEATHCAUSES") + ";" + getField(rs, "DEATHCAUSES_DANISH") + "\n";

			if (counter == 0) {
				outName = args[1] + "/" + args[2] + " " + args[3] + "_burreg.csv";
				bw = new BufferedWriter(new FileWriter(outName));
				bw.write(header);
			}
			bw.write(result);
			counter++;
		}

		stmt.close();

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of Copenhagen burial registry data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}

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
