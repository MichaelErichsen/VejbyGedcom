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
public class MillrollListAarEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;

	/**
	 * Constructor
	 *
	 * @param viewer
	 */
	public MillrollListAarEditingSupport(TableViewer viewer) {
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
		return Integer.toString(((MilrollListModel) element).getAar());
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		final String s = String.valueOf(userInputValue);
		if ("".equals(s)) {
			((MilrollListModel) element).setAar(0);
		} else {
			((MilrollListModel) element).setAar(Integer.parseInt(s));
		}
		viewer.update(element, null);
	}

}
