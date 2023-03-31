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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import net.myerichsen.gedcom.db.models.CensusRecord;
import net.myerichsen.gedcom.db.models.IndividualRecord;
import net.myerichsen.gedcom.db.models.RelocationRecord;
import net.myerichsen.gedcom.db.util.CensusIndividualComparator;
import net.myerichsen.gedcom.db.util.RelocationComparator;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * @author Michael Erichsen
 * @version 31. mar. 2023
 *
 */
public class ArchiveSearcher extends Shell {
	// TODO Make database schemas configurable
	// TODO Run populate as background tasks
	// TODO Run Derby multithreaded
	// FIXME Find why FLOOR column missing in Polreg
	// FIXME RelocationRecord and census scrolls differently from each other
	// TODO Double click on a table row should display a pop up with the contents
	// and copy it to clipboard
	// TODO Filter for census table
	// FIXME Burial table is empty
	// TODO Add all household members as source details
	// TODO Make database schemas configurable
	// TODO Run populate as background tasks
	// TODO Run Derby multithreaded
	// FIXME Find why FLOOR column missing in Polreg
	// FIXME RelocationRecord and census scrolls differently from each other
	// TODO Double click on a table row should display a pop up with the contents
	// and copy it to clipboard
	// TODO Filter for census table
	// FIXME Burial table is empty
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
	private static final String PROPERTIES_PATH = "c:/Users/michael/ArchiveSearcherX.properties";
	private static final String VEJBYDB_PATH = "c:/Users/michael/VEJBYDB";
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
	private final Text searchId;
	private final Text searchName;
	private final Text searchBirth;
	private final Text searchDeath;
	private final Table relocationTable;
	private Button btnRelocationTab;
	private Text vejbyPath;
	private Text vejbySchema;
	private Text probatePath;
	private Text probateSchema;
	private Text probateSource;
	private Text cphPath;
	private Text cphDbPath;
	private Text cphSchema;
	private Text csvFileDirectory;
	private Text kipTextFilename;
	private Text outputDirectory;
	private Text gedcomFilePath;
	private Button btnCensusTab;
	private Button btnProbateTab;
	private Button btnPolitietsRegisterblade;
	private Button btnBegravelsesregistret;
	private Button btnForldre;
	private Table censusTable;

