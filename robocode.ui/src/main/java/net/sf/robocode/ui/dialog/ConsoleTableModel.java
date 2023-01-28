/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * @author Xor (contributor)
 */
@SuppressWarnings("serial")
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
