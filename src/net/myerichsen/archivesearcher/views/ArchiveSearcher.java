package net.myerichsen.archivesearcher.views;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.myerichsen.archivesearcher.dialogs.HelpDialog;
import net.myerichsen.archivesearcher.dialogs.KeystrokeDialog;
import net.myerichsen.archivesearcher.dialogs.MilRollEntryDialog;
import net.myerichsen.archivesearcher.dialogs.MilRollListDialog;
import net.myerichsen.archivesearcher.dialogs.SettingsWizard;
import net.myerichsen.archivesearcher.loaders.CensusLoader;
import net.myerichsen.archivesearcher.loaders.GedcomLoader;
import net.myerichsen.archivesearcher.loaders.LoadBurialPersonComplete;
import net.myerichsen.archivesearcher.loaders.LoadPoliceAddress;
import net.myerichsen.archivesearcher.loaders.LoadPolicePerson;
import net.myerichsen.archivesearcher.loaders.LoadPolicePosition;
import net.myerichsen.archivesearcher.models.IndividualModel;
import net.myerichsen.archivesearcher.tablecreators.CensusTableCreator;
import net.myerichsen.archivesearcher.tablecreators.CphTableCreator;
import net.myerichsen.archivesearcher.tablecreators.GedcomTableCreator;
import net.myerichsen.archivesearcher.tablecreators.MilRollTableCreator;
import net.myerichsen.archivesearcher.tablecreators.ProbateTableCreator;
import net.myerichsen.archivesearcher.util.Constants;
import net.myerichsen.archivesearcher.util.Fonkod;

/**
 * Main class in archive searcher application. Contains main view and all
 * included views.
 *
 * @author Michael Erichsen
 * @version 14.okt. 2023
 *
 */

public class ArchiveSearcher extends Shell {
	private static Display display;
	private static Properties props;

