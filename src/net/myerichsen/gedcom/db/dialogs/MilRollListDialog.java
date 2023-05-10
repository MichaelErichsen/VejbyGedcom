package net.myerichsen.gedcom.db.dialogs;

import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.myerichsen.gedcom.db.models.MilrollListModel;
import net.myerichsen.gedcom.db.populators.ASPopulator;
import net.myerichsen.gedcom.db.populators.MilrollListPopulator;
import net.myerichsen.gedcom.db.views.ArchiveSearcher;
import net.myerichsen.gedcom.util.MillrollListAarEditingSupport;
import net.myerichsen.gedcom.util.MillrollListAmtEditingSupport;
import net.myerichsen.gedcom.util.MillrollListLaegdIdEditingSupport;
import net.myerichsen.gedcom.util.MillrollListLaegdNrEditingSupport;
import net.myerichsen.gedcom.util.MillrollListLitraEditingSupport;
import net.myerichsen.gedcom.util.MillrollListNextLaegdIdEditingSupport;
import net.myerichsen.gedcom.util.MillrollListPrevLaegdIdEditingSupport;
import net.myerichsen.gedcom.util.MillrollListSognEditingSupport;
import net.myerichsen.gedcom.util.MilrollListRulletypeEditingSupport;

/**
 * Military rolls view
 *
 * @author Michael Erichsen
 * @version 10. maj 2023
 *
 */

// TODO Change to tree and make selectable

public class MilRollListDialog extends Dialog {
	private static final int NYTOMLINIE = IDialogConstants.CLIENT_ID + 6;
	private static final int KOPIERLINIE = IDialogConstants.CLIENT_ID + 5;
	private static final int SLETLINIE = IDialogConstants.CLIENT_ID + 4;
	private static final int RETLINIE = IDialogConstants.CLIENT_ID + 3;
	private static final int OPRETLINIE = IDialogConstants.CLIENT_ID + 2;

