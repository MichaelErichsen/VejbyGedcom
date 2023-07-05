package net.myerichsen.archivesearcher.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Find possible ID's for a phonetic name
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
 *
 */
public class MilRollIDialog extends Dialog {
	private Table table;
	private String[] array;
	private String selectedIndividual;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell Parent shell
	 */
	public MilRollIDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
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

		final TableViewer soenTableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		soenTableViewer.addSelectionChangedListener(event -> {
			final String s = event.getSelection().toString();
			final String[] split = s.split(",");
			selectedIndividual = split[0].replace("[", "").trim();
		});
		soenTableViewer.setUseHashlookup(true);
		soenTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		table = soenTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(soenTableViewer, SWT.NONE);
		final TableColumn tblclmnMuligeSnner = tableViewerColumn.getColumn();
		tblclmnMuligeSnner.setWidth(600);
		tblclmnMuligeSnner.setText("Mulige s\u00F8nner");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final String cr = (String) element;
				return cr;
			}
		});
		soenTableViewer.setInput(array);
		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	/**
	 * @return the selectedIndividual
	 */
	public String getSelectedIndividual() {
		return selectedIndividual;
	}

	/**
	 * @param array the array to set
	 */
	public void setInput(String[] array) {
		this.array = array;

	}

	/**
	 * @param selectedIndividual the selectedIndividual to set
	 */
	public void setSelectedIndividual(String selectedIndividual) {
		this.selectedIndividual = selectedIndividual;
	}
}
