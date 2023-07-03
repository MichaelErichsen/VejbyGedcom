package net.myerichsen.archivesearcher.views;

import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.archivesearcher.comparators.BurregComparator;
import net.myerichsen.archivesearcher.filters.BurregBirthDateFilter;
import net.myerichsen.archivesearcher.filters.BurregGivenFilter;
import net.myerichsen.archivesearcher.filters.BurregSurnameFilter;
import net.myerichsen.archivesearcher.models.BurregModel;
import net.myerichsen.archivesearcher.populators.ASPopulator;
import net.myerichsen.archivesearcher.populators.BurregPopulator;

/**
 * Burial registry view
 *
 * @author Michael Erichsen
 * @version 1. jul. 2023
 *
 */
public class BurregView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private ASPopulator listener;
	private Properties props;
	private Text txtBurregSurname;
	private Text txtBurregBirthYear;
	private Text txtBurregGiven;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public BurregView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listener = new BurregPopulator();

		final Composite burregFilterComposite = new Composite(this, SWT.BORDER);
		burregFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		burregFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label bLabel = new Label(burregFilterComposite, SWT.NONE);
		bLabel.setText("Filtre: Fornavn");

		txtBurregGiven = new Text(burregFilterComposite, SWT.BORDER);

		txtBurregGiven.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtBurregGiven.getText().length() > 0) {
					txtBurregGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtBurregGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				BurregGivenFilter.getInstance().setSearchText(txtBurregGiven.getText());
				tableViewer.refresh();
			}
		});

		final Label lblEfternavn = new Label(burregFilterComposite, SWT.NONE);
		lblEfternavn.setText("Efternavn");

		txtBurregSurname = new Text(burregFilterComposite, SWT.BORDER);
		txtBurregSurname.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtBurregSurname.getText().length() > 0) {
					txtBurregSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtBurregSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				BurregSurnameFilter.getInstance().setSearchText(txtBurregSurname.getText());
				tableViewer.refresh();
			}
		});

		final Label lblFder = new Label(burregFilterComposite, SWT.NONE);
		lblFder.setText("F\u00F8de\u00E5r");

		txtBurregBirthYear = new Text(burregFilterComposite, SWT.BORDER);
		txtBurregBirthYear.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtBurregBirthYear.getText().length() > 0) {
					txtBurregBirthYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtBurregBirthYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				BurregBirthDateFilter.getInstance().setSearchText(txtBurregBirthYear.getText());
				tableViewer.refresh();
			}
		});

		final Button btnRydFelterne_2 = new Button(burregFilterComposite, SWT.NONE);
		btnRydFelterne_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterne_2.setText("Ryd felterne");

		final ScrolledComposite burregScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		burregScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		burregScroller.setSize(0, 0);
		burregScroller.setExpandHorizontal(true);
		burregScroller.setExpandVertical(true);

		tableViewer = new TableViewer(burregScroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup(getDisplay()));
		table = tableViewer.getTable();
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		final ViewerFilter[] filters = new ViewerFilter[3];
		filters[0] = BurregBirthDateFilter.getInstance();
		filters[1] = BurregGivenFilter.getInstance();
		filters[2] = BurregSurnameFilter.getInstance();
		tableViewer.setFilters(filters);
		tableViewer.setComparator(new BurregComparator());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFornavne = tableViewerColumn.getColumn();
		tblclmnFornavne.setWidth(100);
		tblclmnFornavne.setText("Fornavne");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getFirstNames();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn_1 = tableViewerColumn_1.getColumn();
		tblclmnEfternavn_1.setWidth(100);
		tblclmnEfternavn_1.setText("Efternavn");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getLastName();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDdsdato = tableViewerColumn_2.getColumn();
		tblclmnDdsdato.setWidth(100);
		tblclmnDdsdato.setText("D\u00F8dsdato");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getDateOfDeath();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFder_1 = tableViewerColumn_3.getColumn();
		tblclmnFder_1.setWidth(100);
		tblclmnFder_1.setText("F\u00F8de\u00E5r");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getYearOfBirth();
			}
		});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDdssted = tableViewerColumn_4.getColumn();
		tblclmnDdssted.setWidth(100);
		tblclmnDdssted.setText("D\u00F8dssted");
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getDeathPlace();
			}
		});

		final TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnCivilstand_1 = tableViewerColumn_5.getColumn();
		tblclmnCivilstand_1.setWidth(100);
		tblclmnCivilstand_1.setText("Civilstand");
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getCivilStatus();
			}
		});

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAdrUdfKbhvn = tableViewerColumn_6.getColumn();
		tblclmnAdrUdfKbhvn.setWidth(100);
		tblclmnAdrUdfKbhvn.setText("Adr. udf. Kbhvn.");
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getAdressOutsideCph();
			}
		});

		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKn_1 = tableViewerColumn_7.getColumn();
		tblclmnKn_1.setWidth(100);
		tblclmnKn_1.setText("K\u00F8n");
		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getSex();
			}
		});

		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKommentar_1 = tableViewerColumn_8.getColumn();
		tblclmnKommentar_1.setWidth(100);
		tblclmnKommentar_1.setText("Kommentar");
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getComment();
			}
		});

		final TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKirkegrd = tableViewerColumn_9.getColumn();
		tblclmnKirkegrd.setWidth(100);
		tblclmnKirkegrd.setText("Kirkeg\u00E5rd");
		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getCemetary();
			}
		});

		final TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKapel = tableViewerColumn_10.getColumn();
		tblclmnKapel.setWidth(100);
		tblclmnKapel.setText("Kapel");
		tableViewerColumn_10.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getChapel();
			}
		});

		final TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnGade_1 = tableViewerColumn_11.getColumn();
		tblclmnGade_1.setWidth(100);
		tblclmnGade_1.setText("Gade");
		tableViewerColumn_11.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getStreet();
			}
		});

		final TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKvarter = tableViewerColumn_12.getColumn();
		tblclmnKvarter.setWidth(100);
		tblclmnKvarter.setText("Kvarter");
		tableViewerColumn_12.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getHood();
			}
		});

		final TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnGadenr_1 = tableViewerColumn_13.getColumn();
		tblclmnGadenr_1.setWidth(40);
		tblclmnGadenr_1.setText("Gadenr.");
		tableViewerColumn_13.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getStreetNumber();
			}
		});

		final TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnBogstav_1 = tableViewerColumn_14.getColumn();
		tblclmnBogstav_1.setWidth(40);
		tblclmnBogstav_1.setText("Bogstav");
		tableViewerColumn_14.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getLetter();
			}
		});

		final TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnEtage_1 = tableViewerColumn_15.getColumn();
		tblclmnEtage_1.setWidth(40);
		tblclmnEtage_1.setText("Etage");
		tableViewerColumn_15.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getFloor();
			}
		});

		final TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnInstitution = tableViewerColumn_16.getColumn();
		tblclmnInstitution.setWidth(100);
		tblclmnInstitution.setText("Institution");
		tableViewerColumn_16.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getInstitution();
			}
		});

		final TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnInstGade = tableViewerColumn_17.getColumn();
		tblclmnInstGade.setWidth(100);
		tblclmnInstGade.setText("Inst. gade");
		tableViewerColumn_17.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getInstitutionStreet();
			}
		});

		final TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnInstGadenr = tableViewerColumn_19.getColumn();
		tblclmnInstGadenr.setWidth(40);
		tblclmnInstGadenr.setText("Inst. gadenr.");
		tableViewerColumn_19.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getInstitutionStreet();
			}
		});

		final TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnInstKvarter = tableViewerColumn_18.getColumn();
		tblclmnInstKvarter.setWidth(100);
		tblclmnInstKvarter.setText("Inst. kvarter");
		tableViewerColumn_18.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getInstitutionHood();
			}
		});

		final TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnErhverv_2 = tableViewerColumn_20.getColumn();
		tblclmnErhverv_2.setWidth(40);
		tblclmnErhverv_2.setText("Erhverv");
		tableViewerColumn_20.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getOccuptations();
			}
		});

		final TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnErhvforhtyper = tableViewerColumn_21.getColumn();
		tblclmnErhvforhtyper.setWidth(100);
		tblclmnErhvforhtyper.setText("Erhv.forh.typer");
		tableViewerColumn_21.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getOccupationRelationTypes();
			}
		});

		final TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDdsrsager = tableViewerColumn_22.getColumn();
		tblclmnDdsrsager.setWidth(100);
		tblclmnDdsrsager.setText("D\u00F8ds\u00E5rsager");
		tableViewerColumn_22.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getDeathCauses();
			}
		});

		final TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDdsrsDansk = tableViewerColumn_23.getColumn();
		tblclmnDdsrsDansk.setWidth(100);
		tblclmnDdsrsDansk.setText("D\u00F8ds\u00E5rs. dansk");
		tableViewerColumn_23.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((BurregModel) element).getDeathCausesDanish();
			}
		});

		burregScroller.setContent(table);
		burregScroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Clear the table
	 */
	public void clear() {
		if (thread != null) {
			thread.interrupt();
		}
		tableViewer.setInput(new BurregModel[0]);
		clearFilters();
	}

	/**
	 *
	 */
	private void clearFilters() {
		txtBurregGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtBurregGiven.setText("");
		BurregGivenFilter.getInstance().setSearchText("");
		txtBurregSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtBurregSurname.setText("");
		BurregSurnameFilter.getInstance().setSearchText("");
		txtBurregBirthYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtBurregBirthYear.setText("");
		BurregBirthDateFilter.getInstance().setSearchText("");
		tableViewer.refresh();
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	private void getInput(String phonName, String birthDate, String deathDate) {
		if (listener != null) {
			try {
				final String[] loadArgs = new String[] { props.getProperty("cphSchema"), props.getProperty("cphDbPath"),
						phonName, birthDate, deathDate };
				final BurregModel[] burregRecords = (BurregModel[]) listener.load(loadArgs);

				Display.getDefault().asyncExec(() -> tableViewer.setInput(burregRecords));
				Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
						.setMessage("Begravelsesregisteret er hentet"));
			} catch (final Exception e) {
				Display.getDefault().asyncExec(
						() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e.getMessage()));
			}
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
	public void populate(String phonName, String birthDate, String deathDate) throws SQLException {
		thread = new Thread(() -> getInput(phonName, birthDate, deathDate));
		thread.start();
	}

	/**
	 * @param display
	 */
	private void popup(Display display) {
		final TableItem[] tia = table.getSelection();
		final String string = ((BurregModel) tia[0].getData()).toString() + "\n";

		final MessageDialog dialog = new MessageDialog(getShell(), "Begravelser", null, string,
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
