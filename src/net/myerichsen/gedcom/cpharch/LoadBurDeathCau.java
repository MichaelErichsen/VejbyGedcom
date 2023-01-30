package net.myerichsen.gedcom.cpharch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

/**
 * Load a dump from Copenhagen Archives.
 * 
 * @author Michael Erichsen
 * @bversion 30. jan. 2023
 *
 */
public class LoadBurDeathCau extends LoadCphArch {
	/**
	 * 
	 */
	private static final String TABLENAME = "BURIAL_DEATHCAUSE";
	private static final String DELETE = "DELETE FROM CPH.BURIAL_DEATHCAUSE";
	private static final String INSERT = "INSERT INTO CPH.BURIAL_DEATHCAUSE (ID, PERSON_ID, DEATHCAUSE, "
			+ "DEATHCAUSE_DANISH, XORDER, PRIORITY) VALUES (";
	private static int counter = 0;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: LoadBurAddr derbydatabasepath csvfile");
			System.exit(4);
		}

		logger = Logger.getLogger("LoadBurAddr");

		final LoadBurDeathCau lba = new LoadBurDeathCau();

		try {
			lba.execute(args);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Worker method
	 * 
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		final Statement statement = connectToDB(args[1]);
		loadTable(statement, args);

		logger.info(counter + " rows added to " + args[0]);

	}

	/**
	 * Load lines into Derby table
	 *
	 * @param statement
	 * @param args
	 * @throws Exception
	 */
	private void loadTable(Statement statement, String[] args) throws Exception {
		String[] columns;
		StringBuffer sb = new StringBuffer();
		String query = "";

		final List<String> columnTypes = getColumnTypes(statement, TABLENAME);

		statement.execute(DELETE);

		final BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
		String line;

		// Ignore header line
		line = br.readLine();

		while ((line = br.readLine()) != null) {
			while (!line.endsWith("\"")) {
				line = line + br.readLine();
			}

			sb = new StringBuffer(INSERT);

			columns = line.replace("\",\"", "\";\"").split(";");

			for (int i = 0; i < columns.length; i++) {
				sb.append(convertString(columnTypes.get(i), columns[i]));

				if (i < columns.length - 1) {
					sb.append(", ");
				}
			}

			try {
				sb.append(")");
				query = sb.toString();
				logger.fine(query);
				statement.execute(query);
				counter++;
			} catch (final SQLException e) {
				if (e.getSQLState().equals("42821")) {
					logger.warning(e.getSQLState() + ", " + e.getMessage() + ", " + query);
				} else {
					logger.severe(e.getSQLState() + ", " + e.getMessage() + ", " + query);
					br.close();
					throw new SQLException(e);
				}
			}

		}

		br.close();

	}
}
