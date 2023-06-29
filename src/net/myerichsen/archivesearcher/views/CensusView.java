package net.myerichsen.archivesearcher.views;

import java.sql.SQLException;
import java.util.List;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.archivesearcher.comparators.CensusComparator;
import net.myerichsen.archivesearcher.filters.CensusAgeFilter;
import net.myerichsen.archivesearcher.filters.CensusBirthDateFilter;
import net.myerichsen.archivesearcher.filters.CensusBirthPlaceFilter;
import net.myerichsen.archivesearcher.filters.CensusCountyFilter;
import net.myerichsen.archivesearcher.filters.CensusNameFilter;
import net.myerichsen.archivesearcher.filters.CensusParishFilter;
import net.myerichsen.archivesearcher.filters.CensusYearFilter;
import net.myerichsen.archivesearcher.models.CensusHouseholdModel;
import net.myerichsen.archivesearcher.models.CensusModel;
import net.myerichsen.archivesearcher.populators.ASPopulator;
import net.myerichsen.archivesearcher.populators.CensusPopulator;

/**
 * Census view
 *
 * @author Michael Erichsen
 * @version 28. jun. 2023
 */

public class CensusView extends Composite {
	private Text txtCensusYear;
	private Text txtCensusCounty;
	private Text txtCensusParish;
	private Text txtCensusName;
	private Text txtCensusAge;
	private Text txtCensusBirthPlace;
	private Text txtCensusBirthDate;
	private TableViewer tableViewer;
	private Table table;
	private ASPopulator listener;
	private List<CensusModel> household;
	private Properties props;
	private Thread thread;
	private boolean spouseFilterFlag;
	private CensusModel[] array;
	private String id;
	private String phonName;
	private String birthDate;
	private String deathDate;
	private Button btngtefller;

	/**
	 * Create the view
	 *
	 * @param parent
	 * @param style
	 */
	public CensusView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listener = new CensusPopulator();

		final Composite filterComposite = new Composite(this, SWT.BORDER);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		filterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(filterComposite, SWT.NONE);
		aLabel.setText("Filtre: \u00C5r");

