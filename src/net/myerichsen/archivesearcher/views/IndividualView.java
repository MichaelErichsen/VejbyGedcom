package net.myerichsen.archivesearcher.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.myerichsen.archivesearcher.models.IndividualModel;

/**
 * Individual view
 *
 * @author Michael Erichsen
 * @version 21. apr. 2023
 *
 */
public class IndividualView extends Composite {
	private final Text txtindividualid;
	private final Text txtindividualname;
	private final Text txtindividualsex;
	private final Text txtindividualfamc;
	private final Text txtindividualphonname;
	private final Text txtindividualbirthdate;
	private final Text txtindividualbirthplace;
	private final Text txtindividualparents;
	private final Text txtindividualdeathdate;
	private final Text txtindividualdeathplace;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */

	public IndividualView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		final Label lblId = new Label(this, SWT.NONE);
		lblId.setText("ID");

		txtindividualid = new Text(this, SWT.BORDER);
		txtindividualid.setEditable(false);
		txtindividualid.setText("");
		txtindividualid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFornavn = new Label(this, SWT.NONE);
		lblFornavn.setText("Navn");

		txtindividualname = new Text(this, SWT.BORDER);
		txtindividualname.setEditable(false);
		txtindividualname.setText("");
		txtindividualname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblKn = new Label(this, SWT.NONE);
		lblKn.setText("K\u00F8n");

		txtindividualsex = new Text(this, SWT.BORDER);
		txtindividualsex.setEditable(false);
		txtindividualsex.setText("");
		txtindividualsex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFamilieId = new Label(this, SWT.NONE);
		lblFamilieId.setText("Familie ID");

		txtindividualfamc = new Text(this, SWT.BORDER);
		txtindividualfamc.setEditable(false);
		txtindividualfamc.setText("");
		txtindividualfamc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFonetiskNavn = new Label(this, SWT.NONE);
		lblFonetiskNavn.setText("Fonetisk navn");

		txtindividualphonname = new Text(this, SWT.BORDER);
		txtindividualphonname.setEditable(false);
		txtindividualphonname.setText("");
		txtindividualphonname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFdselsdato = new Label(this, SWT.NONE);
		lblFdselsdato.setText("F\u00F8dselsdato");

		txtindividualbirthdate = new Text(this, SWT.BORDER);
		txtindividualbirthdate.setEditable(false);
		txtindividualbirthdate.setText("");
		txtindividualbirthdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblFdested = new Label(this, SWT.NONE);
		lblFdested.setText("F\u00F8dested");

		txtindividualbirthplace = new Text(this, SWT.BORDER);
		txtindividualbirthplace.setEditable(false);
		txtindividualbirthplace.setText("");
		txtindividualbirthplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblDddato = new Label(this, SWT.NONE);
		lblDddato.setText("Dødsdato");

		txtindividualdeathdate = new Text(this, SWT.BORDER);
		txtindividualdeathdate.setEditable(false);
		txtindividualdeathdate.setText("");
		txtindividualdeathdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblddsted = new Label(this, SWT.NONE);
		lblddsted.setText("D\u00F8dssted");

		txtindividualdeathplace = new Text(this, SWT.BORDER);
		txtindividualdeathplace.setEditable(false);
		txtindividualdeathplace.setText("");
		txtindividualdeathplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Label lblForldre = new Label(this, SWT.NONE);
		lblForldre.setText("For\u00E6ldre");

		txtindividualparents = new Text(this, SWT.BORDER);
		txtindividualparents.setEditable(false);
		txtindividualparents.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Clear all fields
	 */
	public void clear() {
		txtindividualid.setText("");
		txtindividualname.setText("");
		txtindividualsex.setText("");
		txtindividualfamc.setText("");
		txtindividualphonname.setText("");
		txtindividualbirthdate.setText("");
		txtindividualbirthplace.setText("");
		txtindividualdeathdate.setText("");
		txtindividualdeathplace.setText("");
		txtindividualparents.setText("");
		txtindividualid.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualsex.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualfamc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualphonname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualbirthdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualbirthplace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualdeathdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualdeathplace.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtindividualparents.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	/**
	 * @param individual
	 */
	public void populate(IndividualModel individual) {
		txtindividualid.setText(individual.getId());
		txtindividualname.setText(individual.getName());
		txtindividualsex.setText(individual.getSex());

		if (individual.getFamc() != null) {
			txtindividualfamc.setText(individual.getFamc());
		}

		txtindividualphonname.setText(individual.getPhonName());

		if (individual.getBirthDate() != null) {
			txtindividualbirthdate.setText(individual.getBirthDate().toString());
		}

		txtindividualbirthplace.setText(individual.getBirthPlace());

		if (individual.getDeathDate() != null) {
			txtindividualdeathdate.setText(individual.getDeathDate().toString());
		}

		txtindividualdeathplace.setText(individual.getDeathPlace());

		if (individual.getParents() != null) {
			txtindividualparents.setText(individual.getParents());
		}
	}
}