	private Table table;
	private Properties props;
	private Thread thread;
	private TableViewer tableViewer;
	private ASPopulator listener;
	private final ArchiveSearcher as;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public MilRollListDialog(Shell parentShell) {
		super(parentShell);
		as = (ArchiveSearcher) parentShell;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Lægdsrulleliste");
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		final Button button_1 = createButton(parent, OPRETLINIE, "opret", true);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					opret();
				} catch (final SQLException e1) {
					Display.getDefault().asyncExec(() -> as.setErrorMessage(e1.getMessage()));
				}
			}
		});
		button_1.setText("Opret");

		final Button button_2 = createButton(parent, RETLINIE, "ret", false);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ret();
				} catch (final SQLException e1) {
					Display.getDefault().asyncExec(() -> as.setErrorMessage(e1.getMessage()));
				}
			}
		});
		button_2.setText("Ret");

		final Button button_3 = createButton(parent, SLETLINIE, "slet", false);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					slet();
				} catch (final SQLException e1) {
					Display.getDefault().asyncExec(() -> as.setErrorMessage(e1.getMessage()));
				}
			}
		});
		button_3.setText("Slet");
		final Button button = createButton(parent, KOPIERLINIE, "kopierlinie", false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				kopier();
			}
		});
		button.setText("Kopi\u00E9r linie");

		final Button btnNyTomLinie = createButton(parent, NYTOMLINIE, "nytomlinie", false);
		btnNyTomLinie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.add(new MilrollListModel());
			}
		});
		btnNyTomLinie.setText("Ny tom linie");

		final Button button_5 = createButton(parent, IDialogConstants.OK_ID, "OK", false);
		button_5.setText("OK");
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAmt = tableViewerColumn.getColumn();
		tblclmnAmt.setWidth(100);
		tblclmnAmt.setText("Amt");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return mrlm.getAmt();
			}
		});
		tableViewerColumn.setEditingSupport(new MillrollListAmtEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnr = tableViewerColumn_1.getColumn();
		tblclmnr.setWidth(60);
		tblclmnr.setText("\u00C5r");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return Integer.toString(mrlm.getAar());
			}
		});
		tableViewerColumn_1.setEditingSupport(new MillrollListAarEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLitra = tableViewerColumn_2.getColumn();
		tblclmnLitra.setWidth(60);
		tblclmnLitra.setText("Litra");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return mrlm.getLitra();
			}
		});
		tableViewerColumn_2.setEditingSupport(new MillrollListLitraEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnRulletype = tableViewerColumn_6.getColumn();
		tblclmnRulletype.setWidth(100);
		tblclmnRulletype.setText("Rulletype");
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return mrlm.getRulleType();
			}
		});
		tableViewerColumn_6.setEditingSupport(new MilrollListRulletypeEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLgdNr = tableViewerColumn_3.getColumn();
		tblclmnLgdNr.setWidth(70);
		tblclmnLgdNr.setText("L\u00E6gd nr.");
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return Integer.toString(mrlm.getLaegdNr());
			}
		});
		tableViewerColumn_3.setEditingSupport(new MillrollListLaegdNrEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSogn = tableViewerColumn_7.getColumn();
		tblclmnSogn.setWidth(100);
		tblclmnSogn.setText("Sogn");
		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return mrlm.getSogn();
			}
		});
		tableViewerColumn_7.setEditingSupport(new MillrollListSognEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLgdId = tableViewerColumn_8.getColumn();
		tblclmnLgdId.setWidth(100);
		tblclmnLgdId.setText("L\u00E6gd ID");
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return Integer.toString(mrlm.getLaegdId());
			}
		});
		tableViewerColumn_8.setEditingSupport(new MillrollListLaegdIdEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnNextLgdId = tableViewerColumn_9.getColumn();
		tblclmnNextLgdId.setWidth(100);
		tblclmnNextLgdId.setText("N\u00E6ste l\u00E6gd ID");
		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return Integer.toString(mrlm.getNextLaegdId());
			}
		});
		tableViewerColumn_9.setEditingSupport(new MillrollListNextLaegdIdEditingSupport(tableViewer));

		final TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnPrevLgdId = tableViewerColumn_10.getColumn();
		tblclmnPrevLgdId.setWidth(100);
		tblclmnPrevLgdId.setText("Forrige l\u00E6gd ID");
		tableViewerColumn_10.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				final MilrollListModel mrlm = (MilrollListModel) element;
				return Integer.toString(mrlm.getPrevLaegdId());
			}
		});
		tableViewerColumn_10.setEditingSupport(new MillrollListPrevLaegdIdEditingSupport(tableViewer));

		listener = new MilrollListPopulator();
		populate();
		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(817, 561);
	}

	/**
	 * New empty item with backwards references copied into
	 *
	 */
	protected void kopier() {
		final TableItem[] tia = table.getSelection();

		if (tia.length == 0) {
			Display.getDefault().asyncExec(() -> as.setMessage("Ingen rulle valgt"));
			return;
		}

		final TableItem ti = tia[0];

		final MilrollListModel m = new MilrollListModel();
		m.setAmt(ti.getText(0));
		m.setLaegdNr(Integer.parseInt(ti.getText(3)));
		m.setRulleType(ti.getText(6));
		m.setSogn(ti.getText(7));

		tableViewer.add(m);

		return;
	}

	/**
	 * @throws SQLException
	 *
	 */
	protected int opret() throws SQLException {
		final TableItem[] tia = table.getSelection();

		if (tia.length == 0) {
			System.out.println("Intet valgt");
			return 0;
		}

		final TableItem ti = tia[0];

		final MilrollListModel m = new MilrollListModel();
		m.setAmt(ti.getText(0));
		m.setAar(Integer.parseInt(ti.getText(1)));
		m.setLitra(ti.getText(2));
		m.setRulleType(ti.getText(3));
		m.setLaegdNr(Integer.parseInt(ti.getText(4)));
		m.setSogn(ti.getText(5));
		m.setLaegdId(Integer.parseInt(ti.getText(6)));
		m.setNextLaegdId(Integer.parseInt(ti.getText(7)));
		final int insert = m.insert(props.getProperty("milrollPath"), props.getProperty("milrollSchema"));
		populate();
		return insert;
	}

	/**
	 *
	 */
	public void populate() {
		thread = new Thread(() -> {
			if (listener != null) {
				try {
					final String[] loadArgs = new String[] { props.getProperty("milrollPath"),
							props.getProperty("milrollSchema") };
					final MilrollListModel[] milrolllistRecords = (MilrollListModel[]) listener.load(loadArgs);

					Display.getDefault().asyncExec(() -> tableViewer.setInput(milrolllistRecords));
				} catch (final Exception e) {
					Display.getDefault().asyncExec(() -> as.setErrorMessage(e.getMessage()));
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	/**
	 * @throws SQLException
	 *
	 */
	protected int ret() throws SQLException {
		final TableItem[] tia = table.getSelection();

		if (tia.length == 0) {
			System.out.println("Intet valgt");
			return 0;
		}

		final TableItem ti = tia[0];

		final MilrollListModel m = new MilrollListModel();
		m.setAmt(ti.getText(0));
		m.setAar(Integer.parseInt(ti.getText(1)));
		m.setLitra(ti.getText(2));
		m.setRulleType(ti.getText(3));
		m.setLaegdNr(Integer.parseInt(ti.getText(4)));
		m.setSogn(ti.getText(5));
		m.setLaegdId(Integer.parseInt(ti.getText(6)));
		m.setNextLaegdId(Integer.parseInt(ti.getText(7)));
		final int update = m.update(props.getProperty("milrollPath"), props.getProperty("milrollSchema"));
		populate();
		return update;
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}

	/**
	 * @throws SQLException
	 *
	 */
	protected int slet() throws SQLException {
		final TableItem[] tia = table.getSelection();

		if (tia.length == 0) {
			System.out.println("Intet valgt");
			return 0;
		}

		final TableItem ti = tia[0];

		final MilrollListModel m = new MilrollListModel();
		m.setLaegdId(Integer.parseInt(ti.getText(8)));

		final int slet = m.delete(props.getProperty("milrollPath"), props.getProperty("milrollSchema"));
		populate();
		return slet;
	}
}
