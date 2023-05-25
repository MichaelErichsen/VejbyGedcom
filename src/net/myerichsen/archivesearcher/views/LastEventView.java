package net.myerichsen.archivesearcher.views;

import java.util.Properties;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import net.myerichsen.archivesearcher.populators.ASPopulator;

/**
 * Display all events for a location that are the last events for the given
 * individual to search for burials
 * 
 * @author Michael Erichsen
 * @version 25. maj 2023
 *
 */
// TODO Build the view and integrate in application
public class LastEventView extends Composite {
	private TableViewer tableViewer;
	private Table table;
	private ASPopulator listener;
	private Properties props;
	private Thread thread;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LastEventView(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}

	/**
	 * @param display
	 */
	private void popup(Display display) {
		// TODO List the event details
		// Button to jump to the individual

	}
}