	/**
	 * Create the shell.
	 *
	 * @param display
	 */
	public ArchiveSearcher(Display display) {
		super(display, SWT.SHELL_TRIM);
		getProperties();
		setLayout(new GridLayout(1, false));
		final Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		final MenuItem mntmFiler = new MenuItem(menu, SWT.CASCADE);
		mntmFiler.setText("Filer");
		final Menu menu_1 = new Menu(mntmFiler);
		mntmFiler.setMenu(menu_1);

		final MenuItem mntmL = new MenuItem(menu_1, SWT.NONE);
		mntmL.addSelectionListener(new SelectionAdapter() {
			private Properties props;

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
		mntmL.setText("Indl\u00E6s GEDCOM i databasen");

		final MenuItem mntmIndlsKipFiler = new MenuItem(menu_1, SWT.NONE);
		mntmIndlsKipFiler.addSelectionListener(new SelectionAdapter() {
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
		mntmIndlsKipFiler.setText("Indl\u00E6s KIP filer i databasen");

		final MenuItem mntmAfslut = new MenuItem(menu_1, SWT.NONE);
		mntmAfslut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.display.dispose();
			}
		});
		mntmAfslut.setText("Afslut");
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

		final TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final TabItem tbtmFlytninger = new TabItem(tabFolder, SWT.NONE);
		tbtmFlytninger.setText("Flytninger");

		final ScrolledComposite relocationScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmFlytninger.setControl(relocationScroller);
		relocationScroller.setExpandHorizontal(true);
		relocationScroller.setExpandVertical(true);

		final TableViewer relocationTableViewer = new TableViewer(relocationScroller, SWT.BORDER | SWT.FULL_SELECTION);
		relocationTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TableItem[] tia = relocationTable.getSelection();
				TableItem ti = tia[0];

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 9; i++)
					sb.append(ti.getText(i) + ",\n");
				Shell[] shells = display.getShells();
				final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_INFORMATION | SWT.OK);
				messageBox.setText("Flytning");
				messageBox.setMessage(sb.toString());
				messageBox.open();
			}
		});
		relocationTable = relocationTableViewer.getTable();
		relocationTable.setLinesVisible(true);
		relocationTable.setHeaderVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnId = tableViewerColumn.getColumn();
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFornavn = tableViewerColumn_1.getColumn();
		tblclmnFornavn.setWidth(100);
		tblclmnFornavn.setText("Fornavn");

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn = tableViewerColumn_2.getColumn();
		tblclmnEfternavn.setWidth(100);
		tblclmnEfternavn.setText("Efternavn");

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFlyttedato = tableViewerColumn_3.getColumn();
		tblclmnFlyttedato.setWidth(100);
		tblclmnFlyttedato.setText("Flyttedato");

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnTil = tableViewerColumn_4.getColumn();
		tblclmnTil.setWidth(100);
		tblclmnTil.setText("Til");

		final TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFra = tableViewerColumn_5.getColumn();
		tblclmnFra.setWidth(100);
		tblclmnFra.setText("Fra");

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer = tableViewerColumn_6.getColumn();
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Detaljer");

		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFdselsdato = tableViewerColumn_7.getColumn();
		tblclmnFdselsdato.setWidth(100);
		tblclmnFdselsdato.setText("F\u00F8dselsdato");

		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnForldre = tableViewerColumn_8.getColumn();
		tblclmnForldre.setWidth(100);
		tblclmnForldre.setText("For\u00E6ldre");
		relocationScroller.setContent(relocationTable);
		relocationScroller.setMinSize(relocationTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		createSettingsTab(tabFolder);

		TabItem tbtmSkifter = new TabItem(tabFolder, SWT.NONE);
		tbtmSkifter.setText("Skifter");

		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmSkifter.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		TabItem tbtmPoliietsRegisterblade = new TabItem(tabFolder, SWT.NONE);
		tbtmPoliietsRegisterblade.setText("Poliiets Registerblade");

		ScrolledComposite scrolledComposite_1 = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmPoliietsRegisterblade.setControl(scrolledComposite_1);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);

		TabItem tbtmKbhvnBegravelsesregister = new TabItem(tabFolder, SWT.NONE);
		tbtmKbhvnBegravelsesregister.setText("Kbhvn. begravelsesregister");

		ScrolledComposite scrolledComposite_2 = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmKbhvnBegravelsesregister.setControl(scrolledComposite_2);
		scrolledComposite_2.setExpandHorizontal(true);
		scrolledComposite_2.setExpandVertical(true);

		TabItem tbtmForldre = new TabItem(tabFolder, SWT.NONE);
		tbtmForldre.setText("For\u00E6ldre");

		ScrolledComposite scrolledComposite_3 = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmForldre.setControl(scrolledComposite_3);
		scrolledComposite_3.setExpandHorizontal(true);
		scrolledComposite_3.setExpandVertical(true);

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

		btnRelocationTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnRelocationTab.setSelection(Boolean.parseBoolean(props.getProperty("relocationSearch")));
		btnRelocationTab.setText("Flytninger");

		btnCensusTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnCensusTab.setSelection(Boolean.parseBoolean(props.getProperty("censusSearch")));
		btnCensusTab.setText("Folket\u00E6llinger");

		btnProbateTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnProbateTab.setSelection(Boolean.parseBoolean(props.getProperty("probateSearch")));
		btnProbateTab.setText("Skifter");

		btnPolitietsRegisterblade = new Button(checkButtoncomposite, SWT.CHECK);
		btnPolitietsRegisterblade.setSelection(Boolean.parseBoolean(props.getProperty("polregSearch")));
		btnPolitietsRegisterblade.setText("Politiets registerblade");

		btnBegravelsesregistret = new Button(checkButtoncomposite, SWT.CHECK);
		btnBegravelsesregistret.setSelection(Boolean.parseBoolean(props.getProperty("burregSearch")));
		btnBegravelsesregistret.setText("Begravelsesregistret");

		btnForldre = new Button(checkButtoncomposite, SWT.CHECK);
		btnForldre.setSelection(true);
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

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createContents();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(1112, 625);

	}

	/**
	 * Create the settings tab
	 *
	 * @param tabFolder
	 */
	private void createSettingsTab(final TabFolder tabFolder) {

		TabItem tbtmFolketlling = new TabItem(tabFolder, SWT.NONE);
		tbtmFolketlling.setText("Folket\u00E6lling");

		ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmFolketlling.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		TableViewer tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TableItem[] tia = censusTable.getSelection();
				TableItem ti = tia[0];

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 24; i++)
					sb.append(ti.getText(i) + ",\n");
				Shell[] shells = display.getShells();
				final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_INFORMATION | SWT.OK);
				messageBox.setText("Folketælling");
				messageBox.setMessage(sb.toString());
				messageBox.open();
			}
		});
		censusTable = tableViewer.getTable();
		censusTable.setLinesVisible(true);
		censusTable.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnr = tableViewerColumn.getColumn();
		tblclmnr.setWidth(40);
		tblclmnr.setText("\u00C5r");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnAmt = tableViewerColumn_1.getColumn();
		tblclmnAmt.setWidth(40);
		tblclmnAmt.setText("Amt");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnHerred = tableViewerColumn_2.getColumn();
		tblclmnHerred.setWidth(40);
		tblclmnHerred.setText("Herred");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnSogn = tableViewerColumn_3.getColumn();
		tblclmnSogn.setWidth(40);
		tblclmnSogn.setText("Sogn");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnKildestednavn = tableViewerColumn_4.getColumn();
		tblclmnKildestednavn.setWidth(40);
		tblclmnKildestednavn.setText("Kildestednavn");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnHusstfamnr = tableViewerColumn_5.getColumn();
		tblclmnHusstfamnr.setWidth(40);
		tblclmnHusstfamnr.setText("Husst./fam.nr.");

		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnMatrnraddr = tableViewerColumn_6.getColumn();
		tblclmnMatrnraddr.setWidth(40);
		tblclmnMatrnraddr.setText("Matr.nr.addr.");

		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnKildenavn = tableViewerColumn_7.getColumn();
		tblclmnKildenavn.setWidth(40);
		tblclmnKildenavn.setText("Kildenavn");

		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnKn = tableViewerColumn_8.getColumn();
		tblclmnKn.setWidth(40);
		tblclmnKn.setText("K\u00F8n");

		TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnAlder = tableViewerColumn_9.getColumn();
		tblclmnAlder.setWidth(40);
		tblclmnAlder.setText("Alder");

		TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnCivilstand = tableViewerColumn_10.getColumn();
		tblclmnCivilstand.setWidth(40);
		tblclmnCivilstand.setText("Civilstand");

		TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnErhverv = tableViewerColumn_11.getColumn();
		tblclmnErhverv.setWidth(40);
		tblclmnErhverv.setText("Erhverv");

		TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnStillHusst = tableViewerColumn_12.getColumn();
		tblclmnStillHusst.setWidth(40);
		tblclmnStillHusst.setText("Still. husst.");

		TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnFdested = tableViewerColumn_13.getColumn();
		tblclmnFdested.setWidth(40);
		tblclmnFdested.setText("F\u00F8dested");

		TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnFdedato = tableViewerColumn_14.getColumn();
		tblclmnFdedato.setWidth(40);
		tblclmnFdedato.setText("F\u00F8dedato");

		TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnFder = tableViewerColumn_15.getColumn();
		tblclmnFder.setWidth(40);
		tblclmnFder.setText("F\u00F8de\u00E5r");

		TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnAdresse = tableViewerColumn_16.getColumn();
		tblclmnAdresse.setWidth(40);
		tblclmnAdresse.setText("Adresse");

		TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnMatrikel = tableViewerColumn_17.getColumn();
		tblclmnMatrikel.setWidth(40);
		tblclmnMatrikel.setText("Matrikel");

		TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnGadenr = tableViewerColumn_18.getColumn();
		tblclmnGadenr.setWidth(52);
		tblclmnGadenr.setText("Gadenr.");

		TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnHenvisning = tableViewerColumn_19.getColumn();
		tblclmnHenvisning.setWidth(40);
		tblclmnHenvisning.setText("Henvisning");

		TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnKommentar = tableViewerColumn_20.getColumn();
		tblclmnKommentar.setWidth(47);
		tblclmnKommentar.setText("Kommentar");

		TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnKipNr = tableViewerColumn_21.getColumn();
		tblclmnKipNr.setWidth(58);
		tblclmnKipNr.setText("KIP nr.");

		TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnLbenr = tableViewerColumn_22.getColumn();
		tblclmnLbenr.setWidth(67);
		tblclmnLbenr.setText("L\u00F8benr.");

		TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDetaljer_1 = tableViewerColumn_23.getColumn();
		tblclmnDetaljer_1.setWidth(100);
		tblclmnDetaljer_1.setText("Detaljer");
		scrolledComposite.setContent(censusTable);
		scrolledComposite.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
			props.setProperty("parentSearch", "true");

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
//		burregTable.removeAll();
//
//		if (!btnBegravelsesregistret.getSelection()) {
//			return;
//		}
//
//		setMessage("Begravelsesregister hentes");
//
//		final List<BurregRecord> lbr = BurregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName,
//				birthDate, deathDate);
//
//		TableItem ti;
//
//		for (final BurregRecord br : lbr) {
//			ti = new TableItem(burregTable, SWT.NONE);
//			ti.setText(br.toStringArray());
//		}
//		burregTable.redraw();
//		burregTable.update();

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
		censusTable.removeAll();

		if (!btnCensusTab.getSelection()) {
			return;
		}

		setMessage("Folketællinger hentes");

		final List<CensusRecord> censuses = CensusRecord.loadFromDatabase(props.getProperty("vejbyPath"), phonName,
				birthDate.substring(0, 4), deathDate.substring(0, 4));
		Collections.sort(censuses, new CensusIndividualComparator());

		// TODO Add all household members as source details
