package net.myerichsen.gedcom.db.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.gedcom.db.loaders.CensusDbLoader;
import net.myerichsen.gedcom.db.loaders.DBLoader;
import net.myerichsen.gedcom.db.models.IndividualModel;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * @author Michael Erichsen
 * @version 10. apr. 2023
 *
 */
public class ArchiveSearcher extends Shell {
	// TODO Make database schemas configurable
	// FIXME Find why FLOOR column missing in Polreg

	// TODO Find all relocations to and from an individual

	// FIXME Census table has double Horizonal SCROLLbar

	// TODO Change from Shell to ApplicationWindow @see
	// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesTreeViewer.htm

	// TODO Individual: Add fatherId, motherId and a String[] of children Id's for
	// tree population
	// TODO Populate ancestors tree tab
	// TODO Add descendants tree tab

	// TODO Id search should populate father and mother search fields
	// FIXME Parents search not finished
	// TODO Siblings: Sort by birthdate
	// TODO Siblings Search by ID giving parents should do a parents search for
	// siblings
	// FIXME ID search does not clear siblings table
	// TODO Doubleclick om sibling row inserts id and name in search bar

	/**
	 * Static constants used to initalize properties file
	 */

	private static final String CPHDB_SCHEMA = "CPH";
	private static final String CPHDB_PATH = System.getProperty("user.home") + "/CPHDB";
	private static final String CENSUS_CSV_FILE_DIRECTORY = System.getProperty("user.home")
			+ "/Documents/The Master Genealogist v9/Kilder/DDD";
	private static final String GEDCOM_FILE_PATH = System.getProperty("user.home")
			+ "/Documents/The Master Genealogist v9/Export/Vejby.ged";
	private static final String KIP_TEXT_FILENAME = "kipdata.txt";
	private static final String PROBATE_SOURCE = "Kronborg";
	private static final String PROBATEDB_PATH = "c:/DerbyDB/gedcom";
	private static final String PROBATEDB_SCHEMA = "GEDCOM";
	private static final String PROPERTIES_PATH = System.getProperty("user.home") + "/ArchiveSearcher.properties";
	private static final String VEJBYDB_PATH = System.getProperty("user.home") + "/VEJBYDB";
	private static final String VEJBYDB_SCHEMA = "VEJBY";
	private static final String CPH_CSV_FILE_DIRECTORY = "C:/Users/michael/Downloads/data-20230129T125804Z-001/data";
	private static final String HACK4DK_BURIAL_PERSON_COMPLETE = "hack4dk_burial_person_complete.csv";
	private static final String HACK4DK_POLICE_ADDRESS = "hack4dk_police_address.csv";
	private static final String HACK4DK_POLICE_PERSON = "hack4dk_police_person.csv";
	private static final String HACK4DK_POLICE_POSITION = "hack4dk_police_position.csv";

