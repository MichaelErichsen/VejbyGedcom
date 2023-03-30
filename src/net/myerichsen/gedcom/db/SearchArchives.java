package net.myerichsen.gedcom.db;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.myerichsen.gedcom.db.models.CensusIndividual;
import net.myerichsen.gedcom.db.models.DBIndividual;
import net.myerichsen.gedcom.db.models.Relocation;
import net.myerichsen.gedcom.db.util.CensusIndividualComparator;
import net.myerichsen.gedcom.db.util.RelocationComparator;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * Find all registry entries that match the phonetic name and life span of a
 * given individual in the derby database
 *
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */

public class SearchArchives {

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
	private static final String POLREG_HEADER = "NAME;BirthDate;OCCUPATION;STREET;NUMBER;LETTER;"
			+ "FLOOR;PLACE;HOST;DAY;MONTH;XYEAR;FULL_DATE;FULL_ADDRESS\n";
	private static final String PROBATE_HEADER = "GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;"
			+ "VITALTYPE;COVERED_DATA;SOURCE";
	private static final String RELOCATION_HEADER = "ID;Fornavn;Efternavn;Flyttedato;Til;Fra;Detaljer;Fødselsdato;Forældre";

	private static final String SELECT_BIRTHDATE = "SELECT DATE FROM VEJBY.EVENT WHERE INDIVIDUAL = ? AND (TYPE = 'Birth' OR TYPE = 'Christening')";
	private static final String SELECT_BURIAL_PERSON = "SELECT * FROM CPH.BURIAL_PERSON_COMPLETE "
			+ "WHERE CPH.BURIAL_PERSON_COMPLETE.PHONNAME = ?";
	private static final String SELECT_CENSUS = "SELECT * FROM VEJBY.CENSUS WHERE FONNAVN = ? "
			+ "AND FTAAR >= ? AND FTAAR <= ?";
	private static final String SELECT_CENSUS_HOUSEHOLD = "SELECT * FROM VEJBY.CENSUS WHERE KIPNR = ? "
			+ "AND HUSSTANDS_FAMILIENR = ? ORDER BY LOEBENR";
	private static final String SELECT_POLICE_ADDRESS = "SELECT * FROM CPH.POLICE_ADDRESS WHERE CPH.POLICE_ADDRESS.PERSON_ID = ?";
	private static final String SELECT_POLICE_PERSON = "SELECT * FROM CPH.POLICE_PERSON WHERE CPH.POLICE_PERSON.PHONNAME = ?";
	private static final String SELECT_POLICE_POSITION = "SELECT * FROM CPH.POLICE_POSITION WHERE CPH.POLICE_POSITION.PERSON_ID = ?";
	private static final String SELECT_PROBATE = "SELECT * FROM GEDCOM.EVENT "
			+ "JOIN GEDCOM.INDIVIDUAL ON GEDCOM.EVENT.ID = GEDCOM.INDIVIDUAL.EVENT_ID "
			+ "WHERE GEDCOM.INDIVIDUAL.FONKOD = ? AND GEDCOM.EVENT.FROMDATE >= ? AND TODATE <= ?";
	private static final String SELECT_RELOCATION = "SELECT VEJBY.INDIVIDUAL.ID, VEJBY.INDIVIDUAL.GIVENNAME, "
			+ "VEJBY.INDIVIDUAL.SURNAME, VEJBY.EVENT.DATE, "
			+ "VEJBY.EVENT.PLACE, VEJBY.EVENT.NOTE, VEJBY.EVENT.SOURCEDETAIL, VEJBY.INDIVIDUAL.PARENTS "
			+ "FROM VEJBY.INDIVIDUAL, VEJBY.EVENT WHERE VEJBY.EVENT.SUBTYPE = 'Flytning' "
			+ "AND VEJBY.INDIVIDUAL.ID = VEJBY.EVENT.INDIVIDUAL AND VEJBY.INDIVIDUAL.PHONNAME = ?";
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
					+ "cphdatabasepath outputdirectory [id | name] [BirthDate [DeathDate]]");
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
	 * Worker method
	 *
	 * @param args
	 * @throws Exception
	 */
	private void execute(String[] args) throws Exception {
		// Get a DBIndividual object
		logger.info("Get individual " + args[4]);
		DBIndividual individual = getIndividual(args);

		// Search for similar relocations
		logger.info("Search for similar relocations for " + individual.getName());
		final Connection conn1 = DriverManager.getConnection("jdbc:derby:" + args[0]);
		searchRelocations(args, conn1, individual);

		// Search Census Derby table
		logger.info("Search for censuses for " + individual.getName());
		searchCensusTable(args, conn1, individual);

		// Search Probates Derby table
		logger.info("Search for probates for " + individual.getName() + " in " + PROBATE_SOURCE);
		final Connection conn2 = DriverManager.getConnection("jdbc:derby:" + args[1]);
		searchProbates(args, conn2, individual);

		// Search Copenhagen Police Registry
		logger.info("Search Police Registry");
		final Connection conn3 = DriverManager.getConnection("jdbc:derby:" + args[2]);
		searchPoliceRegistry(args, conn3, individual);

		// Search Copenhagen burial registry
		logger.info("Search Copenhagen burial registry");
		searchBurialRegistry(args, conn3, individual);

		logger.info("Program ended");
	}

	/**
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private DBIndividual getIndividual(String[] args) throws SQLException {
		DBIndividual individual;

		if (args[4].matches(DIGITS_ONLY)) {
			Connection conn = DriverManager.getConnection("jdbc:derby:" + args[0]);
			individual = new DBIndividual(conn, args[4]);

			if (individual.getName().length() == 0) {
				logger.warning("Individual " + args[4] + " findes ikke i tabellen");
				System.exit(4);
			}
		} else {
			final String BirthYear = ((args.length >= 6) ? args[5] : "0001");
			final String DeathYear = ((args.length >= 7) ? args[6] : "9999");
			individual = new DBIndividual(args[4], BirthYear, DeathYear);
		}
		return individual;
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
	private void searchBurialRegistry(String[] args, Connection conn, DBIndividual individual) throws Exception {
		final String outName = args[3] + "/" + individual.getName() + "_burreg.csv";

		String result = "";
		int calcYear = 0;
		counter = 0;

		PreparedStatement statement = conn.prepareStatement(SELECT_BURIAL_PERSON);
		statement.setString(1, individual.getPhonName());
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			if ((individual.getBirthDate() == null) || (individual.getBirthDate().before(Date.valueOf("1000-01-01")))) {
				calcYear = 0;
			} else {
				try {
					calcYear = individual.getBirthDate().toLocalDate().getYear() - rs.getInt("YEAROFBIRTH");
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

		statement.close();

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
	 * @param conn
	 * @param individual
	 * @throws Exception
	 */

	private void searchCensusTable(String[] args, Connection conn, DBIndividual individual) throws Exception {
		PreparedStatement statement1 = conn.prepareStatement(SELECT_CENSUS);
		statement1.setString(1, individual.getPhonName().trim());
		statement1.setLong(2, individual.getBirthDate().toLocalDate().getYear());
		final int dd = (individual.getDeathDate() == null ? 9999 : individual.getDeathDate().toLocalDate().getYear());
		statement1.setLong(3, dd);
//		ResultSet rs1 = statement1.executeQuery();
		ResultSet rs2;
		String string = "";

		List<CensusIndividual> cil = null;
		cil = CensusIndividual.loadFromDatabase(string, string, string, string);

		// Add all household members as source details
		PreparedStatement statement2 = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);

		for (final CensusIndividual ci : cil) {
			statement2.setString(1, ci.getKIPnr());
			statement2.setString(2, ci.getHusstands_familienr());
			rs2 = statement2.executeQuery();

			final StringBuffer sb = new StringBuffer();

			while (rs2.next()) {
				sb.append(rs2.getString("KILDENAVN") + ", " + rs2.getString("ALDER") + ", "
						+ rs2.getString("CIVILSTAND") + ", " + rs2.getString("KILDEERHVERV") + ", "
						+ rs2.getString("STILLING_I_HUSSTANDEN") + " - ");
			}

			string = sb.toString();

			if (string.length() > 4096) {
				string = string.substring(0, 4095);
			}

			ci.setKildedetaljer(string);
		}

		statement2.close();

		if ((cil != null) && (cil.size() > 0)) {
			writeCensusOutput(cil, args, individual);
		}
	}

	/**
	 * Search Police Registry
	 *
	 * @param args
	 * @param conn
	 * @param individual
	 * @throws Exception
	 */
	private void searchPoliceRegistry(String[] args, Connection conn, DBIndividual individual) throws Exception {
		final String outName = args[3] + "/" + individual.getName() + "_polreg.csv";
		String result = "";
		int calcYear = 0;

		PreparedStatement statement1 = conn.prepareStatement(SELECT_POLICE_PERSON);
		statement1.setString(1, individual.getPhonName());
		ResultSet rs1 = statement1.executeQuery();
		ResultSet rs2, rs3;
		final List<Integer> li = new ArrayList<>();
		final List<Integer> lb = new ArrayList<>();
		final List<String> ls = new ArrayList<>();
		final List<String> lp = new ArrayList<>();
		String day;
		String month;
		String year;
		counter = 0;

		while (rs1.next()) {
			li.add(rs1.getInt("ID"));
			ls.add(getField(rs1, "FIRSTNAMES") + " " + getField(rs1, "LASTNAME"));
			try {
				lb.add(rs1.getInt("BirthDate"));
			} catch (final Exception e) {
				lb.add(0);
			}
		}

		PreparedStatement statement2 = conn.prepareStatement(SELECT_POLICE_POSITION);

		for (int i = 0; i < li.size(); i++) {
			statement2.setLong(1, li.get(i));
			rs2 = statement2.executeQuery();

			if (rs2.next()) {
				lp.add(getField(rs2, "POSITION_DANISH"));
			} else {
				lp.add(" ");
			}
		}

		PreparedStatement statement3 = conn.prepareStatement(SELECT_POLICE_ADDRESS);

		for (int i = 0; i < li.size(); i++) {
			statement3.setLong(1, li.get(i));
			rs3 = statement3.executeQuery();

			while (rs3.next()) {
				if (individual.getBirthDate().before(Date.valueOf("1000-01-01"))) {
					calcYear = 0;
				} else {
					try {
						calcYear = individual.getBirthDate().toLocalDate().getYear() - lb.get(i);
					} catch (final Exception e) {
						calcYear = 0;
					}
				}

				if ((calcYear > 2) || (calcYear < -2)) {
					continue;
				}

				day = getFieldInt(rs3, "DAY");
				month = getFieldInt(rs3, "MONTH");
				year = getFieldInt(rs3, "XYEAR");

				result = ls.get(i) + ";" + lb.get(i) + ";" + lp.get(i) + ";" + getField(rs3, "STREET") + ";"
						+ getField(rs3, "NUMBER") + ";" + getField(rs3, "LETTER") + ";" + getField(rs3, "FLOOR") + ";"
						+ getField(rs3, "PLACE") + ";" + getField(rs3, "HOST") + ";" + day + ";" + month + ";" + year
						+ ";" + day + "-" + month + "-" + year + ";" + getField(rs3, "FULL_ADDRESS") + "\n";

				if (counter == 0) {
					bw = new BufferedWriter(new FileWriter(new File(outName)));
					bw.write(POLREG_HEADER);
				}

				bw.write(result);
				counter++;
			}
		}

		statement3.close();
		statement2.close();
		statement1.close();

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
	 * @param conn
	 * @throws Exception
	 */
	private void searchProbates(String[] args, Connection conn, DBIndividual individual) throws Exception {
		String singleLine;
		final HashSet<String> outLines = new HashSet<>();
		String coveredData;
		String source;
		String name;
		int counter = 0;

		final String phonName = new Fonkod().generateKey(args[4]);
		final String BirthDate = (args.length < 6 ? "0001" : args[5]);
		final String DeathDate = (args.length < 7 ? "9999" : args[6]);

		PreparedStatement statement = conn.prepareStatement(SELECT_PROBATE);
		statement.setString(1, phonName);
		statement.setString(2, BirthDate + "-01-01");
		statement.setString(3, DeathDate + "-12-31");
		ResultSet rs = statement.executeQuery();

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

		rs.close();
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
	private void searchRelocations(String[] args, Connection conn, DBIndividual individual)
			throws SQLException, IOException {
		final String outName = args[3] + "/" + individual.getName() + "_flyt.csv";
		counter = 0;
		Relocation relocation;
		final List<Relocation> lr = new ArrayList<>();

		PreparedStatement statement = conn.prepareStatement(SELECT_RELOCATION);
		statement.setString(1, individual.getPhonName());
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			relocation = new Relocation(
					(rs.getString(1) == null ? "" : rs.getString(1).replace("I", "").replace("@", "").trim()),
					(rs.getString(2) == null ? "" : rs.getString(2).trim()),
					(rs.getString(3) == null ? "" : rs.getString(3).trim()), rs.getDate(4),
					(rs.getString(5) == null ? "" : rs.getString(5).trim()),
					(rs.getString(6) == null ? "" : rs.getString(6).trim()),
					(rs.getString(7) == null ? "" : rs.getString(7).trim()),
					(rs.getString(8) == null ? "" : rs.getString(8).trim()));
			lr.add(relocation);
		}

		rs.close();
		statement.close();

		// Find relocation dates outside the life span of the individual to
		// eliminate

		final List<String> ls = new ArrayList<>();

		for (final Relocation relocation2 : lr) {
			if ((relocation2.getRelocationDate().before(individual.getBirthDate()))
					|| ((individual.getDeathDate() != null)
							&& (relocation2.getRelocationDate().after(individual.getDeathDate())))) {
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

		statement = conn.prepareStatement(SELECT_BIRTHDATE);

		for (final Relocation relocation2 : lr) {
			statement.setString(1, "@I" + relocation2.getId() + "@");
			rs = statement.executeQuery();

			if (rs.next()) {
				relocation2.setBirthDate(rs.getDate("DATE"));
			}

		}

		rs.close();
		statement.close();

		// Sort and write out remaining records

		Collections.sort(lr, new RelocationComparator());

		for (final Relocation relocation2 : lr) {

			if (counter == 0) {
				bw = new BufferedWriter(new FileWriter(new File(outName)));
				bw.write(RELOCATION_HEADER + "\n");
			}

			bw.write(relocation2.toString().replace("NULL", "") + "\n");
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
				diff = individual.getBirthDate().toLocalDate().getYear() - ci.getFoedeaar();
			} else {
				if (ci.getFoedt_kildedato().length() > 0) {
					matcher = pattern.matcher(ci.getFoedt_kildedato());

					if (matcher.find()) {
						diff = individual.getBirthDate().toLocalDate().getYear() - Integer.parseInt(matcher.group(0));
					}
				} else {
					if (ci.getAlder() > 0) {
						diff = ci.getFTaar() - individual.getBirthDate().toLocalDate().getYear() - ci.getAlder();
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
