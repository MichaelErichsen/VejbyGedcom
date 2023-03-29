package net.myerichsen.gedcom.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */
public class ArchiveSearcher extends Shell {

	/**
	 * 
	 */
	private static final String PROPERTIES_PATH = "c:/Users/michael/ArchiveSearcher.properties";

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			final Display display = Display.getDefault();
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

	private Text searchId;
	private Text searchName;
	private Text searchBirth;
	private Text searchDeath;
	private Table relocationTable;
	private Table censusTable;
	private Table probateTable;
	private Table polregTable;
	private Text vejbyPath;
	private Text probatePath;
	private Text cphPath;
	private Text outputDirectory;
	private Logger logger = Logger.getLogger("ArchiveSearcher");
	private Properties props;

	/**
	 * Create the shell.
	 *
	 * @param display
	 */
	public ArchiveSearcher(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(1, false));

		getProperties();

		createMenuBar();

		createSearchComposite();

		final TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		createRelocationTab(tabFolder);

		/**
		 * FTaar Amt Herred Sogn Kildestednavn Husstands_familienr Matr_nr_Adresse
		 * Kildenavn Koen Alder Civilstand Kildeerhverv Stilling_i_husstanden
		 * Kildefoedested Foedt_kildedato Foedeaar Adresse Matrikel Gade_nr
		 * Kildehenvisning Kildekommentar KIPnr Loebenr Fonnavn Kildedetaljer
		 */

		final TabItem tbtmCensus = new TabItem(tabFolder, SWT.NONE);
		tbtmCensus.setText("Folket\u00E6llinger");

		final ScrolledComposite censusScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmCensus.setControl(censusScroller);
		censusScroller.setExpandHorizontal(true);
		censusScroller.setExpandVertical(true);

		censusTable = new Table(censusScroller, SWT.BORDER | SWT.FULL_SELECTION);
		censusTable.setHeaderVisible(true);
		censusTable.setLinesVisible(true);

		final TableColumn tblclmnId = new TableColumn(censusTable, SWT.NONE);
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");
		censusScroller.setContent(censusTable);
		censusScroller.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		/**
		 * GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;
		 * VITALTYPE;COVERED_DATA;SOURCE";
		 */

		final TabItem tbtmProbate = new TabItem(tabFolder, SWT.NONE);
		tbtmProbate.setText("Skifter");

		final ScrolledComposite probateScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmProbate.setControl(probateScroller);
		probateScroller.setExpandHorizontal(true);
		probateScroller.setExpandVertical(true);

