/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Luis Crespo
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Integration
 *     - When this dialog is closed, the state of the check box for the Ranking
 *       Panel in the Options Menu is set to unchecked
 *     - The table height in the scroll panel is now automatically resized when
 *       repainted in the repaint thread
 *     - Access to managers is now static
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import robocode.battle.*;
import robocode.manager.WindowManager;


/**
 * Dialog to display the running ranking of a battle
 * 
 * @author Luis Crespo & Flemming N. Larsen
 */
public class RankingDialog extends JDialog {
	private JPanel rankingContentPane;
	private JScrollPane resultsScrollPane;
	private JTable resultsTable;
	private BattleRankingTableModel rankingTableModel;
	private Thread thread;
		
	/**
	 * RankingDialog constructor
	 */
	public RankingDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * Initializes the class
	 */
	private void initialize() {
		setTitle("Ranking");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getRankingContentPane());

		addWindowListener(
				new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				WindowManager.getRobocodeFrame().getRobocodeMenuBar().getOptionsShowRankingCheckBoxMenuItem().setState(
						false);
			}
		});

		addComponentListener(new ComponentAdapter() {
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
		});
	}

	/**
	 * Return the resultsTable.
	 * 
	 * @return JTable
	 */
	private JTable getResultsTable() {
		if (resultsTable == null) {
			resultsTable = new JTable();
			resultsTable.setColumnSelectionAllowed(true);
			resultsTable.setRowSelectionAllowed(true);
			resultsTable.getTableHeader().setReorderingAllowed(false);
			setResultsData();
		}
		return resultsTable;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * 
	 * @return JPanel
	 */
	private JPanel getRankingContentPane() {
		if (rankingContentPane == null) {
			rankingContentPane = new JPanel();
			rankingContentPane.setLayout(new BorderLayout());
			rankingContentPane.add(getRankingScrollPane(), "Center");
		}
		return rankingContentPane;
	}

	/**
	 * Return the rankingScrollPane
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getRankingScrollPane() {
		if (resultsScrollPane == null) {
			resultsScrollPane = new JScrollPane();
			resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			resultsScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
			resultsScrollPane.setViewportView(getResultsTable());
			resultsScrollPane.setColumnHeaderView(resultsTable.getTableHeader());
		}
		return resultsScrollPane;
	}

	private void setResultsData() {
		getResultsTable().setModel(getBattleRankingTableModel());
		int maxScoreColWidth = 0;

		// TODO: the "name" column is getting too much width compared with the two others
		// ...Columns should get a fixed width instead
		for (int x = 0; x < getBattleRankingTableModel().getColumnCount(); x++) {
			if (x != 1) {
				getResultsTable().getColumnModel().getColumn(x).setCellRenderer(new ResultsTableCellRenderer(false));
			}
			TableColumn column = getResultsTable().getColumnModel().getColumn(x);
			Component comp = null;

			column.setHeaderRenderer(new ResultsTableCellRenderer(true));
			comp = column.getHeaderRenderer().getTableCellRendererComponent(null, column.getHeaderValue(), false, false,
					0, 0);
			int width = comp.getPreferredSize().width;

			for (int y = 0; y < getBattleRankingTableModel().getRowCount(); y++) {
				comp = getResultsTable().getDefaultRenderer(getBattleRankingTableModel().getColumnClass(x)).getTableCellRendererComponent(
						getResultsTable(), getBattleRankingTableModel().getValueAt(y, x), false, false, 0, x);
				if (comp.getPreferredSize().width > width) {
					width = comp.getPreferredSize().width;
				}
			}
			TableColumn col = getResultsTable().getColumnModel().getColumn(x);

			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setWidth(width);

			if (x >= 2 && width > maxScoreColWidth) {
				maxScoreColWidth = width;
			}
		}
	}

	private BattleRankingTableModel getBattleRankingTableModel() {
		if (rankingTableModel == null) {
			rankingTableModel = new BattleRankingTableModel();
		}
		return rankingTableModel;
	}

	private void startRepaintThread() {
		if (thread == null) {
			thread = new Thread() {
				public void run() {
					while (thread == Thread.currentThread()) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {}

						resultsTable.setSize(resultsTable.getColumnModel().getTotalColumnWidth(),
								resultsTable.getModel().getRowCount() * resultsTable.getRowHeight());

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
}
