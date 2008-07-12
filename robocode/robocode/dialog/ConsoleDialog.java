/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *    - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ConsoleDialog extends JDialog {
	private JPanel consoleDialogContentPane;
	private JPanel buttonsPanel;
	private ConsoleScrollPane scrollPane;
	private JButton okButton;
	private JMenu editMenu;
	private JMenuItem editCopyMenuItem;
	private JMenuBar consoleDialogMenuBar;
	private EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ConsoleDialog.this.getOkButton() || e.getSource() == getConsoleDialogContentPane()) {
				okButtonActionPerformed();
			}
			if (e.getSource() == getEditCopyMenuItem()) {
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
	 *
	 * @param owner Frame
	 * @param modal boolean
	 */
	public ConsoleDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initialize();
	}

	public void append(String text) {
		getScrollPane().append(text);
	}

	public void editCopyActionPerformed() {
		StringSelection ss;
		String s = getScrollPane().getSelectedText();

		if (s == null) {
			s = getScrollPane().getText();
		}
		ss = new StringSelection(s);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}

	/**
	 * Return the buttonsPanel
	 *
	 * @return JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setPreferredSize(new Dimension(100, 30));
			buttonsPanel.setLayout(new GridBagLayout());
			buttonsPanel.setMinimumSize(new Dimension(20, 20));
			buttonsPanel.setMaximumSize(new Dimension(1000, 30));

			GridBagConstraints constraintsOKButton = new GridBagConstraints();

			constraintsOKButton.gridx = 1;
			constraintsOKButton.gridy = 1;
			constraintsOKButton.ipadx = 34;
			constraintsOKButton.insets = new Insets(2, 173, 3, 168);
			getButtonsPanel().add(getOkButton(), constraintsOKButton);
		}
		return buttonsPanel;
	}

	/**
	 * Return the compilerOutputDialogContentPane
	 *
	 * @return JPanel
	 */
	private JPanel getConsoleDialogContentPane() {
		if (consoleDialogContentPane == null) {
			consoleDialogContentPane = new JPanel();
			consoleDialogContentPane.setLayout(new BorderLayout());
			consoleDialogContentPane.add(getButtonsPanel(), "South");
			consoleDialogContentPane.add(getScrollPane(), "Center");
			consoleDialogContentPane.registerKeyboardAction(eventHandler, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					JComponent.WHEN_FOCUSED);
		}
		return consoleDialogContentPane;
	}

	public JMenuBar getConsoleDialogMenuBar() {
		if (consoleDialogMenuBar == null) {
			consoleDialogMenuBar = new JMenuBar();
			consoleDialogMenuBar.add(getEditMenu());
		}
		return consoleDialogMenuBar;
	}

	public JMenuItem getEditCopyMenuItem() {
		if (editCopyMenuItem == null) {
			editCopyMenuItem = new JMenuItem("Copy");
			editCopyMenuItem.addActionListener(eventHandler);
		}
		return editCopyMenuItem;
	}

	public JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu("Edit");
			editMenu.add(getEditCopyMenuItem());
		}
		return editMenu;
	}

	/**
	 * Return the okButton
	 *
	 * @return JButton
	 */
	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(eventHandler);
		}
		return okButton;
	}

	/**
	 * Return the scrollPane.
	 *
	 * @return JScrollPane
	 */
	private ConsoleScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new ConsoleScrollPane();
		}
		return scrollPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(426, 240);
		setContentPane(getConsoleDialogContentPane());
		setJMenuBar(getConsoleDialogMenuBar());
	}

	public void okButtonActionPerformed() {
		dispose();
	}

	public void processStream(java.io.InputStream in) {
		scrollPane.processStream(in);
	}

	public void scrollToBottom() {
		getScrollPane().scrollToBottom();
	}

	public void setText(String text) {
		getScrollPane().setText(text);
	}
}
