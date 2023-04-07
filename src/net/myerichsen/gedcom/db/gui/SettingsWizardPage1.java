package net.myerichsen.gedcom.db.gui;

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

import net.myerichsen.gedcom.db.models.ASSettings;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SettingsWizardPage1 extends WizardPage {
	private Text vejbyPath;
	private Text vejbySchema;
	private Text probatePath;
	private Text probateSchema;
	private Text probateSource;
	private Text cphPath;
	private Text cphDbPath;
	private Text cphSchema;
	private ASSettings asSettings;

	/**
	 * @return the asSettings
	 */
	public ASSettings getAsSettings() {
		return asSettings;
	}

	public SettingsWizardPage1(ASSettings asSettings) {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
		this.asSettings = asSettings;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		final Label lblVejbyDatabaseSti = new Label(container, SWT.NONE);
		lblVejbyDatabaseSti.setText("Vejby database sti");

		vejbyPath = new Text(container, SWT.BORDER);
		vejbyPath.setText(asSettings.getVejbyPath());
		vejbyPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindVejbyPath = new Button(container, SWT.NONE);
		btnFindVejbyPath.setText("Find");
		btnFindVejbyPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(vejbyPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					vejbyPath.setText(dir);
					asSettings.setVejbyPath(dir);
				}
			}
		});
		final Label lblVejbySchema = new Label(container, SWT.NONE);
		lblVejbySchema.setText("Vejby database schema");

		vejbySchema = new Text(container, SWT.BORDER);
		vejbySchema.setEnabled(false);
		vejbySchema.setEditable(false);
		vejbySchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		vejbySchema.setText(asSettings.getVejbySchema());
		new Label(container, SWT.NONE);

		final Label lblSkifteDatabaseSti = new Label(container, SWT.NONE);
		lblSkifteDatabaseSti.setText("Skifte database sti");

		probatePath = new Text(container, SWT.BORDER);
		probatePath.setText(asSettings.getProbatePath());
		probatePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindProbatePath = new Button(container, SWT.NONE);
		btnFindProbatePath.setText("Find");
		btnFindProbatePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(probatePath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					probatePath.setText(dir);
					asSettings.setProbatePath(dir);
				}
			}
		});
		final Label lblProbateDatabaseSchema = new Label(container, SWT.NONE);
		lblProbateDatabaseSchema.setText("Skifte database schema");

		probateSchema = new Text(container, SWT.BORDER);
		probateSchema.setEnabled(false);
		probateSchema.setEditable(false);
		probateSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		probateSchema.setText(asSettings.getProbateSchema());
		new Label(container, SWT.NONE);

		final Label lblSkiftekilde = new Label(container, SWT.NONE);
		lblSkiftekilde.setText("Skiftekilde");

		probateSource = new Text(container, SWT.BORDER);
		probateSource.setText(asSettings.getProbateSource());
		probateSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		cphPath = new Text(container, SWT.BORDER);
		cphPath.setText(asSettings.getCphDbPath());
		cphPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		final Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		cphDbPath = new Text(container, SWT.BORDER);
		cphDbPath.setText(asSettings.getCphDbPath());
		cphDbPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindCphPath = new Button(container, SWT.NONE);
		btnFindCphPath.setText("Find");
		btnFindCphPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(cphDbPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					cphDbPath.setText(dir);
					asSettings.setCphDbPath(dir);
				}
			}
		});
		final Label lblCphDatabaseSchema = new Label(container, SWT.NONE);
		lblCphDatabaseSchema.setText("K\u00F8benhavnsdatabase database schema");

		cphSchema = new Text(container, SWT.BORDER);
		cphSchema.setEnabled(false);
		cphSchema.setEditable(false);
		cphSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cphSchema.setText(asSettings.getCphSchema());
		new Label(container, SWT.NONE);

	}

}
