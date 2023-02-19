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
import java.util.logging.Logger;

/**
 * Find all individuals ID with events for a given location in the Derby
 * database
 *
 * @author Michael Erichsen
 * @version 19. feb. 2023
 */
public class FindLocationIndividuals {
	/**
	 * 
	 */
	private static final String SELECT_EVENT = "SELECT * FROM VEJBY.EVENT";
	private static Logger logger;
	private static final String header = "ID;Navn;Fødselsår;Dødsår;Fødested;Fonetisk navn\n";

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: FindLocationIndividuals location vejbydatabasepath outputdirectory");
			System.exit(4);
		}

		logger = Logger.getLogger("FindLocationIndividuals");

		final FindLocationIndividuals fli = new FindLocationIndividuals();

		try {
			fli.execute(args);
		} catch (final Exception e) {
			try {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} catch (final SQLException e1) {
			}
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void execute(String[] args) throws SQLException, IOException {
		final String dbURL = "jdbc:derby:" + args[1];
		final Connection conn = DriverManager.getConnection(dbURL);
		logger.info("Connected to database " + dbURL);
		final Statement statement = conn.createStatement();
		DBEvent dbe;
		DBIndividual individual;
		final HashSet<String> hss = new HashSet<>();
		int counter = 0;
		String outName = args[2] + "/" + args[0] + "_individuals.csv";
		BufferedWriter bw = null;

		final ResultSet rs = statement.executeQuery(SELECT_EVENT);

		while (rs.next()) {
			dbe = new DBEvent(rs.getInt("ID"), rs.getString("INDIVIDUAL"), rs.getString("PLACE"),
					rs.getString("SUBTYPE"), rs.getString("NOTE"));

			if (dbe.isInLocation(args[0])) {
				hss.add(dbe.getIndividual().trim());
			}
		}

		for (final String id : hss) {
			if (counter == 0) {
				bw = new BufferedWriter(new FileWriter(outName));
				bw.write(header);
			}

			individual = new DBIndividual(statement, id);
			bw.write(individual.toString() + "\n");
			counter++;
		}

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of census data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}

		logger.info(counter + " individuals with events in " + args[0] + " written to " + outName + ". Program ended");
	}
}
