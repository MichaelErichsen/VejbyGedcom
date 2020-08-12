/**
 * 
 */
package net.myerichsen.vejby.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.census.Table;

/**
 * This panel displays a census table as loaded from a KIP file.
 * 
 * @author Michael Erichsen
 * @version 13. aug. 2020
 *
 */
public class CensusJPanel extends JPanel {
	private static final long serialVersionUID = -6638275102973476672L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private JTable censusJtable;
	private JButton btnAnalysr;
	private JButton btnMapningAfFelter;
	private Table censusTable;
	private JButton btnbenKipFil;

	/**
	 * Create the panel.
	 */
	public CensusJPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JScrollPane censusscrollPane = new JScrollPane();
		GridBagConstraints gbc_censusscrollPane = new GridBagConstraints();
		gbc_censusscrollPane.fill = GridBagConstraints.BOTH;
		gbc_censusscrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_censusscrollPane.gridx = 0;
		gbc_censusscrollPane.gridy = 0;
		add(censusscrollPane, gbc_censusscrollPane);

		censusJtable = new JTable();
		censusJtable.setRowSelectionAllowed(false);
		censusscrollPane.setViewportView(censusJtable);

		JPanel censusButtonPanel = new JPanel();
		censusButtonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		GridBagConstraints gbc_censusButtonPanel = new GridBagConstraints();
		gbc_censusButtonPanel.fill = GridBagConstraints.BOTH;
		gbc_censusButtonPanel.gridx = 0;
		gbc_censusButtonPanel.gridy = 1;
		add(censusButtonPanel, gbc_censusButtonPanel);
		censusButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnAnalysr = new JButton("Analys\u00E9r");
		btnAnalysr.setEnabled(false);
		btnAnalysr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				analyseCensus();
			}
		});

		btnMapningAfFelter = new JButton("Mapning af felter");
		btnMapningAfFelter.setEnabled(false);
		btnMapningAfFelter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapCensusFields();
			}
		});

		btnbenKipFil = new JButton("\u00C5ben KIP fil");
		btnbenKipFil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openKipFile();
			}
		});
		censusButtonPanel.add(btnbenKipFil);
		censusButtonPanel.add(btnMapningAfFelter);
		censusButtonPanel.add(btnAnalysr);
	}

	/**
	 * Split the table into households. Split the household into families and
	 * single persons.
	 * 
	 * Then open the household dialog
	 */
	protected void analyseCensus() {
		// TODO Get from mapping
		int householdFieldNumber = 3;
		int sexFieldNumber = 6;
		String message = censusTable.createHouseholds(householdFieldNumber);
		LOGGER.log(Level.INFO, message);

		for (Household household : censusTable.getHouseholds()) {
			message = household.identifyFamilies(sexFieldNumber);
			LOGGER.log(Level.FINE, message + " " + household.getFamilies().get(0).toString());
		}

		HouseholdJDialog householdJDialog = new HouseholdJDialog(censusTable);
		householdJDialog.setVisible(true);
	}

	/**
	 * Map census fields into person attributes and event attributes
	 */
	protected void mapCensusFields() {
		CensusFieldMapJDialog mapDialog = new CensusFieldMapJDialog();
		mapDialog.setCensusTable(censusTable);
		mapDialog.setVisible(true);
		// TODO Test if saved or cancelled
		btnAnalysr.setEnabled(true);
	}

	/**
	 * Choose and open a KIP file, remove empty columns and display i a tabbed
	 * pane.
	 */
	protected void openKipFile() {
		FileFilter ff = new FileNameExtensionFilter("KIP fil", "csv");
		JFileChooser kipChooser = new JFileChooser("C://Users//michael//git//VejbyGedcom//Documentation");

		kipChooser.setFileFilter(ff);

		int returnValue = kipChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File kipFile = kipChooser.getSelectedFile();
			LOGGER.log(Level.INFO, kipFile.getAbsolutePath());

			String[] headers;
			try {
				FileInputStream fis = new FileInputStream(kipFile);
				Scanner sc = new Scanner(fis);

				// First line contains headers
				headers = sc.nextLine().split(";");
				int index = -1;

				for (int i = 0; i < headers.length; i++) {
					if (headers[i].contains("FTår")) {
						index = i;
						break;
					}
				}

				if (index == -1) {
					LOGGER.log(Level.SEVERE, "FTår ikke fundet som kolonnenavn");
				}

				// Second line is data
				String[] ftData = sc.nextLine().split(";");
				String ftYear = ftData[index].substring(3);
				LOGGER.log(Level.INFO, ftYear);
				int year = Integer.parseInt(ftYear);

				String kipFileName = kipFile.getAbsolutePath();
				String message;
				censusTable = new Table(year, kipFileName);
				message = censusTable.readKipfile();
				LOGGER.log(Level.INFO, message);
				message = censusTable.removeEmptyColumns();
				LOGGER.log(Level.INFO, message);
				sc.close();
				fis.close();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			String[][] censusArray = new String[censusTable.getPersons().size()][censusTable.getHeaders().size()];
			List<List<String>> lls = censusTable.getPersons();
			List<String> list;

			for (int i = 0; i < lls.size(); i++) {
				list = lls.get(i);

				for (int j = 0; j < list.size(); j++) {
					LOGGER.log(Level.FINE, "i " + i + " j " + j + " " + list.get(j));
					censusArray[i][j] = list.get(j);
				}
			}

			List<String> cth = censusTable.getHeaders();
			String[] headerArray = new String[cth.size()];
			for (int i = 0; i < cth.size(); i++) {
				headerArray[i] = cth.get(i);
			}

			DefaultTableModel defaultTableModel = new DefaultTableModel(censusArray, headerArray);
			censusJtable.setModel(defaultTableModel);
			btnMapningAfFelter.setEnabled(true);
		}
	}

}
