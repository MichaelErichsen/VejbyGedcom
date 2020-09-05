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
 * This panel reads a birth query result from family Search and displays the
 * reduced result. The result can be saved as a GEDCOM file.
 * 
 * @author Michael Erichsen
 * @version 05-09-2020
 *
 */
public class BirthPanel extends JPanel {
	private static final long serialVersionUID = 3673964025732718748L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private String[][] birthArray = new String[100][7];

	private JTable table;
	private JButton saveButton;
	private String fileNameStub;

	/**
	 * Create the panel.
	 */
	public BirthPanel() {
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
			fileNameStub = fsFile.getName().replaceFirst("[.][^.]+$", "");
			LOGGER.log(Level.INFO, fsFile.getPath());
			prefs.put("KIPFILENAME", fsFile.getPath());

			/**
			 * 
			 * The interesting columns are:
			 * 
			 * fullName 8 sex 9 birthLikeDate 10 chrDate 12 chrPlaceText 13 fatherFullName
			 * 22 motherFullName 23
			 */

			String[] columns;
			String[] headerArray = new String[7];

			try {
				FileInputStream fis = new FileInputStream(fsFile);
				Scanner sc = new Scanner(fis);

				// First line contains headers
				columns = sc.nextLine().split("\t");

				headerArray[0] = columns[8];
				headerArray[1] = columns[9];
				headerArray[2] = columns[10];
				headerArray[3] = columns[12];
				headerArray[4] = columns[13];
				headerArray[5] = columns[22];
				headerArray[6] = columns[23];

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

					birthArray[i][0] = s;
					birthArray[i][1] = columns[9];
					birthArray[i][2] = columns[10];
					birthArray[i][3] = columns[12];
					birthArray[i][4] = columns[13];

					s = columns[22];
					a = null;

					try {
						a = s.getBytes("ISO-8859-1");
						s = new String(a, "UTF-8");

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					birthArray[i][5] = s;

					try {
						s = columns[23];
					} catch (Exception e1) {
						s = "";
					}

					a = null;

					try {
						a = s.getBytes("ISO-8859-1");
						s = new String(a, "UTF-8");

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					birthArray[i][6] = s;
					i++;
				}

				sc.close();
				fis.close();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			DefaultTableModel model = new DefaultTableModel(birthArray, headerArray);
			table.setModel(model);
			saveButton.setEnabled(true);
		}

	}

	/**
	 * Instantiate a GedcomFile object and populate it with the birth data. Choose a
	 * file name and save it.
	 * 
	 */
	protected void saveAsGedcom() {
		GedcomFile gedcomFile = new GedcomFile();
		Family family;
		Individual child = null;
		Individual father = null;
		Individual mother = null;

		int individualId = 1;
		String[] line = new String[7];

		// Add a family object for each birth in the array
		for (int i = 0; i < birthArray.length; i++) {
			line = birthArray[i];

			family = new Family(0, i);

			child = new Individual(individualId++);
			child.setName(line[0] + " ?");
			child.setSex(line[1].equals("male") ? "M" : "F");
			child.setBirthDate(line[2]);
			child.setChristeningDate(line[3]);
			child.setChristeningPlace(line[4]);

			father = new Individual(individualId++);
			father.setName(line[5]);
			father.setSex("M");

			mother = new Individual(individualId++);
			mother.setName(line[6]);
			mother.setSex("F");

			family.setFather(father);
			family.setMother(mother);
			family.getChildren().add(child);

			gedcomFile.addFamily(family);
		}

		String path = gedcomFile.saveFsExtract(fileNameStub);

		if (!path.equals("")) {
			JOptionPane.showMessageDialog(new JFrame(), "Vielser er gemt som GEDCOM fil " + path, "Vejby Gedcom",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