//		PreparedStatement statement2 = conn.prepareStatement(SELECT_CENSUS_HOUSEHOLD);
//
//		for (final CensusRecord ci : cil) {
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

		for (final CensusRecord ci : censuses) {
			ti = new TableItem(censusTable, SWT.NONE);
			ti.setText(ci.toStringArray());
		}
		censusTable.redraw();
		censusTable.update();
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateParentsTable(String id) throws SQLException {
//		parentsTable.removeAll();
//
//		if (!btnForldre.getSelection()) {
//			return;
//		}
//
//		setMessage("Forældre hentes");
//
//		final List<ParentRecord> lpr = ParentRecord.loadFromDatabase(props.getProperty("vejbyPath"), id);
//
//		TableItem ti;
//
//		for (final ParentRecord pr : lpr) {
//			ti = new TableItem(parentsTable, SWT.NONE);
//			ti.setText(pr.toStringArray());
//		}
//		parentsTable.redraw();
//		parentsTable.update();
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	private void populateParentsTable(String phonName, String birthDate, String deathDate) {
		// TODO Auto-generated method stub

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
//		polregTable.removeAll();
//
//		if (!btnPolitietsRegisterblade.getSelection()) {
//			return;
//		}
//
//		setMessage("Politiregister hentes");
//
//		final List<PolregRecord> lpr = PolregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName,
//				birthDate, deathDate);
//
//		TableItem ti;
//
//		for (final PolregRecord pr : lpr) {
//			ti = new TableItem(polregTable, SWT.NONE);
//			ti.setText(pr.toStringArray());
//		}
//		polregTable.redraw();
//		polregTable.update();
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
//		probateTable.removeAll();
//
//		if (!btnProbateTab.getSelection()) {
//			return;
//		}
//
//		setMessage("Skifter hentes");
//
//		final String path = props.getProperty("probatePath");
//		final String probateSource = props.getProperty("probateSource");
//
//		final List<ProbateRecord> lp = ProbateRecord.loadFromDatabase(path, phonName, birthDate, deathDate,
//				probateSource);
//
//		TableItem ti;
//
//		for (final ProbateRecord probateRecord : lp) {
//			ti = new TableItem(probateTable, SWT.NONE);
//			ti.setText(probateRecord.toStringArray());
//		}
//		probateTable.redraw();
//		probateTable.update();
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
		relocationTable.removeAll();

		if (!btnRelocationTab.getSelection()) {
			return;
		}

		setMessage("Flytninger hentes");

		final List<RelocationRecord> relocationRecords = RelocationRecord
				.loadFromDatabase(props.getProperty("vejbyPath"), phonName, birthDate, deathDate);

		Collections.sort(relocationRecords, new RelocationComparator());

		TableItem ti;

		for (final RelocationRecord relocationRecord : relocationRecords) {
			ti = new TableItem(relocationTable, SWT.NONE);
			ti.setText(relocationRecord.toStringArray());
		}
		relocationTable.redraw();
		relocationTable.update();
	}

	/**
	 * Search by ID
	 *
	 * @param e
	 */
	private void searchById(SelectionEvent e) {
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

			populateRelocationTable(phonName, birthDate, deathDate);
			populateCensusTable(phonName, birthDate, deathDate);
			populateProbateTable(phonName, birthDate, deathDate);
			populatePolregTable(phonName, birthDate, deathDate);
			populateBurregTable(phonName, birthDate, deathDate);
			populateParentsTable(Id);
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
			populatePolregTable(phonName, birthDate, deathDate);
			populateBurregTable(phonName, birthDate, deathDate);
			populateParentsTable(phonName, birthDate, deathDate);
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
