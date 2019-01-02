/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.ui.IWindowManager;
import robocode.control.events.*;
import robocode.control.snapshot.IDebugProperty;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotDialog extends JFrame {
	private final Color grayGreen = new Color(0x0080C080);
	private RobotButton robotButton;
	private JTabbedPane tabbedPane;
	private ConsoleScrollPane consoleScrollPane;
	private ConsoleScrollPane propertiesScrollPane;
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
	private IRobotSnapshot lastSnapshot;
	private boolean printSnapshot;
	private boolean grayGreenButton;
	private final Map<String, String> debugProperties = new HashMap<String, String>();

	private final BattleObserver battleObserver = new BattleObserver();
	private final IWindowManager windowManager;
	private final IBattleManager battleManager;

	public RobotDialog(IWindowManager windowManager, IBattleManager battleManager) {
		super();
		this.battleManager = battleManager;
		this.windowManager = windowManager;
	}

	public void setup(RobotButton robotButton) {
		this.robotButton = robotButton;
		initialize();
	}

	private void initialize() {
		robotIndex = robotButton.getRobotIndex();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getRobotDialogContentPane());
		if (windowManager.isSlave()) {
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
			windowManager.removeBattleListener(battleObserver);
			isListening = false;
		}
		robotButton.detach();
		
		getPauseButton().setEnabled(false);
		getKillButton().setEnabled(false);

		lastSnapshot = null;
		printSnapshot();
	}

	public void attach(RobotButton robotButton) {
		this.robotButton = robotButton;
		robotIndex = this.robotButton.getRobotIndex();
		if (!isListening) {
			isListening = true;
			windowManager.addBattleListener(battleObserver);
		}
		getPauseButton().setEnabled(true);
		if (!windowManager.isSlave()) {
			getKillButton().setEnabled(true);
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
		getTabbedPane().setPreferredSize(null);
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

	private final transient ActionListener eventHandler = new ActionListener() {
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
			tabbedPane.addTab("Properties", getPropertiesScrollPane());
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					printSnapshot = (tabbedPane.getSelectedIndex() == 1);
					printSnapshot();
				}
			});
		}
		return tabbedPane;
	}

	private void printSnapshot() {
		if (printSnapshot) {
			String text = null;

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
				IDebugProperty[] debugPropeties = lastSnapshot.getDebugProperties();

				if (debugPropeties != null) {
					for (IDebugProperty prop : debugPropeties) {
						if (prop.getValue() == null || prop.getValue().length() == 0) {
							debugProperties.remove(prop.getKey());
						} else {
							debugProperties.put(prop.getKey(), prop.getValue());
						}
					}
				}
				for (Map.Entry<String, String> prop : debugProperties.entrySet()) {
					sb.append(prop.getKey()).append(": ").append(prop.getValue()).append('\n');
				}

				text = sb.toString();
			}
			getPropertiesScrollPane().setText(text);
		}
	}

	private ConsoleScrollPane getPropertiesScrollPane() {
		if (propertiesScrollPane == null) {
			propertiesScrollPane = new ConsoleScrollPane();
		}
		return propertiesScrollPane;
	}

	/**
	 * Returns the console scroll pane
	 *
	 * @return the console scroll pane
	 */
	private ConsoleScrollPane getConsoleScrollPane() {
		if (consoleScrollPane == null) {
			consoleScrollPane = new ConsoleScrollPane();
		}
		return consoleScrollPane;
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
		battleManager.killRobot(robotIndex);
	}

	/**
	 * Is called when the Paint button has been activated
	 */
	private void paintButtonActionPerformed() {
		battleManager.setPaintEnabled(robotIndex, getPaintButton().isSelected());
	}

	/**
	 * Is called when the SG check box has been activated
	 */
	private void sgCheckBoxActionPerformed() {
		battleManager.setSGPaintEnabled(robotIndex, getSGCheckBox().isSelected());
	}

	/**
	 * Is called when the Pause/Resume button has been activated
	 */
	private void pauseResumeButtonActionPerformed() {
		battleManager.togglePauseResumeBattle();
	}

	private class BattleObserver extends BattleAdaptor {

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
			final ITurnSnapshot turn = event.getTurnSnapshot();

			if (turn == null) {
				return;
			}

			lastSnapshot = turn.getRobots()[robotIndex];
			final String text = lastSnapshot.getOutputStreamSnapshot();

			if (text != null && text.length() > 0) {
				getConsoleScrollPane().append(text);
				getConsoleScrollPane().scrollToBottom();
			}

			if (lastSnapshot.isPaintRobot() && !grayGreenButton) {
				grayGreenButton = true;
				getPaintButton().setBackground(grayGreen);
			}

			printSnapshot();
		}
	}
}
