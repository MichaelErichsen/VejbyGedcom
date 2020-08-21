package net.myerichsen.vejby.util;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author MadProgrammer at StackOverflow
 * @version 16. aug. 2020
 *
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -6676370849190065442L;
	private Map<Integer, Color> mapColors;

	public CustomTableCellRenderer() {
		mapColors = new HashMap<>();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, 1);
		Color color = mapColors.get(row);
		if (color != null) {
			cell.setBackground(color);
		} else {
			cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		}
		return cell;
	}

	public void setRowColor(int row, Color color) {
		mapColors.put(row, color);
	}
}