		probateTable = new Table(probateScroller, SWT.BORDER | SWT.FULL_SELECTION);
		probateTable.setHeaderVisible(true);
		probateTable.setLinesVisible(true);
		probateScroller.setContent(probateTable);
		probateScroller.setMinSize(probateTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		/**
		 *
		 * NAME;BirthDate;OCCUPATION;STREET;NUMBER;LETTER;
		 * FLOOR;PLACE;HOST;DAY;MONTH;XYEAR;FULL_DATE;FULL_ADDRESS
		 *
		 */

		final TabItem tbtmPolreg = new TabItem(tabFolder, SWT.NONE);
		tbtmPolreg.setText("Politiets Registerblade");

		final ScrolledComposite polregScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmPolreg.setControl(polregScroller);
		polregScroller.setExpandHorizontal(true);
		polregScroller.setExpandVertical(true);

		polregTable = new Table(polregScroller, SWT.BORDER | SWT.FULL_SELECTION);
		polregTable.setHeaderVisible(true);
		polregTable.setLinesVisible(true);
		polregScroller.setContent(polregTable);
		polregScroller.setMinSize(polregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		/**
		 * FIRSTNAMES LASTNAME DATEOFDEATH YEAROFBIRTH DEATHPLACE CIVILSTATUS
		 * ADRESSOUTSIDECPH SEX COMMENT CEMETARY CHAPEL PARISH STREET HOOD STREET_NUMBER
		 * LETTER FLOOR INSTITUTION INSTITUTION_STREET INSTITUTION_HOOD
		 * INSTITUTION_STREET_NUMBER OCCUPTATIONS OCCUPATION_RELATION_TYPES DEATHCAUSES
		 * DEATHCAUSES_DANISH
		 *
		 */

		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
		tbtmBurreg.setText("Begravelser i Kbhvn.");

		final ScrolledComposite burregScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmBurreg.setControl(burregScroller);
		burregScroller.setExpandHorizontal(true);
		burregScroller.setExpandVertical(true);

		createSettingsTab(tabFolder);
		createContents();
	}

	/**
	 * 
	 */
	private void getProperties() {
		props = new Properties();

		try {
			InputStream input = new FileInputStream(PROPERTIES_PATH);
			props.load(input);
		} catch (Exception e) {
			props.setProperty("vejbyPath", "c:/Users/michael/VEJBYDB");
			props.setProperty("probatePath", "c:/DerbyDB/gedcom");
			props.setProperty("cphPath", "c:/Users/michael/CPHDB");
			props.setProperty("outputDirectory", "c:/Users/michael/Documents/Vejby/VejbyGedcom");

			storeProperties();
		}
	}

	/**
	 * 
	 */
	private void storeProperties() {
		try {
			OutputStream output = new FileOutputStream(PROPERTIES_PATH);
			props.store(output, "Archive searcher properties");
		} catch (Exception e2) {
			logger.severe("Could not store properties in " + PROPERTIES_PATH);
			e2.printStackTrace();
		}
	}

	/**
	 * @param tabFolder
	 */
	private void createSettingsTab(final TabFolder tabFolder) {
		final TabItem tbtmOpstning = new TabItem(tabFolder, SWT.NONE);
		tbtmOpstning.setText("Ops\u00E6tning");

		final Composite compositeOpstning = new Composite(tabFolder, SWT.NONE);
		tbtmOpstning.setControl(compositeOpstning);
		compositeOpstning.setLayout(new GridLayout(2, false));

		final Label lblVejbyDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		vejbyPath = new Text(compositeOpstning, SWT.BORDER);
		vejbyPath.setText(props.getProperty("vejbyPath"));
		vejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblSkifteDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		probatePath = new Text(compositeOpstning, SWT.BORDER);
		probatePath.setText(props.getProperty("probatePath"));
		probatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblNewLabel = new Label(compositeOpstning, SWT.NONE);
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		cphPath = new Text(compositeOpstning, SWT.BORDER);
		cphPath.setText(props.getProperty("cphPath"));
		cphPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblUddatasti = new Label(compositeOpstning, SWT.NONE);
		lblUddatasti.setText("Uddatasti");

		outputDirectory = new Text(compositeOpstning, SWT.BORDER);
		outputDirectory.setText(props.getProperty("outputDirectory"));
		outputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Composite settingsButtonComposite = new Composite(compositeOpstning, SWT.NONE);
		settingsButtonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		settingsButtonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));

