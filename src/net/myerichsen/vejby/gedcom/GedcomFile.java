package net.myerichsen.vejby.gedcom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * Singleton class representing a GEDCOM file.
 * 
 * @version 28. aug. 2020
 * @author Michael Erichsen
 */
public class GedcomFile {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static GedcomFile single_instance = null;

	/**
	 * Static method to create instance of Singleton class.
	 * 
	 * @return An instance of the class
	 */
	public static GedcomFile getInstance() {
		if (single_instance == null) {
			single_instance = new GedcomFile();
		}

		return single_instance;
	}

	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private List<Family> families;

	/**
	 * Constructor
	 *
	 */
	private GedcomFile() {
		super();
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
	public String save(Census censusTable) {
		FileFilter ff = new FileNameExtensionFilter("GEDCOM fil", "ged");
		JFileChooser gedcomChooser = new JFileChooser(prefs.get("GEDCOMFILENAME", "."));
		String path = "";

		gedcomChooser.setFileFilter(ff);

		int returnValue = gedcomChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File gedcomFile = gedcomChooser.getSelectedFile();
			String fileName = gedcomFile.getName();
			if (!fileName.endsWith(".ged")) {
				gedcomFile = new File(fileName + ".ged");
			}
			prefs.put("GEDCOMFILENAME", gedcomFile.getPath());

			OutputStreamWriter fw = null;
			try {
				fw = new OutputStreamWriter(new FileOutputStream(gedcomFile), "ANSEL");

				writeHeader(fw);

				int familyId = 1;

				for (Family family : censusTable.getFamilies()) {
					if (family.getFamilyId() == 0) {
						for (Individual person : family.getSingles()) {
							// TODO Debug
							LOGGER.log(Level.FINE, "Writing GEDCOM for person " + person.getId() + ", " + person);
							fw.write(person.toGedcom());
							LOGGER.log(Level.FINE, "Family " + family.getHouseholdId() + ", " + family.getFamilyId()
									+ ", " + person.toString());
						}
					} else {
						fw.write(family.toGedcom(familyId++));
						LOGGER.log(Level.FINE, "Family " + family.getHouseholdId() + ", " + family.getFamilyId() + ", "
								+ family.toString());
					}
				}

				writeCensusTrailer(fw);
				fw.close();
				path = gedcomFile.getPath();
				LOGGER.log(Level.INFO, "Data gemt som GEDCOM fil " + path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return path;
	}

	/**
	 * Save a family as GEDCOM. Used by church registry birth.
	 * 
	 * @param family The family to save
	 */
	public void save(Family family) {
		FileFilter ff = new FileNameExtensionFilter("GEDCOM fil", "ged");
		JFileChooser gedcomChooser = new JFileChooser(prefs.get("GEDCOMFILENAME", "."));

		gedcomChooser.setFileFilter(ff);

		int returnValue = gedcomChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File gedcomFile = gedcomChooser.getSelectedFile();
			String fileName = gedcomFile.getName();
			if (!fileName.endsWith(".ged")) {
				gedcomFile = new File(fileName + ".ged");
			}
			prefs.put("GEDCOMFILENAME", gedcomFile.getPath());

			OutputStreamWriter fw = null;
			try {
				fw = new OutputStreamWriter(new FileOutputStream(gedcomFile), "ANSEL");

				writeHeader(fw);

				fw.write(family.toGedcom(1));
				LOGGER.log(Level.FINE, family.toString());

				writeChurchRegistryTrailer(fw);
				fw.close();
				LOGGER.log(Level.INFO, "Data gemt som GEDCOM fil " + gedcomFile.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param families the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	/**
	 * Write a census GEDCOM trailer.
	 * 
	 * @param fw File writer
	 * @throws IOException
	 */
	private void writeCensusTrailer(final OutputStreamWriter fw) throws IOException {
		// Source for places
		fw.write("0 @S1@ SOUR\n");
		fw.write("1 TITL DDD Folketællinger. " + "Kildeindtastningsprojektet.\n");
		fw.write("1 AUTH Statens Arkiver\n");

		// Trailer
		fw.write("0 TRLR\n");
	}

	/**
	 * Write a church registry GEDCOM trailer.
	 * 
	 * @param fw File writer
	 * @throws IOException
	 */
	private void writeChurchRegistryTrailer(final OutputStreamWriter fw) throws IOException {
		// Source for places
		fw.write("0 @S1@ SOUR\n");
		fw.write("1 TITL Kirkebog\n");
		fw.write("1 AUTH Arkivalier Online\n");

		// Trailer
		fw.write("0 TRLR\n");
	}

	/**
	 * Write a GEDCOM header.
	 * 
	 * @param fw Filewriter
	 * @throws IOException
	 */
	private void writeHeader(final OutputStreamWriter fw) throws IOException {
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
		fw.write("1 FILE Folketaelling.ged\n");
		fw.write("0 @SUB1@ SUBM\n");
		fw.write("1 NAME Dansk Demografisk Database\n");
		fw.write("1 ADDR Rigsarkivet, Jernbanegade 36, 5000 Odense C\n");
	}
}
