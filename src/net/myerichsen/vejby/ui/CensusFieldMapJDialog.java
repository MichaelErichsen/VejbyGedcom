package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.census.Mapping;
import net.myerichsen.vejby.census.Table;

/**
 * This Census field map dialog displays six columns.
 * 
 * The first one is field number. The second one is populated by the reduced
 * table headers. The others have a choice for each cell with the relevant
 * attributes for each type.
 * 
 * @author Michael Erichsen
 * @version 13. aug. 2020
 */
public class CensusFieldMapJDialog extends JDialog {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -8669257676955258942L;

	// FIXME OK button not working
	// TODO Both handle an age and alternatively a birth date or year
	private final JPanel mappingcontentPanel = new JPanel();
	private JTable mappingtable;
	private JButton okButton;
	private int rowCount;
	private String[][] data;

	/**
	 * Create the dialog.
	 */
	public CensusFieldMapJDialog() {
		setTitle("Mapning af kolonner i folket\u00E6llinger");
		setBounds(100, 100, 853, 559);
		getContentPane().setLayout(new BorderLayout());
		mappingcontentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(mappingcontentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_mappingcontentPanel = new GridBagLayout();
		gbl_mappingcontentPanel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_mappingcontentPanel.rowWeights = new double[] { 1.0 };
		mappingcontentPanel.setLayout(gbl_mappingcontentPanel);
		{
			JScrollPane mappingscrollPane = new JScrollPane();
			GridBagConstraints gbc_mappingscrollPane = new GridBagConstraints();
			gbc_mappingscrollPane.gridwidth = 2;
			gbc_mappingscrollPane.fill = GridBagConstraints.BOTH;
			gbc_mappingscrollPane.gridx = 0;
			gbc_mappingscrollPane.gridy = 0;
			mappingcontentPanel.add(mappingscrollPane, gbc_mappingscrollPane);
			{
				mappingtable = new JTable();
				mappingscrollPane.setViewportView(mappingtable);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Gem");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						saveMapping();

					}
				});
				okButton.setActionCommand("OK");
				okButton.setEnabled(false);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Fortryd");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
						setVisible(false);
					}

				});
				cancelButton.setActionCommand("Fortryd");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Save the table to a mapping object
	 */
	private void saveMapping() {
		// TODO Does not set any data in rhe mapping object
		Mapping mapping = new Mapping(rowCount, 5);
		mapping.setMappingMatrix(data);
		LOGGER.log(Level.INFO, mapping.toString());
	}

	/**
	 * @param censusTable
	 *            the censusTable to set
	 */
	public void setCensusTable(Table censusTable) {
		String[] columnNames = new String[] { "Nr.", "FT kolonne", "Individ", "Folket\u00E6llingsh\u00E6ndelse",
				"F\u00F8dselsh\u00E6ndelse", "Erhvervsh\u00E6ndelse" };

		List<String> headers = censusTable.getHeaders();
		rowCount = headers.size();
		data = new String[rowCount][columnNames.length];

		for (int i = 0; i < rowCount; i++) {
			data[i][0] = Integer.toString(i + 1);
			data[i][1] = headers.get(i);
			for (int j = 2; j < columnNames.length; j++) {
				data[i][j] = "Bruges ikke";
			}
		}

		DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames);
		mappingtable.setModel(defaultTableModel);

		JComboBox<String> individualcomboBox = new JComboBox<String>();
		individualcomboBox.addItem("Bruges ikke");
		individualcomboBox.addItem("Id");
		individualcomboBox.addItem("Navn");
		individualcomboBox.addItem("K�n");
		individualcomboBox.addItem("F�dsels�r");
		individualcomboBox.addItem("Alder");
		individualcomboBox.addItem("F�dested");
		individualcomboBox.addItem("Civilstand");
		individualcomboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// FIXME Must be the contents of the first column
				// TODO Find which cell editor is active in

				data[3][individualcomboBox.getSelectedIndex()] = individualcomboBox.getSelectedItem().toString();
				// mappingtable.getCellEditor().stopCellEditing();
			}
		});
		mappingtable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(individualcomboBox));

		JComboBox<String> censuscombobox = new JComboBox<String>();
		censuscombobox.addItem("Bruges ikke");
		censuscombobox.addItem("Alder");
		censuscombobox.addItem("�r");
		censuscombobox.addItem("Sted");
		mappingtable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(censuscombobox));

		JComboBox<String> birthcombox = new JComboBox<String>();
		birthcombox.addItem("Bruges ikke");
		birthcombox.addItem("F�dsels�r");
		birthcombox.addItem("F�dested");
		mappingtable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(birthcombox));

		JComboBox<String> tradecombox = new JComboBox<String>();
		tradecombox.addItem("Bruges ikke");
		tradecombox.addItem("�r");
		tradecombox.addItem("Erhverv");
		mappingtable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(tradecombox));

		okButton.setEnabled(true);
	}

}
