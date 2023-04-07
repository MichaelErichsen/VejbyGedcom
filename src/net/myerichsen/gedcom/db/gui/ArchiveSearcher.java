package net.myerichsen.gedcom.db.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.window.Window;
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
import net.myerichsen.gedcom.db.models.IndividualRecord;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class ArchiveSearcher extends Shell {
	// TODO Make database schemas configurable
	// FIXME Find why FLOOR column missing in Polreg
	// FIXME Parents search not finished
	// TODO Find all relocations to and from an individual
	// FIXME Census table has double Horizonal SCROLLbar
	// TODO Doubleclick om sibling row inserts id and name in search bar
	// TODO properties for C:\Users\michael\Downloads\data-20230129T125804Z-001\data
	// hack4dk_burial_person_complete.csv
	// hack4dk_police_address.csv
	// hack4dk_police_person.csv
	// hack4dk_police_position.csv
	// TODO Add ancestors tree tab
	// TODO Add descendants tree tab
	// TODO Debug settings wizard

	/**
	 * Static constants used to initalize properties file
	 */

	private static final String CPHDB_SCHEMA = "CPH";
	private static final String CPHDB_PATH = System.getProperty("user.home") + "/CPHDB";
	private static final String CSV_FILE_DIRECTORY = System.getProperty("user.home")
			+ "/Documents/The Master Genealogist v9/Kilder/DDD";
	private static final String GEDCOM_FILE_PATH = System.getProperty("user.home")
			+ "/Documents/The Master Genealogist v9/Export/Vejby.ged";
	private static final String KIP_TEXT_FILENAME = "kipdata.txt";
	private static final String OUTPUT_PATH = System.getProperty("user.home") + "/Documents/Vejby/VejbyGedcom";
	private static final String PROBATE_SOURCE = "Kronborg";
	private static final String PROBATEDB_PATH = "c:/DerbyDB/gedcom";
	private static final String PROBATEDB_SCHEMA = "GEDCOM";
	private static final String PROPERTIES_PATH = System.getProperty("user.home") + "/ArchiveSearcherX.properties";
	private static final String VEJBYDB_PATH = System.getProperty("user.home") + "/VEJBYDB";
	private static final String VEJBYDB_SCHEMA = "VEJBY";
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
	private Button btnRelocationTab;
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
	 * Create burial registry tab
	 *
	 * @param display
	 * @param tabFolder
	 */

//	private void createBurregTab(Display display, final TabFolder tabFolder) {
//		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
//		tbtmBurreg.setText("Kbhvn. begravelsesregister");
//
//		final Composite burregComposite = new Composite(tabFolder, SWT.NONE);
//		tbtmBurreg.setControl(burregComposite);
//		burregComposite.setLayout(new GridLayout(1, false));
//
//		final Composite burregFilterComposite = new Composite(burregComposite, SWT.BORDER);
//		burregFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		burregFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
//
//		final Label bLabel = new Label(burregFilterComposite, SWT.NONE);
//		bLabel.setText("Filtre: Fornavn");
//
//		txtBurregGiven = new Text(burregFilterComposite, SWT.BORDER);
//		txtBurregGiven.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent e) {
//				BurregGivenFilter.getInstance().setSearchText(txtBurregGiven.getText());
//				burregTableViewer.refresh();
//			}
//		});
//
//		final Label lblEfternavn = new Label(burregFilterComposite, SWT.NONE);
//		lblEfternavn.setText("Efternavn");
//
//		txtBurregSurname = new Text(burregFilterComposite, SWT.BORDER);
//		txtBurregSurname.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent e) {
//				BurregSurnameFilter.getInstance().setSearchText(txtBurregSurname.getText());
//				burregTableViewer.refresh();
//			}
//		});
//
//		final Label lblFder = new Label(burregFilterComposite, SWT.NONE);
//		lblFder.setText("F\u00F8de\u00E5r");
//
//		txtBurregBirthYear = new Text(burregFilterComposite, SWT.BORDER);
//		txtBurregBirthYear.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent e) {
//				BurregBirthDateFilter.getInstance().setSearchText(txtBurregBirthYear.getText());
//				burregTableViewer.refresh();
//			}
//		});
//
//		final Button btnRydFelterne_2 = new Button(burregFilterComposite, SWT.NONE);
//		btnRydFelterne_2.setText("Ryd felterne");
//
//		final ScrolledComposite burregScroller = new ScrolledComposite(burregComposite,
//				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		burregScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		burregScroller.setSize(0, 0);
//		tbtmBurreg.setControl(burregComposite);
//		burregScroller.setExpandHorizontal(true);
//		burregScroller.setExpandVertical(true);
//
//		burregTableViewer = new TableViewer(burregScroller, SWT.BORDER | SWT.FULL_SELECTION);
//		burregTableViewer.addDoubleClickListener(event -> burregPopup(display));
//		burregTable = burregTableViewer.getTable();
//		burregTableViewer.setContentProvider(ArrayContentProvider.getInstance());
//		final ViewerFilter[] filters = new ViewerFilter[3];
//		filters[0] = BurregBirthDateFilter.getInstance();
//		filters[1] = BurregGivenFilter.getInstance();
//		filters[2] = BurregSurnameFilter.getInstance();
//		burregTableViewer.setFilters(filters);
//		burregTableViewer.setComparator(new BurregComparator());
//
//		burregTable.setHeaderVisible(true);
//		burregTable.setLinesVisible(true);
//
//		final TableViewerColumn tableViewerColumn = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnFornavne = tableViewerColumn.getColumn();
//		tblclmnFornavne.setWidth(100);
//		tblclmnFornavne.setText("Fornavne");
//		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getFirstNames();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnEfternavn_1 = tableViewerColumn_1.getColumn();
//		tblclmnEfternavn_1.setWidth(100);
//		tblclmnEfternavn_1.setText("Efternavn");
//		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getLastName();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnDdsdato = tableViewerColumn_2.getColumn();
//		tblclmnDdsdato.setWidth(100);
//		tblclmnDdsdato.setText("D\u00F8dsdato");
//		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getDateOfDeath();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnFder_1 = tableViewerColumn_3.getColumn();
//		tblclmnFder_1.setWidth(100);
//		tblclmnFder_1.setText("F\u00F8de\u00E5r");
//		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getYearOfBirth();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnDdssted = tableViewerColumn_4.getColumn();
//		tblclmnDdssted.setWidth(100);
//		tblclmnDdssted.setText("D\u00F8dssted");
//		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getDeathPlace();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnCivilstand_1 = tableViewerColumn_5.getColumn();
//		tblclmnCivilstand_1.setWidth(100);
//		tblclmnCivilstand_1.setText("Civilstand");
//		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getCivilStatus();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnAdrUdfKbhvn = tableViewerColumn_6.getColumn();
//		tblclmnAdrUdfKbhvn.setWidth(100);
//		tblclmnAdrUdfKbhvn.setText("Adr. udf. Kbhvn.");
//		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getAdressOutsideCph();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnKn_1 = tableViewerColumn_7.getColumn();
//		tblclmnKn_1.setWidth(100);
//		tblclmnKn_1.setText("K\u00F8n");
//		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getSex();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnKommentar_1 = tableViewerColumn_8.getColumn();
//		tblclmnKommentar_1.setWidth(100);
//		tblclmnKommentar_1.setText("Kommentar");
//		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getComment();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnKirkegrd = tableViewerColumn_9.getColumn();
//		tblclmnKirkegrd.setWidth(100);
//		tblclmnKirkegrd.setText("Kirkeg\u00E5rd");
//		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getCemetary();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnKapel = tableViewerColumn_10.getColumn();
//		tblclmnKapel.setWidth(100);
//		tblclmnKapel.setText("Kapel");
//		tableViewerColumn_10.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getChapel();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnGade_1 = tableViewerColumn_11.getColumn();
//		tblclmnGade_1.setWidth(100);
//		tblclmnGade_1.setText("Gade");
//		tableViewerColumn_11.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getStreet();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnKvarter = tableViewerColumn_12.getColumn();
//		tblclmnKvarter.setWidth(100);
//		tblclmnKvarter.setText("Kvarter");
//		tableViewerColumn_12.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getHood();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnGadenr_1 = tableViewerColumn_13.getColumn();
//		tblclmnGadenr_1.setWidth(40);
//		tblclmnGadenr_1.setText("Gadenr.");
//		tableViewerColumn_13.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getStreetNumber();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnBogstav_1 = tableViewerColumn_14.getColumn();
//		tblclmnBogstav_1.setWidth(40);
//		tblclmnBogstav_1.setText("Bogstav");
//		tableViewerColumn_14.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getLetter();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnEtage_1 = tableViewerColumn_15.getColumn();
//		tblclmnEtage_1.setWidth(40);
//		tblclmnEtage_1.setText("Etage");
//		tableViewerColumn_15.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getFloor();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnInstitution = tableViewerColumn_16.getColumn();
//		tblclmnInstitution.setWidth(100);
//		tblclmnInstitution.setText("Institution");
//		tableViewerColumn_16.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getInstitution();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnInstGade = tableViewerColumn_17.getColumn();
//		tblclmnInstGade.setWidth(100);
//		tblclmnInstGade.setText("Inst. gade");
//		tableViewerColumn_17.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getInstitutionStreet();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnInstGadenr = tableViewerColumn_19.getColumn();
//		tblclmnInstGadenr.setWidth(40);
//		tblclmnInstGadenr.setText("Inst. gadenr.");
//		tableViewerColumn_19.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getInstitutionStreet();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnInstKvarter = tableViewerColumn_18.getColumn();
//		tblclmnInstKvarter.setWidth(100);
//		tblclmnInstKvarter.setText("Inst. kvarter");
//		tableViewerColumn_18.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getInstitutionHood();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnErhverv_2 = tableViewerColumn_20.getColumn();
//		tblclmnErhverv_2.setWidth(40);
//		tblclmnErhverv_2.setText("Erhverv");
//		tableViewerColumn_20.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getOccuptations();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnErhvforhtyper = tableViewerColumn_21.getColumn();
//		tblclmnErhvforhtyper.setWidth(100);
//		tblclmnErhvforhtyper.setText("Erhv.forh.typer");
//		tableViewerColumn_21.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getOccupationRelationTypes();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnDdsrsager = tableViewerColumn_22.getColumn();
//		tblclmnDdsrsager.setWidth(100);
//		tblclmnDdsrsager.setText("D\u00F8ds\u00E5rsager");
//		tableViewerColumn_22.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getDeathCauses();
//			}
//		});
//
//		final TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(burregTableViewer, SWT.NONE);
//		final TableColumn tblclmnDdsrsDansk = tableViewerColumn_23.getColumn();
//		tblclmnDdsrsDansk.setWidth(100);
//		tblclmnDdsrsDansk.setText("D\u00F8ds\u00E5rs. dansk");
//		tableViewerColumn_23.setLabelProvider(new ColumnLabelProvider() {
//
//			@Override
//			public String getText(Object element) {
//				final BurregRecord pr = (BurregRecord) element;
//				return pr.getDeathCausesDanish();
//			}
//		});
//
//		burregScroller.setContent(burregTable);
//		burregScroller.setMinSize(burregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//	}

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
				WizardDialog wizardDialog = new WizardDialog(shell, new SettingsWizard(props));
				if (wizardDialog.open() == Window.OK) {
					System.out.println("Ok pressed");
				} else {
					System.out.println("Cancel pressed");
				}
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
			setMessage("Data hentes fra KIP-filerne " + props.getProperty("csvFileDirectory") + " ind i tabellen i "
					+ props.getProperty("vejbyPath"));

			new Thread(() -> {
				final String[] sa = new String[] { props.getProperty("kipTextFilename"),
						props.getProperty("csvFileDirectory"), props.getProperty("vejbyPath") };
				System.out.println(props.getProperty("kipTextFilename") + ", " + props.getProperty("csvFileDirectory")
						+ ", " + props.getProperty("vejbyPath"));
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
			final IndividualRecord individual = new IndividualRecord(conn, Id);

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

			if (btnRelocationTab.getSelection()) {
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
			siblingsTable.forceFocus();
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