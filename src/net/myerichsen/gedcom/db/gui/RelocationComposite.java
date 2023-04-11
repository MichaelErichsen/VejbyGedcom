package net.myerichsen.gedcom.db.gui;

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

import net.myerichsen.gedcom.db.comparators.RelocationComparator;
import net.myerichsen.gedcom.db.filters.RelocationGivenFilter;
import net.myerichsen.gedcom.db.filters.RelocationSurnameFilter;
import net.myerichsen.gedcom.db.models.RelocationModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.RelocationPopulator;

/**
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */
public class RelocationComposite extends Composite {
	private TableViewer relocationTableViewer;
	private Text txtRelocationGiven;
	private Text txtRelocationSurname;
	private Table relocationTable;
	private ASPopulator relocationListener;
	private Properties props;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public RelocationComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		relocationListener = new RelocationPopulator();

		final Composite relocationFilterComposite = new Composite(this, SWT.BORDER);
		relocationFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		relocationFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label cLabel = new Label(relocationFilterComposite, SWT.NONE);
		cLabel.setText("Filtre: Fornavn");

		txtRelocationGiven = new Text(relocationFilterComposite, SWT.BORDER);
		txtRelocationGiven.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				RelocationGivenFilter.getInstance().setSearchText(txtRelocationGiven.getText());
				relocationTableViewer.refresh();
			}
		});

		final Label dLabel = new Label(relocationFilterComposite, SWT.NONE);
		dLabel.setText("Efternavn");

		txtRelocationSurname = new Text(relocationFilterComposite, SWT.BORDER);
		txtRelocationSurname.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				RelocationSurnameFilter.getInstance().setSearchText(txtRelocationSurname.getText());
				relocationTableViewer.refresh();
			}
		});

		final Button btnRydFelterneRelocation = new Button(relocationFilterComposite, SWT.NONE);
		btnRydFelterneRelocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RelocationGivenFilter.getInstance().setSearchText("");
				RelocationSurnameFilter.getInstance().setSearchText("");
				txtRelocationGiven.setText("");
				txtRelocationSurname.setText("");
				relocationTableViewer.refresh();
			}
		});
		btnRydFelterneRelocation.setText("Ryd felterne");

		final ScrolledComposite relocationScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		relocationScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		relocationScroller.setSize(0, 0);
		relocationScroller.setExpandHorizontal(true);
		relocationScroller.setExpandVertical(true);

		relocationTableViewer = new TableViewer(relocationScroller, SWT.BORDER | SWT.FULL_SELECTION);
		relocationTableViewer.addDoubleClickListener(event -> relocationPopup());

		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = RelocationGivenFilter.getInstance();
		filters[1] = RelocationSurnameFilter.getInstance();
		relocationTableViewer.setFilters(filters);
		relocationTableViewer.setComparator(new RelocationComparator());

		relocationTable = relocationTableViewer.getTable();
		relocationTable.setLinesVisible(true);
		relocationTable.setHeaderVisible(true);

		relocationTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		final TableViewerColumn relocationTableViewerColumn = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		relocationTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
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
				final RelocationModel rr = (RelocationModel) element;
				return rr.getBirthDate().toString();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_8 = new TableViewerColumn(relocationTableViewer, SWT.NONE);
		final TableColumn tblclmnForldre = relocationTableViewerColumn_8.getColumn();
		tblclmnForldre.setWidth(177);
		tblclmnForldre.setText("For\u00E6ldre");
		relocationTableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final RelocationModel rr = (RelocationModel) element;
				return rr.getParents();
			}
		});

		relocationScroller.setContent(relocationTable);
		relocationScroller.setMinSize(relocationTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	public void populate(String phonName, String birthDate, String deathDate) {
		new Thread(() -> {
			if (relocationListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),
							props.getProperty("vejbyPath"), phonName, birthDate, deathDate };
					final RelocationModel[] relocationRecords = (RelocationModel[]) relocationListener
							.loadFromDatabase(loadArgs);

					Display.getDefault().asyncExec(() -> relocationTableViewer.setInput(relocationRecords));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 *
	 */
	private void relocationPopup() {
		final TableItem[] tia = relocationTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (ti.getText(i).length() > 0) {
				sb.append(ti.getText(i) + ", ");
			}
		}
		sb.append("\n");

		final MessageDialog dialog = new MessageDialog(getShell(), "Flytning", null, sb.toString(),
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(getDisplay());
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { tia[0].getText(6) }, new Transfer[] { textTransfer });
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
