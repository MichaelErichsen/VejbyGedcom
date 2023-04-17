package net.myerichsen.gedcom.db.views;

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

import net.myerichsen.gedcom.db.comparators.PolregComparator;
import net.myerichsen.gedcom.db.filters.PolregAddressFilter;
import net.myerichsen.gedcom.db.filters.PolregBirthdateFilter;
import net.myerichsen.gedcom.db.models.PolregModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.PolregPopulator;

/**
 * @author Michael Erichsen
 * @version 17. apr. 2023
 *
 */
public class PolregView extends Composite {
	private TableViewer polregTableViewer;
	private Table polregTable;
	private ASPopulator polregListener;
	private Properties props;
	private Text txtPolregAddress;
	private Text txtPolregBirthDate;

	// TODO Add a name filter

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public PolregView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		polregListener = new PolregPopulator();

		final Composite PolregFilterComposite = new Composite(this, SWT.BORDER);
		PolregFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		PolregFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(PolregFilterComposite, SWT.NONE);
		aLabel.setText("Filtre: Adresse");

		txtPolregAddress = new Text(PolregFilterComposite, SWT.BORDER);
		txtPolregAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtPolregAddress.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				PolregAddressFilter.getInstance().setSearchText(txtPolregAddress.getText());
				polregTableViewer.refresh();
			}
		});

		final Label lblprdb = new Label(PolregFilterComposite, SWT.NONE);
		lblprdb.setText("Fødselsdato");

		txtPolregBirthDate = new Text(PolregFilterComposite, SWT.BORDER);
		txtPolregBirthDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtPolregBirthDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				PolregBirthdateFilter.getInstance().setSearchText(txtPolregBirthDate.getText());
				polregTableViewer.refresh();
			}
		});

		final Button btnRydFelternePolreg = new Button(PolregFilterComposite, SWT.NONE);
		btnRydFelternePolreg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PolregBirthdateFilter.getInstance().setSearchText("");
				PolregAddressFilter.getInstance().setSearchText("");
				txtPolregBirthDate.setText("");
				txtPolregAddress.setText("");
				txtPolregAddress.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				txtPolregBirthDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				polregTableViewer.refresh();
			}
		});
		btnRydFelternePolreg.setText("Ryd felterne");

		final ScrolledComposite polregScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		polregScroller.setExpandHorizontal(true);
		polregScroller.setExpandVertical(true);
		polregScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		polregScroller.setSize(0, 0);

		polregTableViewer = new TableViewer(polregScroller, SWT.BORDER | SWT.FULL_SELECTION);
		polregTable = polregTableViewer.getTable();
		polregTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = PolregAddressFilter.getInstance();
		filters[1] = PolregBirthdateFilter.getInstance();
		polregTableViewer.setFilters(filters);
		polregTableViewer.setComparator(new PolregComparator());
		polregTableViewer.addDoubleClickListener(event -> {
			polregPopup();
		});

		polregTable.setHeaderVisible(true);
		polregTable.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnNavn_1 = tableViewerColumn.getColumn();
		tblclmnNavn_1.setWidth(100);
		tblclmnNavn_1.setText("Navn");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getName();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnFdedag = tableViewerColumn_1.getColumn();
		tblclmnFdedag.setWidth(100);
		tblclmnFdedag.setText("F\u00F8dselsdato");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getBirthDate().toString();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnErhverv_1 = tableViewerColumn_2.getColumn();
		tblclmnErhverv_1.setWidth(100);
		tblclmnErhverv_1.setText("Erhverv");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getOccupation();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnGade = tableViewerColumn_3.getColumn();
		tblclmnGade.setWidth(100);
		tblclmnGade.setText("Gade");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getStreet();
			}
		});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnNr = tableViewerColumn_4.getColumn();
		tblclmnNr.setWidth(40);
		tblclmnNr.setText("Nr.");
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return Integer.toString(pr.getNumber());
			}
		});

		final TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnBogstav = tableViewerColumn_5.getColumn();
		tblclmnBogstav.setWidth(40);
		tblclmnBogstav.setText("Bogstav");
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getLetter();
			}
		});

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnEtage = tableViewerColumn_6.getColumn();
		tblclmnEtage.setWidth(50);
		tblclmnEtage.setText("Etage");
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getFloor();
			}
		});

		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnSted_1 = tableViewerColumn_7.getColumn();
		tblclmnSted_1.setWidth(100);
		tblclmnSted_1.setText("Sted");
		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getPlace();
			}
		});

		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnVrt = tableViewerColumn_8.getColumn();
		tblclmnVrt.setWidth(100);
		tblclmnVrt.setText("V\u00E6rt");
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getHost();
			}
		});

		final TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnDag = tableViewerColumn_9.getColumn();
		tblclmnDag.setWidth(50);
		tblclmnDag.setText("Dag");
		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return Integer.toString(pr.getDay());
			}
		});

		final TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnMned = tableViewerColumn_10.getColumn();
		tblclmnMned.setWidth(49);
		tblclmnMned.setText("M\u00E5ned");
		tableViewerColumn_10.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return Integer.toString(pr.getMonth());
			}
		});

		final TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnr_1 = tableViewerColumn_11.getColumn();
		tblclmnr_1.setWidth(50);
		tblclmnr_1.setText("\u00C5r");
		tableViewerColumn_11.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return Integer.toString(pr.getYear());
			}
		});

		final TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(polregTableViewer, SWT.NONE);
		final TableColumn tblclmnAdresse_1 = tableViewerColumn_12.getColumn();
		tblclmnAdresse_1.setWidth(300);
		tblclmnAdresse_1.setText("Adresse");
		tableViewerColumn_12.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final PolregModel pr = (PolregModel) element;
				return pr.getFullAddress();
			}
		});

		polregScroller.setContent(polregTable);
		polregScroller.setMinSize(polregTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 *
	 */
	private void polregPopup() {
		final TableItem[] tia = polregTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 13; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}
		sb.append("\n");

		final MessageDialog dialog = new MessageDialog(getShell(), "Politiets Registerblade", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(getDisplay());
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { sb.toString() }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	/**
	 * Populate police registry table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	public void populate(String phonName, String birthDate, String deathDate) throws SQLException {
		new Thread(() -> {
			if (polregListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("cphSchema"),
							props.getProperty("cphDbPath"), phonName, birthDate, deathDate };
					final PolregModel[] PolregRecords = (PolregModel[]) polregListener.load(loadArgs);

					Display.getDefault().asyncExec(() -> polregTableViewer.setInput(PolregRecords));
					Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
							.setMessage("Politiets Registerblade er hentet"));
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
