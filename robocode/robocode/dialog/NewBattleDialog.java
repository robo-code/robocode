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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons and tabs
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.dialog;


import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import robocode.battle.BattleProperties;
import robocode.manager.RobocodeManager;


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

	private EventHandler eventHandler = new EventHandler();
	private JPanel newBattleDialogContentPane;
	private WizardTabbedPane tabbedPane;
	private NewBattleBattleFieldTab battleFieldTab;

	private BattleProperties battleProperties;

	private NewBattleRulesTab rulesTab;
	private WizardController wizardController;

	private RobotSelectionPanel robotSelectionPanel;

	private RobocodeManager manager;

	class EventHandler extends WindowAdapter implements ActionListener {
		@Override
		public void windowClosing(WindowEvent e) {
			if (e.getSource() == NewBattleDialog.this) {
				manager.getBattleManager().resumeBattle();
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList();
			}
		}
	}

	/**
	 * Comment
	 */
	public void cancelButtonActionPerformed() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Comment
	 */
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
		String selectedRobotsProperty = robotSelectionPanel.getSelectedRobotsAsString();

		battleProperties.setSelectedRobots(selectedRobotsProperty);
		battleProperties.setBattlefieldWidth(getBattleFieldTab().getBattleFieldWidth());
		battleProperties.setBattlefieldHeight(getBattleFieldTab().getBattleFieldHeight());
		battleProperties.setNumRounds(getRobotSelectionPanel().getNumRounds());
		battleProperties.setGunCoolingRate(getRulesTab().getGunCoolingRate());
		battleProperties.setInactivityTime(getRulesTab().getInactivityTime());
		new Thread(new Runnable() {
			public void run() {
				manager.getBattleManager().startNewBattle(battleProperties, false, false);
			}
		}).start();
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Return the battleFieldTab
	 *
	 * @return JPanel
	 */
	private NewBattleBattleFieldTab getBattleFieldTab() {
		if (battleFieldTab == null) {
			battleFieldTab = new NewBattleBattleFieldTab();
		}
		return battleFieldTab;
	}

	/**
	 * Return the newBattleDialogContentPane
	 *
	 * @return JPanel
	 */
	private JPanel getNewBattleDialogContentPane() {
		if (newBattleDialogContentPane == null) {
			newBattleDialogContentPane = new JPanel();
			newBattleDialogContentPane.setLayout(new BorderLayout());
			newBattleDialogContentPane.add(getWizardController(), BorderLayout.SOUTH);
			newBattleDialogContentPane.add(getTabbedPane(), BorderLayout.CENTER);
			newBattleDialogContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		}
		return newBattleDialogContentPane;
	}

	/**
	 * Return the rulesTab property value.
	 *
	 * @return robocode.dialog.NewBattleRulesTab
	 */
	private NewBattleRulesTab getRulesTab() {
		if (rulesTab == null) {
			rulesTab = new robocode.dialog.NewBattleRulesTab();
		}
		return rulesTab;
	}

	public List<robocode.repository.FileSpecification> getSelectedRobots() {
		return getRobotSelectionPanel().getSelectedRobots();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("New Battle");
		setContentPane(getNewBattleDialogContentPane());
		addWindowListener(eventHandler);
	}

	/**
	 * NewBattleDialog constructor comment.
	 *
	 * @param owner Frame
	 */
	public NewBattleDialog(RobocodeManager manager, BattleProperties battleProperties) {
		super(manager.getWindowManager().getRobocodeFrame());
		this.manager = manager;
		this.battleProperties = battleProperties;
		initialize();
		processBattleProperties();
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
			String selectedRobots = "";

			if (battleProperties != null) {
				selectedRobots = battleProperties.getSelectedRobots();
			}
			robotSelectionPanel = new RobotSelectionPanel(manager.getRobotRepositoryManager(), MIN_ROBOTS, MAX_ROBOTS,
					true, "Select robots for the battle", false, false, false, false, false,
					!manager.getProperties().getOptionsTeamShowTeamRobots(), selectedRobots);
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
			tabbedPane.insertTab("BattleField", null, getBattleFieldTab(), null, 1);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_F);
			tabbedPane.setDisplayedMnemonicIndexAt(1, 6);
			tabbedPane.insertTab("Rules", null, getRulesTab(), null, 2);
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_U);
			tabbedPane.setDisplayedMnemonicIndexAt(2, 1);
		}
		return tabbedPane;
	}

	private void processBattleProperties() {
		if (battleProperties == null) {
			return;
		}
		getBattleFieldTab().setBattleFieldWidth(battleProperties.getBattlefieldWidth());
		getBattleFieldTab().setBattleFieldHeight(battleProperties.getBattlefieldHeight());
		getRobotSelectionPanel().setNumRounds(battleProperties.getNumRounds());

		getRulesTab().setGunCoolingRate(battleProperties.getGunCoolingRate());
		getRulesTab().setInactivityTime(battleProperties.getInactivityTime());
	}
}
