package net.myerichsen.gedcom.db.gui;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import net.myerichsen.gedcom.db.models.ASSettings;

/**
 * Settings wizard
 *
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class SettingsWizard extends Wizard {
	private Properties props;
	private ASSettings asSettings;

	public SettingsWizard(Properties props) {
		setWindowTitle("Indstillinger");
		this.props = props;
		asSettings = new ASSettings(props);
	}

	@Override
	public void addPages() {
		addPage(new SettingsWizardPage1(asSettings));
		addPage(new SettingsWizardPage2(asSettings));
	}

	@Override
	public boolean performFinish() {
		asSettings.storeProperties(props);
		return true;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}
}
