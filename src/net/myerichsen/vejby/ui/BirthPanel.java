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
 * This panel reads a birth query result from Family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 *
 * @author Michael Erichsen
 * @version 11-09-2020
 *
 */
public class BirthPanel extends FsPanel {
	private static final long serialVersionUID = 3673964025732718748L;

	/**
	 * Create the panel.
	 */
	public BirthPanel() {
		dataArray = new String[0][8];

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
	 * fullName 8, sex 9, birthLikeDate 10, birthLikePlaceText 11, chrDate 12,
	 * chrPlaceText 13, fatherFullName 22, motherFullName 23
	 */
	@Override
	protected void openTsvFile() {
		Scanner sc;
		String[] columns;
		headerArray = new String[8];
		FileInputStream fis;
		final FileFilter ff = new FileNameExtensionFilter("FS eksport fil (TSV)", "tsv");
		final String fsFileName = prefs.get("FSFILENAME", ".");
		String[][] birthArray = new String[100][8];

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
					birthArray = new String[100][8];

					fis = new FileInputStream(fsFiles[fileNo]);
					sc = new Scanner(fis);

					// First line contains headers. Neds only to be read for the
					// first file
					columns = sc.nextLine().split("\t");

					if (fileNo == 0) {
						headerArray[0] = columns[8];
						headerArray[1] = columns[9];
						headerArray[2] = columns[10];
						headerArray[3] = columns[11];
						headerArray[4] = columns[12];
						headerArray[5] = columns[13];
						headerArray[6] = columns[22];
						headerArray[7] = columns[23];
					}

					// Ignore second line
					sc.nextLine();

					// The rest of the lines contain data

					int i = 0;

					while (sc.hasNext()) {
						columns = sc.nextLine().split("\t");

						birthArray[i][0] = fixCodePage(columns, 8);
						birthArray[i][1] = columns[9];
						birthArray[i][2] = columns[10];
						birthArray[i][3] = fixCodePage(columns, 11);
						birthArray[i][4] = columns[12];
						birthArray[i][5] = fixCodePage(columns, 13);
						birthArray[i][6] = fixCodePage(columns, 22);
						birthArray[i][7] = fixCodePage(columns, 23);
						i++;
					}

					sc.close();
					fis.close();
				} catch (final FileNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				} catch (final IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}

				dataArray = concatenate(dataArray, birthArray);

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
	 * Instantiate a GedcomFile object and populate it with the birth data.
	 * Choose a file name and save it.
	 */
	@Override
	protected void saveAsGedcom() {
		final GedcomFile gedcomFile = new GedcomFile();
		Family family;
		Individual child = null;
		Individual father = null;
		Individual mother = null;

		int individualId = 1;
		String[] line = new String[8];

		// Add a family object for each birth in the array
		for (int i = 0; i < dataArray.length; i++) {
			line = dataArray[i];

			family = new Family(0, i);

			child = new Individual(individualId++);

			final String childName = line[0];

			if (childName == null) {
				continue;
			}

			final String[] childNamePart = childName.split(" ");

			// Handle cases with and without child surnames
			if (childNamePart.length == 1) {
				child.setName(childName + " ?");
			} else {
				if (childName.endsWith("sen") || childName.endsWith("datter") || childName.endsWith("dtr")
						|| childName.endsWith("d") || childName.endsWith("D") || childName.endsWith("son")) {
					child.setName(childName);
				} else {
					child.setName(childName + " ?");
				}
			}

			child.setSex(line[1].equals("male") ? "M" : "F");
			child.setBirthDate(line[2]);
			child.setBirthPlace(line[3]);
			child.setChristeningDate(line[4]);
			child.setChristeningPlace(line[5]);
			family.getChildren().add(child);

			if (!line[6].equals("")) {
				father = new Individual(individualId++);
				father.setName(line[6]);
				father.setSex("M");
				family.setFather(father);
			}

			if (!line[7].equals("")) {
				mother = new Individual(individualId++);
				mother.setName(line[7]);
				mother.setSex("F");
				family.setMother(mother);
			}

			gedcomFile.addFamily(family);
		}

		final String path = gedcomFile.saveFsExtract(fileNameStub);

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Fødsler er gemt som GEDCOM fil " + path, "Vejby Gedcom",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
