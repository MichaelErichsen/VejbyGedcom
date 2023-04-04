package net.myerichsen.gedcom.db.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import net.myerichsen.gedcom.db.models.CensusHousehold;
import net.myerichsen.gedcom.db.models.CensusRecord;
import net.myerichsen.gedcom.db.models.IndividualRecord;
import net.myerichsen.gedcom.db.models.PolregRecord;
import net.myerichsen.gedcom.db.models.ProbateRecord;
import net.myerichsen.gedcom.db.models.RelocationRecord;
import net.myerichsen.gedcom.db.models.SiblingRecord;
import net.myerichsen.gedcom.db.util.BurregBirthDateFilter;
import net.myerichsen.gedcom.db.util.BurregGivenFilter;
import net.myerichsen.gedcom.db.util.BurregSurnameFilter;
import net.myerichsen.gedcom.db.util.CensusBirthPlaceFilter;
import net.myerichsen.gedcom.db.util.CensusCountyFilter;
import net.myerichsen.gedcom.db.util.CensusNameFilter;
import net.myerichsen.gedcom.db.util.CensusParishFilter;
import net.myerichsen.gedcom.db.util.CensusSexFilter;
import net.myerichsen.gedcom.db.util.CensusYearFilter;
import net.myerichsen.gedcom.db.util.PolregAddressFilter;
import net.myerichsen.gedcom.db.util.PolregBirthdateFilter;
import net.myerichsen.gedcom.db.util.ProbatePlaceFilter;
import net.myerichsen.gedcom.db.util.RelocationGivenFilter;
import net.myerichsen.gedcom.db.util.RelocationSurnameFilter;
import net.myerichsen.gedcom.util.Fonkod;

/**
 * @author Michael Erichsen
 * @version 4. apr. 2023
 *
 */
public class ArchiveSearcher extends Shell {
	// TODO Change all tables to content and label providers
	// TODO Make database schemas configurable
	// TODO Run populate as background tasks
	// TODO Run Derby multithreaded
	// FIXME Find why FLOOR column missing in Polreg
	// FIXME Census table missing scrollbar
	// TODO Add shortcuts for "Copy" on popups
	// FIXME Parents ID search not using parents names

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
	private Text searchId;
	private Text searchName;
	private Text searchBirth;
	private Text searchDeath;
	private Table relocationTable;
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
	private Button btnSiblings;
	private Table censusTable;
	private final Shell shell;
	private List<CensusRecord> household;
	private Table probateTable;
	private Table polregTable;
	private Table burregTable;
	private Table siblingsTable;
	private Text searchFather;
	private Text searchMother;
	private final TabFolder tabFolder;
	private Text txtCensusYear;
	private TableViewer censusTableViewer;
	private Text txtCensusCounty;
	private Text txtCensusParish;
	private Text txtCensusName;
	private Text txtCensusSex;
	private Text txtBirthPlace;
	private Text txtRelocationGiven;
	private Text txtBurregSurname;
	private Text txtBurregBirthYear;
	private TableViewer relocationTableViewer;
	private Text txtRelocationSurname;
	private TableViewer polregTableViewer;
	private TableViewer burregTableViewer;
	private TableViewer probateTableViewer;
	private TableViewer siblingsTableViewer;

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

		createRelocationTab(tabFolder);
		createCensusTab(tabFolder);
		createProbateTab(display, tabFolder);
		createPolregTab(tabFolder);
		createBurregTab(display, tabFolder);
		createSiblingsTab(tabFolder);
		createSettingsTab(tabFolder);

