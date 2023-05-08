package net.myerichsen.gedcom.db.dialogs;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import net.myerichsen.gedcom.db.models.SettingsModel;
import net.myerichsen.gedcom.db.views.ArchiveSearcher;

/**
 * Settings wizard
 *
 * @author Michael Erichsen
 * @version 25. apr. 2023
 *
 */
public class SettingsWizard extends Wizard {
	private final Properties props;
	private SettingsModel settings;
	private final ArchiveSearcher as;

	public SettingsWizard(Properties props, ArchiveSearcher as) {
		setWindowTitle("Indstillinger");
		this.props = props;
		this.as = as;
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
		final String string = settings.storeProperties(props);
		as.setMessage(string);
		return true;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(SettingsModel settings) {
		this.settings = settings;
	}

}
