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

import net.myerichsen.vejby.census.Mapping;
import net.myerichsen.vejby.util.CustomTableCellRenderer;
import net.myerichsen.vejby.util.PrefKey;

/**
 * Panel to map census fields for further analysis. It displays six columns.
 * 
 * The first one is field number. The second one is populated by the reduced
 * table headers. The others have a choice for each cell with the relevant
 * attributes for each type.
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class CensusMappingJPanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -2181211331271971240L;
	private Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");

	private JTable mappingTable;
	private DefaultTableModel mappingModel;
	private CustomTableCellRenderer renderer2;
	private CustomTableCellRenderer renderer3;
	private CustomTableCellRenderer renderer4;
	private CustomTableCellRenderer renderer5;

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
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
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
	 * @param defaultTableModel
	 * 
	 */
	public void populateMappingTable(DefaultTableModel censusModel) {
		String[] columnNames = new String[] { "Nr.", "FT kolonne", "Individ", "Folket\u00E6llingsh\u00E6ndelse",
				"F\u00F8dselsh\u00E6ndelse", "Erhvervsh\u00E6ndelse" };

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
				mappingArray[i][j] = "Bruges ikke";
			}
		}

		mappingModel = new DefaultTableModel(mappingArray, columnNames);
		renderer2 = new CustomTableCellRenderer();
		renderer3 = new CustomTableCellRenderer();
		renderer4 = new CustomTableCellRenderer();
		renderer5 = new CustomTableCellRenderer();
		mappingTable.setModel(mappingModel);
		mappingTable.getColumnModel().getColumn(2).setCellRenderer(renderer2);
		mappingTable.getColumnModel().getColumn(3).setCellRenderer(renderer3);
		mappingTable.getColumnModel().getColumn(4).setCellRenderer(renderer4);
		mappingTable.getColumnModel().getColumn(5).setCellRenderer(renderer5);

		JComboBox<String> individualcomboBox = new JComboBox<String>();
		individualcomboBox.addItem("Bruges ikke");
		individualcomboBox.addItem("Personid");
		individualcomboBox.addItem("Husstandsnr.");
		individualcomboBox.addItem("Navn");
		individualcomboBox.addItem("K�n");
		individualcomboBox.addItem("F�dsels�r");
		individualcomboBox.addItem("Alder");
		individualcomboBox.addItem("Civilstand");
		individualcomboBox.addItem("Erhverv");
		individualcomboBox.addItem("F�dested");
		individualcomboBox.addItem("FT�r");
		mappingTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(individualcomboBox));

		JComboBox<String> censuscombobox = new JComboBox<String>();
		censuscombobox.addItem("Bruges ikke");
		censuscombobox.addItem("Alder");
		censuscombobox.addItem("FT�r");
		censuscombobox.addItem("Sted");
		mappingTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(censuscombobox));

		JComboBox<String> birthcomboBox = new JComboBox<String>();
		birthcomboBox.addItem("Bruges ikke");
		birthcomboBox.addItem("Alder");
		birthcomboBox.addItem("F�dsels�r");
		birthcomboBox.addItem("F�dested");
		mappingTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(birthcomboBox));

		JComboBox<String> tradecomboBox = new JComboBox<String>();
		tradecomboBox.addItem("Bruges ikke");
		tradecomboBox.addItem("FT�r");
		tradecomboBox.addItem("Erhverv");
		mappingTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(tradecomboBox));

		setValuesFromPreferences();
	}

	/**
	 * Set field values and colours from preferences
	 */
	private void setValuesFromPreferences() {
		int row;

		row = prefs.getInt(PrefKey.INDIVIDUAL_1, 0);
		if (row > 0) {
			mappingTable.setValueAt("Personid", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_2, 0);
		if (row > 0) {
			mappingTable.setValueAt("Husstandsnr.", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_3, 0);
		if (row > 0) {
			mappingTable.setValueAt("Navn", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_4, 0);
		if (row > 0) {
			mappingTable.setValueAt("K�n", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_5, 0);
		if (row > 0) {
			mappingTable.setValueAt("F�dsels�r", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_6, 0);
		if (row > 0) {
			mappingTable.setValueAt("Alder", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_7, 0);
		if (row > 0) {
			mappingTable.setValueAt("Civilstand", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_8, 0);
		if (row > 0) {
			mappingTable.setValueAt("Erhverv", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_9, 0);
		if (row > 0) {
			mappingTable.setValueAt("F�dested", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.INDIVIDUAL_10, 0);
		if (row > 0) {
			mappingTable.setValueAt("FT�r", row, 2);
			renderer2.setRowColor(row, Color.ORANGE);
		}

		row = prefs.getInt(PrefKey.CENSUS_1, 0);
		if (row > 0) {
			mappingTable.setValueAt("Alder", row, 3);
			renderer3.setRowColor(row, Color.GREEN);
		}

		row = prefs.getInt(PrefKey.CENSUS_2, 0);
		if (row > 0) {
			mappingTable.setValueAt("FT�r", row, 3);
			renderer3.setRowColor(row, Color.GREEN);
		}

		row = prefs.getInt(PrefKey.CENSUS_3, 0);
		if (row > 0) {
			mappingTable.setValueAt("Sted", row, 3);
			renderer3.setRowColor(row, Color.GREEN);
		}

		row = prefs.getInt(PrefKey.BIRTH_1, 0);
		if (row > 0) {
			mappingTable.setValueAt("Alder", row, 4);
			renderer4.setRowColor(row, Color.YELLOW);
		}

		row = prefs.getInt(PrefKey.BIRTH_2, 0);
		if (row > 0) {
			mappingTable.setValueAt("F�dsels�r", row, 4);
			renderer4.setRowColor(row, Color.YELLOW);
		}

		row = prefs.getInt(PrefKey.BIRTH_3, 0);
		if (row > 0) {
			mappingTable.setValueAt("F�dested", row, 4);
			renderer4.setRowColor(row, Color.YELLOW);
		}

		row = prefs.getInt(PrefKey.OCCUPATION_1, 0);
		if (row > 0) {
			mappingTable.setValueAt("FT�r", row, 5);
			renderer5.setRowColor(row, Color.MAGENTA);
		}

		row = prefs.getInt(PrefKey.OCCUPATION_2, 0);
		if (row > 0) {
			mappingTable.setValueAt("Erhverv", row, 5);
			renderer5.setRowColor(row, Color.MAGENTA);
		}
	}

	/**
	 * Get data from table and save into mapping arrays.
	 * 
	 * @return
	 */
	private Mapping map() {
		Mapping mapping = new Mapping();
		String value;

		int[] individual = mapping.getIndividual();

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 2);

			if (value.equals("Personid")) {
				individual[1] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_1, i);
			} else if (value.equals("Husstandsnr.")) {
				individual[2] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_2, i);
			} else if (value.equals("Navn")) {
				individual[3] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_3, i);
			} else if (value.equals("K�n")) {
				individual[4] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_4, i);
			} else if (value.equals("F�dsels�r")) {
				individual[5] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_5, i);
			} else if (value.equals("Alder")) {
				individual[6] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_6, i);
			} else if (value.equals("Civilstand")) {
				individual[7] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_7, i);
			} else if (value.equals("Erhverv")) {
				individual[8] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_8, i);
			} else if (value.equals("F�dested")) {
				individual[9] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_9, i);
			} else if (value.equals("FT�r")) {
				individual[10] = i;
				prefs.putInt(PrefKey.INDIVIDUAL_10, i);
			}
		}

		LOGGER.log(Level.INFO,
				"Individual: " + individual[0] + " " + individual[1] + " " + individual[2] + " " + individual[3] + " "
						+ individual[4] + " " + individual[5] + " " + individual[6] + " " + individual[7] + " "
						+ individual[8] + " " + individual[9] + " " + individual[10]);

		int[] census = mapping.getCensus();

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 3);

			if (value.equals("Alder")) {
				census[1] = i;
				prefs.putInt(PrefKey.CENSUS_1, i);
			} else if (value.equals("FT�r")) {
				census[2] = i;
				prefs.putInt(PrefKey.CENSUS_2, i);
			} else if (value.equals("Sted")) {
				census[3] = i;
				prefs.putInt(PrefKey.CENSUS_3, i);
			}
		}

		LOGGER.log(Level.INFO, "Census: " + census[0] + " " + census[1] + " " + census[2] + " " + census[3]);

		int[] birth = mapping.getBirth();

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 4);

			if (value.equals("Alder")) {
				birth[1] = i;
				prefs.putInt(PrefKey.BIRTH_1, i);
			} else if (value.equals("F�dsels�r")) {
				birth[2] = i;
				prefs.putInt(PrefKey.BIRTH_2, i);
			} else if (value.equals("F�dested")) {
				birth[3] = i;
				prefs.putInt(PrefKey.BIRTH_3, i);
			}
		}

		LOGGER.log(Level.INFO, "Birth: " + birth[0] + " " + birth[1] + " " + birth[2] + " " + birth[3]);

		int[] trade = mapping.getTrade();

		for (int i = 0; i < mappingTable.getRowCount(); i++) {
			value = (String) mappingTable.getValueAt(i, 5);

			if (value.equals("FT�r")) {
				trade[1] = i;
				prefs.putInt(PrefKey.OCCUPATION_1, i);
			} else if (value.equals("Erhverv")) {
				trade[2] = i;
				prefs.putInt(PrefKey.OCCUPATION_2, i);
			}
		}

		LOGGER.log(Level.INFO, "Trade: " + trade[0] + " " + trade[1] + " " + trade[2]);
		return mapping;
	}
}
