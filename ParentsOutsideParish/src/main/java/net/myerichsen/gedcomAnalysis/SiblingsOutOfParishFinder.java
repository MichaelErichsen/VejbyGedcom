package net.myerichsen.gedcomAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gedcom4j.exception.GedcomParserException;

/**
 * Find common ancestors in census entries in a GEDCOM
 *
 * Individual lines are parsed into an object with birth and first census
 *
 * Analyse census records
 *
 * Find one or both parents in the entry, if present
 *
 * For each other individual born in the same parish
 *
 * Get first census entry
 *
 * Compare parents
 *
 * If a match is found, list both individuals with their numbers
 *
 * @author Michael Erichsen, 2026
 */
public class SiblingsOutOfParishFinder {
	private static Logger log = Logger.getLogger("SiblingsOutOfParishFinder");

	private static String parish;

	/**
	 * Main method
	 *
	 * @param args GEDCOM file and parish name
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: SiblingsOutOfParishFinder <gedcomfile> <parish>");
			System.exit(4);
		}

		parish = args[1];
		final var cpf = new SiblingsOutOfParishFinder();

		try {
			cpf.execute(args);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Extract data from lines into individual object
	 *
	 * @param lineList A list of lines from the GEDCOM, delineated by "0 @I..."
	 */
	private Individual createIndividualObject(List<String> lineList) {
		final var idPattern = Pattern.compile("0 @I(\\d*)@ INDI");
		final var namePattern = Pattern.compile("1 NAME\\s(.*)");
		final var sexPattern = Pattern.compile("1 SEX\\s(.*)");
		final var birthPattern = Pattern.compile("1 BIRT");
		final var datePattern = Pattern.compile("2 DATE\\s(.*)");
		final var placePattern = Pattern.compile("2 PLAC\\s(.*)");
		final var censusPattern = Pattern.compile("1 CENS");
		final var pagePattern = Pattern.compile("3 PAGE\\s(.*)");
		final var conPattern = Pattern.compile("4 CON.\\s(.*)");
		final var individual = new Individual();
		String line;
		Matcher matcher, matcher2, matcher3;
		Birth birth;
		CensusRecord censusRecord;

		for (var i = 0; i < lineList.size(); i++) {
			line = lineList.get(i);
			log.fine(line);

			matcher = idPattern.matcher(line);

			if (matcher.find()) {
				individual.setId(matcher.group(1));
				continue;
			}

			matcher = namePattern.matcher(line);

			if (matcher.find()) {
				individual.setName(matcher.group(1));
				continue;
			}

			matcher = sexPattern.matcher(line);

			if (matcher.find()) {
				individual.setSex(matcher.group(1));
				continue;
			}

			matcher = birthPattern.matcher(line);

			if (matcher.find()) {
				birth = new Birth();

				matcher2 = datePattern.matcher(lineList.get(i + 1));

				if (matcher2.find()) {
					birth.setDateString(matcher2.group(1));
				}

				matcher3 = placePattern.matcher(lineList.get(i + 3));

				if (matcher3.find()) {
					birth.setPlace(matcher3.group(1));
				} else {
					matcher3 = placePattern.matcher(lineList.get(i + 2));

					if (matcher3.find()) {
						birth.setPlace(matcher3.group(1));
					}

					if (individual.getBirth().getPlace() != null && individual.getBirth().getPlace().contains(parish)) {
						return null;
					}
				}

				individual.setBirth(birth);
				continue;
			}

			matcher = censusPattern.matcher(line);

			if (matcher.find()) {
				censusRecord = new CensusRecord();

				matcher2 = datePattern.matcher(lineList.get(i + 1));

				if (matcher2.find()) {
					censusRecord.setDateString(matcher2.group(1));
				}

				matcher3 = placePattern.matcher(lineList.get(i + 3));

				if (matcher3.find()) {
					censusRecord.setPlace(matcher3.group(1));
				} else {
					matcher3 = placePattern.matcher(lineList.get(i + 2));

					if (matcher3.find()) {
						censusRecord.setPlace(matcher3.group(1));
					}
				}

				if (individual.getCensusRecord().getPlace() != null
						&& individual.getCensusRecord().getPlace().contains(parish)) {
					return null;
				}

				// TODO Must loop over CONC and CONT records and test PAGE+CONC og CONT+CONC for
				// fathers and mothers. Line + 7???
				matcher2 = pagePattern.matcher(line);

				if (matcher2.find()) {
					censusRecord.setPage(matcher2.group(1));
				}

				individual.setCensusRecord(censusRecord);
				break;
			}

//			3 PAGE Povel Pedersen, 39, Gift, Hosbonde, M, Huusmand med Jord og Hukker,
//			4 CONC M²nge, 7de Familie, Opslag 27149|13, , 366, 1, B3532

//			4 CONT Karen J²rgensdatter, 31, Gift, hans Kone, K, , M²nge, 7de Familie,
//			4 CONC Opslag 27149|13, , 367, 1, B3532

//			4 CONT Peder Povelsen, 10, -, deres B²rn, M, , M²nge, 7de Familie, Opslag
//			4 CONC 27149|13, , 368, , B3532

//			4 CONT Cisse Povelsdatter, 5, -, deres B²rn, K, , M²nge, 7de Familie, Opslag
//			4 CONC 27149|13, , 369, , B3532

//			4 CONT Anne Katrine Povelsdatter, 3, -, deres B²rn, K, , M²nge, 7de Familie,
//			4 CONC Opslag 27149|13, , 370, , B3532
		}

		log.info(individual.toString());
		return individual;
	}

	/**
	 * Worker method
	 *
	 * @param args A GEDCOM file and a parish
	 * @throws GedcomParserException
	 * @throws IOException
	 *
	 */
	private void execute(String[] args) throws IOException {
		final var br = new BufferedReader(new FileReader(args[0]));
		final List<String> lineList = new ArrayList<>();
		final List<Individual> individualList = new ArrayList<>();
		String line;
		Individual individual;

		// Discard header lines
		while ((line = br.readLine()) != null) {
			if (line.contains("0 @I")) {
				break;
			}
		}

		log.fine(line);

		// Read each individual as lines into a list
		lineList.add(line);

		while ((line = br.readLine()) != null) {
			// Collect lines for an object
			lineList.add(line);

			if (line.contains("0 @I")) {
				individual = createIndividualObject(lineList);

				if (individual != null) {
					individualList.add(individual);
				}
				lineList.clear();
				lineList.add(line);
			}

			// Family and other records follow individual records and are ignored
			if (line.contains("0 @F")) {
				break;
			}

		}

		log.info(individualList.size() + " personer");

		br.close();
	}
}