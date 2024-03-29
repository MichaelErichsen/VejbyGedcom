package net.myerichsen.archivesearcher.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import net.myerichsen.archivesearcher.models.MilRollEntryModel;
import net.myerichsen.archivesearcher.providers.MilRollTreeContentProvider;
import net.myerichsen.archivesearcher.providers.MilRollTreeLabelProvider;

/**
 * Display a tree view of connected military roll entries
 *
 * @author Michael Erichsen
 * @version 5. jul. 2023
 *
 */
public class MilRollTreeDialog extends Dialog {
	private final Properties props;
	private final String laegdId;
	private final String loebeNr;

	/**
	 * Constructor
	 *
	 * @param props       Properties
	 * @param parentShell Parent shell
	 * @param laegdId     Military roll id
	 * @param loebeNr     Serial number
	 */
	public MilRollTreeDialog(Properties props, Shell parentShell, String laegdId, String loebeNr) {
		super(parentShell);
		this.props = props;
		this.laegdId = laegdId;
		this.loebeNr = loebeNr;
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
//		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);

		final TreeViewer treeViewer = new TreeViewer(container, SWT.BORDER);
		final Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setLabelProvider(new MilRollTreeLabelProvider());
		treeViewer.setContentProvider(new MilRollTreeContentProvider(props));
		treeViewer.setInput(MilRollEntryModel.select(props, Integer.parseInt(laegdId), Integer.parseInt(loebeNr)));

		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(682, 342);
	}

}
