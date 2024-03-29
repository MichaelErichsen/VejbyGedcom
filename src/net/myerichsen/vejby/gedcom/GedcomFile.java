package net.myerichsen.vejby.gedcom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.myerichsen.vejby.census.Census;

/**
 * A class representing a GEDCOM file.
 *
 * @version 05-09-2020
 * @author Michael Erichsen
 */
public class GedcomFile {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private List<Family> families;

	/**
	 * Constructor
	 *
	 */
	public GedcomFile() {
		setFamilies(new ArrayList<Family>());
	}

	/**
	 * @param family The family to add to the GEDCOM file
	 */
	public void addFamily(Family family) {
		families.add(family);
	}

	/**
	 * @return the families in the GEDCOM file
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * Save a census table as GEDCOM. Used by census analysis.
	 *
	 * @param censusTable The census table loaded from a KIP file
	 * @return
	 */
	public String saveCensus(Census censusTable) {
		final FileFilter ff = new FileNameExtensionFilter("GEDCOM fil", "ged");
		final JFileChooser gedcomChooser = new JFileChooser(prefs.get("GEDCOMFILENAME", "."));
		String path = "";

		gedcomChooser.setFileFilter(ff);

		final int returnValue = gedcomChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File gedcomFile = gedcomChooser.getSelectedFile();
			final String fileName = gedcomFile.getName();
			if (!fileName.endsWith(".ged")) {
				gedcomFile = new File(fileName + ".ged");
			}
			prefs.put("GEDCOMFILENAME", gedcomFile.getPath());

			OutputStreamWriter fw = null;
			try {
				fw = new OutputStreamWriter(new FileOutputStream(gedcomFile), "ANSEL");

				fw.write("0 HEAD\n");
				fw.write("1 SOUR VejbyGedcom\n");
				fw.write("2 VERS v 0.1\n");
				fw.write("1 SUBM @SUB1@\n");
				fw.write("1 GEDC\n");
				fw.write("2 VERS 5.5\n");
				fw.write("2 FORM LINEAGE-LINKED\n");
				fw.write("1 DEST GED55\n");

				final Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
				fw.write("1 DATE " + sdf.format(cal.getTime()) + "\n");

				sdf = new SimpleDateFormat("hh:mm");
				fw.write("2 TIME " + sdf.format(cal.getTime()) + "\n");
				fw.write("1 CHAR ANSEL\n");
				fw.write("1 FILE " + fileName + "\n");
				fw.write("0 @SUB1@ SUBM\n");
				fw.write("1 NAME Dansk Demografisk Database\n");
				fw.write("1 ADDR Rigsarkivet, Jernbanegade 36, 5000 Odense C\n");

				int familyId = 1;

				for (final Family family : censusTable.getFamilies()) {
					if (family.getFamilyId() == 0) {
						for (final Individual person : family.getSingles()) {
							LOGGER.log(Level.FINE, "Writing GEDCOM for person " + person.getId() + ", " + person);
							fw.write(person.toGedcom());
							LOGGER.log(Level.FINE, "Family " + family.getHouseholdId() + ", " + family.getFamilyId()
									+ ", " + person.toString());
						}
					} else {
						fw.write(family.toGedcom(familyId));
						familyId++;
						LOGGER.log(Level.FINE, "Family " + family.getHouseholdId() + ", " + family.getFamilyId() + ", "
								+ family.toString());
					}
				}

				// Source for places
				fw.write("1 TITL DDD Folketællinger. " + "Kildeindtastningsprojektet.\n");
				fw.write("1 AUTH Statens Arkiver\n");

				// Trailer
				fw.write("0 TRLR\n");

				fw.close();
				path = gedcomFile.getPath();
				LOGGER.log(Level.INFO, "Data gemt som GEDCOM fil " + path);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return path;
	}

	/**
	 * Save a birth or marriage file as GEDCOM. Used by FS analysis.
	 *
	 * @param fileNameStub Name of input file without extension
	 * @return Save path for GEDCOM file
	 */
	public String saveFsExtract(String fileNameStub) {
		final FileFilter ff = new FileNameExtensionFilter("GEDCOM fil", "ged");
		final JFileChooser gedcomChooser = new JFileChooser(prefs.get("GEDCOMFILENAME", "."));
		gedcomChooser.setSelectedFile(new File(fileNameStub + ".ged"));
		String path = "";

		gedcomChooser.setFileFilter(ff);

		final int returnValue = gedcomChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File gedcomFile = gedcomChooser.getSelectedFile();
			final String fileName = gedcomFile.getName();
			if (!fileName.endsWith(".ged")) {
				gedcomFile = new File(fileName + ".ged");
			}
			prefs.put("GEDCOMFILENAME", gedcomFile.getPath());

			try {
				final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(gedcomFile));

				writer.write("0 HEAD\n");
				writer.write("1 SOUR VejbyGedcom\n");
				writer.write("2 VERS v 0.1\n");
				writer.write("1 SUBM @SUB1@\n");
				writer.write("1 GEDC\n");
				writer.write("2 VERS 5.5\n");
				writer.write("2 FORM LINEAGE-LINKED\n");
				writer.write("1 DEST GED55\n");

				final Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
				writer.write("1 DATE " + sdf.format(cal.getTime()) + "\n");

				sdf = new SimpleDateFormat("hh:mm");
				writer.write("2 TIME " + sdf.format(cal.getTime()) + "\n");
				writer.write("1 CHAR ANSEL\n");
				writer.write("1 FILE " + fileName + "\n");
				writer.write("0 @SUB1@ SUBM\n");
				writer.write("1 NAME Intellectual Reserve, Inc.\n");
				writer.write("1 ADDR Salt Lake City, USA\n");

				Family family;
				String s = "";

				for (int i = 0; i < families.size(); i++) {
					family = families.get(i);
					LOGGER.log(Level.FINE, "Family " + i);
					s = family.toGedcom(i);
					writer.write(s);
					LOGGER.log(Level.FINE, "Family " + family.getHouseholdId() + ", " + family.getFamilyId() + ", "
							+ family.toString());
				}

				// Source for marriages
				writer.write("0 @S1@ SOUR\n");
				writer.write("1 TITL FamilySearch (https://familysearch.org/)\n");
				writer.write("1 AUTH Intellectual Reserve, Inc.\n");

				// Trailer
				writer.write("0 TRLR\n");
				writer.close();
				path = gedcomFile.getPath();
				LOGGER.log(Level.INFO, "Data gemt som GEDCOM fil " + path);
			} catch (final Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				return "";
			}
		}

		return path;
	}

	/**
	 * @param families the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

}
