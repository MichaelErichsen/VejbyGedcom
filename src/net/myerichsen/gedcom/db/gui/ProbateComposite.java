package net.myerichsen.gedcom.db.gui;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.gedcom.db.filters.ProbatePlaceFilter;
import net.myerichsen.gedcom.db.models.ProbateModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.ProbatePopulator;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class ProbateComposite extends Composite {
	private TableViewer probateTableViewer;
	private Table probateTable;
	private ASPopulator probateListener;
	private Properties props;
	private Text txtProbatePlace;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public ProbateComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		probateListener = new ProbatePopulator();

		final Composite ProbateFilterComposite = new Composite(this, SWT.BORDER);
		ProbateFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		ProbateFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(ProbateFilterComposite, SWT.NONE);
		aLabel.setText("Filter: Sted");

		txtProbatePlace = new Text(ProbateFilterComposite, SWT.BORDER);
		txtProbatePlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				ProbatePlaceFilter.getInstance().setSearchText(txtProbatePlace.getText());
				probateTableViewer.refresh();
			}
		});

		final Button btnRydFelterneProbate = new Button(ProbateFilterComposite, SWT.NONE);
		btnRydFelterneProbate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProbatePlaceFilter.getInstance().setSearchText("");
				txtProbatePlace.setText("");
				probateTableViewer.refresh();
			}
		});
		btnRydFelterneProbate.setText("Ryd felterne");

		final ScrolledComposite probateScroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		probateScroller.setExpandHorizontal(true);
		probateScroller.setExpandVertical(true);
		probateScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		probateScroller.setSize(0, 0);

		probateTableViewer = new TableViewer(probateScroller, SWT.BORDER | SWT.FULL_SELECTION);
		probateTableViewer.addDoubleClickListener(event -> probatePopup(getDisplay()));
		probateTable = probateTableViewer.getTable();
		probateTable.setLinesVisible(true);
		probateTable.setHeaderVisible(true);

		probateTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		final ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = ProbatePlaceFilter.getInstance();
		probateTableViewer.setFilters(filters);

		final TableViewerColumn Column_9 = new TableViewerColumn(probateTableViewer, SWT.NONE);
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

		final TableViewerColumn Column_10 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnFra_1 = Column_10.getColumn();
		tblclmnFra_1.setWidth(100);
		tblclmnFra_1.setText("Fra");
		Column_10.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getFromDate();
			}
		});

		final TableViewerColumn Column_11 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnTil_1 = Column_11.getColumn();
		tblclmnTil_1.setWidth(100);
		tblclmnTil_1.setText("Til");
		Column_11.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getToDate();
			}
		});

		final TableViewerColumn Column_12 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnSted = Column_12.getColumn();
		tblclmnSted.setWidth(100);
		tblclmnSted.setText("Sted");
		Column_12.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getPlace();
			}
		});

		final TableViewerColumn Column_13 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnData = Column_13.getColumn();
		tblclmnData.setWidth(300);
		tblclmnData.setText("Data");
		Column_13.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getData();
			}
		});

		final TableViewerColumn Column_14 = new TableViewerColumn(probateTableViewer, SWT.NONE);
		final TableColumn tblclmnKilde = Column_14.getColumn();
		tblclmnKilde.setWidth(300);
		tblclmnKilde.setText("Kilde");
		Column_14.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final ProbateModel pr = (ProbateModel) element;
				return pr.getSource();
			}
		});

		probateScroller.setContent(probateTable);
		probateScroller.setMinSize(probateTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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

	/**
	 * Populate probate table
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	public void populate(String phonName, String birthDate, String deathDate) throws SQLException {
		new Thread(() -> {
			if (probateListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("probatePath"), phonName, birthDate,
							deathDate, props.getProperty("probateSource") };
					final ProbateModel[] probateRecords = (ProbateModel[]) probateListener.loadFromDatabase(loadArgs);

					Display.getDefault().asyncExec(() -> probateTableViewer.setInput(probateRecords));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
