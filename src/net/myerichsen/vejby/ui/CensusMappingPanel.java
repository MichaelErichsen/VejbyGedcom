package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.census.Census;
import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.util.CustomTableCellRenderer;
import net.myerichsen.vejby.util.Mapping;
import net.myerichsen.vejby.util.PrefKey;

/**
 * Panel to map census fields for further analysis. It displays six columns.
 * <p>
 * The first one is field number. The second one is populated by the reduced
 * table headers. The third one has a choice for each cell with the relevant
 * attributes for each type.
 *
 * @version 04-09-2020
 * @author Michael Erichsen
 */
public class CensusMappingPanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -2181211331271971240L;
	private final Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private final Mapping mapping;
	private final int[] mappingKeys;

	private final JTable mappingTable;
	private DefaultTableModel mappingModel;
	private CustomTableCellRenderer renderer;
	private final VejbyGedcom vejbyGedcom;

	/**
	 * Create the panel.
	 *
	 * @param vejbyGedcom The root panel
	 */
	public CensusMappingPanel(VejbyGedcom vejbyGedcom) {
		this.vejbyGedcom = vejbyGedcom;
		mapping = Mapping.getInstance();
		mappingKeys = mapping.getMappingKeys();

		setLayout(new BorderLayout(0, 0));

		final JScrollPane mappingScrollPane = new JScrollPane();
		add(mappingScrollPane, BorderLayout.CENTER);

		mappingTable = new JTable();
		mappingScrollPane.setViewportView(mappingTable);

		final JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JButton btnAnalysr = new JButton("Analys\u00E9r");
		btnAnalysr.addActionListener(e -> {
			if (populateMappingFromTable() && identifyHouseholds()) {
				vejbyGedcom.getHouseholdJPanel().populateTree();

				final JTabbedPane pane = vejbyGedcom.getTabbedPane();
				pane.setEnabledAt(3, true);
				pane.setSelectedIndex(3);
			}
		});
		buttonPanel.add(btnAnalysr);
	}

	/**
	 * Identify and create households and families from census table.
	 *
	 * @return Flag to mark if identification succeeded
	 */
	protected boolean identifyHouseholds() {
		if (mappingKeys[3] == 0) {
			JOptionPane.showMessageDialog(new JFrame(), "Husstandsnummer kan ikke mappes", "Vejby Gedcom",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		final Census censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();
		String message = censusTable.createHouseholds(mappingKeys[3]);
		LOGGER.log(Level.INFO, message);

		// Create a family for each household
		if (mappingKeys[5] != 0) {
			for (final Household household : censusTable.getHouseholds()) {
				message = household.createFamilies(mappingKeys[5]);
				LOGGER.log(Level.FINE, message);
			}
		} else {
			// Handle 1787 without a sex column
			for (final Household household : censusTable.getHouseholds()) {
				message = household.createFamilies(-1);
				LOGGER.log(Level.FINE, message);
			}
		}
		return true;
	}

	/**
	 * Get data from table and save into mapping array.
	 *
	 * @return
	 *
	 */
	private boolean populateMappingFromTable() {
		String value;

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 2);

			if (PrefKey.INDIVIDUAL_0.equals(value)) {
				mappingKeys[0] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_0, i);
			} else if (PrefKey.INDIVIDUAL_1.equals(value)) {
				mappingKeys[1] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_1, i);
			} else if (PrefKey.INDIVIDUAL_2.equals(value)) {
				mappingKeys[2] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_2, i);
			} else if (PrefKey.INDIVIDUAL_3.equals(value)) {
				mappingKeys[3] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_3, i);
			} else if (PrefKey.INDIVIDUAL_4.equals(value)) {
				mappingKeys[4] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_4, i);
			} else if (PrefKey.INDIVIDUAL_5.equals(value)) {
				mappingKeys[5] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_5, i);
			} else if (PrefKey.INDIVIDUAL_6.equals(value)) {
				mappingKeys[6] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_6, i);
			} else if (PrefKey.INDIVIDUAL_7.equals(value)) {
				mappingKeys[7] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_7, i);
			} else if (PrefKey.INDIVIDUAL_8.equals(value)) {
				mappingKeys[8] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_8, i);
			} else if (PrefKey.INDIVIDUAL_9.equals(value)) {
				mappingKeys[9] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_9, i);
			} else if (PrefKey.INDIVIDUAL_10.equals(value)) {
				mappingKeys[10] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_10, i);
			} else if (PrefKey.INDIVIDUAL_11.equals(value)) {
				mappingKeys[11] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_11, i);
			} else if (PrefKey.INDIVIDUAL_12.equals(value)) {
				mappingKeys[12] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_12, i);
			}
		}

		boolean found = false;
		String error = "";
		for (int i = 0; i < mappingKeys.length; i++) {
			for (int j = i + 1; j < mappingKeys.length; j++) {
				if (mappingKeys[i] != 0 && mappingKeys[i] == mappingKeys[j]) {
					found = true;
					error = "Samme værdi (" + mappingKeys[i] + ") er brugt både i " + (i + 1) + ". og " + (j + 1)
							+ ". række";
				}
			}
		}

		if (found) {
			JOptionPane.showMessageDialog(new JFrame(), error, "Vejby Gedcom", JOptionPane.ERROR_MESSAGE);
		}
		LOGGER.log(Level.INFO, mapping.toString());

		return !found;
	}

	/**
	 * Populate the mapping table with values entered.
	 *
	 * @param censusModel The underlying table model
	 */
	public void populateMappingTable(DefaultTableModel censusModel) {
		final String[] columnNames = { "Nr.", "FT kolonne", "GEDCOM kolonne" };

		final List<String> headers = new ArrayList<>();

		for (int i = 0; i < censusModel.getColumnCount(); i++) {
			headers.add(censusModel.getColumnName(i));
		}

		final int rowCount = headers.size();
		final String[][] mappingArray = new String[rowCount][columnNames.length];

		for (int i = 0; i < rowCount; i++) {
			mappingArray[i][0] = Integer.toString(i + 1);
			mappingArray[i][1] = headers.get(i);
			for (int j = 2; j < columnNames.length; j++) {
				mappingArray[i][j] = PrefKey.INDIVIDUAL_0;
			}
		}

		mappingModel = new DefaultTableModel(mappingArray, columnNames);
		renderer = new CustomTableCellRenderer();
		mappingTable.setModel(mappingModel);
		mappingTable.getColumnModel().getColumn(2).setCellRenderer(renderer);

		final JComboBox<String> individualcomboBox = new JComboBox<>();
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_0);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_1);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_2);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_3);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_4);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_5);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_6);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_7);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_8);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_9);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_10);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_11);
		individualcomboBox.addItem(PrefKey.INDIVIDUAL_12);
		mappingTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(individualcomboBox));

		setValuesFromPreferences();
	}

	/**
	 * Set field values and colours from preferences.
	 */
	private void setValuesFromPreferences() {
		int row;

		final int maxSize = mappingTable.getModel().getRowCount();
		LOGGER.log(Level.FINE, "Mapping table row count: " + maxSize);

		row = prefs.getInt(PrefKey.INDIVIDUAL_0, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_0, row, 2);
			renderer.setRowColor(row, Color.WHITE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_1, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_1, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_2, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_2, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_3, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_3, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_4, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_4, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_5, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_5, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_6, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_6, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_7, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_7, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_8, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_8, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_9, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_9, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_10, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_10, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_11, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_11, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_12, 0);
		if (row > 0 && row < maxSize) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_12, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}
	}
}
