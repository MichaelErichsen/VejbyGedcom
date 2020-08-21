package net.myerichsen.vejby.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;
import net.myerichsen.vejby.gedcom.Sex;

/**
 * Data entry panel for church registry baptism entries.
 * 
 * @version 21. aug. 2020
 * @author Michael Erichsen
 *
 */
public class BaptismPanel extends JPanel {
	private static final long serialVersionUID = 2704936588696233694L;

	private JTextField nametextField;
	private JComboBox<String> sexComboBox;
	private JTextField fatherNametextField;
	private JTextField fatherTradetextField;
	private JTextField fatherAddresstextField;
	private JTextField motherNametextField;
	private JTextField motherAddresstextField;
	private JDateChooser birthdateChooser;
	private JDateChooser homeBaptismdateChooser;
	private JDateChooser baptismdateChooser;
	private JEditorPane godParentseditorPane;

	/**
	 * Create the panel.
	 */
	public BaptismPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		JLabel lblFdselsdato = new JLabel("F\u00F8dselsdato");
		GridBagConstraints gbc_lblFdselsdato = new GridBagConstraints();
		gbc_lblFdselsdato.insets = new Insets(0, 0, 5, 5);
		gbc_lblFdselsdato.gridx = 0;
		gbc_lblFdselsdato.gridy = 0;
		add(lblFdselsdato, gbc_lblFdselsdato);

