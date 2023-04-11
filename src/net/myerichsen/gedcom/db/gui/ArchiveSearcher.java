package net.myerichsen.gedcom.db.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
import net.myerichsen.gedcom.db.loaders.GedcomLoader;
import net.myerichsen.gedcom.db.loaders.LoadBurialPersonComplete;
import net.myerichsen.gedcom.db.loaders.LoadPoliceAddress;
import net.myerichsen.gedcom.db.loaders.LoadPolicePerson;
import net.myerichsen.gedcom.db.loaders.LoadPolicePosition;
import net.myerichsen.gedcom.db.models.IndividualModel;
import net.myerichsen.gedcom.db.tablecreators.CensusTableCreator;
import net.myerichsen.gedcom.db.tablecreators.CphTableCreator;
import net.myerichsen.gedcom.db.tablecreators.GedcomTableCreator;
import net.myerichsen.gedcom.db.tablecreators.ProbateTableCreator;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class ArchiveSearcher extends Shell {
	// FIXME Find why FLOOR column missing in Polreg

	// TODO Find all relocations to and from an individual

	// FIXME Census table has double Horizonal SCROLLbar

	// TODO Change from Shell to ApplicationWindow @see
	// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesTreeViewer.htm

	// TODO Individual: Add fatherId, motherId and a String[] of children Id's for
	// tree population
	// TODO Populate ancestors tree tab
	// TODO Add descendants tree tab

	// FIXME ID search and name search does not clear siblings table
	// TODO Doubleclick om sibling row should insert id and name in search bar

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
	private Text searchFather;
	private Text searchMother;
	private final Shell shell;
	private Table siblingsTable;
	private final TabFolder tabFolder;
	private final IndividualComposite individualComposite;
	private final RelocationComposite relocationComposite;
	private final CensusComposite censusComposite;
	private final BurregComposite burregComposite;
	private final PolregComposite polregComposite;
	private final ProbateComposite probateComposite;
	private final SiblingsComposite siblingsComposite;

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

		final TabItem tbtmPerson = new TabItem(tabFolder, SWT.NONE);
		tbtmPerson.setText("Person");
		individualComposite = new IndividualComposite(tabFolder, SWT.NONE);
		tbtmPerson.setControl(individualComposite);

		final TabItem tbtmRelocations = new TabItem(tabFolder, SWT.NONE);
		tbtmRelocations.setText("Flytninger");
		relocationComposite = new RelocationComposite(tabFolder, SWT.NONE);
		relocationComposite.setProperties(props);
		tbtmRelocations.setControl(relocationComposite);

		final TabItem tbtmCensus = new TabItem(tabFolder, SWT.NONE);
		tbtmCensus.setText("Folket�llinger");
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
		tbtmSiblings.setText("S�skende");
		siblingsComposite = new SiblingsComposite(tabFolder, SWT.NONE);
		siblingsComposite.setProperties(props);
		tbtmSiblings.setControl(siblingsComposite);

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createContents();
	}

	/**
	 * Load burial registry
	 *
	 * @param e
	 */
	protected void burregLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne f�r opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphDbPath") + " ind i tabellerne i "
					+ props.getProperty("cphPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("cphDbPath"), props.getProperty("cphPath"),
						props.getProperty("cphSchema") };
				final String message = LoadBurialPersonComplete.loadCsvFiles(sa);

				messageField.getDisplay().asyncExec(() -> setMessage(message));
			}).start();
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
		setText("Arkivs�gning");
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

		final MenuItem mntmIndstillinger = new MenuItem(menu_1, SWT.NONE);
		mntmIndstillinger.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final SettingsWizard settingsWizard = new SettingsWizard(props);
				final WizardDialog wizardDialog = new WizardDialog(shell, settingsWizard);

				wizardDialog.setBlockOnOpen(true);

//				final int returnCode =
				wizardDialog.open();

