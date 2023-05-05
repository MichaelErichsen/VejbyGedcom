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
public class MillrollListGlLitraEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;

	/**
	 * Constructor
	 *
	 * @param viewer
	 * @param viewer2
	 */
	public MillrollListGlLitraEditingSupport(TableViewer viewer) {
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
		return ((MilrollListModel) element).getGlLitra();
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		((MilrollListModel) element).setGlLitra(String.valueOf(userInputValue));
		viewer.update(element, null);
	}

}
