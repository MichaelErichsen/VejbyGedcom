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

import net.myerichsen.archivesearcher.filters.ProbatePlaceFilter;
import net.myerichsen.archivesearcher.filters.ProbateSourceFilter;
import net.myerichsen.archivesearcher.models.ProbateModel;

/**
 * Probate view
 *
 * @author Michael Erichsen
 * @version 8. aug. 2023
 *
 */
public class ProbateView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private Properties props;
	private Thread thread;
	private Text txtProbateSource;
	private Text txtProbatePlace;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public ProbateView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		final Composite ProbateFilterComposite = new Composite(this, SWT.BORDER);
		ProbateFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ProbateFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(ProbateFilterComposite, SWT.NONE);
		aLabel.setText("Filter: Sted");

		txtProbatePlace = new Text(ProbateFilterComposite, SWT.BORDER);
		txtProbatePlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtProbatePlace.getText().length() > 0) {
					txtProbatePlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtProbatePlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				ProbatePlaceFilter.getInstance().setSearchText(txtProbatePlace.getText());
				tableViewer.refresh();
			}
		});

		final Label lblKilde = new Label(ProbateFilterComposite, SWT.NONE);
		lblKilde.setText("Kilde");

		txtProbateSource = new Text(ProbateFilterComposite, SWT.BORDER);
		txtProbateSource.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtProbateSource.getText().length() > 0) {
					txtProbateSource.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtProbateSource.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				ProbateSourceFilter.getInstance().setSearchText(txtProbateSource.getText());
				tableViewer.refresh();
			}
		});

		final Button btnRydFelterneProbate = new Button(ProbateFilterComposite, SWT.NONE);
		btnRydFelterneProbate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneProbate.setText("Ryd felterne");

		final ScrolledComposite probateScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		probateScroller.setExpandHorizontal(true);
		probateScroller.setExpandVertical(true);
		probateScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		probateScroller.setSize(0, 0);

		tableViewer = new TableViewer(probateScroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup(getDisplay()));
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = ProbatePlaceFilter.getInstance();
		filters[1] = ProbateSourceFilter.getInstance();
		tableViewer.setFilters(filters);

		final TableViewerColumn Column_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnNavn = Column_9.getColumn();
		tblclmnNavn.setWidth(100);
		tblclmnNavn.setText("Navn");
		Column_9.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getName();
			}
		});

		final TableViewerColumn Column_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFra_1 = Column_10.getColumn();
		tblclmnFra_1.setWidth(100);
		tblclmnFra_1.setText("Fra");
		Column_10.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ProbateModel) element).getFromDate();
			}
		});

		final TableViewerColumn Column_11 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnTil_1 = Column_11.getColumn();
		tblclmnTil_1.setWidth(100);
		tblclmnTil_1.setText("Til");
		Column_11.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ProbateModel) element).getToDate();
			}
		});

		final TableViewerColumn Column_12 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSted = Column_12.getColumn();
		tblclmnSted.setWidth(100);
		tblclmnSted.setText("Sted");
		Column_12.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ProbateModel) element).getPlace();
			}
		});

		final TableViewerColumn Column_13 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnData = Column_13.getColumn();
		tblclmnData.setWidth(300);
		tblclmnData.setText("Data");
		Column_13.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ProbateModel) element).getData();
			}
		});

		final TableViewerColumn Column_14 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKilde = Column_14.getColumn();
		tblclmnKilde.setWidth(300);
		tblclmnKilde.setText("Kilde");
		Column_14.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ProbateModel) element).getSource();
			}
		});

		probateScroller.setContent(table);
		probateScroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
		tableViewer.setInput(new ProbateModel[0]);
		clearFilters();
	}

	/**
	 * Clear filters
	 */
	private void clearFilters() {
		ProbatePlaceFilter.getInstance().setSearchText("");
		ProbateSourceFilter.getInstance().setSearchText("");
		txtProbatePlace.setText("");
		txtProbateSource.setText("");
		txtProbatePlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtProbateSource.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tableViewer.refresh();
	}

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	private void getInput(String phonName, String birthDate, String deathDate) {
		try {
			final ProbateModel[] modelArray = ProbateModel.load(props.getProperty("probateSchema"),
					props.getProperty("probatePath"), phonName, birthDate, deathDate);

			Display.getDefault().asyncExec(() -> tableViewer.setInput(modelArray));
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage("Skifter er hentet"));
		} catch (final Exception e) {
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setErrorMessage(e.getMessage(), e));
		}
	}

	/**
	 * Populate probate table
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

		final String string = ((ProbateModel) tia[0].getData()).toString().replace("�", "\n");

		final MessageDialog dialog = new MessageDialog(getShell(), "Skifter", null, string, MessageDialog.INFORMATION,
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
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
