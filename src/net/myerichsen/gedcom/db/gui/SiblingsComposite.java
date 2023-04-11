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

import net.myerichsen.gedcom.db.comparators.SiblingComparator;
import net.myerichsen.gedcom.db.filters.SiblingsParentsFilter;
import net.myerichsen.gedcom.db.filters.SiblingsPlaceFilter;
import net.myerichsen.gedcom.db.models.SiblingsModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.SiblingsPopulator;

/**
 * @author Michael Erichsen
 * @version 8. apr. 2023
 *
 */
public class SiblingsComposite extends Composite {
	private TableViewer siblingsTableViewer;
	private Table siblingsTable;
	private ASPopulator siblingsListener;
	private Properties props;
	private Text txtSiblingsPlace;
	private Text txtSiblingsParents;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public SiblingsComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		siblingsListener = new SiblingsPopulator();

		final Composite SiblingsFilterComposite = new Composite(this, SWT.BORDER);
		SiblingsFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		SiblingsFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label lblSted = new Label(SiblingsFilterComposite, SWT.NONE);
		lblSted.setText("Filter: Sted");

		txtSiblingsPlace = new Text(SiblingsFilterComposite, SWT.BORDER);
		txtSiblingsPlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				SiblingsPlaceFilter.getInstance().setSearchText(txtSiblingsPlace.getText());
				siblingsTableViewer.refresh();
			}
		});

		final Label lblparents = new Label(SiblingsFilterComposite, SWT.NONE);
		lblparents.setText("Forældre");

		txtSiblingsParents = new Text(SiblingsFilterComposite, SWT.BORDER);
		txtSiblingsParents.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				SiblingsParentsFilter.getInstance().setSearchText(txtSiblingsParents.getText());
				siblingsTableViewer.refresh();
			}
		});

		final Button btnRydFelterneSiblings = new Button(SiblingsFilterComposite, SWT.NONE);
		btnRydFelterneSiblings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SiblingsPlaceFilter.getInstance().setSearchText("");
				SiblingsParentsFilter.getInstance().setSearchText("");
				txtSiblingsPlace.setText("");
				txtSiblingsParents.setText("");
				siblingsTableViewer.refresh();
			}
		});
		btnRydFelterneSiblings.setText("Ryd felterne");

		final ScrolledComposite siblingsScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		siblingsScroller.setExpandHorizontal(true);
		siblingsScroller.setExpandVertical(true);
		siblingsScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		siblingsScroller.setSize(0, 0);

		siblingsTableViewer = new TableViewer(siblingsScroller, SWT.BORDER | SWT.FULL_SELECTION);
		siblingsTableViewer.addDoubleClickListener(event -> siblingsPopup(getDisplay()));
		siblingsTable = siblingsTableViewer.getTable();

		siblingsTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = SiblingsParentsFilter.getInstance();
		filters[1] = SiblingsPlaceFilter.getInstance();
		siblingsTableViewer.setFilters(filters);
		siblingsTableViewer.setComparator(new SiblingComparator());

		siblingsTable.setHeaderVisible(true);
		siblingsTable.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(siblingsTableViewer, SWT.NONE);
		final TableColumn tblclmnId_1 = tableViewerColumn.getColumn();
		tblclmnId_1.setWidth(91);
		tblclmnId_1.setText("ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final SiblingsModel pr = (SiblingsModel) element;
				return pr.getIndividualKey();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(siblingsTableViewer, SWT.NONE);
		final TableColumn tblclmnFdselsdato = tableViewerColumn_1.getColumn();
		tblclmnFdselsdato.setWidth(100);
		tblclmnFdselsdato.setText("F\u00F8dselsdato");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final SiblingsModel pr = (SiblingsModel) element;
				return pr.getBirthYear() + "";
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(siblingsTableViewer, SWT.NONE);
		final TableColumn tblclmnNavn_2 = tableViewerColumn_2.getColumn();
		tblclmnNavn_2.setWidth(146);
		tblclmnNavn_2.setText("Navn");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final SiblingsModel pr = (SiblingsModel) element;
				return pr.getName();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(siblingsTableViewer, SWT.NONE);
		final TableColumn tblclmnForldre_1 = tableViewerColumn_3.getColumn();
		tblclmnForldre_1.setWidth(237);
		tblclmnForldre_1.setText("For\u00E6ldre");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final SiblingsModel pr = (SiblingsModel) element;
				return pr.getParents();
			}
		});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(siblingsTableViewer, SWT.NONE);
		final TableColumn tblclmnSted_2 = tableViewerColumn_4.getColumn();
		tblclmnSted_2.setWidth(484);
		tblclmnSted_2.setText("Sted");
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final SiblingsModel pr = (SiblingsModel) element;
				return pr.getPlace();
			}
		});

		siblingsScroller.setContent(siblingsTable);
		siblingsScroller.setMinSize(siblingsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Populate siblings table by individual parent field
	 *
	 * @param parents
	 * @throws SQLException
	 */
	public void populate(String parents) throws SQLException {
		new Thread(() -> {
			if (siblingsListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbyPath"), parents };
					final SiblingsModel[] siblingRecords = (SiblingsModel[]) siblingsListener
							.loadFromDatabase(loadArgs);

					Display.getDefault().asyncExec(() -> siblingsTableViewer.setInput(siblingRecords));
				} catch (final Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * Populate siblings table by father and mother
	 *
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @throws SQLException
	 */
	public void populate(String fathersName, String mothersName) throws SQLException {
		siblingsTable.removeAll();

		new Thread(() -> {
			if (siblingsListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbyPath"), fathersName, mothersName };
					final SiblingsModel[] SiblingRecords = (SiblingsModel[]) siblingsListener
							.loadFromDatabase(loadArgs);

					Display.getDefault().asyncExec(() -> siblingsTableViewer.setInput(SiblingRecords));
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

	/**
	 * @param display
	 */
	private void siblingsPopup(Display display) {
		final TableItem[] tia = siblingsTable.getSelection();
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

		final MessageDialog dialog = new MessageDialog(getShell(), "Søskende", null, string, MessageDialog.INFORMATION,
				new String[] { "OK", "Kopier" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}
}
