/**
 * 
 */
package net.myerichsen.vejby.churchregistry;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

import net.myerichsen.vejby.gedcom.Family;
import net.myerichsen.vejby.gedcom.GedcomFile;
import net.myerichsen.vejby.gedcom.Individual;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.beans.PropertyChangeEvent;

/**
 * Church registry baptism data entry
 * 
 * Must create a family and populate it with father, mother and child Must
 * create an event for birth, home baptism and baptism as appropriate Must
 * create single individuals for godparents
 * 
 * @author michael
 *
 */

// TODO Mark output file with date and time
public class Baptism {
	private JFrame frmKirkebogDb;
	private JTextField nametextField;
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
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Baptism window = new Baptism();
					window.frmKirkebogDb.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Baptism() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKirkebogDb = new JFrame();
		frmKirkebogDb.setTitle("Kirkebog - D\u00E5b");
		frmKirkebogDb.setBounds(100, 100, 450, 399);
		frmKirkebogDb.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmKirkebogDb.getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("F\u00F8dselsdato");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		frmKirkebogDb.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		birthdateChooser = new JDateChooser();
		birthdateChooser.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				birthDateChanged();
			}
		});
		GridBagConstraints gbc_birthdateChooser = new GridBagConstraints();
		gbc_birthdateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_birthdateChooser.fill = GridBagConstraints.BOTH;
		gbc_birthdateChooser.gridx = 1;
		gbc_birthdateChooser.gridy = 0;
		frmKirkebogDb.getContentPane().add(birthdateChooser, gbc_birthdateChooser);

		JLabel lblNavn = new JLabel("Navn");
		GridBagConstraints gbc_lblNavn = new GridBagConstraints();
		gbc_lblNavn.anchor = GridBagConstraints.EAST;
		gbc_lblNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblNavn.gridx = 0;
		gbc_lblNavn.gridy = 1;
		frmKirkebogDb.getContentPane().add(lblNavn, gbc_lblNavn);

		nametextField = new JTextField();
		GridBagConstraints gbc_nametextField = new GridBagConstraints();
		gbc_nametextField.insets = new Insets(0, 0, 5, 0);
		gbc_nametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nametextField.gridx = 1;
		gbc_nametextField.gridy = 1;
		frmKirkebogDb.getContentPane().add(nametextField, gbc_nametextField);
		nametextField.setColumns(10);

		JLabel lblHjemmedbsdato = new JLabel("Hjemmed\u00E5bsdato");
		GridBagConstraints gbc_lblHjemmedbsdato = new GridBagConstraints();
		gbc_lblHjemmedbsdato.insets = new Insets(0, 0, 5, 5);
		gbc_lblHjemmedbsdato.gridx = 0;
		gbc_lblHjemmedbsdato.gridy = 2;
		frmKirkebogDb.getContentPane().add(lblHjemmedbsdato, gbc_lblHjemmedbsdato);

		homeBaptismdateChooser = new JDateChooser();
		GridBagConstraints gbc_homeBaptismdateChooser = new GridBagConstraints();
		gbc_homeBaptismdateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_homeBaptismdateChooser.fill = GridBagConstraints.BOTH;
		gbc_homeBaptismdateChooser.gridx = 1;
		gbc_homeBaptismdateChooser.gridy = 2;
		frmKirkebogDb.getContentPane().add(homeBaptismdateChooser, gbc_homeBaptismdateChooser);

		JLabel lblDbsdato = new JLabel("D\u00E5bsdato");
		GridBagConstraints gbc_lblDbsdato = new GridBagConstraints();
		gbc_lblDbsdato.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbsdato.gridx = 0;
		gbc_lblDbsdato.gridy = 3;
		frmKirkebogDb.getContentPane().add(lblDbsdato, gbc_lblDbsdato);

		baptismdateChooser = new JDateChooser();
		GridBagConstraints gbc_baptismdateChooser = new GridBagConstraints();
		gbc_baptismdateChooser.insets = new Insets(0, 0, 5, 0);
		gbc_baptismdateChooser.fill = GridBagConstraints.BOTH;
		gbc_baptismdateChooser.gridx = 1;
		gbc_baptismdateChooser.gridy = 3;
		frmKirkebogDb.getContentPane().add(baptismdateChooser, gbc_baptismdateChooser);

		JLabel lblFadersNavn = new JLabel("Faders navn");
		GridBagConstraints gbc_lblFadersNavn = new GridBagConstraints();
		gbc_lblFadersNavn.anchor = GridBagConstraints.EAST;
		gbc_lblFadersNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersNavn.gridx = 0;
		gbc_lblFadersNavn.gridy = 4;
		frmKirkebogDb.getContentPane().add(lblFadersNavn, gbc_lblFadersNavn);

		fatherNametextField = new JTextField();
		GridBagConstraints gbc_fatherNametextField = new GridBagConstraints();
		gbc_fatherNametextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherNametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherNametextField.gridx = 1;
		gbc_fatherNametextField.gridy = 4;
		frmKirkebogDb.getContentPane().add(fatherNametextField, gbc_fatherNametextField);
		fatherNametextField.setColumns(10);

		JLabel lblFadersHndtering = new JLabel("Faders h\u00E5ndtering");
		GridBagConstraints gbc_lblFadersHndtering = new GridBagConstraints();
		gbc_lblFadersHndtering.anchor = GridBagConstraints.EAST;
		gbc_lblFadersHndtering.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersHndtering.gridx = 0;
		gbc_lblFadersHndtering.gridy = 5;
		frmKirkebogDb.getContentPane().add(lblFadersHndtering, gbc_lblFadersHndtering);

		fatherTradetextField = new JTextField();
		GridBagConstraints gbc_fatherTradetextField = new GridBagConstraints();
		gbc_fatherTradetextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherTradetextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherTradetextField.gridx = 1;
		gbc_fatherTradetextField.gridy = 5;
		frmKirkebogDb.getContentPane().add(fatherTradetextField, gbc_fatherTradetextField);
		fatherTradetextField.setColumns(10);

		JLabel lblFadersBopl = new JLabel("Faders bop\u00E6l");
		GridBagConstraints gbc_lblFadersBopl = new GridBagConstraints();
		gbc_lblFadersBopl.anchor = GridBagConstraints.EAST;
		gbc_lblFadersBopl.insets = new Insets(0, 0, 5, 5);
		gbc_lblFadersBopl.gridx = 0;
		gbc_lblFadersBopl.gridy = 6;
		frmKirkebogDb.getContentPane().add(lblFadersBopl, gbc_lblFadersBopl);

		fatherAddresstextField = new JTextField();
		GridBagConstraints gbc_fatherAddresstextField = new GridBagConstraints();
		gbc_fatherAddresstextField.insets = new Insets(0, 0, 5, 0);
		gbc_fatherAddresstextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fatherAddresstextField.gridx = 1;
		gbc_fatherAddresstextField.gridy = 6;
		frmKirkebogDb.getContentPane().add(fatherAddresstextField, gbc_fatherAddresstextField);
		fatherAddresstextField.setColumns(10);

		JLabel lblModersNavn = new JLabel("Moders navn");
		GridBagConstraints gbc_lblModersNavn = new GridBagConstraints();
		gbc_lblModersNavn.anchor = GridBagConstraints.EAST;
		gbc_lblModersNavn.insets = new Insets(0, 0, 5, 5);
		gbc_lblModersNavn.gridx = 0;
		gbc_lblModersNavn.gridy = 7;
		frmKirkebogDb.getContentPane().add(lblModersNavn, gbc_lblModersNavn);

		motherNametextField = new JTextField();
		GridBagConstraints gbc_motherNametextField = new GridBagConstraints();
		gbc_motherNametextField.insets = new Insets(0, 0, 5, 0);
		gbc_motherNametextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_motherNametextField.gridx = 1;
		gbc_motherNametextField.gridy = 7;
		frmKirkebogDb.getContentPane().add(motherNametextField, gbc_motherNametextField);
		motherNametextField.setColumns(10);

		JLabel lblModersBopl = new JLabel("Moders bop\u00E6l");
		GridBagConstraints gbc_lblModersBopl = new GridBagConstraints();
		gbc_lblModersBopl.anchor = GridBagConstraints.EAST;
		gbc_lblModersBopl.insets = new Insets(0, 0, 5, 5);
		gbc_lblModersBopl.gridx = 0;
		gbc_lblModersBopl.gridy = 8;
		frmKirkebogDb.getContentPane().add(lblModersBopl, gbc_lblModersBopl);

		motherAddresstextField = new JTextField();
		GridBagConstraints gbc_motherAddresstextField = new GridBagConstraints();
		gbc_motherAddresstextField.insets = new Insets(0, 0, 5, 0);
		gbc_motherAddresstextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_motherAddresstextField.gridx = 1;
		gbc_motherAddresstextField.gridy = 8;
		frmKirkebogDb.getContentPane().add(motherAddresstextField, gbc_motherAddresstextField);
		motherAddresstextField.setColumns(10);

		JLabel lblFaddere = new JLabel("Faddere");
		GridBagConstraints gbc_lblFaddere = new GridBagConstraints();
		gbc_lblFaddere.insets = new Insets(0, 0, 5, 5);
		gbc_lblFaddere.gridx = 0;
		gbc_lblFaddere.gridy = 9;
		frmKirkebogDb.getContentPane().add(lblFaddere, gbc_lblFaddere);

		godParentseditorPane = new JEditorPane();
		GridBagConstraints gbc_godParentseditorPane = new GridBagConstraints();
		gbc_godParentseditorPane.insets = new Insets(0, 0, 5, 0);
		gbc_godParentseditorPane.fill = GridBagConstraints.BOTH;
		gbc_godParentseditorPane.gridx = 1;
		gbc_godParentseditorPane.gridy = 9;
		frmKirkebogDb.getContentPane().add(godParentseditorPane, gbc_godParentseditorPane);

		Panel panel = new Panel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 10;
		frmKirkebogDb.getContentPane().add(panel, gbc_panel);
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

		JButton dismissButton = new JButton("Luk");
		dismissButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		panel.add(dismissButton);

	}

	/**
	 * Birth date changed
	 */
	protected void birthDateChanged() {
		Date date = birthdateChooser.getDate();
		homeBaptismdateChooser.setDate(date);
		baptismdateChooser.setDate(date);
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
		birthdateChooser.setCalendar(null);
		homeBaptismdateChooser.setCalendar(null);
		baptismdateChooser.setCalendar(null);
		godParentseditorPane.setText("");

	}

	/**
	 * Save button was pressed
	 */
	protected void saveFamily() {
		Family family = new Family();
		Individual father = new Individual();
		Individual mother = new Individual();
		Individual child = new Individual();

		family.setFather(father);
		family.setMother(mother);
		family.setChild(child);

		GedcomFile file = GedcomFile.getInstance();
		file.addFamily(family);

		String string = godParentseditorPane.getText();
		String[] gpArray = string.split("\n");

		Individual gp;
		for (String s : gpArray) {
			gp = new Individual();
			// TODO Set name to s
			family = new Family();
			// TODO Check if male or female
			// TODO Also add room for address for godparent
			family.setFather(gp);
			file.addFamily(family);
		}

		try {
			file.print(new File("c://temp//vejby.txt"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
