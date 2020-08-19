package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.util.CustomTableCellRenderer;
import net.myerichsen.vejby.util.Mapping;
import net.myerichsen.vejby.util.PrefKey;

/**
 * Panel to map census fields for further analysis. It displays six columns.
 * 
 * The first one is field number. The second one is populated by the reduced
 * table headers. The third one has choice for each cell with the relevant
 * attributes for each type.
 * 
 * @author Michael Erichsen
 * @version 19. aug. 2020
 *
 */
public class CensusMappingJPanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -2181211331271971240L;
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private JTable mappingTable;
	private DefaultTableModel mappingModel;
	private CustomTableCellRenderer renderer;

	/**
	 * Create the panel.
	 * 
	 * @param vejbyGedcom
	 */
	public CensusMappingJPanel(VejbyGedcom vejbyGedcom) {
		setLayout(new BorderLayout(0, 0));

		JScrollPane mappingScrollPane = new JScrollPane();
		add(mappingScrollPane, BorderLayout.CENTER);

		mappingTable = new JTable();
		mappingScrollPane.setViewportView(mappingTable);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnAnalysr = new JButton("Analys\u00E9r");
		btnAnalysr.addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Mapping mapping = map();

				vejbyGedcom.getHouseholdJPanel().populateTree(mapping);

				JTabbedPane pane = vejbyGedcom.getTabbedPane();
				pane.setEnabledAt(2, true);
				pane.setSelectedIndex(2);
			}
		});
		buttonPanel.add(btnAnalysr);
	}

	/**
	 * Get data from table and save into mapping arrays.
	 * 
	 * @return
	 */
	private Mapping map() {
		Mapping mapping = new Mapping();
		String value;

		int[] mappingKeys = mapping.getMappingKeys();

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 2);

			if (value.equals(PrefKey.INDIVIDUAL_1)) {
				mappingKeys[1] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_1, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_2)) {
				mappingKeys[2] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_2, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_3)) {
				mappingKeys[3] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_3, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_4)) {
				mappingKeys[4] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_4, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_5)) {
				mappingKeys[5] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_5, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_6)) {
				mappingKeys[6] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_6, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_7)) {
				mappingKeys[7] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_7, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_8)) {
				mappingKeys[8] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_8, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_9)) {
				mappingKeys[9] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_9, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_10)) {
				mappingKeys[10] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_10, i);
			} else if (value.equals(PrefKey.INDIVIDUAL_11)) {
				mappingKeys[11] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_11, i);
			}
		}

		LOGGER.log(Level.INFO, mapping.toString());
		return mapping;
	}

	/**
	 * @param defaultTableModel
	 * 
	 */
	public void populateMappingTable(DefaultTableModel censusModel) {
		String[] columnNames = new String[] { "Nr.", "FT kolonne", "GEDCOM kolonne" };

		List<String> headers = new ArrayList<String>();

		for (int i = 0; i < censusModel.getColumnCount(); i++) {
			headers.add(censusModel.getColumnName(i));
		}

		int rowCount = headers.size();
		String[][] mappingArray = new String[rowCount][columnNames.length];

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

		JComboBox<String> individualcomboBox = new JComboBox<String>();
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
		mappingTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(individualcomboBox));

		setValuesFromPreferences();
	}

	/**
	 * Set field values and colours from preferences
	 */
	private void setValuesFromPreferences() {
		int row;

		int maxSize = mappingTable.getModel().getRowCount();
		LOGGER.log(Level.FINE, "Mapping table row count: " + maxSize);

		row = prefs.getInt(PrefKey.INDIVIDUAL_1, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_1, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_2, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_2, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_3, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_3, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_4, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_4, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_5, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_5, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_6, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_6, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_7, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_7, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_8, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_8, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_9, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_9, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_10, 0);
		if ((row > 0) && (row < maxSize)) {
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_10, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_11, 0);
		if ((row > 0) && (row < maxSize)) {
			LOGGER.log(Level.INFO, "Row: " + row);
			mappingTable.setValueAt(PrefKey.INDIVIDUAL_11, row, 2);
			renderer.setRowColor(row, Color.ORANGE);
		}

	}
}
