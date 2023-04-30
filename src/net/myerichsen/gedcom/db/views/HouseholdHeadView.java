package net.myerichsen.gedcom.db.views;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.gedcom.db.comparators.HouseholdHeadComparator;
import net.myerichsen.gedcom.db.filters.HouseholdHeadNameFilter;
import net.myerichsen.gedcom.db.filters.HouseholdHeadPlaceFilter;
import net.myerichsen.gedcom.db.filters.HouseholdHeadTypeFilter;
import net.myerichsen.gedcom.db.models.HouseholdHeadModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.HouseholdHeadPopulator;

/**
 * @author Michael Erichsen
 * @version 30. apr. 2023
 *
 */
public class HouseholdHeadView extends Composite {
	private TableViewer householdHeadTableViewer;
	private Table householdHeadTable;
	private ASPopulator householdHeadListener;
	private Text txtHouseholdPlace;
	private Text txtHouseholdRelocatorName;
	private Combo txtHouseholdEventType;
	private Properties props;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public HouseholdHeadView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		householdHeadListener = new HouseholdHeadPopulator();

		final Composite HouseholdHeadFilterComposite = new Composite(this, SWT.BORDER);
		HouseholdHeadFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		HouseholdHeadFilterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label cLabel = new Label(HouseholdHeadFilterComposite, SWT.NONE);
		cLabel.setText("Filtre: Sted");

