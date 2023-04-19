package net.myerichsen.gedcom.db.views;

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

import net.myerichsen.gedcom.db.models.CensusdupModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.CensusdupPopulator;

/**
 * Census duplicates view
 *
 * @author Michael Erichsen
 * @version 19. apr. 2023
 *
 */
public class CensusdupView extends Composite {
	private TableViewer censusdupTableViewer;
	private Table censusdupTable;
	private ASPopulator censusdupListener;
	private Properties props;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public CensusdupView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		censusdupListener = new CensusdupPopulator();

		final ScrolledComposite censusdupScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_censusdupScroller = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_censusdupScroller.widthHint = 617;
		censusdupScroller.setLayoutData(gd_censusdupScroller);
		censusdupScroller.setSize(0, 0);
		censusdupScroller.setExpandHorizontal(true);
		censusdupScroller.setExpandVertical(true);

		censusdupTableViewer = new TableViewer(censusdupScroller, SWT.BORDER | SWT.FULL_SELECTION);
		censusdupTableViewer.addDoubleClickListener(event -> censusdupPopup(getDisplay()));
		censusdupTable = censusdupTableViewer.getTable();
		censusdupTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		censusdupTable.setHeaderVisible(true);
		censusdupTable.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(censusdupTableViewer, SWT.NONE);
		final TableColumn tblclmnFornavne = tableViewerColumn.getColumn();
		tblclmnFornavne.setWidth(70);
		tblclmnFornavne.setText("ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusdupModel pr = (CensusdupModel) element;
				return pr.getIndividual();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(censusdupTableViewer, SWT.NONE);
		final TableColumn tblclmnEfternavn_1 = tableViewerColumn_1.getColumn();
		tblclmnEfternavn_1.setWidth(100);
		tblclmnEfternavn_1.setText("Dato");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusdupModel pr = (CensusdupModel) element;
				return pr.getDate().toString();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(censusdupTableViewer, SWT.NONE);
		final TableColumn tblclmnDdsdato = tableViewerColumn_2.getColumn();
		tblclmnDdsdato.setWidth(300);
		tblclmnDdsdato.setText("Sted");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusdupModel pr = (CensusdupModel) element;
				return pr.getPlace();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(censusdupTableViewer, SWT.NONE);
		final TableColumn tblclmnFder_1 = tableViewerColumn_3.getColumn();
		tblclmnFder_1.setWidth(600);
		tblclmnFder_1.setText("Detaljer");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final CensusdupModel pr = (CensusdupModel) element;
				return pr.getSourceDetail();
			}
		});

		censusdupScroller.setContent(censusdupTable);
		censusdupScroller.setMinSize(censusdupTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite buttonComposite = new Composite(this, SWT.BORDER);
		buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Label lblDanListeOver = new Label(buttonComposite, SWT.NONE);
		lblDanListeOver
				.setText("Dan liste over personer med flere folket\u00E6llingsh\u00E6ndelser p\u00E5 samme dato");

		Button btnFind = new Button(buttonComposite, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					populate();
				} catch (SQLException e1) {
					((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e1.getMessage());
				}
			}
		});
		btnFind.setText("Find");

	}

	/**
	 * @param display
	 */
	private void censusdupPopup(Display display) {
		final TableItem[] tia = censusdupTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 25; i++) {
			if (ti.getText(i).length() > 0) {
				if (ti.getText(i).length() > 0) {
					sb.append(ti.getText(i).trim() + ", ");
				}
			}
		}

		sb.append("\n");

		final String string = sb.toString();

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

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Clear the table
	 */
	public void clear() {
		final CensusdupModel[] input = new CensusdupModel[0];
		censusdupTableViewer.setInput(input);
		censusdupTableViewer.refresh();
	}

	/**
	 * Populate the census duplicate tab from the database
	 *
	 * @throws SQLException
	 */
	public void populate() throws SQLException {
		new Thread(() -> {
			if (censusdupListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),
							props.getProperty("vejbyPath") };
					final CensusdupModel[] censusdupRecords = (CensusdupModel[]) censusdupListener.load(loadArgs);

					Display.getDefault().asyncExec(() -> censusdupTableViewer.setInput(censusdupRecords));
					Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
							.setMessage("Folketællingsdubletter er hentet"));
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
