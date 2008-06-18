/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
 *     - Changed to pack the dialog to fit the table with the rankings
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup to prevent memory leaks with the battle object in
 *       okButtonActionPerformed()
 *******************************************************************************/
package robocode.dialog;


import robocode.battle.Battle;
import robocode.battle.BattleRankingTableModel;
import robocode.battle.BattleResultsTableModel;
import robocode.manager.RobocodeManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;


/**
 * Frame to display the battle results or ranking during battles.
 *
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
@SuppressWarnings("serial")
public class RankingDialog extends JDialog {
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

	private Battle battle;

	/**
	 * RankingDialog constructor
	 */
	public RankingDialog(RobocodeManager manager, boolean isCurrentRankings) {
		super(manager.getWindowManager().getRobocodeFrame(), !isCurrentRankings);
        this.manager = manager;
		this.isCurrentRankings = isCurrentRankings;
		initialize();
	}

	/**
	 * Initializes the frame
	 */
	private void initialize() {
		// We need to know what battle we are showing results for so that we can
		// clean it up later.
		battle = manager.getBattleManager().getBattle();

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
			pack();
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

	private AbstractTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = isCurrentRankings ? new BattleRankingTableModel(manager) : new BattleResultsTableModel(manager);
		}
		return tableModel;
	}

	private void startRepaintThread() {
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					int rows = table.getModel().getRowCount();

					while (thread == Thread.currentThread()) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// Immediately reasserts the exception by interrupting the caller thread itself
							Thread.currentThread().interrupt();
						}

						if (table.getModel().getRowCount() != rows) {
							rows = table.getModel().getRowCount();

							table.setPreferredSize(
									new Dimension(table.getColumnModel().getTotalColumnWidth(),
									table.getModel().getRowCount() * table.getRowHeight()));
							table.setPreferredScrollableViewportSize(table.getPreferredSize());
							pack();
						}
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
		// Hide the window so as to hopefully not get any more updates that might
		// depend on battle being intact still
		setVisible(false);

		dispose();

		// Since we are displaying the results we have to keep the battle
		// around, but now that we are done we need to clean things up.
		battle.cleanup();
		battle = null;
	}

	private void saveButtonActionPerformed() {
		manager.getWindowManager().showSaveResultsDialog();
	}
}
