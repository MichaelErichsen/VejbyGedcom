package net.myerichsen.gedcom.db.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael Erichsen
 * @version 8. maj 2023
 *
 */
public class KeystrokeDialog extends Dialog {

	/**
	 * Constructor
	 *
	 * @param parentShell
	 */
	public KeystrokeDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Hj�lpetaster til arkivs�gningsprogrammet");
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
		final GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 10;
		gl_container.marginBottom = 5;
		gl_container.marginRight = 5;
		gl_container.marginLeft = 5;
		container.setLayout(gl_container);

		final Label lblA = new Label(container, SWT.NONE);
		lblA.setText("Alt+A N�ste (L�gdsrulleindtastning)");
		final Label lblB = new Label(container, SWT.NONE);
		lblB.setText("Alt+B K�benhavns begravelsesregister faneblad");
		final Label lblC = new Label(container, SWT.NONE);
		lblC.setText("Alt+C S�g efter GEDCOM ID (L�gdsrulleindtastning)");
		final Label lblD = new Label(container, SWT.NONE);
		lblD.setText("Alt+D Folket�llingsdubletter faneblad");
		final Label lblE = new Label(container, SWT.NONE);
		lblE.setText("Alt+E Efterkommere faneblad");
		final Label lblF = new Label(container, SWT.NONE);
		lblF.setText("Alt+F S�g p� for�ldre");
		final Label lblG = new Label(container, SWT.NONE);
		lblG.setText("Alt+G L�gdsruller faneblad");
		final Label lblH = new Label(container, SWT.NONE);
		lblH.setText("Alt+H Husbond faneblad");
		final Label lblI = new Label(container, SWT.NONE);
		lblI.setText("Alt+I S�g p� ID ");
		final Label lblK = new Label(container, SWT.NONE);
		lblK.setText("Alt+K Skifter faneblad");
		final Label lblL = new Label(container, SWT.NONE);
		lblL.setText("Alt+L Flytninger faneblad");
		final Label lblM = new Label(container, SWT.NONE);
		lblM.setText("Alt+M Gem indtastning (L�gdsrulleindtastning)");
		final Label lblN = new Label(container, SWT.NONE);
		lblN.setText("Alt+N S�g efter navn");
		final Label lblO = new Label(container, SWT.NONE);
		lblO.setText("Alt+O Folket�llinger faneblad");
		final Label lblP = new Label(container, SWT.NONE);
		lblP.setText("Alt+P Person faneblad");
		final Label lblR = new Label(container, SWT.NONE);
		lblR.setText("Alt+R Ryd felterne og stop s�gning");
		final Label lblS = new Label(container, SWT.NONE);
		lblS.setText("Alt+S S�skende faneblad");
		final Label lblT = new Label(container, SWT.NONE);
		lblT.setText("Alt+T Politiets registerblade faneblad");

		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(478, 555);
	}

}
