/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import robocode.util.Utils;
import robocode.battle.*;


/**
 * Dialog to display results (scores) of a battle.
 * Creation date: (1/16/2001 3:21:15 PM)
 * @author: Mathew Nelson
 */
public class ResultsDialog extends JDialog {

	class CenteredRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
		boolean isBordered;

		public CenteredRenderer(boolean isBordered) {
			super();
			this.setHorizontalAlignment(JLabel.CENTER);
			this.isBordered = isBordered;
			this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));	    
		}

		public java.awt.Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			if (isBordered) {
				this.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.RAISED));
				this.setBackground(SystemColor.menu);
				this.setForeground(SystemColor.menuText);
			} else if (isSelected) {
				this.setBackground(SystemColor.textHighlight);
				this.setForeground(SystemColor.textHighlightText);
			} else {
				this.setBackground(SystemColor.text);
				this.setForeground(SystemColor.textText);
			}
			this.setText(value.toString());
			return this;
		}
		;
	}

	private Battle battle = null;

	;

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 3:37:23 PM)
	 * @return robocode.Battle
	 */
	private Battle getBattle() {
		return battle;
	}

	/**
	 * Return the resultsTable.
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getResultsTable() {
		if (resultsTable == null) {
			try {
				resultsTable = new javax.swing.JTable();
				resultsTable.setName("ResultsTable");
				// resultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
				resultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
				// resultsTable.setCellSelectionEnabled(true);
				resultsTable.setColumnSelectionAllowed(true);
				resultsTable.setRowSelectionAllowed(true);
				resultsTable.getTableHeader().setReorderingAllowed(false);
				// resultsTable.getTableHeader().set)
				setResultsData();
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return resultsTable;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("ResultsDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			// setSize(640, 480);
			setTitle(getBattleResultsTableModel().getTitle());
			setContentPane(getResultsDialogContentPane());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	private JPanel buttonPanel = null;
	private EventHandler eventHandler = new EventHandler();
	private JPanel mainPanel = null;
	private JButton okButton = null;
	private JPanel resultsDialogContentPane = null;
	private JScrollPane resultsScrollPane = null;
	private JTable resultsTable = null;

	class EventHandler implements java.awt.event.ActionListener, java.awt.event.ComponentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ResultsDialog.this.getOkButton()) { 
				okButtonActionPerformed();
			}
		}
		;
		public void componentHidden(java.awt.event.ComponentEvent e) {}
		;
		public void componentMoved(java.awt.event.ComponentEvent e) {}
		;
		public void componentResized(java.awt.event.ComponentEvent e) {
			if (e.getSource() == ResultsDialog.this.getResultsScrollPane()) {
				resultsScrollPaneComponentResized(e);
			}
		}
		;
		public void componentShown(java.awt.event.ComponentEvent e) {}
		;
	}

	/**
	 * ResultsDialog constructor comment.
	 * @param owner java.awt.Frame
	 */
	public ResultsDialog(java.awt.Frame owner, Battle battle) {
		super(owner);
		this.battle = battle;
		initialize();
	}

	/**
	 * Return the buttonPanel.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new javax.swing.JPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setLayout(new java.awt.BorderLayout());
				// buttonPanel.setPreferredSize(new java.awt.Dimension(200, 34));
				// buttonPanel.setMinimumSize(new java.awt.Dimension(200, 34));
				// buttonPanel.setMaximumSize(new java.awt.Dimension(5000, 5000));
				buttonPanel.add(getOkButton(), "East");
				// getJPanel2().add(getJOkButton(), getJOkButton().getName());
				// , "East");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return buttonPanel;
	}

	/**
	 * Return the buttonPanel.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getMainPanel() {
		if (mainPanel == null) {
			try {
				mainPanel = new javax.swing.JPanel();
				mainPanel.setName("mainPanel");
				mainPanel.setLayout(new java.awt.BorderLayout());
				mainPanel.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
				mainPanel.add(getResultsScrollPane(), "Center");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return mainPanel;
	}

	/**
	 * Return the okButton
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			try {
				okButton = new javax.swing.JButton();
				okButton.setName("okButton");
				okButton.setText("OK");
				// okButton.setBounds(4, 2, 94, 32);
				// okButton.setPreferredSize(new java.awt.Dimension(51, 20));
				// okButton.setMaximumSize(new java.awt.Dimension(51, 20));
				okButton.setPreferredSize(new java.awt.Dimension(80, 25));
				okButton.setMaximumSize(new java.awt.Dimension(80, 25));
				okButton.setMinimumSize(new java.awt.Dimension(80, 25));
				okButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return okButton;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getResultsDialogContentPane() {
		if (resultsDialogContentPane == null) {
			try {
				resultsDialogContentPane = new javax.swing.JPanel();
				resultsDialogContentPane.setName("ResultsDialogContentPane");
				resultsDialogContentPane.setLayout(new java.awt.BorderLayout());
				resultsDialogContentPane.add(getResultsScrollPane(), "Center");
				// resultsDialogContentPane.add(getMainPanel(), "Center");
				resultsDialogContentPane.add(getButtonPanel(), "South");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return resultsDialogContentPane;
	}

	/**
	 * Return the resultsScrollPane
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getResultsScrollPane() {
		if (resultsScrollPane == null) {
			try {
				resultsScrollPane = new javax.swing.JScrollPane();
				resultsScrollPane.setName("resultsScrollPane");
				resultsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				resultsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				resultsScrollPane.setViewportView(getResultsTable());
				resultsScrollPane.setColumnHeaderView(resultsTable.getTableHeader());
				// resultsScrollPane.getViewport().setBackingStoreEnabled(true);
				resultsScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
				resultsScrollPane.addComponentListener(eventHandler);

				/* SwingUtilities.invokeAndWait(
				 new Runnable() {
				 public void run() {
				 resultsTable.validate();
				 resultsTable.doLayout();
				 resultsTable.getTableHeader().validate();
				 resultsTable.getTableHeader().doLayout();
				 resultsTable.getTableHeader().doLayout();
				 resultsTable.getTableHeader().validate();
				 }
				 });
				 */
				tableSize = new Dimension(getResultsTable().getColumnModel().getTotalColumnWidth(),
						getResultsTable().getModel().getRowCount()
						* (getResultsTable().getRowHeight() + getResultsTable().getRowMargin()));
				resultsTable.setPreferredScrollableViewportSize(tableSize);
				// log("Setting preferred size to " + tableSize);
				// resultsScrollPane.getViewport().setPreferredSize(tableSize);
				resultsTable.setPreferredSize(tableSize);
				resultsTable.setMinimumSize(tableSize);

				// log("I would add: " + resultsScrollPane.getColumnHeader().getSize().height);
			

			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return resultsScrollPane;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	private void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 12:19:37 PM)
	 */
	private void okButtonActionPerformed() {
		this.dispose();
	}

	/**
	 * Expand table if we can.  Don't shrink it beyond tableSize - use scrollbars.
	 */
	private void resultsScrollPaneComponentResized(java.awt.event.ComponentEvent componentEvent) {

		// This code is not working...
		Dimension scrollPaneExtent = getResultsScrollPane().getViewport().getExtentSize();

		if (tableSize != null && (tableSize.width < scrollPaneExtent.width)) {
			getResultsTable().setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			getResultsTable().setSize(scrollPaneExtent);
			getResultsTable().sizeColumnsToFit(-1);
		} else {
			if (tableSize != null) {
				getResultsTable().setSize(tableSize);
				getResultsTable().sizeColumnsToFit(-1);
			}
			resultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		}
		this.validate();
		repaint();
		return;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 3:37:23 PM)
	 * @param newBattle robocode.Battle
	 */
	private void setResultsData() {

		getResultsTable().setModel(getBattleResultsTableModel());
		int maxScoreColWidth = 0;

		for (int x = 0; x < getBattleResultsTableModel().getColumnCount(); x++) {
			if (x > 0) {
				getResultsTable().getColumnModel().getColumn(x).setCellRenderer(new CenteredRenderer(false));
			}
			TableColumn column = getResultsTable().getColumnModel().getColumn(x);
			Component comp = null;

			column.setHeaderRenderer(new CenteredRenderer(true));
			comp = column.getHeaderRenderer().getTableCellRendererComponent(null, column.getHeaderValue(), false, false,
					0, 0);

			int width = comp.getPreferredSize().width;

			// log("Column " + x + " has preferred width " + width + " from header.");
			for (int y = 0; y < getBattleResultsTableModel().getRowCount(); y++) {
				comp = getResultsTable().getDefaultRenderer(getBattleResultsTableModel().getColumnClass(x)).getTableCellRendererComponent(
						getResultsTable(), getBattleResultsTableModel().getValueAt(y, x), false, false, 0, x);
				if (comp.getPreferredSize().width > width) {
					width = comp.getPreferredSize().width;
					// log("Row " + y + " increasing width to " + width);
				}
			}
			getResultsTable().getColumnModel().getColumn(x).setPreferredWidth(width);
			getResultsTable().getColumnModel().getColumn(x).setMinWidth(width);
			getResultsTable().getColumnModel().getColumn(x).setWidth(width);
			if (x >= 2 && width > maxScoreColWidth) {
				maxScoreColWidth = width;
			}
		}

		/* // sample code to change a column header value. :)
		 new Thread(new Runnable() {
		 public void run() {
		 try {Thread.sleep(10000);} catch (InterruptedException e) {}
		 ResultsDialog.this.getResultsTable().getColumnModel().getColumn(4).setHeaderValue("blah!");
		 repaint();
		 System.out.println("changed");
		 }
		 }).start();
		 */
	
		/*
		 // Equalize widths
		 for (int x = 2; x < orderedRobots.size() + 2; x++)
		 {
		 getResultsTable().getColumnModel().getColumn(x).setPreferredWidth(maxScoreColWidth);
		 getResultsTable().getColumnModel().getColumn(x).setMinWidth(maxScoreColWidth);
		 getResultsTable().getColumnModel().getColumn(x).setWidth(maxScoreColWidth);
		 }
		 */

	}

	private robocode.battle.BattleResultsTableModel battleResultsTableModel = null;
	private java.awt.Dimension tableSize = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 3:37:23 PM)
	 * @param newBattle robocode.Battle
	 */
	private BattleResultsTableModel getBattleResultsTableModel() {
		if (battleResultsTableModel == null) {
			battleResultsTableModel = new BattleResultsTableModel(battle);
		}
		return battleResultsTableModel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	private void log(String s) {
		Utils.log(s);
	}
}
