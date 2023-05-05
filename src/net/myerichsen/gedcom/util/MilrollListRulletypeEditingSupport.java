package net.myerichsen.gedcom.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import net.myerichsen.gedcom.db.models.MilrollListModel;

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
		rulletype[0] = "Hovedliste";
		rulletype[1] = "Tilgangsliste";

		return new ComboBoxCellEditor(viewer.getTable(), rulletype);
	}

	@Override
	protected Object getValue(Object element) {
		final MilrollListModel mrlm = (MilrollListModel) element;

		if (mrlm.getRulleType().equals("Hovedliste")) {
			return 0;
		}

		return 1;
	}

	@Override
	protected void setValue(Object element, Object value) {
		final MilrollListModel mrlm = (MilrollListModel) element;

		if ((Integer) value == 0) {
			mrlm.setRulleType("Hovedliste");
		} else {
			mrlm.setRulleType("Tilgangsliste");
		}

		viewer.update(element, null);
	}

}
