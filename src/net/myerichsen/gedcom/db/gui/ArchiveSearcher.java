package net.myerichsen.gedcom.db.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.gedcom.db.loaders.CensusDbLoader;
import net.myerichsen.gedcom.db.models.CensusIndividual;
import net.myerichsen.gedcom.db.models.DBIndividual;
import net.myerichsen.gedcom.db.models.Probate;
import net.myerichsen.gedcom.db.models.Relocation;
import net.myerichsen.gedcom.db.util.CensusIndividualComparator;
import net.myerichsen.gedcom.db.util.RelocationComparator;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * GUI program to search archive copies in Derby databases
 *
 * @author Michael Erichsen
 * @version 30. mar. 2023
 *
 */
// TODO Make database schemas configurable
// TODO Add parents tab
public class ArchiveSearcher extends Shell {

	/**
	 * Static constants used to initalize properties file
	 */
	private static final String CPHDB_SCHEMA = "CPH";
	private static final String CPHDB_PATH = "c:/Users/michael/CPHDB";
	private static final String CSV_FILE_DIRECTORY = "c:/Users/michael/Documents/The Master Genealogist v9/Kilder/DDD";
	private static final String GEDCOM_FILE_PATH = "c:/Users/michael/Documents/The Master Genealogist v9/Export/Vejby.ged";
	private static final String KIP_TEXT_FILENAME = "kipdata.txt";
	private static final String OUTPUT_PATH = "c:/Users/michael/Documents/Vejby/VejbyGedcom";
	private static final String PROBATE_SOURCE = "Kronborg";
	private static final String PROBATEDB_PATH = "c:/DerbyDB/gedcom";
	private static final String PROBATEDB_SCHEMA = "GEDCOM";
	private static final String PROPERTIES_PATH = "c:/Users/michael/ArchiveSearcher.properties";
	private static final String VEJBYDB_PATH = "c:/Users/michael/VEJBYDB";
	private static final String VEJBYDB_SCHEMA = "VEJBY";

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
	private final Table polregTable;
	private Text vejbyPath;
	private Text probatePath;
	private Text cphPath;
	private Text outputDirectory;
	private Properties props;
	private Text probateSource;
	private final Text messageField;
	private TabItem tbtmCensus;
	private TabItem tbtmProbate;
	private TabItem tbtmRelocation;
	private Text gedcomFilePath;
	private Text kipTextFilename;
	private Text csvFileDirectory;
	private Text vejbyDbSchema;
	private Text probateDbSchema;
	private Text cphDbSchema;

	// TODO Make tabs visible/invisible. To hide the tab item you call
	// TabItem.dispose(). To show it again, create a
//	new TabItem at the appropriate index.
//
//	Note: When you dispose a TabItem, this does not dispose the control that was
//	the content of the TabItem. The content control just becomes invisible.
//	Therefore, when you create the new TabItem, you can set the same content
//	control into it.

	// TODO Polreg tab
	// TODO Burreg tab
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
		createSearchBar();

		final TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createRelocationTab(tabFolder);
		createCensusTab(tabFolder);
		createProbateTab(tabFolder);

