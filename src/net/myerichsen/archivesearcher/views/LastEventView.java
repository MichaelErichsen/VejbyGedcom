package net.myerichsen.archivesearcher.views;

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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
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

import net.myerichsen.archivesearcher.models.LastEventModel;

/**
 * Display all events for a location that are the last events for the given
 * individual to search for burials
 *
 * @author Michael Erichsen
 * @version 8. aug. 2023
 *
 */
public class LastEventView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private Properties props;
	private Thread thread;
	private Text textLocation;

	/**
	 * Create the view
	 *
	 * @param parent
	 * @param style
	 */
	public LastEventView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		new Label(this, SWT.NONE);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		tableViewer = new TableViewer(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> popup(getDisplay()));
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnId = tableViewerColumn.getColumn();
		tblclmnId.setWidth(85);
		tblclmnId.setText("ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getIndividualId();
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnNavn = tableViewerColumn_1.getColumn();
		tblclmnNavn.setWidth(172);
		tblclmnNavn.setText("Navn");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getName();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDato = tableViewerColumn_2.getColumn();
		tblclmnDato.setWidth(72);
		tblclmnDato.setText("Dato");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getDate().toString();
			}
		});

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnType = tableViewerColumn_3.getColumn();
		tblclmnType.setWidth(63);
		tblclmnType.setText("Type");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getType();
			}
		});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnUndertype = tableViewerColumn_4.getColumn();
		tblclmnUndertype.setWidth(85);
		tblclmnUndertype.setText("Undertype");
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getSubType();
			}
		});

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnDetaljer = tableViewerColumn_6.getColumn();
		tblclmnDetaljer.setWidth(494);
		tblclmnDetaljer.setText("Detaljer");
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((LastEventModel) element).getSourceDetail();
			}
		});

		final Composite searchComposite = new Composite(this, SWT.BORDER);
		searchComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label lblSted = new Label(searchComposite, SWT.NONE);
		lblSted.setLayoutData(new RowData(81, SWT.DEFAULT));
		lblSted.setText("Sted");

		textLocation = new Text(searchComposite, SWT.BORDER);
		textLocation.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					if (textLocation.getText().isBlank()) {
						((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
								.setMessage("Indtast venligst et sted og tryk ENTER");
						textLocation.setFocus();
					} else {
						populate(textLocation.getText());
					}
				}
			}
		});
		textLocation.setToolTipText("Benyt store og sm\u00E5 bogstaver. Tryk ENTER for at s\u00F8ge");
		textLocation.setLayoutData(new RowData(729, SWT.DEFAULT));

		final Button btnKopir = new Button(searchComposite, SWT.NONE);
		btnKopir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final TableItem[] array = table.getItems();
				LastEventModel model;
				final StringBuilder sb = new StringBuilder("");

				for (final TableItem tableItem : array) {
					model = (LastEventModel) tableItem.getData();
					sb.append(model.toString() + "\n");
				}

				if (sb.length() > 0) {
					final Clipboard clipboard = new Clipboard(getDisplay());
					final TextTransfer textTransfer = TextTransfer.getInstance();
					clipboard.setContents(new String[] { sb.toString() }, new Transfer[] { textTransfer });
					clipboard.dispose();
				}
			}
		});
		btnKopir.setText("Kopi\u00E9r");

		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param location
	 */
	private void getInput(String location) {
		try {
			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.getIndicator().setVisible(true));
			final LastEventModel[] array = LastEventModel.load(props.getProperty("parishSchema"),
					props.getProperty("parishPath"), location);

			Display.getDefault().asyncExec(() -> tableViewer.setInput(array));
			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.setMessage("Sidste h�ndelser i " + location + " er hentet"));
		} catch (final Exception e) {
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setErrorMessage(e.getMessage(), e));
		}
	}

	/**
	 * Populate the census duplicate tab from the database
	 */
	public void populate(String location) {
		thread = new Thread(() -> getInput(location));
		thread.start();
	}

	/**
	 * @param display
	 */
	private void popup(Display display) {
		final TableItem[] tia = table.getSelection();
		final String string = ((LastEventModel) tia[0].getData()).toString() + "\n";

		final MessageDialog dialog = new MessageDialog(getShell(), "Sidste h�ndelse", null, string,
				MessageDialog.INFORMATION, new String[] { "OK", "S�g efter" }, 0);
		final int open = dialog.open();

		if (open == 1) {
			final String siblingsId = ((LastEventModel) tia[0].getData()).getIndividualId();
			final ArchiveSearcher greatGrandParent = (ArchiveSearcher) getParent().getParent().getParent();
			greatGrandParent.getSearchId().setText(siblingsId);
			greatGrandParent.searchById(null);
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
