package net.myerichsen.archivesearcher.dialogs;

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
 * @version 5. jul. 2023
 *
 */
public class HelpDialog extends Dialog {

	/**
	 * Create the dialog.
	 *
	 * @param parentShell Parent shell
	 */
	public HelpDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Hjælp til arkivsøgningsprogrammet");
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

		final Label lbl1 = new Label(container, SWT.NONE);
		lbl1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lbl1.setText("Dette program analyserer databaser med sl\u00E6gtsforskningsoplysninger.");

		final Label lblDatabaserneSkalIndlse = new Label(container, SWT.NONE);
		lblDatabaserneSkalIndlse.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblDatabaserneSkalIndlse.setText("Databaserne skal indl\u00E6se data fra forskellige kilder.");

		final Label lblFolketllingerneKanHentes = new Label(container, SWT.NONE);
		lblFolketllingerneKanHentes.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblFolketllingerneKanHentes.setText("Folket\u00E6llingerne kan hentes fra " + "http://www.salldata.dk/zip/.");

		final Label lblDeKbenhavnskeDatabaser = new Label(container, SWT.NONE);
		lblDeKbenhavnskeDatabaser.setText("De k\u00F8benhavnske databaser kan hentes fra "
				+ "https://docs.google.com/spreadsheets/d/1hDJItyQqaeRTbo30C1y4fHPzp4Q4tlQHoCCKSJwv2iQ/edit#gid=1318577.");

		final Label lblSkifterneErHentet = new Label(container, SWT.WRAP);
		final GridData gd_lblSkifterneErHentet = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSkifterneErHentet.widthHint = 782;
		gd_lblSkifterneErHentet.heightHint = 54;
		lblSkifterneErHentet.setLayoutData(gd_lblSkifterneErHentet);
		lblSkifterneErHentet.setText(
				"Skifterne er indl\u00E6st med et specialprogram, baseret p\u00E5 Apache UIMA-arkitekturen (https://uima.apache.org/). "
						+ "Data kan for eksempel hentes fra Asger Bruuns skifteuddrag, Aurelia Clemons probate extract, Bornholm Sl\u00E6gtss\u00F8gning, "
						+ "Erik Brejl skifteuddrag Erik Reinert Nielsen skifteuddrag, Godsskifter Vendsyssel, Kurt Kermit skifteuddrag, "
						+ "Sydfynske godsskifter og S\u00F8rensen & Helseby skifteuddrag.");

		final Label lblIndldningprogrammerneAnvenderGedcomjbiblioteket = new Label(container, SWT.NONE);
		lblIndldningprogrammerneAnvenderGedcomjbiblioteket
				.setText("Indl\u00E6sningprogrammerne anvender Gedcom4J-biblioteket fra "
						+ "https://github.com/frizbog/gedcom4j.");

		final Label lblProgrammetKanIndlse = new Label(container, SWT.NONE);
		lblProgrammetKanIndlse
				.setText("Programmet kan indl\u00E6se en GEDCOM-fil, som de \u00F8vrige data analyseres fra.");

		final Label lblProgrammetForventerAt = new Label(container, SWT.WRAP);
		lblProgrammetForventerAt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		final GridData gd_lblProgrammetForventerAt = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblProgrammetForventerAt.widthHint = 836;
		gd_lblProgrammetForventerAt.heightHint = 68;
		lblProgrammetForventerAt.setLayoutData(gd_lblProgrammetForventerAt);
		lblProgrammetForventerAt.setText(
				"Programmet forventer, at flytninger fra Til-og afgangslisterne i kirkeb\u00F8gerne har afgangssted som note(\"fra Vejby\") og h\u00E5ndtering som kildereferencedetalje (\"Tj gmd Niels Nielsen i Vejby\"). Det forventer ogs\u00E5, at teksten fra folket\u00E6llingerne i DDD er indkopieret som kildereferencedetalje i folket\u00E6llingerne.");

		final Label lblDetForventerEndvidere = new Label(container, SWT.WRAP);
		lblDetForventerEndvidere.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		final GridData gd_lblDetForventerEndvidere = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblDetForventerEndvidere.widthHint = 820;
		gd_lblDetForventerEndvidere.heightHint = 48;
		lblDetForventerEndvidere.setLayoutData(gd_lblDetForventerEndvidere);
		lblDetForventerEndvidere
				.setText("Det forventer endvidere, at de personer, hvis for\u00E6ldre ikke er med i databasen, har "
						+ "for\u00E6ldrene angivet i kildedetaljereference for d\u00E5ben (\"Niels Madsen og Bodil "
						+ "Nielsdatter, Blistrup\").");

		final Label lblProgrammetErSkrevet = new Label(container, SWT.NONE);
		lblProgrammetErSkrevet.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblProgrammetErSkrevet.setText("Programmet er skrevet i Java version 17 og anvender en Apache Derby database.");

		final Label lblKildetekstenTilProgrammet = new Label(container, SWT.NONE);
		lblKildetekstenTilProgrammet.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblKildetekstenTilProgrammet.setText(
				"Kildeteksten til programmet kan hentes fra " + "https://github.com/MichaelErichsen/VejbyGedcom.");

		final Label lblProgrammetErSkrevet_1 = new Label(container, SWT.NONE);
		lblProgrammetErSkrevet_1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblProgrammetErSkrevet_1.setText(
				"Programmet er skrevet af Michael Erichsen i 2023 og er til fri afbenyttelse til ikke-kommercielt brug.");

		return container;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(869, 555);
	}

}