		/**
		 * GEDCOM NAME;ID;FROMDATE;TODATE;PLACE;EVENTTYPE;
		 * VITALTYPE;COVERED_DATA;SOURCE";
		 */

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

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createContents();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param tabFolder
	 */
	private void createCensusTab(TabFolder tabFolder) {
		tbtmCensus = new TabItem(tabFolder, SWT.NONE);
		tbtmCensus.setText("Folket\u00E6llinger");

		final ScrolledComposite censusScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmCensus.setControl(censusScroller);
		censusScroller.setExpandHorizontal(true);
		censusScroller.setExpandVertical(true);

		censusTable = new Table(censusScroller, SWT.BORDER | SWT.FULL_SELECTION);
		censusTable.setHeaderVisible(true);
		censusTable.setLinesVisible(true);

		final TableColumn tblclmnAar = new TableColumn(censusTable, SWT.NONE);
		tblclmnAar.setWidth(50);
		tblclmnAar.setText("År");

		final TableColumn tblclmnAmt = new TableColumn(censusTable, SWT.NONE);
		tblclmnAmt.setWidth(75);
		tblclmnAmt.setText("Amt");

		final TableColumn tblclmnHerred = new TableColumn(censusTable, SWT.NONE);
		tblclmnHerred.setWidth(75);
		tblclmnHerred.setText("Herred");

		final TableColumn tblclmnSogn = new TableColumn(censusTable, SWT.NONE);
		tblclmnSogn.setWidth(75);
		tblclmnSogn.setText("Sogn");

		final TableColumn tblclmnKildestednavn = new TableColumn(censusTable, SWT.NONE);
		tblclmnKildestednavn.setWidth(100);
		tblclmnKildestednavn.setText("Kildestednavn");

		final TableColumn tblclmnNr = new TableColumn(censusTable, SWT.NONE);
		tblclmnNr.setWidth(50);
		tblclmnNr.setText("Nr.");

		final TableColumn tblclmnMatrAdr = new TableColumn(censusTable, SWT.NONE);
		tblclmnMatrAdr.setWidth(100);
		tblclmnMatrAdr.setText("Matrikel/adresse");

		final TableColumn tblclmnNavn = new TableColumn(censusTable, SWT.NONE);
		tblclmnNavn.setWidth(100);
		tblclmnNavn.setText("Navn");

		final TableColumn tblclmnKn = new TableColumn(censusTable, SWT.NONE);
		tblclmnKn.setWidth(35);
		tblclmnKn.setText("K\u00F8n");

		final TableColumn tblclmnAlder = new TableColumn(censusTable, SWT.NONE);
		tblclmnAlder.setWidth(35);
		tblclmnAlder.setText("Alder");

		final TableColumn tblclmnCivilstand = new TableColumn(censusTable, SWT.NONE);
		tblclmnCivilstand.setWidth(100);
		tblclmnCivilstand.setText("Civilstand");

		final TableColumn tblclmnErhverv = new TableColumn(censusTable, SWT.NONE);
		tblclmnErhverv.setWidth(100);
		tblclmnErhverv.setText("Erhverv");

		final TableColumn tblclmnStilling = new TableColumn(censusTable, SWT.NONE);
		tblclmnStilling.setWidth(100);
		tblclmnStilling.setText("Stilling i husstanden");

		final TableColumn tblclmnFoedested = new TableColumn(censusTable, SWT.NONE);
		tblclmnFoedested.setWidth(100);
		tblclmnFoedested.setText("Fødested");

		final TableColumn tblclmnFoededato = new TableColumn(censusTable, SWT.NONE);
		tblclmnFoededato.setWidth(100);
		tblclmnFoededato.setText("Fødedato");

		final TableColumn tblclmnFoedeaar = new TableColumn(censusTable, SWT.NONE);
		tblclmnFoedeaar.setWidth(50);
		tblclmnFoedeaar.setText("Fødeår");

		final TableColumn tblclmnAdresse = new TableColumn(censusTable, SWT.NONE);
		tblclmnAdresse.setWidth(100);
		tblclmnAdresse.setText("Adresse");

		final TableColumn tblclmnMatrikel = new TableColumn(censusTable, SWT.NONE);
		tblclmnMatrikel.setWidth(100);
		tblclmnMatrikel.setText("Matrikel");

		final TableColumn tblclmnGadenr = new TableColumn(censusTable, SWT.NONE);
		tblclmnGadenr.setWidth(100);
		tblclmnGadenr.setText("Gade nr.");

		final TableColumn tblclmnHenvisning = new TableColumn(censusTable, SWT.NONE);
		tblclmnHenvisning.setWidth(100);
		tblclmnHenvisning.setText("Henvisning");

		final TableColumn tblclmnKommentar = new TableColumn(censusTable, SWT.NONE);
		tblclmnKommentar.setWidth(100);
		tblclmnKommentar.setText("Kommentar");

		final TableColumn tblclmnKipNr = new TableColumn(censusTable, SWT.NONE);
		tblclmnKipNr.setWidth(50);
		tblclmnKipNr.setText("KIP nr.");

		final TableColumn tblclmnLoebenr = new TableColumn(censusTable, SWT.NONE);
		tblclmnLoebenr.setWidth(50);
		tblclmnLoebenr.setText("Løbenr.");

		final TableColumn tblclmnDetaljer = new TableColumn(censusTable, SWT.NONE);
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Detaljer");

		censusScroller.setContent(censusTable);
		censusScroller.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create contents of the shell
	 */
	protected void createContents() {
		setText("ArchiveSearcher");
		setSize(1116, 580);

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

		MenuItem mntmLoadGedcom = new MenuItem(menu_1, SWT.NONE);
		mntmLoadGedcom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				messageBox.setText("Advarsel");
				messageBox.setMessage("Dette valg sletter indholdet i tabellerne før opdatering!");
				int buttonID = messageBox.open();
				switch (buttonID) {
				case SWT.OK:
					setMessage("Data hentes fra " + props.getProperty("gedcomFilePath") + " ind i tabellerne i "
							+ props.getProperty("vejbyPath"));
					String[] sa = new String[] { props.getProperty("gedcomFilePath"), props.getProperty("vejbyPath") };
					System.out.println(props.getProperty("gedcomFilePath") + "; " + props.getProperty("vejbyPath"));
					// DBLoader.main(sa);
					break;
				case SWT.CANCEL:
					break;
				}
			}
		});
		mntmLoadGedcom.setText("Indl\u00E6s GEDCOM i databasen");

		MenuItem mntmLoadKip = new MenuItem(menu_1, SWT.NONE);
		mntmLoadKip.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				messageBox.setText("Advarsel");
				messageBox.setMessage("Dette tager lang tid!");
				int buttonID = messageBox.open();
				switch (buttonID) {
				case SWT.OK:
					setMessage("Data hentes fra KIP-filerne " + props.getProperty("csvFileDirectory")
							+ " ind i tabellen i " + props.getProperty("vejbyPath"));

					String[] sa = new String[] { props.getProperty("kipTextFilename"),
							props.getProperty("csvFileDirectory"), props.getProperty("vejbyPath") };
					System.out.println(props.getProperty("kipTextFilename") + ", "
							+ props.getProperty("csvFileDirectory") + ", " + props.getProperty("vejbyPath"));
					CensusDbLoader.main(sa);

					break;
				case SWT.CANCEL:
					break;
				}
			}
		});
		mntmLoadKip.setText("Indl\u00E6s KIP filer i databasen");

		final MenuItem mntmAfslut = new MenuItem(menu_1, SWT.NONE);
		mntmAfslut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.display.dispose();
			}
		});
		mntmAfslut.setText("Afslut");
	}

	/**
	 * Create census tab
	 *
	 * @param tabFolder
	 */
	private void createProbateTab(TabFolder tabFolder) {
		tbtmProbate = new TabItem(tabFolder, SWT.NONE);
		tbtmProbate.setText("Skifter");

		final ScrolledComposite probateScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmProbate.setControl(probateScroller);
		probateScroller.setExpandHorizontal(true);
		probateScroller.setExpandVertical(true);

		probateTable = new Table(probateScroller, SWT.BORDER | SWT.FULL_SELECTION);
		probateTable.setHeaderVisible(true);
		probateTable.setLinesVisible(true);

		final TableColumn tblclmnName = new TableColumn(probateTable, SWT.NONE);
		tblclmnName.setWidth(200);
		tblclmnName.setText("Navn");

		final TableColumn tblclmnFra = new TableColumn(probateTable, SWT.NONE);
		tblclmnFra.setWidth(100);
		tblclmnFra.setText("Fra");

		final TableColumn tblclmnTil = new TableColumn(probateTable, SWT.NONE);
		tblclmnTil.setWidth(100);
		tblclmnTil.setText("Til");

		final TableColumn tblclmnSted = new TableColumn(probateTable, SWT.NONE);
		tblclmnSted.setWidth(100);
		tblclmnSted.setText("Sted");

		final TableColumn tblclmnData = new TableColumn(probateTable, SWT.NONE);
		tblclmnData.setWidth(300);
		tblclmnData.setText("Data");

		final TableColumn tblclmnKilde = new TableColumn(probateTable, SWT.NONE);
		tblclmnKilde.setWidth(300);
		tblclmnKilde.setText("Kilde");

		probateScroller.setContent(probateTable);
		probateScroller.setMinSize(probateTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create the relocation tab
	 *
	 * @param tabFolder
	 */
	private void createRelocationTab(final TabFolder tabFolder) {
		tbtmRelocation = new TabItem(tabFolder, SWT.NONE);
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
		relocationIdColumn.setWidth(50);
		relocationIdColumn.setText("ID");

		final TableColumn relocationGivenColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationGivenColumn.setWidth(100);
		relocationGivenColumn.setText("Fornavn");

		final TableColumn relocationSurnameColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationSurnameColumn.setWidth(100);
		relocationSurnameColumn.setText("Efternavn");

		final TableColumn relocationDateColumn = new TableColumn(relocationTable, SWT.NONE);
		relocationDateColumn.setWidth(80);
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
		relocationParentsColumn.setWidth(200);
		relocationParentsColumn.setText("For\u00E6ldre");

		relocationScroller.setContent(relocationTable);
		relocationScroller.setMinSize(relocationTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create the search composite
	 */
	private void createSearchBar() {
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

		final Button btnSearchId = new Button(searchComposite, SWT.NONE);
		btnSearchId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchById(e);

			}
		});
		btnSearchId.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSearchId.setText("S\u00F8g ID");

		final Button btnSearchName = new Button(searchComposite, SWT.NONE);
		btnSearchName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchByName(e);
			}

		});
		btnSearchName.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSearchName.setText("S\u00F8g navn");
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);
	}

	/**
	 * Create the settings tab
	 *
	 * @param tabFolder
	 */
	private void createSettingsTab(final TabFolder tabFolder) {
		final TabItem tbtmOpstning = new TabItem(tabFolder, SWT.NONE);
		tbtmOpstning.setText("Ops\u00E6tning");

		final Composite compositeOpstning = new Composite(tabFolder, SWT.NONE);
		tbtmOpstning.setControl(compositeOpstning);
		compositeOpstning.setLayout(new GridLayout(4, false));

		final Label lblVejbyDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		vejbyPath = new Text(compositeOpstning, SWT.BORDER);
		vejbyPath.setText(props.getProperty("vejbyPath"));
		vejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblVejbyDatabaseSchema = new Label(compositeOpstning, SWT.NONE);
		lblVejbyDatabaseSchema.setText("Vejby database schema");

		vejbyDbSchema = new Text(compositeOpstning, SWT.BORDER);
		vejbyDbSchema.setEnabled(false);
		vejbyDbSchema.setEditable(false);
		vejbyDbSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		vejbyDbSchema.setText(props.getProperty("vejbyDbSchema"));

		final Label lblSkifteDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		probatePath = new Text(compositeOpstning, SWT.BORDER);
		probatePath.setText(props.getProperty("probatePath"));
		probatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblSkiftekilde = new Label(compositeOpstning, SWT.NONE);
		lblSkiftekilde.setText("Skiftekilde");

		probateSource = new Text(compositeOpstning, SWT.BORDER);
		probateSource.setText(props.getProperty("probateSource"));
		probateSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblProbateDatabaseSchema = new Label(compositeOpstning, SWT.NONE);
		lblProbateDatabaseSchema.setText("Skifte database schema");

		probateDbSchema = new Text(compositeOpstning, SWT.BORDER);
		probateDbSchema.setEnabled(false);
		probateDbSchema.setEditable(false);
		probateDbSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		probateDbSchema.setText(props.getProperty("probateDbSchema"));

		final Label lblNewLabel = new Label(compositeOpstning, SWT.NONE);
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		cphPath = new Text(compositeOpstning, SWT.BORDER);
		cphPath.setText(props.getProperty("cphPath"));
		cphPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblCphDatabaseSchema = new Label(compositeOpstning, SWT.NONE);
		lblCphDatabaseSchema.setText("K\u00F8benhavnsdatabase database schema");

		cphDbSchema = new Text(compositeOpstning, SWT.BORDER);
		cphDbSchema.setEnabled(false);
		cphDbSchema.setEditable(false);
		cphDbSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cphDbSchema.setText(props.getProperty("cphDbSchema"));

		final Label lblUddatasti = new Label(compositeOpstning, SWT.NONE);
		lblUddatasti.setText("Uddatasti");

		outputDirectory = new Text(compositeOpstning, SWT.BORDER);
		outputDirectory.setText(props.getProperty("outputDirectory"));
		outputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblGedcomFilSti = new Label(compositeOpstning, SWT.NONE);
		lblGedcomFilSti.setText("GEDCOM fil sti");

		gedcomFilePath = new Text(compositeOpstning, SWT.BORDER);
		gedcomFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		gedcomFilePath.setText(props.getProperty("gedcomFilePath"));

		Label lblKipTextFilnavn = new Label(compositeOpstning, SWT.NONE);
		lblKipTextFilnavn.setText("KIP text filnavn uden sti");

		kipTextFilename = new Text(compositeOpstning, SWT.BORDER);
		kipTextFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		kipTextFilename.setText(props.getProperty("kipTextFilename"));

		Label lblKipCsvFil = new Label(compositeOpstning, SWT.NONE);
		lblKipCsvFil.setText("KIP csv fil sti");

		csvFileDirectory = new Text(compositeOpstning, SWT.BORDER);
		csvFileDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		csvFileDirectory.setText(props.getProperty("csvFileDirectory"));

		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);

		Composite composite = new Composite(compositeOpstning, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_composite.widthHint = 1071;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(7, false));

		Label lblAktiveSgninger = new Label(composite, SWT.NONE);
		lblAktiveSgninger.setText("Aktive s\u00F8gninger:");

		Button btnRelocationTab = new Button(composite, SWT.CHECK);
		btnRelocationTab.setSelection(Boolean.parseBoolean(props.getProperty("relocationSearch")));
		btnRelocationTab.setText("Flytninger");

		Button btnCensusTab = new Button(composite, SWT.CHECK);
		btnCensusTab.setSelection(Boolean.parseBoolean(props.getProperty("censusSearch")));
		btnCensusTab.setText("Folket\u00E6llinger");

		Button btnProbateTab = new Button(composite, SWT.CHECK);
		btnProbateTab.setSelection(Boolean.parseBoolean(props.getProperty("probateSearch")));
		btnProbateTab.setText("Skifter");

		Button btnPolitietsRegisterblade = new Button(composite, SWT.CHECK);
		btnPolitietsRegisterblade.setSelection(Boolean.parseBoolean(props.getProperty("polregSearch")));
		btnPolitietsRegisterblade.setText("Politiets registerblade");

		Button btnBegravelsesregistret = new Button(composite, SWT.CHECK);
		btnBegravelsesregistret.setSelection(Boolean.parseBoolean(props.getProperty("burregSearch")));
		btnBegravelsesregistret.setText("Begravelsesregistret");

		Button btnForldre = new Button(composite, SWT.CHECK);
		btnForldre.setEnabled(Boolean.parseBoolean(props.getProperty("parentSearch")));
		btnForldre.setText("For\u00E6ldre");

		final Composite settingsButtonComposite = new Composite(compositeOpstning, SWT.NONE);
		settingsButtonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		settingsButtonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1));

		final Button settingsUpdateButton = new Button(settingsButtonComposite, SWT.NONE);
		settingsUpdateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				props.setProperty("vejbyPath", vejbyPath.getText());
				props.setProperty("probatePath", probatePath.getText());
				props.setProperty("probateSource", probateSource.getText());
				props.setProperty("cphPath", cphPath.getText());
				props.setProperty("outputDirectory", outputDirectory.getText());
				props.setProperty("gedcomFilePath", gedcomFilePath.getText());
				props.setProperty("kipTextFilename", kipTextFilename.getText());
				props.setProperty("csvFileDirectory", csvFileDirectory.getText());
				props.setProperty("vejbyDbSchema", vejbyDbSchema.getText());
				props.setProperty("probateDbSchema", probateDbSchema.getText());
				props.setProperty("cphDbSchema", cphDbSchema.getText());
				props.setProperty("relocationSearch", String.valueOf(btnRelocationTab.getSelection()));
				props.setProperty("censusSearch", String.valueOf(btnCensusTab.getSelection()));
				props.setProperty("probateSearch", String.valueOf(btnProbateTab.getSelection()));
				props.setProperty("polregSearch", String.valueOf(btnPolitietsRegisterblade.getSelection()));
				props.setProperty("burregSearch", String.valueOf(btnBegravelsesregistret.getSelection()));
				props.setProperty("parentSearch", String.valueOf(btnForldre.getSelection()));

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
				probateSource.setText(props.getProperty("probateSource"));
				cphPath.setText(props.getProperty("cphPath"));
				outputDirectory.setText(props.getProperty("outputDirectory"));
			}
		});
		settingsCancelButton.setText("Fortryd");
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
			props.setProperty("cphPath", CPHDB_PATH);
			props.setProperty("outputDirectory", OUTPUT_PATH);
			props.setProperty("gedcomFilePath", GEDCOM_FILE_PATH);
			props.setProperty("kipTextFilename", KIP_TEXT_FILENAME);
			props.setProperty("csvFileDirectory", CSV_FILE_DIRECTORY);
			props.setProperty("vejbyDbSchema", VEJBYDB_SCHEMA);
			props.setProperty("probateDbSchema", PROBATEDB_SCHEMA);
			props.setProperty("cphDbSchema", CPHDB_SCHEMA);
			props.setProperty("relocationSearch", "true");
			props.setProperty("censusSearch", "true");
			props.setProperty("probateSearch", "true");
			props.setProperty("polregSearch", "true");
			props.setProperty("burregSearch", "true");
			props.setProperty("parentSearch", "false");

			storeProperties();
			System.out.println("Egenskaber gemt i " + PROPERTIES_PATH);
		}
	}

	/**
	 * Populate census table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateCensusTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!props.getProperty("censusSearch").equals("true")) {
			return;
		}

		setMessage("Folketællinger hentes");

		censusTable.removeAll();

		final List<CensusIndividual> censuses = CensusIndividual.loadFromDatabase(props.getProperty("vejbyPath"),
				phonName, birthDate.substring(0, 4), deathDate.substring(0, 4));
		Collections.sort(censuses, new CensusIndividualComparator());

		// TODO Add all household members as source details
//		PreparedStatement statement2 = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);
//
//		for (final CensusIndividual ci : cil) {
//			statement2.setString(1, ci.getKIPnr());
//			statement2.setString(2, ci.getHusstands_familienr());
//			rs2 = statement2.executeQuery();
//
//			final StringBuffer sb = new StringBuffer();
//
//			while (rs2.next()) {
//				sb.append(rs2.getString("KILDENAVN") + ", " + rs2.getString("ALDER") + ", "
//						+ rs2.getString("CIVILSTAND") + ", " + rs2.getString("KILDEERHVERV") + ", "
//						+ rs2.getString("STILLING_I_HUSSTANDEN") + " - ");
//			}
//
//			string = sb.toString();
//
//			if (string.length() > 4096) {
//				string = string.substring(0, 4095);
//			}
//
//			ci.setKildedetaljer(string);
//		}
//
//		statement2.close();

		TableItem ti;

		for (final CensusIndividual ci : censuses) {
			ti = new TableItem(censusTable, SWT.NONE);
			ti.setText(ci.toStringArray());
		}
		censusTable.redraw();
		censusTable.update();
		tbtmCensus.getControl().setFocus();
	}

	/**
	 * Populate probate table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateProbateTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!props.getProperty("probateSearch").equals("true")) {
			return;
		}

		setMessage("Skifter hentes");
		probateTable.removeAll();

		final String path = props.getProperty("probatePath");
		final String probateSource = props.getProperty("probateSource");

		final List<Probate> lp = Probate.loadFromDatabase(path, phonName, birthDate, deathDate, probateSource);

		TableItem ti;

		for (final Probate probate : lp) {
			ti = new TableItem(probateTable, SWT.NONE);
			ti.setText(probate.toStringArray());
		}
		probateTable.redraw();
		probateTable.update();
		tbtmProbate.getControl().setFocus();
	}

	/**
	 * Populate relocation table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateRelocationTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!props.getProperty("relocationSearch").equals("true")) {
			return;
		}

		setMessage("Flytninger hentes");
		relocationTable.removeAll();

		final List<Relocation> relocations = Relocation.loadFromDatabase(props.getProperty("vejbyPath"), phonName,
				birthDate, deathDate);

		Collections.sort(relocations, new RelocationComparator());

		TableItem ti;

		for (final Relocation relocation : relocations) {
			ti = new TableItem(relocationTable, SWT.NONE);
			ti.setText(relocation.toStringArray());
		}
		relocationTable.redraw();
		relocationTable.update();
		tbtmRelocation.getControl().setFocus();
	}

	/**
	 * Populate the relocation table from the database
	 *
	 * @param e
	 */
	private void searchById(SelectionEvent e) {
		final String Id = "@I" + searchId.getText() + "@";
		try {
			final Connection conn = DriverManager.getConnection("jdbc:derby:" + props.getProperty("vejbyPath"));
			final DBIndividual individual = new DBIndividual(conn, Id);

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

			populateRelocationTable(phonName, birthDate, deathDate);
			populateCensusTable(phonName, birthDate, deathDate);
			populateProbateTable(phonName, birthDate, deathDate);
			setMessage("Klar");
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
	private void searchByName(SelectionEvent e) {
		if (searchName.getText().equals("")) {
			final Shell[] shells = e.widget.getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
			messageBox.setText("Advarsel");
			messageBox.setMessage("Indtast et navn");
			messageBox.open();
			searchId.setFocus();
			return;
		}

		searchId.setText("");

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

			populateRelocationTable(phonName, birthDate, deathDate);
			populateCensusTable(phonName, birthDate, deathDate);
			populateProbateTable(phonName, birthDate, deathDate);
			setMessage("Klar");
		} catch (final Exception e1) {
			setMessage(e1.getMessage());
			e1.printStackTrace();
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
