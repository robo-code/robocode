/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten to compact code
 *     - Added Javadoc comments
 *     - Added Paint button and isPaintEnabled()
 *     - Added Robocode SG check box and isSGPaintEnabled() for enabling Robocode
 *       SG compatibility
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added Pause button
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.peer.proxies.IDisplayRobotProxy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotDialog extends JFrame {
	private RobocodeManager manager;
	private IDisplayRobotProxy robotView;
	private ConsoleScrollPane scrollPane;
	private JPanel robotDialogContentPane;
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton clearButton;
	private JButton killButton;
	private JToggleButton paintButton;
	private JCheckBox sgCheckBox;
	private JToggleButton pauseButton;

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
			} else if (src == RobotDialog.this.getPauseButton()) {
				pauseResumeButtonActionPerformed();
			}
		}
	};

	private BattleManager.PauseResumeListener pauseResumeHandler = new BattleManager.PauseResumeListener() {
		public void battlePaused() {
			getPauseButton().setSelected(true);
		}

		public void battleResumed() {
			getPauseButton().setSelected(false);
		}
	};

	/**
	 * RobotDialog constructor
	 */
	public RobotDialog(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	/**
	 * Initialize the dialog
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getRobotDialogContentPane());
		if (manager.isSlave()) {
			getKillButton().setEnabled(false);
		}

		manager.getBattleManager().addListener(pauseResumeHandler);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		manager.getBattleManager().removeListener(pauseResumeHandler);
	}

	/**
	 * Sets the robot peer of this dialog
	 *
	 * @param robotView the robot peer of this dialog
	 */
	public void setRobotPeer(IDisplayRobotProxy robotView) {
		this.robotView = robotView;
		getConsoleScrollPane().setText("");
		if (robotView != null && robotView.getOut() != null) {
			getConsoleScrollPane().processStream(robotView.getOut().getInputStream());
		}
	}

	/**
	 * When robotDialog is packed, we want to set a reasonable size. However,
	 * after that, we need a null preferred size so the scrollpane will scroll.
	 * (preferred size should be based on the text inside)
	 */
	@Override
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
			buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.add(getOkButton());
			buttonPanel.add(getClearButton());
			buttonPanel.add(getKillButton());
			buttonPanel.add(getPaintButton());
			buttonPanel.add(getSGCheckBox());
			buttonPanel.add(getPauseButton());
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
	 * Returns the Pause button.
	 *
	 * @return the Pause button
	 */
	private JToggleButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JToggleButton("Pause/Debug");
			pauseButton.addActionListener(eventHandler);
		}
		return pauseButton;
	}

	/**
	 * Returns a new button with event handler and with the specified text
	 *
	 * @param text The text of the button
	 * @return a new button with event handler and with the specified text
	 */
	private JButton getNewButton(String text) {
		JButton button = new JButton(text);

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
		robotView.displayKill();
	}

	/**
	 * Is called when the Paint button has been activated
	 */
	private void paintButtonActionPerformed() {
		robotView.displaySetPaintEnabled(getPaintButton().isSelected());
	}

	/**
	 * Is called when the SG check box has been activated
	 */
	private void sgCheckBoxActionPerformed() {
		robotView.displaySetSGPaintEnabled(getSGCheckBox().isSelected());
	}

	/**
	 * Is called when the Pause/Resume button has been activated
	 */
	private void pauseResumeButtonActionPerformed() {
		BattleManager battleManager = manager.getBattleManager();

		if (battleManager.isPaused()) {
			battleManager.resumeBattle();
		} else {
			battleManager.pauseBattle();
		}
	}
}
