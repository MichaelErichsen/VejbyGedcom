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
 * Wizard page to handle census imports
 *
 * @author Michael Erichsen
 * @version 26. apr. 2023
 *
 */
public class SettingsWizardPage3 extends WizardPage {
	private Text txtProbatePath;
	private Text txtProbateSchema;

	private SettingsModel settings;

	public SettingsWizardPage3() {
		super("wizardPage");
		setTitle("Import af skifteprotokoludtræk");
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

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")) {
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

			if (settings.getProbatePath().equals("") || settings.getProbateSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtProbateSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtProbateSchema.setText(settings.getProbateSchema());
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		final Label lblDatabaseOgSchema = new Label(container, SWT.NONE);
		lblDatabaseOgSchema.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblDatabaseOgSchema.setText("Database og schema beh\u00F8ver ikke at v\u00E6re forskellige");

		setControl(container);

	}
}
