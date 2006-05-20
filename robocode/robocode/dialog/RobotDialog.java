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


import robocode.peer.*;
import java.awt.*;
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (1/24/2001 3:14:21 PM)
 * @author: Mathew A. Nelson
 */
public class RobotDialog extends javax.swing.JFrame {
	private javax.swing.JPanel robotDialogContentPane = null;
	private javax.swing.JPanel buttonPanel = null;
	private RobotPeer robotPeer = null;
	EventHandler eventHandler = new EventHandler();
	private ConsoleScrollPane consoleScrollPane = null;
	public java.awt.Rectangle bottomRect = new java.awt.Rectangle(0, 32767, 1, 1);
	private javax.swing.JButton clearButton = null;
	private javax.swing.JButton okButton = null;
	private javax.swing.JButton killButton = null;
	private boolean slave = false;

	class EventHandler implements java.awt.event.ActionListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RobotDialog.this.getOkButton()) { 
				okButtonActionPerformed();
			}
			if (e.getSource() == RobotDialog.this.getClearButton()) {
				clearButtonActionPerformed();
			}
			if (e.getSource() == RobotDialog.this.getKillButton()) {
				killButtonActionPerformed();
			}
		}
		;
		public void windowActivated(java.awt.event.WindowEvent e) {}
		;
		public void windowClosed(java.awt.event.WindowEvent e) {}
		;
		public void windowClosing(java.awt.event.WindowEvent e) {// RobotDialog.this.getConsoleScrollPane().
			// if (e.getSource() == RobotDialog.this) 
			// robotDialogWindowClosing(e);
		}
		;
		public void windowDeactivated(java.awt.event.WindowEvent e) {}
		;
		public void windowDeiconified(java.awt.event.WindowEvent e) {}
		;
		public void windowIconified(java.awt.event.WindowEvent e) {}
		;
		public void windowOpened(java.awt.event.WindowEvent e) {}
		;
	}


	;

	/**
	 * RobotDialog constructor
	 */
	public RobotDialog(boolean slave) {
		super();
		this.slave = slave;
		initialize();
	}

	/**
	 * Comment
	 */
	private void clearButtonActionPerformed() {
		getConsoleScrollPane().setText("");
		return;
	}

	/**
	 * Return the buttonPanel
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new javax.swing.JPanel();
				buttonPanel.setName("buttonPanel");
				// buttonPanel.setPreferredSize(new java.awt.Dimension(100, 30));
				buttonPanel.setLayout(getButtonPanelLayout());
				// buttonPanel.setMaximumSize(new java.awt.Dimension(32767, 32767));
				// buttonPanel.setMinimumSize(new java.awt.Dimension(100, 30));
				buttonPanel.add(getOkButton(), getOkButton().getName());
				buttonPanel.add(getClearButton(), getClearButton().getName());
				buttonPanel.add(getKillButton(), getKillButton().getName());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return buttonPanel;
	}

	/**
	 * Return the JPanel1FlowLayout property value.
	 * @return java.awt.FlowLayout
	 */
	private java.awt.LayoutManager getButtonPanelLayout() {
		java.awt.FlowLayout buttonPanelLayout = null;

		try {
			buttonPanelLayout = new java.awt.FlowLayout();
			buttonPanelLayout.setAlignment(java.awt.FlowLayout.RIGHT);
		} catch (java.lang.Throwable e) {
			log(e);
		}
		;
		return buttonPanelLayout;
	}

	/**
	 * Return the clearButton.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getClearButton() {
		if (clearButton == null) {
			try {
				clearButton = new javax.swing.JButton();
				clearButton.setName("clearButton");
				clearButton.setText("Clear");
				clearButton.setPreferredSize(new java.awt.Dimension(80, 25));
				clearButton.setMaximumSize(new java.awt.Dimension(80, 25));
				clearButton.setMinimumSize(new java.awt.Dimension(80, 25));
				clearButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return clearButton;
	}

	/**
	 * Return the killButton.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getKillButton() {
		if (killButton == null) {
			try {
				killButton = new javax.swing.JButton();
				killButton.setName("killButton");
				killButton.setText("Kill Robot");
				killButton.setPreferredSize(new java.awt.Dimension(80, 25));
				killButton.setMaximumSize(new java.awt.Dimension(80, 25));
				killButton.setMinimumSize(new java.awt.Dimension(80, 25));
				java.awt.Insets i = killButton.getInsets();

				i.left = 0;
				i.right = 0;
				killButton.setMargin(i);

				killButton.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return killButton;
	}

	/**
	 * Return the okButton.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getOkButton() {
		if (okButton == null) {
			try {
				okButton = new javax.swing.JButton();
				okButton.setName("okButton");
				okButton.setText("OK");
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
	 * Return the consoleScrollPane.
	 * @return robocode.dialog.ConsoleScrollPane
	 */
	private ConsoleScrollPane getConsoleScrollPane() {
		if (consoleScrollPane == null) {
			try {
				consoleScrollPane = new ConsoleScrollPane();
				consoleScrollPane.setName("consoleScrollPane");
				consoleScrollPane.getTextPane().setBackground(Color.darkGray);
				consoleScrollPane.getTextPane().setForeground(Color.white);
				// consoleScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return consoleScrollPane;
	}

	/**
	 * Return the robotDialogContentPane.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getRobotDialogContentPane() {
		if (robotDialogContentPane == null) {
			try {
				robotDialogContentPane = new javax.swing.JPanel();
				robotDialogContentPane.setName("JDialogContentPane");
				robotDialogContentPane.setLayout(new java.awt.BorderLayout());
				robotDialogContentPane.add(getButtonPanel(), "South");
				robotDialogContentPane.add(getConsoleScrollPane(), "Center");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return robotDialogContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("RobotDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(426, 240);
			setContentPane(getRobotDialogContentPane());
			addWindowListener(eventHandler);
			if (slave) {
				getKillButton().setEnabled(false);
			}
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Comment
	 */
	public void killButtonActionPerformed() {
		robotPeer.setDead(true);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}

	/**
	 * Comment
	 */
	public void okButtonActionPerformed() {
		dispose();
		return;
	}

	/**
	 * When robotDialog is packed, we want to set a reasonable size.
	 * However, after that, we need a null preferred size so the scrollpane will scroll.
	 * (preferred size should be based on the text inside)
	 * Creation date: (9/5/2001 5:46:18 PM)
	 */
	public void pack() {
		getConsoleScrollPane().setPreferredSize(new Dimension(426, 200));
		super.pack();
		getConsoleScrollPane().setPreferredSize(null);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/23/2001 4:14:47 PM)
	 * @param newSafeRobot robocode.JSafeRobot
	 */
	public void setRobotPeer(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		getConsoleScrollPane().setText("");
		getConsoleScrollPane().processStream(robotPeer.getOut().getInputStream());
	}
}
