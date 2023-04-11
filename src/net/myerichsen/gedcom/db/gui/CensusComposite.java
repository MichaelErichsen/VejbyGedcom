package net.myerichsen.gedcom.db.gui;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.gedcom.db.comparators.CensusComparator;
import net.myerichsen.gedcom.db.filters.CensusAgeFilter;
import net.myerichsen.gedcom.db.filters.CensusBirthPlaceFilter;
import net.myerichsen.gedcom.db.filters.CensusCountyFilter;
import net.myerichsen.gedcom.db.filters.CensusNameFilter;
import net.myerichsen.gedcom.db.filters.CensusParishFilter;
import net.myerichsen.gedcom.db.filters.CensusSexFilter;
import net.myerichsen.gedcom.db.filters.CensusYearFilter;
import net.myerichsen.gedcom.db.models.CensusHousehold;
import net.myerichsen.gedcom.db.models.CensusModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.CensusPopulator;

/**
 * @author Michael Erichsen
 * @version 8. apr. 2023
 *
 */
public class CensusComposite extends Composite {

	private Text txtCensusYear;
	private Text txtCensusCounty;
	private Text txtCensusParish;
	private Text txtCensusName;
	private Text txtCensusSex;
	private TableViewer censusTableViewer;
	private Text txtCensusAge;
	private Text txtCensusBirthPlace;
	private Table censusTable;
	private ASPopulator censusListener;
	private List<CensusModel> household;
	private Properties props;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public CensusComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		censusListener = new CensusPopulator();

		final Composite censusFilterComposite = new Composite(this, SWT.BORDER);
		censusFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		censusFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(censusFilterComposite, SWT.NONE);
		aLabel.setText("Filtre: \u00C5r");

		txtCensusYear = new Text(censusFilterComposite, SWT.BORDER);

		final Label lblAmt = new Label(censusFilterComposite, SWT.NONE);
		lblAmt.setText("Amt");

		txtCensusCounty = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusCounty.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				CensusCountyFilter.getInstance().setSearchText(txtCensusCounty.getText());
				censusTableViewer.refresh();
			}
		});

		final Label lblSogn = new Label(censusFilterComposite, SWT.NONE);
		lblSogn.setText("Sogn");

		txtCensusParish = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusParish.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusParishFilter.getInstance().setSearchText(txtCensusParish.getText());
				censusTableViewer.refresh();
			}
		});

		final Label lblNavn = new Label(censusFilterComposite, SWT.NONE);
		lblNavn.setText("Navn");

		txtCensusName = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusNameFilter.getInstance().setSearchText(txtCensusName.getText());
				censusTableViewer.refresh();
			}
		});

		final Label lblKn = new Label(censusFilterComposite, SWT.NONE);
		lblKn.setText("K\u00F8n");

		txtCensusSex = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusSex.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusSexFilter.getInstance().setSearchText(txtCensusSex.getText());
				censusTableViewer.refresh();
			}
		});

		final Label lblAlder = new Label(censusFilterComposite, SWT.NONE);
		lblAlder.setText("Alder +/- 2 \u00E5r");

		txtCensusAge = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusAge.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusAgeFilter.getInstance().setSearchText(txtCensusAge.getText());
				censusTableViewer.refresh();
			}
		});

		final Label lblFdested = new Label(censusFilterComposite, SWT.NONE);
		lblFdested.setText("F\u00F8dested");

		txtCensusBirthPlace = new Text(censusFilterComposite, SWT.BORDER);
		txtCensusBirthPlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				CensusBirthPlaceFilter.getInstance().setSearchText(txtCensusBirthPlace.getText());
				censusTableViewer.refresh();
			}
		});
		final Button btnRydFelterneCensus = new Button(censusFilterComposite, SWT.NONE);
		btnRydFelterneCensus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CensusBirthPlaceFilter.getInstance().setSearchText("");
				CensusCountyFilter.getInstance().setSearchText("");
				CensusNameFilter.getInstance().setSearchText("");
				CensusParishFilter.getInstance().setSearchText("");
				CensusSexFilter.getInstance().setSearchText("");
				CensusAgeFilter.getInstance().setSearchText("");
				CensusYearFilter.getInstance().setSearchText("");
				txtCensusBirthPlace.setText("");
				txtCensusCounty.setText("");
				txtCensusName.setText("");
				txtCensusParish.setText("");
				txtCensusSex.setText("");
				txtCensusAge.setText("");
				txtCensusYear.setText("");
				censusTableViewer.refresh();
			}
		});
		btnRydFelterneCensus.setText("Ryd felterne");

		final ScrolledComposite censusScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		censusScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		censusScroller.setSize(0, 0);

		censusScroller.setExpandHorizontal(true);
		censusScroller.setExpandVertical(true);

		censusTableViewer = new TableViewer(censusScroller, SWT.BORDER | SWT.FULL_SELECTION);
		censusTableViewer.addDoubleClickListener(event -> {
			censusPopup();
		});

		final ViewerFilter[] filters = new ViewerFilter[7];
		filters[0] = CensusBirthPlaceFilter.getInstance();
		filters[1] = CensusCountyFilter.getInstance();
		filters[2] = CensusNameFilter.getInstance();
		filters[3] = CensusParishFilter.getInstance();
		filters[4] = CensusSexFilter.getInstance();
		filters[5] = CensusYearFilter.getInstance();
		filters[6] = CensusAgeFilter.getInstance();
		censusTableViewer.setFilters(filters);
		censusTableViewer.setComparator(new CensusComparator());

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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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
				final CensusModel cr = (CensusModel) element;
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

		censusScroller.setContent(censusTable);
		censusScroller.setMinSize(censusTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	/**
	 * Create the census popup
	 */
	private void censusPopup() {
		final TableItem[] tia = censusTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 24; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}
		sb.append("\n");

		try {
			sb.append(getCensusHousehold(ti.getText(21), ti.getText(5)));
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		final MessageDialog dialog = new MessageDialog(getShell(), "Folketælling", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			try {
				final List<CensusModel> lcr = CensusHousehold.loadFromDatabase(props.getProperty("vejbyPath"),
						ti.getText(21), ti.getText(5));
				final StringBuilder sb2 = new StringBuilder();

				for (final CensusModel element : lcr) {
					sb2.append(element.toString() + "\n");
				}

				final Clipboard clipboard = new Clipboard(getDisplay());
				final TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { sb2.toString() }, new Transfer[] { textTransfer });
				clipboard.dispose();

			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param property
	 * @param text
	 * @param text2
	 * @return
	 * @throws SQLException
	 */
	protected String getCensusHousehold(String kipNr, String nr) throws SQLException {
		final StringBuilder sb = new StringBuilder();
		String string;

		household = CensusHousehold.loadFromDatabase(props.getProperty("vejbyPath"), kipNr, nr);

		for (final CensusModel hhr : household) {
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
	 * Populate census table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	public void populate(String phonName, String birthDate, String deathDate) throws SQLException {
		new Thread(() -> {
			if (censusListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbyPath"), phonName,
							birthDate.substring(0, 4), deathDate.substring(0, 4) };
					final CensusModel[] CensusRecords = (CensusModel[]) censusListener.loadFromDatabase(loadArgs);

					Display.getDefault().asyncExec(() -> censusTableViewer.setInput(CensusRecords));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
