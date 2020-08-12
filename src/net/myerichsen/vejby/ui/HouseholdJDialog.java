package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.myerichsen.vejby.census.Family;
import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.census.Person;
import net.myerichsen.vejby.census.Table;

/**
 * The Household dialog displays a tree structure of households and families and
 * a table of persons in that entity.
 * 
 * @author Michael Erichsen
 * @version 13. aug. 2020
 *
 */
public class HouseholdJDialog extends JDialog {
	// private final static Logger LOGGER =
	// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static final long serialVersionUID = -324334468367664714L;
	private final JPanel contentPanel = new JPanel();
	private Table censusTable;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JTable table;
	private JButton btnNewButton;
	private JButton okButton;

	/**
	 * Create the dialog.
	 * 
	 * @param censusTable2
	 */
	public HouseholdJDialog(Table censusTable) {
		if (censusTable != null) {
			this.censusTable = censusTable;
		}
		setTitle("Husholdninger og familier i folket\u00E6llingen");
		setBounds(100, 100, 949, 565);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tree = new JTree();
			tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					populateTable(table);
				}
			});
			contentPanel.add(new JScrollPane(tree), BorderLayout.WEST);
			populateTree(tree);
		}
		{
			table = new JTable();
			table.setRowSelectionAllowed(false);
			contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Defin\u00E9r en ny familie");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Define a new family
					}
				});
				okButton.setEnabled(false);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{

				btnNewButton = new JButton("Gem \u00E6ndringer");
				btnNewButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Save changes to household
					}
				});
				btnNewButton.setEnabled(false);
				buttonPane.add(btnNewButton);
			}
			{
				JButton cancelButton = new JButton("Luk");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Drop any uncommitted changes, such as a new
						// family
						dispose();
						setVisible(false);
					}

				});

				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * 
	 * @param id
	 *            Person id in the census file
	 * @param families
	 *            A list of families in the household
	 * @return String A string in the format "Familie" nr rolle
	 */
	private String listFamiliesForPerson(int id, List<Family> families) {
		StringBuilder sb = new StringBuilder();
		for (Family family : families) {
			if ((family.getFather() != null) && (family.getFather().getId() == id)) {
				sb.append("Fader i familie " + family.getFamilyId() + ". ");
			} else if ((family.getMother() != null) && (family.getMother().getId() == id)) {
				sb.append("Moder i familie " + family.getFamilyId() + ". ");
			} else {
				for (Person child : family.getChildren()) {
					if (child.getId() == id) {
						sb.append("Barn i familie " + family.getFamilyId() + ". ");
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Populate table with family data. Rows contain father, mother and
	 * children.
	 */
	private void populateFamilyTable(int householdId, int familyId) {
		btnNewButton.setEnabled(false);
		okButton.setEnabled(false);
		Family family = censusTable.getFamily(householdId, familyId);

		String[] columnNames = new String[] { "Navn", "Køn", "Født", "Rolle" };
		String[][] data = family.getMembers();

		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		table.setModel(model);

	}

	/**
	 * Populate table with household data. Rows contains families and singles.
	 * 
	 * If a household is selected in the tree then display all persons in the
	 * household
	 * 
	 * Second last column marks 0-n family roles
	 * 
	 * Last column contains a combobox to select a new family roles Family is
	 * created when button is pressed It is saved by the save button
	 * 
	 * @param j
	 */
	private void populateHouseholdTable(int id) {
		btnNewButton.setEnabled(true);
		okButton.setEnabled(true);
		Household household = censusTable.getHouseholds().get(id);
		int size = household.getRows().size();
		List<Family> families = household.getFamilies();
		List<String> row;
		int personId;

		String[] columnNames = new String[] { "Navn", "Rolle", "Rolle i familie", "Ny rolle" };
		String[][] data = new String[size][columnNames.length];

		for (int i = 0; i < size; i++) {
			row = household.getRows().get(i);

			// Second column is id in census file 1845
			personId = Integer.parseInt(row.get(1));

			data[i][0] = row.get(5);
			data[i][1] = row.get(9);
			data[i][2] = listFamiliesForPerson(personId, families);
			data[i][3] = "";
		}

		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		table.setModel(model);

		JComboBox<String> currentRoleComboBox = new JComboBox<String>();
		currentRoleComboBox.addItem("");
		currentRoleComboBox.addItem("Fader i familie 1");
		currentRoleComboBox.addItem("Moder i familie 1");
		currentRoleComboBox.addItem("Barn i familie 1");
		currentRoleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Change families
			}
		});
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(currentRoleComboBox));

		JComboBox<String> newRoleComboBox = new JComboBox<String>();
		newRoleComboBox.addItem("");
		newRoleComboBox.addItem("Fader");
		newRoleComboBox.addItem("Moder");
		newRoleComboBox.addItem("Barn");
		newRoleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Change families
			}
		});
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(newRoleComboBox));
	}

	/**
	 * Populate the table with either a household or a family, depending on
	 * selecxtion the tree.
	 * 
	 * @param table
	 */
	private void populateTable(JTable table) {
		DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		Object userObject = lastSelectedPathComponent.getUserObject();

		if (userObject instanceof Household) {
			populateHouseholdTable(((Household) userObject).getId());
		} else if (userObject instanceof Family) {
			populateFamilyTable(((Family) userObject).getHouseholdId(), ((Family) userObject).getFamilyId());
		}

	}

	/**
	 * Populate tree structure with households and families.
	 */
	private void populateTree(JTree tree) {
		tree.setRootVisible(false);
		DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		if (rootTreeNode != null) {
			rootTreeNode.removeAllChildren();
		}
		DefaultMutableTreeNode householdNode;
		DefaultMutableTreeNode familyNode;

		for (Household household : censusTable.getHouseholds()) {
			householdNode = new DefaultMutableTreeNode(household);
			rootTreeNode.add(householdNode);

			for (Family family : household.getFamilies()) {
				familyNode = new DefaultMutableTreeNode(family);
				householdNode.add(familyNode);
			}
		}

		treeModel = ((DefaultTreeModel) tree.getModel());
		treeModel.reload(rootTreeNode);
	}

}