	private static Display display;

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			display = Display.getDefault();
			final ArchiveSearcher shell = new ArchiveSearcher(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private Properties props;
	private final Text messageField;
	private Text searchId;
	private Text searchName;
	private Text searchBirth;
	private Text searchDeath;
	private final Shell shell;
	private Table siblingsTable;
	private Text searchFather;
	private Text searchMother;
	private final TabFolder tabFolder;
	private IndividualComposite individualComposite;
	private RelocationComposite relocationComposite;
	private CensusComposite censusComposite;
	private BurregComposite burregComposite;
	private PolregComposite polregComposite;
	private ProbateComposite probateComposite;
	private SiblingsComposite siblingsComposite;

	/**
	 * Create the shell.
	 *
	 * @param display
	 */
	public ArchiveSearcher(Display display) {
		super(display, SWT.SHELL_TRIM);
		getProperties();
		setLayout(new GridLayout(1, false));
		shell = display.getActiveShell();
		createMenuBar();

		createSearchBar();

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TabItem tbtmPerson = new TabItem(tabFolder, SWT.NONE);
		tbtmPerson.setText("Person");
		individualComposite = new IndividualComposite(tabFolder, SWT.NONE);
		tbtmPerson.setControl(individualComposite);

		final TabItem tbtmRelocations = new TabItem(tabFolder, SWT.NONE);
		tbtmRelocations.setText("Flytninger");
		relocationComposite = new RelocationComposite(tabFolder, SWT.NONE);
		relocationComposite.setProperties(props);
		tbtmRelocations.setControl(relocationComposite);

		final TabItem tbtmCensus = new TabItem(tabFolder, SWT.NONE);
		tbtmCensus.setText("Folketællinger");
		censusComposite = new CensusComposite(tabFolder, SWT.NONE);
		censusComposite.setProperties(props);
		tbtmCensus.setControl(censusComposite);

		final TabItem tbtmProbate = new TabItem(tabFolder, SWT.NONE);
		tbtmProbate.setText("Skifter");
		probateComposite = new ProbateComposite(tabFolder, SWT.NONE);
		probateComposite.setProperties(props);
		tbtmProbate.setControl(probateComposite);

		final TabItem tbtmPolreg = new TabItem(tabFolder, SWT.NONE);
		tbtmPolreg.setText("Politiets Registerblade");
		polregComposite = new PolregComposite(tabFolder, SWT.NONE);
		polregComposite.setProperties(props);
		tbtmPolreg.setControl(polregComposite);

		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
		tbtmBurreg.setText("Kbhvn. begravelsesregister");
		burregComposite = new BurregComposite(tabFolder, SWT.NONE);
		burregComposite.setProperties(props);
		tbtmBurreg.setControl(burregComposite);

		final TabItem tbtmSiblings = new TabItem(tabFolder, SWT.NONE);
		tbtmSiblings.setText("Søskende");
		siblingsComposite = new SiblingsComposite(tabFolder, SWT.NONE);
		siblingsComposite.setProperties(props);
		tbtmSiblings.setControl(siblingsComposite);

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createContents();
	}

	/**
	 * @param e
	 */
	protected void burregLoader(SelectionEvent e) {
		// TODO LoadBurialPersonComplete derbydatabasepath csvfile
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphDbPath") + " ind i tabellerne i "
					+ props.getProperty("cphPath"));

//			new Thread(() -> {
//				final String[] sa = new String[] { props.getProperty("cphDbPath"), props.getProperty("cphPath") };
//				LoadBurialPersonComplete.main(sa);
//			}).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Arkivsøgning");
		setSize(1112, 625);

	}

