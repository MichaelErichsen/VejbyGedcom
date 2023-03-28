package net.myerichsen.gedcom.gui;

import org.eclipse.swt.SWT;
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

/**
 * @author Michael Erichsen
 * @version 28. mar. 2023
 *
 */
public class ArchiveSearcher extends Shell {
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

	private Table relocationTable;
	private Text VejbyPath;
	private Text probatePath;
	private Text cphPath;
	private Text outputDirectory;
	private Text searchId;
	private Text searchName;
	private Text searchBirth;
	private Text searchDeath;

	/**
	 * Create the shell.
	 *
	 * @param display
	 */
	public ArchiveSearcher(Display display) {
		super(display, SWT.SHELL_TRIM);
		setLayout(new GridLayout(1, false));

		final Menu menuBar = new Menu(this, SWT.BAR);
		setMenuBar(menuBar);

		MenuItem mntmFiler = new MenuItem(menuBar, SWT.CASCADE);
		mntmFiler.setText("Filer");

		Menu menuFiler = new Menu(mntmFiler);
		mntmFiler.setMenu(menuFiler);

		MenuItem mntmAfslut = new MenuItem(menuFiler, SWT.NONE);
		mntmAfslut.setText("Afslut");

		createTabFolder();
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
		setSize(1101, 419);

		TableItem tableItem = new TableItem(relocationTable, SWT.NONE);
		tableItem.setText("New TableItem");

		String[] sa = new String[] { "1383", "Anders", "Pedersen", "1841-04-13", "Mårum, Holbo, Frederiksborg",
				"fra Vejby", "", "1808-01-01", "" };

		tableItem.setText(sa);
	}

	/**
	 * Create relocation tab
	 */
	private void createTabFolder() {

		Composite SearchComposite = new Composite(this, SWT.NONE);
		SearchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		SearchComposite.setLayout(new GridLayout(9, false));

		Label lblId = new Label(SearchComposite, SWT.NONE);
		lblId.setBounds(0, 0, 55, 15);
		lblId.setText("ID");

		searchId = new Text(SearchComposite, SWT.BORDER);
		searchId.setBounds(0, 0, 56, 21);

		Label lblNewLabel_1 = new Label(SearchComposite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Alt: Navn");

		searchName = new Text(SearchComposite, SWT.BORDER);
		searchName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblAltFdt = new Label(SearchComposite, SWT.NONE);
		lblAltFdt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltFdt.setText("Alt: F\u00F8de\u00E5r");

		searchBirth = new Text(SearchComposite, SWT.BORDER);
		searchBirth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblAltDdsr = new Label(SearchComposite, SWT.NONE);
		lblAltDdsr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltDdsr.setText("Alt: D\u00F8ds\u00E5r");

		searchDeath = new Text(SearchComposite, SWT.BORDER);
		searchDeath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(SearchComposite, SWT.NONE);
		final TabFolder searcherTabFolder = new TabFolder(this, SWT.NONE);

		final TabItem tbtmRelocation = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmRelocation.setText("Flytninger");

		relocationTable = new Table(searcherTabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmRelocation.setControl(relocationTable);
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

		TabItem tbtmCensus = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmCensus.setText("Folket\u00E6llinger");

		TabItem tbtmProbate = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmProbate.setText("Skifter");

		TabItem tbtmCphPolreg = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmCphPolreg.setText("Kbhvn Politi");

		TabItem tbtmCphBurreg = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmCphBurreg.setText("Kbhvn begravelser");

		TabItem tbtmOpstning = new TabItem(searcherTabFolder, SWT.NONE);
		tbtmOpstning.setText("Ops\u00E6tning");

		Composite composite = new Composite(searcherTabFolder, SWT.NONE);
		tbtmOpstning.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label lblVejbyDatabaseSti = new Label(composite, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		VejbyPath = new Text(composite, SWT.BORDER);
		VejbyPath.setText("c:\\Users\\michael\\VEJBYDB");
		VejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblSkifteDatabaseSti = new Label(composite, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		probatePath = new Text(composite, SWT.BORDER);
		probatePath.setText("C:\\DerbyDB\\gedcom");
		probatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		cphPath = new Text(composite, SWT.BORDER);
		cphPath.setText("C:\\Users\\michael\\CPHDB");
		cphPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblUddatasti = new Label(composite, SWT.NONE);
		lblUddatasti.setText("Uddatasti");

		outputDirectory = new Text(composite, SWT.BORDER);
		outputDirectory.setText("C:\\Users\\michael\\Documents\\Vejby\\VejbyGedcom");
		outputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite settingsButtonComposite = new Composite(composite, SWT.NONE);
		settingsButtonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		settingsButtonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));

		Button settingsUpdateButton = new Button(settingsButtonComposite, SWT.NONE);
		settingsUpdateButton.setText("Opdater");

		Button settingsCancelButton = new Button(settingsButtonComposite, SWT.NONE);
		settingsCancelButton.setText("Fortryd");

	}
}
