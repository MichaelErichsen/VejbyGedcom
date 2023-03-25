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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.gedcom.db.models.CensusIndividual;
import net.myerichsen.gedcom.db.models.DBIndividual;
import net.myerichsen.gedcom.db.models.Relocation;

/**
 * Find all registry entries that match the phonetic name and life span of a
 * given individual in the derby database
 *
 * @author Michael Erichsen
 * @version 25. mar. 2023
 *
 */

public class SearchArchives {
	/**
	 * Helper class implementing Comparator interface
	 *
	 * @author Michael Erichsen
	 * @version 11. mar. 2023
	 *
	 */
	public class CensusIndividualComparator implements Comparator<CensusIndividual> {

		/**
		 * Sort in ascending order of birth year and location
		 */
		@Override
		public int compare(CensusIndividual o1, CensusIndividual o2) {
			final String key1 = o1.getFTaar() + o1.getAmt() + o1.getHerred() + o1.getSogn();
			final String key2 = o2.getFTaar() + o2.getAmt() + o2.getHerred() + o2.getSogn();

			return key1.compareToIgnoreCase(key2);
		}

	}

	/**
	 * Helper class implementing Comparator interface
	 *
	 * @author Michael Erichsen
	 * @version 13. mar. 2023
	 *
	 */
	public class RelocationComparator implements Comparator<Relocation> {

		/**
		 * Sort in ascending order of given name, birth date, and relocation date
		 */
		@Override
		public int compare(Relocation o1, Relocation o2) {
			final String key1 = o1.getGivenName() + o1.getBirthYear() + o1.getDate();
			final String key2 = o2.getGivenName() + o2.getBirthYear() + o2.getDate();

			return key1.compareToIgnoreCase(key2);
		}

	}

	/**
	 * Constants and static fields
	 */
	private static final String PROBATE_SOURCE = "Kronborg";
	private static final String DIGITS_ONLY = "\\d+";
	private static final String FOUR_DIGITS = "\\d{4}";

	private static final String BURIAL_HEADER = "FIRSTNAMES;LASTNAME;DATEOFDEATH;YEAROFBIRTH;DEATHPLACE;CIVILSTATUS;"
			+ "ADRESSOUTSIDECPH;SEX;COMMENT;CEMETARY;CHAPEL;PARISH;STREET;HOOD;STREET_NUMBER;LETTER;"
			+ "FLOOR;INSTITUTION;INSTITUTION_STREET;INSTITUTION_HOOD;INSTITUTION_STREET_NUMBER;"
			+ "OCCUPTATIONS;OCCUPATION_RELATION_TYPES;DEATHCAUSES;DEATHCAUSES_DANISH\n";
	private static final String CENSUS_HEADER = "FTaar;Amt;Herred;Sogn;Kildestednavn;"
			+ "Husstands_familienr;Matr_nr_Adresse;Kildenavn;Koen;Alder;Civilstand;"
			+ "Kildeerhverv;Stilling_i_husstanden;Kildefoedested;Foedt_kildedato;Foedeaar;"
			+ "Adresse;Matrikel;Gade_nr;Kildehenvisning;Kildekommentar;KIPnr;Loebenr;Fonnavn;Kildedetaljer\n";
	private static final String POLREG_HEADER = "NAME;BIRTHYEAR;OCCUPATION;STREET;NUMBER;LETTER;"
			+ "FLOOR;PLACE;HOST;DAY;MONTH;XYEAR;FULL_DATE;FULL_ADDRESS\n";
	private static final String PROBATE_HEADER = "GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;"
			+ "VITALTYPE;COVERED_DATA;SOURCE";
	private static final String RELOCATION_HEADER = "ID;Fornavn;Efternavn;Flyttedato;Til;Fra;Detaljer;Fødselsdato";