	/**
	 * Launch the application
	 *
	 * @param args Arguments are not used
	 */
	public static void main(String args[]) {
		try {
			display = Display.getDefault();

			final ArchiveSearcher shell = new ArchiveSearcher(display);

			// Add a listener to save the main window size and location when closing the
			// application
			shell.addListener(SWT.Dispose, event -> {
				final Rectangle bounds = shell.getBounds();
				props.setProperty("x", Integer.toString(bounds.x));
				props.setProperty("y", Integer.toString(bounds.y));
				props.setProperty("width", Integer.toString(bounds.width));
				props.setProperty("height", Integer.toString(bounds.height));
				shell.storeProperties();
			});

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

	private Combo messageCombo;
	private Combo searchIdCombo;
	private Text searchName;
	private Text searchBirth;
	private Text searchDeath;
	private Text searchFather;
	private Text searchMother;
	private final Shell shell;
	private final TabFolder tabFolder;
	private final IndividualView individualView;
	private final RelocationView relocationView;
	private final CensusView censusView;
	private final BurregView burregView;
	private final PolregView polregView;
	private final ProbateView probateView;
	private final SiblingsView siblingsView;
	private final DescendantCounterView descendantCounterView;
	private final HouseholdHeadView householdHeadView;
	private final CensusDupView censusDupView;
	private final MilRollEntryView milRollEntryView;
	private final LastEventView lastEventView;
	private ProgressBar indicator;
	private final PotentialSpouseView potentialSpousesView;

	/**
	 * Create the shell
	 *
	 * @param display The system display
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
		tbtmPerson.setText("&Person");
		individualView = new IndividualView(tabFolder, SWT.NONE);
		tbtmPerson.setControl(individualView);

		final TabItem tbtmRelocations = new TabItem(tabFolder, SWT.NONE);
		tbtmRelocations.setText("F&lytninger");
		relocationView = new RelocationView(tabFolder, SWT.NONE);
		relocationView.setProperties(props);
		tbtmRelocations.setControl(relocationView);

		final TabItem tbtmCensus = new TabItem(tabFolder, SWT.NONE);
		tbtmCensus.setText("F&olket\u00E6llinger");
		censusView = new CensusView(tabFolder, SWT.NONE);
		censusView.setProperties(props);
		tbtmCensus.setControl(censusView);

		final TabItem tbtmProbate = new TabItem(tabFolder, SWT.NONE);
		tbtmProbate.setText("S&kifter");
		probateView = new ProbateView(tabFolder, SWT.NONE);
		probateView.setProperties(props);
		tbtmProbate.setControl(probateView);

		final TabItem tbtmPolreg = new TabItem(tabFolder, SWT.NONE);
		tbtmPolreg.setText("Poli&tiets Registerblade");
		polregView = new PolregView(tabFolder, SWT.NONE);
		polregView.setProperties(props);
		tbtmPolreg.setControl(polregView);

		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
		tbtmBurreg.setText("Kbhvn. &begravelsesregister");
		burregView = new BurregView(tabFolder, SWT.NONE);
		burregView.setProperties(props);
		tbtmBurreg.setControl(burregView);

		final TabItem tbtmSiblings = new TabItem(tabFolder, SWT.NONE);
		tbtmSiblings.setText("&S\u00F8skende");
		siblingsView = new SiblingsView(tabFolder, SWT.NONE);
		siblingsView.setProperties(props);
		tbtmSiblings.setControl(siblingsView);

		final TabItem tbtmPotentialSpouses = new TabItem(tabFolder, SWT.NONE);
		tbtmPotentialSpouses.setText("&Mulige ægtefæller");
		potentialSpousesView = new PotentialSpouseView(tabFolder, SWT.NONE);
		potentialSpousesView.setProperties(props);
		tbtmPotentialSpouses.setControl(potentialSpousesView);

		final TabItem tbtmHusbond = new TabItem(tabFolder, SWT.NONE);
		tbtmHusbond.setText("&Husbond");
		householdHeadView = new HouseholdHeadView(tabFolder, SWT.NONE);
		householdHeadView.setProperties(props);
		tbtmHusbond.setControl(householdHeadView);

		final TabItem tbtmKrydsreferencer = new TabItem(tabFolder, SWT.NONE);
		tbtmKrydsreferencer.setText("Kr&ydsreferencer");

		final TabFolder tabFolderXref = new TabFolder(tabFolder, SWT.NONE);
		tbtmKrydsreferencer.setControl(tabFolderXref);

		final TabItem tbtmEfterkommere = new TabItem(tabFolderXref, SWT.NONE);
		tbtmEfterkommere.setText("&Efterkommere");
		descendantCounterView = new DescendantCounterView(tabFolderXref, SWT.NONE);
		tbtmEfterkommere.setControl(descendantCounterView);
		descendantCounterView.setProperties(props);

		final TabItem tbtmFtDubletter = new TabItem(tabFolderXref, SWT.NONE);
		tbtmFtDubletter.setText("Ft. &dubletter");
		censusDupView = new CensusDupView(tabFolderXref, SWT.NONE);
		tbtmFtDubletter.setControl(censusDupView);
		censusDupView.setProperties(props);

		final TabItem tbtmLgdsruller = new TabItem(tabFolderXref, SWT.NONE);
		tbtmLgdsruller.setText("&L\u00E6gdsruller");
		milRollEntryView = new MilRollEntryView(tabFolderXref, SWT.NONE);
		tbtmLgdsruller.setControl(milRollEntryView);
		milRollEntryView.setProperties(props);

		final TabItem tbtmLastEvent = new TabItem(tabFolderXref, SWT.NONE);
		tbtmLastEvent.setText("Sidste h&ændelse");
		lastEventView = new LastEventView(tabFolderXref, SWT.NONE);
		tbtmLastEvent.setControl(lastEventView);
		lastEventView.setProperties(props);

		createMessageBar();
		createContents();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Clear search fields and stop all searches
	 */
	private void clear() {
		searchBirth.setText("");
		searchDeath.setText("");
		searchFather.setText("");
		searchIdCombo.setText("");
		searchMother.setText("");
		searchName.setText("");
		burregView.clear();
		censusView.clear();
		householdHeadView.clear();
		individualView.clear();
		potentialSpousesView.clear();
		polregView.clear();
		probateView.clear();
		relocationView.clear();
		siblingsView.clear();
		householdHeadView.clear();
		potentialSpousesView.clear();
		searchIdCombo.setFocus();
		setMessage("Felter er ryddet");
	}

	/**
	 * Create contents of the shell
	 */
	protected void createContents() {
		setText("Arkivsøgning");

		final Rectangle screenBounds = getShell().getMonitor().getBounds();
		int x = Integer.parseInt(props.getProperty("x"));
		int y = Integer.parseInt(props.getProperty("y"));
		int width = Integer.parseInt(props.getProperty("width"));
		int height = Integer.parseInt(props.getProperty("height"));

		width = Math.min(screenBounds.width, width);
		height = Math.min(screenBounds.height, height);
		setSize(width, height);

		if (x + width > screenBounds.width) {
			x = screenBounds.width - width;
		}

		if (y + height > screenBounds.height) {
			y = screenBounds.height - height;
		}

		setLocation(x, y);

	}

	/**
	 * Create the menu bar
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
				final MenuItem mi = (MenuItem) e.getSource();
				final Menu m = mi.getParent();
				final ArchiveSearcher as = (ArchiveSearcher) m.getParent();
				final SettingsWizard settingsWizard = new SettingsWizard(props, as);
				final WizardDialog wizardDialog = new WizardDialog(shell, settingsWizard);
				wizardDialog.setBlockOnOpen(true);
				wizardDialog.open();
			}
		});
		mntmIndstillinger.setText("Indstillinger");

		final MenuItem mntmAfslut = new MenuItem(menu_1, SWT.NONE);
		mntmAfslut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clear();
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

		final MenuItem mntmDanTabelTil = new MenuItem(menu_2, SWT.NONE);
		mntmDanTabelTil.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMessage(MilRollTableCreator.createTables(props));
			}
		});
		mntmDanTabelTil.setText("Dan tabel til l\u00E6gdsruller");

		new MenuItem(menu_2, SWT.SEPARATOR);

		final MenuItem mntmL = new MenuItem(menu_2, SWT.NONE);
		mntmL.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runGedcomLoader(e);
			}
		});
		mntmL.setText("Indl\u00E6s GEDCOM i databasen");

		final MenuItem mntmIndlsKipFiler = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsKipFiler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runKipFileLoader(e);
			}
		});
		mntmIndlsKipFiler.setText("Indl\u00E6s folket\u00E6llinger i databasen");

		final MenuItem mntmIndlsPolitietsRegisterblade = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsPolitietsRegisterblade.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runPolregLoader(e);
			}
		});
		mntmIndlsPolitietsRegisterblade.setText("Indl\u00E6s Politiets Registerblade");

		final MenuItem mntmIndlsBegravelsregisteret = new MenuItem(menu_2, SWT.NONE);
		mntmIndlsBegravelsregisteret.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runBurregLoader(e);
			}
		});
		mntmIndlsBegravelsregisteret.setText("Indl\u00E6s Begravelsesregisteret");

		new MenuItem(menu_2, SWT.SEPARATOR);

		final MenuItem mntmLgdsrulleliste = new MenuItem(menu_2, SWT.NONE);
		mntmLgdsrulleliste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				laegdsRulleListe();
			}
		});
		mntmLgdsrulleliste.setText("L\u00E6gdsrulleliste");

		final MenuItem mntmIndtastLgdsruller = new MenuItem(menu_2, SWT.NONE);
		mntmIndtastLgdsruller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MilRollEntryDialog.main(props);
			}
		});
		mntmIndtastLgdsruller.setText("Indtast l\u00E6gdsruller");

		final MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Hj\u00E6lp");

		final Menu menu_3 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_3);

		final MenuItem mntmHjlpetaster = new MenuItem(menu_3, SWT.NONE);
		mntmHjlpetaster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final KeystrokeDialog kd = new KeystrokeDialog(shell);
				kd.open();
			}
		});
		mntmHjlpetaster.setText("Hj\u00E6lpetaster");

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
	 * Create the message bar with progress indicator
	 */
	private void createMessageBar() {
		final Composite messageComposite = new Composite(this, SWT.NONE);
		messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		messageComposite.setLayout(new GridLayout(4, false));

		messageCombo = new Combo(messageComposite, SWT.READ_ONLY);
		messageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		messageCombo.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		indicator = new ProgressBar(messageComposite, SWT.INDETERMINATE);
		indicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		indicator.setBackground(getDisplay().getSystemColor(SWT.COLOR_CYAN));
		indicator.setVisible(false);
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

		searchIdCombo = new Combo(searchComposite, SWT.DROP_DOWN);
		searchIdCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					searchById(e);
				}
			}
		});
		searchIdCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		searchIdCombo.setBounds(0, 0, 56, 21);

		final Button btnSearchId = new Button(searchComposite, SWT.NONE);
		btnSearchId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchById(e);

			}
		});
		btnSearchId.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnSearchId.setText("S\u00F8g p\u00E5 &ID");

		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);

		final Button btnRydFelterne = new Button(searchComposite, SWT.NONE);
		btnRydFelterne.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clear();
			}
		});
		btnRydFelterne.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnRydFelterne.setText("&Ryd felterne og stop s\u00F8gninger");

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
		searchFather.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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
		btnSearchName.setText("S\u00F8g p\u00E5 &navn");

		final Button btnSgPForldre = new Button(searchComposite, SWT.NONE);
		btnSgPForldre.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchByParents(e);
			}
		});
		btnSgPForldre.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnSgPForldre.setText("S\u00F8g p\u00E5 &for\u00E6ldre");
	}

	/**
	 * Get burial registry
	 */
	private void getBurialregistry() {
		final String[] sa = new String[] { props.getProperty("cphCsvFileDirectory"), props.getProperty("cphDbPath"),
				props.getProperty("cphSchema"), props.getProperty("burialPersonComplete") };
		final String message = LoadBurialPersonComplete.main(sa, null);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message));
	}

	/**
	 * Get GEDCOM file
	 */
	private void getGedcom() {
		final String[] sa = new String[] { props.getProperty("gedcomFilePath"), props.getProperty("parishPath"),
				props.getProperty("parishSchema") };
		final String message = GedcomLoader.main(sa, this);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message));
	}

	/**
	 * @return the indicator
	 */
	public ProgressBar getIndicator() {
		return indicator;
	}

	/**
	 * Get KIP files
	 */
	private void getKipFiles() {
		final String[] sa = new String[] { props.getProperty("kipTextFilename"),
				props.getProperty("censusCsvFileDirectory"), props.getProperty("censusPath"),
				props.getProperty("censusSchema") };
		final String message = CensusLoader.main(sa, this);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message));
	}

	/**
	 * Get Police Registry tables
	 */
	private void getPoliceRegistries() {
		String[] sa = new String[] { props.getProperty("cphCsvFileDirectory"), props.getProperty("cphDbPath"),
				props.getProperty("cphSchema"), props.getProperty("policeAddress") };
		final String message1 = LoadPoliceAddress.main(sa, this);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message1));
		messageCombo.getDisplay().asyncExec(() -> indicator.setVisible(true));
		sa = new String[] { props.getProperty("cphCsvFileDirectory"), props.getProperty("cphDbPath"),
				props.getProperty("cphSchema"), props.getProperty("policePosition") };
		final String message2 = LoadPolicePosition.main(sa, this);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message2));
		messageCombo.getDisplay().asyncExec(() -> indicator.setVisible(true));
		sa = new String[] { props.getProperty("cphCsvFileDirectory"), props.getProperty("cphDbPath"),
				props.getProperty("cphSchema"), props.getProperty("policePerson") };
		final String message3 = LoadPolicePerson.main(sa, this);
		messageCombo.getDisplay().asyncExec(() -> setMessage(message3));
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
			props.setProperty("aar", Constants.AAR);
			props.setProperty("amt", Constants.AMT);
			props.setProperty("burialPersonComplete", Constants.HACK4DK_BURIAL_PERSON_COMPLETE);
			props.setProperty("censusCsvFileDirectory", Constants.CENSUS_CSV_FILE_DIRECTORY);
			props.setProperty("censusPath", Constants.CENSUSDB_PATH);
			props.setProperty("censusSchema", Constants.CENSUSDB_SCHEMA);
			props.setProperty("cphCsvFileDirectory", Constants.CPH_CSV_FILE_DIRECTORY);
			props.setProperty("cphDbPath", Constants.CPHDB_PATH);
			props.setProperty("cphSchema", Constants.CPHDB_SCHEMA);
			props.setProperty("gedcomFilePath", Constants.GEDCOM_FILE_PATH);
			props.setProperty("height", Constants.height);
			props.setProperty("kipTextFilename", Constants.KIP_TEXT_FILENAME);
			props.setProperty("laegdid", Constants.LAEGDID);
			props.setProperty("laegdnr", Constants.LAEGDNR);
			props.setProperty("litra", Constants.LITRA);
			props.setProperty("milrollPath", Constants.MILROLLDB_PATH);
			props.setProperty("milrollSchema", Constants.MILROLLDB_SCHEMA);
			props.setProperty("msgLogLen", "20");
			props.setProperty("policeAddress", Constants.HACK4DK_POLICE_ADDRESS);
			props.setProperty("policePerson", Constants.HACK4DK_POLICE_PERSON);
			props.setProperty("policePosition", Constants.HACK4DK_POLICE_POSITION);
			props.setProperty("probatePath", Constants.PROBATEDB_PATH);
			props.setProperty("probateSchema", Constants.PROBATEDB_SCHEMA);
			props.setProperty("probateSource", Constants.PROBATE_SOURCE);
			props.setProperty("relocationSearch", "true");
			props.setProperty("rulletype", Constants.HOVEDRULLE);
			props.setProperty("sogn", Constants.PARISH);
			props.setProperty("uri", Constants.MILROLL_URI);
			props.setProperty("parishPath", Constants.PARISHDB_PATH);
			props.setProperty("parishSchema", Constants.PARISHDB_SCHEMA);
			props.setProperty("width", Constants.width);
			props.setProperty("x", Constants.x);
			props.setProperty("y", Constants.y);

			storeProperties();
			System.out.println("Egenskaber gemt i " + Constants.PROPERTIES_PATH);
		}
	}

	/**
	 * @return the searchIdCombo text
	 */
	public Combo getSearchId() {
		return searchIdCombo;
	}

	/**
	 * @return the searchName
	 */
	public Text getSearchName() {
		return searchName;
	}

	/**
	 * Open the military roll list dialog
	 */
	public void laegdsRulleListe() {
		final MilRollListDialog m = new MilRollListDialog(getShell());
		m.setProperties(props);
		m.open();
	}

	/**
	 * Load burial registry
	 *
	 * @param e
	 */
	protected void runBurregLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphCsvFileDirectory") + "/"
					+ props.getProperty("burialPersonComplete") + " ind i tabellerne i "
					+ props.getProperty("cphDbPath"));

			indicator.setVisible(true);
			new Thread(this::getBurialregistry).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * Load GEDCOM export files
	 *
	 * @param e
	 */
	private void runGedcomLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("gedcomFilePath") + " ind i tabellerne i "
					+ props.getProperty("parishPath"));

			indicator.setVisible(true);
			new Thread(this::getGedcom).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * Load source input project files
	 *
	 * @param e
	 */
	private void runKipFileLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette tager lang tid!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra KIP-filerne " + props.getProperty("censusCsvFileDirectory")
					+ " ind i tabellen i " + props.getProperty("censusPath"));

			indicator.setVisible(true);
			new Thread(this::getKipFiles).start();
			break;
		case SWT.CANCEL:
			break;
		}
	}

	/**
	 * Load Police Registry files
	 *
	 * @param e
	 */
	protected void runPolregLoader(SelectionEvent e) {
		final Shell[] shells = e.widget.getDisplay().getShells();
		final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		messageBox.setText("Advarsel");
		messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
		final int buttonID = messageBox.open();

		switch (buttonID) {
		case SWT.OK:
			setMessage("Data hentes fra " + props.getProperty("cphCsvFileDirectory") + " ind i tabellerne i "
					+ props.getProperty("cphDbPath"));

			indicator.setVisible(true);
			new Thread(this::getPoliceRegistries).start();
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
	protected void searchById(TypedEvent e) {
		searchBirth.setText("");
		searchDeath.setText("");
		searchFather.setText("");
		searchMother.setText("");
		searchName.setText("");
		individualView.clear();
		potentialSpousesView.clear();

		if ("".equals(searchIdCombo.getText())) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst et tal");
			messageBox.open();
			searchIdCombo.setFocus();
			return;
		}

		final String idx = searchIdCombo.getText();

		if (!Arrays.asList(searchIdCombo.getItems()).contains(idx)) {
			searchIdCombo.add(idx, 0);
			searchIdCombo.select(0);

			if (searchIdCombo.getItemCount() > 8) {
				searchIdCombo.remove(8);
			}
		}

		final String id = idx.contains("@") ? idx : "@I" + searchIdCombo.getText().trim() + "@";

		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("parishPath"));
			final IndividualModel individual = new IndividualModel(conn, id, props.getProperty("parishSchema"));

			if ("".equals(individual.getName())) {
				setErrorMessage("ID " + id + " findes ikke i databasen", null);
				searchIdCombo.setFocus();
				return;
			}

			setMessage("Søger efter ID " + idx);
			indicator.setVisible(true);

			searchName.setText(individual.getName());
			final String phonName = individual.getPhonName();
			final String birthDate;
			if (individual.getBirthDate() != null) {
				birthDate = individual.getBirthDate().toString();
			} else {
				birthDate = "0001-01-01";
			}
			searchBirth.setText(birthDate);
			final String deathDate = individual.getDeathDate() == null ? "9999-12-31"
					: individual.getDeathDate().toString();
			searchDeath.setText(deathDate);

			try {
				final String[] parentPair = IndividualModel.splitParents(individual.getParents());
				if (parentPair.length > 0) {
					searchFather.setText(parentPair[0]);

				}

				if (parentPair.length > 1) {
					searchMother.setText(parentPair[1]);
				}
			} catch (final Exception e1) {
			}

			individualView.populate(individual);
			relocationView.populate(phonName, birthDate);
			censusView.populate(individual.getId(), phonName, birthDate, deathDate);
			probateView.populate(phonName, birthDate, deathDate);
			polregView.populate(phonName, birthDate, deathDate);
			burregView.populate(phonName, birthDate, deathDate);
			potentialSpousesView.populate(id);

			if (individual.getParents() != null && individual.getParents().length() > 0) {
				siblingsView.populate(individual.getParents());
			}

			householdHeadView.populate(individual.getId());
		} catch (final SQLException e1) {
			messageCombo.setText(e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Search by name
	 *
	 * @param e
	 */
	protected void searchByName(TypedEvent e) {
		searchIdCombo.setText("");
		individualView.clear();
		siblingsView.clear();
		householdHeadView.clear();
		potentialSpousesView.clear();

		if ("".equals(searchName.getText())) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst et navn");
			messageBox.open();
			searchName.setFocus();
			return;
		}

		setMessage("Søger efter " + searchName.getText());
		indicator.setVisible(true);

		final Fonkod fk = new Fonkod();
		try {
			final String phonName = fk.generateKey(searchName.getText());

			String birthDate;
			if ("".equals(searchBirth.getText())) {
				birthDate = "0001-01-01";
			} else if (searchBirth.getText().length() == 4) {
				birthDate = searchBirth.getText() + "-01-01";
			} else {
				birthDate = searchBirth.getText();
			}

			String deathDate;
			if ("".equals(searchDeath.getText())) {
				deathDate = "9999-12-31";
			} else if (searchDeath.getText().length() == 4) {
				deathDate = searchDeath.getText() + "-12-31";
			} else {
				deathDate = searchDeath.getText();
			}

			relocationView.populate(phonName, birthDate);
			censusView.populate("", phonName, birthDate, deathDate);
			probateView.populate(phonName, birthDate, deathDate);
			polregView.populate(phonName, birthDate, deathDate);
			burregView.populate(phonName, birthDate, deathDate);

			if (searchFather.getText().length() > 0 || searchMother.getText().length() > 0) {
				siblingsView.populate(searchFather.getText(), searchMother.getText());
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
		searchIdCombo.setText("");
		individualView.clear();
		siblingsView.clear();
		householdHeadView.clear();
		potentialSpousesView.clear();

		if ("".equals(searchFather.getText()) && "".equals(searchMother.getText())) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast venligst fader og/eller moder");
			messageBox.open();
			searchFather.setFocus();
			return;
		}

		final StringBuilder sb = new StringBuilder();
		final String father = searchFather.getText();
		final String mother = searchMother.getText();

		if (!father.isBlank()) {
			sb.append(father);
		}

		if (!father.isBlank() && !mother.isBlank()) {
			sb.append(" og ");
		}

		if (!mother.isBlank()) {
			sb.append(mother);
		}

		setMessage("Søger efter " + sb);
		indicator.setVisible(true);

		try {
			siblingsView.populate(father, mother);
			siblingsView.setFocus();
		} catch (final Exception e2) {
			setErrorMessage(e2.getMessage(), e2);
			e2.printStackTrace();
		}

	}

	/**
	 * Set the message in the message combo box and mark as an error message
	 *
	 * @param string the error message to set
	 */
	public void setErrorMessage(String string, Exception e) {
		setMessage(string);
		messageCombo.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		if (e != null) {
			e.printStackTrace();
		}
	}

	/**
	 * @param indicator the indicator to set
	 */
	public void setIndicator(ProgressBar indicator) {
		this.indicator = indicator;
	}

	/**
	 * Set the message in the message combo box
	 *
	 * @param string the message to set
	 *
	 */
	public void setMessage(String string) {

		final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		final LocalTime localTime = LocalTime.now();
		messageCombo.add(dtf.format(localTime) + " " + string, 0);
		messageCombo.select(0);
		messageCombo.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final int lastItem = Integer.parseInt(props.getProperty("msgLogLen"));

		if (messageCombo.getItemCount() > lastItem) {
			messageCombo.remove(lastItem);
		}

		if (Thread.activeCount() <= 6) {
			indicator.setVisible(false);
		}
	}

	/**
	 * Store properties in a file
	 */
	private void storeProperties() {
		try {
			final OutputStream output = new FileOutputStream(Constants.PROPERTIES_PATH);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			if (this.isDisposed()) {
				System.out.println("Kan ikke gemme egenskaber i " + Constants.PROPERTIES_PATH);
			} else {
				setMessage("Kan ikke gemme egenskaber i " + Constants.PROPERTIES_PATH);
			}
			e2.printStackTrace();
		}
	}
}