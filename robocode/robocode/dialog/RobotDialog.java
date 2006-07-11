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
 *     - Rewritten to compact code
 *     - Added Javadoc comments
 *     - Added "Paint" button and isPaintEnabled()
 *     - Added "Robocode SG" check box and isSGPaintEnabled() for enabling Robocode SG
 *       compatibility
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.peer.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobotDialog extends JFrame {
	public final static Dimension BUTTON_SIZE = new Dimension(80, 25);
	private RobotPeer robotPeer;
	private boolean slave;
	private ConsoleScrollPane scrollPane;
	private JPanel robotDialogContentPane;
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton clearButton;
	private JButton killButton;
	private JToggleButton paintButton;
	private JCheckBox sgCheckBox;

	private ActionListener eventHandler = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == RobotDialog.this.getOkButton()) {
				okButtonActionPerformed();
			} else if (src == RobotDialog.this.getClearButton()) {
				clearButtonActionPerformed();
			} else if (src == RobotDialog.this.getKillButton()) {
				killButtonActionPerformed();
			} else if (src == RobotDialog.this.getPaintButton()) {
				paintButtonActionPerformed();
			} else if (src == RobotDialog.this.getSGCheckBox()) {
				sgCheckBoxActionPerformed();
			}
		}
	};

	/**
	 * RobotDialog constructor
	 */
	public RobotDialog(boolean slave) {
		super();
		this.slave = slave;
		initialize();
	}

	/**
	 * Initialize the dialog
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getRobotDialogContentPane());
		if (slave) {
			getKillButton().setEnabled(false);
		}
	}

	/**
	 * Sets the robot peer of this dialog
	 *
	 * @param robotPeer the robot peer of this dialog
	 */
	public void setRobotPeer(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		getConsoleScrollPane().setText("");
		getConsoleScrollPane().processStream(robotPeer.getOut().getInputStream());
	}

	/**
	 * When robotDialog is packed, we want to set a reasonable size. However,
	 * after that, we need a null preferred size so the scrollpane will scroll.
	 * (preferred size should be based on the text inside)
	 */
	public void pack() {
		getConsoleScrollPane().setPreferredSize(new Dimension(426, 200));
		super.pack();
		getConsoleScrollPane().setPreferredSize(null);
	}

	/**
	 * Returns true if Paint is enabled; false otherwise
	 *
	 * @return true if Paint is enabled; false otherwise
	 */
	public boolean isPaintEnabled() {
		return getPaintButton().isSelected();
	}

	/**
	 * Returns true if the Robocode SG paint is enabled; false otherwise
	 *
	 * @return true if the Robocode SG paint enabled; false otherwise
	 */
	public boolean isSGPaintEnabled() {
		return getSGCheckBox().isSelected();
	}

	/**
	 * Returns the dialog's content pane
	 * 
	 * @return the dialog's content pane
	 */
	private JPanel getRobotDialogContentPane() {
		if (robotDialogContentPane == null) {
			robotDialogContentPane = new JPanel();
			robotDialogContentPane.setLayout(new BorderLayout());
			robotDialogContentPane.add(getConsoleScrollPane());
			robotDialogContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return robotDialogContentPane;
	}

	/**
	 * Returns the console scroll pane
	 * 
	 * @return the console scroll pane
	 */
	private ConsoleScrollPane getConsoleScrollPane() {
		if (scrollPane == null) {
			scrollPane = new ConsoleScrollPane();
			JTextArea textPane = scrollPane.getTextPane();

			textPane.setBackground(Color.DARK_GRAY);
			textPane.setForeground(Color.WHITE);
		}
		return scrollPane;
	}

	/**
	 * Returns the button panel
	 * 
	 * @return the button panel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(getOkButton());
			buttonPanel.add(getClearButton());
			buttonPanel.add(getKillButton());
			buttonPanel.add(getPaintButton());
			buttonPanel.add(getSGCheckBox());
		}
		return buttonPanel;
	}

	/**
	 * Returns the OK button
	 * 
	 * @return the OK button
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = getNewButton("OK");
		}
		return okButton;
	}

	/**
	 * Returns the Clear button
	 * 
	 * @return the Clear button
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = getNewButton("Clear");
		}
		return clearButton;
	}

	/**
	 * Returns the Kill button.
	 * 
	 * @return the Kill button
	 */
	private JButton getKillButton() {
		if (killButton == null) {
			killButton = getNewButton("Kill Robot");
		}
		return killButton;
	}

	/**
	 * Returns the Paint button.
	 * 
	 * @return the Paint button
	 */
	private JToggleButton getPaintButton() {
		if (paintButton == null) {
			paintButton = new JToggleButton("Paint");
			Utils.setFixedSize(paintButton, BUTTON_SIZE);
			paintButton.addActionListener(eventHandler);
		}
		return paintButton;
	}

	/**
	 * Returns the SG checkbox.
	 * 
	 * @return the SG checkbox
	 */
	private JCheckBox getSGCheckBox() {
		if (sgCheckBox == null) {
			sgCheckBox = new JCheckBox("Robocode SG");
			sgCheckBox.addActionListener(eventHandler);
		}
		return sgCheckBox;
	}

	/**
	 * Returns a new button with event handler and with the specified text
	 *
	 * @param text The text of the button
	 * @return a new button with event handler and with the specified text
	 */
	private JButton getNewButton(String text) {
		JButton button = new JButton(text);

		Utils.setFixedSize(button, BUTTON_SIZE);
		button.addActionListener(eventHandler);
		return button;
	}

	/**
	 * Is called when the OK button has been activated
	 */
	private void okButtonActionPerformed() {
		dispose();
	}

	/**
	 * Is called when the Clear button has been activated
	 */
	private void clearButtonActionPerformed() {
		getConsoleScrollPane().setText("");
	}

	/**
	 * Is called when the Kill button has been activated
	 */
	private void killButtonActionPerformed() {
		robotPeer.setDead(true);
	}

	/**
	 * Is called when the Paint button has been activated
	 */
	private void paintButtonActionPerformed() {
		robotPeer.setPaintEnabled(getPaintButton().isSelected());
	}

	/**
	 * Is called when the SG check box has been activated
	 */
	private void sgCheckBoxActionPerformed() {
		robotPeer.setSGPaintEnabled(getSGCheckBox().isSelected());
	}
}
