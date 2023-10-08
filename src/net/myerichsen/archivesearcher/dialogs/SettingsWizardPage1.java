package net.myerichsen.archivesearcher.dialogs;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.archivesearcher.models.SettingsModel;

/**
 * Wizard page to handle GEDCOM imports
 *
 * @author Michael Erichsen
 * @version 8.okt. 2023
 *
 */

public class SettingsWizardPage1 extends WizardPage {
	private Text txtGedcomFilePath;
	private Text txtparishPath;
	private Text txtparishSchema;

	private SettingsModel settings;

	public SettingsWizardPage1() {
		super("wizardPage");
		setTitle("Import af en GEDCOM fil");
		setDescription("Eksportér en GEDCOM-fil fra dit slægtsforskningsprogram til analyse med dette program.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(3, false));

		settings = ((SettingsWizard) getWizard()).getSettings();

		final Label lblGedcomFilSti = new Label(container, SWT.NONE);
		lblGedcomFilSti.setText("GEDCOM fil sti");

		txtGedcomFilePath = new Text(container, SWT.BORDER);
		txtGedcomFilePath.addModifyListener(e -> {
			settings.setGedcomFilePath(txtGedcomFilePath.getText());

			if ("".equals(settings.getGedcomFilePath()) || "".equals(settings.getparishPath())
					|| "".equals(settings.getparishSchema())) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtGedcomFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtGedcomFilePath.setText(settings.getGedcomFilePath());

		final Button btnFindGedcomCsv = new Button(container, SWT.NONE);
		btnFindGedcomCsv.setText("Find");
		btnFindGedcomCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final FileDialog fileDialog = new FileDialog(shells[0]);
				fileDialog.setFilterPath(txtGedcomFilePath.getText());
				fileDialog.setText("Vælg en GEDCOM fil");
				final String[] filterExt = { "*.ged", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				final String file = fileDialog.open();

				if (!"".equals(file)) {
					txtGedcomFilePath.setText(file);
					settings.setGedcomFilePath(file);
				}
			}
		});

		final Label lblParishDatabaseSti = new Label(container, SWT.NONE);
		lblParishDatabaseSti.setText("Sognedatabase sti");

		txtparishPath = new Text(container, SWT.BORDER);
		txtparishPath.addModifyListener(e -> {
			settings.setparishPath(txtparishPath.getText());

			if ("".equals(settings.getGedcomFilePath()) || "".equals(settings.getparishPath())
					|| "".equals(settings.getparishSchema())) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtparishPath.setText(settings.getparishPath());
		txtparishPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindparishPath = new Button(container, SWT.NONE);
		btnFindparishPath.setText("Find");
		btnFindparishPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtparishPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (!"".equals(dir)) {
					txtparishPath.setText(dir);
					settings.setparishPath(dir);
				}
			}
		});

		final Label lblparishSchema = new Label(container, SWT.NONE);
		lblparishSchema.setText("Sognedatabase schema");

		txtparishSchema = new Text(container, SWT.BORDER);
		txtparishSchema.addModifyListener(e -> {
			settings.setparishSchema(txtparishSchema.getText());

			if ("".equals(settings.getGedcomFilePath()) || "".equals(settings.getparishPath())
					|| "".equals(settings.getparishSchema())) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtparishSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtparishSchema.setText(settings.getparishSchema());
		new Label(container, SWT.NONE);

		setControl(container);

	}
}
