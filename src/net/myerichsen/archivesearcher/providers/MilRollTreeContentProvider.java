package net.myerichsen.archivesearcher.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.ITreeContentProvider;

import net.myerichsen.archivesearcher.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 10. jul. 2023
 *
 */
public class MilRollTreeContentProvider implements ITreeContentProvider {
	private final Properties props;

	/**
	 * Constructor
	 *
	 * @param props
	 */
	public MilRollTreeContentProvider(Properties props) {
		this.props = props;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MilRollEntryModel) {
			final MilRollEntryModel model = (MilRollEntryModel) parentElement;
			final MilRollEntryModel item = MilRollEntryModel.select(props, model.getPrevLaegdId(),
					model.getPrevLoebeNr());
			final List<MilRollEntryModel> list = new ArrayList<>();

			if (item != null) {
				list.add(item);
			}

			return list.toArray();
		}
		return getElements(parentElement);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof MilRollEntryModel) {
			final MilRollEntryModel model = (MilRollEntryModel) inputElement;
			final List<MilRollEntryModel> list = new ArrayList<>();

			list.add(MilRollEntryModel.select(props, model.getLaegdId(), model.getLoebeNr()));
			return list.toArray();
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		final MilRollEntryModel model = (MilRollEntryModel) element;
		return MilRollEntryModel.selectPrev(props, model.getPrevLaegdId(), model.getPrevLoebeNr());
	}

	@Override
	public boolean hasChildren(Object element) {
		if (getChildren(element).length > 0) {
			return true;
		}

		return false;
	}
}
