package net.myerichsen.gedcom.gui;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.gedcom.db.Fonkod;
import net.myerichsen.gedcom.db.comparators.CensusIndividualComparator;
import net.myerichsen.gedcom.db.comparators.RelocationComparator;
import net.myerichsen.gedcom.db.models.CensusIndividual;
import net.myerichsen.gedcom.db.models.DBIndividual;
import net.myerichsen.gedcom.db.models.Relocation;

/**
 * GUI program to search archive copies in Derby databases
 *
 * @author Michael Erichsen
 * @version 29. mar. 2023
 *
 */

public class ArchiveSearcher extends Shell {

	/**
	 * Static constants
	 */
	private static final String OUTPUT_PATH = "c:/Users/michael/Documents/Vejby/VejbyGedcom";
	private static final String CPHDB_PATH = "c:/Users/michael/CPHDB";
	private static final String PROBATEDB_PATH = "c:/DerbyDB/gedcom";
	private static final String VEJBYDB_PATH = "c:/Users/michael/VEJBYDB";
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
	private final Table probateTable;
	private final Table polregTable;
	private Text vejbyPath;
	private Text probatePath;
	private Text cphPath;
	private Text outputDirectory;
	private final Logger logger = Logger.getLogger("ArchiveSearcher");
	private Properties props;

	// TODO Make tabs visible/invisible

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
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createRelocationTab(tabFolder);

		createCensusTab(tabFolder);

		/**
		 * FTaar Amt Herred Sogn Kildestednavn Husstands_familienr Matr_nr_Adresse
		 * Kildenavn Koen Alder Civilstand Kildeerhverv Stilling_i_husstanden
		 * Kildefoedested Foedt_kildedato Foedeaar Adresse Matrikel Gade_nr
		 * Kildehenvisning Kildekommentar KIPnr Loebenr Fonnavn Kildedetaljer
		 */

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

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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
	 * Create the relocation tab
	 *
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
	 * @param tabFolder
	 */
	private void createCensusTab(TabFolder tabFolder) {
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

		final TableColumn tblclmnAar = new TableColumn(censusTable, SWT.NONE);
		tblclmnAar.setWidth(100);
		tblclmnAar.setText("År");

		final TableColumn tblclmnAmt = new TableColumn(censusTable, SWT.NONE);
		tblclmnAmt.setWidth(100);
		tblclmnAmt.setText("Amt");

		final TableColumn tblclmnHerred = new TableColumn(censusTable, SWT.NONE);
		tblclmnHerred.setWidth(100);
		tblclmnHerred.setText("Herred");

		final TableColumn tblclmnSogn = new TableColumn(censusTable, SWT.NONE);
		tblclmnSogn.setWidth(100);
		tblclmnSogn.setText("Sogn");

		final TableColumn tblclmnKildestednavn = new TableColumn(censusTable, SWT.NONE);
		tblclmnKildestednavn.setWidth(100);
		tblclmnKildestednavn.setText("Kildestednavn");

		final TableColumn tblclmnNr = new TableColumn(censusTable, SWT.NONE);
		tblclmnNr.setWidth(100);
		tblclmnNr.setText("Nr.");

		final TableColumn tblclmnMatrAdr = new TableColumn(censusTable, SWT.NONE);
		tblclmnMatrAdr.setWidth(100);
		tblclmnMatrAdr.setText("Matrikel/adresse");

		final TableColumn tblclmnNavn = new TableColumn(censusTable, SWT.NONE);
		tblclmnNavn.setWidth(100);
		tblclmnNavn.setText("Navn");

		final TableColumn tblclmnKn = new TableColumn(censusTable, SWT.NONE);
		tblclmnKn.setWidth(100);
		tblclmnKn.setText("K\u00F8n");

		final TableColumn tblclmnAlder = new TableColumn(censusTable, SWT.NONE);
		tblclmnAlder.setWidth(100);
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
		tblclmnFoedeaar.setWidth(100);
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
		tblclmnKipNr.setWidth(100);
		tblclmnKipNr.setText("KIP nr.");

		final TableColumn tblclmnLoebenr = new TableColumn(censusTable, SWT.NONE);
		tblclmnLoebenr.setWidth(100);
		tblclmnLoebenr.setText("Løbenr.");

		final TableColumn tblclmnDetaljer = new TableColumn(censusTable, SWT.NONE);
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Detaljer");

		censusScroller.setContent(censusTable);
		censusScroller.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create the search composite
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
			props.setProperty("cphPath", CPHDB_PATH);
			props.setProperty("outputDirectory", OUTPUT_PATH);

			storeProperties();
		}
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateRelocationTable(String phonName, String birthDate, String deathDate) throws SQLException {
		final List<Relocation> relocations = Relocation.loadFromDatabase(props.getProperty("vejbyPath"), phonName,
				birthDate, deathDate);

		Collections.sort(relocations, new RelocationComparator());

		relocationTable.removeAll();
		TableItem ti;

		for (final Relocation relocation : relocations) {
			ti = new TableItem(relocationTable, SWT.NONE);
			ti.setText(relocation.toStringArray());
		}
		relocationTable.redraw();
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

		censusTable.removeAll();
		TableItem ti;

		for (final CensusIndividual ci : censuses) {
			ti = new TableItem(censusTable, SWT.NONE);
			ti.setText(ci.toStringArray());
		}
		censusTable.redraw();
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
				final Shell[] shells = e.widget.getDisplay().getShells();
				final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK);
				messageBox.setText("Advarsel");
				messageBox.setMessage("ID findes ikke i databasen");
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
		} catch (final SQLException e1) {
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
		} catch (final Exception e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Store properties in file
	 */
	private void storeProperties() {
		try {
			final OutputStream output = new FileOutputStream(PROPERTIES_PATH);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			logger.severe("Could not store properties in " + PROPERTIES_PATH);
			e2.printStackTrace();
		}
	}

}
