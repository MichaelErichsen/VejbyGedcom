package net.myerichsen.vejby.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.census.Census;

/**
 * This panel displays a census table as loaded from a KIP file. It creates a
 * census table object, but only populates it with census rows.
 *
 * @version 04-09-2020
 * @author Michael Erichsen
 *
 */
public class CensusPanel extends JPanel {
	private static final long serialVersionUID = -6638275102973476672L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private final JTable censusJtable;
	private final JButton btnMapningAfFelter;
	private Census censusTable;
	private final JButton btnbenKipFil;
	private DefaultTableModel censusModel;

	/**
	 * Create the panel.
	 *
	 * @param vejbyGedcom
	 */
	public CensusPanel(VejbyGedcom vejbyGedcom) {
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		final JScrollPane censusscrollPane = new JScrollPane();
		final GridBagConstraints gbc_censusscrollPane = new GridBagConstraints();
		gbc_censusscrollPane.fill = GridBagConstraints.BOTH;
		gbc_censusscrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_censusscrollPane.gridx = 0;
		gbc_censusscrollPane.gridy = 0;
		add(censusscrollPane, gbc_censusscrollPane);

		censusJtable = new JTable();
		censusJtable.setRowSelectionAllowed(false);
		censusscrollPane.setViewportView(censusJtable);

		final JPanel censusButtonPanel = new JPanel();
		censusButtonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		final GridBagConstraints gbc_censusButtonPanel = new GridBagConstraints();
		gbc_censusButtonPanel.fill = GridBagConstraints.BOTH;
		gbc_censusButtonPanel.gridx = 0;
		gbc_censusButtonPanel.gridy = 1;
		add(censusButtonPanel, gbc_censusButtonPanel);
		censusButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnMapningAfFelter = new JButton("Mapning af felter");
		btnMapningAfFelter.setEnabled(false);
		btnMapningAfFelter.addActionListener(e -> {
			final JTabbedPane pane = vejbyGedcom.getTabbedPane();
			vejbyGedcom.getCensusMappingJPanel().populateMappingTable(getCensusModel());
			pane.setEnabledAt(2, true);
			pane.setSelectedIndex(2);
		});

		btnbenKipFil = new JButton("\u00C5ben KIP fil");
		btnbenKipFil.addActionListener(e -> openKipFile(vejbyGedcom));
		censusButtonPanel.add(btnbenKipFil);
		censusButtonPanel.add(btnMapningAfFelter);
	}

	/**
	 * @return the censusModel
	 */
	public DefaultTableModel getCensusModel() {
		return censusModel;
	}

	/**
	 * @return the censusTable
	 */
	public Census getCensusTable() {
		return censusTable;
	}

	/**
	 * Choose and open a KIP file, remove empty columns and display i a tabbed pane.
	 *
	 * @param vejbyGedcom
	 */
	protected void openKipFile(VejbyGedcom vejbyGedcom) {
		final FileFilter ff = new FileNameExtensionFilter("KIP fil", "csv");
		final String kipFileName = prefs.get("KIPFILENAME", ".");

		final JFileChooser kipChooser = new JFileChooser(kipFileName);
		kipChooser.setFileFilter(ff);

		final int returnValue = kipChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			final File kipFile = kipChooser.getSelectedFile();
			LOGGER.log(Level.INFO, kipFile.getPath());
			prefs.put("KIPFILENAME", kipFile.getPath());

			String[] headers;
			try {
				final FileInputStream fis = new FileInputStream(kipFile);
				final Scanner sc = new Scanner(fis);

				// First line contains headers
				headers = sc.nextLine().split(";");
				int index = -1;

				for (int i = 0; i < headers.length; i++) {
					if (headers[i].contains("FT�r")) {
						index = i;
						break;
					}
				}

				if (index == -1) {
					LOGGER.log(Level.SEVERE, "FT�r ikke fundet som kolonnenavn");
				}

				// Second line is data
				final String[] ftData = sc.nextLine().split(";");
				final String ftYear = ftData[index].substring(3);
				LOGGER.log(Level.INFO, ftYear);
				final int year = Integer.parseInt(ftYear);
				String message;
				setCensusTable(Census.getInstance(year));
				message = getCensusTable().readKipfile(kipFile);
				LOGGER.log(Level.INFO, message);
				message = getCensusTable().removeEmptyColumns();
				LOGGER.log(Level.INFO, message);
				sc.close();
				fis.close();
			} catch (final IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			final String[][] censusArray = new String[getCensusTable().getPersons().size()][getCensusTable()
					.getHeaders().size()];
			final List<List<String>> lls = getCensusTable().getPersons();
			List<String> list;

			for (int i = 0; i < lls.size(); i++) {
				list = lls.get(i);

				for (int j = 0; j < list.size(); j++) {
					LOGGER.log(Level.FINE, "i " + i + " j " + j + " " + list.get(j));
					censusArray[i][j] = list.get(j);
				}
			}

			final List<String> cth = getCensusTable().getHeaders();
			final String[] headerArray = new String[cth.size()];
			for (int i = 0; i < cth.size(); i++) {
				headerArray[i] = cth.get(i);
			}

			setCensusModel(new DefaultTableModel(censusArray, headerArray));
			censusJtable.setModel(getCensusModel());
			btnMapningAfFelter.setEnabled(true);
		}
	}

	/**
	 * @param censusModel the censusModel to set
	 */
	public void setCensusModel(DefaultTableModel censusModel) {
		this.censusModel = censusModel;
	}

	/**
	 * @param censusTable the censusTable to set
	 */
	public void setCensusTable(Census censusTable) {
		this.censusTable = censusTable;
	}

}
