package net.myerichsen.gedcom.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import net.myerichsen.gedcom.db.models.MilrollListModel;

/**
 * @author Michael Erichsen
 * @version 5. maj 2023
 *
 */
public class MillrollListGlAarEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;

	/**
	 * Constructor
	 *
	 * @param viewer
	 * @param viewer2
	 */
	public MillrollListGlAarEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		return Integer.toString(((MilrollListModel) element).getGlAar());
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		if (userInputValue.equals("")) {
			((MilrollListModel) element).setGlAar(0);
		} else {
			((MilrollListModel) element).setGlAar(Integer.parseInt(String.valueOf(userInputValue)));
		}
		viewer.update(element, null);
	}

}
