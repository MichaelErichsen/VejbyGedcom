package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.census.Mapping;
import net.myerichsen.vejby.census.Table;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * Panel to display and define families in households. It displays a tree
 * structure of households and families and a table of persons.
 * 
 * @author Michael Erichsen
 * @version 17. aug. 2020
 *
 */
public class HouseholdJPanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -4694127991314617939L;
	private JTable table;
	private JButton saveButton;
	private JButton defineButton;
	private Table censusTable;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private VejbyGedcom vejbyGedcom;
	private Mapping mapping;

	/**
	 * Create the panel.
	 * 
	 * @param vejbyGedcom
	 */
	public HouseholdJPanel(VejbyGedcom vejbyGedcom) {
		this.vejbyGedcom = vejbyGedcom;

		setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				populateTable(table);
			}
		});
		add(new JScrollPane(tree), BorderLayout.WEST);
		// populateTree(tree);

		table = new JTable();
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		defineButton = new JButton("Defin\u00E9r en ny familie");
		defineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Define a new family from cell editor inputs and update
				// tree and table
			}
		});
		buttonPanel.add(defineButton);

		saveButton = new JButton("Gem som GEDCOM");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GedcomFile gedcomFile = GedcomFile.getInstance();
				gedcomFile.save(censusTable);
			}
		});
		buttonPanel.add(saveButton);
	}

	/**
	 * 
	 * @param id
	 *            Individual id in the census file
	 * @param families
	 *            A list of families in the household
	 * @return String A string in the format "Familie" nr rolle
	 */
	private String listFamiliesForIndividual(int id, List<Family> families) {
		StringBuilder sb = new StringBuilder();
		for (net.myerichsen.vejby.gedcom.Family family : families) {
			if ((family.getFather() != null) && (family.getFather().getId() == id)) {
				sb.append("Fader i familie " + family.getFamilyId() + ". ");
			} else if ((family.getMother() != null) && (family.getMother().getId() == id)) {
				sb.append("Moder i familie " + family.getFamilyId() + ". ");
			} else {
				for (Individual child : family.getChildren()) {
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
		defineButton.setEnabled(true);
		saveButton.setEnabled(true);
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
		defineButton.setEnabled(true);
		saveButton.setEnabled(true);
		List<String> row;
		int personId;
		Household household = censusTable.getHouseholds().get(id);
		int size = household.getRows().size();
		List<Family> families = household.getFamilies();
		int[] mappingKeys = mapping.getMappingKeys();

		String[] columnNames = new String[] { "Navn", "Rolle", "Rolle i familie", "Ny rolle" };
		String[][] data = new String[size][columnNames.length];

		for (int i = 0; i < size; i++) {
			row = household.getRows().get(i);

			personId = Integer.parseInt(row.get(mappingKeys[1]));

			data[i][0] = row.get(mappingKeys[4]);
			data[i][1] = row.get(mappingKeys[8]);
			data[i][2] = listFamiliesForIndividual(personId, families);
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
	 * @param censusModel
	 * @param mappingModel
	 */
	public void populateTree(Mapping mapping) {
		this.mapping = mapping;
		String message;
		DefaultMutableTreeNode householdNode;
		DefaultMutableTreeNode familyNode;

		tree.setRootVisible(false);
		DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		if (rootTreeNode != null) {
			rootTreeNode.removeAllChildren();
		}

		int[] mappingKeys = mapping.getMappingKeys();

		censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();

		// Create households
		if (mappingKeys[3] != 0) {
			message = censusTable.createHouseholds(mappingKeys[3]);
			LOGGER.log(Level.FINE, message);

			// Create a family for each household
			if (mappingKeys[5] != 0) {
				for (Household household : censusTable.getHouseholds()) {
					message = household.identifyFamilies(mappingKeys);
					LOGGER.log(Level.FINE, message);
				}
			}

			// Add household to tree
			for (Household household : censusTable.getHouseholds()) {
				householdNode = new DefaultMutableTreeNode(household);
				rootTreeNode.add(householdNode);

				// Add family to tree
				for (Family family : household.getFamilies()) {
					familyNode = new DefaultMutableTreeNode(family);
					householdNode.add(familyNode);
				}
			}
		}

		treeModel = ((DefaultTreeModel) tree.getModel());
		treeModel.reload(rootTreeNode);
	}

}
