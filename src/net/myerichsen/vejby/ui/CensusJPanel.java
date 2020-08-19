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

import net.myerichsen.vejby.census.Table;

/**
 * This panel displays a census table as loaded from a KIP file. It creates a
 * census table object, but only populates it with census rows.
 * 
 * @author Michael Erichsen
 * @version 19. aug. 2020
 *
 */
public class CensusJPanel extends JPanel {
	private static final long serialVersionUID = -6638275102973476672L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private JTable censusJtable;
	private JButton btnMapningAfFelter;
	private Table censusTable;
	private JButton btnbenKipFil;
	private DefaultTableModel censusModel;

	/**
	 * Create the panel.
	 * 
	 * @param vejbyGedcom
	 */
	public CensusJPanel(VejbyGedcom vejbyGedcom) {
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

		btnMapningAfFelter = new JButton("Mapning af felter");
		btnMapningAfFelter.setEnabled(false);
		btnMapningAfFelter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTabbedPane pane = vejbyGedcom.getTabbedPane();
				vejbyGedcom.getCensusMappingJPanel().populateMappingTable(getCensusModel());
				pane.setEnabledAt(1, true);
				pane.setSelectedIndex(1);
			}
		});

		btnbenKipFil = new JButton("\u00C5ben KIP fil");
		btnbenKipFil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openKipFile(vejbyGedcom);
			}
		});
		censusButtonPanel.add(btnbenKipFil);
		censusButtonPanel.add(btnMapningAfFelter);
	}

	/**
	 * Choose and open a KIP file, remove empty columns and display i a tabbed pane.
	 * 
	 * @param vejbyGedcom
	 */
	protected void openKipFile(VejbyGedcom vejbyGedcom) {
		FileFilter ff = new FileNameExtensionFilter("KIP fil", "csv");
		String kipFileName = prefs.get("KIPFILENAME", ".");
		JFileChooser kipChooser = new JFileChooser(kipFileName);

		kipChooser.setFileFilter(ff);

		int returnValue = kipChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File kipFile = kipChooser.getSelectedFile();
			LOGGER.log(Level.INFO, kipFile.getPath());
			prefs.put("KIPFILENAME", kipFile.getPath());

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
				// FIXME Exception in thread "AWT-EventQueue-0"
				// java.lang.StringIndexOutOfBoundsException: String index out
				// of range: 3
				String ftYear = ftData[index].substring(3);
				LOGGER.log(Level.INFO, ftYear);
				int year = Integer.parseInt(ftYear);
				String message;
				setCensusTable(new Table(year, kipFileName));
				message = getCensusTable().readKipfile();
				LOGGER.log(Level.INFO, message);
				message = getCensusTable().removeEmptyColumns();
				LOGGER.log(Level.INFO, message);
				sc.close();
				fis.close();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			String[][] censusArray = new String[getCensusTable().getPersons().size()][getCensusTable().getHeaders()
					.size()];
			List<List<String>> lls = getCensusTable().getPersons();
			List<String> list;

			for (int i = 0; i < lls.size(); i++) {
				list = lls.get(i);

				for (int j = 0; j < list.size(); j++) {
					LOGGER.log(Level.FINE, "i " + i + " j " + j + " " + list.get(j));
					censusArray[i][j] = list.get(j);
				}
			}

			List<String> cth = getCensusTable().getHeaders();
			String[] headerArray = new String[cth.size()];
			for (int i = 0; i < cth.size(); i++) {
				headerArray[i] = cth.get(i);
			}

			setCensusModel(new DefaultTableModel(censusArray, headerArray));
			censusJtable.setModel(getCensusModel());
			btnMapningAfFelter.setEnabled(true);
		}
	}

	/**
	 * @return the censusModel
	 */
	public DefaultTableModel getCensusModel() {
		return censusModel;
	}

	/**
	 * @param censusModel the censusModel to set
	 */
	public void setCensusModel(DefaultTableModel censusModel) {
		this.censusModel = censusModel;
	}

	/**
	 * @return the censusTable
	 */
	public Table getCensusTable() {
		return censusTable;
	}

	/**
	 * @param censusTable the censusTable to set
	 */
	public void setCensusTable(Table censusTable) {
		this.censusTable = censusTable;
	}

}
