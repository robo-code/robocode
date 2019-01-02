/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.ui.IWindowManagerExt;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


/**
 * @author Pavel Savara (original)
 */
@SuppressWarnings("serial")
public abstract class BaseScoreDialog extends JDialog {
	protected final IWindowManagerExt windowManager;

	private final EventHandler eventHandler = new EventHandler();
	protected JPanel contentPane;
	protected JScrollPane scrollPane;
	protected JTable table;
	protected Dimension tableSize;

	public BaseScoreDialog(IWindowManager windowManager, boolean modal) {
		// In order to prevent the dialog from hiding, null must be given as 'owner' parameter
		super(windowManager != null ? windowManager.getRobocodeFrame() : null, modal);
		this.windowManager = (IWindowManagerExt) windowManager;
	}

	protected void initialize() {
		addComponentListener(eventHandler);
		setContentPane(getDialogContentPane());
	}

	/**
	 * Return the content pane.
	 *
	 * @return JPanel
	 */
	protected JPanel getDialogContentPane() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(getScrollPane(), "Center");
		}
		return contentPane;
	}

	/**
	 * Return the scroll pane
	 *
	 * @return JScrollPane
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
			scrollPane.setViewportView(getTable());
			scrollPane.setColumnHeaderView(table.getTableHeader());
			scrollPane.addComponentListener(eventHandler);

			tableSize = new Dimension(getTable().getColumnModel().getTotalColumnWidth(),
					getTable().getModel().getRowCount() * (getTable().getRowHeight()));
			table.setPreferredScrollableViewportSize(tableSize);
			table.setPreferredSize(tableSize);
			table.setMinimumSize(tableSize);
		}
		return scrollPane;
	}

	/**
	 * Return the table.
	 *
	 * @return JTable
	 */
	protected JTable getTable() {
		if (table == null) {
			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setColumnSelectionAllowed(true);
			table.setRowSelectionAllowed(true);
			table.getTableHeader().setReorderingAllowed(false);
		}
		return table;
	}

	private class EventHandler implements ComponentListener {
		public void componentShown(ComponentEvent e) {
			onDialogShown();
		}

		public void componentHidden(ComponentEvent e) {
			onDialogHidden();
		}

		public void componentResized(ComponentEvent e) {
			if (e.getSource() == BaseScoreDialog.this.getScrollPane()) {
				scrollPaneComponentResized();
			}
		}

		public void componentMoved(ComponentEvent e) {}
	}

	protected abstract AbstractTableModel getTableModel();

	protected void onDialogShown() {}

	protected void onDialogHidden() {
		dispose();
	}

	protected void scrollPaneComponentResized() {
		// This code is not working...
		Dimension scrollPaneExtent = getScrollPane().getViewport().getExtentSize();

		if (tableSize != null && (tableSize.width < scrollPaneExtent.width)) {
			getTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			getTable().setSize(scrollPaneExtent);
			getTable().sizeColumnsToFit(-1);
		} else {
			if (tableSize != null) {
				getTable().setSize(tableSize);
				getTable().sizeColumnsToFit(-1);
			}
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
		validate();
		repaint();
	}

	protected void setResultsData() {
		getTable().setModel(getTableModel());
		int maxScoreColWidth = 0;

		for (int x = 0; x < getTableModel().getColumnCount(); x++) {
			if (x != 1) {
				getTable().getColumnModel().getColumn(x).setCellRenderer(new ResultsTableCellRenderer(false));
			}
			TableColumn column = getTable().getColumnModel().getColumn(x);
			Component comp;

			column.setHeaderRenderer(new ResultsTableCellRenderer(true));
			comp = column.getHeaderRenderer().getTableCellRendererComponent(null, column.getHeaderValue(), false, false,
					0, 0);
			int width = comp.getPreferredSize().width;

			for (int y = 0; y < getTableModel().getRowCount(); y++) {
				comp = getTable().getDefaultRenderer(getTableModel().getColumnClass(x)).getTableCellRendererComponent(
						getTable(), getTableModel().getValueAt(y, x), false, false, 0, x);
				if (comp.getPreferredSize().width > width) {
					width = comp.getPreferredSize().width;
				}
			}
			TableColumn col = getTable().getColumnModel().getColumn(x);

			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setWidth(width);

			if (x >= 3 && width > maxScoreColWidth) {
				maxScoreColWidth = width;
			}
		}
	}
}
