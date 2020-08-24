package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * definitions of up to three families.
 * 
 * @version 24. aug. 2020
 * @author Michael Erichsen
 * 
 */
public class HouseholdPanel extends JPanel {
//	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final long serialVersionUID = -4694127991314617939L;

	private Household selectedHousehold;
	private VejbyGedcom vejbyGedcom;
	private Census censusTable;

	private JTable table;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootTreeNode;
	private DefaultTableModel householdTableModel;
	private JButton family1Button;
	private JButton family2Button;
	private JButton family3Button;
	private JButton saveButton;

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

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);

		family1Button = new JButton("Opdat\u00E9r familie 1");
		family1Button.setEnabled(false);
		family1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFamily1();
			}
		});
		buttonPanel.add(family1Button);

		family2Button = new JButton("Opdat\u00E9r familie 2");
		family2Button.setEnabled(false);
		family2Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFamily2();
			}
		});
		buttonPanel.add(family2Button);

		family3Button = new JButton("Opdat\u00E9r familie 3");
		family3Button.setEnabled(false);
		family3Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFamily3();
			}
		});
		buttonPanel.add(family3Button);

		saveButton = new JButton("Gem som GEDCOM");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				censusTable = vejbyGedcom.getCensusJPanel().getCensusTable();
				GedcomFile gedcomFile = GedcomFile.getInstance();
				String path = gedcomFile.save(censusTable);

				JOptionPane.showMessageDialog(new JFrame(),
						"Folketælling for " + censusTable.getYear() + " er gemt som GEDCOM fil " + path, "Vejby Gedcom",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPanel.add(saveButton);
	}

	/**
	 * Populate table with family data. Rows contain father, mother and children.
	 * 
	 * @param householdId The id of the household
	 * @param familyId    The id of the family in the household
	 */
	private void populateFamilyTable(int householdId, int familyId) {
		Family family = censusTable.getFamily(householdId, familyId);

		String[] columnNames = new String[] { "Løbenr", "Navn", "Civilstand", "Erhverv", "Rolle" };
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
	 * created, when button is pressed.
	 * 
	 * @param id The id of the household
	 */
	private void populateHouseholdTable(int id) {
		Individual person;

		selectedHousehold = censusTable.getHouseholds().get(id);

		// Create table
		String[] columnNames = new String[] { "Løbenr", "Navn", "Civilstand", "Erhverv", "Familie 1", "Familie 2",
				"Familie 3" };
		int size = selectedHousehold.getPersonCount();
		String[][] data = new String[size][columnNames.length];

		for (int i = 0; i < size; i++) {
			person = selectedHousehold.getPerson(i);
			data[i][0] = String.valueOf(person.getId());
			data[i][1] = person.getName();
			data[i][2] = person.getMaritalStatus();
			data[i][3] = person.getTrade();
			data[i][4] = person.getFamilyRole1();
			data[i][5] = person.getFamilyRole2();
			data[i][6] = person.getFamilyRole3();
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

		for (Household household : censusTable.getHouseholds()) {
			householdNode = new DefaultMutableTreeNode(household);
			rootTreeNode.add(householdNode);

			// Add families to tree
			for (Family family : household.getFamilies()) {
				familyNode = new DefaultMutableTreeNode(family);
				householdNode.add(familyNode);
			}
		}

		treeModel = ((DefaultTreeModel) tree.getModel());
		treeModel.reload(rootTreeNode);
	}

	/**
	 * Update the singles list (family 0) and the first family.
	 */
	private void updateFamily1() {
		Individual individual;

		selectedHousehold.getFamilies().clear();

		Family family0 = new Family(selectedHousehold.getId(), 0);
		Family family1 = new Family(selectedHousehold.getId(), 1);

		@SuppressWarnings("unchecked")
		Vector<Vector<String>> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role
			String newRole = tableRowVector.get(4);

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
	protected void updateFamily2() {
		Individual individual;

		// Get existing list of singletons
		Family oldFamily0 = selectedHousehold.getFamilies().get(0);

		// Create a new family 2
		Family family2 = new Family(selectedHousehold.getId(), 2);

		// Read all rows and add to family 2
		@SuppressWarnings("unchecked")
		Vector<Vector<String>> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role in family 2
			String newRole = tableRowVector.get(5);

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
		Family family0 = new Family(selectedHousehold.getId(), 0);

		// Read all rows and add unassigned to singles list
		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);

			individual = selectedHousehold.getPerson(i);

			if ((individual.getFamilyRole1().equals("")) && (individual.getFamilyRole2().equals(""))
					&& (individual.getFamilyRole3().equals(""))) {
				family0.getSingles().add(individual);
			}
		}

		// Replace old family 0 with new family 0
		selectedHousehold.getFamilies().remove(oldFamily0);
		selectedHousehold.getFamilies().add(0, family0);

		// Update tree
		DefaultMutableTreeNode householdNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		DefaultMutableTreeNode family2Node = new DefaultMutableTreeNode(family2);
		householdNode.add(family2Node);
		treeModel.reload(rootTreeNode);
	}

	/**
	 * Update the singles list (family 0) and the third family.
	 */
	protected void updateFamily3() {
		Individual individual;

		// Get existing list of singletons
		Family oldFamily0 = selectedHousehold.getFamilies().get(0);

		// Create a new family 3
		Family family3 = new Family(selectedHousehold.getId(), 3);

		// Read all rows and add to family 3
		@SuppressWarnings("unchecked")
		Vector<Vector<String>> dataVector = householdTableModel.getDataVector();
		Vector<String> tableRowVector;

		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);
			individual = selectedHousehold.getPerson(i);

			// Set new role in family 3
			String newRole = tableRowVector.get(6);

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
		Family family0 = new Family(selectedHousehold.getId(), 0);

		// Read all rows and add unassigned to singles list
		for (int i = 0; i < dataVector.size(); i++) {
			tableRowVector = dataVector.get(i);

			individual = selectedHousehold.getPerson(i);

			if ((individual.getFamilyRole1().equals("")) && (individual.getFamilyRole2().equals(""))
					&& (individual.getFamilyRole3().equals(""))) {
				family0.getSingles().add(individual);
			}
		}

		// Replace old family 0 with new family 0
		selectedHousehold.getFamilies().remove(oldFamily0);
		selectedHousehold.getFamilies().add(0, family0);

		// Update tree
		DefaultMutableTreeNode householdNode = (DefaultMutableTreeNode) rootTreeNode
				.getChildAt(selectedHousehold.getId());
		DefaultMutableTreeNode family3Node = new DefaultMutableTreeNode(family3);
		householdNode.add(family3Node);
		treeModel.reload(rootTreeNode);
		;
	}
}
