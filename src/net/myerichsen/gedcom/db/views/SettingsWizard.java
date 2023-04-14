package net.myerichsen.gedcom.db.views;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import net.myerichsen.gedcom.db.models.SettingsModel;

/**
 * Settings wizard
 *
 * @author Michael Erichsen
 * @version 10. apr. 2023
 *
 */
public class SettingsWizard extends Wizard {
	private final Properties props;
	private SettingsModel settings;

	public SettingsWizard(Properties props) {
		setWindowTitle("Indstillinger");
		this.props = props;
		settings = new SettingsModel(props);
	}

	@Override
	public void addPages() {
		addPage(new SettingsWizardPage1());
		addPage(new SettingsWizardPage2());
		addPage(new SettingsWizardPage3());
		addPage(new SettingsWizardPage4());
	}

	/**
	 * @return the settings
	 */
	public SettingsModel getSettings() {
		return settings;
	}

	@Override
	public boolean performFinish() {
		settings.storeProperties(props);
		return true;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(SettingsModel settings) {
		this.settings = settings;
	}

}
