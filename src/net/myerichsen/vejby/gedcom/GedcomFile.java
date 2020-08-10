package net.myerichsen.vejby.gedcom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * 
 * @author michael
 *
 */
public class GedcomFile {
	// static variable single_instance of type Singleton
	private static GedcomFile single_instance = null;

	private List<Family> families;

	private GedcomFile() {
		super();
		setFamilies(new ArrayList<Family>());
	}

	// static method to create instance of Singleton class
	public static GedcomFile getInstance() {
		if (single_instance == null)
			single_instance = new GedcomFile();

		return single_instance;
	}

	/**
	 * @return the families
	 */
	public List<Family> getFamilies() {
		return families;
	}

	/**
	 * @param families
	 *            the families to set
	 */
	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	public void addFamily(Family family) {
		families.add(family);
	}

	public void print(File gedcomFile) throws Exception {
		OutputStreamWriter fw = null;
		try {
			// Requires AnselCharset-1.0.jar on classpath
			fw = new OutputStreamWriter(new FileOutputStream(gedcomFile), "ANSEL");

			writeHeader(fw);

			for (Family family : families) {
				fw.write(family.toString());
				System.out.print(family.toString());
			}

			writeTrailer(fw);
			fw.close();
		} catch (final IOException e) {
			throw new Exception(e);
		}

	}

	/**
	 * @param fw
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

	/**
	 * @param fw
	 * @throws IOException
	 */
	private void writeTrailer(final OutputStreamWriter fw) throws IOException {
		// Source for places
		fw.write("0 @S1@ SOUR\n");
		fw.write("1 TITL DDD Folketællinger. " + "Kildeindtastningsprojektet.\n");
		fw.write("1 AUTH Statens Arkiver\n");

		// Trailer
		fw.write("0 TRLR\n");
	}

	/**
	 * @return
	 */
	public static GedcomFile getFile() {
		return null;
		// TODO Auto-generated method stub
	}
}
