/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
@SuppressWarnings("serial")
public final class ConsoleScrollPane extends JScrollPane {
	private final ArrayList<String> lines = new ArrayList<String>();
	private JTable table = null;

	public ConsoleScrollPane() {
		super();
		setViewportView(getTextPane());
	}

	public JTable getTextPane() {
		if (table == null) {
			// https://stackoverflow.com/questions/15499255/jtable-with-autoresize-horizontal-scrolling-and-shrinkable-first-column
			table = new JTable() {
				@Override
				public boolean getScrollableTracksViewportWidth() {
					return hasExcessWidth();
				}

				@Override
				public void doLayout() {
					if (hasExcessWidth()) {
						autoResizeMode = AUTO_RESIZE_ALL_COLUMNS;
					}
					super.doLayout();
					autoResizeMode = AUTO_RESIZE_OFF;
				}

				protected boolean hasExcessWidth() {
					return getPreferredSize().width < getViewport().getWidth();
				}
			};
			table.setTableHeader(null);
			table.setFillsViewportHeight(true);

			table.setModel(new ConsoleTableModel(lines));
			resetWidth();

			getViewport().setBackground(Color.DARK_GRAY);
			table.setBackground(Color.DARK_GRAY);
			table.setGridColor(Color.DARK_GRAY);
			table.setShowGrid(false);
			table.setForeground(Color.WHITE);

			table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		}
		return table;
	}

	private void fitTextWidth(int low, int high) {
		JTable table = getTextPane();
		TableColumn col = table.getColumnModel().getColumn(0);
		int width = col.getMinWidth() - 2;
		for (int r = low; r <= high; r++) {
			TableCellRenderer renderer = table.getCellRenderer(r, 0);
			Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, 0),
				false, false, r, 0);
			width = Math.max(width, comp.getPreferredSize().width);
		}
		col.setMinWidth(width + 2);
	}

	public void append(String str) {
		// Return if the string is null or empty
		if (str == null || str.length() == 0) {
			return;
		}

		int n = lines.size();

		String[] t = str.split("\\n", -1);
		if (lines.isEmpty()) {
			lines.addAll(Arrays.asList(t));
		} else {
			int last = n - 1;
			String lastLine = lines.get(last);
			assert t.length > 0; // emptiness already checked
			if (lastLine.length() > 0) {
				lines.set(last, lastLine + t[0]);
			} else {
				lines.set(last, t[0]);
			}
			lines.addAll(Arrays.asList(t).subList(1, t.length));
		}

		AbstractTableModel model = (AbstractTableModel) getTextPane().getModel();
		if (n > 0) {
			model.fireTableRowsUpdated(n - 1, n - 1);
		}
		model.fireTableRowsInserted(n, lines.size() - 1);

		fitTextWidth(Math.max(0, n - 1), lines.size() - 1);
	}

	public String getSelectedText() {
		JTable table = getTextPane();
		int[] rows = table.getSelectedRows();
		StringBuilder sb = new StringBuilder();
		for (int i : rows) {
			sb.append(lines.get(i));
			sb.append('\n');
		}

		return sb.toString();
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
			sb.append('\n');
		}
		return sb.toString();
	}

	public void setText(String t) {
		resetWidth();

		lines.clear();
		lines.trimToSize(); // release the potentially big array
		if (t != null && t.length() != 0) {
			lines.addAll(Arrays.asList(t.split("\\n", -1)));
		}
		((AbstractTableModel) getTextPane().getModel()).fireTableDataChanged();
		if (!lines.isEmpty()) fitTextWidth(0, lines.size() - 1);
	}

	private void resetWidth() {
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setMinWidth(0);
		col.setPreferredWidth(0);
	}

	public void processStream(InputStream input) {
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String line;

		try {
			while ((line = br.readLine()) != null) {
				append(line + "\n");
			}
		} catch (IOException e) {
			append("SYSTEM: IOException: " + e);
		}
		scrollToBottom();
	}

	public void scrollToBottom() {
		final JTable table = getTextPane();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
			}
		});
	}
}
