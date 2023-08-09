package net.myerichsen.archivesearcher.views;

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

import net.myerichsen.archivesearcher.comparators.RelocationComparator;
import net.myerichsen.archivesearcher.filters.RelocationGivenFilter;
import net.myerichsen.archivesearcher.filters.RelocationSurnameFilter;
import net.myerichsen.archivesearcher.models.RelocationModel;

/**
 * Relocation view
 *
 * @author Michael Erichsen
 * @version 8. aug. 2023
 *
 */
public class RelocationView extends Composite {
	private TableViewer tableViewer;
	private Text txtRelocationGiven;
	private Text txtRelocationSurname;
	private Table table;
	private Properties props;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public RelocationView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		final Composite relocationFilterComposite = new Composite(this, SWT.BORDER);
		relocationFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		relocationFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label cLabel = new Label(relocationFilterComposite, SWT.NONE);
		cLabel.setText("Filtre: Fornavn");

		txtRelocationGiven = new Text(relocationFilterComposite, SWT.BORDER);
		txtRelocationGiven.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtRelocationGiven.getText().length() > 0) {
					txtRelocationGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtRelocationGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				RelocationGivenFilter.getInstance().setSearchText(txtRelocationGiven.getText());
				tableViewer.refresh();
			}
		});

		final Label dLabel = new Label(relocationFilterComposite, SWT.NONE);
		dLabel.setText("Efternavn");

		txtRelocationSurname = new Text(relocationFilterComposite, SWT.BORDER);
		txtRelocationSurname.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtRelocationSurname.getText().length() > 0) {
					txtRelocationSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtRelocationSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				RelocationSurnameFilter.getInstance().setSearchText(txtRelocationSurname.getText());
				tableViewer.refresh();
			}
		});

		final Button btnRydFelterneRelocation = new Button(relocationFilterComposite, SWT.NONE);
		btnRydFelterneRelocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneRelocation.setText("Ryd felterne");

		final ScrolledComposite relocationScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		relocationScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		relocationScroller.setSize(0, 0);
		relocationScroller.setExpandHorizontal(true);
		relocationScroller.setExpandVertical(true);

		tableViewer = new TableViewer(relocationScroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup());

		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = RelocationGivenFilter.getInstance();
		filters[1] = RelocationSurnameFilter.getInstance();
		tableViewer.setFilters(filters);
		tableViewer.setComparator(new RelocationComparator());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableViewerColumn relocationTableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
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

		final TableViewerColumn relocationTableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFornavn = relocationTableViewerColumn_1.getColumn();
		tblclmnFornavn.setWidth(100);
		tblclmnFornavn.setText("Fornavn");
		relocationTableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getGivenName();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn = relocationTableViewerColumn_2.getColumn();
		tblclmnEfternavn.setWidth(100);
		tblclmnEfternavn.setText("Efternavn");
		relocationTableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getSurName();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFlyttedato = relocationTableViewerColumn_3.getColumn();
		tblclmnFlyttedato.setWidth(100);
		tblclmnFlyttedato.setText("Flyttedato");
		relocationTableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getDate().toString();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnTil = relocationTableViewerColumn_4.getColumn();
		tblclmnTil.setWidth(200);
		tblclmnTil.setText("Til");
		relocationTableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getPlace();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFra = relocationTableViewerColumn_5.getColumn();
		tblclmnFra.setWidth(100);
		tblclmnFra.setText("Fra");
		relocationTableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getNote();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer = relocationTableViewerColumn_6.getColumn();
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Detaljer");
		relocationTableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getSourceDetail();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdselsdato = relocationTableViewerColumn_7.getColumn();
		tblclmnFdselsdato.setWidth(100);
		tblclmnFdselsdato.setText("F\u00F8dselsdato");
		relocationTableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getBirthDate().toString();
			}
		});
		final TableViewerColumn relocationTableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnForldre = relocationTableViewerColumn_8.getColumn();
		tblclmnForldre.setWidth(177);
		tblclmnForldre.setText("For\u00E6ldre");
		relocationTableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((RelocationModel) element).getParents();
			}
		});

		relocationScroller.setContent(table);
		relocationScroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
		tableViewer.setInput(new RelocationModel[0]);
		clearFilters();
	}

	/**
	 *
	 */
	private void clearFilters() {
		RelocationGivenFilter.getInstance().setSearchText("");
		RelocationSurnameFilter.getInstance().setSearchText("");
		txtRelocationGiven.setText("");
		txtRelocationSurname.setText("");
		txtRelocationGiven.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtRelocationSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tableViewer.refresh();
	}

	/**
	 * @param phonName
	 * @param birthDate
	 */
	private void getInput(String phonName, String birthDate) {
		try {
			final RelocationModel[] relocationRecords = RelocationModel.load(props.getProperty("parishSchema"),
					props.getProperty("parishPath"), phonName, birthDate);

			Display.getDefault().asyncExec(() -> tableViewer.setInput(relocationRecords));
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage("Flytninger er hentet"));
		} catch (final Exception e) {
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setErrorMessage(e.getMessage(), e));
		}
	}

	/**
	 * @param phonName
	 * @param birthDate
	 */
	public void populate(String phonName, String birthDate) {
		thread = new Thread(() -> getInput(phonName, birthDate));
		thread.start();
	}

	/**
	 *
	 */
	private void popup() {
		final TableItem[] tia = table.getSelection();
		final String string = ((RelocationModel) tia[0].getData()).toString() + "\n";
		final MessageDialog dialog = new MessageDialog(getShell(), "Flytning", null, string, MessageDialog.INFORMATION,
				new String[] { "OK", "Kopier" }, 0);
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