		messageField = new Text(this, SWT.BORDER);
		messageField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		createContents();
	}

	/**
	 * Create setting tab
	 * 
	 * @param tabFolder
	 */
	private void createSettingsTab(TabFolder tabFolder) {
		final TabItem tbtmSettings = new TabItem(tabFolder, SWT.NONE);
		tbtmSettings.setText("Indstillinger");

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

		btnSiblings = new Button(checkButtoncomposite, SWT.CHECK);
		btnSiblings.setSelection(true);
		btnSiblings.setText("S\u00F8skende");
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
				props.setProperty("siblingSearch", String.valueOf(btnSiblings.getSelection()));

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
				btnSiblings.setSelection(Boolean.parseBoolean(props.getProperty("siblingSearch")));

			}
		});
		btnCancel.setText("Fortryd");
		new Label(compositeOpstning, SWT.NONE);

	}

	/**
	 * @param display
	 */
	private void burregPopup(Display display) {
		final TableItem[] tia = burregTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 25; i++) {
			if (ti.getText(i).length() > 0) {
				if (ti.getText(i).length() > 0) {
					sb.append(ti.getText(i).trim() + ", ");
				}
			}
		}

		sb.append("\n");

		final String string = sb.toString();

		final MessageDialog dialog = new MessageDialog(shell, "Begravelser", null, string, MessageDialog.INFORMATION,
				new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	/**
	 *
	 */
	private void censusPopup() {
		final TableItem[] tia = censusTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 24; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}
		sb.append("\n");

		try {
			sb.append(getCensusHousehold(ti.getText(21), ti.getText(5)));
		} catch (final SQLException e) {
			setMessage(e.getMessage());
		}

		final MessageDialog dialog = new MessageDialog(shell, "Folketælling", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			try {
				final List<CensusRecord> lcr = CensusHousehold.loadFromDatabase(props.getProperty("vejbyPath"),
						ti.getText(21), ti.getText(5));
				final StringBuffer sb2 = new StringBuffer();

				for (final CensusRecord element : lcr) {
					sb2.append(element.toString() + "\n");
				}

				final Clipboard clipboard = new Clipboard(display);
				final TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { sb2.toString() }, new Transfer[] { textTransfer });
				clipboard.dispose();

			} catch (final SQLException e) {
				setMessage(e.getMessage());
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param display
	 * @param tabFolder
	 */

	// TODO Filter burreg for given name, surname, birth year
	// TODO Sort burreg by birth year

	private void createBurregTab(Display display, final TabFolder tabFolder) {
		final TabItem tbtmBurreg = new TabItem(tabFolder, SWT.NONE);
		tbtmBurreg.setText("Kbhvn. begravelsesregister");

		Composite burregComposite = new Composite(tabFolder, SWT.NONE);
		tbtmBurreg.setControl(burregComposite);
		burregComposite.setLayout(new GridLayout(1, false));

		Composite burregFilterComposite = new Composite(burregComposite, SWT.BORDER);
		burregFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		burregFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label bLabel = new Label(burregFilterComposite, SWT.NONE);
		bLabel.setText("Filtre: Fornavn");

		txtRelocationGiven = new Text(burregFilterComposite, SWT.BORDER);

		Label lblEfternavn = new Label(burregFilterComposite, SWT.NONE);
		lblEfternavn.setText("Efternavn");

		txtBurregSurname = new Text(burregFilterComposite, SWT.BORDER);

		Label lblFder = new Label(burregFilterComposite, SWT.NONE);
		lblFder.setText("F\u00F8de\u00E5r");

		txtBurregBirthYear = new Text(burregFilterComposite, SWT.BORDER);

		Button btnRydFelterne_2 = new Button(burregFilterComposite, SWT.NONE);
		btnRydFelterne_2.setText("Ryd felterne");

		final ScrolledComposite burregScroller = new ScrolledComposite(burregComposite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		burregScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		burregScroller.setSize(0, 0);
		tbtmBurreg.setControl(burregScroller);
		burregScroller.setExpandHorizontal(true);
		burregScroller.setExpandVertical(true);

		burregTableViewer = new TableViewer(burregScroller, SWT.BORDER | SWT.FULL_SELECTION);
		burregTableViewer.addDoubleClickListener(event -> burregPopup(display));
		burregTable = burregTableViewer.getTable();
		burregTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		ViewerFilter[] filters = new ViewerFilter[3];
		filters[0] = BurregBirthDateFilter.getInstance();
		filters[1] = BurregGivenFilter.getInstance();
		filters[2] = BurregSurnameFilter.getInstance();
		burregTableViewer.setFilters(filters);

		burregTable.setHeaderVisible(true);
		burregTable.setLinesVisible(true);

		final TableColumn tblclmnbrFornavn = new TableColumn(burregTable, SWT.NONE);
		tblclmnbrFornavn.setWidth(100);
		tblclmnbrFornavn.setText("Fornavne");

		final TableColumn tblclmnEfternavn1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnEfternavn1.setWidth(100);
		tblclmnEfternavn1.setText("Efternavn");

		final TableColumn tblclmnDdsdato = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsdato.setWidth(100);
		tblclmnDdsdato.setText("D\u00F8dsdato");

		final TableColumn tblclmnFder = new TableColumn(burregTable, SWT.NONE);
		tblclmnFder.setWidth(50);
		tblclmnFder.setText("F\u00F8de\u00E5r");

		final TableColumn tblclmnDdssted = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdssted.setWidth(100);
		tblclmnDdssted.setText("D\u00F8dssted");

		final TableColumn tblclmnCivilstand_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnCivilstand_1.setWidth(100);
		tblclmnCivilstand_1.setText("Civilstand");

		final TableColumn tblclmnAdrUdfKbhvn = new TableColumn(burregTable, SWT.NONE);
		tblclmnAdrUdfKbhvn.setWidth(100);
		tblclmnAdrUdfKbhvn.setText("Adr. udf. Kbhvn.");

		final TableColumn tblclmnKn_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnKn_1.setWidth(50);
		tblclmnKn_1.setText("K\u00F8n");

		final TableColumn tblclmnKommentar_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnKommentar_1.setWidth(100);
		tblclmnKommentar_1.setText("Kommentar");

		final TableColumn tblclmnKirkegrd = new TableColumn(burregTable, SWT.NONE);
		tblclmnKirkegrd.setWidth(100);
		tblclmnKirkegrd.setText("Kirkeg\u00E5rd");

		final TableColumn tblclmnKapel = new TableColumn(burregTable, SWT.NONE);
		tblclmnKapel.setWidth(100);
		tblclmnKapel.setText("Kapel");

		final TableColumn tblclmnSogn_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnSogn_1.setWidth(100);
		tblclmnSogn_1.setText("Sogn");

		final TableColumn tblclmnGade_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnGade_1.setWidth(100);
		tblclmnGade_1.setText("Gade");

		final TableColumn tblclmnKvarter = new TableColumn(burregTable, SWT.NONE);
		tblclmnKvarter.setWidth(100);
		tblclmnKvarter.setText("Kvarter");

		final TableColumn tblclmnGadenr_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnGadenr_1.setWidth(50);
		tblclmnGadenr_1.setText("Gadenr.");

		final TableColumn tblclmnBogstav_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnBogstav_1.setWidth(50);
		tblclmnBogstav_1.setText("Bogstav");

		final TableColumn tblclmnEtage_1 = new TableColumn(burregTable, SWT.NONE);
		tblclmnEtage_1.setWidth(50);
		tblclmnEtage_1.setText("Etage");

		final TableColumn tblclmnInstitution = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstitution.setWidth(100);
		tblclmnInstitution.setText("Institution");

		final TableColumn tblclmnInstGade = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstGade.setWidth(100);
		tblclmnInstGade.setText("Inst. gade");

		final TableColumn tblclmnInstKvarter = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstKvarter.setWidth(100);
		tblclmnInstKvarter.setText("Inst. kvarter");

		final TableColumn tblclmnInstGadenr = new TableColumn(burregTable, SWT.NONE);
		tblclmnInstGadenr.setWidth(50);
		tblclmnInstGadenr.setText("Inst. gadenr.");

		final TableColumn tblclmnErhverv_2 = new TableColumn(burregTable, SWT.NONE);
		tblclmnErhverv_2.setWidth(100);
		tblclmnErhverv_2.setText("Erhverv");

		final TableColumn tblclmnErhvforhtyper = new TableColumn(burregTable, SWT.NONE);
		tblclmnErhvforhtyper.setWidth(50);
		tblclmnErhvforhtyper.setText("Erhv.forh.typer");

		final TableColumn tblclmnDdsrsager = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsrsager.setWidth(100);
		tblclmnDdsrsager.setText("D\u00F8ds\u00E5rsager");

		final TableColumn tblclmnDdsrsDansk = new TableColumn(burregTable, SWT.NONE);
		tblclmnDdsrsDansk.setWidth(100);
		tblclmnDdsrsDansk.setText("D\u00F8ds\u00E5rs. dansk");

		burregScroller.setContent(burregTable);
		burregScroller.setMinSize(burregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create the census tab
	 *
	 * @param tabFolder
	 */
	private void createCensusTab(final TabFolder tabFolder) {
		final TabItem tbtmFolketlling = new TabItem(tabFolder, SWT.NONE);
		tbtmFolketlling.setText("Folket\u00E6llinger");

		final Composite censusComposite = new Composite(tabFolder, SWT.NONE);
		tbtmFolketlling.setControl(censusComposite);
		censusComposite.setLayout(new GridLayout(1, false));

		final Composite censusFilterComposite = new Composite(censusComposite, SWT.BORDER);
		censusFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		censusFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(censusFilterComposite, SWT.NONE);
		aLabel.setText("Filtre: \u00C5r");

		txtCensusYear = new Text(censusFilterComposite, SWT.BORDER);

		Label lblAmt = new Label(censusFilterComposite, SWT.NONE);
		lblAmt.setText("Amt");

		txtCensusCounty = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusCounty.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusCountyFilter.getInstance().setSearchText(txtCensusCounty.getText());
				censusTableViewer.refresh();
			}
		});

		Label lblSogn = new Label(censusFilterComposite, SWT.NONE);
		lblSogn.setText("Sogn");

		txtCensusParish = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusParish.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusParishFilter.getInstance().setSearchText(txtCensusParish.getText());
				censusTableViewer.refresh();
			}
		});

		Label lblNavn = new Label(censusFilterComposite, SWT.NONE);
		lblNavn.setText("Navn");

		txtCensusName = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusNameFilter.getInstance().setSearchText(txtCensusName.getText());
				censusTableViewer.refresh();
			}
		});

		Label lblKn = new Label(censusFilterComposite, SWT.NONE);
		lblKn.setText("K\u00F8n");

		txtCensusSex = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusSex.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusSexFilter.getInstance().setSearchText(txtCensusSex.getText());
				censusTableViewer.refresh();
			}
		});

		Label lblFdested = new Label(censusFilterComposite, SWT.NONE);
		lblFdested.setText("F\u00F8dested");

		txtBirthPlace = new Text(censusFilterComposite, SWT.BORDER);

		Button btnRydFelterneCensus = new Button(censusFilterComposite, SWT.NONE);
		btnRydFelterneCensus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CensusBirthPlaceFilter.getInstance().setSearchText("");
				CensusCountyFilter.getInstance().setSearchText("");
				CensusNameFilter.getInstance().setSearchText("");
				CensusParishFilter.getInstance().setSearchText("");
				CensusSexFilter.getInstance().setSearchText("");
				CensusYearFilter.getInstance().setSearchText("");
				txtBirthPlace.setText("");
				txtCensusCounty.setText("");
				txtCensusName.setText("");
				txtCensusParish.setText("");
				txtCensusSex.setText("");
				txtCensusYear.setText("");
				censusTableViewer.refresh();
			}
		});
		btnRydFelterneCensus.setText("Ryd felterne");
		txtBirthPlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusBirthPlaceFilter.getInstance().setSearchText(txtBirthPlace.getText());
				censusTableViewer.refresh();
			}
		});

		final ScrolledComposite censusScroller = new ScrolledComposite(censusComposite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		censusScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		censusScroller.setSize(0, 0);

		tbtmFolketlling.setControl(censusComposite);
		censusScroller.setExpandHorizontal(true);
		censusScroller.setExpandVertical(true);

		censusTableViewer = new TableViewer(censusScroller, SWT.BORDER | SWT.FULL_SELECTION);
		censusTableViewer.addDoubleClickListener(event -> {
			censusPopup();
		});

		ViewerFilter[] filters = new ViewerFilter[6];
		filters[0] = CensusBirthPlaceFilter.getInstance();
		filters[1] = CensusCountyFilter.getInstance();
		filters[2] = CensusNameFilter.getInstance();
		filters[3] = CensusParishFilter.getInstance();
		filters[4] = CensusSexFilter.getInstance();
		filters[5] = CensusYearFilter.getInstance();
		censusTableViewer.setFilters(filters);

		censusTable = censusTableViewer.getTable();
		censusTable.setLinesVisible(true);
		censusTable.setHeaderVisible(true);

		final TableViewerColumn censusTableVieverColumn = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnr = censusTableVieverColumn.getColumn();
		tblclmnr.setWidth(50);
		tblclmnr.setText("\u00C5r");
		censusTableVieverColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return Integer.toString(cr.getFTaar());
			}
		});

		final TableViewerColumn censusTableVieverColumn_1 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnAmt = censusTableVieverColumn_1.getColumn();
		tblclmnAmt.setWidth(75);
		tblclmnAmt.setText("Amt");
		censusTableVieverColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getAmt();
			}
		});

		final TableViewerColumn censusTableVieverColumn_2 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnHerred = censusTableVieverColumn_2.getColumn();
		tblclmnHerred.setWidth(75);
		tblclmnHerred.setText("Herred");
		censusTableVieverColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getHerred();
			}
		});

		final TableViewerColumn censusTableVieverColumn_3 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnSogn = censusTableVieverColumn_3.getColumn();
		tblclmnSogn.setWidth(75);
		tblclmnSogn.setText("Sogn");
		censusTableVieverColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getSogn();
			}
		});

		final TableViewerColumn censusTableVieverColumn_4 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnKildestednavn = censusTableVieverColumn_4.getColumn();
		tblclmnKildestednavn.setWidth(75);
		tblclmnKildestednavn.setText("Kildestednavn");
		censusTableVieverColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildestednavn();
			}
		});

		final TableViewerColumn censusTableVieverColumn_5 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnHusstfamnr = censusTableVieverColumn_5.getColumn();
		tblclmnHusstfamnr.setWidth(40);
		tblclmnHusstfamnr.setText("Husst./fam.nr.");
		censusTableVieverColumn_5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getHusstands_familienr();
			}
		});

		final TableViewerColumn censusTableVieverColumn_6 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnMatrnraddr = censusTableVieverColumn_6.getColumn();
		tblclmnMatrnraddr.setWidth(75);
		tblclmnMatrnraddr.setText("Matr.nr.addr.");
		censusTableVieverColumn_6.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getMatr_nr_Adresse();
			}
		});

		final TableViewerColumn censusTableVieverColumn_7 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnKildenavn = censusTableVieverColumn_7.getColumn();
		tblclmnKildenavn.setWidth(75);
		tblclmnKildenavn.setText("Kildenavn");
		censusTableVieverColumn_7.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildenavn();
			}
		});

		final TableViewerColumn censusTableVieverColumn_8 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnKn = censusTableVieverColumn_8.getColumn();
		tblclmnKn.setWidth(40);
		tblclmnKn.setText("K\u00F8n");
		censusTableVieverColumn_8.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKoen();
			}
		});

		final TableViewerColumn censusTableVieverColumn_9 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnAlder = censusTableVieverColumn_9.getColumn();
		tblclmnAlder.setWidth(40);
		tblclmnAlder.setText("Alder");
		censusTableVieverColumn_9.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return Integer.toString(cr.getAlder());
			}
		});

		final TableViewerColumn censusTableVieverColumn_10 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnCivilstand = censusTableVieverColumn_10.getColumn();
		tblclmnCivilstand.setWidth(75);
		tblclmnCivilstand.setText("Civilstand");
		censusTableVieverColumn_10.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getCivilstand();
			}
		});

		final TableViewerColumn censusTableVieverColumn_11 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnErhverv = censusTableVieverColumn_11.getColumn();
		tblclmnErhverv.setWidth(75);
		tblclmnErhverv.setText("Erhverv");
		censusTableVieverColumn_11.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildeerhverv();
			}
		});

		final TableViewerColumn censusTableVieverColumn_12 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnStillHusst = censusTableVieverColumn_12.getColumn();
		tblclmnStillHusst.setWidth(75);
		tblclmnStillHusst.setText("Still. husst.");
		censusTableVieverColumn_12.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getStilling_i_husstanden();
			}
		});

		final TableViewerColumn censusTableVieverColumn_13 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnFdested = censusTableVieverColumn_13.getColumn();
		tblclmnFdested.setWidth(75);
		tblclmnFdested.setText("F\u00F8dested");
		censusTableVieverColumn_13.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildefoedested();
			}
		});

		final TableViewerColumn censusTableVieverColumn_14 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnFdedato = censusTableVieverColumn_14.getColumn();
		tblclmnFdedato.setWidth(75);
		tblclmnFdedato.setText("F\u00F8dedato");
		censusTableVieverColumn_14.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getFoedt_kildedato();
			}
		});

		final TableViewerColumn censusTableVieverColumn_15 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnFder = censusTableVieverColumn_15.getColumn();
		tblclmnFder.setWidth(75);
		tblclmnFder.setText("F\u00F8de\u00E5r");
		censusTableVieverColumn_15.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return Integer.toString(cr.getFoedeaar());
			}
		});

		final TableViewerColumn censusTableVieverColumn_16 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnAdresse = censusTableVieverColumn_16.getColumn();
		tblclmnAdresse.setWidth(75);
		tblclmnAdresse.setText("Adresse");
		censusTableVieverColumn_16.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getAdresse();
			}
		});

		final TableViewerColumn censusTableVieverColumn_17 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnMatrikel = censusTableVieverColumn_17.getColumn();
		tblclmnMatrikel.setWidth(75);
		tblclmnMatrikel.setText("Matrikel");
		censusTableVieverColumn_17.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getMatrikel();
			}
		});

		final TableViewerColumn censusTableVieverColumn_18 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnGadenr = censusTableVieverColumn_18.getColumn();
		tblclmnGadenr.setWidth(40);
		tblclmnGadenr.setText("Gadenr.");
		censusTableVieverColumn_18.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getGade_nr();
			}
		});

		final TableViewerColumn censusTableVieverColumn_19 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnHenvisning = censusTableVieverColumn_19.getColumn();
		tblclmnHenvisning.setWidth(75);
		tblclmnHenvisning.setText("Henvisning");
		censusTableVieverColumn_19.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildehenvisning();
			}
		});

		final TableViewerColumn censusTableVieverColumn_20 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnKommentar = censusTableVieverColumn_20.getColumn();
		tblclmnKommentar.setWidth(75);
		tblclmnKommentar.setText("Kommentar");
		censusTableVieverColumn_20.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildekommentar();
			}
		});

		final TableViewerColumn censusTableVieverColumn_21 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnKipNr = censusTableVieverColumn_21.getColumn();
		tblclmnKipNr.setWidth(50);
		tblclmnKipNr.setText("KIP nr.");
		censusTableVieverColumn_21.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKIPnr();
			}
		});

		final TableViewerColumn censusTableVieverColumn_22 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnLbenr = censusTableVieverColumn_22.getColumn();
		tblclmnLbenr.setWidth(40);
		tblclmnLbenr.setText("L\u00F8benr.");
		censusTableVieverColumn_22.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return Integer.toString(cr.getLoebenr());
			}
		});

		final TableViewerColumn censusTableVieverColumn_23 = new TableViewerColumn(censusTableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer_1 = censusTableVieverColumn_23.getColumn();
		tblclmnDetaljer_1.setWidth(100);
		tblclmnDetaljer_1.setText("Detaljer");
		censusTableVieverColumn_23.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final CensusRecord cr = (CensusRecord) element;
				return cr.getKildedetaljer();
			}
		});

		censusTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		txtCensusYear.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusYearFilter.getInstance().setSearchText(txtCensusYear.getText());
				censusTableViewer.refresh();
			}
		});

		censusTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				CensusRecord t1 = (CensusRecord) e1;
				CensusRecord t2 = (CensusRecord) e2;
				return (t1.getFTaar() + t1.getAmt() + t1.getHerred() + t1.getSogn())
						.compareTo((t2.getFTaar() + t2.getAmt() + t2.getHerred() + t2.getSogn()));
			};
		});

		censusScroller.setContent(censusTable);
		censusScroller.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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

		final MenuItem mntmL = new MenuItem(menu_1, SWT.NONE);
		mntmL.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				gedcomLoader(e);
			}
		});
		mntmL.setText("Indl\u00E6s GEDCOM i databasen");

		final MenuItem mntmIndlsKipFiler = new MenuItem(menu_1, SWT.NONE);
		mntmIndlsKipFiler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				kipFileLoader(e);
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
	}

	/**
	 * @param tabFolder
	 */

	// TODO Filter polreg for bithr date address
	// TODO Add polreg popup with copy/paste

	private void createPolregTab(final TabFolder tabFolder) {
		final TabItem tbtmPolitietsRegisterblade = new TabItem(tabFolder, SWT.NONE);
		tbtmPolitietsRegisterblade.setText("Politiets Registerblade");

		final ScrolledComposite polregScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmPolitietsRegisterblade.setControl(polregScroller);
		polregScroller.setExpandHorizontal(true);
		polregScroller.setExpandVertical(true);

		polregTableViewer = new TableViewer(polregScroller, SWT.BORDER | SWT.FULL_SELECTION);
		polregTable = polregTableViewer.getTable();
		polregTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = PolregAddressFilter.getInstance();
		filters[1] = PolregBirthdateFilter.getInstance();
		polregTableViewer.setFilters(filters);

		polregTable.setHeaderVisible(true);
		polregTable.setLinesVisible(true);

		final TableColumn tblclmnNavn_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnNavn_1.setWidth(100);
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
		tblclmnDag.setWidth(50);
		tblclmnDag.setText("Dag");

		final TableColumn tblclmnMned = new TableColumn(polregTable, SWT.NONE);
		tblclmnMned.setWidth(50);
		tblclmnMned.setText("M\u00E5ned");

		final TableColumn tblclmnr = new TableColumn(polregTable, SWT.NONE);
		tblclmnr.setWidth(50);
		tblclmnr.setText("\u00C5r");

		final TableColumn tblclmnAdresse_1 = new TableColumn(polregTable, SWT.NONE);
		tblclmnAdresse_1.setWidth(200);
		tblclmnAdresse_1.setText("Adresse");

		polregScroller.setContent(polregTable);
		polregScroller.setMinSize(polregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Create the probate tab
	 * 
	 * @param display
	 * @param tabFolder
	 */

	// TODO Filter probates for place

	private void createProbateTab(Display display, final TabFolder tabFolder) {
		final TabItem tbtmSkifter = new TabItem(tabFolder, SWT.NONE);
		tbtmSkifter.setText("Skifter");

		final ScrolledComposite probateScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmSkifter.setControl(probateScroller);
		probateScroller.setExpandHorizontal(true);
		probateScroller.setExpandVertical(true);

		probateTableViewer = new TableViewer(probateScroller, SWT.BORDER | SWT.FULL_SELECTION);
		probateTableViewer.addDoubleClickListener(event -> probatePopup(display));
		probateTable = probateTableViewer.getTable();
		probateTable.setLinesVisible(true);
		probateTable.setHeaderVisible(true);

		probateTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = ProbatePlaceFilter.getInstance();
		probateTableViewer.setFilters(filters);

		final TableViewerColumn Column_9 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnNavn = Column_9.getColumn();
		tblclmnNavn.setWidth(100);
		tblclmnNavn.setText("Navn");

		final TableViewerColumn Column_10 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnFra_1 = Column_10.getColumn();
		tblclmnFra_1.setWidth(100);
		tblclmnFra_1.setText("Fra");

		final TableViewerColumn Column_11 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnTil_1 = Column_11.getColumn();
		tblclmnTil_1.setWidth(100);
		tblclmnTil_1.setText("Til");

		final TableViewerColumn Column_12 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnSted = Column_12.getColumn();
		tblclmnSted.setWidth(100);
		tblclmnSted.setText("Sted");

		final TableViewerColumn Column_13 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnData = Column_13.getColumn();
		tblclmnData.setWidth(300);
		tblclmnData.setText("Data");

		final TableViewerColumn Column_14 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnKilde = Column_14.getColumn();
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
		final TabItem tbtmFlytninger = new TabItem(tabFolder, SWT.NONE);
		tbtmFlytninger.setText("Flytninger");

//		Composite relocationComposite = new Composite(tabFolder, SWT.NONE);
//		tbtmFlytninger.setControl(relocationComposite);
//		relocationComposite.setLayout(new GridLayout(1, false));
//
//		Composite relocationFilterComposite = new Composite(relocationComposite, SWT.BORDER);
//		relocationFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		relocationFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
//
//		final Label cLabel = new Label(censusFilterComposite, SWT.NONE);
//		cLabel.setText("Filtre: Fornavn");
//
//		txtRelocationGiven = new Text(censusFilterComposite, SWT.BORDER);
//		txtRelocationGiven.addKeyListener(new KeyAdapter() {
//			private StructuredViewer relocationTableViewer;
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//				RelocationGivenFilter.getInstance().setSearchText(txtRelocationGiven.getText());
//				relocationTableViewer.refresh();
//			}
//		});
//
//		final Label dLabel = new Label(censusFilterComposite, SWT.NONE);
//		dLabel.setText("Efternavn");
//
//		txtRelocationSurname = new Text(censusFilterComposite, SWT.BORDER);
//		txtRelocationSurname.addKeyListener(new KeyAdapter() {
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//				RelocationSurnameFilter.getInstance().setSearchText(txtRelocationSurname.getText());
//				relocationTableViewer.refresh();
//			}
//		});
//
//		Button btnRydFelterneRelocation = new Button(relocationFilterComposite, SWT.NONE);
//		btnRydFelterneRelocation.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				// FIXME Edit
//				CensusBirthPlaceFilter.getInstance().setSearchText("");
//				CensusCountyFilter.getInstance().setSearchText("");
//				CensusNameFilter.getInstance().setSearchText("");
//				CensusParishFilter.getInstance().setSearchText("");
//				CensusSexFilter.getInstance().setSearchText("");
//				CensusYearFilter.getInstance().setSearchText("");
//				txtBirthPlace.setText("");
//				txtCensusCounty.setText("");
//				txtCensusName.setText("");
//				txtCensusParish.setText("");
//				txtCensusSex.setText("");
//				txtCensusYear.setText("");
//				censusTableViewer.refresh();
//			}
//		});
//		btnRydFelterneRelocation.setText("Ryd felterne");

		final ScrolledComposite relocationScroller = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmFlytninger.setControl(relocationScroller);
		relocationScroller.setExpandHorizontal(true);
		relocationScroller.setExpandVertical(true);

		relocationTableViewer = new TableViewer(relocationScroller, SWT.BORDER | SWT.FULL_SELECTION);
		relocationTableViewer.addDoubleClickListener(event -> relocationPopup());
		relocationTable = relocationTableViewer.getTable();
		relocationTable.setLinesVisible(true);
		relocationTable.setHeaderVisible(true);

		relocationTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = RelocationGivenFilter.getInstance();
		filters[1] = RelocationSurnameFilter.getInstance();
		relocationTableViewer.setFilters(filters);

		final TableViewerColumn relocationTableViewerColumn = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		relocationTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getId();
			}
		});
		final TableColumn tblclmnId = relocationTableViewerColumn.getColumn();
		tblclmnId.setWidth(40);
		tblclmnId.setText("ID");

		final TableViewerColumn relocationTableViewerColumn_1 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFornavn = relocationTableViewerColumn_1.getColumn();
		tblclmnFornavn.setWidth(100);
		tblclmnFornavn.setText("Fornavn");
		relocationTableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getGivenName();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_2 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn = relocationTableViewerColumn_2.getColumn();
		tblclmnEfternavn.setWidth(100);
		tblclmnEfternavn.setText("Efternavn");
		relocationTableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getSurName();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_3 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFlyttedato = relocationTableViewerColumn_3.getColumn();
		tblclmnFlyttedato.setWidth(100);
		tblclmnFlyttedato.setText("Flyttedato");
		relocationTableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getDate().toString();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_4 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnTil = relocationTableViewerColumn_4.getColumn();
		tblclmnTil.setWidth(200);
		tblclmnTil.setText("Til");
		relocationTableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getPlace();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_5 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFra = relocationTableViewerColumn_5.getColumn();
		tblclmnFra.setWidth(100);
		tblclmnFra.setText("Fra");
		relocationTableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getNote();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_6 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer = relocationTableViewerColumn_6.getColumn();
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Detaljer");
		relocationTableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getSourceDetail();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_7 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnFdselsdato = relocationTableViewerColumn_7.getColumn();
		tblclmnFdselsdato.setWidth(100);
		tblclmnFdselsdato.setText("F\u00F8dselsdato");
		relocationTableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getBirthDate().toString();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_8 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnForldre = relocationTableViewerColumn_8.getColumn();
		tblclmnForldre.setWidth(200);
		tblclmnForldre.setText("For\u00E6ldre");
		relocationTableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationRecord rr = (RelocationRecord) element;
				return rr.getParents();
			}
		});

		relocationScroller.setContent(relocationTable);
		relocationScroller.setMinSize(relocationTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
				"V\u00E6lg enten ID fra stamtr\u00E6et eller et navn, evt. med f\u00F8de\u00E5r og evt. d\u00F8ds\u00E5r");
		lblVlgEntenId.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 7, 1));

		final Label lblId = new Label(searchComposite, SWT.NONE);
		lblId.setBounds(0, 0, 55, 15);
		lblId.setText("ID");

		searchId = new Text(searchComposite, SWT.BORDER);
		searchId.setBounds(0, 0, 56, 21);

		final Button btnSearchId = new Button(searchComposite, SWT.NONE);
		btnSearchId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchById(e);

			}
		});
		btnSearchId.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSearchId.setText("S\u00F8g p\u00E5 ID");
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);
		new Label(searchComposite, SWT.NONE);

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
		lblFader.setText("Fader (Valgfri)");

		searchFather = new Text(searchComposite, SWT.BORDER);
		searchFather.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		final Label lblModer = new Label(searchComposite, SWT.NONE);
		lblModer.setText("Moder (Valgfri)");

		searchMother = new Text(searchComposite, SWT.BORDER);
		searchMother.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnSearchName = new Button(searchComposite, SWT.NONE);
		btnSearchName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchByName(e);
			}

		});
		btnSearchName.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnSearchName.setText("S\u00F8g p\u00E5 navn");

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
			}
		});
		btnRydFelterne.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		btnRydFelterne.setText("Ryd felterne");

		final Composite composite = new Composite(searchComposite, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 6, 1));
	}

	/**
	 * Create the siblings tab
	 * 
	 * @param tabFolder
	 */
	// TODO Siblings not correctly populated
	private void createSiblingsTab(final TabFolder tabFolder) {
		final TabItem tbtmForldre = new TabItem(tabFolder, SWT.NONE);
		tbtmForldre.setText("S\u00F8skende");

		final ScrolledComposite scrolledComposite_3 = new ScrolledComposite(tabFolder,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmForldre.setControl(scrolledComposite_3);
		scrolledComposite_3.setExpandHorizontal(true);
		scrolledComposite_3.setExpandVertical(true);

		siblingsTableViewer = new TableViewer(scrolledComposite_3, SWT.BORDER | SWT.FULL_SELECTION);
		siblingsTable = siblingsTableViewer.getTable();

		siblingsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		siblingsTable.setHeaderVisible(true);
		siblingsTable.setLinesVisible(true);

		final TableColumn tblclmnId1 = new TableColumn(siblingsTable, SWT.NONE);
		tblclmnId1.setWidth(75);
		tblclmnId1.setText("ID");

		final TableColumn tblclmnFdselsdato1 = new TableColumn(siblingsTable, SWT.NONE);
		tblclmnFdselsdato1.setWidth(100);
		tblclmnFdselsdato1.setText("F\u00F8dselsdato");

		final TableColumn tblclmnNavn_2 = new TableColumn(siblingsTable, SWT.NONE);
		tblclmnNavn_2.setWidth(100);
		tblclmnNavn_2.setText("Navn");

		final TableColumn tblclmnForldre1 = new TableColumn(siblingsTable, SWT.NONE);
		tblclmnForldre1.setWidth(200);
		tblclmnForldre1.setText("For\u00E6ldre");

		final TableColumn tblclmnSted_2 = new TableColumn(siblingsTable, SWT.NONE);
		tblclmnSted_2.setWidth(600);
		tblclmnSted_2.setText("Sted");

		scrolledComposite_3.setContent(siblingsTable);
		scrolledComposite_3.setMinSize(siblingsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
	 * @param property
	 * @param text
	 * @param text2
	 * @return
	 * @throws SQLException
	 */
	protected String getCensusHousehold(String kipNr, String nr) throws SQLException {
		final StringBuffer sb = new StringBuffer();
		String string;

		household = CensusHousehold.loadFromDatabase(props.getProperty("vejbyPath"), kipNr, nr);

		for (final CensusRecord hhr : household) {
			sb.append(hhr.getKildenavn() + "," + hhr.getAlder() + ", " + hhr.getCivilstand() + ", "
					+ hhr.getKildeerhverv() + ", " + hhr.getStilling_i_husstanden() + "\n");
		}
		string = sb.toString();

		if (string.length() > 4096) {
			string = string.substring(0, 4095);
		}

		return string;
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
	 * Populate the burial registry tab from the database
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateBurregTable(String phonName, String birthDate, String deathDate) throws SQLException {
		if (!btnBegravelsesregistret.getSelection()) {
			return;
		}

		setMessage("Begravelsesregister hentes");

		BurregRecord[] lbr = BurregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName, birthDate,
				deathDate);
		burregTableViewer.setInput(lbr);
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
		if (!btnCensusTab.getSelection()) {
			return;
		}

		setMessage("Folketællinger hentes");

		CensusRecord[] censuses = CensusRecord.loadFromDatabase(props.getProperty("vejbyPath"), phonName,
				birthDate.substring(0, 4), deathDate.substring(0, 4));
		censusTableViewer.setInput(censuses);
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
		polregTable.removeAll();

		if (!btnPolitietsRegisterblade.getSelection()) {
			return;
		}

		setMessage("Politiregister hentes");

		PolregRecord[] lpr = PolregRecord.loadFromDatabase(props.getProperty("cphDbPath"), phonName, birthDate,
				deathDate);
		polregTableViewer.setInput(lpr);
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
		if (!btnProbateTab.getSelection()) {
			return;
		}

		setMessage("Skifter hentes");

		ProbateRecord[] lp = ProbateRecord.loadFromDatabase(props.getProperty("probatePath"), phonName, birthDate,
				deathDate, props.getProperty("probateSource"));
		probateTableViewer.setInput(lp);
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
		if (!btnRelocationTab.getSelection()) {
			return;
		}

		setMessage("Flytninger hentes");

//		Collections.sort(relocationRecords, new RelocationComparator());

		RelocationRecord[] relocationRecords = RelocationRecord.loadFromDatabase(props.getProperty("vejbyPath"),
				phonName, birthDate, deathDate);
		relocationTableViewer.setInput(relocationRecords);
	}

	/**
	 * Populate siblings table by individual parent field
	 *
	 * @param parents
	 * @throws SQLException
	 */
	private void populateSiblingsTable(String parents) throws SQLException {
		if (!btnSiblings.getSelection()) {
			return;
		}

		setMessage("Søskende hentes");

		final SiblingRecord[] lpr = SiblingRecord.loadFromDatabase(props.getProperty("vejbyPath"), parents);
		siblingsTableViewer.setInput(lpr);
	}

	/**
	 * Populate siblings table by father and mother
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	private void populateSiblingsTable(String fathersName, String mothersName) throws SQLException {
		siblingsTable.removeAll();

		if (!btnSiblings.getSelection()) {
			return;
		}

		setMessage("Søskende hentes");

//		Collections.sort(lpr, new SiblingComparator());

		final SiblingRecord[] lpr = SiblingRecord.loadFromDatabase(props.getProperty("vejbyPath"), fathersName,
				mothersName);
		siblingsTableViewer.setInput(lpr);
	}

	/**
	 * @param display
	 */
	private void probatePopup(Display display) {
		final TableItem[] tia = probateTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 5; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}

		sb.append("\n");

		final String string = sb.toString().replace("¤", "\n");

		final MessageDialog dialog = new MessageDialog(shell, "Skifter", null, string, MessageDialog.INFORMATION,
				new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	/**
	 *
	 */
	private void relocationPopup() {
		final TableItem[] tia = relocationTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 9; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}
		sb.append("\n");

		final MessageDialog dialog = new MessageDialog(shell, "Flytning", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { tia[0].getText(5) }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	/**
	 * Search by ID
	 *
	 * @param e
	 */
	private void searchById(SelectionEvent e) {
		searchBirth.setText("");
		searchDeath.setText("");
		searchFather.setText("");
		searchMother.setText("");
		searchName.setText("");

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
			populateSiblingsTable(individual.getParents());
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
			populateSiblingsTable(searchFather.getText(), searchMother.getText());
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