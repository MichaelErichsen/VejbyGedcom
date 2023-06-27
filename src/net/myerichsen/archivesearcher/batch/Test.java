package net.myerichsen.archivesearcher.batch;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael Erichsen
 * @version 27. jun. 2023
 *
 */
public class Test {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setSize(300, 100);
		final ProgressIndicator indicator = new ProgressIndicator(shell, SWT.HORIZONTAL);
		indicator.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		indicator.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		shell.open();
		indicator.beginTask(200);
		indicator.showNormal();
		indicator.worked(50);
		indicator.showPaused();
		indicator.worked(50);
		indicator.showError();
		indicator.worked(50);
		indicator.done();
		display.dispose();
	}

}