	/**
	 *
	 */
	private void createMenuBar() {
		final Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		final MenuItem mntmFiler = new MenuItem(menu, SWT.CASCADE);
		mntmFiler.setText("Filer");
		final Menu menu_1 = new Menu(mntmFiler);
		mntmFiler.setMenu(menu_1);

		MenuItem mntmIndstillinger = new MenuItem(menu_1, SWT.NONE);
		mntmIndstillinger.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SettingsWizard settingsWizard = new SettingsWizard(props);
				WizardDialog wizardDialog = new WizardDialog(shell, settingsWizard);

				wizardDialog.setBlockOnOpen(true);

				int returnCode = wizardDialog.open();

				if (returnCode == Dialog.OK) {
					// TODO Store in properties
					System.out.println("Ok pressed");
				} else
					System.out.println("Cancel pressed");
			}
		});
		mntmIndstillinger.setText("Indstillinger");

		final MenuItem mntmAfslut = new MenuItem(menu_1, SWT.NONE);
		mntmAfslut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.display.dispose();
			}
		});
		mntmAfslut.setText("Afslut");

		MenuItem mntmIndlsning = new MenuItem(menu, SWT.CASCADE);
		mntmIndlsning.setText("Indl\u00E6sning");

		Menu menu_2 = new Menu(mntmIndlsning);
		mntmIndlsning.setMenu(menu_2);

		final MenuItem mntmL = new MenuItem(menu_2, SWT.NONE);
		mntmL.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				gedcomLoader(e);
			}
		});
		mntmL.setText("Indl\u00E6s GEDCOM i databasen");

		final MenuItem mntmIndlsKipFiler = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsKipFiler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				kipFileLoader(e);
			}
		});
		mntmIndlsKipFiler.setText("Indl\u00E6s KIP filer i databasen");

		final MenuItem mntmIndlsPolitietsRegisterblade = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsPolitietsRegisterblade.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				polregLoader(e);
			}
		});
		mntmIndlsPolitietsRegisterblade.setText("Indl\u00E6s Politiets Registerblade");

		final MenuItem mntmIndlsBegravelsregisteret = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsBegravelsregisteret.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				burregLoader(e);
			}
		});
		mntmIndlsBegravelsregisteret.setText("Indl\u00E6s begravelsesregisteret");

		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Hj\u00E6lp");

		Menu menu_3 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_3);

		MenuItem mntmOm = new MenuItem(menu_3, SWT.NONE);
		mntmOm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HelpDialog helpDialog = new HelpDialog(shell);
				helpDialog.open();
			}
		});
		mntmOm.setText("Om...");
	}

	/**
	 * Create the search bar
	 */
	private void createSearchBar() {
		final Composite searchComposite = new Composite(this, SWT.NONE);
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		searchComposite.setLayout(new GridLayout(6, false));
		final Label lblVlgEntenId = new Label(searchComposite, SWT.NONE);
		lblVlgEntenId.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblVlgEntenId.setText(
				"V\u00E6lg enten ID fra stamtr\u00E6et, et navn (evt. med f\u00F8de\u00E5r og d\u00F8ds\u00E5r) eller fader og moder");
		lblVlgEntenId.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 7, 1));

		final Label lblId = new Label(searchComposite, SWT.NONE);
		lblId.setBounds(0, 0, 55, 15);
		lblId.setText("ID");

		searchId = new Text(searchComposite, SWT.BORDER);
		searchId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					searchById(e);
				}
			}
		});
		searchId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		searchId.setBounds(0, 0, 56, 21);

		final Button btnSearchId = new Button(searchComposite, SWT.NONE);
		btnSearchId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchById(e);

			}
		});
		btnSearchId.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnSearchId.setText("S\u00F8g p\u00E5 ID");
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);

		final Button btnRydFelterne = new Button(searchComposite, SWT.NONE);
		btnRydFelterne.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchBirth.setText("");
				searchDeath.setText("");
				searchFather.setText("");
				searchId.setText("");
				searchMother.setText("");
				searchName.setText("");
				searchId.setFocus();
			}
		});
		btnRydFelterne.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnRydFelterne.setText("Ryd felterne");

		final Label lblNewLabel_1 = new Label(searchComposite, SWT.NONE);
		lblNewLabel_1.setText("Navn");

		searchName = new Text(searchComposite, SWT.BORDER);
		searchName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAltFdt = new Label(searchComposite, SWT.NONE);
		lblAltFdt.setText("F\u00F8de\u00E5r (Valgfri)");

		searchBirth = new Text(searchComposite, SWT.BORDER);
		searchBirth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAltDdsr = new Label(searchComposite, SWT.NONE);
		lblAltDdsr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltDdsr.setText("D\u00F8ds\u00E5r (Valgfri)");

		searchDeath = new Text(searchComposite, SWT.BORDER);
		searchDeath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFader = new Label(searchComposite, SWT.NONE);
		lblFader.setText("Fader");

		searchFather = new Text(searchComposite, SWT.BORDER);
		searchFather.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		final Label lblModer = new Label(searchComposite, SWT.NONE);
		lblModer.setText("Moder");

		searchMother = new Text(searchComposite, SWT.BORDER);
		searchMother.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnSearchName = new Button(searchComposite, SWT.NONE);
		btnSearchName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchByName(e);
			}

		});
		btnSearchName.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnSearchName.setText("S\u00F8g p\u00E5 navn");

		final Button btnSgPForldre = new Button(searchComposite, SWT.NONE);
		btnSgPForldre.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchByParents(e);
			}
		});
		btnSgPForldre.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnSgPForldre.setText("S\u00F8g p\u00E5 for\u00E6ldre");

		final Composite composite = new Composite(searchComposite, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 6, 1));
	}

	/**
	 * @param e
	 */
	private void gedcomLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("gedcomFilePath") + " ind i tabellerne i "
					+ props.getProperty("vejbyPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("gedcomFilePath"),
						props.getProperty("vejbyPath") };
				DBLoader.main(sa);
			}).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * Get properties from file or create them
	 */
	private void getProperties() {
		props = new Properties();

		try {
			final InputStream input = new FileInputStream(PROPERTIES_PATH);
			props.load(input);
		} catch (final Exception e) {
			props.setProperty("vejbyPath", VEJBYDB_PATH);
			props.setProperty("probatePath", PROBATEDB_PATH);
			props.setProperty("probateSource", PROBATE_SOURCE);
			props.setProperty("cphDbPath", CPHDB_PATH);
			props.setProperty("gedcomFilePath", GEDCOM_FILE_PATH);
			props.setProperty("kipTextFilename", KIP_TEXT_FILENAME);
			props.setProperty("censusCsvFileDirectory", CENSUS_CSV_FILE_DIRECTORY);
			props.setProperty("vejbySchema", VEJBYDB_SCHEMA);
			props.setProperty("probateSchema", PROBATEDB_SCHEMA);
			props.setProperty("cphSchema", CPHDB_SCHEMA);
			props.setProperty("cphCsvFilePath", CPH_CSV_FILE_DIRECTORY);
			props.setProperty("burialPersonComplete", HACK4DK_BURIAL_PERSON_COMPLETE);
			props.setProperty("policePerson", HACK4DK_POLICE_PERSON);
			props.setProperty("policeAddress", HACK4DK_POLICE_ADDRESS);
			props.setProperty("policePosition", HACK4DK_POLICE_POSITION);
			props.setProperty("relocationSearch", "true");
			props.setProperty("censusSearch", "true");
			props.setProperty("probateSearch", "true");
			props.setProperty("polregSearch", "true");
			props.setProperty("burregSearch", "true");
			props.setProperty("siblingSearch", "true");

			storeProperties();
			System.out.println("Egenskaber gemt i " + PROPERTIES_PATH);
		}
	}

	/**
	 * @param e
	 */
	private void kipFileLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette tager lang tid!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra KIP-filerne " + props.getProperty("censusCsvFileDirectory")
					+ " ind i tabellen i " + props.getProperty("vejbyPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("kipTextFilename"),
						props.getProperty("censusCsvFileDirectory"), props.getProperty("vejbyPath") };
				System.out.println(props.getProperty("kipTextFilename") + ", "
						+ props.getProperty("censusCsvFileDirectory") + ", " + props.getProperty("vejbyPath"));
				CensusDbLoader.main(sa);
			}).start();

			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * @param e
	 */
	protected void polregLoader(SelectionEvent e) {
		// TODO LoadPolicePosition derbydatabasepath csvfile

		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphDbPath") + " ind i tabellerne i "
					+ props.getProperty("cphPath"));

