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
 * This JDialog displays five columns. The first is populated by the reduced
 * table headers. The others have a choice for each cell with the relevant
 * attributes for each type.
 * 
 * @author michael
 *
 */
public class CensusFieldMapJDialog extends JDialog {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
 
	private static final long serialVersionUID = -8669257676955258942L;
	private final JPanel mappingcontentPanel = new JPanel();
	private JTable mappingtable;
	private JButton okButton;
	private int rowCount;
	private String[][] data;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CensusFieldMapJDialog dialog = new CensusFieldMapJDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		gbl_mappingcontentPanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_mappingcontentPanel.rowWeights = new double[] { 0.0 };
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
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				 okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// TODO Does not set any data in rhe mapping object
						Mapping mapping = new Mapping(rowCount, 5);
						mapping.setMappingMatrix(data);
						LOGGER.log(Level.INFO, mapping.toString());

					}
				});
				okButton.setActionCommand("OK");
				okButton.setEnabled(false);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * @param censusTable
	 *            the censusTable to set
	 */
	public void setCensusTable(Table censusTable) {
		String[] columnNames = new String[] { "FT kolonne", "Individ", "Folket\u00E6llingsh\u00E6ndelse",
				"F\u00F8dselsh\u00E6ndelse", "Erhvervsh\u00E6ndelse" };

		List<String> headers = censusTable.getHeaders();
		rowCount = headers.size();
		 data = new String[rowCount][5];

		for (int i = 0; i < rowCount; i++) {
			data[i][0] = headers.get(i);
			for (int j = 1; j < 5; j++) {
				data[i][j] = "Bruges ikke";
			}
		}

		DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames);
		mappingtable.setModel(defaultTableModel);

		JComboBox<String> individualcomboBox = new JComboBox<String>();
		individualcomboBox.addItem("Bruges ikke");
		individualcomboBox.addItem("Navn");
		individualcomboBox.addItem("Køn");
		individualcomboBox.addItem("Fødselsår");
		individualcomboBox.addItem("Fødested");
		individualcomboBox.addItem("Ægtestand");
		mappingtable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(individualcomboBox));

		JComboBox<String> censuscombobox = new JComboBox<String>();
		censuscombobox.addItem("Bruges ikke");
		censuscombobox.addItem("År");
		censuscombobox.addItem("Sted");
		mappingtable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(censuscombobox));

		JComboBox<String> birthcombox = new JComboBox<String>();
		birthcombox.addItem("Bruges ikke");
		birthcombox.addItem("Fødselsår");
		birthcombox.addItem("Fødested");
		mappingtable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(birthcombox));

		JComboBox<String> tradecombox = new JComboBox<String>();
		tradecombox.addItem("Bruges ikke");
		tradecombox.addItem("År");
		tradecombox.addItem("Erhverv");
		mappingtable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(tradecombox));
		
		okButton.setEnabled(true);
	}

}