		JDateChooser birthdateChooser = new JDateChooser();
		birthdateChooser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				birthDateChanged();
			}
		});
		GridBagConstraints gbc_dateChooser = new GridBagConstraints();
		gbc_dateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_dateChooser.fill = GridBagConstraints.BOTH;
		gbc_dateChooser.gridx = 1;
		gbc_dateChooser.gridy = 0;
		add(birthdateChooser, gbc_dateChooser);

		JLabel lblNavn = new JLabel("Navn");
		GridBagConstraints gbc_lblNavn = new GridBagConstraints();
		gbc_lblNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblNavn.gridx = 0;
		gbc_lblNavn.gridy = 1;
		add(lblNavn, gbc_lblNavn);

		nametextField = new JTextField();
		GridBagConstraints gbc_nametextField = new GridBagConstraints();
		gbc_nametextField.insets = new Insets(0, 0, 5, 0);
		gbc_nametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nametextField.gridx = 1;
		gbc_nametextField.gridy = 1;
		add(nametextField, gbc_nametextField);
		nametextField.setColumns(10);

		JLabel lblSex = new JLabel("K\u00F8n");
		GridBagConstraints gbc_lblSex = new GridBagConstraints();
		gbc_lblSex.insets = new Insets(0, 0, 5, 5);
		gbc_lblSex.gridx = 0;
		gbc_lblSex.gridy = 2;
		add(lblSex, gbc_lblSex);

		sexComboBox = new JComboBox<String>();
		sexComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Mand", "Kvinde" }));
		GridBagConstraints gbc_sexComboBox = new GridBagConstraints();
		gbc_sexComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_sexComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_sexComboBox.gridx = 1;
		gbc_sexComboBox.gridy = 2;
		add(sexComboBox, gbc_sexComboBox);

		JLabel lblHjemmedbsdato = new JLabel("Hjemmed\u00E5bsdato");
		GridBagConstraints gbc_lblHjemmedbsdato = new GridBagConstraints();
		gbc_lblHjemmedbsdato.insets = new Insets(0, 0, 5, 5);
		gbc_lblHjemmedbsdato.gridx = 0;
		gbc_lblHjemmedbsdato.gridy = 3;
		add(lblHjemmedbsdato, gbc_lblHjemmedbsdato);

		homeBaptismdateChooser = new JDateChooser();
		GridBagConstraints gbc_homeBaptismdateChooser = new GridBagConstraints();
		gbc_homeBaptismdateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_homeBaptismdateChooser.fill = GridBagConstraints.BOTH;
		gbc_homeBaptismdateChooser.gridx = 1;
		gbc_homeBaptismdateChooser.gridy = 3;
		add(homeBaptismdateChooser, gbc_homeBaptismdateChooser);

		JLabel lblDbsdato = new JLabel("D\u00E5bsdato");
		GridBagConstraints gbc_lblDbsdato = new GridBagConstraints();
		gbc_lblDbsdato.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbsdato.gridx = 0;
		gbc_lblDbsdato.gridy = 4;
		add(lblDbsdato, gbc_lblDbsdato);

		baptismdateChooser = new JDateChooser();
		GridBagConstraints gbc_baptismdateChooser = new GridBagConstraints();
		gbc_baptismdateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_baptismdateChooser.fill = GridBagConstraints.BOTH;
		gbc_baptismdateChooser.gridx = 1;
		gbc_baptismdateChooser.gridy = 4;
		add(baptismdateChooser, gbc_baptismdateChooser);

		JLabel lblFadersNavn = new JLabel("Faders navn");
		GridBagConstraints gbc_lblFadersNavn = new GridBagConstraints();
		gbc_lblFadersNavn.anchor = GridBagConstraints.EAST;
		gbc_lblFadersNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersNavn.gridx = 0;
		gbc_lblFadersNavn.gridy = 5;
		add(lblFadersNavn, gbc_lblFadersNavn);

		fatherNametextField = new JTextField();
		GridBagConstraints gbc_fatherNametextField = new GridBagConstraints();
		gbc_fatherNametextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherNametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherNametextField.gridx = 1;
		gbc_fatherNametextField.gridy = 5;
		add(fatherNametextField, gbc_fatherNametextField);
		fatherNametextField.setColumns(10);

		JLabel lblFadersHndtering = new JLabel("Faders h\u00E5ndtering");
		GridBagConstraints gbc_lblFadersHndtering = new GridBagConstraints();
		gbc_lblFadersHndtering.anchor = GridBagConstraints.EAST;
		gbc_lblFadersHndtering.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersHndtering.gridx = 0;
		gbc_lblFadersHndtering.gridy = 6;
		add(lblFadersHndtering, gbc_lblFadersHndtering);

		fatherTradetextField = new JTextField();
		GridBagConstraints gbc_fatherTradetextField = new GridBagConstraints();
		gbc_fatherTradetextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherTradetextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherTradetextField.gridx = 1;
		gbc_fatherTradetextField.gridy = 6;
		add(fatherTradetextField, gbc_fatherTradetextField);
		fatherTradetextField.setColumns(10);

		JLabel lblFadersBopl = new JLabel("Faders bop\u00E6l");
		GridBagConstraints gbc_lblFadersBopl = new GridBagConstraints();
		gbc_lblFadersBopl.anchor = GridBagConstraints.EAST;
		gbc_lblFadersBopl.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersBopl.gridx = 0;
		gbc_lblFadersBopl.gridy = 7;
		add(lblFadersBopl, gbc_lblFadersBopl);

		fatherAddresstextField = new JTextField();
		GridBagConstraints gbc_fatherAddresstextField = new GridBagConstraints();
		gbc_fatherAddresstextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherAddresstextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherAddresstextField.gridx = 1;
		gbc_fatherAddresstextField.gridy = 7;
		add(fatherAddresstextField, gbc_fatherAddresstextField);
		fatherAddresstextField.setColumns(10);

		JLabel lblModersNavn = new JLabel("Moders navn");
		GridBagConstraints gbc_lblModersNavn = new GridBagConstraints();
		gbc_lblModersNavn.anchor = GridBagConstraints.EAST;
		gbc_lblModersNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblModersNavn.gridx = 0;
		gbc_lblModersNavn.gridy = 8;
		add(lblModersNavn, gbc_lblModersNavn);

		motherNametextField = new JTextField();
		GridBagConstraints gbc_motherNametextField = new GridBagConstraints();
		gbc_motherNametextField.insets = new Insets(0, 0, 5, 0);
		gbc_motherNametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_motherNametextField.gridx = 1;
		gbc_motherNametextField.gridy = 8;
		add(motherNametextField, gbc_motherNametextField);
		motherNametextField.setColumns(10);

		JLabel lblModersBopl = new JLabel("Moders bop\u00E6l");
		GridBagConstraints gbc_lblModersBopl = new GridBagConstraints();
		gbc_lblModersBopl.anchor = GridBagConstraints.EAST;
		gbc_lblModersBopl.insets = new Insets(0, 0, 5, 5);
		gbc_lblModersBopl.gridx = 0;
		gbc_lblModersBopl.gridy = 9;
		add(lblModersBopl, gbc_lblModersBopl);

		motherAddresstextField = new JTextField();
		GridBagConstraints gbc_motherAddresstextField = new GridBagConstraints();
		gbc_motherAddresstextField.insets = new Insets(0, 0, 5, 0);
		gbc_motherAddresstextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_motherAddresstextField.gridx = 1;
		gbc_motherAddresstextField.gridy = 9;
		add(motherAddresstextField, gbc_motherAddresstextField);
		motherAddresstextField.setColumns(10);

		JLabel lblFaddere = new JLabel("Faddere");
		GridBagConstraints gbc_lblFaddere = new GridBagConstraints();
		gbc_lblFaddere.insets = new Insets(0, 0, 5, 5);
		gbc_lblFaddere.gridx = 0;
		gbc_lblFaddere.gridy = 10;
		add(lblFaddere, gbc_lblFaddere);

		godParentseditorPane = new JEditorPane();
		GridBagConstraints gbc_godParentseditorPane = new GridBagConstraints();
		gbc_godParentseditorPane.weighty = 5.0;
		gbc_godParentseditorPane.insets = new Insets(0, 0, 5, 0);
		gbc_godParentseditorPane.fill = GridBagConstraints.BOTH;
		gbc_godParentseditorPane.gridx = 1;
		gbc_godParentseditorPane.gridy = 10;
		add(godParentseditorPane, gbc_godParentseditorPane);

		Panel panel = new Panel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 11;
		add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JButton savebutton = new JButton("Gem");
		savebutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveFamily();
			}
		});
		panel.add(savebutton);

		JButton cancelbutton = new JButton("Fortryd");
		cancelbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearScreen();
			}
		});
		panel.add(cancelbutton);

	}

	/**
	 * Birth date changed
	 */
	protected void birthDateChanged() {
		try {
			Date date = birthdateChooser.getDate();
			homeBaptismdateChooser.setDate(date);
			baptismdateChooser.setDate(date);
		} catch (Exception ignoredException) {
		}
	}

	/**
	 * Cancel button was pressed
	 */
	protected void clearScreen() {
		nametextField.setText("");
		fatherNametextField.setText("");
		fatherTradetextField.setText("");
		fatherAddresstextField.setText("");
		motherNametextField.setText("");
		motherAddresstextField.setText("");
		try {
			birthdateChooser.setCalendar(null);
			homeBaptismdateChooser.setCalendar(null);
			baptismdateChooser.setCalendar(null);
		} catch (Exception ignoredException) {
		}
		godParentseditorPane.setText("");

	}

	/**
	 * Save button was pressed
	 */
	protected void saveFamily() {
		int individualId = 1;
		Family family = new Family(1, 1);
		Individual father = new Individual(individualId++);
		Individual mother = new Individual(individualId++);
		Individual child = new Individual(individualId++);

		family.setFather(father);
		family.setMother(mother);
		family.setChild(child);

		GedcomFile gedcomFile = GedcomFile.getInstance();
		gedcomFile.addFamily(family);

		String string = godParentseditorPane.getText();
		String[] gpArray = string.split("\n");

		Individual gp;
		for (String s : gpArray) {
			gp = new Individual(individualId++);

			gp.setName(s);
			String sSex = (String) sexComboBox.getSelectedItem();
			if (sSex.startsWith("M")) {
				gp.setSex(Sex.M);
			} else {
				gp.setSex(Sex.F);
			}

			family = new Family(1, 1);
			// TODO Also add room for address for godparent
			family.setFather(gp);
			gedcomFile.addFamily(family);
		}

		try {
			gedcomFile.save(family);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
