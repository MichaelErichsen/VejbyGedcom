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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.gedcom.db.models.ASSettings;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SettingsWizardPage2 extends WizardPage {
	private Text csvFileDirectory;
	private Text kipTextFilename;
	private Text outputDirectory;
	private Text gedcomFilePath;
	private Button btnCensusTab;
	private Button btnProbateTab;
	private Button btnPolitietsRegisterblade;
	private Button btnBegravelsesregistret;
	private Button btnSiblings;
	private Button btnRelocationTab;
	private ASSettings asSettings;

	/**
	 * @return the asSettings
	 */
	public ASSettings getAsSettings() {
		return asSettings;
	}

	/**
	 * @param asSettings the asSettings to set
	 */
	public void setAsSettings(ASSettings asSettings) {
		this.asSettings = asSettings;
	}

	public SettingsWizardPage2(ASSettings asSettings) {
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

		final Label lblKipCsvFil = new Label(container, SWT.NONE);
		lblKipCsvFil.setText("KIP csv fil sti");

		csvFileDirectory = new Text(container, SWT.BORDER);
		csvFileDirectory.setText(asSettings.getCsvFileDirectory());

		final Button btnFindCsvPath = new Button(container, SWT.NONE);
		btnFindCsvPath.setText("Find");
		btnFindCsvPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(csvFileDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					csvFileDirectory.setText(dir);
					asSettings.setCsvFileDirectory(dir);
				}
			}
		});
		final Label lblKipTextFilnavn = new Label(container, SWT.NONE);
		lblKipTextFilnavn.setText("KIP tekst filnavn uden sti");

		kipTextFilename = new Text(container, SWT.BORDER);
		kipTextFilename.setText(asSettings.getKipTextFilename());
		new Label(container, SWT.NONE);

		final Label lblUddatasti = new Label(container, SWT.NONE);
		lblUddatasti.setText("Uddatasti");

		outputDirectory = new Text(container, SWT.BORDER);
		outputDirectory.setText(asSettings.getOutputDirectory());
		GridData gd_outputDirectory = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_outputDirectory.widthHint = 243;
		outputDirectory.setLayoutData(gd_outputDirectory);

		final Button btnFindOutpath = new Button(container, SWT.NONE);
		btnFindOutpath.setText("Find");
		btnFindOutpath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(outputDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					outputDirectory.setText(dir);
					asSettings.setOutputDirectory(dir);
				}
			}
		});

		final Label lblGedcomFilSti = new Label(container, SWT.NONE);
		lblGedcomFilSti.setText("GEDCOM fil sti");

		gedcomFilePath = new Text(container, SWT.BORDER);
		GridData gd_gedcomFilePath = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_gedcomFilePath.widthHint = 235;
		gedcomFilePath.setLayoutData(gd_gedcomFilePath);
		gedcomFilePath.setText(asSettings.getGedcomFilePath());

		final Button btnFindKipCsv = new Button(container, SWT.NONE);
		btnFindKipCsv.setText("Find");
		btnFindKipCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final FileDialog fileDialog = new FileDialog(shells[0]);
				fileDialog.setFilterPath(gedcomFilePath.getText());
				fileDialog.setText("Vælg en GEDCOM fil");
				final String[] filterExt = { "*.ged", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				final String file = fileDialog.open();

				if (file != null) {
					gedcomFilePath.setText(file);
					asSettings.setGedcomFilePath(file);
				}
			}
		});

		final Composite checkButtoncomposite = new Composite(container, SWT.BORDER);
		final GridData gd_checkButtoncomposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_checkButtoncomposite.widthHint = 1071;
		checkButtoncomposite.setLayoutData(gd_checkButtoncomposite);
		checkButtoncomposite.setLayout(new GridLayout(7, false));

		final Label lblAktiveSgninger = new Label(checkButtoncomposite, SWT.NONE);
		lblAktiveSgninger.setText("Aktive s\u00F8gninger:");

		btnRelocationTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnRelocationTab.setSelection(Boolean.parseBoolean(asSettings.getRelocationSearch()));
		btnRelocationTab.setText("Flytninger");

		btnCensusTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnCensusTab.setSelection(Boolean.parseBoolean(asSettings.getCensusSearch()));
		btnCensusTab.setText("Folket\u00E6llinger");

		btnProbateTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnProbateTab.setSelection(Boolean.parseBoolean(asSettings.getPolregSearch()));
		btnProbateTab.setText("Skifter");

		btnPolitietsRegisterblade = new Button(checkButtoncomposite, SWT.CHECK);
		btnPolitietsRegisterblade.setSelection(Boolean.parseBoolean(asSettings.getBurregSearch()));
		btnPolitietsRegisterblade.setText("Politiets registerblade");

		btnBegravelsesregistret = new Button(checkButtoncomposite, SWT.CHECK);
		btnBegravelsesregistret.setSelection(Boolean.parseBoolean(asSettings.getBurregSearch()));
		btnBegravelsesregistret.setText("Begravelsesregistret");

		btnSiblings = new Button(checkButtoncomposite, SWT.CHECK);
		btnSiblings.setSelection(true);
		btnSiblings.setText("S\u00F8skende");

	}
}
