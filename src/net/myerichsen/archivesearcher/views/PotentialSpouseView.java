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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.myerichsen.archivesearcher.models.PotentialSpouseModel;
import net.myerichsen.archivesearcher.populators.ASPopulator;
import net.myerichsen.archivesearcher.populators.PotentialSpousePopulator;

/**
 * View of potential spouses
 *
 * @author Michael Erichsen
 * @version 10. jul. 2023
 *
 */
public class PotentialSpouseView extends Composite {
	private ASPopulator listener;
	private Properties props;
	private Thread thread;
	private PotentialSpouseModel[] array;
	private Table table;
	private TableViewer tableViewer;

	/**
	 * Create the view
	 *
	 * @param parent
	 * @param style
	 */
	public PotentialSpouseView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		listener = new PotentialSpousePopulator();

		final ScrolledComposite scroller = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scroller.setExpandHorizontal(true);
		scroller.setExpandVertical(true);

		tableViewer = new TableViewer(scroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.addDoubleClickListener(event -> displayPopup());

		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableViewerColumn tableViewerColumnNavn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKildenavn = tableViewerColumnNavn.getColumn();
		tblclmnKildenavn.setWidth(200);
		tblclmnKildenavn.setText("Ægtefælle");
		tableViewerColumnNavn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getKildenavn();
			}
		});

		final TableViewerColumn tableViewerColumnKoen = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnKn = tableViewerColumnKoen.getColumn();
		tblclmnKn.setWidth(40);
		tblclmnKn.setText("K\u00F8n");
		tableViewerColumnKoen.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getKoen();
			}
		});

		final TableViewerColumn tableViewerColumnSted = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdested = tableViewerColumnSted.getColumn();
		tblclmnFdested.setWidth(350);
		tblclmnFdested.setText("F\u00F8dested");
		tableViewerColumnSted.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getKildefoedested();
			}
		});

		final TableViewerColumn tableViewerColumnDato = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdedato = tableViewerColumnDato.getColumn();
		tblclmnFdedato.setWidth(75);
		tblclmnFdedato.setText("F\u00F8dedato");
		tableViewerColumnDato.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getFoedt_kildedato();
			}
		});

		final TableViewerColumn tableViewerSourceType = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSourceType = tableViewerSourceType.getColumn();
		tblclmnSourceType.setWidth(110);
		tblclmnSourceType.setText("Kilde");
		tableViewerSourceType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getSourceType();
			}
		});

		final TableViewerColumn tableViewerColumnIdListe = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnIdList = tableViewerColumnIdListe.getColumn();
		tblclmnIdList.setWidth(300);
		tblclmnIdList.setText("Mulige ID'er");
		tableViewerColumnIdListe.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PotentialSpouseModel) element).getId();
			}
		});

		scroller.setContent(table);
		scroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Clear the table
	 */
	public void clear() {
		if (thread != null) {
			thread.interrupt();
		}

		tableViewer.setInput(new PotentialSpouseModel[0]);
	}

	/**
	 * Display the popup
	 */
	private void displayPopup() {
		try {
			popup();
		} catch (final SQLException e1) {
			((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e1.getMessage());
		}
	}

	/**
	 * Get the data
	 *
	 * @param id
	 */
	private void getInput(String id) {
		if (listener != null) {
			try {
				final String[] loadArgs = new String[] { props.getProperty("parishSchema"),
						props.getProperty("parishPath"), props.getProperty("censusSchema"),
						props.getProperty("censusPath"), id };

				array = (PotentialSpouseModel[]) listener.load(loadArgs);

				Display.getDefault().asyncExec(() -> tableViewer.setInput(array));

				Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent())
						.setMessage("Mulige ægtefæller er hentet"));
			} catch (final Exception e) {
				Display.getDefault().asyncExec(
						() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setMessage(e.getMessage()));
			}
		}
	}

	/**
	 * Populate the table
	 *
	 * @param id
	 * @throws SQLException
	 */
	public void populate(String id) throws SQLException {
		thread = new Thread(() -> getInput(id));
		thread.start();
	}

	/**
	 * Create the popup
	 *
	 * @throws SQLException
	 */
	private void popup() throws SQLException {
		final TableItem[] tia = table.getSelection();
		final PotentialSpouseModel model = (PotentialSpouseModel) tia[0].getData();
		final StringBuilder sb = new StringBuilder(model.toString());
		sb.append("\n\n");

		String[] buttonArray;
		final String spouseId = ((PotentialSpouseModel) tia[0].getData()).getId();
		final String[] idArray = spouseId.split(",");

		if (idArray.length > 1) {
			buttonArray = new String[] { "OK", "Kopier", "Søg efter første ID" };
		} else {
			buttonArray = new String[] { "OK", "Kopier" };
		}

		final MessageDialog dialog = new MessageDialog(getShell(), "Folketællinger", null, sb.toString(),
				MessageDialog.INFORMATION, buttonArray, 0);
		final int open = dialog.open();

		if (open == 1) {
			final Clipboard clipboard = new Clipboard(getDisplay());
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { sb.toString() }, new Transfer[] { textTransfer });
			clipboard.dispose();
		} else if (open == 2) {
			final ArchiveSearcher grandParent = (ArchiveSearcher) getParent().getParent();
			grandParent.getSearchId().setText(idArray[0]);
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
