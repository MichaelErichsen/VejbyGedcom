package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * This panel reads a marriage query result from family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 * 
 * @author Michael Erichsen
 * @version 04-09-2020
 *
 */
public class MarriagePanel extends JPanel {
	private static final long serialVersionUID = -4314220214428910383L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private String[][] marriageArray = new String[100][6];

	private JTable table;
	private JButton saveButton;

	/**
	 * Create the panel.
	 */
	public MarriagePanel() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		JButton openButton = new JButton("\u00C5ben Family Search eksport fil");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openTsvFile();
			}
		});
		buttonPanel.add(openButton);

		saveButton = new JButton("Gem som Gedcom");
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsGedcom();
			}
		});
		buttonPanel.add(saveButton);
	}

	/**
	 * Open a tsv file from Family Search and reduce it to relevant columns
	 */
	protected void openTsvFile() {
		FileFilter ff = new FileNameExtensionFilter("FS eksport fil (TSV)", "tsv");
		String fsFileName = prefs.get("FSFILENAME", ".");

		JFileChooser fsChooser = new JFileChooser(fsFileName);
		fsChooser.setFileFilter(ff);

		int returnValue = fsChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File fsFile = fsChooser.getSelectedFile();
			LOGGER.log(Level.INFO, fsFile.getPath());
			prefs.put("KIPFILENAME", fsFile.getPath());

			/**
			 * These are the headers:
			 * 
			 * queryUrl, queryTime, score, arkId, sourceMediaType, batchNumber,
			 * roleInRecord, relationshipToHead, fullName, sex, birthLikeDate,
			 * birthLikePlaceText, chrDate, chrPlaceText, residenceDate, residencePlaceText,
			 * marriageLikeDate, marriageLikePlaceText, deathLikeDate, deathLikePlaceText,
			 * burialDate, burialPlaceText, fatherFullName, motherFullName, spouseFullName,
			 * parentFullNames, childrenFullNames, otherFullNames, otherEvents
			 * 
			 * The interesting columns are:
			 * 
			 * 8 fullName, 9 sex ["male"|""], 10 birthLikeDate, 16 marriageLikeDate, 17
			 * marriageLikePlaceText, 24 spouseFullName,
			 * 
			 */

			String[] columns;
			String[] headerArray = new String[6];

			try {
				FileInputStream fis = new FileInputStream(fsFile);
				Scanner sc = new Scanner(fis);

				// First line contains headers
				columns = sc.nextLine().split("\t");

				headerArray[0] = columns[8];
				headerArray[1] = columns[9];
				headerArray[2] = columns[10];
				headerArray[3] = columns[16];
				headerArray[4] = columns[17];
				headerArray[5] = columns[24];

				// Ignore second line
				sc.nextLine();

				// The rest of the lines contain data

				int i = 0;

				while (sc.hasNext()) {
					columns = sc.nextLine().split("\t");

					String s = columns[8];
					byte[] a = null;

					try {
						a = s.getBytes("ISO-8859-1");
						s = new String(a, "UTF-8");

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					marriageArray[i][0] = s;
					marriageArray[i][1] = columns[9];
					marriageArray[i][2] = columns[10];
					marriageArray[i][3] = columns[16];
					marriageArray[i][4] = columns[17];

					s = columns[24];
					a = null;

					try {
						a = s.getBytes("ISO-8859-1");
						s = new String(a, "UTF-8");

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					marriageArray[i][5] = s;
					i++;
				}

				sc.close();
				fis.close();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			DefaultTableModel model = new DefaultTableModel(marriageArray, headerArray);
			table.setModel(model);
			saveButton.setEnabled(true);
		}

	}

	/**
	 * 
	 * Instantiate a GedcomFile object and populate it with the marriage data.
	 * Choose a file name and save it.
	 */
	protected void saveAsGedcom() {
		GedcomFile gedcomFile = GedcomFile.getInstance();
		Family family;
		Individual groom = null;
		Individual bride = null;

		int individualId = 1;
		String[] line = new String[5];

		// Add a family object for each marriage in the array
		for (int i = 0; i < marriageArray.length; i++) {
			line = marriageArray[i];
			LOGGER.log(Level.FINE, i + ", " + line[0] + ", " + line[1] + ", " + line[2] + ", " + line[3] + ", "
					+ line[4] + ", " + line[5]);

			family = new Family(0, i);

			// Test for principal's sex
			if (line[1].equals("male")) {
				groom = new Individual(individualId++);
				groom.setName(line[0]);

				if (!line[2].equals("")) {
					groom.setBirthDate(line[2]);
				}

				bride = new Individual(individualId++);
				bride.setName(line[5]);
			} else {
				bride = new Individual(individualId++);
				bride.setName(line[0]);

				if (!line[2].equals("")) {
					bride.setBirthDate(line[2]);
				}

				groom = new Individual(individualId++);
				groom.setName(line[5]);
			}

			groom.setSex("M");
			bride.setSex("F");
			family.setFather(groom);
			family.setMother(bride);
			family.setMarriageDate(line[3]);
			family.setMarriagePlace(line[4]);

			gedcomFile.addFamily(family);
		}

		String path = gedcomFile.saveMarriage();

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Vielser er gemt som GEDCOM fil " + path, "Vejby Gedcom",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