		txtCensusYear = new Text(filterComposite, SWT.BORDER);
		txtCensusYear.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusYear.getText().length() > 0) {
					txtCensusYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusYearFilter.getInstance().setSearchText(txtCensusYear.getText());
				tableViewer.refresh();
			}
		});

		final Label lblAmt = new Label(filterComposite, SWT.NONE);
		lblAmt.setText("Amt");

		txtCensusCounty = new Text(filterComposite, SWT.BORDER);
		txtCensusCounty.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusCounty.getText().length() > 0) {
					txtCensusCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusCountyFilter.getInstance().setSearchText(txtCensusCounty.getText());
				tableViewer.refresh();
			}
		});

		final Label lblSogn = new Label(filterComposite, SWT.NONE);
		lblSogn.setText("Sogn");

		txtCensusParish = new Text(filterComposite, SWT.BORDER);
		txtCensusParish.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusParish.getText().length() > 0) {
					txtCensusParish.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusParish.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusParishFilter.getInstance().setSearchText(txtCensusParish.getText());
				tableViewer.refresh();
			}
		});

		final Label lblNavn = new Label(filterComposite, SWT.NONE);
		lblNavn.setText("Navn");

		txtCensusName = new Text(filterComposite, SWT.BORDER);
		txtCensusName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusName.getText().length() > 0) {
					txtCensusName.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusNameFilter.getInstance().setSearchText(txtCensusName.getText());
				tableViewer.refresh();
			}
		});

		final Label lblAlder = new Label(filterComposite, SWT.NONE);
		lblAlder.setText("Alder +/- 2 \u00E5r");

		txtCensusAge = new Text(filterComposite, SWT.BORDER);
		txtCensusAge.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusAge.getText().length() > 0) {
					txtCensusAge.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusAge.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusAgeFilter.getInstance().setSearchText(txtCensusAge.getText());
				tableViewer.refresh();
			}
		});

		final Label lblFdested = new Label(filterComposite, SWT.NONE);
		lblFdested.setText("F\u00F8dested");

		txtCensusBirthPlace = new Text(filterComposite, SWT.BORDER);
		txtCensusBirthPlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusBirthPlace.getText().length() > 0) {
					txtCensusBirthPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusBirthPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusBirthPlaceFilter.getInstance().setSearchText(txtCensusBirthPlace.getText());
				tableViewer.refresh();
			}
		});

		final Label lblFdedato = new Label(filterComposite, SWT.NONE);
		lblFdedato.setText("F\u00F8dedato");

		txtCensusBirthDate = new Text(filterComposite, SWT.BORDER);
		txtCensusBirthDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtCensusBirthDate.getText().length() > 0) {
					txtCensusBirthDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtCensusBirthDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				CensusBirthDateFilter.getInstance().setSearchText(txtCensusBirthDate.getText());
				tableViewer.refresh();
			}
		});

		btngtefller = new Button(filterComposite, SWT.CHECK);
		btngtefller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				spouseFilterFlag = selection;

				btngtefller.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				try {
					populate(id, phonName, birthDate, deathDate);
				} catch (final SQLException e1) {
					((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e1.getMessage());
				}
			}
		});
		btngtefller.setText("\u00C6gtef\u00E6ller");

		final Button btnRydFelterneCensus = new Button(filterComposite, SWT.NONE);
		btnRydFelterneCensus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneCensus.setText("Ryd felterne");

		final ScrolledComposite scroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scroller.setSize(0, 0);
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);

		tableViewer = new TableViewer(scroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> displayPopup());

		final ViewerFilter[] filters = new ViewerFilter[7];
		filters[0] = CensusBirthPlaceFilter.getInstance();
		filters[1] = CensusCountyFilter.getInstance();
		filters[2] = CensusNameFilter.getInstance();
		filters[3] = CensusParishFilter.getInstance();
		filters[4] = CensusYearFilter.getInstance();
		filters[5] = CensusAgeFilter.getInstance();
		filters[6] = CensusBirthDateFilter.getInstance();
		tableViewer.setFilters(filters);
		tableViewer.setComparator(new CensusComparator());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnr = tableViewerColumn.getColumn();
		tblclmnr.setWidth(50);
		tblclmnr.setText("\u00C5r");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return Integer.toString(((CensusModel) element).getFTaar());
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAmt = tableViewerColumn_1.getColumn();
		tblclmnAmt.setWidth(75);
		tblclmnAmt.setText("Amt");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getAmt();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnHerred = tableViewerColumn_2.getColumn();
		tblclmnHerred.setWidth(75);
		tblclmnHerred.setText("Herred");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getHerred();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSogn = tableViewerColumn_3.getColumn();
		tblclmnSogn.setWidth(75);
		tblclmnSogn.setText("Sogn");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getSogn();
			}
		});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKildestednavn = tableViewerColumn_4.getColumn();
		tblclmnKildestednavn.setWidth(75);
		tblclmnKildestednavn.setText("Kildestednavn");
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildestednavn();
			}
		});

		final TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnHusstfamnr = tableViewerColumn_5.getColumn();
		tblclmnHusstfamnr.setWidth(40);
		tblclmnHusstfamnr.setText("Husst./fam.nr.");
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getHusstands_familienr();
			}
		});

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnMatrnraddr = tableViewerColumn_6.getColumn();
		tblclmnMatrnraddr.setWidth(75);
		tblclmnMatrnraddr.setText("Matr.nr.addr.");
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getMatr_nr_Adresse();
			}
		});

		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKildenavn = tableViewerColumn_7.getColumn();
		tblclmnKildenavn.setWidth(75);
		tblclmnKildenavn.setText("Kildenavn");
		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildenavn();
			}
		});

		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKn = tableViewerColumn_8.getColumn();
		tblclmnKn.setWidth(40);
		tblclmnKn.setText("K\u00F8n");
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKoen();
			}
		});

		final TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAlder = tableViewerColumn_9.getColumn();
		tblclmnAlder.setWidth(40);
		tblclmnAlder.setText("Alder");
		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((CensusModel) element).getAlder());
			}
		});

		final TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnCivilstand = tableViewerColumn_10.getColumn();
		tblclmnCivilstand.setWidth(75);
		tblclmnCivilstand.setText("Civilstand");
		tableViewerColumn_10.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getCivilstand();
			}
		});

		final TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnErhverv = tableViewerColumn_11.getColumn();
		tblclmnErhverv.setWidth(75);
		tblclmnErhverv.setText("Erhverv");
		tableViewerColumn_11.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildeerhverv();
			}
		});

		final TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnStillHusst = tableViewerColumn_12.getColumn();
		tblclmnStillHusst.setWidth(75);
		tblclmnStillHusst.setText("Still. husst.");
		tableViewerColumn_12.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getStilling_i_husstanden();
			}
		});

		final TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdested = tableViewerColumn_13.getColumn();
		tblclmnFdested.setWidth(75);
		tblclmnFdested.setText("F\u00F8dested");
		tableViewerColumn_13.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildefoedested();
			}
		});

		final TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdedato = tableViewerColumn_14.getColumn();
		tblclmnFdedato.setWidth(75);
		tblclmnFdedato.setText("F\u00F8dedato");
		tableViewerColumn_14.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getFoedt_kildedato();
			}
		});

		final TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFder = tableViewerColumn_15.getColumn();
		tblclmnFder.setWidth(75);
		tblclmnFder.setText("F\u00F8de\u00E5r");
		tableViewerColumn_15.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((CensusModel) element).getFoedeaar());
			}
		});

		final TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAdresse = tableViewerColumn_16.getColumn();
		tblclmnAdresse.setWidth(75);
		tblclmnAdresse.setText("Adresse");
		tableViewerColumn_16.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getAdresse();
			}
		});

		final TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnMatrikel = tableViewerColumn_17.getColumn();
		tblclmnMatrikel.setWidth(75);
		tblclmnMatrikel.setText("Matrikel");
		tableViewerColumn_17.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getMatrikel();
			}
		});

		final TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnGadenr = tableViewerColumn_18.getColumn();
		tblclmnGadenr.setWidth(40);
		tblclmnGadenr.setText("Gadenr.");
		tableViewerColumn_18.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getGade_nr();
			}
		});

		final TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnHenvisning = tableViewerColumn_19.getColumn();
		tblclmnHenvisning.setWidth(75);
		tblclmnHenvisning.setText("Henvisning");
		tableViewerColumn_19.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildehenvisning();
			}
		});

		final TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKommentar = tableViewerColumn_20.getColumn();
		tblclmnKommentar.setWidth(75);
		tblclmnKommentar.setText("Kommentar");
		tableViewerColumn_20.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildekommentar();
			}
		});

		final TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKipNr = tableViewerColumn_21.getColumn();
		tblclmnKipNr.setWidth(50);
		tblclmnKipNr.setText("KIP nr.");
		tableViewerColumn_21.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKIPnr();
			}
		});

		final TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLbenr = tableViewerColumn_22.getColumn();
		tblclmnLbenr.setWidth(40);
		tblclmnLbenr.setText("L\u00F8benr.");
		tableViewerColumn_22.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((CensusModel) element).getLoebenr());
			}
		});

		final TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer_1 = tableViewerColumn_23.getColumn();
		tblclmnDetaljer_1.setWidth(100);
		tblclmnDetaljer_1.setText("Detaljer");
		tableViewerColumn_23.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CensusModel) element).getKildedetaljer();
			}
		});

		scroller.setContent(table);
		scroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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

		final CensusModel[] input = new CensusModel[0];
		tableViewer.setInput(input);
		clearFilters();
	}

	/**
	 * Clear filters
	 */
	private void clearFilters() {
		CensusBirthPlaceFilter.getInstance().setSearchText("");
		CensusBirthDateFilter.getInstance().setSearchText("");
		CensusCountyFilter.getInstance().setSearchText("");
		CensusNameFilter.getInstance().setSearchText("");
		CensusParishFilter.getInstance().setSearchText("");
		CensusAgeFilter.getInstance().setSearchText("");
		CensusYearFilter.getInstance().setSearchText("");
		txtCensusBirthPlace.setText("");
		txtCensusBirthDate.setText("");
		txtCensusCounty.setText("");
		txtCensusName.setText("");
		txtCensusParish.setText("");
		txtCensusAge.setText("");
		txtCensusYear.setText("");
		txtCensusAge.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusBirthPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusBirthDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusParish.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtCensusYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		btngtefller.setSelection(false);
		spouseFilterFlag = false;
		btngtefller.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		try {
			populate(id, phonName, birthDate, deathDate);
		} catch (final SQLException e1) {
			((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e1.getMessage());
		}

		tableViewer.refresh();
	}

	/**
	 * Display the popup
	 */
	private void displayPopup() {
		try {
			popup();
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Get the data
	 *
	 * @param dbPath
	 * @param schema
	 * @param id
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	private void getCensus(String id, String phonName, String birthDate, String deathDate) {
		if (listener != null) {
			try {
				final String[] loadArgs = new String[] { props.getProperty("censusSchema"),
						props.getProperty("censusPath"), phonName, birthDate.substring(0, 4),
						deathDate.substring(0, 4) };

				array = (CensusModel[]) listener.load(loadArgs);

				if (spouseFilterFlag && !id.isBlank()) {
					array = CensusModel.filterForSpouses(props.getProperty("censusPath"),
							props.getProperty("censusSchema"), id, array);
				}

				Display.getDefault().asyncExec(() -> tableViewer.setInput(array));

				Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
						.setMessage("Folketællinger er hentet"));
			} catch (final Exception e) {
				e.printStackTrace();
				Display.getDefault().asyncExec(
						() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e.getMessage()));
			}
		}
	}

	/**
	 * Get the census household
	 *
	 * @param kipNr
	 * @param kildested
	 * @param nr
	 * @param matr
	 * @return
	 * @throws SQLException
	 */
	protected String getCensusHousehold(String kipNr, String kildested, String nr, String matr, String kildeHenvisning)
			throws SQLException {
		final StringBuilder sb = new StringBuilder();
		String string;

		household = CensusHouseholdModel.load(props.getProperty("censusPath"), kipNr, kildested, nr, matr,
				kildeHenvisning, props.getProperty("censusSchema"));

		for (final CensusModel hhr : household) {
			sb.append(hhr.getKildenavn() + "," + hhr.getAlder() + ", " + hhr.getCivilstand() + ", "
					+ hhr.getKildeerhverv() + ", " + hhr.getStilling_i_husstanden() + ", " + hhr.getKildefoedested()
					+ "\n");
		}
		string = sb.toString();

		if (string.length() > 4096) {
			string = string.substring(0, 4095);
		}

		return string;
	}

	/**
	 * Populate the table
	 *
	 * @param id
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	public void populate(String id, String phonName, String birthDate, String deathDate) throws SQLException {
		this.id = id;
		this.phonName = phonName;
		this.birthDate = birthDate;
		this.deathDate = deathDate;

		thread = new Thread(() -> getCensus(id, phonName, birthDate, deathDate));
		thread.start();
	}

	/**
	 * Create the popup
	 *
	 * @throws SQLException
	 */
	private void popup() throws SQLException {
		final TableItem[] tia = table.getSelection();
		final CensusModel model = (CensusModel) tia[0].getData();
		final StringBuilder sb = new StringBuilder(model.toString());
		sb.append("\n\n");

		try {
			sb.append(getCensusHousehold(model.getKIPnr(), model.getKildestednavn(), model.getHusstands_familienr(),
					model.getMatr_nr_Adresse(), model.getKildehenvisning()));
		} catch (final SQLException e) {
			final ArchiveSearcher as = (ArchiveSearcher) getParent().getParent();
			as.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}

		final MessageDialog dialog = new MessageDialog(getShell(), "Folketælling", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier", "Overhovede" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			try {
				final List<CensusModel> lcr = CensusHouseholdModel.load(props.getProperty("vejbyPath"),
						model.getKIPnr(), model.getKildestednavn(), model.getHusstands_familienr(),
						model.getMatr_nr_Adresse(), model.getKildehenvisning(), props.getProperty("censusSchema"));

				final StringBuilder sb2 = new StringBuilder();

				for (final CensusModel element : lcr) {
					sb2.append(element.toString() + "\n");
				}

				final Clipboard clipboard = new Clipboard(getDisplay());
				final TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { sb2.toString() }, new Transfer[] { textTransfer });
				clipboard.dispose();

			} catch (final SQLException e) {
				final ArchiveSearcher as = (ArchiveSearcher) getParent().getParent();
				as.setErrorMessage(e.getMessage());
				e.printStackTrace();
			}
		} else if (open == 2) {
			final List<CensusModel> lcr = CensusHouseholdModel.load(props.getProperty("vejbyPath"), model.getKIPnr(),
					model.getKildestednavn(), model.getHusstands_familienr(), model.getMatr_nr_Adresse(),
					model.getKildehenvisning(), props.getProperty("censusSchema"));
			final CensusModel censusModel = lcr.get(0);
			final String headOfHousehold = CensusHouseholdModel.getHeadOfHousehold(props, censusModel);

			final Shell[] shells = getDisplay().getShells();
			final MessageBox messageBox = new MessageBox(shells[0], SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			messageBox.setText("Info");
			messageBox.setMessage("Husstandens overhovede var " + headOfHousehold);
			final int buttonID = messageBox.open();

			if (buttonID == SWT.OK && headOfHousehold != null && headOfHousehold.length() > 0
					&& !headOfHousehold.equals("ikke fundet")) {
				final ArchiveSearcher grandParent = (ArchiveSearcher) getParent().getParent();
				grandParent.getSearchId().setText(headOfHousehold);
				grandParent.searchById(null);
			}
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
