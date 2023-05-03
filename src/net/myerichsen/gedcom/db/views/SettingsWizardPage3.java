package net.myerichsen.gedcom.db.views;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.gedcom.db.models.SettingsModel;

/**
 * Wizard page to handle census imports and military roll input
 *
 * @author Michael Erichsen
 * @version 3. maj 2023
 *
 */
public class SettingsWizardPage3 extends WizardPage {
	private Text txtProbatePath;
	private Text txtProbateSchema;
	private Text txtMilRollPath;
	private Text txtMilRollSchema;
	private SettingsModel settings;

	public SettingsWizardPage3() {
		super("wizardPage");
		setTitle("Import af skifteprotokoludtr\u00E6k og indtastning af l\u00E6gdsruller");
		setDescription("Skifteprotokoludtræk kan indlæses i en database med et specialprogram.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(3, false));

		settings = ((SettingsWizard) getWizard()).getSettings();

		final Label lblSkifteDatabaseSti = new Label(container, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		txtProbatePath = new Text(container, SWT.BORDER);
		txtProbatePath.addModifyListener(e -> {
			settings.setProbatePath(txtProbatePath.getText());

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")
					|| settings.getMilrollPath().equals("") || settings.getMilrollSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtProbatePath.setText(settings.getProbatePath());
		txtProbatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindProbatePath = new Button(container, SWT.NONE);
		btnFindProbatePath.setText("Find");
		btnFindProbatePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtProbatePath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					txtProbatePath.setText(dir);
					settings.setProbatePath(dir);
				}
			}
		});
		final Label lblProbateDatabaseSchema = new Label(container, SWT.NONE);
		lblProbateDatabaseSchema.setText("Skifte database schema");

		txtProbateSchema = new Text(container, SWT.BORDER);
		txtProbateSchema.addModifyListener(e -> {
			settings.setProbateSchema(txtProbateSchema.getText());

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")
					|| settings.getMilrollPath().equals("") || settings.getMilrollSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtProbateSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtProbateSchema.setText(settings.getProbateSchema());
		new Label(container, SWT.NONE);

		final Label lblLgdsrulleDatabaseSti = new Label(container, SWT.NONE);
		lblLgdsrulleDatabaseSti.setText("L\u00E6gdsrulle database sti");

		txtMilRollPath = new Text(container, SWT.BORDER);
		txtMilRollPath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		txtMilRollPath.addModifyListener(e -> {
			settings.setMilrollPath(txtMilRollPath.getText());

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")
					|| settings.getMilrollPath().equals("") || settings.getMilrollSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtMilRollPath.setText(settings.getMilrollPath());

		final Button btnFind = new Button(container, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtMilRollPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					txtMilRollPath.setText(dir);
					settings.setMilrollPath(dir);
				}
			}
		});
		btnFind.setText("Find");

		final Label lblLgdsrulleDatabaseSchema = new Label(container, SWT.NONE);
		lblLgdsrulleDatabaseSchema.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLgdsrulleDatabaseSchema.setText("L\u00E6gdsrulle database schema");

		txtMilRollSchema = new Text(container, SWT.BORDER);
		txtMilRollSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtMilRollSchema.addModifyListener(e -> {
			settings.setMilrollSchema(txtMilRollSchema.getText());

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")
					|| settings.getMilrollPath().equals("") || settings.getMilrollSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtMilRollSchema.setText(settings.getMilrollSchema());
		new Label(container, SWT.NONE);

		final Label lblDatabaseOgSchema = new Label(container, SWT.NONE);
		lblDatabaseOgSchema.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblDatabaseOgSchema.setText("Database og schema beh\u00F8ver ikke at v\u00E6re forskellige");

		setControl(container);

	}
}
