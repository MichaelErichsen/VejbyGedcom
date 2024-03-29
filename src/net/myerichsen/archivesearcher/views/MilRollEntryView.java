package net.myerichsen.archivesearcher.views;

import java.math.BigDecimal;
import java.sql.Date;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.archivesearcher.comparators.MilrollComparator;
import net.myerichsen.archivesearcher.dialogs.MilRollEntryDialog;
import net.myerichsen.archivesearcher.dialogs.MilRollTreeDialog;
import net.myerichsen.archivesearcher.filters.MilrollCountyFilter;
import net.myerichsen.archivesearcher.filters.MilrollIDFilter;
import net.myerichsen.archivesearcher.filters.MilrollNameFilter;
import net.myerichsen.archivesearcher.filters.MilrollYearFilter;
import net.myerichsen.archivesearcher.models.MilRollEntryModel;

/**
 * Military roll entry view
 *
 * @author Michael Erichsen
 * @version 8. aug. 2023
 *
 */

public class MilRollEntryView extends Composite {
	private Text txtMilrollYear;
	private Text txtMilrollCounty;
	private Text txtMilrollName;
	private Text txtMilrollId;
	private TableViewer tableViewer;
	private Table table;
	private Properties props;
	private Thread thread;
	private Shell shell;

	/**
	 * Create the view
	 *
	 * @param parent
	 * @param style
	 */
	public MilRollEntryView(Composite parent, int style) {
		super(parent, style);
		shell = getShell();
		setLayout(new GridLayout(1, false));

		final Composite filterComposite = new Composite(this, SWT.BORDER);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		filterComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label aLabel = new Label(filterComposite, SWT.NONE);
		aLabel.setText("Filtre: \u00C5r");

		txtMilrollYear = new Text(filterComposite, SWT.BORDER);
		txtMilrollYear.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMilrollYear.getText().length() > 0) {
					txtMilrollYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtMilrollYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				MilrollYearFilter.getInstance().setSearchText(txtMilrollYear.getText());
				tableViewer.refresh();
			}
		});

		final Label lblAmt = new Label(filterComposite, SWT.NONE);
		lblAmt.setText("Amt");

		txtMilrollCounty = new Text(filterComposite, SWT.BORDER);
		txtMilrollCounty.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMilrollCounty.getText().length() > 0) {
					txtMilrollCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtMilrollCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				MilrollCountyFilter.getInstance().setSearchText(txtMilrollCounty.getText());
				tableViewer.refresh();
			}
		});

		final Label lblNavn = new Label(filterComposite, SWT.NONE);
		lblNavn.setText("Navn");

		txtMilrollName = new Text(filterComposite, SWT.BORDER);
		txtMilrollName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMilrollName.getText().length() > 0) {
					txtMilrollName.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtMilrollName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				MilrollNameFilter.getInstance().setSearchText(txtMilrollName.getText());
				tableViewer.refresh();
			}
		});

		final Label lblId = new Label(filterComposite, SWT.NONE);
		lblId.setText("ID");

		txtMilrollId = new Text(filterComposite, SWT.BORDER);
		txtMilrollId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMilrollId.getText().length() > 0) {
					txtMilrollId.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				} else {
					txtMilrollId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				}
				MilrollIDFilter.getInstance().setSearchText(txtMilrollId.getText());
				tableViewer.refresh();
			}
		});

		final Button btnRydFelterneMilroll = new Button(filterComposite, SWT.NONE);
		btnRydFelterneMilroll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFilters();
			}
		});
		btnRydFelterneMilroll.setText("Ryd felterne");

		final ScrolledComposite scrolledCompotise = new ScrolledComposite(this,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledCompotise.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledCompotise.setSize(0, 0);
		scrolledCompotise.setExpandHorizontal(true);
		scrolledCompotise.setExpandVertical(true);

		tableViewer = new TableViewer(scrolledCompotise, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.setUseHashlookup(true);
		tableViewer.addDoubleClickListener(event -> {
			try {
				popup();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		});

		final ViewerFilter[] filters = new ViewerFilter[4];
		filters[0] = MilrollCountyFilter.getInstance();
		filters[1] = MilrollNameFilter.getInstance();
		filters[2] = MilrollIDFilter.getInstance();
		filters[3] = MilrollYearFilter.getInstance();
		tableViewer.setFilters(filters);
		tableViewer.setComparator(new MilrollComparator());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableViewerColumn MilrollTableVieverColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAmt = MilrollTableVieverColumn_1.getColumn();
		tblclmnAmt.setWidth(75);
		tblclmnAmt.setText("Amt");
		MilrollTableVieverColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getAmt();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmAar = MilrollTableVieverColumn.getColumn();
		tblclmAar.setWidth(50);
		tblclmAar.setText("\u00C5r");
		MilrollTableVieverColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getAar());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLitra = MilrollTableVieverColumn_2.getColumn();
		tblclmnLitra.setWidth(40);
		tblclmnLitra.setText("Litra");
		MilrollTableVieverColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getLitra();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnRulletype = MilrollTableVieverColumn_4.getColumn();
		tblclmnRulletype.setWidth(75);
		tblclmnRulletype.setText("Rulletype");
		MilrollTableVieverColumn_4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getRulletype();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLaegdnr = MilrollTableVieverColumn_9.getColumn();
		tblclmnLaegdnr.setWidth(40);
		tblclmnLaegdnr.setText("L\u00E6gd nr.");
		MilrollTableVieverColumn_9.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getLaegdnr());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSogn = MilrollTableVieverColumn_3.getColumn();
		tblclmnSogn.setWidth(75);
		tblclmnSogn.setText("Sogn");
		MilrollTableVieverColumn_3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getSogn();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_prevlaegdid = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnlprevlaegdid = MilrollTableVieverColumn_prevlaegdid.getColumn();
		tblclmnlprevlaegdid.setWidth(40);
		tblclmnlprevlaegdid.setText("Forr. l\u00E6gd ID");
		MilrollTableVieverColumn_prevlaegdid.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getPrevLaegdId());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_glloebenr = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnglloebenr = MilrollTableVieverColumn_glloebenr.getColumn();
		tblclmnglloebenr.setWidth(36);
		tblclmnglloebenr.setText("Forr. l\u00F8benr.\u00A8");
		MilrollTableVieverColumn_glloebenr.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getPrevLoebeNr());
			}
		});

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnLgdId = tableViewerColumn.getColumn();
		tblclmnLgdId.setWidth(46);
		tblclmnLgdId.setText("L\u00E6gd ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getLaegdId());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_loebenr = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnlloebenr = MilrollTableVieverColumn_loebenr.getColumn();
		tblclmnlloebenr.setWidth(40);
		tblclmnlloebenr.setText("L\u00F8benr.");
		MilrollTableVieverColumn_loebenr.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getLoebeNr());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFader = MilrollTableVieverColumn_5.getColumn();
		tblclmnFader.setWidth(81);
		tblclmnFader.setText("Fader");
		MilrollTableVieverColumn_5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getFader();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnSoen = MilrollTableVieverColumn_6.getColumn();
		tblclmnSoen.setWidth(75);
		tblclmnSoen.setText("S\u00F8n");
		MilrollTableVieverColumn_6.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getSoen();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_13 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFdested = MilrollTableVieverColumn_13.getColumn();
		tblclmnFdested.setWidth(75);
		tblclmnFdested.setText("F\u00F8dested");
		MilrollTableVieverColumn_13.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getFoedeSted();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_9x = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAlder = MilrollTableVieverColumn_9x.getColumn();
		tblclmnAlder.setWidth(40);
		tblclmnAlder.setText("Alder");
		MilrollTableVieverColumn_9x.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return Integer.toString(((MilRollEntryModel) element).getAlder());
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnStoerrelse = MilrollTableVieverColumn_7.getColumn();
		tblclmnStoerrelse.setWidth(40);
		tblclmnStoerrelse.setText("Str. i '\"");
		MilrollTableVieverColumn_7.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getStoerrelseITommer().toString();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnOphold = MilrollTableVieverColumn_8.getColumn();
		tblclmnOphold.setWidth(56);
		tblclmnOphold.setText("Ophold");
		MilrollTableVieverColumn_8.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getOphold();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_10 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnAnmaerkinger = MilrollTableVieverColumn_10.getColumn();
		tblclmnAnmaerkinger.setWidth(75);
		tblclmnAnmaerkinger.setText("Anm\u00E6rkninger");
		MilrollTableVieverColumn_10.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getAnmaerkninger();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_11 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnFoedt = MilrollTableVieverColumn_11.getColumn();
		tblclmnFoedt.setWidth(75);
		tblclmnFoedt.setText("F\u00F8dt");
		MilrollTableVieverColumn_11.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final Date foedt = ((MilRollEntryModel) element).getFoedt();
				return foedt == null ? "" : foedt.toString();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_12 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmngedcomId = MilrollTableVieverColumn_12.getColumn();
		tblclmngedcomId.setWidth(75);
		tblclmngedcomId.setText("GEDCOM ID");
		MilrollTableVieverColumn_12.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getGedcomId();
			}
		});

		final TableViewerColumn MilrollTableVieverColumn_15 = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tblclmnNavn = MilrollTableVieverColumn_15.getColumn();
		tblclmnNavn.setWidth(100);
		tblclmnNavn.setText("Navn");
		MilrollTableVieverColumn_15.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MilRollEntryModel) element).getNavn();
			}
		});

		scrolledCompotise.setContent(table);
		scrolledCompotise.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		final Composite buttonComposite = new Composite(this, SWT.BORDER);
		buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

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
		btnFind.setText("Hent indtastninger");

		final Button btnLgdsrulleliste = new Button(buttonComposite, SWT.NONE);
		btnLgdsrulleliste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ArchiveSearcher) ((TabFolder) getParent()).getParent()).laegdsRulleListe();
			}
		});
		btnLgdsrulleliste.setText("L\u00E6gdsrulleliste");

		final Button btnIndtastLgdsruller = new Button(buttonComposite, SWT.NONE);
		btnIndtastLgdsruller.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MilRollEntryDialog.main(props);
			}

		});
		btnIndtastLgdsruller.setText("Indtast l\u00E6gdsruller");
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
		tableViewer.setInput(new MilRollEntryModel[0]);
		clearFilters();
	}

	/**
	 * Clear filters
	 */
	private void clearFilters() {
		MilrollCountyFilter.getInstance().setSearchText("");
		MilrollNameFilter.getInstance().setSearchText("");
		MilrollIDFilter.getInstance().setSearchText("");
		MilrollYearFilter.getInstance().setSearchText("");
		txtMilrollCounty.setText("");
		txtMilrollName.setText("");
		txtMilrollId.setText("");
		txtMilrollYear.setText("");
		txtMilrollCounty.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtMilrollName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtMilrollId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtMilrollYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tableViewer.refresh();
	}

	/**
	 *
	 */
	private void getInput() {
		try {
			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.getIndicator().setVisible(true));
			final MilRollEntryModel[] MilrollRecords = MilRollEntryModel.load(props.getProperty("milrollPath"),
					props.getProperty("milrollSchema"));

			Display.getDefault().asyncExec(() -> tableViewer.setInput(MilrollRecords));

			Display.getDefault().asyncExec(() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent().getParent())
					.setMessage("L�gdsruller er hentet"));
		} catch (final Exception e) {
			Display.getDefault().asyncExec(
					() -> ((ArchiveSearcher) ((TabFolder) getParent()).getParent()).setErrorMessage(e.getMessage(), e));
		}
	}

	/**
	 * Populate Milroll table
	 *
	 * @throws SQLException SQL exception
	 */
	public void populate() throws SQLException {
		thread = new Thread(this::getInput);
		thread.start();
	}

	/**
	 * Create the military roll popup
	 *
	 * @throws SQLException
	 */
	private void popup() throws SQLException {
		final TableItem[] tia = table.getSelection();
		final String string = ((MilRollEntryModel) tia[0].getData()).toString();

		final MessageDialog dialog = new MessageDialog(getShell(), "L�gdsruller", null, string,
				MessageDialog.INFORMATION, new String[] { "OK", "Kopier", "S�g efter", "Vis tidligere for denne" }, 0);
		final int open = dialog.open();

		switch (open) {
		case 1: {
			final String h = ((MilRollEntryModel) tia[0].getData()).getStoerrelseITommer() == new BigDecimal(0) ? ""
					: ", H�jde i tommer " + ((MilRollEntryModel) tia[0].getData()).getStoerrelseITommer();
			final String a = ((MilRollEntryModel) tia[0].getData()).getAnmaerkninger().length() == 0 ? ""
					: ", " + ((MilRollEntryModel) tia[0].getData()).getAnmaerkninger().trim();
			final String s = ((MilRollEntryModel) tia[0].getData()).getAmt().trim() + " amt "
					+ ((MilRollEntryModel) tia[0].getData()).getAar()
					+ ((MilRollEntryModel) tia[0].getData()).getLitra() + ", l�gd "
					+ ((MilRollEntryModel) tia[0].getData()).getLaegdnr() + ", L�benr. "
					+ ((MilRollEntryModel) tia[0].getData()).getLoebeNr() + ", Fader "
					+ ((MilRollEntryModel) tia[0].getData()).getFader().trim() + ", F�dested "
					+ ((MilRollEntryModel) tia[0].getData()).getFoedeSted().trim() + ", Alder "
					+ ((MilRollEntryModel) tia[0].getData()).getAlder() + h + ", Opholdssted "
					+ ((MilRollEntryModel) tia[0].getData()).getOphold().trim() + a;
			final Clipboard clipboard = new Clipboard(getDisplay());
			final TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new String[] { s }, new Transfer[] { textTransfer });
			clipboard.dispose();
			break;
		}
		case 2: {
			final String id = ((MilRollEntryModel) tia[0].getData()).getGedcomId();
			if (id.length() > 0 && id.startsWith("@I")) {
				final ArchiveSearcher greatGrandParent = (ArchiveSearcher) getParent().getParent().getParent();
				greatGrandParent.getSearchId().setText(id);
				greatGrandParent.searchById(null);
			}
			break;
		}
		case 3: {
			final MilRollTreeDialog tree = new MilRollTreeDialog(props, shell,
					((MilRollEntryModel) tia[0].getData()).getLaegdId() + "",
					((MilRollEntryModel) tia[0].getData()).getLoebeNr() + "");
			tree.open();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}
}
