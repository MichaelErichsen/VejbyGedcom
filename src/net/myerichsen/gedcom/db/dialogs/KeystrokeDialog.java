package net.myerichsen.gedcom.db.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author Michael Erichsen
 * @version 10. maj 2023
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
		newShell.setText("Hjælpetaster til arkivsøgningsprogrammet");
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
		final GridLayout gl_container = new GridLayout(2, false);
		gl_container.verticalSpacing = 10;
		gl_container.marginBottom = 5;
		gl_container.marginRight = 5;
		gl_container.marginLeft = 5;
		container.setLayout(gl_container);

		Label lblHovedbillede = new Label(container, SWT.NONE);
		lblHovedbillede.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblHovedbillede.setText("Hovedbillede");

		Label lblLgdsrulleindtastning = new Label(container, SWT.NONE);
		lblLgdsrulleindtastning.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLgdsrulleindtastning.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblLgdsrulleindtastning.setText("L\u00E6gdsrulleindtastning");
		final Label lblB = new Label(container, SWT.NONE);
		lblB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblB.setText("Alt+B Københavns begravelsesregister faneblad");

		Label lblAlteRydIndtastningsfelter = new Label(container, SWT.NONE);
		lblAlteRydIndtastningsfelter.setText("Alt+E Ryd indtastningsfelter");
		final Label lblD = new Label(container, SWT.NONE);
		lblD.setText("Alt+D Folketællingsdubletter faneblad");

		final Label lblLF = new Label(container, SWT.NONE);
		lblLF.setText("Alt+F Frem til næste rulle");
		final Label lblE = new Label(container, SWT.NONE);
		lblE.setText("Alt+E Efterkommere faneblad");
		final Label lblLG = new Label(container, SWT.NONE);
		lblLG.setText("Alt+G Gem rulle");
		final Label lblF = new Label(container, SWT.NONE);
		lblF.setText("Alt+F Søg på forældre");
		final Label lblLH = new Label(container, SWT.NONE);
		lblLH.setText("Alt+H Hent med nyt løbenr.");
		final Label lblG = new Label(container, SWT.NONE);
		lblG.setText("Alt+G Lægdsruller faneblad");
		final Label lblLL = new Label(container, SWT.NONE);
		lblLL.setText("Alt+L Hent med gl. løbenr.");
		final Label lblH = new Label(container, SWT.NONE);
		lblH.setText("Alt+H Husbond faneblad");
		final Label lblLM = new Label(container, SWT.NONE);
		lblLM.setText("Alt+M Gem indtastning");
		final Label lblI = new Label(container, SWT.NONE);
		lblI.setText("Alt+I Søg på ID ");
		final Label lblLN = new Label(container, SWT.NONE);
		lblLN.setText("Alt+N Ryd til næste indtastning");
		final Label lblK = new Label(container, SWT.NONE);
		lblK.setText("Alt+K Skifter faneblad");
		final Label lblLR = new Label(container, SWT.NONE);
		lblLR.setText("Alt+R Ret indtastning");
		final Label lblL = new Label(container, SWT.NONE);
		lblL.setText("Alt+L Flytninger faneblad");
		final Label lblLS = new Label(container, SWT.NONE);
		lblLS.setText("Alt+S Søg efter GEDCOM ID");
		final Label lblN = new Label(container, SWT.NONE);
		lblN.setText("Alt+N Søg efter navn");
		final Label lblLT = new Label(container, SWT.NONE);
		lblLT.setText("Alt+T Tilbage til forrige rulle");
		final Label lblO = new Label(container, SWT.NONE);
		lblO.setText("Alt+O Folketællinger faneblad");
		new Label(container, SWT.NONE);
		final Label lblP = new Label(container, SWT.NONE);
		lblP.setText("Alt+P Person faneblad");
		new Label(container, SWT.NONE);
		final Label lblR = new Label(container, SWT.NONE);
		lblR.setText("Alt+R Ryd felterne og stop søgning");
		new Label(container, SWT.NONE);
		final Label lblS = new Label(container, SWT.NONE);
		lblS.setText("Alt+S Søskende faneblad");
		new Label(container, SWT.NONE);
		final Label lblT = new Label(container, SWT.NONE);
		lblT.setText("Alt+T Politiets registerblade faneblad");
		new Label(container, SWT.NONE);

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
