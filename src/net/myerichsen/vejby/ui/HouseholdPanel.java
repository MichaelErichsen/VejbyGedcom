package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.myerichsen.vejby.census.Census;
import net.myerichsen.vejby.census.Household;
import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;

/**
 * Panel to display and define families in households. It displays a tree
 * structure of households and families and a table of persons in the selected
 * entity.
 * <p>
 * The panel supports manual changes to the generated family structure by
 * definitions of up to four families.
 *
 * @version 6. jul.-2023
 * @author Michael Erichsen
 *
 */
public class HouseholdPanel extends JPanel {
	// private final static Logger LOGGER =
	// Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -4694127991314617939L;

	private Household selectedHousehold;
	private final VejbyGedcom vejbyGedcom;
	private Census censusTable;

	private final JTable table;
	private final JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootTreeNode;
	private DefaultTableModel householdTableModel;
	private final JButton family1Button;
	private final JButton family2Button;
	private final JButton family3Button;
	private final JButton family4Button;
	private final JButton delete2Button;
	private final JButton delete3Button;
	private final JButton delete4Button;
	private final JButton saveButton;
	private final JButton clear1Button;

	/**
	 * Create the panel.
	 *
	 * @param vejbyGedcom The main panel of the application
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

		final JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		family1Button = new JButton("Opdat\u00E9r familie 1");
		family1Button.setEnabled(false);
		family1Button.addActionListener(e -> updateFamily1());
		buttonPanel.setLayout(new GridLayout(0, 5, 0, 0));
		buttonPanel.add(family1Button);

		family2Button = new JButton("Opdat\u00E9r familie 1 og 2");
		family2Button.setEnabled(false);
		family2Button.addActionListener(e -> {
			updateFamily1();
			updateFamily2();
		});
		buttonPanel.add(family2Button);

		family3Button = new JButton("Opdat\u00E9r familie 1, 2 og 3");
		family3Button.setEnabled(false);
		family3Button.addActionListener(e -> {
			updateFamily1();
			updateFamily2();
			updateFamily3();
		});
		buttonPanel.add(family3Button);

		family4Button = new JButton("Opdat\u00E9r familie 1, 2, 3 og 4\r\n");
		family4Button.setEnabled(false);
		family4Button.addActionListener(e -> {
			updateFamily1();
			updateFamily2();
			updateFamily3();
			updateFamily4();
		});
		buttonPanel.add(family4Button);

		clear1Button = new JButton("Ryd familie 1 herfra");
		clear1Button.addActionListener(e -> clearFamily1FromCursor());
		clear1Button.setEnabled(false);
		buttonPanel.add(clear1Button);

		delete2Button = new JButton("Slet familie 2, 3 og 4\r\n");
		delete2Button.setEnabled(false);
		delete2Button.addActionListener(e -> {
			deleteFamily4();
			deleteFamily3();
			deleteFamily2();
		});

		buttonPanel.add(delete2Button);

		delete3Button = new JButton("Slet familie 3 og 4");
		delete3Button.setEnabled(false);
		delete3Button.addActionListener(e -> {
			deleteFamily4();
			deleteFamily3();
		});
		buttonPanel.add(delete3Button);

		delete4Button = new JButton("Slet familie 4");
		delete4Button.setEnabled(false);
		delete4Button.addActionListener(e -> deleteFamily4());
		buttonPanel.add(delete4Button);

		saveButton = new JButton("Gem som GEDCOM");
		saveButton.addActionListener(e -> {
			censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();
			// GedcomFile gedcomFile = GedcomFile.getInstance();
			final GedcomFile gedcomFile = new GedcomFile();
			final String path = gedcomFile.saveCensus(censusTable);

			JOptionPane.showMessageDialog(new JFrame(),
					"Folket�lling for " + censusTable.getYear() + " er gemt som GEDCOM fil " + path, "Vejby Gedcom",
					JOptionPane.INFORMATION_MESSAGE);
		});

		buttonPanel.add(saveButton);
	}

	/**
	 * Clear the family 1 column from the cursor and downwards
	 */
	protected void clearFamily1FromCursor() {
		for (int i = table.getSelectedRow(); i < table.getRowCount(); i++) {
			table.setValueAt("", i, 5);
		}
	}

