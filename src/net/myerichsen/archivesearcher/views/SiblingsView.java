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

import net.myerichsen.archivesearcher.comparators.SiblingComparator;
import net.myerichsen.archivesearcher.filters.SiblingsParentsFilter;
import net.myerichsen.archivesearcher.filters.SiblingsPlaceFilter;
import net.myerichsen.archivesearcher.models.SiblingsModel;
import net.myerichsen.archivesearcher.populators.ASPopulator;
import net.myerichsen.archivesearcher.populators.SiblingsPopulator;

/**
 * Siblings view
 *
 * @author Michael Erichsen
 * @version 9. maj 2023
 *
 */
public class SiblingsView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private ASPopulator listener;
	private Properties props;
	private Text txtSiblingsPlace;
	private Text txtSiblingsParents;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public SiblingsView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listener = new SiblingsPopulator();

		final Composite SiblingsFilterComposite = new Composite(this, SWT.BORDER);
		SiblingsFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		SiblingsFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label lblSted = new Label(SiblingsFilterComposite, SWT.NONE);
		lblSted.setText("Filter: Sted");

		txtSiblingsPlace = new Text(SiblingsFilterComposite, SWT.BORDER);
		txtSiblingsPlace.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtSiblingsPlace.getText().length() > 0) {
					txtSiblingsPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtSiblingsPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				SiblingsPlaceFilter.getInstance().setSearchText(txtSiblingsPlace.getText());
				tableViewer.refresh();
			}
		});

		final Label lblparents = new Label(SiblingsFilterComposite, SWT.NONE);
		lblparents.setText("For�ldre");

		txtSiblingsParents = new Text(SiblingsFilterComposite, SWT.BORDER);
		txtSiblingsParents.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtSiblingsParents.getText().length() > 0) {
					txtSiblingsParents.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtSiblingsParents.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				SiblingsParentsFilter.getInstance().setSearchText(txtSiblingsParents.getText());
				tableViewer.refresh();
			}
		});

		final Button btnRydFelterneSiblings = new Button(SiblingsFilterComposite, SWT.NONE);
		btnRydFelterneSiblings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneSiblings.setText("Ryd felterne");

		final ScrolledComposite siblingsScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		siblingsScroller.setExpandHorizontal(true);
		siblingsScroller.setExpandVertical(true);
		siblingsScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		siblingsScroller.setSize(0, 0);

		tableViewer = new TableViewer(siblingsScroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup(getDisplay()));
		table = tableViewer.getTable();

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		final ViewerFilter[] filters = new ViewerFilter[2];
		filters[0] = SiblingsParentsFilter.getInstance();
		filters[1] = SiblingsPlaceFilter.getInstance();
		tableViewer.setFilters(filters);
		tableViewer.setComparator(new SiblingComparator());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
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

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
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

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
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

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
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

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
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

		siblingsScroller.setContent(table);
		siblingsScroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
		final SiblingsModel[] input = new SiblingsModel[0];
		tableViewer.setInput(input);
		clearFilters();
	}

	/**
	 *
	 */
	private void clearFilters() {
		SiblingsPlaceFilter.getInstance().setSearchText("");
		SiblingsParentsFilter.getInstance().setSearchText("");
		txtSiblingsPlace.setText("");
		txtSiblingsParents.setText("");
		txtSiblingsParents.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtSiblingsPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tableViewer.refresh();
	}

	/**
	 * Populate siblings table by individual parent field
	 *
	 * @param parents
	 * @throws SQLException
	 */
	public void populate(String parents) throws SQLException {
		new Thread(() -> {
			if (listener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),
							props.getProperty("vejbyPath"), parents };
					final SiblingsModel[] siblingRecords = (SiblingsModel[]) listener.load(loadArgs);

					Display.getDefault().asyncExec(() -> tableViewer.setInput(siblingRecords));
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
		thread = new Thread(() -> {
			if (listener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),
							props.getProperty("vejbyPath"), fathersName, mothersName };
					final SiblingsModel[] SiblingRecords = (SiblingsModel[]) listener.load(loadArgs);

					Display.getDefault().asyncExec(() -> tableViewer.setInput(SiblingRecords));
					Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
							.setMessage("S�skende er hentet"));

				} catch (final Exception e) {
					Display.getDefault().asyncExec(
							() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e.getMessage()));
				}
			}
		});
		thread.start();
	}

	/**
	 * @param display
	 * @param event
	 */
	private void popup(Display display) {
		final TableItem[] tia = table.getSelection();
//		final TableItem ti = tia[0];
//
//		final StringBuilder sb = new StringBuilder();
//
//		for (int i = 0; i < 25; i++) {
//			if (ti.getText(i).length() > 0) {
//				if (ti.getText(i).length() > 0) {
//					sb.append(ti.getText(i).trim() + ", ");
//				}
//			}
//		}
//
//		sb.append("\n");
//
//		final String string = sb.toString();

		final SiblingsModel m = (SiblingsModel) tia[0].getData();
		final String string = m.toString() + "\n";

		final MessageDialog dialog = new MessageDialog(getShell(), "S�skende", null, string, MessageDialog.INFORMATION,
				new String[] { "OK", "Kopier", "S�g efter" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(display);
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { string }, new Transfer[] { textTransfer });
			clipboard.dispose();
		} else if (open == 2) {
			final String siblingsId = m.getIndividualKey();
			final ArchiveSearcher grandParent = (ArchiveSearcher) getParent().getParent();
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