/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class NewBattleDialog extends JDialog implements WizardListener {

	private final static int MAX_ROBOTS = 256; // 64;
	private final static int MIN_ROBOTS = 1;

	private final EventHandler eventHandler = new EventHandler();

	private ISettingsManager settingsManager;
	private BattleProperties battleProperties;

	private WizardTabbedPane tabbedPane;
	private NewBattleRulesTab battleFieldTab;

	private WizardController wizardController;

	private RobotSelectionPanel robotSelectionPanel;

	private final IBattleManager battleManager;

	public NewBattleDialog(IWindowManager windowManager, IBattleManager battleManager) {
		super(windowManager.getRobocodeFrame(), true);
		this.battleManager = battleManager;
	}

	public void setup(ISettingsManager settingsManager, BattleProperties battleProperties) { // XXX
		this.settingsManager = settingsManager;
		this.battleProperties = battleProperties;

		robotSelectionPanel = null;

		setTitle("New Battle");
		setPreferredSize(new Dimension(850, 650));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addCancelByEscapeKey();

		add(createNewBattleDialogPanel());
	}

	public void cancelButtonActionPerformed() {
		dispose();
	}

	public void finishButtonActionPerformed() {
		if (robotSelectionPanel.getSelectedRobotsCount() > 24) {
			if (JOptionPane.showConfirmDialog(this,
					"Warning:  The battle you are about to start (" + robotSelectionPanel.getSelectedRobotsCount()
					+ " robots) " + " is very large and will consume a lot of CPU and memory.  Do you wish to proceed?",
					"Large Battle Warning",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE)
					== JOptionPane.NO_OPTION) {
				return;
			}
		}
		if (robotSelectionPanel.getSelectedRobotsCount() == 1) {
			if (JOptionPane.showConfirmDialog(this,
					"You have only selected one robot.  For normal battles you should select at least 2.\nDo you wish to proceed anyway?",
					"Just one robot?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
					== JOptionPane.NO_OPTION) {
				return;
			}
		}
		// Dispose this dialog before starting the battle due to pause/resume battle state
		dispose();

		// Start new battle after the dialog has been disposed and hence has called resumeBattle()
		battleManager.startNewBattle(battleProperties, false, false);
	}

	private NewBattleRulesTab getBattleFieldTab() {
		if (battleFieldTab == null) {
			battleFieldTab = new NewBattleRulesTab();
			battleFieldTab.setup(settingsManager, battleProperties);
		}
		return battleFieldTab;
	}

	private JPanel createNewBattleDialogPanel() {
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.add(getWizardController(), BorderLayout.SOUTH);
		panel.add(getTabbedPane(), BorderLayout.CENTER);
		panel.registerKeyboardAction(eventHandler, "Refresh",
				KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		return panel;
	}

	public List<IRobotSpecItem> getSelectedRobots() {
		return getRobotSelectionPanel().getSelectedRobots();
	}

	private void addCancelByEscapeKey() {
		String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
		int noModifiers = 0;
		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, noModifiers, false);
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		inputMap.put(escapeKey, CANCEL_ACTION_KEY);
		AbstractAction cancelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed();
			}
		};

		getRootPane().getActionMap().put(CANCEL_ACTION_KEY, cancelAction);
	}

	/**
	 * Return the wizardController
	 *
	 * @return JButton
	 */
	private WizardController getWizardController() {
		if (wizardController == null) {
			wizardController = getTabbedPane().getWizardController();
			wizardController.setFinishButtonTextAndMnemonic("Start Battle", 'S', 0);
			wizardController.setFocusOnEnabled(true);
		}
		return wizardController;
	}

	/**
	 * Return the Page property value.
	 *
	 * @return JPanel
	 */
	private RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = Container.createComponent(RobotSelectionPanel.class);
			robotSelectionPanel.addAncestorListener(eventHandler);

			final boolean ignoreTeamRobots = false; // TODO do we really want to have this !properties.getOptionsTeamShowTeamRobots();
			String selectedRobots = (battleProperties == null) ? "" : battleProperties.getSelectedRobots();

			robotSelectionPanel.setup(MIN_ROBOTS, MAX_ROBOTS, true, "Select robots for the battle", false, false, false,
					false, false, ignoreTeamRobots, selectedRobots);
		}
		return robotSelectionPanel;
	}

	/**
	 * Return the tabbedPane.
	 *
	 * @return JTabbedPane
	 */
	private WizardTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new WizardTabbedPane(this);
			tabbedPane.insertTab("Robots", null, getRobotSelectionPanel(), null, 0);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);
			tabbedPane.setDisplayedMnemonicIndexAt(0, 0);
			tabbedPane.insertTab("Rules", null, getBattleFieldTab(), null, 1);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_U);
			tabbedPane.setDisplayedMnemonicIndexAt(1, 1);
		}
		return tabbedPane;
	}

	private class EventHandler implements AncestorListener, ActionListener {
		@Override
		public void ancestorAdded(AncestorEvent event) {}

		@Override
		public void ancestorRemoved(AncestorEvent event) {
			battleProperties.setSelectedRobots(getRobotSelectionPanel().getSelectedRobotsAsString());
		}

		@Override
		public void ancestorMoved(AncestorEvent event) {}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(true);
			}
		}
	}
}
