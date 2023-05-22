package net.myerichsen.archivesearcher.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import net.myerichsen.archivesearcher.models.MilrollListModel;

/**
 * @author Michael Erichsen
 * @version 5. maj 2023
 *
 */
public class MillrollListNextLaegdIdEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;

	/**
	 * Constructor
	 *
	 * @param viewer
	 * @param viewer2
	 */
	public MillrollListNextLaegdIdEditingSupport(TableViewer viewer) {
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
		return Integer.toString(((MilrollListModel) element).getNextLaegdId());
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		if (userInputValue.equals("")) {
			((MilrollListModel) element).setNextLaegdId(0);
		} else {
			((MilrollListModel) element).setNextLaegdId(Integer.parseInt(String.valueOf(userInputValue)));
		}
		viewer.update(element, null);
	}

}
