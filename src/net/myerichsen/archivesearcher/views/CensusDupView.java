package net.myerichsen.archivesearcher.views;

import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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

import net.myerichsen.archivesearcher.models.CensusDupModel;
import net.myerichsen.archivesearcher.populators.ASPopulator;
import net.myerichsen.archivesearcher.populators.CensusDupPopulator;

/**
 * Census duplicates view
 *
 * @author Michael Erichsen
 * @version 27. jun. 2023
 *
 */
public class CensusDupView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private ASPopulator listener;
	private Properties props;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public CensusDupView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listener = new CensusDupPopulator();

		final ScrolledComposite scrolledComposite = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		final GridData gd_censusdupScroller = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_censusdupScroller.widthHint = 617;
		scrolledComposite.setLayoutData(gd_censusdupScroller);
		scrolledComposite.setSize(0, 0);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup(getDisplay()));
		table = tableViewer.getTable();
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFornavne = tableViewerColumn.getColumn();
		tblclmnFornavne.setWidth(70);
		tblclmnFornavne.setText("ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusDupModel pr = (CensusDupModel) element;
				return pr.getIndividual();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn_1 = tableViewerColumn_1.getColumn();
		tblclmnEfternavn_1.setWidth(100);
		tblclmnEfternavn_1.setText("Dato");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusDupModel pr = (CensusDupModel) element;
				return pr.getDate().toString();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDdsdato = tableViewerColumn_2.getColumn();
		tblclmnDdsdato.setWidth(300);
		tblclmnDdsdato.setText("Sted");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusDupModel pr = (CensusDupModel) element;
				return pr.getPlace();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFder_1 = tableViewerColumn_3.getColumn();
		tblclmnFder_1.setWidth(600);
		tblclmnFder_1.setText("Detaljer");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusDupModel pr = (CensusDupModel) element;
				return pr.getSourceDetail();
			}
		});

		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Composite buttonComposite = new Composite(this, SWT.BORDER);
		buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label lblDanListeOver = new Label(buttonComposite, SWT.NONE);
		lblDanListeOver
				.setText("Dan liste over personer med flere folket\u00E6llingsh\u00E6ndelser p\u00E5 samme dato");

		final Button btnFind = new Button(buttonComposite, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					populate();
				} catch (final SQLException e1) {
					((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e1.getMessage());
				}
			}
		});
		btnFind.setText("Find");

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
		final CensusDupModel[] input = new CensusDupModel[0];
		tableViewer.setInput(input);
		tableViewer.refresh();
	}

	/**
	 *
	 */
	private void getInput() {
		if (listener != null) {
			try {
				Display.getDefault()
						.asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
								.getIndicator().setVisible(true));
				final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),

						props.getProperty("vejbyPath") };
				final CensusDupModel[] censusdupRecords = (CensusDupModel[]) listener.load(loadArgs);

				Display.getDefault().asyncExec(() -> tableViewer.setInput(censusdupRecords));
				Display.getDefault()
						.asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
								.setMessage("Folketællingsdubletter er hentet"));
			} catch (final Exception e) {
				Display.getDefault()
						.asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
								.setMessage(e.getMessage()));
			}
		}
	}

	/**
	 * Populate the census duplicate tab from the database
	 *
	 * @throws SQLException
	 */
	public void populate() throws SQLException {
		thread = new Thread(this::getInput);
		thread.start();
	}

	/**
	 * Popup
	 *
	 * @param display
	 */
	private void popup(Display display) {
		final TableItem[] tia = table.getSelection();
		final CensusDupModel m = (CensusDupModel) tia[0].getData();
		final String string = m.toString() + "\n";

		final MessageDialog dialog = new MessageDialog(getShell(), "Folketællingsdubletter", null, string,
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier", "Søg efter" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		} else if (open == 2) {
			final String siblingsId = m.getIndividual();
			final ArchiveSearcher grandParent = (ArchiveSearcher) getParent().getParent().getParent();
			grandParent.getSearchId().setText(siblingsId);
			grandParent.searchById(null);
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