		final Button settingsUpdateButton = new Button(settingsButtonComposite, SWT.NONE);
		settingsUpdateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				props.setProperty("vejbyPath", vejbyPath.getText());
				props.setProperty("probatePath", probatePath.getText());
				props.setProperty("cphPath", cphPath.getText());
				props.setProperty("outputDirectory", outputDirectory.getText());
				storeProperties();
			}
		});
		settingsUpdateButton.setText("Opdater");

		final Button settingsCancelButton = new Button(settingsButtonComposite, SWT.NONE);
		settingsCancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				vejbyPath.setText(props.getProperty("vejbyPath"));
				probatePath.setText(props.getProperty("probatePath"));
				cphPath.setText(props.getProperty("cphPath"));
				outputDirectory.setText(props.getProperty("outputDirectory"));
			}
		});
		settingsCancelButton.setText("Fortryd");
	}

	/**
	 * @param tabFolder
	 */
	private void createRelocationTab(final TabFolder tabFolder) {
		final TabItem tbtmRelocation = new TabItem(tabFolder, SWT.NONE);
		tbtmRelocation.setText("Flytninger");

		final ScrolledComposite relocationScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmRelocation.setControl(relocationScroller);
		relocationScroller.setExpandHorizontal(true);
		relocationScroller.setExpandVertical(true);

		relocationTable = new Table(relocationScroller, SWT.BORDER | SWT.FULL_SELECTION);
		relocationTable.setHeaderVisible(true);
		relocationTable.setLinesVisible(true);

		final TableColumn relocationIdColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationIdColumn.setWidth(100);
		relocationIdColumn.setText("ID");

		final TableColumn relocationGivenColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationGivenColumn.setWidth(100);
		relocationGivenColumn.setText("Fornavn");

		final TableColumn relocationSurnameColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationSurnameColumn.setWidth(100);
		relocationSurnameColumn.setText("Efternavn");

		final TableColumn relocationDateColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationDateColumn.setWidth(100);
		relocationDateColumn.setText("Flyttedato");

		final TableColumn relocationToColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationToColumn.setWidth(183);
		relocationToColumn.setText("Til");

		final TableColumn relocationFromColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationFromColumn.setWidth(100);
		relocationFromColumn.setText("Fra");

		final TableColumn relocationDetailsColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationDetailsColumn.setWidth(100);
		relocationDetailsColumn.setText("Detaljer");

		final TableColumn relocationBirthDateColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationBirthDateColumn.setWidth(100);
		relocationBirthDateColumn.setText("F\u00F8dselsdato");

		final TableColumn relocationParentsColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationParentsColumn.setWidth(100);
		relocationParentsColumn.setText("For\u00E6ldre");

		relocationScroller.setContent(relocationTable);
		relocationScroller.setMinSize(relocationTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * 
	 */
	private void createSearchComposite() {
		final Composite searchComposite = new Composite(this, SWT.NONE);
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		searchComposite.setLayout(new GridLayout(6, false));

		final Label lblVlgEntenId = new Label(searchComposite, SWT.NONE);
		lblVlgEntenId.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		lblVlgEntenId.setText(
				"V\u00E6lg enten ID fra stamtr\u00E6et eller et navn, evt. med f\u00F8de\u00E5r og evt. d\u00F8ds\u00E5r");
		lblVlgEntenId.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 7, 1));

		final Label lblId = new Label(searchComposite, SWT.NONE);
		lblId.setBounds(0, 0, 55, 15);
		lblId.setText("ID");

		searchId = new Text(searchComposite, SWT.BORDER);
		searchId.setBounds(0, 0, 56, 21);
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);

		final Label lblNewLabel_1 = new Label(searchComposite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Navn");

		searchName = new Text(searchComposite, SWT.BORDER);
		searchName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAltFdt = new Label(searchComposite, SWT.NONE);
		lblAltFdt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltFdt.setText("F\u00F8de\u00E5r (Valgfri)");

		searchBirth = new Text(searchComposite, SWT.BORDER);
		searchBirth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblAltDdsr = new Label(searchComposite, SWT.NONE);
		lblAltDdsr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltDdsr.setText("D\u00F8ds\u00E5r (Valgfri)");

		searchDeath = new Text(searchComposite, SWT.BORDER);
		searchDeath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);

		final Button btnSg = new Button(searchComposite, SWT.NONE);
		btnSg.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSg.setText("S\u00F8g ID");

		final Button btnSg_1 = new Button(searchComposite, SWT.NONE);
		btnSg_1.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSg_1.setText("S\u00F8g navn");
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);
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

		final MenuItem mntmAfslut = new MenuItem(menu_1, SWT.NONE);
		mntmAfslut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.display.dispose();
			}
		});
		mntmAfslut.setText("Afslut");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("ArchiveSearcher");
		setSize(1116, 580);

		TableItem tableItem = new TableItem(relocationTable, SWT.NONE);

		final String[] sa = new String[] { "1383", "Anders", "Pedersen", "1841-04-13", "Mårum, Holbo, Frederiksborg",
				"fra Vejby", "", "1808-01-01", "" };

		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);
		tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText(sa);

	}
}
