/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Corrected table cell heights when rendering
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import robocode.battle.*;


/**
 * Dialog to display results (scores) of a battle.
 * 
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class ResultsDialog extends JDialog {

	private JPanel buttonPanel;
	private EventHandler eventHandler = new EventHandler();
	private JButton okButton;
	private JPanel resultsDialogContentPane;
	private JScrollPane resultsScrollPane;
	private JTable resultsTable;

	private Battle battle;
	private BattleResultsTableModel battleResultsTableModel;
	private Dimension tableSize;

	/**
	 * Return the resultsTable.
	 * 
	 * @return JTable
	 */
	private JTable getResultsTable() {
		if (resultsTable == null) {
			resultsTable = new JTable();
			resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			resultsTable.setColumnSelectionAllowed(true);
			resultsTable.setRowSelectionAllowed(true);
			resultsTable.getTableHeader().setReorderingAllowed(false);
			setResultsData();
		}
		return resultsTable;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(getBattleResultsTableModel().getTitle());
		setContentPane(getResultsDialogContentPane());
	}

	class EventHandler implements ActionListener, ComponentListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ResultsDialog.this.getOkButton()) {
				okButtonActionPerformed();
			}
		}

		public void componentHidden(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {
			if (e.getSource() == ResultsDialog.this.getResultsScrollPane()) {
				resultsScrollPaneComponentResized(e);
			}
		}

		public void componentShown(ComponentEvent e) {}
	}

	/**
	 * ResultsDialog constructor comment.
	 * 
	 * @param owner Frame
	 */
	public ResultsDialog(Frame owner, Battle battle) {
		super(owner);
		this.battle = battle;
		initialize();
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
			okButton.setPreferredSize(new Dimension(80, 25));
			okButton.setMaximumSize(new Dimension(80, 25));
			okButton.setMinimumSize(new Dimension(80, 25));
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * 
	 * @return JPanel
	 */
	private JPanel getResultsDialogContentPane() {
		if (resultsDialogContentPane == null) {
			resultsDialogContentPane = new JPanel();
			resultsDialogContentPane.setLayout(new BorderLayout());
			resultsDialogContentPane.add(getResultsScrollPane(), "Center");
			resultsDialogContentPane.add(getButtonPanel(), "South");
		}
		return resultsDialogContentPane;
	}

	/**
	 * Return the resultsScrollPane
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getResultsScrollPane() {
		if (resultsScrollPane == null) {
			resultsScrollPane = new JScrollPane();
			resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			resultsScrollPane.setViewportView(getResultsTable());
			resultsScrollPane.setColumnHeaderView(resultsTable.getTableHeader());
			resultsScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
			resultsScrollPane.addComponentListener(eventHandler);

			tableSize = new Dimension(getResultsTable().getColumnModel().getTotalColumnWidth(),
					getResultsTable().getModel().getRowCount() * (getResultsTable().getRowHeight()));
			resultsTable.setPreferredScrollableViewportSize(tableSize);
			resultsTable.setPreferredSize(tableSize);
			resultsTable.setMinimumSize(tableSize);
		}
		return resultsScrollPane;
	}

	private void okButtonActionPerformed() {
		dispose();
	}

	private void resultsScrollPaneComponentResized(ComponentEvent componentEvent) {
		// This code is not working...
		Dimension scrollPaneExtent = getResultsScrollPane().getViewport().getExtentSize();

		if (tableSize != null && (tableSize.width < scrollPaneExtent.width)) {
			getResultsTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			getResultsTable().setSize(scrollPaneExtent);
			getResultsTable().sizeColumnsToFit(-1);
		} else {
			if (tableSize != null) {
				getResultsTable().setSize(tableSize);
				getResultsTable().sizeColumnsToFit(-1);
			}
			resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
		validate();
		repaint();
	}

	private void setResultsData() {
		getResultsTable().setModel(getBattleResultsTableModel());
		int maxScoreColWidth = 0;

		for (int x = 0; x < getBattleResultsTableModel().getColumnCount(); x++) {
			if (x > 0) {
				getResultsTable().getColumnModel().getColumn(x).setCellRenderer(new ResultsTableCellRenderer(false));
			}
			TableColumn column = getResultsTable().getColumnModel().getColumn(x);
			Component comp = null;

			column.setHeaderRenderer(new ResultsTableCellRenderer(true));
			comp = column.getHeaderRenderer().getTableCellRendererComponent(null, column.getHeaderValue(), false, false,
					0, 0);
			int width = comp.getPreferredSize().width;

			for (int y = 0; y < getBattleResultsTableModel().getRowCount(); y++) {
				comp = getResultsTable().getDefaultRenderer(getBattleResultsTableModel().getColumnClass(x)).getTableCellRendererComponent(
						getResultsTable(), getBattleResultsTableModel().getValueAt(y, x), false, false, 0, x);
				if (comp.getPreferredSize().width > width) {
					width = comp.getPreferredSize().width;
				}
			}
			getResultsTable().getColumnModel().getColumn(x).setPreferredWidth(width);
			getResultsTable().getColumnModel().getColumn(x).setMinWidth(width);
			getResultsTable().getColumnModel().getColumn(x).setWidth(width);
			if (x >= 2 && width > maxScoreColWidth) {
				maxScoreColWidth = width;
			}
		}
	}

	private BattleResultsTableModel getBattleResultsTableModel() {
		if (battleResultsTableModel == null) {
			battleResultsTableModel = new BattleResultsTableModel(battle);
		}
		return battleResultsTableModel;
	}
}