	/**
	 * For each individual in the household:
	 * <p>
	 * Set requested family role to spaces
	 *
	 * @param i Family number
	 */
	private void clearFamilyRoles(int i) {
		for (final Individual individual : selectedHousehold.getPersons()) {
			switch (i) {
			case 2:
				individual.setFamilyRole2("");
				break;
			case 3:
				individual.setFamilyRole3("");
				break;
			case 4:
				individual.setFamilyRole4("");
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Delete family 2
	 */
	protected void deleteFamily2() {
		Family family2 = selectedHousehold.getFamilies().get(2);
		selectedHousehold.getFamilies().remove(family2);
		final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		try {
			currentNode.remove(3);
		} catch (final Exception ignoredException) {
		}
		currentNode.remove(2);
		treeModel.reload(rootTreeNode);
		family2 = null;
		clearFamilyRoles(2);
		rebuildSinglesList(householdTableModel.getDataVector(), selectedHousehold.getFamilies().get(0));
	}

	/**
	 * Delete family 3
	 */
	protected void deleteFamily3() {
		Family family3 = selectedHousehold.getFamilies().get(3);
		selectedHousehold.getFamilies().remove(family3);
		rootTreeNode.remove(3);
		treeModel.reload(rootTreeNode);
		family3 = null;
		clearFamilyRoles(3);
		rebuildSinglesList(householdTableModel.getDataVector(), selectedHousehold.getFamilies().get(0));
	}

	/**
	 * Delete family 4
	 */
	protected void deleteFamily4() {
		Family family4 = selectedHousehold.getFamilies().get(4);
		selectedHousehold.getFamilies().remove(family4);
		rootTreeNode.remove(4);
		treeModel.reload(rootTreeNode);
		family4 = null;
		clearFamilyRoles(4);
		rebuildSinglesList(householdTableModel.getDataVector(), selectedHousehold.getFamilies().get(0));
	}

	/**
	 * Populate table with family data. Rows contain father, mother and children.
	 *
	 * @param householdId The id of the household
	 * @param familyId    The id of the family in the household
	 */
	private void populateFamilyTable(int householdId, int familyId) {
		final Family family = censusTable.getFamily(householdId, familyId);

		final String[] columnNames = { "L�benr", "Navn", "Civilstand", "Erhverv", "Rolle" };
		final String[][] data = family.getMembers();

		final DefaultTableModel familyTableModel = new DefaultTableModel(data, columnNames);
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
	 * created, when button is pressed.
	 *
	 * @param id The id of the household
	 */
	private void populateHouseholdTable(int id) {
		Individual person;

		selectedHousehold = censusTable.getHouseholds().get(id);

		// Create table
		final String[] columnNames = { "L�benr", "Navn", "Civilstand", "Erhverv", "Stilling", "Familie 1", "Familie 2",
				"Familie 3", "Familie 4" };
		final int size = selectedHousehold.getPersonCount();
		final String[][] data = new String[size][columnNames.length];

		for (int i = 0; i < size; i++) {
			person = selectedHousehold.getPerson(i);
			data[i][0] = String.valueOf(person.getId());
			data[i][1] = person.getName();
			data[i][2] = person.getMaritalStatus();
			data[i][3] = person.getTrade();
			data[i][4] = person.getPosition();
			data[i][5] = person.getFamilyRole1();
			data[i][6] = person.getFamilyRole2();
			data[i][7] = person.getFamilyRole3();
			data[i][8] = person.getFamilyRole4();
		}

		householdTableModel = new DefaultTableModel(data, columnNames);
		table.setModel(householdTableModel);

		// Populate combo boxes for cell editors
		final JComboBox<String> familyRoleComboBox = new JComboBox<>();
		familyRoleComboBox.addItem("");
		familyRoleComboBox.addItem("Fader");
		familyRoleComboBox.addItem("Moder");
		familyRoleComboBox.addItem("Barn");
		table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
		table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
		table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
		table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(familyRoleComboBox));
	}

	/**
	 * Populate the table with either a household or a family, depending on
	 * selection the tree.
	 *
	 * @param table The visible table
	 */
	private void populateTable(JTable table) {
		final DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		final Object userObject = lastSelectedPathComponent.getUserObject();

		// Populate either table content
		if (userObject instanceof Household) {
			populateHouseholdTable(((Household) userObject).getId());
		} else if (userObject instanceof Family) {
			populateFamilyTable(((Family) userObject).getHouseholdId(), ((Family) userObject).getFamilyId());
		}

		family1Button.setEnabled(true);
		family2Button.setEnabled(true);
		family3Button.setEnabled(true);
		family4Button.setEnabled(true);
		clear1Button.setEnabled(true);
	}

	/**
	 * Populate the tree structure with all households and all families in each
	 * household.
	 *
	 */
	public void populateTree() {
		DefaultMutableTreeNode householdNode;
		DefaultMutableTreeNode familyNode;

		tree.setRootVisible(false);
		rootTreeNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		if (rootTreeNode != null) {
			rootTreeNode.removeAllChildren();
		}

		// Add households to tree
		censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();

		for (final Household household : censusTable.getHouseholds()) {
			householdNode = new DefaultMutableTreeNode(household);
			rootTreeNode.add(householdNode);

			// Add families to tree
			for (final Family family : household.getFamilies()) {
				familyNode = new DefaultMutableTreeNode(family);
				householdNode.add(familyNode);
			}
		}

		treeModel = (DefaultTreeModel) tree.getModel();
		treeModel.reload(rootTreeNode);
	}

	/**
	 * @param dataVector
	 * @param family0
	 */
	public void rebuildSinglesList(@SuppressWarnings("rawtypes") Vector<Vector> dataVector, Family family0) {
		Individual individual;

		// Read all rows and add unassigned to singles list
		for (int i = 0; i < dataVector.size(); i++) {
			individual = selectedHousehold.getPerson(i);

			if ("".equals(individual.getFamilyRole1()) && "".equals(individual.getFamilyRole2())
					&& "".equals(individual.getFamilyRole3())) {
				family0.getSingles().add(individual);
			}
		}
	}

	/**
	 * Update the singles list (family 0) and the first family.
	 */
	@SuppressWarnings("unchecked")
	private void updateFamily1() {
		Individual individual;

		final TreePath selectedNode = tree.getSelectionPath();

		selectedHousehold.getFamilies().clear();

		final Family family0 = new Family(selectedHousehold.getId(), 0);
		final Family family1 = new Family(selectedHousehold.getId(), 1);

		@SuppressWarnings("rawtypes")
		final Vector<Vector> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role
			final String newRole = tableRowVector.get(5);

			if (newRole.startsWith("F")) {
				family1.setFather(individual);
			} else if (newRole.startsWith("M")) {
				family1.setMother(individual);
			} else if (newRole.startsWith("B")) {
				family1.getChildren().add(individual);
			} else {
				family0.getSingles().add(individual);
			}

			individual.setFamilyRole1(newRole);
		}

		selectedHousehold.getFamilies().add(family0);
		selectedHousehold.getFamilies().add(family1);

		treeModel.reload(rootTreeNode);
		tree.setSelectionPath(selectedNode);
		table.setModel(householdTableModel);
	}

	/**
	 * Update the singles list (family 0) and the second family.
	 * <p>
	 * The list starts empty. Each individual added must be removed from family 0,
	 * but not from 1 or 3.
	 * <p>
	 * If this cannot be done concurrently, then family 0 must be deleted and then
	 * populated by all individuals not in families 1-3.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateFamily2() {
		Individual individual;

		final TreePath selectedNode = tree.getSelectionPath();

		// Get existing list of singletons
		final Family oldFamily0 = selectedHousehold.getFamilies().get(0);

		// Create a new family 2
		final Family family2 = new Family(selectedHousehold.getId(), 2);

		// Read all rows and add to family 2
		final Vector<Vector> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role in family 2
			final String newRole = tableRowVector.get(6);

			if (newRole.startsWith("F")) {
				family2.setFather(individual);
			} else if (newRole.startsWith("M")) {
				family2.setMother(individual);
			} else if (newRole.startsWith("B")) {
				family2.getChildren().add(individual);
			}

			individual.setFamilyRole2(newRole);
		}

		selectedHousehold.getFamilies().add(family2);

		// Create a new family 0 with all unassigned individuals
		final Family family0 = new Family(selectedHousehold.getId(), 0);
		rebuildSinglesList(dataVector, family0);

		// Replace old family 0 with new family 0
		selectedHousehold.getFamilies().remove(oldFamily0);
		selectedHousehold.getFamilies().add(0, family0);

		// Update tree
		final DefaultMutableTreeNode householdNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		final DefaultMutableTreeNode family2Node = new DefaultMutableTreeNode(family2);
		householdNode.add(family2Node);
		treeModel.reload(rootTreeNode);
		tree.setSelectionPath(selectedNode);

		delete2Button.setEnabled(true);
	}

	/**
	 * Update the singles list (family 0) and the third family.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateFamily3() {
		Individual individual;

		final TreePath selectedNode = tree.getSelectionPath();

		// Get existing list of singletons
		final Family oldFamily0 = selectedHousehold.getFamilies().get(0);

		// Create a new family 3
		final Family family3 = new Family(selectedHousehold.getId(), 3);

		// Read all rows and add to family 3
		final Vector<Vector> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role in family 3
			final String newRole = tableRowVector.get(7);

			if (newRole.startsWith("F")) {
				family3.setFather(individual);
			} else if (newRole.startsWith("M")) {
				family3.setMother(individual);
			} else if (newRole.startsWith("B")) {
				family3.getChildren().add(individual);
			}

			individual.setFamilyRole3(newRole);
		}

		selectedHousehold.getFamilies().add(family3);

		// Create a new family 0 with all unassigned individuals
		final Family family0 = new Family(selectedHousehold.getId(), 0);
		rebuildSinglesList(dataVector, family0);

		// Replace old family 0 with new family 0
		selectedHousehold.getFamilies().remove(oldFamily0);
		selectedHousehold.getFamilies().add(0, family0);

		// Update tree
		final DefaultMutableTreeNode householdNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		final DefaultMutableTreeNode family3Node = new DefaultMutableTreeNode(family3);
		householdNode.add(family3Node);
		treeModel.reload(rootTreeNode);
		tree.setSelectionPath(selectedNode);

		delete3Button.setEnabled(true);
	}

	/**
	 * Update the singles list (family 0) and the fourth family.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateFamily4() {
		Individual individual;

		final TreePath selectedNode = tree.getSelectionPath();

		// Get existing list of singletons
		final Family oldFamily0 = selectedHousehold.getFamilies().get(0);

		// Create a new family 3
		final Family family4 = new Family(selectedHousehold.getId(), 4);

		// Read all rows and add to family 3
		final Vector<Vector> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role in family 3
			final String newRole = tableRowVector.get(8);

			if (newRole.startsWith("F")) {
				family4.setFather(individual);
			} else if (newRole.startsWith("M")) {
				family4.setMother(individual);
			} else if (newRole.startsWith("B")) {
				family4.getChildren().add(individual);
			}

			individual.setFamilyRole4(newRole);
		}

		selectedHousehold.getFamilies().add(family4);

		// Create a new family 0 with all unassigned individuals
		final Family family0 = new Family(selectedHousehold.getId(), 0);
		rebuildSinglesList(dataVector, family0);

		// Replace old family 0 with new family 0
		selectedHousehold.getFamilies().remove(oldFamily0);
		selectedHousehold.getFamilies().add(0, family0);

		// Update tree
		final DefaultMutableTreeNode householdNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		final DefaultMutableTreeNode family4Node = new DefaultMutableTreeNode(family4);
		householdNode.add(family4Node);
		treeModel.reload(rootTreeNode);
		tree.setSelectionPath(selectedNode);

		delete4Button.setEnabled(true);
	}
}
