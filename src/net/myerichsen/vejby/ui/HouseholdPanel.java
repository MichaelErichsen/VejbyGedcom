package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
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
import net.myerichsen.vejby.census.Table;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;
import net.myerichsen.vejby.util.Mapping;

/**
 * Panel to display and define families in households. It displays a tree
 * structure of households and families and a table of persons in the selected
 * entity.
 * <p>
 * The panel supports manual changes to the generated family structure by
 * definitions of up to three families.
 * 
 * @version 22. aug. 2020
 * @author Michael Erichsen
 * 
 */
public class HouseholdPanel extends JPanel {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -4694127991314617939L;

	private JTable table;
	private Table censusTable;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private VejbyGedcom vejbyGedcom;
	private Mapping mapping;
	private Household selectedHousehold;
	private DefaultMutableTreeNode rootTreeNode;
	private List<List<String>> familyRoleList;
	private DefaultTableModel householdTableModel;
	private JButton family1Button;
	private JButton family2Button;
	private JButton family3Button;
	private JButton saveButton;

	/**
	 * Create the panel.
	 * 
	 * @param vejbyGedcom
	 */
	public HouseholdPanel(VejbyGedcom vejbyGedcom) {
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

		table = new JTable();
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		family1Button = new JButton("Opdat\u00E9r familie 1");
		family1Button.setEnabled(false);
		family1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redefineFirstFamily();
			}
		});
		buttonPanel.add(family1Button);

		family2Button = new JButton("Opdat\u00E9r familie 2");
		family2Button.setEnabled(false);
		family2Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonPanel.add(family2Button);

		family3Button = new JButton("Opdat\u00E9r familie 3");
		family3Button.setEnabled(false);
		family3Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		buttonPanel.add(family3Button);

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
	 * Create text for family row cell
	 * 
	 * @param id       Individual id in the census file
	 * @param families A list of families in the household
	 * @return A string listing family number and role therein
	 */
	private String listFamiliesForIndividual(int id, List<Family> families) {
		StringBuilder sb = new StringBuilder();

		for (net.myerichsen.vejby.gedcom.Family family : families) {
			if ((family.getFather() != null) && (family.getFather().getId() == id)) {
				sb.append("Fader");
				return sb.toString();

			} else if ((family.getMother() != null) && (family.getMother().getId() == id)) {
				sb.append("Moder");
				return sb.toString();

			} else {
				for (Individual child : family.getChildren()) {
					if (child.getId() == id) {
						sb.append("Barn");
						return sb.toString();
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Populate table with family data. Rows contain father, mother and children.
	 * 
	 * @param householdId The id of the household
	 * @param familyId    The id of the family in the household
	 */
	private void populateFamilyTable(int householdId, int familyId) {
		Family family = censusTable.getFamily(householdId, familyId);

		String[] columnNames = new String[] { "Navn", "K�n", "F�dt", "Rolle" };
		String[][] data = family.getMembers();

		DefaultTableModel familyTableModel = new DefaultTableModel(data, columnNames);
		table.setModel(familyTableModel);
	}

	/**
	 * Populate table with household data. Rows contains families and singles.
	 * <p>
	 * If a household is selected in the tree then display all persons in the
	 * household.
	 * <p>
	 * Next columns marks up to three family roles and can be edited. Each contains
	 * a combobox to select a new family role. A second and a third Family is
	 * created when button is pressed.
	 * 
	 * @param id The id of the household
	 */
	private void populateHouseholdTable(int id) {
		List<String> row;
		int personId;

		// Get the household from the census table
		selectedHousehold = censusTable.getHouseholds().get(id);
		List<List<String>> rows = selectedHousehold.getRows();
		int size = rows.size();

		// Get all families in the household
		List<Family> families = selectedHousehold.getFamilies();
		int[] mappingKeys = mapping.getMappingKeys();

		// Get all manually entered family roles in the household
		familyRoleList = selectedHousehold.getFamilyRoleList(size);

		// Populate table
		String[] columnNames = new String[] { "L�benr", "Navn", "Civilstand", "Erhverv", "Familie 1", "Familie 2",
				"Familie 3" };
		String[][] data = new String[size][columnNames.length];
		String fr;

		for (int i = 0; i < size; i++) {
			row = rows.get(i);

			personId = Integer.parseInt(row.get(mappingKeys[1]));

			data[i][0] = row.get(mappingKeys[1]);
			data[i][1] = row.get(mappingKeys[4]);
			data[i][2] = row.get(mappingKeys[8]);
			data[i][3] = row.get(mappingKeys[9]);

			// Use manual entry if existing, otherwise insert rows data in family 1 column
			if (familyRoleList.get(i).isEmpty()) {
				fr = listFamiliesForIndividual(personId, families);
				data[i][4] = fr;
				familyRoleList.get(i).add(fr);
			} else {
				data[i][4] = familyRoleList.get(i).get(0);
			}

			data[i][5] = "";
		}

		householdTableModel = new DefaultTableModel(data, columnNames);
		table.setModel(householdTableModel);

		// Populate combo boxes for cell editors
		JComboBox<String> familyRoleComboBox = new JComboBox<>();
		familyRoleComboBox.addItem("");
		familyRoleComboBox.addItem("Fader");
		familyRoleComboBox.addItem("Moder");
		familyRoleComboBox.addItem("Barn");
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
		table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
		table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
	}

	/**
	 * Populate the table with either a household or a family, depending on
	 * selection the tree.
	 * 
	 * @param table The visible table
	 */
	private void populateTable(JTable table) {
		DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		Object userObject = lastSelectedPathComponent.getUserObject();

		// Populate either table content
		if (userObject instanceof Household) {
			populateHouseholdTable(((Household) userObject).getId());
		} else if (userObject instanceof Family) {
			populateFamilyTable(((Family) userObject).getHouseholdId(), ((Family) userObject).getFamilyId());
		}

		family1Button.setEnabled(true);
		family2Button.setEnabled(true);
		family3Button.setEnabled(true);
	}

	/**
	 * Populate the tree structure with all households and all families in each
	 * household.
	 * 
	 * @param mapping The mapping definition as created in CensusMappingPanel
	 * @see net.myerichsen.vejby.ui.CensusMappingPanel.Class
	 */
	public void populateTree() {
		mapping = Mapping.getInstance();
		String message;
		DefaultMutableTreeNode householdNode;
		DefaultMutableTreeNode familyNode;

		tree.setRootVisible(false);
		rootTreeNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		if (rootTreeNode != null) {
			rootTreeNode.removeAllChildren();
		}

		int[] mappingKeys = mapping.getMappingKeys();

		censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();

		// Create households
		if (mappingKeys[3] != 0) {
			message = censusTable.createHouseholds(mappingKeys[3]);
			LOGGER.log(Level.INFO, message);

			// Create a family for each household
			if (mappingKeys[5] != 0) {
				for (Household household : censusTable.getHouseholds()) {
					message = household.identifyFamilies();
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

	/**
	 * Delete and redefine the first family.
	 */
	private void redefineFirstFamily() {
		Individual ind;
		List<String> row;

		// Remove all families from household
		selectedHousehold.getFamilies().clear();

		// Create a new family
		Family firstFamily = new Family(selectedHousehold.getId(), 1);

		householdTableModel = (DefaultTableModel) table.getModel();

		@SuppressWarnings("unchecked")
		Vector<Vector<String>> dataVector = householdTableModel.getDataVector();
		Vector<String> vector2;

		List<List<String>> rows = selectedHousehold.getRows();

		for (int i = 0; i < dataVector.size(); i++) {
			vector2 = dataVector.get(i);
			String newRole = vector2.get(4);
			LOGGER.log(Level.INFO, "New role " + i + ": " + newRole);

			row = rows.get(i);

			ind = selectedHousehold.createIndividual(row);

			LOGGER.log(Level.INFO, "L�benr. " + vector2.get(0) + ", " + vector2.get(1) + ", " + vector2.get(2) + ", "
					+ vector2.get(3) + ", " + vector2.get(4) + ", " + vector2.get(5));
			if (newRole.startsWith("F")) {
				firstFamily.setFather(ind);
			} else if (newRole.startsWith("M")) {
				firstFamily.setMother(ind);
			} else {
				firstFamily.getChildren().add(ind);
			}

			familyRoleList.get(i).add(newRole);
			treeModel.reload(rootTreeNode);
			table.setModel(householdTableModel);
		}

		selectedHousehold.getFamilies().add(firstFamily);
	}
}
