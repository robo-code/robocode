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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.util.*;
/**
 * Insert the type's description here.
 * Creation date: (4/23/2001 2:22:53 PM)
 * @author: Mathew A. Nelson
 */
public class ConsoleDialog extends javax.swing.JDialog {
	private javax.swing.JPanel consoleDialogContentPane = null;
	private javax.swing.JPanel buttonsPanel = null;
	private ConsoleScrollPane scrollPane = null;
	private javax.swing.JButton okButton = null;
	private JMenu editMenu = null;
	private JMenuItem editCopyMenuItem = null;
	private JMenuBar consoleDialogMenuBar = null;
	EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ConsoleDialog.this.getOkButton()
				|| e.getSource() == getConsoleDialogContentPane()) 
				okButtonActionPerformed();
			if (e.getSource() == getEditCopyMenuItem())
			{
				editCopyActionPerformed();
			}
		}
	}

/**
 * CompilerOutputDialog constructor comment.
 */
public ConsoleDialog() {
	super();
	initialize();
}
/**
 * CompilerOutputDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ConsoleDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 2:29:23 PM)
 * @param text java.lang.String
 */
public synchronized void append(String text) {
	getScrollPane().append(text);
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 8:03:44 PM)
 */
public void editCopyActionPerformed() {
//	System.out.println("Copy");
	java.awt.datatransfer.StringSelection ss;
	String s = 	getScrollPane().getSelectedText();
	if (s == null)
		s = getScrollPane().getText();
	ss = new java.awt.datatransfer.StringSelection(s);
	Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,null);
}
/**
 * Return the buttonsPanel
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getButtonsPanel() {
	if (buttonsPanel == null) {
		try {
			buttonsPanel = new javax.swing.JPanel();
			buttonsPanel.setName("buttonsPanel");
			buttonsPanel.setPreferredSize(new java.awt.Dimension(100, 30));
			buttonsPanel.setLayout(new java.awt.GridBagLayout());
			buttonsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
			buttonsPanel.setMaximumSize(new java.awt.Dimension(1000, 30));

			java.awt.GridBagConstraints constraintsOKButton = new java.awt.GridBagConstraints();
			constraintsOKButton.gridx = 1; constraintsOKButton.gridy = 1;
			constraintsOKButton.ipadx = 34;
			constraintsOKButton.insets = new java.awt.Insets(2, 173, 3, 168);
			getButtonsPanel().add(getOkButton(), constraintsOKButton);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return buttonsPanel;
}
/**
 * Return the compilerOutputDialogContentPane
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getConsoleDialogContentPane() {
	if (consoleDialogContentPane == null) {
		try {
			consoleDialogContentPane = new javax.swing.JPanel();
			consoleDialogContentPane.setName("consoleDialogContentPane");
			consoleDialogContentPane.setLayout(new java.awt.BorderLayout());
			consoleDialogContentPane.add(getButtonsPanel(), "South");
			consoleDialogContentPane.add(getScrollPane(), "Center");
			consoleDialogContentPane.registerKeyboardAction(eventHandler,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return consoleDialogContentPane;
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 8:01:01 PM)
 * @return javax.swing.JMenuBar
 */
public javax.swing.JMenuBar getConsoleDialogMenuBar() {
	if (consoleDialogMenuBar == null)
	{
		consoleDialogMenuBar = new JMenuBar();
		consoleDialogMenuBar.add(getEditMenu());
	}
	return consoleDialogMenuBar;
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 8:01:06 PM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getEditCopyMenuItem() {
	if (editCopyMenuItem == null)
	{
		editCopyMenuItem = new JMenuItem("Copy");
		editCopyMenuItem.addActionListener(eventHandler);
	}
	return editCopyMenuItem;
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 8:01:11 PM)
 * @return javax.swing.JMenu
 */
public javax.swing.JMenu getEditMenu() {
	if (editMenu == null)
	{
		editMenu = new JMenu("Edit");
		editMenu.add(getEditCopyMenuItem());
	}
	return editMenu;
}
/**
 * Return the okButton
 * @return javax.swing.JButton
 */
public javax.swing.JButton getOkButton() {
	if (okButton == null) {
		try {
			okButton = new javax.swing.JButton();
			okButton.setName("okButton");
			okButton.setText("OK");
			okButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return okButton;
}
/**
 * Return the scrollPane.
 * @return javax.swing.JScrollPane
 */
private ConsoleScrollPane getScrollPane() {
	if (scrollPane == null) {
		try {
			scrollPane = new ConsoleScrollPane();
			scrollPane.setName("consoleScrollPane");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return scrollPane;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("CompilerOutputDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(426, 240);
		setContentPane(getConsoleDialogContentPane());
		//getRootPane().setDefaultButton(getOkButton());
		setJMenuBar(getConsoleDialogMenuBar());
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 1:41:21 PM)
 * @param e java.lang.Exception
 */
public void log(String s) {
	Utils.log(s);
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
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	
	dispatchEvent(evt);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (11/7/2001 4:59:58 PM)
 * @param in java.io.InputStream
 */
public void processStream(java.io.InputStream in) {
	scrollPane.processStream(in);
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2001 3:26:12 PM)
 */
public void scrollToBottom() {
	getScrollPane().scrollToBottom();
}
/**
 * Insert the method's description here.
 * Creation date: (4/23/2001 2:29:23 PM)
 * @param text java.lang.String
 */
public void setText(String text) {
	getScrollPane().setText(text);
}
}