		txtHouseholdPlace = new Text(HouseholdHeadFilterComposite, SWT.BORDER);
		txtHouseholdPlace.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtHouseholdPlace.getText().length() > 0) {
					txtHouseholdPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtHouseholdPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				HouseholdHeadPlaceFilter.getInstance().setSearchText(txtHouseholdPlace.getText());
				householdHeadTableViewer.refresh();
			}
		});

		final Label dLabel = new Label(HouseholdHeadFilterComposite, SWT.NONE);
		dLabel.setText("Tjenendenavn");

		txtHouseholdRelocatorName = new Text(HouseholdHeadFilterComposite, SWT.BORDER);
		txtHouseholdRelocatorName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtHouseholdRelocatorName.getText().length() > 0) {
					txtHouseholdRelocatorName.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtHouseholdRelocatorName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				HouseholdHeadNameFilter.getInstance().setSearchText(txtHouseholdRelocatorName.getText());
				householdHeadTableViewer.refresh();
			}
		});

		final Label eLabel = new Label(HouseholdHeadFilterComposite, SWT.NONE);
		eLabel.setText("H\u00E6ndelsestype");

		txtHouseholdEventType = new Combo(HouseholdHeadFilterComposite, SWT.BORDER);
		txtHouseholdEventType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txtHouseholdEventType.getText().length() > 0) {
					txtHouseholdEventType.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtHouseholdEventType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				HouseholdHeadTypeFilter.getInstance().setSearchText(txtHouseholdEventType.getText());
				householdHeadTableViewer.refresh();

			}
		});
		final String[] eventTypes = { "", "Flytning", "Folketælling" };
		txtHouseholdEventType.setItems(eventTypes);

		final Button btnRydFelterneHouseholdHead = new Button(HouseholdHeadFilterComposite, SWT.NONE);
		btnRydFelterneHouseholdHead.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneHouseholdHead.setText("Ryd felterne");

		final ScrolledComposite HouseholdHeadScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		HouseholdHeadScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		HouseholdHeadScroller.setSize(0, 0);
		HouseholdHeadScroller.setExpandHorizontal(true);
		HouseholdHeadScroller.setExpandVertical(true);

		householdHeadTableViewer = new TableViewer(HouseholdHeadScroller,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		householdHeadTableViewer.setUseHashlookup(true);
		householdHeadTableViewer.addDoubleClickListener(event -> HouseholdHeadPopup(getDisplay()));

		final ViewerFilter[] filters = new ViewerFilter[3];
		filters[0] = HouseholdHeadPlaceFilter.getInstance();
		filters[1] = HouseholdHeadNameFilter.getInstance();
		filters[2] = HouseholdHeadTypeFilter.getInstance();
		householdHeadTableViewer.setFilters(filters);

		householdHeadTableViewer.setComparator(new HouseholdHeadComparator());
		householdHeadTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		householdHeadTable = householdHeadTableViewer.getTable();
		householdHeadTable.setLinesVisible(true);
		householdHeadTable.setHeaderVisible(true);

		final TableViewerColumn HouseholdHeadTableViewerColumn = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		HouseholdHeadTableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getHeadId();
			}
		});
		final TableColumn tblclmnId = HouseholdHeadTableViewerColumn.getColumn();
		tblclmnId.setWidth(80);
		tblclmnId.setText("Husbonds ID");

		final TableViewerColumn HouseholdHeadTableViewerColumn_1 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnFornavn = HouseholdHeadTableViewerColumn_1.getColumn();
		tblclmnFornavn.setWidth(100);
		tblclmnFornavn.setText("Husbond");
		HouseholdHeadTableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getHeadName();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_2 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnEfternavn = HouseholdHeadTableViewerColumn_2.getColumn();
		tblclmnEfternavn.setWidth(73);
		tblclmnEfternavn.setText("Dato");
		HouseholdHeadTableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getEventDate().toString();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_6 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnDetaljer = HouseholdHeadTableViewerColumn_6.getColumn();
		tblclmnDetaljer.setWidth(100);
		tblclmnDetaljer.setText("Fra");
		HouseholdHeadTableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getNote();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_3 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnFlyttedato = HouseholdHeadTableViewerColumn_3.getColumn();
		tblclmnFlyttedato.setWidth(123);
		tblclmnFlyttedato.setText("Til");
		HouseholdHeadTableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getPlace();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_5 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnFra = HouseholdHeadTableViewerColumn_5.getColumn();
		tblclmnFra.setWidth(87);
		tblclmnFra.setText("Tjenende ID");
		HouseholdHeadTableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getRelocatorId();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_8 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnRId = HouseholdHeadTableViewerColumn_8.getColumn();
		tblclmnRId.setWidth(148);
		tblclmnRId.setText("Tjenende navn");
		HouseholdHeadTableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getRelocatorName();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_4 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnTil = HouseholdHeadTableViewerColumn_4.getColumn();
		tblclmnTil.setWidth(270);
		tblclmnTil.setText("Detaljer");
		HouseholdHeadTableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getSourceDetail();
			}
		});
		final TableViewerColumn HouseholdHeadTableViewerColumn_9 = new TableViewerColumn(householdHeadTableViewer,
				SWT.NONE);
		final TableColumn tblclmnType = HouseholdHeadTableViewerColumn_9.getColumn();
		tblclmnType.setWidth(70);
		tblclmnType.setText("Type");
		HouseholdHeadTableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final HouseholdHeadModel rr = (HouseholdHeadModel) element;
				return rr.getEventType();
			}
		});

		HouseholdHeadScroller.setContent(householdHeadTable);
		HouseholdHeadScroller.setMinSize(householdHeadTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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

		final HouseholdHeadModel[] input = new HouseholdHeadModel[0];
		householdHeadTableViewer.setInput(input);
		clearFilters();
	}

	/**
	 *
	 */
	private void clearFilters() {
		HouseholdHeadPlaceFilter.getInstance().setSearchText("");
		HouseholdHeadNameFilter.getInstance().setSearchText("");
		HouseholdHeadTypeFilter.getInstance().setSearchText("");
		txtHouseholdPlace.setText("");
		txtHouseholdRelocatorName.setText("");
		txtHouseholdEventType.setText("");
		txtHouseholdEventType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtHouseholdPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtHouseholdRelocatorName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		householdHeadTableViewer.refresh();
	}

	/**
	 * @param display
	 */
	private void HouseholdHeadPopup(Display display) {
		final TableItem[] tia = householdHeadTable.getSelection();
		final TableItem ti = tia[0];

		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 8; i++) {
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

	/**
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 */
	public void populate(String headId) {
		thread = new Thread(() -> {
			if (householdHeadListener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("vejbySchema"),
							props.getProperty("vejbyPath"), props.getProperty("censusSchema"),
							props.getProperty("censusPath"), headId };
					final HouseholdHeadModel[] HouseholdHeadRecords = (HouseholdHeadModel[]) householdHeadListener
							.load(loadArgs);

					Display.getDefault().asyncExec(() -> householdHeadTableViewer.setInput(HouseholdHeadRecords));
					Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
							.setMessage("Husbond er hentet"));
				} catch (final Exception e) {
					Display.getDefault().asyncExec(
							() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e.getMessage()));
				}
			}
		});
		thread.start();
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}

}
