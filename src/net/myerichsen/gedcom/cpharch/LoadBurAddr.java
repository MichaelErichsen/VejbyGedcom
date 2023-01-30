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
public class LoadBurAddr extends LoadCphArch {
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

		final LoadBurAddr lba = new LoadBurAddr();

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
		loadBurialAddress(statement, args);

		logger.info(counter + " rows added to " + args[0]);

	}

	/**
	 * Load lines into Derby table
	 *
	 * @param statement
	 * @param args
	 * @throws Exception
	 */
	private void loadBurialAddress(Statement statement, String[] args) throws Exception {
		String[] columns;
		StringBuffer sb = new StringBuffer();

		final List<String> columnTypes = getColumnTypes(statement, "BURIAL_ADDRESS");

		String query = "DELETE FROM CPH.BURIAL_ADDRESS";
		statement.execute(query);

		final BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
		String line;

		// Ignore header line
		line = br.readLine();

		while ((line = br.readLine()) != null) {
			while (!line.endsWith("\"")) {
				line = line + br.readLine();
			}

			sb = new StringBuffer(
					"INSERT INTO CPH.BURIAL_ADDRESS (ID, PERSON_ID, STREET, HOOD, STREET_UNIQUE, STREET_NUMBER, "
							+ "LETTER, FLOOR, INSTITUTION, INSTITUTION_STREET, INSTITUTION_HOOD, "
							+ "INSTITUTION_STREET_UNIQUE, INSTITUTION_STREET_NUMBER) VALUES (");

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