	private static final String SELECT_BIRTHDATE = "SELECT DATE FROM VEJBY.EVENT WHERE INDIVIDUAL = '%s' AND (TYPE = 'Birth' OR TYPE = 'Christening')";
	private static final String SELECT_BURIAL_PERSON = "SELECT * FROM CPH.BURIAL_PERSON_COMPLETE "
			+ "WHERE CPH.BURIAL_PERSON_COMPLETE.PHONNAME = '%s'";
	private static final String SELECT_CENSUS = "SELECT * FROM VEJBY.CENSUS WHERE FONNAVN = '%s' "
			+ "AND FTAAR >= %s AND FTAAR <= %s";
	private static final String SELECT_CENSUS_HOUSEHOLD = "SELECT * FROM VEJBY.CENSUS WHERE KIPNR = '%s' "
			+ "AND HUSSTANDS_FAMILIENR = '%s' ORDER BY LOEBENR";
	private static final String SELECT_POLICE_ADDRESS = "SELECT * FROM CPH.POLICE_ADDRESS WHERE CPH.POLICE_ADDRESS.PERSON_ID = %d";
	private static final String SELECT_POLICE_PERSON = "SELECT * FROM CPH.POLICE_PERSON WHERE CPH.POLICE_PERSON.PHONNAME = '%s'";
	private static final String SELECT_POLICE_POSITION = "SELECT * FROM CPH.POLICE_POSITION WHERE CPH.POLICE_POSITION.PERSON_ID = %d";
	private static final String SELECT_PROBATE = "SELECT * FROM GEDCOM.EVENT "
			+ "JOIN GEDCOM.INDIVIDUAL ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID "
			+ "WHERE GEDCOM.INDIVIDUAL.FONKOD = '%s' AND GEDCOM.EVENT.FROMDATE >= '%s' AND TODATE <= '%s'";
	private static final String SELECT_RELOCATION = "SELECT VEJBY.INDIVIDUAL.ID, VEJBY.INDIVIDUAL.GIVENNAME, "
			+ "VEJBY.INDIVIDUAL.SURNAME, VEJBY.EVENT.DATE, "
			+ "VEJBY.EVENT.PLACE, VEJBY.EVENT.NOTE, VEJBY.EVENT.SOURCEDETAIL "
			+ "FROM VEJBY.INDIVIDUAL, VEJBY.EVENT WHERE VEJBY.EVENT.SUBTYPE = 'Flytning' "
			+ "AND VEJBY.INDIVIDUAL.ID = VEJBY.EVENT.INDIVIDUAL AND VEJBY.INDIVIDUAL.PHONNAME = '%s'";
	private static BufferedWriter bw = null;
	private static int counter = 0;

	private static Logger logger;

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if ((args.length < 5) || (args.length > 7)) {
			System.out.println("Usage: SearchArchives censusdatabasepath probatedatabasepath "
					+ "cphdatabasepath outputdirectory [id | name] [birthyear [deathyear]]");
			System.exit(4);
		}

		logger = Logger.getLogger("SearchArchives");

		final SearchArchives sa = new SearchArchives();

