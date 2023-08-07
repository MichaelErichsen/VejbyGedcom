package net.myerichsen.archivesearcher.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import net.myerichsen.archivesearcher.models.MilrollListModel;

/**
 * @author Michael Erichsen
 * @version 5. maj 2023
 *
 */
public class MilrollListRulletypeEditingSupport extends EditingSupport {

	private final TableViewer viewer;

	/**
	 * Constructor
	 *
	 * @param viewer
	 */
	public MilrollListRulletypeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		final String[] rulletype = new String[2];
		rulletype[0] = "Hovedrulle";
		rulletype[1] = "Tilgangsrulle";

		return new ComboBoxCellEditor(viewer.getTable(), rulletype);
	}

	@Override
	protected Object getValue(Object element) {
		final MilrollListModel mrlm = (MilrollListModel) element;

		if ("Hovedrulle".equals(mrlm.getRulleType())) {
			return 0;
		}

		return 1;
	}

	@Override
	protected void setValue(Object element, Object value) {
		final MilrollListModel mrlm = (MilrollListModel) element;

		if ((Integer) value == 0) {
			mrlm.setRulleType("Hovedrulle");
		} else {
			mrlm.setRulleType("Tilgangsrulle");
		}

		viewer.update(element, null);
	}

}
