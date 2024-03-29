package net.myerichsen.archivesearcher.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import net.myerichsen.archivesearcher.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 14. maj 2023
 *
 */
public class MilRollTreeLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		final MilRollEntryModel model = (MilRollEntryModel) element;
		return model.toString();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

}
