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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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
import net.myerichsen.gedcom.db.loaders.DBLoader;
import net.myerichsen.gedcom.db.models.BurregRecord;
import net.myerichsen.gedcom.db.models.CensusIndividual;
import net.myerichsen.gedcom.db.models.DBIndividual;
import net.myerichsen.gedcom.db.models.PolregRecord;
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
// TODO Run populate as background tasks
// TODO Run Derby multithreaded
// FIXME Find why FLOOR column missing in Polreg

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
	private Table polregTable;
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
	private Text vejbySchema;
	private Text probateSchema;
	private Text cphSchema;
	private Text csvFileDirectory;
	private Text cphDbPath;
	private Table burregTable;

	// TODO Make tabs visible/invisible. To hide the tab item you call
	// TabItem.dispose(). To show it again, create a
//	new TabItem at the appropriate index.
//
//	Note: When you dispose a TabItem, this does not dispose the control that was
//	the content of the TabItem. The content control just becomes invisible.
//	Therefore, when you create the new TabItem, you can set the same content
//	control into it.

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
		createPolregRab(tabFolder);
		createBurregTab(tabFolder);

		createSettingsTab(tabFolder);

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		createContents();
	}

	/**
	 * Create burial registry tab
	 * 
	 * @param tabFolder
	 */
	private void createBurregTab(TabFolder tabFolder) {
		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
		tbtmBurreg.setText("Begravelser i Kbhvn.");

		final ScrolledComposite burregScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmBurreg.setControl(burregScroller);
		burregScroller.setExpandHorizontal(true);
		burregScroller.setExpandVertical(true);

		burregTable = new Table(burregScroller, SWT.BORDER | SWT.FULL_SELECTION);
		burregTable.setHeaderVisible(true);
		burregTable.setLinesVisible(true);

		final TableColumn tblclmnbrFornavn = new TableColumn(burregTable, SWT.NONE);
		tblclmnbrFornavn.setWidth(48);
		tblclmnbrFornavn.setText("Fornavne");

		TableColumn tblclmnEfternavn = new TableColumn(burregTable, SWT.NONE);
		tblclmnEfternavn.setWidth(29);
		tblclmnEfternavn.setText("Efternavn");

		TableColumn tblclmnDdsdato = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsdato.setWidth(45);
		tblclmnDdsdato.setText("D\u00F8dsdato");

		TableColumn tblclmnFder = new TableColumn(burregTable, SWT.NONE);
		tblclmnFder.setWidth(40);
		tblclmnFder.setText("F\u00F8de\u00E5r");

		TableColumn tblclmnDdssted = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdssted.setWidth(59);
		tblclmnDdssted.setText("D\u00F8dssted");

		TableColumn tblclmnCivilstand_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnCivilstand_1.setWidth(37);
		tblclmnCivilstand_1.setText("Civilstand");

		TableColumn tblclmnAdrUdfKbhvn = new TableColumn(burregTable, SWT.NONE);
		tblclmnAdrUdfKbhvn.setWidth(57);
		tblclmnAdrUdfKbhvn.setText("Adr. udf. Kbhvn.");

		TableColumn tblclmnKn_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnKn_1.setWidth(34);
		tblclmnKn_1.setText("K\u00F8n");

		TableColumn tblclmnKommentar_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnKommentar_1.setWidth(34);
		tblclmnKommentar_1.setText("Kommentar");

		TableColumn tblclmnKirkegrd = new TableColumn(burregTable, SWT.NONE);
		tblclmnKirkegrd.setWidth(43);
		tblclmnKirkegrd.setText("Kirkeg\u00E5rd");

		TableColumn tblclmnKapel = new TableColumn(burregTable, SWT.NONE);
		tblclmnKapel.setWidth(49);
		tblclmnKapel.setText("Kapel");

		TableColumn tblclmnSogn_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnSogn_1.setWidth(45);
		tblclmnSogn_1.setText("Sogn");

		TableColumn tblclmnGade_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnGade_1.setWidth(48);
		tblclmnGade_1.setText("Gade");

		TableColumn tblclmnKvarter = new TableColumn(burregTable, SWT.NONE);
		tblclmnKvarter.setWidth(47);
		tblclmnKvarter.setText("Kvarter");

		TableColumn tblclmnGadenr_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnGadenr_1.setWidth(39);
		tblclmnGadenr_1.setText("Gadenr.");

		TableColumn tblclmnBogstav_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnBogstav_1.setWidth(49);
		tblclmnBogstav_1.setText("Bogstav");

		TableColumn tblclmnEtage_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnEtage_1.setWidth(39);
		tblclmnEtage_1.setText("Etage");

		TableColumn tblclmnInstitution = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstitution.setWidth(44);
		tblclmnInstitution.setText("Institution");

		TableColumn tblclmnInstGade = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstGade.setWidth(53);
		tblclmnInstGade.setText("Inst. gade");

		TableColumn tblclmnInstKvarter = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstKvarter.setWidth(30);
		tblclmnInstKvarter.setText("Inst. kvarter");

		TableColumn tblclmnInstGadenr = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstGadenr.setWidth(34);
		tblclmnInstGadenr.setText("Inst. gadenr.");

		TableColumn tblclmnErhverv_2 = new TableColumn(burregTable, SWT.NONE);
		tblclmnErhverv_2.setWidth(25);
		tblclmnErhverv_2.setText("Erhverv");

		TableColumn tblclmnErhvforhtyper = new TableColumn(burregTable, SWT.NONE);
		tblclmnErhvforhtyper.setWidth(24);
		tblclmnErhvforhtyper.setText("Erhv.forh.typer");

		TableColumn tblclmnDdsrsager = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsrsager.setWidth(100);
		tblclmnDdsrsager.setText("D\u00F8ds\u00E5rsager");

		TableColumn tblclmnDdsrsDansk = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsrsDansk.setWidth(100);
		tblclmnDdsrsDansk.setText("D\u00F8ds\u00E5rs. dansk");

		burregScroller.setContent(burregTable);
		burregScroller.setMinSize(burregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	/**
	 * Create police registry tab
	 * 
	 * @param tabFolder
	 */
	private void createPolregRab(TabFolder tabFolder) {
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

		final TableColumn tblclmnNavn_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnNavn_1.setWidth(50);
		tblclmnNavn_1.setText("Navn");

		final TableColumn tblclmnFdedag = new TableColumn(polregTable, SWT.NONE);
		tblclmnFdedag.setWidth(100);
		tblclmnFdedag.setText("F\u00F8dedag");

		final TableColumn tblclmnErhverv_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnErhverv_1.setWidth(100);
		tblclmnErhverv_1.setText("Erhverv");

		final TableColumn tblclmnGade = new TableColumn(polregTable, SWT.NONE);
		tblclmnGade.setWidth(100);
		tblclmnGade.setText("Gade");

		final TableColumn tblclmnNr_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnNr_1.setWidth(50);
		tblclmnNr_1.setText("Nr.");

		final TableColumn tblclmnBogstav = new TableColumn(polregTable, SWT.NONE);
		tblclmnBogstav.setWidth(50);
		tblclmnBogstav.setText("Bogstav");

		final TableColumn tblclmnEtage = new TableColumn(polregTable, SWT.NONE);
		tblclmnEtage.setWidth(50);
		tblclmnEtage.setText("Etage");

		final TableColumn tblclmnSted_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnSted_1.setWidth(100);
		tblclmnSted_1.setText("Sted");

		final TableColumn tblclmnVrt = new TableColumn(polregTable, SWT.NONE);
		tblclmnVrt.setWidth(100);
		tblclmnVrt.setText("V\u00E6rt");

		final TableColumn tblclmnDag = new TableColumn(polregTable, SWT.NONE);
		tblclmnDag.setWidth(48);
		tblclmnDag.setText("Dag");

		final TableColumn tblclmnMned = new TableColumn(polregTable, SWT.NONE);
		tblclmnMned.setWidth(49);
		tblclmnMned.setText("M\u00E5ned");

		final TableColumn tblclmnr = new TableColumn(polregTable, SWT.NONE);
		tblclmnr.setWidth(49);
		tblclmnr.setText("\u00C5r");

		final TableColumn tblclmnAdresse_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnAdresse_1.setWidth(100);
		tblclmnAdresse_1.setText("Adresse");
		polregScroller.setContent(polregTable);
		polregScroller.setMinSize(polregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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

		final MenuItem mntmLoadGedcom = new MenuItem(menu_1, SWT.NONE);
		mntmLoadGedcom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
		});
		mntmLoadGedcom.setText("Indl\u00E6s GEDCOM i databasen");

		final MenuItem mntmLoadKip = new MenuItem(menu_1, SWT.NONE);
		mntmLoadKip.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				messageBox.setText("Advarsel");
				messageBox.setMessage("Dette tager lang tid!");
				final int buttonID = messageBox.open();

				switch (buttonID) {
				case SWT.OK:
					setMessage("Data hentes fra KIP-filerne " + props.getProperty("csvFileDirectory")
							+ " ind i tabellen i " + props.getProperty("vejbyPath"));

					new Thread(() -> {
						final String[] sa = new String[] { props.getProperty("kipTextFilename"),
								props.getProperty("csvFileDirectory"), props.getProperty("vejbyPath") };
						System.out.println(props.getProperty("kipTextFilename") + ", "
								+ props.getProperty("csvFileDirectory") + ", " + props.getProperty("vejbyPath"));
						CensusDbLoader.main(sa);
					}).start();

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

		final TabItem tbtmSettings = new TabItem(tabFolder, SWT.NONE);
		tbtmSettings.setText("Ops\u00E6tning");

		final Composite compositeOpstning = new Composite(tabFolder, SWT.NONE);
		tbtmSettings.setControl(compositeOpstning);
		compositeOpstning.setLayout(new GridLayout(6, false));

		final Label lblVejbyDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		vejbyPath = new Text(compositeOpstning, SWT.BORDER);
		vejbyPath.setText(props.getProperty("vejbyPath"));
		vejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindVejbyPath = new Button(compositeOpstning, SWT.NONE);
		btnFindVejbyPath.setText("Find");
		btnFindVejbyPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(vejbyPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					vejbyPath.setText(dir);
					props.setProperty("vejbyPath", dir);
				}
			}
		});
		final Label lblVejbySchema = new Label(compositeOpstning, SWT.NONE);
		lblVejbySchema.setText("Vejby database schema");

		vejbySchema = new Text(compositeOpstning, SWT.BORDER);
		vejbySchema.setEnabled(false);
		vejbySchema.setEditable(false);
		vejbySchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		vejbySchema.setText(props.getProperty("vejbySchema"));
		new Label(compositeOpstning, SWT.NONE);

		final Label lblSkifteDatabaseSti = new Label(compositeOpstning, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		probatePath = new Text(compositeOpstning, SWT.BORDER);
		probatePath.setText(props.getProperty("probatePath"));
		probatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindProbatePath = new Button(compositeOpstning, SWT.NONE);
		btnFindProbatePath.setText("Find");
		btnFindProbatePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(probatePath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					probatePath.setText(dir);
					props.setProperty("probatePath", dir);
				}
			}
		});
		final Label lblProbateDatabaseSchema = new Label(compositeOpstning, SWT.NONE);
		lblProbateDatabaseSchema.setText("Skifte database schema");

		probateSchema = new Text(compositeOpstning, SWT.BORDER);
		probateSchema.setEnabled(false);
		probateSchema.setEditable(false);
		probateSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		probateSchema.setText(props.getProperty("probateSchema"));
		new Label(compositeOpstning, SWT.NONE);

		final Label lblSkiftekilde = new Label(compositeOpstning, SWT.NONE);
		lblSkiftekilde.setText("Skiftekilde");

		probateSource = new Text(compositeOpstning, SWT.BORDER);
		probateSource.setText(props.getProperty("probateSource"));
		probateSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);

		cphPath = new Text(compositeOpstning, SWT.BORDER);
		cphPath.setText(props.getProperty("cphDbPath"));
		cphPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblNewLabel = new Label(compositeOpstning, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		cphDbPath = new Text(compositeOpstning, SWT.BORDER);
		cphDbPath.setText(props.getProperty("cphDbPath"));
		cphDbPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindCphPath = new Button(compositeOpstning, SWT.NONE);
		btnFindCphPath.setText("Find");
		btnFindCphPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(cphDbPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					cphDbPath.setText(dir);
					props.setProperty("cphDbPath", dir);
				}
			}
		});
		final Label lblCphDatabaseSchema = new Label(compositeOpstning, SWT.NONE);
		lblCphDatabaseSchema.setText("K\u00F8benhavnsdatabase database schema");

		cphSchema = new Text(compositeOpstning, SWT.BORDER);
		cphSchema.setEnabled(false);
		cphSchema.setEditable(false);
		cphSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cphSchema.setText(props.getProperty("cphSchema"));
		new Label(compositeOpstning, SWT.NONE);

		final Label lblKipCsvFil = new Label(compositeOpstning, SWT.NONE);
		lblKipCsvFil.setText("KIP csv fil sti");

		csvFileDirectory = new Text(compositeOpstning, SWT.BORDER);
		csvFileDirectory.setText(props.getProperty("csvFileDirectory"));
		csvFileDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindCsvPath = new Button(compositeOpstning, SWT.NONE);
		btnFindCsvPath.setText("Find");
		btnFindCsvPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(csvFileDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					csvFileDirectory.setText(dir);
					props.setProperty("csvFileDirectory", dir);
				}
			}
		});
		final Label lblKipTextFilnavn = new Label(compositeOpstning, SWT.NONE);
		lblKipTextFilnavn.setText("KIP cphDbPath filnavn uden sti");

		kipTextFilename = new Text(compositeOpstning, SWT.BORDER);
		kipTextFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		kipTextFilename.setText(props.getProperty("kipTextFilename"));
		new Label(compositeOpstning, SWT.NONE);

		final Label lblUddatasti = new Label(compositeOpstning, SWT.NONE);
		lblUddatasti.setText("Uddatasti");

		outputDirectory = new Text(compositeOpstning, SWT.BORDER);
		outputDirectory.setText(props.getProperty("outputDirectory"));
		outputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindOutpath = new Button(compositeOpstning, SWT.NONE);
		btnFindOutpath.setText("Find");
		btnFindOutpath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(outputDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					outputDirectory.setText(dir);
					props.setProperty("outputDirectory", dir);
				}
			}
		});
		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);

		final Label lblGedcomFilSti = new Label(compositeOpstning, SWT.NONE);
		lblGedcomFilSti.setText("GEDCOM fil sti");

		gedcomFilePath = new Text(compositeOpstning, SWT.BORDER);
		gedcomFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		gedcomFilePath.setText(props.getProperty("gedcomFilePath"));

		final Button btnFindKipCsv = new Button(compositeOpstning, SWT.NONE);
		btnFindKipCsv.setText("Find");
		btnFindKipCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final FileDialog fileDialog = new FileDialog(shells[0]);

				System.out.println(gedcomFilePath.getText());

				fileDialog.setFilterPath(gedcomFilePath.getText());
				fileDialog.setText("Vælg en GEDCOM fil");
				final String[] filterExt = { "*.ged", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				final String file = fileDialog.open();

				if (file != null) {
					gedcomFilePath.setText(file);
					props.setProperty("gedcomFilePath", file);
				}
			}
		});
		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);
		new Label(compositeOpstning, SWT.NONE);

		final Composite checkButtoncomposite = new Composite(compositeOpstning, SWT.BORDER);
		final GridData gd_checkButtoncomposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_checkButtoncomposite.widthHint = 1071;
		checkButtoncomposite.setLayoutData(gd_checkButtoncomposite);
		checkButtoncomposite.setLayout(new GridLayout(7, false));

		final Label lblAktiveSgninger = new Label(checkButtoncomposite, SWT.NONE);
		lblAktiveSgninger.setText("Aktive s\u00F8gninger:");

		final Button btnRelocationTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnRelocationTab.setSelection(Boolean.parseBoolean(props.getProperty("relocationSearch")));
		btnRelocationTab.setText("Flytninger");

		final Button btnCensusTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnCensusTab.setSelection(Boolean.parseBoolean(props.getProperty("censusSearch")));
		btnCensusTab.setText("Folket\u00E6llinger");

		final Button btnProbateTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnProbateTab.setSelection(Boolean.parseBoolean(props.getProperty("probateSearch")));
		btnProbateTab.setText("Skifter");

		final Button btnPolitietsRegisterblade = new Button(checkButtoncomposite, SWT.CHECK);
		btnPolitietsRegisterblade.setSelection(Boolean.parseBoolean(props.getProperty("polregSearch")));
		btnPolitietsRegisterblade.setText("Politiets registerblade");

		final Button btnBegravelsesregistret = new Button(checkButtoncomposite, SWT.CHECK);
		btnBegravelsesregistret.setSelection(Boolean.parseBoolean(props.getProperty("burregSearch")));
		btnBegravelsesregistret.setText("Begravelsesregistret");

		final Button btnForldre = new Button(checkButtoncomposite, SWT.CHECK);
		btnForldre.setEnabled(Boolean.parseBoolean(props.getProperty("parentSearch")));
		btnForldre.setText("For\u00E6ldre");
		new Label(compositeOpstning, SWT.NONE);

		final Composite settingsButtonComposite = new Composite(compositeOpstning, SWT.NONE);
		settingsButtonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		settingsButtonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 5, 1));

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
				props.setProperty("vejbySchema", vejbySchema.getText());
				props.setProperty("probateSchema", probateSchema.getText());
				props.setProperty("cphSchema", cphSchema.getText());
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

		final Button btnCancel = new Button(settingsButtonComposite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				vejbyPath.setText(props.getProperty("vejbyPath"));
				probatePath.setText(props.getProperty("probatePath"));
				probateSource.setText(props.getProperty("probateSource"));
				cphPath.setText(props.getProperty("cphDbPath"));
				outputDirectory.setText(props.getProperty("outputDirectory"));
				gedcomFilePath.setText(props.getProperty("gedcomFilePath"));
				kipTextFilename.setText(props.getProperty("kipTextFilename"));
				csvFileDirectory.setText(props.getProperty("csvFileDirectory"));
				vejbySchema.setText(props.getProperty("vejbySchema"));
				probateSchema.setText(props.getProperty("probateSchema"));
				cphSchema.setText(props.getProperty("cphSchema"));
				btnRelocationTab.setSelection(Boolean.parseBoolean(props.getProperty("relocationSearch")));
				btnCensusTab.setSelection(Boolean.parseBoolean(props.getProperty("censusSearch")));
				btnProbateTab.setSelection(Boolean.parseBoolean(props.getProperty("probateSearch")));
				btnPolitietsRegisterblade.setSelection(Boolean.parseBoolean(props.getProperty("polregSearch")));
				btnBegravelsesregistret.setSelection(Boolean.parseBoolean(props.getProperty("burregSearch")));
				btnForldre.setSelection(Boolean.parseBoolean(props.getProperty("parentSearch")));

			}
		});
		btnCancel.setText("Fortryd");
		new Label(compositeOpstning, SWT.NONE);

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
			props.setProperty("outputDirectory", OUTPUT_PATH);
			props.setProperty("gedcomFilePath", GEDCOM_FILE_PATH);
			props.setProperty("kipTextFilename", KIP_TEXT_FILENAME);
			props.setProperty("csvFileDirectory", CSV_FILE_DIRECTORY);
			props.setProperty("vejbySchema", VEJBYDB_SCHEMA);
			props.setProperty("probateSchema", PROBATEDB_SCHEMA);
			props.setProperty("cphSchema", CPHDB_SCHEMA);
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
	 * Populate the burial registry tab from the database
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateBurregTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!props.getProperty("burregSearch").equals("true")) {
			return;
		}

		setMessage("Bregravelsesregister hentes");
		burregTable.removeAll();

		final List<BurregRecord> lbr = BurregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName,
				birthDate, deathDate);

		TableItem ti;

		for (final BurregRecord br : lbr) {
			ti = new TableItem(burregTable, SWT.NONE);
			ti.setText(br.toStringArray());
		}
		burregTable.redraw();
		burregTable.update();

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
	 * Populate police registry table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populatePolregTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!props.getProperty("polregSearch").equals("true")) {
			return;
		}

		setMessage("Politiregister hentes");
		polregTable.removeAll();

		final List<PolregRecord> lpr = PolregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName,
				birthDate, deathDate);

		TableItem ti;

		for (final PolregRecord pr : lpr) {
			ti = new TableItem(polregTable, SWT.NONE);
			ti.setText(pr.toStringArray());
		}
		polregTable.redraw();
		polregTable.update();
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
	 * Populate all tables
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateTables(final String phonName, final String birthDate, final String deathDate)
			throws SQLException {
		populateRelocationTable(phonName, birthDate, deathDate);
		populateCensusTable(phonName, birthDate, deathDate);
		populateProbateTable(phonName, birthDate, deathDate);
		populatePolregTable(phonName, birthDate, deathDate);
		populateBurregTable(phonName, birthDate, deathDate);
		setMessage("Klar");
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

			populateTables(phonName, birthDate, deathDate);
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

			populateTables(phonName, birthDate, deathDate);
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
