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
 * This panel reads a burial query result from Family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 *
 * @author Michael Erichsen
 * @version 11-09-2020
 *
 */
public class BurialPanel extends FsPanel {
	private static final long serialVersionUID = 3673964025732718748L;

	/**
	 * Create the panel.
	 */
	public BurialPanel() {
		dataArray = new String[0][5];

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
		eliminateButton.setEnabled(false);
		eliminateButton.addActionListener(e -> eliminateDuplicates());
		buttonPanel.add(eliminateButton);

		saveButton = new JButton("Gem som Gedcom");
		saveButton.setEnabled(false);
		saveButton.addActionListener(e -> saveAsGedcom());
		buttonPanel.add(saveButton);
	}

	/**
	 * Open one or more tsv files from Family Search and reduce them to the
	 * relevant columns:
	 *
	 * fullName 8 sex 9 birthLikeDate 10 deathLikeDate 18 deathLikePlaceText 19
	 * burialDate 20 burialPlaceText 21 fatherFullName 22 motherFullName 23
	 * spouseFullName 24
	 */
	@Override
	protected void openTsvFile() {
		Scanner sc;
		String[] columnLabels;
		String[] columns;
		headerArray = new String[5];
		FileInputStream fis;
		final FileFilter ff = new FileNameExtensionFilter("FS eksport fil (TSV)", "tsv");
		final String fsFileName = prefs.get("FSFILENAME", ".");
		String[][] burialArray = new String[100][5];
		// String[] c;

		final JFileChooser fsChooser = new JFileChooser(fsFileName);
		fsChooser.setFileFilter(ff);
		fsChooser.setMultiSelectionEnabled(true);

		final int returnValue = fsChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			final File[] fsFiles = fsChooser.getSelectedFiles();

			// Save the first or only file name
			fileNameStub = fsFiles[0].getName().replaceFirst("[.][^.]+$", "");
			LOGGER.log(Level.FINE, fsFiles[0].getPath());
			prefs.put("KIPFILENAME", fsFiles[0].getPath());

			for (int fileNo = 0; fileNo < fsFiles.length; fileNo++) {
				try {
					burialArray = new String[100][5];

					fis = new FileInputStream(fsFiles[fileNo]);
					sc = new Scanner(fis);

					// First line contains headers. Neds only to be read for the
					// first file
					columnLabels = sc.nextLine().split("\t");

					if (fileNo == 0) {
						headerArray[0] = columnLabels[8];
						headerArray[1] = columnLabels[9];
						headerArray[2] = columnLabels[10];
						headerArray[3] = columnLabels[18];
						headerArray[4] = columnLabels[19];
					}

					// Ignore second line
					sc.nextLine();

					// The rest of the lines contain data

					int i = 0;

					while (sc.hasNext()) {
						columns = sc.nextLine().split("\t");

						burialArray[i][0] = fixCodePage(columns, 8);
						burialArray[i][1] = columns[9];
						burialArray[i][2] = columns[10];
						burialArray[i][3] = columns[18];
						burialArray[i][4] = fixCodePage(columns, 19);
						i++;
					}

					sc.close();
					fis.close();
				} catch (final FileNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				} catch (final IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}

				dataArray = concatenate(dataArray, burialArray);

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
	 * Instantiate a GedcomFile object and populate it with the burial data.
	 * Choose a file name and save it.
	 */
	@Override
	protected void saveAsGedcom() {
		final GedcomFile gedcomFile = new GedcomFile();
		Family family;
		Individual deceased = null;

		int individualId = 1;
		String[] line = new String[5];

		// Add a family object for each burial in the array
		for (int i = 0; i < dataArray.length; i++) {
			line = dataArray[i];

			family = new Family(0, i);

			deceased = new Individual(individualId++);
			final String deceasedName = line[0];

			if (deceasedName == null) {
				continue;
			}

			final String[] deceasedNamePart = deceasedName.split(" ");

			// Handle cases with and without surnames
			if (deceasedNamePart.length == 1) {
				deceased.setName(deceasedName + " ?");
			} else {
				if (deceasedName.endsWith("sen") || deceasedName.endsWith("datter") || deceasedName.endsWith("dtr")
						|| deceasedName.endsWith("d") || deceasedName.endsWith("D") || deceasedName.endsWith("son")) {
					deceased.setName(deceasedName);
				} else {
					deceased.setName(deceasedName + " ?");
				}
			}

			deceased.setSex(line[1].equals("male") ? "M" : "F");
			deceased.setBirthDate(line[2]);
			deceased.setDeathDate(line[3]);
			deceased.setDeathPlace(line[4]);

			if (deceased.getSex().equals("M")) {
				family.setFather(deceased);
			} else {
				family.setMother(deceased);
			}

			gedcomFile.addFamily(family);
		}

		final String path = gedcomFile.saveFsExtract(fileNameStub);

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), individualId - 1 + " dødsfald er gemt som GEDCOM fil " + path,
					"Vejby Gedcom", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