//			new Thread(() -> {
//				final String[] sa = new String[] { props.getProperty("cphDbPath"), props.getProperty("cphPath") };
//				LoadPoliceAddress.main(sa);
//				LoadPolicePosition.main(sa);
//				LoadPolicePerson.main(sa);
//			}).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * Search by ID
	 *
	 * @param e
	 */
	private void searchById(TypedEvent e) {
		searchBirth.setText("");
		searchDeath.setText("");
		searchFather.setText("");
		searchMother.setText("");
		searchName.setText("");
		individualComposite.clear();

		if (searchId.getText().equals("")) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst et tal");
			messageBox.open();
			searchId.setFocus();
			return;
		}

		final String Id = "@I" + searchId.getText() + "@";
		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("vejbyPath"));
			final IndividualModel individual = new IndividualModel(conn, Id);

			if (individual.getName().equals("")) {
				setMessage("ID " + Id + " findes ikke i databasen");
				final Shell[] shells = e.widget.getDisplay().getShells();
				final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
				messageBox.setText("Advarsel");
				messageBox.setMessage("ID " + Id + "findes ikke i databasen");
				messageBox.open();
				searchId.setFocus();
				return;
			}

			searchName.setText(individual.getName());
			final String phonName = individual.getPhonName();
			final String birthDate = individual.getBirthDate().toString();
			searchBirth.setText(birthDate);
			final String deathDate = (individual.getDeathDate() == null ? "9999-12-31"
					: individual.getDeathDate().toString());
			searchDeath.setText(deathDate);

			individualComposite.populate(individual);

			if (props.getProperty("relocationSearch").equals("true")) {
				relocationComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("censusSearch").equals("true")) {
				censusComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("probateSearch").equals("true")) {
				probateComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("polregSearch").equals("true")) {
				polregComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("burregSearch").equals("true")) {
				burregComposite.populate(phonName, birthDate, deathDate);
			}

			if ((individual.getParents() != null) && (individual.getParents().length() > 0)) {
				siblingsComposite.populate(individual.getParents());
			}

		} catch (final SQLException e1) {
			messageField.setText(e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Search by name
	 *
	 * @param e
	 */
	private void searchByName(TypedEvent e) {
		searchId.setText("");
		individualComposite.clear();

		if (searchName.getText().equals("")) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst et navn");
			messageBox.open();
			searchName.setFocus();
			return;
		}

		final Fonkod fk = new Fonkod();
		try {
			final String phonName = fk.generateKey(searchName.getText());

			String birthDate;
			if (searchBirth.getText().equals("")) {
				birthDate = "0001-01-01";
			} else {
				if (searchBirth.getText().length() == 4) {
					birthDate = searchBirth.getText() + "-01-01";
				} else {
					birthDate = searchBirth.getText();
				}
			}

			String deathDate;
			if (searchDeath.getText().equals("")) {
				deathDate = "9999-12-31";
			} else {
				if (searchDeath.getText().length() == 4) {
					deathDate = searchDeath.getText() + "-12-31";
				} else {
					deathDate = searchDeath.getText();
				}
			}

			if (props.getProperty("relocationSearch").equals("true")) {
				relocationComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("censusSearch").equals("true")) {
				censusComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("probateSearch").equals("true")) {
				probateComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("polregSearch").equals("true")) {
				polregComposite.populate(phonName, birthDate, deathDate);
			}

			if (props.getProperty("burregSearch").equals("true")) {
				burregComposite.populate(phonName, birthDate, deathDate);
			}

			if ((searchFather.getText().length() > 0) || (searchMother.getText().length() > 0)) {
				siblingsComposite.populate(searchFather.getText(), searchMother.getText());
			}
		} catch (final Exception e1) {
			setMessage(e1.getMessage());
			e1.printStackTrace();
		}

	}

	/**
	 * Search by parents
	 */
	protected void searchByParents(TypedEvent e) {
		searchBirth.setText("");
		searchDeath.setText("");
		searchName.setText("");
		searchId.setText("");
		individualComposite.clear();

		if ((searchFather.getText().equals("")) && (searchMother.getText().equals(""))) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst fader og/eller moder");
			messageBox.open();
			searchFather.setFocus();
			return;
		}

		try {
			siblingsComposite.populate(searchFather.getText(), searchMother.getText());
			if (siblingsTable != null) {
				siblingsTable.forceFocus();
			}
		} catch (final Exception e2) {
			setMessage(e2.getMessage());
			e2.printStackTrace();
		}

	}

	/**
	 * Set the message in the message field
	 *
	 * @param string
	 *
	 */
	private void setMessage(String string) {
		messageField.setText(string);
		messageField.redraw();
		messageField.update();
	}

	/**
	 * Store properties in file
	 */
	private void storeProperties() {
		try {
			final OutputStream output = new FileOutputStream(PROPERTIES_PATH);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			setMessage("Kan ikke gemme egenskaber i " + PROPERTIES_PATH);
			e2.printStackTrace();
		}
	}
}