//				if (returnCode == Window.OK) {
//					System.out.println("Ok pressed");
//				} else {
//					System.out.println("Cancel pressed");
//				}
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

		final MenuItem mntmIndlsning = new MenuItem(menu, SWT.CASCADE);
		mntmIndlsning.setText("Indl\u00E6sning");

		final Menu menu_2 = new Menu(mntmIndlsning);
		mntmIndlsning.setMenu(menu_2);

		final MenuItem mntmDanGedcomTabeller = new MenuItem(menu_2, SWT.NONE);
		mntmDanGedcomTabeller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMessage(GedcomTableCreator.createTables(props));
			}
		});
		mntmDanGedcomTabeller.setText("Dan GEDCOM tabeller");

		final MenuItem mntmDanFolketllingstabeller = new MenuItem(menu_2, SWT.NONE);
		mntmDanFolketllingstabeller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMessage(CensusTableCreator.createTables(props));

			}
		});
		mntmDanFolketllingstabeller.setText("Dan folket\u00E6llingstabeller");

		final MenuItem mntmDanSkifteprotokoltabeller = new MenuItem(menu_2, SWT.NONE);
		mntmDanSkifteprotokoltabeller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMessage(ProbateTableCreator.createTables(props));
			}
		});
		mntmDanSkifteprotokoltabeller.setText("Dan skifteprotokoltabeller");

		final MenuItem mntmDanTabbellerTil = new MenuItem(menu_2, SWT.NONE);
		mntmDanTabbellerTil.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMessage(CphTableCreator.createTables(props));
			}
		});
		mntmDanTabbellerTil.setText("Dan tabeller til K\u00F8benhavnske registre");

		new MenuItem(menu_2, SWT.SEPARATOR);

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
		mntmIndlsKipFiler.setText("Indl\u00E6s folket\u00E6llinger i databasen");

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
		mntmIndlsBegravelsregisteret.setText("Indl\u00E6s Begravelsesregisteret");

		final MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Hj\u00E6lp");

		final Menu menu_3 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_3);

		final MenuItem mntmOm = new MenuItem(menu_3, SWT.NONE);
		mntmOm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final HelpDialog helpDialog = new HelpDialog(shell);
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
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne f�r opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("gedcomFilePath") + " ind i tabellerne i "
					+ props.getProperty("vejbyPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("gedcomFilePath"), props.getProperty("vejbyPath"),
						props.getProperty("vejbySchema") };
				final String message = GedcomLoader.loadCsvFiles(sa);

				messageField.getDisplay().asyncExec(() -> setMessage(message));

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
			final InputStream input = new FileInputStream(Constants.PROPERTIES_PATH);
			props.load(input);
		} catch (final Exception e) {
			props.setProperty("burialPersonComplete", Constants.HACK4DK_BURIAL_PERSON_COMPLETE);
			props.setProperty("burregSearch", "true");
			props.setProperty("censusCsvFileDirectory", Constants.CENSUS_CSV_FILE_DIRECTORY);
			props.setProperty("censusPath", Constants.CENSUSDB_PATH);
			props.setProperty("censusSchema", Constants.CENSUSDB_SCHEMA);
			props.setProperty("censusSearch", "true");
			props.setProperty("cphCsvFileDirectory", Constants.CPH_CSV_FILE_DIRECTORY);
			props.setProperty("cphDbPath", Constants.CPHDB_PATH);
			props.setProperty("cphSchema", Constants.CPHDB_SCHEMA);
			props.setProperty("gedcomFilePath", Constants.GEDCOM_FILE_PATH);
			props.setProperty("kipTextFilename", Constants.KIP_TEXT_FILENAME);
			props.setProperty("policeAddress", Constants.HACK4DK_POLICE_ADDRESS);
			props.setProperty("policePerson", Constants.HACK4DK_POLICE_PERSON);
			props.setProperty("policePosition", Constants.HACK4DK_POLICE_POSITION);
			props.setProperty("polregSearch", "true");
			props.setProperty("probatePath", Constants.PROBATEDB_PATH);
			props.setProperty("probateSchema", Constants.PROBATEDB_SCHEMA);
			props.setProperty("probateSearch", "true");
			props.setProperty("probateSource", Constants.PROBATE_SOURCE);
			props.setProperty("relocationSearch", "true");
			props.setProperty("siblingSearch", "true");
			props.setProperty("vejbyPath", Constants.VEJBYDB_PATH);
			props.setProperty("vejbySchema", Constants.VEJBYDB_SCHEMA);

			storeProperties();
			System.out.println("Egenskaber gemt i " + Constants.PROPERTIES_PATH);
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
					+ " ind i tabellen i " + props.getProperty("censusPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("kipTextFilename"),
						props.getProperty("censusCsvFileDirectory"), props.getProperty("censusPath"),
						props.getProperty("censusSchema") };
				final String message = CensusDbLoader.loadCsvFiles(sa);

				messageField.getDisplay().asyncExec(() -> setMessage(message));
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
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne f�r opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphDbPath") + " ind i tabellerne i "
					+ props.getProperty("cphPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("cphDbPath"), props.getProperty("cphPath"),
						props.getProperty("cphSchema") };
				final String message1 = LoadPoliceAddress.loadCsvFiles(sa);
				messageField.getDisplay().asyncExec(() -> setMessage(message1));

				final String message2 = LoadPolicePosition.loadCsvFiles(sa);
				messageField.getDisplay().asyncExec(() -> setMessage(message2));
				final String message3 = LoadPolicePerson.loadCsvFiles(sa);
				messageField.getDisplay().asyncExec(() -> setMessage(message3));

			}).start();
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
			final IndividualModel individual = new IndividualModel(conn, Id, props.getProperty("vejbySchema"));

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
			final String deathDate = individual.getDeathDate() == null ? "9999-12-31"
					: individual.getDeathDate().toString();
			searchDeath.setText(deathDate);

			try {
				String[] parentPair = IndividualModel.splitParents(individual.getParents());
				if (parentPair.length > 0) {
					searchFather.setText(parentPair[0]);

				}

				if (parentPair.length > 1) {
					searchMother.setText(parentPair[1]);
				}
			} catch (Exception e1) {
			}

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

			if (individual.getParents() != null && individual.getParents().length() > 0) {
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
			} else if (searchBirth.getText().length() == 4) {
				birthDate = searchBirth.getText() + "-01-01";
			} else {
				birthDate = searchBirth.getText();
			}

			String deathDate;
			if (searchDeath.getText().equals("")) {
				deathDate = "9999-12-31";
			} else if (searchDeath.getText().length() == 4) {
				deathDate = searchDeath.getText() + "-12-31";
			} else {
				deathDate = searchDeath.getText();
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

			if (searchFather.getText().length() > 0 || searchMother.getText().length() > 0) {
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

		if (searchFather.getText().equals("") && searchMother.getText().equals("")) {
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
			final OutputStream output = new FileOutputStream(Constants.PROPERTIES_PATH);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			setMessage("Kan ikke gemme egenskaber i " + Constants.PROPERTIES_PATH);
			e2.printStackTrace();
		}
	}
}