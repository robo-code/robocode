package net.sf.robocode.ui.dialog;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;

public final class ConsoleTableModel extends AbstractTableModel implements TableModel {
	private final ArrayList<String> lines;

	public ConsoleTableModel(ArrayList<String> lines) {
		this.lines = lines;
	}

	@Override
	public int getRowCount() {
		return lines.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Class<?> getColumnClass(int i) {
		return String.class;
	}

	@Override
	public String getValueAt(int i, int i1) {
		if (0 <= i && i < getRowCount()) {
			return lines.get(i);
		}
		return "";
	}
}
