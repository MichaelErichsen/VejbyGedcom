package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

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
 * This panel reads a marriage query result from Family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 *
 * @author Michael Erichsen
 * @version 11-09-2020
 *
 */
public class MarriagePanel extends FsPanel {
	private static final long serialVersionUID = -4314220214428910383L;

	/**
	 * Create the panel.
	 */
	public MarriagePanel() {
		dataArray = new String[0][6];

		setLayout(new BorderLayout(0, 0));

		final JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);

		final JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		final JButton openButton = new JButton("\u00C5ben Family Search eksport fil");
		openButton.addActionListener(e -> openTsvFile());
		buttonPanel.add(openButton);

		eliminateButton = new JButton("Fjern dubletter");
		eliminateButton.addActionListener(e -> eliminateDuplicates());
		eliminateButton.setEnabled(false);
		buttonPanel.add(eliminateButton);

		saveButton = new JButton("Gem som Gedcom");
		saveButton.setEnabled(false);
		saveButton.addActionListener(e -> saveAsGedcom());

		buttonPanel.add(saveButton);
	}

	/**
	 * Open one or more tsv files from Family Search and reduce them to relevant
	 * columns:
	 *
	 * 8 fullName, 9 sex ["male"|""], 10 birthLikeDate, 16 marriageLikeDate, 17
	 * marriageLikePlaceText, 24 spouseFullName,
	 */
	@Override
	protected void openTsvFile() {
		Scanner sc;
		String[] columns;
		headerArray = new String[8];
		FileInputStream fis;
		final FileFilter ff = new FileNameExtensionFilter("FS eksport fil (TSV)", "tsv");
		final String fsFileName = prefs.get("FSFILENAME", ".");
		String[][] marriageArray = new String[100][6];

		final JFileChooser fsChooser = new JFileChooser(fsFileName);
		fsChooser.setFileFilter(ff);
		fsChooser.setMultiSelectionEnabled(true);

		final int returnValue = fsChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			final File[] fsFiles = fsChooser.getSelectedFiles();
			fileNameStub = fsFiles[0].getName().replaceFirst("[.][^.]+$", "");
			LOGGER.log(Level.INFO, fsFiles[0].getPath());
			prefs.put("KIPFILENAME", fsFiles[0].getPath());

			for (int fileNo = 0; fileNo < fsFiles.length; fileNo++) {
				try {
					marriageArray = new String[100][6];

					fis = new FileInputStream(fsFiles[fileNo]);
					sc = new Scanner(fis);

					// First line contains headers. Neds only to be read for the
					// first file
					columns = sc.nextLine().split("\t");

					if (fileNo == 0) {
						headerArray[0] = columns[8];
						headerArray[1] = columns[9];
						headerArray[2] = columns[10];
						headerArray[3] = columns[16];
						headerArray[4] = columns[17];
						headerArray[5] = columns[24];
					}

					// Ignore second line
					sc.nextLine();

					// The rest of the lines contain data

					int i = 0;

					while (sc.hasNext()) {
						columns = sc.nextLine().split("\t");

						marriageArray[i][0] = fixCodePage(columns, 8);
						marriageArray[i][1] = columns[9];
						marriageArray[i][2] = columns[10];
						marriageArray[i][3] = columns[16];
						marriageArray[i][4] = columns[17];
						marriageArray[i][5] = fixCodePage(columns, 24);
						i++;
					}

					sc.close();
					fis.close();
				} catch (final FileNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				} catch (final IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}

				dataArray = concatenate(dataArray, marriageArray);

				LOGGER.log(Level.FINE, "Data array length after concatenation: " + dataArray.length);
			}

			LOGGER.log(Level.INFO, "Data array length after concatenations: " + dataArray.length);
		}

		final DefaultTableModel model = new DefaultTableModel(dataArray, headerArray);
		table.setModel(model);
		saveButton.setEnabled(true);
		eliminateButton.setEnabled(true);
	}

	/**
	 *
	 * Instantiate a GedcomFile object and populate it with the marriage data.
	 * Choose a file name and save it.
	 */
	@Override
	protected void saveAsGedcom() {
		// GedcomFile gedcomFile = GedcomFile.getInstance();
		final GedcomFile gedcomFile = new GedcomFile();
		Family family;
		Individual groom = null;
		Individual bride = null;

		int individualId = 1;
		String[] line = new String[5];

		// Add a family object for each marriage in the array
		for (int i = 0; i < dataArray.length; i++) {
			line = dataArray[i];

			if (line[0] == null) {
				continue;
			}

			LOGGER.log(Level.INFO, i + ", " + line[0] + ", " + line[1] + ", " + line[2] + ", " + line[3] + ", "
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

		final String path = gedcomFile.saveFsExtract(fileNameStub);

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Vielser er gemt som GEDCOM fil " + path, "Vejby Gedcom",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
