/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Luis Crespo & Mathew A. Nelson
 *     - Original implementation
 *     Flemming N. Larsen
 *     - Totally rewritten to contain the functionality for both the
 *       RankingDialog and ResultsDialog (code reuse)
 *     - Changed to be a independent frame instead of a dialog
 *******************************************************************************/
package robocode.dialog;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import robocode.battle.BattleRankingTableModel;
import robocode.battle.BattleResultsTableModel;
import robocode.manager.RobocodeManager;


/**
 * Frame to display the battle results or ranking during battles.
 *
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RankingDialog extends JFrame {
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;

	private JPanel buttonPanel;
	private JButton okButton;
	private JButton saveButton;

	private RobocodeManager manager;

	private AbstractTableModel tableModel;

	private Dimension tableSize;

	private Thread thread;

	private EventHandler eventHandler = new EventHandler();

	private boolean isCurrentRankings;

	/**
	 * RankingDialog constructor
	 */
	public RankingDialog(RobocodeManager manager, boolean isCurrentRankings) {
		super();
		this.manager = manager;
		this.isCurrentRankings = isCurrentRankings;
		initialize();
	}

	/**
	 * Initializes the frame
	 */
	private void initialize() {
		setTitle(isCurrentRankings ? "Ranking" : ((BattleResultsTableModel) getTableModel()).getTitle());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getDialogContentPane());

		addComponentListener(eventHandler);

		if (isCurrentRankings) {
			addWindowListener(
					new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getOptionsShowRankingCheckBoxMenuItem().setState(
							false);
				}
			});
		}
	}

	/**
	 * Return the content pane.
	 *
	 * @return JPanel
	 */
	private JPanel getDialogContentPane() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(getScrollPane(), "Center");
			if (!isCurrentRankings) {
				contentPane.add(getButtonPanel(), "South");
			}
		}
		return contentPane;
	}

	/**
	 * Return the buttonPanel.
	 *
	 * @return JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());
			buttonPanel.add(getOkButton(), "East");
			buttonPanel.add(getSaveButton(), "West");
		}
		return buttonPanel;
	}

	/**
	 * Return the okButton
	 *
	 * @return JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(eventHandler);
			WindowUtil.setFixedSize(okButton, new Dimension(80, 25));
		}
		return okButton;
	}

	/**
	 * Return the saveButton
	 *
	 * @return JButton
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("Save");
			saveButton.addActionListener(eventHandler);
			WindowUtil.setFixedSize(saveButton, new Dimension(80, 25));
		}
		return saveButton;
	}

	/**
	 * Return the scroll pane
	 *
	 * @return JScrollPane
	 */
	private JScrollPane getScrollPane() {
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
	private JTable getTable() {
		if (table == null) {
			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setColumnSelectionAllowed(true);
			table.setRowSelectionAllowed(true);
			table.getTableHeader().setReorderingAllowed(false);
			setResultsData();
		}
		return table;
	}

	private void setResultsData() {
		getTable().setModel(getTableModel());
		int maxScoreColWidth = 0;

		for (int x = 0; x < getTableModel().getColumnCount(); x++) {
			if (x != 1) {
				getTable().getColumnModel().getColumn(x).setCellRenderer(new ResultsTableCellRenderer(false));
			}
			TableColumn column = getTable().getColumnModel().getColumn(x);
			Component comp = null;

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

	private AbstractTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = isCurrentRankings
					? new BattleRankingTableModel(manager)
					: new BattleResultsTableModel(manager.getBattleManager().getBattle());
		}
		return tableModel;
	}

	private void startRepaintThread() {
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					while (thread == Thread.currentThread()) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {}

						table.setSize(table.getColumnModel().getTotalColumnWidth(),
								table.getModel().getRowCount() * table.getRowHeight());

						repaint();
					}
				}
			};
			thread.start();
		}
	}

	private void stopRepaintThread() {
		thread = null;
	}

	private class EventHandler implements ActionListener, ComponentListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == RankingDialog.this.getOkButton()) {
				okButtonActionPerformed();
			} else if (source == RankingDialog.this.getSaveButton()) {
				saveButtonActionPerformed();
			}
		}

		public void componentShown(ComponentEvent e) {
			if (e.getSource() == RankingDialog.this) {
				startRepaintThread();
			}
		}

		public void componentHidden(ComponentEvent e) {
			if (e.getSource() == RankingDialog.this) {
				stopRepaintThread();
			}
		}

		public void componentResized(ComponentEvent e) {
			if (e.getSource() == RankingDialog.this.getScrollPane()) {
				scrollPaneComponentResized(e);
			}
		}

		public void componentMoved(ComponentEvent e) {}
	}

	private void scrollPaneComponentResized(ComponentEvent componentEvent) {
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

	private void okButtonActionPerformed() {
		dispose();
	}

	private void saveButtonActionPerformed() {
		manager.getWindowManager().showSaveResultsDialog();
	}
}
