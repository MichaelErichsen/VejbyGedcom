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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import net.myerichsen.archivesearcher.models.SettingsModel;

/**
 * Wizard page to handle Copenhagen registries
 *
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class SettingsWizardPage4 extends WizardPage {
	private Text txtCphCsvFileDirectory;
	private Text txtBurialPersonComplete;
	private Text txtPolicePerson;
	private Text txtPoliceAddress;
	private Text txtPolicePosition;
	private Text txtCphDbPath;
	private Text txtCphSchema;

	private Button btnRelocationTab;
	private Button btnCensusTab;
	private Button btnProbateTab;
	private Button btnPolitietsRegisterblade;
	private Button btnBegravelsesregistret;
	private Button btnSiblings;

	private SettingsModel settings;
	private Button btnHusbond;
	private Label lblMeddelelsesloglngde;
	private Slider sliderMsgLogLen;
	private Text txtMsgLogLen;

	public SettingsWizardPage4() {
		super("wizardPage");
		setTitle("Import af Københavnske registre");
		setDescription("Politiets registerblade og Begravelsesprotokoller kan indlæses fra Københavns Stadsarkiv.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(3, false));

		settings = ((SettingsWizard) getWizard()).getSettings();

		final Label lblStiTilCsv = new Label(container, SWT.NONE);
		lblStiTilCsv.setText("Sti til københavnske csv filer");

		txtCphCsvFileDirectory = new Text(container, SWT.BORDER);
		txtCphCsvFileDirectory.addModifyListener(e -> {
			settings.setCphCsvFileDirectory(txtCphCsvFileDirectory.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCphCsvFileDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtCphCsvFileDirectory.setText(settings.getCphCsvFileDirectory());

		final Button btnFindCphCsvFileDirectory = new Button(container, SWT.NONE);
		btnFindCphCsvFileDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtCphCsvFileDirectory.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					txtCphCsvFileDirectory.setText(dir);
					settings.setCphCsvFileDirectory(dir);
				}
			}
		});
		btnFindCphCsvFileDirectory.setText("Find");

		final Label lblBurialpersoncomplete = new Label(container, SWT.NONE);
		lblBurialpersoncomplete.setText("Burial Person Complete");

		txtBurialPersonComplete = new Text(container, SWT.BORDER);
		txtBurialPersonComplete.addModifyListener(e -> {
			settings.setBurialPersonComplete(txtBurialPersonComplete.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtBurialPersonComplete.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		txtBurialPersonComplete.setText(settings.getBurialPersonComplete());
		new Label(container, SWT.NONE);

		final Label lblPoliceperson = new Label(container, SWT.NONE);
		lblPoliceperson.setText("Police Person");

		txtPolicePerson = new Text(container, SWT.BORDER);
		txtPolicePerson.addModifyListener(e -> {
			settings.setPolicePerson(txtPolicePerson.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtPolicePerson.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtPolicePerson.setText(settings.getPolicePerson());
		new Label(container, SWT.NONE);

		final Label lblPoliceaddress = new Label(container, SWT.NONE);
		lblPoliceaddress.setText("Police Address");

		txtPoliceAddress = new Text(container, SWT.BORDER);
		txtPoliceAddress.addModifyListener(e -> {
			settings.setPoliceAddress(txtPoliceAddress.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtPoliceAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtPoliceAddress.setText(settings.getPoliceAddress());
		new Label(container, SWT.NONE);

		final Label lblPoliceposition = new Label(container, SWT.NONE);
		lblPoliceposition.setText("Police Position");

		txtPolicePosition = new Text(container, SWT.BORDER);
		txtPolicePosition.addModifyListener(e -> {
			settings.setPolicePosition(txtPolicePosition.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtPolicePosition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtPolicePosition.setText(settings.getPolicePosition());
		new Label(container, SWT.NONE);

		final Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("K\u00F8benhavnsdatabase sti");

		txtCphDbPath = new Text(container, SWT.BORDER);
		txtCphDbPath.addModifyListener(e -> {
			settings.setCphDbPath(txtCphDbPath.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCphDbPath.setText(settings.getCphDbPath());
		txtCphDbPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnFindCphPath = new Button(container, SWT.NONE);
		btnFindCphPath.setText("Find");
		btnFindCphPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell[] shells = e.widget.getDisplay().getShells();
				final DirectoryDialog directoryDialog = new DirectoryDialog(shells[0]);

				directoryDialog.setFilterPath(txtCphDbPath.getText());
				directoryDialog.setText("Vælg venligst en folder og klik OK");

				final String dir = directoryDialog.open();
				if (dir != null) {
					txtCphDbPath.setText(dir);
					settings.setCphDbPath(dir);
				}
			}
		});

		final Label lblCphDatabaseSchema = new Label(container, SWT.NONE);
		lblCphDatabaseSchema.setText("K\u00F8benhavnsdatabase database schema");

		txtCphSchema = new Text(container, SWT.BORDER);
		txtCphSchema.addModifyListener(e -> {
			settings.setCphSchema(txtCphSchema.getText());

			if (settings.getCphCsvFileDirectory().equals("") || settings.getBurialPersonComplete().equals("")
					|| settings.getPolicePerson().equals("") || settings.getPoliceAddress().equals("")
					|| settings.getPolicePosition().equals("") || settings.getCphDbPath().equals("")
					|| settings.getCphSchema().equals("")) {

				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		});
		txtCphSchema.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtCphSchema.setText(settings.getCphSchema());
		new Label(container, SWT.NONE);

		final Composite checkButtoncomposite = new Composite(container, SWT.BORDER);
		final GridData gd_checkButtoncomposite = new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1);
		gd_checkButtoncomposite.widthHint = 799;
		checkButtoncomposite.setLayoutData(gd_checkButtoncomposite);
		final GridLayout gl_checkButtoncomposite = new GridLayout(8, false);
		gl_checkButtoncomposite.verticalSpacing = 3;
		checkButtoncomposite.setLayout(gl_checkButtoncomposite);

		final Label lblAktiveSgninger = new Label(checkButtoncomposite, SWT.NONE);
		lblAktiveSgninger.setText("Aktive s\u00F8gninger:");

		btnRelocationTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnRelocationTab.setSelection(true);
		btnRelocationTab.setText("Flytninger");

		btnCensusTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnCensusTab.setSelection(true);
		btnCensusTab.setText("Folket\u00E6llinger");

		btnProbateTab = new Button(checkButtoncomposite, SWT.CHECK);
		btnProbateTab.setSelection(true);
		btnProbateTab.setText("Skifter");

		btnPolitietsRegisterblade = new Button(checkButtoncomposite, SWT.CHECK);
		btnPolitietsRegisterblade.setSelection(true);
		btnPolitietsRegisterblade.setText("Politiets registerblade");

		btnBegravelsesregistret = new Button(checkButtoncomposite, SWT.CHECK);
		btnBegravelsesregistret.setSelection(true);
		btnBegravelsesregistret.setText("Begravelsesregistret");

		btnSiblings = new Button(checkButtoncomposite, SWT.CHECK);
		btnSiblings.setSelection(true);
		btnSiblings.setText("S\u00F8skende");

		btnHusbond = new Button(checkButtoncomposite, SWT.CHECK);
		btnHusbond.setSelection(true);
		btnHusbond.setText("Husbond");

		setControl(container);

		lblMeddelelsesloglngde = new Label(container, SWT.NONE);
		lblMeddelelsesloglngde.setText("Meddelelseslogl\u00E6ngde");

		sliderMsgLogLen = new Slider(container, SWT.NONE);
		sliderMsgLogLen.setMinimum(1);
		sliderMsgLogLen.setMaximum(100);
		sliderMsgLogLen.setIncrement(1);
		sliderMsgLogLen.setSelection(Integer.parseInt(settings.getMsgLogLen()));
		sliderMsgLogLen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int selection = sliderMsgLogLen.getSelection();
				txtMsgLogLen.setText(selection + "");
				settings.setMsgLogLen(Integer.toString(selection));
			}
		});

		txtMsgLogLen = new Text(container, SWT.BORDER);
		txtMsgLogLen.setEditable(false);
		txtMsgLogLen.setText(settings.getMsgLogLen());
		txtMsgLogLen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
}
