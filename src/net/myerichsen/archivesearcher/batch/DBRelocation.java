package net.myerichsen.archivesearcher.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.gedcom4j.exception.GedcomParserException;

/**
 * Find all relocations to and from a given location in a GEDCOM file. Requires
 * the non-standard "Flytning" event subtype with the location in the place
 * fields (to) or as part of the note field (from).
 * <p>
 * Parameters:
 * <ul>
 * <li>Full path to GEDCOM file</li>
 * <li>Path to an existing output directory</li>
 * </ul>
 * <p>
 * The program produces a .csv file with a row for each relocation found.
 *
 * @author Michael Erichsen
 * @version 8. mar. 2023
 *
 */
public class DBRelocation {
	private static final String HEADER = "ID;Fornavn;Efternavn;Fonetisk navn;Flyttedato;Til;Fra;Detaljer";
	private static final String query = "SELECT VEJBY.INDIVIDUAL.ID, VEJBY.INDIVIDUAL.GIVENNAME, VEJBY.INDIVIDUAL.SURNAME, "
			+ "VEJBY.INDIVIDUAL.PHONNAME, VEJBY.EVENT.DATE, VEJBY.EVENT.PLACE, VEJBY.EVENT.NOTE, VEJBY.EVENT.SOURCEDETAIL "
			+ "FROM VEJBY.INDIVIDUAL, VEJBY.EVENT "
			+ "WHERE VEJBY.EVENT.SUBTYPE = 'Flytning' AND VEJBY.INDIVIDUAL.ID = VEJBY.EVENT.INDIVIDUAL "
			+ "ORDER BY VEJBY.INDIVIDUAL.SURNAME, VEJBY.INDIVIDUAL.GIVENNAME";
	private static Logger logger;

	/**
	 * MilRollEntryDialog method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: DBRelocation derbydatabasepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("DBRelocation");

		final DBRelocation dbr = new DBRelocation();

		try {
			dbr.execute(args);
		} catch (IOException | GedcomParserException | SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Connect to the Derby database
	 *
	 * @param args
	 * @return
	 *
	 * @throws SQLException
	 *
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL = "jdbc:derby:" + args[0];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		return conn.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws GedcomParserException
	 * @throws IOException
	 * @throws SQLException
	 */
	private void execute(String[] args) throws IOException, GedcomParserException, SQLException {
		int counter = 0;
		final Statement statement = connectToDB(args);

		final String outfile = args[1] + "\\flyt_all.csv";
		final BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

		writer.write(HEADER + "\n");

		final ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			writer.write(rs.getString(1).replace("I", "").replace("@", "") + ";" + rs.getString(2) + ";"
					+ rs.getString(3) + ";" + rs.getString(4) + ";" + rs.getString(5) + ";" + rs.getString(6) + ";"
					+ rs.getString(7) + ";" + rs.getString(8) + "\n");

			counter++;
		}

		writer.flush();
		writer.close();
		statement.close();

		System.out.println(counter + " flytninger gemt i " + outfile);
	}

}
