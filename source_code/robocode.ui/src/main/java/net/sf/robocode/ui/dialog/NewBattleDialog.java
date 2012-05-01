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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons and tabs
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Changed the F5 key press for refreshing the list of available robots
 *       into 'modifier key' + R to comply with other OSes like e.g. Mac OS
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
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
	private JPanel newBattleDialogContentPane;
	private WizardTabbedPane tabbedPane;
	private NewBattleBattleFieldTab battleFieldTab;

	private BattleProperties battleProperties;
	private boolean isOpeningBattle;

	private NewBattleRulesTab rulesTab;
	private WizardController wizardController;

	private RobotSelectionPanel robotSelectionPanel;

	private final IBattleManager battleManager;
	private final ISettingsManager settingsManager;

	public NewBattleDialog(IWindowManager windowManager, IBattleManager battleManager, ISettingsManager settingsManager) {
		super(windowManager.getRobocodeFrame(), true);
		this.battleManager = battleManager;
		this.settingsManager = settingsManager;
	}

	public void setup(BattleProperties battleProperties, boolean openBattle) {
		this.battleProperties = battleProperties;
		this.isOpeningBattle = openBattle;
		robotSelectionPanel = null;
		initialize();
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
		battleProperties.setSelectedRobots(getRobotSelectionPanel().getSelectedRobotsAsString());
		battleProperties.setBattlefieldWidth(getBattleFieldTab().getBattleFieldWidth());
		battleProperties.setBattlefieldHeight(getBattleFieldTab().getBattleFieldHeight());
		battleProperties.setNumRounds(getRobotSelectionPanel().getNumRounds());
		battleProperties.setGunCoolingRate(getRulesTab().getGunCoolingRate());
		battleProperties.setInactivityTime(getRulesTab().getInactivityTime());	
		if (!getRulesTab().getExtensionPackage().equals("") && !getRulesTab().getExtensionFile().equals("")) {
			battleProperties.setExtensionFilename(getRulesTab().getExtensionFile());
			battleProperties.setExtensionPackage(getRulesTab().getExtensionPackage());	
		}

		// Dispose this dialog before starting the battle due to pause/resume battle state
		dispose();

		// Start new battle after the dialog has been disposed and hence has called resumeBattle()
		battleManager.startNewBattle(battleProperties, false, false);
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
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK),
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
			rulesTab = new net.sf.robocode.ui.dialog.NewBattleRulesTab();
		}
		return rulesTab;
	}

	public List<IRepositoryItem> getSelectedRobots() {
		return getRobotSelectionPanel().getSelectedRobots();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setTitle("New Battle");
		setPreferredSize(new Dimension(850, 650));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(getNewBattleDialogContentPane());
		addCancelByEscapeKey();

		processBattleProperties();		
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
			String selectedRobots = "";

			if (battleProperties != null) {
				selectedRobots = battleProperties.getSelectedRobots();
			}
			robotSelectionPanel = net.sf.robocode.core.Container.createComponent(RobotSelectionPanel.class);
			final boolean ignoreTeamRobots = false; // TODO do we really want to have this !properties.getOptionsTeamShowTeamRobots();

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
		getRulesTab().setGunCoolingRate(battleProperties.getGunCoolingRate());
		getRulesTab().setInactivityTime(battleProperties.getInactivityTime());

		// When opening a battle, we use the 'number of rounds' from the battle properties.
		// When starting a new battle, we use the 'number of rounds' from the settings manager instead.
		int numRounds = isOpeningBattle ? battleProperties.getNumRounds() : settingsManager.getNumberOfRounds();

		getRobotSelectionPanel().setNumRounds(numRounds);
	}

	private class EventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(true);
			}
		}
	}
}