		try {
			sa.execute(args);
		} catch (final Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Compare all component parts of the two locations with each other
	 *
	 * @param ci
	 * @param location
	 * @return
	 */
	private boolean compareLocation(CensusIndividual ci, String location) {
		if (ci.getKildefoedested().length() == 0) {
			return true;
		}

		final String[] locationParts = location.split(",");

		for (String part : locationParts) {
			part = part.trim();

			if (ci.getAmt().contains(part) || ci.getHerred().contains(part) || ci.getSogn().contains(part)
					|| ci.getKildestednavn().contains(part)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Connect to the Derby database
	 *
	 * @param dbpath
	 * @return
	 * @throws SQLException
	 */
	private Statement connectToDB(String dbpath) throws SQLException {
		final String dbURL = "jdbc:derby:" + dbpath;
		final Connection conn1 = DriverManager.getConnection(dbURL);
		return conn1.createStatement();
	}

	/**
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		// Get a DBIndividual object
		logger.info("Get individual " + args[4]);
		DBIndividual individual;

		if (args[4].matches(DIGITS_ONLY)) {
			final Statement statement = connectToDB(args[0]);
			individual = new DBIndividual(statement, args[4]);
			statement.close();

			if (individual.getName().length() == 0) {
				logger.warning("Individual " + args[4] + " findes ikke i tabellen");
				System.exit(4);
			}
		} else {
			final String birthYear = ((args.length >= 6) ? args[5] : "0001");
			final String deathYear = ((args.length >= 7) ? args[6] : "9999");
			individual = new DBIndividual(args[4], birthYear, deathYear);
		}

		// Search for similar relocations
		logger.info("Search for similar relocations for " + individual.getName());
		searchRelocations(args, individual);

		// Search Census Derby table
		logger.info("Search for censuses for " + individual.getName());
		searchCensusTable(args, individual);

		// Search Probates Derby table
		logger.info("Search for probates for " + individual.getName() + " in " + PROBATE_SOURCE);
		searchProbates(args, individual);

		// Search Copenhagen Police Registry
		logger.info("Search Police Registry");
		searchPoliceRegistry(args, individual);

		// Search Copenhagen burial registry
		logger.info("Search Copenhagen burial registry");
		searchBurialRegistry(args, individual);

		logger.info("Program ended");
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
	 * Search Copenhagen burial registry
	 *
	 * @param args
	 * @param individual
	 * @throws Exception
	 */
	private void searchBurialRegistry(String[] args, DBIndividual individual) throws Exception {
		final String outName = args[3] + "/" + individual.getName() + "_burreg.csv";

		String result = "";
		int calcYear = 0;
		counter = 0;

		final Statement stmt = connectToDB(args[2]);

		final String query = String.format(SELECT_BURIAL_PERSON, individual.getPhonName());
		final ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			if (individual.getBirthYear() < 1000) {
				calcYear = 0;
			} else {
				try {
					calcYear = individual.getBirthYear() - rs.getInt("YEAROFBIRTH");
				} catch (final Exception e) {
					calcYear = 0;
				}
			}

			if ((calcYear > 2) || (calcYear < -2)) {
				continue;
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
				bw = new BufferedWriter(new FileWriter(new File(outName)));
				bw.write(BURIAL_HEADER);
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
	 * Search the census table for the individual
	 *
	 * @param args
	 * @param individual
	 * @throws Exception
	 */

	private void searchCensusTable(String[] args, DBIndividual individual) throws Exception {
		final Statement statement = connectToDB(args[0]);
		String query = String.format(SELECT_CENSUS, individual.getPhonName().trim(), individual.getBirthYear(),
				individual.getDeathYear());
		ResultSet rs = statement.executeQuery(query);
		String string = "";

		List<CensusIndividual> cil = null;
		cil = CensusIndividual.getFromDb(rs);

		// Add all household members as source details
		for (final CensusIndividual ci : cil) {
			query = String.format(SELECT_CENSUS_HOUSEHOLD, ci.getKIPnr(), ci.getHusstands_familienr());
			rs = statement.executeQuery(query);
			final StringBuffer sb = new StringBuffer();

			while (rs.next()) {
				sb.append(rs.getString("KILDENAVN") + ", " + rs.getString("ALDER") + ", " + rs.getString("CIVILSTAND")
						+ ", " + rs.getString("KILDEERHVERV") + ", " + rs.getString("STILLING_I_HUSSTANDEN") + " - ");
			}

			string = sb.toString();

			if (string.length() > 4096) {
				string = string.substring(0, 4095);
			}

			ci.setKildedetaljer(string);
		}

		statement.close();

		if ((cil != null) && (cil.size() > 0)) {
			writeCensusOutput(cil, args, individual);
		}
	}

	/**
	 * Search Police Registry
	 *
	 * @param args
	 * @param individual
	 * @throws Exception
	 */
	private void searchPoliceRegistry(String[] args, DBIndividual individual) throws Exception {
		final String outName = args[3] + "/" + individual.getName() + "_polreg.csv";
		String result = "";
		int calcYear = 0;

		final Statement stmt = connectToDB(args[2]);

		String query = String.format(SELECT_POLICE_PERSON, individual.getPhonName());
		ResultSet rs = stmt.executeQuery(query);
		ResultSet rs3;
		final List<Integer> li = new ArrayList<>();
		final List<Integer> lb = new ArrayList<>();
		final List<String> ls = new ArrayList<>();
		final List<String> lp = new ArrayList<>();
		String query3;
		String day;
		String month;
		String year;
		counter = 0;

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
			query3 = String.format(SELECT_POLICE_POSITION, li.get(i));
			rs3 = stmt.executeQuery(query3);

			if (rs3.next()) {
				lp.add(getField(rs3, "POSITION_DANISH"));
			} else {
				lp.add(" ");
			}
		}

		for (int i = 0; i < li.size(); i++) {
			query = String.format(SELECT_POLICE_ADDRESS, li.get(i));
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				if (individual.getBirthYear() < 1000) {
					calcYear = 0;
				} else {
					try {
						calcYear = individual.getBirthYear() - lb.get(i);
					} catch (final Exception e) {
						calcYear = 0;
					}
				}

				if ((calcYear > 2) || (calcYear < -2)) {
					continue;
				}

				day = getFieldInt(rs, "DAY");
				month = getFieldInt(rs, "MONTH");
				year = getFieldInt(rs, "XYEAR");

				result = ls.get(i) + ";" + lb.get(i) + ";" + lp.get(i) + ";" + getField(rs, "STREET") + ";"
						+ getField(rs, "NUMBER") + ";" + getField(rs, "LETTER") + ";" + getField(rs, "FLOOR") + ";"
						+ getField(rs, "PLACE") + ";" + getField(rs, "HOST") + ";" + day + ";" + month + ";" + year
						+ ";" + day + "-" + month + "-" + year + ";" + getField(rs, "FULL_ADDRESS") + "\n";

				if (counter == 0) {
					bw = new BufferedWriter(new FileWriter(new File(outName)));
					bw.write(POLREG_HEADER);
				}

				bw.write(result);
				counter++;
			}
		}

		stmt.close();

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of Police Registry data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}
	}

	/**
	 * Find probates mentioning the individual
	 *
	 * @param individual
	 * @param args
	 * @throws Exception
	 */
	private void searchProbates(String[] args, DBIndividual individual) throws Exception {
		final Statement statement = connectToDB(args[1]);

		final String phonName = new Fonkod().generateKey(args[4]);
		final String birthYear = (args.length < 6 ? "0001" : args[5]);
		final String deathYear = (args.length < 7 ? "9999" : args[6]);
		final String query = String.format(SELECT_PROBATE, phonName, birthYear + "-01-01", deathYear + "-12-31");
		final ResultSet rs = statement.executeQuery(query);

		String singleLine;
		final HashSet<String> outLines = new HashSet<>();
		String coveredData;
		String source;
		String name;
		int counter = 0;

		while (rs.next()) {
			coveredData = rs.getString("COVERED_DATA");
			source = rs.getString("SOURCE");
			name = rs.getString("NAME").trim();

			if (source.contains(PROBATE_SOURCE) && coveredData.contains(name)) {
				singleLine = name + ";" + rs.getString("ID").trim() + ";" + rs.getString("FROMDATE").trim() + ";"
						+ rs.getString("TODATE").trim() + ";" + rs.getString("PLACE").trim() + ";"
						+ rs.getString("EVENTTYPE").trim() + ";" + rs.getString("VITALTYPE").trim() + ";"
						+ coveredData.replace(";", ".").trim() + ";" + source.replace(";", ".").trim();
				singleLine = singleLine.replaceAll("\\r\\n", " ¤ ");
				outLines.add(singleLine);
				counter++;
			}
		}

		statement.close();

		if (counter > 0) {
			writeProbateOutput(args, outLines, individual);
		}

	}

	/**
	 * Search for similar relocations
	 *
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	private void searchRelocations(String[] args, DBIndividual individual) throws SQLException, IOException {
		final String outName = args[3] + "/" + individual.getName() + "_flyt.csv";
		counter = 0;

		final Statement statement = connectToDB(args[0]);
		String query = String.format(SELECT_RELOCATION, individual.getPhonName());
		ResultSet rs = statement.executeQuery(query);
		Relocation relocation;
		final List<Relocation> lr = new ArrayList<>();

		while (rs.next()) {
			relocation = new Relocation(
					(rs.getString(1) == null ? "" : rs.getString(1).replace("I", "").replace("@", "").trim()),
					(rs.getString(2) == null ? "" : rs.getString(2).trim()),
					(rs.getString(3) == null ? "" : rs.getString(3).trim()),
					(rs.getString(4) == null ? "" : rs.getString(4).trim()),
					(rs.getString(5) == null ? "" : rs.getString(5).trim()),
					(rs.getString(6) == null ? "" : rs.getString(6).trim()),
					(rs.getString(7) == null ? "" : rs.getString(7).trim()));
			lr.add(relocation);
		}

		// Find relocation dates outside the life span of the individual

		final List<String> ls = new ArrayList<>();

		for (final Relocation relocation2 : lr) {
			if ((relocation2.getYear() < individual.getBirthYear())
					|| (relocation2.getYear() > individual.getDeathYear())) {
				ls.add(relocation2.getId());
			}
		}

		// Eliminate individuals with relocation dates outside their life span

		for (int j = 0; j < lr.size(); j++) {
			for (final String id : ls) {
				if (lr.get(j).getId().equals(id)) {
					lr.remove(j);
				}
			}
		}

		// Add birth date to each record

		for (final Relocation relocation2 : lr) {

			query = String.format(SELECT_BIRTHDATE, "@I" + relocation2.getId() + "@");

			rs = statement.executeQuery(query);

			if (rs.next()) {
				relocation2.setBirthYear((rs.getDate("DATE") != null ? rs.getDate("DATE").toString() : ""));
			}

		}

		// Sort and write out remaining records

		Collections.sort(lr, new RelocationComparator());

		for (final Relocation relocation2 : lr) {

			if (counter == 0) {
				bw = new BufferedWriter(new FileWriter(new File(outName)));
				bw.write(RELOCATION_HEADER + "\n");
			}

			bw.write(relocation2.toString() + "\n");
			counter++;
		}

		if (counter > 0) {
			bw.flush();
			bw.close();

			logger.info(counter + " flytninger gemt i " + outName);
			Desktop.getDesktop().open(new File(outName));
		}

		statement.close();

	}

	/**
	 * Write output for the census search
	 *
	 * @param cil
	 * @param args
	 * @param individual
	 * @throws IOException
	 */
	private void writeCensusOutput(List<CensusIndividual> cil, String[] args, DBIndividual individual)
			throws IOException {
		final String outName = args[3] + "/" + individual.getName() + "_census.csv";
		int diff = 0;
		final Pattern pattern = Pattern.compile(FOUR_DIGITS);
		Matcher matcher;
		counter = 0;

		Collections.sort(cil, new CensusIndividualComparator());

		for (final CensusIndividual ci : cil) {
			diff = 0;

			if (ci.getFoedeaar() > 0) {
				diff = individual.getBirthYear() - ci.getFoedeaar();
			} else {
				if (ci.getFoedt_kildedato().length() > 0) {
					matcher = pattern.matcher(ci.getFoedt_kildedato());

					if (matcher.find()) {
						diff = individual.getBirthYear() - Integer.parseInt(matcher.group(0));
					}
				} else {
					if (ci.getAlder() > 0) {
						diff = ci.getFTaar() - individual.getBirthYear() - ci.getAlder();
					}
				}
			}

			if ((diff >= -2) && (diff <= 2)) {
				if ((individual.getBirthPlace() == null) || (individual.getBirthPlace().length() == 0)
						|| (ci.getKildefoedested().length() == 0)
						|| (compareLocation(ci, individual.getBirthPlace()))) {
					if (counter == 0) {
						bw = new BufferedWriter(new FileWriter(new File(outName)));
						bw.write(CENSUS_HEADER);
					}

					bw.write(ci.toString());
					counter++;
				}
			}
		}

		if (counter > 0) {
			bw.flush();
			bw.close();
			logger.info(counter + " lines of census data written to " + outName);

			Desktop.getDesktop().open(new File(outName));
		}
	}

	/**
	 * Writa a line of probate output
	 *
	 * @param args
	 * @param outLines
	 * @param counter
	 * @throws IOException
	 */
	private void writeProbateOutput(String[] args, final HashSet<String> outLines, DBIndividual individual)
			throws IOException {
		final String outName = args[3] + "/" + individual.getName() + "_probates.csv";
		bw = new BufferedWriter(new FileWriter(outName));
		bw.write(PROBATE_HEADER + "\n");

		for (final String string : outLines) {
			bw.write(string + "\n");
		}

		bw.flush();
		bw.close();

		logger.info(counter + " records written to " + outName);
		Desktop.getDesktop().open(new File(outName));
	}
}
