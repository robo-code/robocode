/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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


import robocode.battle.events.*;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.manager.RobocodeManager;
import robocode.peer.DebugProperty;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotDialog extends JFrame {
	private RobocodeManager manager;
	private RobotButton robotButton;
	private JTabbedPane tabbedPane;
	private ConsoleScrollPane scrollPane;
	private ConsoleScrollPane propertiesPane;
	private JPanel robotDialogContentPane;
	private JPanel buttonPanel;
	private JButton okButton;
	private JButton clearButton;
	private JButton killButton;
	private JToggleButton paintButton;
	private JCheckBox sgCheckBox;
	private JToggleButton pauseButton;
	private boolean isListening;
	private int robotIndex;
	private RobotSnapshot lastSnapshot;
	private boolean paintSnapshot;
	private Hashtable<String, String> debugProperties = new Hashtable<String, String>();

	private BattleObserver battleObserver = new BattleObserver();

	/**
	 * RobotDialog constructor
	 * @param manager game root
	 * @param robotButton related button
	 */
	public RobotDialog(RobocodeManager manager, RobotButton robotButton) {
		super();
		this.manager = manager;
		this.robotButton = robotButton;
		initialize();
	}

	/**
	 * Initialize the dialog
	 */
	private void initialize() {
		robotIndex = robotButton.getRobotIndex();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getRobotDialogContentPane());
		if (manager.isSlave()) {
			getKillButton().setEnabled(false);
		}
		this.setTitle(robotButton.getRobotName());
		pack();
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			detach();
		} finally {
			super.finalize();
		}
	}

	public void detach() {
		if (isListening) {
			manager.getWindowManager().removeBattleListener(battleObserver);
			isListening = false;
		}
		robotButton.detach();
	}

	public void attach() {
		robotIndex = robotButton.getRobotIndex();
		if (!isListening) {
			isListening = true;
			manager.getWindowManager().addBattleListener(battleObserver);
		}
	}

	public void reset() {
		getConsoleScrollPane().setText(null);
		lastSnapshot = null;
		debugProperties.clear();
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

	/**
	 * Returns the dialog's content pane
	 *
	 * @return the dialog's content pane
	 */
	private JPanel getRobotDialogContentPane() {
		if (robotDialogContentPane == null) {
			robotDialogContentPane = new JPanel();
			robotDialogContentPane.setLayout(new BorderLayout());
			robotDialogContentPane.add(getTabbedPane());
			robotDialogContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return robotDialogContentPane;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setLayout(new BorderLayout());
			tabbedPane.addTab("Console", getConsoleScrollPane());
			tabbedPane.addTab("Properties", getTurnScrollPane());
			// tabbedPane.setSelectedIndex(0);
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					paintSnapshot = (tabbedPane.getSelectedIndex() == 1);
					paintSnapshot();
				}
			});
		}
		return tabbedPane;
	}

	private void paintSnapshot() {
		if (paintSnapshot) {
			if (lastSnapshot != null) {
				StringBuilder sb = new StringBuilder();

				sb.append("energy: ").append(lastSnapshot.getEnergy()).append('\n');
				sb.append("x: ").append(lastSnapshot.getX()).append('\n');
				sb.append("y: ").append(lastSnapshot.getY()).append('\n');
				sb.append("velocity: ").append(lastSnapshot.getVelocity()).append('\n');
				sb.append("heat: ").append(lastSnapshot.getGunHeat()).append('\n');
				sb.append("bodyHeading: rad: ").append(lastSnapshot.getBodyHeading()).append(" deg: ").append(Math.toDegrees(lastSnapshot.getBodyHeading())).append(
						'\n');
				sb.append("gunHeading: rad: ").append(lastSnapshot.getGunHeading()).append(" deg: ").append(Math.toDegrees(lastSnapshot.getGunHeading())).append(
						'\n');
				sb.append("radarHeading: rad: ").append(lastSnapshot.getRadarHeading()).append(" deg: ").append(Math.toDegrees(lastSnapshot.getRadarHeading())).append(
						'\n');
				sb.append("state: ").append(lastSnapshot.getState()).append('\n');
				sb.append('\n');
				java.util.List<DebugProperty> debugPropeties = lastSnapshot.getDebugProperties();

				if (debugPropeties != null) {
					for (DebugProperty prop : debugPropeties) {
						if (prop.value == null || prop.value.length() == 0) {
							debugProperties.remove(prop.key);
						} else {
							debugProperties.put(prop.key, prop.value);
						}
					}
				}
				for (Map.Entry<String, String> prop : debugProperties.entrySet()) {
					sb.append(prop.getKey()).append(": ").append(prop.getValue()).append('\n');
				}

				getTurnScrollPane().setText(sb.toString());
			} else {
				getTurnScrollPane().setText(null);
			}
		}
	}

	private ConsoleScrollPane getTurnScrollPane() {
		if (propertiesPane == null) {
			propertiesPane = new ConsoleScrollPane();
		}
		return propertiesPane;
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
		reset();
	}

	/**
	 * Is called when the Kill button has been activated
	 */
	private void killButtonActionPerformed() {
		manager.getBattleManager().killRobot(robotIndex);
	}

	/**
	 * Is called when the Paint button has been activated
	 */
	private void paintButtonActionPerformed() {
		manager.getBattleManager().setPaintEnabled(robotIndex, getPaintButton().isSelected());
	}

	/**
	 * Is called when the SG check box has been activated
	 */
	private void sgCheckBoxActionPerformed() {
		manager.getBattleManager().setSGPaintEnabled(robotIndex, getSGCheckBox().isSelected());
	}

	/**
	 * Is called when the Pause/Resume button has been activated
	 */
	private void pauseResumeButtonActionPerformed() {
		manager.getBattleManager().togglePauseResumeBattle();
	}

	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			getPauseButton().setEnabled(true);
			getKillButton().setEnabled(true);
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			lastSnapshot = null;
			paintSnapshot();
			getPauseButton().setEnabled(false);
			getKillButton().setEnabled(false);
		}

		@Override
		public void onBattlePaused(BattlePausedEvent event) {
			getPauseButton().setSelected(true);
		}

		@Override
		public void onBattleResumed(BattleResumedEvent event) {
			getPauseButton().setSelected(false);
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			// TODO: Get rid of this check (bugfix) if possible:
			// Make sanity check as a new battle could have been started since the dialog was initialized,
			// and thus the robot index can be too high compared to the robot's array size causing an
			// ArrayOutOfBoundsException. This is a bugfix
			if (event == null) {
				return;
			}
			final TurnSnapshot turn = event.getTurnSnapshot();

			if (turn == null) {
				return;
			}
			java.util.List<RobotSnapshot> robots = turn.getRobots();

			if (robots == null || robotIndex >= robots.size()) {
				return;
			}

			lastSnapshot = robots.get(robotIndex);
			final String text = lastSnapshot.getOutputStreamSnapshot();

			if (text != null && text.length() > 0) {
				getConsoleScrollPane().append(text);
				getConsoleScrollPane().scrollToBottom();
			}

			paintSnapshot();
		}

	}
}
