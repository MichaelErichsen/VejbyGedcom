package net.myerichsen.gedcom.db.dialogs;

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

import net.myerichsen.gedcom.db.models.SettingsModel;

/**
 * Wizard page to handle GEDCOM imports
 *
 * @author Michael Erichsen
 * @version 11. apr. 2023
 *
 */

public class SettingsWizardPage1 extends WizardPage {
	private Text txtGedcomFilePath;
	private Text txtVejbyPath;
	private Text txtVejbySchema;

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

			if (settings.getGedcomFilePath().equals("") || settings.getVejbyPath().equals("")
					|| settings.getVejbySchema().equals("")) {
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

				if (file.equals("")) {
					txtGedcomFilePath.setText(file);
					settings.setGedcomFilePath(file);
				}
			}
		});

		final Label lblVejbyDatabaseSti = new Label(container, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		txtVejbyPath = new Text(container, SWT.BORDER);
		txtVejbyPath.addModifyListener(e -> {
			settings.setVejbyPath(txtVejbyPath.getText());

			if (settings.getGedcomFilePath().equals("") || settings.getVejbyPath().equals("")
					|| settings.getVejbySchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtVejbyPath.setText(settings.getVejbyPath());
		txtVejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindVejbyPath = new Button(container, SWT.NONE);
		btnFindVejbyPath.setText("Find");
		btnFindVejbyPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtVejbyPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir.equals("")) {
					txtVejbyPath.setText(dir);
					settings.setVejbyPath(dir);
				}
			}
		});

		final Label lblVejbySchema = new Label(container, SWT.NONE);
		lblVejbySchema.setText("Vejby database schema");

		txtVejbySchema = new Text(container, SWT.BORDER);
		txtVejbySchema.addModifyListener(e -> {
			settings.setVejbySchema(txtVejbySchema.getText());

			if (settings.getGedcomFilePath().equals("") || settings.getVejbyPath().equals("")
					|| settings.getVejbySchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtVejbySchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtVejbySchema.setText(settings.getVejbySchema());
		new Label(container, SWT.NONE);

		setControl(container);

	}
}
