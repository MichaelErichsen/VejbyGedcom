package net.myerichsen.gedcom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Read each individual in a table. Insert phonetic coding of first word in
 * FIRSTNAMES and of LASTNAME and store the result in PHONNAME.
 * 
 * C:\Users\michael\CPHDB CPH.BURIAL_PERSON_COMPLETE FIRSTNAMES LASTNAME
 * PHONNAME
 * 
 * @author Michael Erichsen
 * @version 12. feb. 2023
 *
 */
public class PhonCoder {
	private static Logger logger;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			System.out.println(
					"Usage: PhonCoder derbydatabasepath tablename firstnamecolumn lastnamecolumn phoncodedcolumn");
			System.exit(4);
		}

		logger = Logger.getLogger("DBLister");

		final PhonCoder pc = new PhonCoder();
		try {
			pc.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the Derby database
	 * 
	 * @return connected statement
	 *
	 * @throws SQLException
	 *
	 */
	private Statement connectToDB(String[] args) throws SQLException {
		final String dbURL1 = "jdbc:derby:" + args[0];
		final Connection conn1 = DriverManager.getConnection(dbURL1);
		System.out.println("Connected to database " + dbURL1);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 */
	private void execute(String[] args) throws Exception {
		int counter = 0;
		String phonName = "";
		final Fonkod fk = new Fonkod();

		final Statement statement = connectToDB(args);

		final String SELECT = "SELECT " + args[2] + ", " + args[3] + " FROM " + args[1] + " FOR UPDATE OF " + args[4];
		logger.fine(SELECT);

		String[] sa;
		final List<String[]> lsa = new ArrayList<>();

		final ResultSet rs = statement.executeQuery(SELECT);

		while (rs.next()) {
			sa = new String[2];
			sa[0] = rs.getString(args[2]).trim();
			sa[1] = rs.getString(args[3]).trim();
			lsa.add(sa);
		}

		logger.info("Læste rækker: " + lsa.size());

		final String UPDATE = "UPDATE " + args[1] + " SET " + args[4] + " = '%s' WHERE " + args[2] + " = '%s' AND "
				+ args[3] + " = '%s'";
		String query = "";

		for (final String[] strings : lsa) {
			phonName = fk.generateKey(strings[0]) + " " + fk.generateKey(strings[1]);
			query = String.format(UPDATE, phonName, strings[0], strings[1]);
			statement.executeUpdate(query);
			counter++;

			if ((counter % 1000) == 0) {
				logger.info("Opdateret " + counter);
			}
		}

		statement.close();
		logger.info(counter + " rækker er opdateret");
	}

}
