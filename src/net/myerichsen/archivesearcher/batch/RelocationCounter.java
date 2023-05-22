package net.myerichsen.archivesearcher.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Counter for relocations in and out of a location between 1822 and 1875.
 * Depends on subtype "Flytning" having from-location in NOTE and to-location in
 * PLACE
 *
 * @author Michael Erichsen
 * @version 1. maj 2023
 *
 */
public class RelocationCounter {

	/**
	 * Inner class representing a year and relocation counts
	 *
	 * @author Michael Erichsen
	 * @version 1. maj 2023
	 *
	 */
	private static class RcModel implements Comparable<RcModel> {
		private int year = 0;

		private int from = 0;
		private int to = 0;

		@Override
		public int compareTo(RcModel o) {
			return this.year - o.year;
		}

		/**
		 * @return the year
		 */
		public int getYear() {
			return year;
		}

		/**
		 * @param from the from to set
		 */
		public void setFrom(int from) {
			this.from = from;
		}

		/**
		 * @param to the to to set
		 */
		public void setTo(int to) {
			this.to = to;
		}

		/**
		 * @param year the year to set
		 */
		public void setYear(int year) {
			this.year = year;
		}

		@Override
		public String toString() {
			return year + ";" + from + ";" + to + "\n";
		}
	}

	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT1 = "SELECT YEAR(DATE) AS X, COUNT(*) AS Y FROM EVENT WHERE SUBTYPE = 'Flytning' "
			+ "AND NOTE LIKE ? AND YEAR(DATE) > 1821 AND YEAR(DATE) < 1876 GROUP BY YEAR(DATE)";
	private static final String SELECT2 = "SELECT YEAR(DATE) AS X, COUNT(*) AS Y FROM EVENT WHERE SUBTYPE = 'Flytning' "
			+ "AND PLACE LIKE ? AND YEAR(DATE) > 1821 AND YEAR(DATE) < 1876 GROUP BY YEAR(DATE)";
	private static final String HEADER = "År;Fra;Til";
	private static Logger logger;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Usage: RelocationCounter derbydatabasepath schema location outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLister");

		final RelocationCounter rc = new RelocationCounter();
		try {
			rc.execute(args);
			logger.info("Output saved to " + args[3] + "\\flyt_antal.csv");
		} catch (SQLException | IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		RcModel rcm;
		final List<RcModel> lrcm = new ArrayList<>();

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + args[0]);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, args[1]);
		statement.execute();

		statement = conn.prepareStatement(SELECT1);
		statement.setString(1, "%" + args[2] + "%");
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			rcm = new RcModel();
			rcm.setYear(rs.getInt("X"));
			rcm.setFrom(rs.getInt("Y"));
			lrcm.add(rcm);
		}

		statement = conn.prepareStatement(SELECT2);
		statement.setString(1, "%" + args[2] + "%");
		rs = statement.executeQuery();
		int year;

		outer: while (rs.next()) {
			year = rs.getInt("X");

			for (final RcModel rcModel : lrcm) {
				if (rcModel.getYear() == year) {
					rcModel.setTo(rs.getInt("Y"));
					continue outer;
				}
			}

			rcm = new RcModel();
			rcm.setYear(year);
			rcm.setTo(rs.getInt("Y"));
			lrcm.add(rcm);
		}

		statement.close();
		conn.close();

		Collections.sort(lrcm);

		final String outfile = args[3] + "\\flyt_antal.csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		writer.write(HEADER + "\n");

		for (final RcModel rcModel : lrcm) {
			writer.write(rcModel.toString());
		}

		writer.flush();
		writer.close();
	}

}
