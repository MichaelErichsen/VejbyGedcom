package net.myerichsen.gedcom.cpharch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Erichsen
 * @version 3. feb. 2023
 *
 */
public class GetPolReg {
	private static Logger logger;
	private static String template1 = "SELECT * FROM CPH.POLICE_PERSON WHERE CPH.POLICE_PERSON.FIRSTNAMES "
			+ "LIKE '%s' AND CPH.POLICE_PERSON.LASTNAME LIKE '%s'";
	private static String template2 = "SELECT * FROM CPH.POLICE_ADDRESS WHERE CPH.POLICE_ADDRESS.PERSON_ID = %d";
	private static String template3 = "SELECT * FROM CPH.POLICE_POSITION WHERE CPH.POLICE_POSITION.PERSON_ID = %d";
	private static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out.println("Usage: GetPolReg derbydatabasepath outputdirectory firstNames lastName [birthyear]\n"
					+ "Names can be truncated");
			System.exit(4);
		}

		try {
			@SuppressWarnings("unused")
			final GetPolReg gpr = new GetPolReg(args);
		} catch (final SQLException e) {
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
	public GetPolReg(String[] args) throws Exception {
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

		String query = String.format(template1, args[2] + "%", args[3] + "%");
		logger.fine(query);
		ResultSet rs = stmt.executeQuery(query);
		ResultSet rs3;
		final List<Integer> li = new ArrayList<>();
		final List<Integer> lb = new ArrayList<>();
		final List<String> ls = new ArrayList<>();
		final List<String> lp = new ArrayList<>();
		String query3;

		while (rs.next()) {
			li.add(rs.getInt("ID"));
			ls.add(getField(rs, "FIRSTNAMES") + " " + getField(rs, "LASTNAME"));
			try {
				lb.add(rs.getInt("BIRTHYEAR"));
			} catch (final Exception e) {
				lb.add(0);
			}
		}

		for (int i = 0; i < li.size(); i++) {
			query3 = String.format(template3, li.get(i));
			logger.fine(query);

			rs3 = stmt.executeQuery(query3);

			if (rs3.next()) {
				lp.add(getField(rs3, "POSITION_DANISH"));
			} else {
				lp.add(" ");
			}
		}

		for (int i = 0; i < li.size(); i++) {
			query = String.format(template2, li.get(i));
			logger.fine(query);

			rs = stmt.executeQuery(query);

			while (rs.next()) {

				if (args.length > 4) {
					calcYear = getYearFromDate(args[4]) - lb.get(i);

					if (calcYear > 2 || calcYear < -2) {
						continue;
					}
				}

				result = ls.get(i) + ";" + lb.get(i) + ";" + lp.get(i) + ";" + getField(rs, "STREET") + ";"
						+ getField(rs, "NUMBER") + ";" + getField(rs, "LETTER") + ";" + getField(rs, "FLOOR") + ";"
						+ getField(rs, "PLACE") + ";" + getField(rs, "HOST") + ";" + getFieldInt(rs, "DAY") + ";"
						+ getFieldInt(rs, "MONTH") + ";" + getFieldInt(rs, "XYEAR") + ";" + getField(rs, "FULL_ADDRESS")
						+ "\n";

				logger.fine(result);

				if (counter == 0) {
					outName = args[1] + "/" + args[2] + " " + args[3] + "_polreg.csv";
					bw = new BufferedWriter(new FileWriter(outName));
					final String header = "ID;NAME;BIRTHYEAR;STREET;NUMBER;LETTER;FLOOR;PLACE;HOST;DAY;MONTH;XYEAR;FULL_ADDRESS";
					bw.write(header);
				}

				bw.write(result);
				counter++;
			}
		}

		if (counter > 0) {
			bw.flush();
			bw.close();
		}

		stmt.close();

		if (counter > 0) {
			logger.info(counter + " lines of Police Registry data written to " + outName);
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
