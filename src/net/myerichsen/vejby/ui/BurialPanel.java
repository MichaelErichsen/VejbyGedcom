package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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
 * This panel reads a burial query result from family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 * 
 * @author Michael Erichsen
 * @version 09-09-2020
 *
 */
public class BurialPanel extends JPanel {
	private static final long serialVersionUID = 3673964025732718748L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private String[][] dataArray = new String[0][5];
	private String[] headerArray;

	private JTable table;
	private JButton saveButton;
	private String fileNameStub;
	private JButton eliminateButton;

	/**
	 * Create the panel.
	 */
	public BurialPanel() {
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

		eliminateButton = new JButton("Fjern dubletter");
		eliminateButton.setEnabled(false);
		eliminateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eliminateDuplicates();
			}
		});
		buttonPanel.add(eliminateButton);

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
	 * @param j
	 */
	private void clearRow(int i) {
		for (int j = 0; j < dataArray[i].length; j++) {
			dataArray[i][j] = "";
		}
	}

	/**
	 * Concatenate two arrays. Copied from
	 * https://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	private <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	/**
	 * Eliminate duplicates in the data array.
	 * 
	 * For each row in the data array:
	 * 
	 * Compare all fields with all subsequent rows. If identical, then remove
	 * subsequent row.
	 */
	private void eliminateDuplicates() {
		int deletions = 0;
		String stringI = "";
		String stringJ = "";

		for (int i = 0; i < dataArray.length; i++) {
//			LOGGER.log(Level.INFO, "Række: " + i + "; " + dataArray[i][0]);

			if ((dataArray[i][0] == null) || (dataArray[i][0].equals(""))) {
				continue;
			}
			stringI = listDataArray(i);

			for (int j = i + 1; j < dataArray.length; j++) {
//				LOGGER.log(Level.INFO, "Række: " + j + "; " + dataArray[j][0]);
				if ((dataArray[j][0] == null) || (dataArray[j][0].equals(""))) {
					continue;
				}
				stringJ = listDataArray(j);

				if (stringI.equals(stringJ)) {
					LOGGER.log(Level.FINE, "Fundet " + i + ": " + stringI);
					LOGGER.log(Level.FINE, "Fundet " + i + ": " + stringJ);
					clearRow(j);
					deletions++;
				}
			}

		}

		String[][] dataArray2 = new String[dataArray.length - deletions][5];

		int i2 = 0;
		for (String[] element : dataArray) {
			if ((element[0] != null) && (!element[0].equals(""))) {
				dataArray2[i2++] = element;
			}
		}

		LOGGER.log(Level.INFO, "Data array efter sletning af " + deletions + " rækker: " + dataArray2.length);

		dataArray = dataArray2;

		DefaultTableModel model = new DefaultTableModel(dataArray, headerArray);
		table.setModel(model);
	}

	/**
	 * Fix some of the code page problems.
	 * 
	 * @param columns
	 * @param col
	 * @return
	 */
	private String fixCodePage(String[] columns, int col) {
		String s;
		byte[] a;

		try {
			s = columns[col];
		} catch (Exception e1) {
			s = "";
		}

		try {
			a = s.getBytes("ISO-8859-1");
			s = new String(a, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return s;
	}

	/**
	 * @param i
	 * @return
	 */
	private String listDataArray(int i) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < dataArray[i].length; j++) {
			sb.append(dataArray[i][j] + ", ");
		}

		LOGGER.log(Level.FINE, "[" + i + "]: " + sb.toString());
		return sb.toString();
	}

	/**
	 * Open one or more tsv files from Family Search and reduce them to the relevant
	 * columns:
	 * 
	 * fullName 8 sex 9 birthLikeDate 10 deathLikeDate 18 deathLikePlaceText 19
	 * burialDate 20 burialPlaceText 21 fatherFullName 22 motherFullName 23
	 * spouseFullName 24
	 */
	protected void openTsvFile() {
		Scanner sc;
		String[] columnLabels;
		String[] columns;
		headerArray = new String[5];
		FileInputStream fis;
		FileFilter ff = new FileNameExtensionFilter("FS eksport fil (TSV)", "tsv");
		String fsFileName = prefs.get("FSFILENAME", ".");
		String[][] burialArray = new String[100][5];
//		String[] c;

		JFileChooser fsChooser = new JFileChooser(fsFileName);
		fsChooser.setFileFilter(ff);
		fsChooser.setMultiSelectionEnabled(true);

		int returnValue = fsChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File[] fsFiles = fsChooser.getSelectedFiles();

			// Save the first or only file name
			fileNameStub = fsFiles[0].getName().replaceFirst("[.][^.]+$", "");
			LOGGER.log(Level.FINE, fsFiles[0].getPath());
			prefs.put("KIPFILENAME", fsFiles[0].getPath());

			for (int fileNo = 0; fileNo < fsFiles.length; fileNo++) {
				try {
					burialArray = new String[100][5];

					fis = new FileInputStream(fsFiles[fileNo]);
					sc = new Scanner(fis);

					// First line contains headers. Neds only to be read for the first file
					columnLabels = sc.nextLine().split("\t");

					if (fileNo == 0) {
						headerArray[0] = columnLabels[8];
						headerArray[1] = columnLabels[9];
						headerArray[2] = columnLabels[10];
						headerArray[3] = columnLabels[18];
						headerArray[4] = columnLabels[19];
//						headerArray[5] = columnLabels[20];
//						headerArray[6] = columnLabels[21];
//						headerArray[7] = columnLabels[22];
//						headerArray[8] = columnLabels[23];
//						headerArray[9] = columnLabels[24];
					}

					// Ignore second line
					sc.nextLine();

					// The rest of the lines contain data

					int i = 0;

					while (sc.hasNext()) {
						columns = sc.nextLine().split("\t");

//						if (columns.length < 25) {
//							c = new String[25];
//
//							for (int j = 0; j < columns.length; j++) {
//								c[j] = columns[j];
//							}
//
//							columns = c;
//						}

//						fullName	8
//						sex	9
//						birthLikeDate	10
//						deathLikeDate	18
//						deathLikePlaceText	19
//						burialDate	20
//						burialPlaceText	21
//						fatherFullName	22
//						motherFullName	23
//						spouseFullName	24

						burialArray[i][0] = fixCodePage(columns, 8);
						burialArray[i][1] = columns[9];
						burialArray[i][2] = columns[10];
						burialArray[i][3] = columns[18];
						burialArray[i][4] = fixCodePage(columns, 19);
//						burialArray[i][5] = (columns[20] == null ? "" : columns[20]);
//						burialArray[i][6] = (columns[21] == null ? "" : fixCodePage(columns, 21));
//						burialArray[i][7] = (columns[22] == null ? "" : fixCodePage(columns, 22));
//						burialArray[i][8] = (columns[23] == null ? "" : fixCodePage(columns, 23));
//						burialArray[i][9] = (columns[24] == null ? "" : fixCodePage(columns, 24));
						i++;
					}

					sc.close();
					fis.close();
				} catch (FileNotFoundException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}

				dataArray = concatenate(dataArray, burialArray);

				LOGGER.log(Level.FINE, "Data array length after concatenation: " + dataArray.length);
			}

			LOGGER.log(Level.INFO, "Data array length after concatenations: " + dataArray.length);
		}

		DefaultTableModel model = new DefaultTableModel(dataArray, headerArray);
		table.setModel(model);
		saveButton.setEnabled(true);
		eliminateButton.setEnabled(true);
	}

	/**
	 * Instantiate a GedcomFile object and populate it with the burial data. Choose
	 * a file name and save it.
	 */
	protected void saveAsGedcom() {
		GedcomFile gedcomFile = new GedcomFile();
		Family family;
		Individual deceased = null;

		int individualId = 1;
		String[] line = new String[5];

		// Add a family object for each burial in the array
		for (int i = 0; i < dataArray.length; i++) {
			line = dataArray[i];

			family = new Family(0, i);

//			fullName	8
//			sex	9
//			birthLikeDate	10
//			deathLikeDate	18
//			deathLikePlaceText	19

			deceased = new Individual(individualId++);

			String deceasedName = line[0];

			if (deceasedName == null) {
				continue;
			}

			String[] deceasedNamePart = deceasedName.split(" ");

			// Handle cases with and without surnames
			if (deceasedNamePart.length == 1) {
				deceased.setName(deceasedName + " ?");
			} else {
				if ((deceasedName.endsWith("sen")) || (deceasedName.endsWith("datter"))
						|| (deceasedName.endsWith("dtr")) || (deceasedName.endsWith("d"))
						|| (deceasedName.endsWith("D")) || (deceasedName.endsWith("son"))) {
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

		String path = gedcomFile.saveFsExtract(fileNameStub);

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), (individualId - 1) + " dødsfald er gemt som GEDCOM fil " + path,
					"Vejby Gedcom", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
