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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.archivesearcher.models.SettingsModel;

/**
 * Wizard page to handle census imports
 *
 * @author Michael Erichsen
 * @version 12. apr. 2023
 *
 */
public class SettingsWizardPage2 extends WizardPage {
	private Text txtCensusCsvFileDirectory;
	private Text txtKipTextFilename;
	private Text txtCensusPath;
	private Text txtCensusSchema;

	private SettingsModel settings;

	public SettingsWizardPage2() {
		super("wizardPage");
		setTitle("Import af folketællinger");
		setDescription("Download folketællinger som zippede csv-filer fra Sall data, http://www.salldata.dk/zip/\n"
				+ "Folketællinger anvender samme database og skema som GEDCOM-importen");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(3, false));

		settings = ((SettingsWizard) getWizard()).getSettings();

		final Label lblKipCsvFil = new Label(container, SWT.NONE);
		lblKipCsvFil.setText("KIP csv fil sti");

		txtCensusCsvFileDirectory = new Text(container, SWT.BORDER);
		txtCensusCsvFileDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtCensusCsvFileDirectory.addModifyListener(e -> {
			settings.setCensusCsvFileDirectory(txtCensusCsvFileDirectory.getText());

			if (settings.getCensusCsvFileDirectory().equals("") || settings.getKipTextFilename().equals("")
					|| settings.getCensusPath().equals("") || settings.getCensusSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCensusCsvFileDirectory.setText(settings.getCsvFileDirectory());

		final Button btnFindCsvPath = new Button(container, SWT.NONE);
		btnFindCsvPath.setText("Find");
		btnFindCsvPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtCensusCsvFileDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					txtCensusCsvFileDirectory.setText(dir);
					settings.setCsvFileDirectory(dir);
				}
			}
		});

		final Label lblKipTextFilnavn = new Label(container, SWT.NONE);
		lblKipTextFilnavn.setText("KIP tekst filnavn uden sti");

		txtKipTextFilename = new Text(container, SWT.BORDER);
		txtKipTextFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtKipTextFilename.addModifyListener(e -> {
			settings.setKipTextFilename(txtKipTextFilename.getText());

			if (settings.getCensusCsvFileDirectory().equals("") || settings.getKipTextFilename().equals("")
					|| settings.getCensusPath().equals("") || settings.getCensusSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtKipTextFilename.setText(settings.getKipTextFilename());
		new Label(container, SWT.NONE);

		final Label lblCensusDatabaseSti = new Label(container, SWT.NONE);
		lblCensusDatabaseSti.setText("Census database sti");

		txtCensusPath = new Text(container, SWT.BORDER);
		txtCensusPath.addModifyListener(e -> {
			settings.setCensusPath(txtCensusPath.getText());

			if (settings.getCensusCsvFileDirectory().equals("") || settings.getKipTextFilename().equals("")
					|| settings.getCensusPath().equals("") || settings.getCensusSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCensusPath.setText(settings.getCensusPath());
		txtCensusPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindCensusPath = new Button(container, SWT.NONE);
		btnFindCensusPath.setText("Find");
		btnFindCensusPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtCensusPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir.equals("")) {
					txtCensusPath.setText(dir);
					settings.setCensusPath(dir);
				}
			}
		});

		final Label lblCensusSchema = new Label(container, SWT.NONE);
		lblCensusSchema.setText("Census database schema");

		txtCensusSchema = new Text(container, SWT.BORDER);
		txtCensusSchema.addModifyListener(e -> {
			settings.setCensusSchema(txtCensusSchema.getText());

			if (settings.getCensusCsvFileDirectory().equals("") || settings.getKipTextFilename().equals("")
					|| settings.getCensusPath().equals("") || settings.getCensusSchema().equals("")) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCensusSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtCensusSchema.setText(settings.getCensusSchema());
		new Label(container, SWT.NONE);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		final Label lblDatabaseOgSchema = new Label(container, SWT.NONE);
		lblDatabaseOgSchema.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblDatabaseOgSchema.setText("Database og schema beh\u00F8ver ikke at v\u00E6re forskellige");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		setControl(container);
		new Label(container, SWT.NONE);
	}
}
