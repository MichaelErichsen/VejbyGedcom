package net.myerichsen.archivesearcher.views;

import java.util.Properties;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

import net.myerichsen.archivesearcher.comparators.DescendantComparator;
import net.myerichsen.archivesearcher.models.DescendantModel;

/**
 * @author Michael Erichsen
 * @version 8. aug. 2023
 *
 */
public class DescendantCounterView extends Composite {
	private Table table;
	private TableViewer tableViewer;
	private Properties props;
	private Thread thread;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public DescendantCounterView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		final ScrolledComposite descendantScroller = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		descendantScroller.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		descendantScroller.setSize(0, 0);
		descendantScroller.setExpandHorizontal(true);
		descendantScroller.setExpandVertical(true);

		tableViewer = new TableViewer(descendantScroller, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.setComparator(new DescendantComparator());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAntalEfterkommere = tableViewerColumn.getColumn();
		tblclmnAntalEfterkommere.setWidth(143);
		tblclmnAntalEfterkommere.setText("Antal efterkommere");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((DescendantModel) element).getDescendantCount());
			}
		});

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnId = tableViewerColumn_1.getColumn();
		tblclmnId.setWidth(58);
		tblclmnId.setText("ID");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DescendantModel) element).getId();
			}
		});

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnNavn = tableViewerColumn_2.getColumn();
		tblclmnNavn.setWidth(300);
		tblclmnNavn.setText("Navn");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DescendantModel) element).getName();
			}
		});

		final Composite buttonComposite = new Composite(this, SWT.BORDER);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label lblDanListeOver = new Label(buttonComposite, SWT.NONE);
		lblDanListeOver.setText("Dan liste over personer uden for\u00E6ldre med flest efterkommere");

		final Button btnFind = new Button(buttonComposite, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populate();
			}
		});
		btnFind.setText("Find");

		descendantScroller.setContent(table);
		descendantScroller.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
		tableViewer.setInput(new DescendantModel[0]);
	}

	/**
	 *
	 */
	private void getInput() {
		try {
			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.getIndicator().setVisible(true));
			final DescendantModel[] array = DescendantModel.load(props.getProperty("gedcomFilePath"));

			Display.getDefault().asyncExec(() -> tableViewer.setInput(array));

			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.setMessage("Efterkommere er hentet"));
		} catch (final Exception e) {
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setErrorMessage(e.getMessage(), e));
		}
	}

	/**
	 * Populate the view
	 */
	public void populate() {
		thread = new Thread(this::getInput);
		thread.start();
	}

	/**
	 * @param props the properties to set
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
