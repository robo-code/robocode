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
 *     - Added menu items for Robocode API, Robo Wiki, Yahoo Group Robocode,
 *       and Robocode Repository
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Code cleanup
 *     Matthew Reeder
 *     - Added keyboard mnemonics and a few accelerators to all menus and menu
 *       items
 *     Luis Crespo & Flemming N. Larsen
 *     - Added check box menu item for "Show Rankings"
 *******************************************************************************/
package robocode.dialog;


import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import robocode.manager.RobocodeManager;


/**
 * Handles menu display and interaction for Robocode.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 * @author Luis Crespo (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeMenuBar extends JMenuBar {

	// Battle menu
	private JMenu battleMenu;
	private JMenuItem battleNewMenuItem;
	private JMenuItem battleOpenMenuItem;
	private JMenuItem battleSaveMenuItem;
	private JMenuItem battleSaveAsMenuItem;
	private JMenuItem battleExitMenuItem;

	// Robot menu
	private JMenu robotMenu;
	private JMenuItem robotEditorMenuItem;
	private JMenuItem robotImportMenuItem;
	private JMenuItem robotPackagerMenuItem;

	// Team menu
	private JMenu teamMenu;
	private JMenuItem teamCreateTeamMenuItem;

	// Options menu
	private JMenu optionsMenu;
	private JMenuItem optionsPreferencesMenuItem;
	private JMenuItem optionsFitWindowMenuItem;
	private JCheckBoxMenuItem optionsShowRankingCheckBoxMenuItem;

	// Help Menu
	private JMenu helpMenu;
	private JMenuItem helpOnlineHelpMenuItem;
	private JMenuItem helpCheckForNewVersionMenuItem;
	private JMenuItem helpVersionsTxtMenuItem;
	private JMenuItem helpRobocodeApiMenuItem;
	private JMenuItem helpFaqMenuItem;
	private JMenuItem helpAboutMenuItem;
	private JMenuItem helpRobocodeMenuItem;
	private JMenuItem helpRoboWikiMenuItem;
	private JMenuItem helpYahooGroupRobocodeMenuItem;
	private JMenuItem helpRobocodeRepositoryMenuItem;
	private RobocodeFrame robocodeFrame;
	private RobocodeManager manager;

	private class EventHandler implements ActionListener, MenuListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			RobocodeMenuBar mb = RobocodeMenuBar.this;

			// Battle menu
			if (source == mb.getBattleNewMenuItem()) {
				battleNewActionPerformed();
			} else if (source == mb.getBattleOpenMenuItem()) {
				battleOpenActionPerformed();
			} else if (source == mb.getBattleSaveMenuItem()) {
				battleSaveActionPerformed();
			} else if (source == mb.getBattleSaveAsMenuItem()) {
				battleSaveAsActionPerformed();
			} else if (source == mb.getBattleExitMenuItem()) {
				battleExitActionPerformed();

				// Robot Editor menu
			} else if (source == mb.getRobotEditorMenuItem()) {
				robotEditorActionPerformed();
			} else if (source == mb.getRobotImportMenuItem()) {
				robotImportActionPerformed();
			} else if (source == mb.getRobotPackagerMenuItem()) {
				robotPackagerActionPerformed();

				// Team / Create Team menu
			} else if (source == mb.getTeamCreateTeamMenuItem()) {
				teamCreateTeamActionPerformed();

				// Options / Preferences menu
			} else if (source == mb.getOptionsPreferencesMenuItem()) {
				optionsPreferencesActionPerformed();
			} else if (source == mb.getOptionsFitWindowMenuItem()) {
				optionsFitWindowActionPerformed();
			} else if (source == mb.getOptionsShowRankingCheckBoxMenuItem()) {
				optionsShowRankingActionPerformed();

				// Help menu
			} else if (source == mb.getHelpOnlineHelpMenuItem()) {
				helpOnlineHelpActionPerformed();
			} else if (source == mb.getHelpRobocodeApiMenuItem()) {
				helpRobocodeApiActionPerformed();
			} else if (source == mb.getHelpFaqMenuItem()) {
				helpFaqActionPerformed();
			} else if (source == mb.getHelpRobocodeMenuItem()) {
				helpRobocondeMenuItemActionPerformed();
			} else if (source == mb.getHelpRoboWikiMenuItem()) {
				helpRoboWikiMenuItemActionPerformed();
			} else if (source == mb.getHelpYahooGroupRobocodeMenuItem()) {
				helpYahooGroupRobocodeActionPerformed();
			} else if (source == mb.getHelpRobocodeRepositoryMenuItem()) {
				helpRobocodeRepositoryActionPerformed();
			} else if (source == mb.getHelpCheckForNewVersionMenuItem()) {
				helpCheckForNewVersionActionPerformed();
			} else if (source == mb.getHelpVersionsTxtMenuItem()) {
				helpVersionsTxtActionPerformed();
			} else if (source == mb.getHelpAboutMenuItem()) {
				helpAboutActionPerformed();
			}
		}

		public void menuDeselected(MenuEvent e) {
			manager.getBattleManager().resumeBattle();
		}

		public void menuSelected(MenuEvent e) {
			manager.getBattleManager().pauseBattle();
		}

		public void menuCanceled(MenuEvent e) {}
	}

	public RobocodeMenuBar.EventHandler eventHandler = new EventHandler();

	/**
	 * RoboCodeMenu constructor
	 */
	public RobocodeMenuBar(RobocodeManager manager, RobocodeFrame robocodeFrame) {
		super();
		this.manager = manager;
		this.robocodeFrame = robocodeFrame;
		add(getBattleMenu());
		add(getRobotMenu());
		add(getOptionsMenu());
		add(getHelpMenu());
	}

	private void battleExitActionPerformed() {
		java.awt.AWTEvent evt = new WindowEvent(robocodeFrame, WindowEvent.WINDOW_CLOSING);

		robocodeFrame.dispatchEvent(evt);
	}

	/**
	 * Handle battleNew menu item action
	 */
	private void battleNewActionPerformed() {
		manager.getWindowManager().showNewBattleDialog(manager.getBattleManager().getBattleProperties());
	}

	private void battleOpenActionPerformed() {
		manager.getWindowManager().showBattleOpenDialog();
	}

	private void battleSaveActionPerformed() {
		manager.getBattleManager().saveBattle();
	}

	private void battleSaveAsActionPerformed() {
		manager.getBattleManager().saveBattleAs();
	}

	/**
	 * Return the battleExitMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getBattleExitMenuItem() {
		if (battleExitMenuItem == null) {
			battleExitMenuItem = new JMenuItem();
			battleExitMenuItem.setText("Exit");
			battleExitMenuItem.setMnemonic('x');
			battleExitMenuItem.setDisplayedMnemonicIndex(1);
			battleExitMenuItem.addActionListener(eventHandler);
		}
		return battleExitMenuItem;
	}

	/**
	 * Return the Battle Menu
	 *
	 * @return JMenu
	 */
	public JMenu getBattleMenu() {
		if (battleMenu == null) {
			battleMenu = new JMenu();
			battleMenu.setText("Battle");
			battleMenu.setMnemonic('B');
			battleMenu.setDisplayedMnemonicIndex(0);
			battleMenu.add(getBattleNewMenuItem());
			battleMenu.add(getBattleOpenMenuItem());
			battleMenu.add(new JSeparator());
			battleMenu.add(getBattleSaveMenuItem());
			battleMenu.add(getBattleSaveAsMenuItem());
			battleMenu.add(getBattleExitMenuItem());
			battleMenu.addMenuListener(eventHandler);
		}
		return battleMenu;
	}

	/**
	 * Return the battleNewMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getBattleNewMenuItem() {
		if (battleNewMenuItem == null) {
			battleNewMenuItem = new JMenuItem();
			battleNewMenuItem.setText("New");
			battleNewMenuItem.setMnemonic('N');
			battleNewMenuItem.setDisplayedMnemonicIndex(0);
			battleNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
			battleNewMenuItem.addActionListener(eventHandler);
		}
		return battleNewMenuItem;
	}

	/**
	 * Return the battleOpenMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getBattleOpenMenuItem() {
		if (battleOpenMenuItem == null) {
			battleOpenMenuItem = new JMenuItem();
			battleOpenMenuItem.setText("Open");
			battleOpenMenuItem.setMnemonic('O');
			battleOpenMenuItem.setDisplayedMnemonicIndex(0);
			battleOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
			battleOpenMenuItem.addActionListener(eventHandler);
		}
		return battleOpenMenuItem;
	}

	/**
	 * Return the battleSaveAsMenuItem.
	 *
	 * @return JMenuItem
	 */
	public JMenuItem getBattleSaveAsMenuItem() {
		if (battleSaveAsMenuItem == null) {
			battleSaveAsMenuItem = new JMenuItem();
			battleSaveAsMenuItem.setText("Save As");
			battleSaveAsMenuItem.setMnemonic('A');
			battleSaveAsMenuItem.setDisplayedMnemonicIndex(5);
			battleSaveAsMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false));
			battleSaveAsMenuItem.setEnabled(false);
			battleSaveAsMenuItem.addActionListener(eventHandler);
		}
		return battleSaveAsMenuItem;
	}

	/**
	 * Return the battleSaveMenuItem.
	 *
	 * @return JMenuItem
	 */
	public JMenuItem getBattleSaveMenuItem() {
		if (battleSaveMenuItem == null) {
			battleSaveMenuItem = new JMenuItem();
			battleSaveMenuItem.setText("Save");
			battleSaveMenuItem.setMnemonic('S');
			battleSaveMenuItem.setDisplayedMnemonicIndex(0);
			battleSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK, false));
			battleSaveMenuItem.setEnabled(false);
			battleSaveMenuItem.addActionListener(eventHandler);
		}
		return battleSaveMenuItem;
	}

	/**
	 * Return the helpAboutMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpAboutMenuItem() {
		if (helpAboutMenuItem == null) {
			helpAboutMenuItem = new JMenuItem();
			helpAboutMenuItem.setText("About");
			helpAboutMenuItem.setMnemonic('A');
			helpAboutMenuItem.setDisplayedMnemonicIndex(0);
			helpAboutMenuItem.addActionListener(eventHandler);
		}
		return helpAboutMenuItem;
	}

	/**
	 * Return the helpCheckForNewVersion menu item.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpCheckForNewVersionMenuItem() {
		if (helpCheckForNewVersionMenuItem == null) {
			helpCheckForNewVersionMenuItem = new JMenuItem();
			helpCheckForNewVersionMenuItem.setText("Check for new version");
			helpCheckForNewVersionMenuItem.setMnemonic('C');
			helpCheckForNewVersionMenuItem.setDisplayedMnemonicIndex(0);
			helpCheckForNewVersionMenuItem.addActionListener(eventHandler);
		}
		return helpCheckForNewVersionMenuItem;
	}

	/**
	 * Return the Help Menu.
	 *
	 * @return JMenu
	 */
	@Override
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic('H');
			helpMenu.setDisplayedMnemonicIndex(0);
			helpMenu.add(getHelpOnlineHelpMenuItem());
			helpMenu.add(getHelpRobocodeApiMenuItem());
			helpMenu.add(getHelpFaqMenuItem());
			helpMenu.add(new JSeparator());
			helpMenu.add(getHelpRobocodeMenuItem());
			helpMenu.add(getHelpRoboWikiMenuItem());
			helpMenu.add(getHelpYahooGroupRobocodeMenuItem());
			helpMenu.add(getHelpRobocodeRepositoryMenuItem());
			helpMenu.add(new JSeparator());
			helpMenu.add(getHelpCheckForNewVersionMenuItem());
			helpMenu.add(getHelpVersionsTxtMenuItem());
			helpMenu.add(new JSeparator());
			helpMenu.add(getHelpAboutMenuItem());
			helpMenu.addMenuListener(eventHandler);
		}
		return helpMenu;
	}

	/**
	 * Return the helpOnlineHelpMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpFaqMenuItem() {
		if (helpFaqMenuItem == null) {
			helpFaqMenuItem = new JMenuItem();
			helpFaqMenuItem.setText("Robocode FAQ");
			helpFaqMenuItem.setMnemonic('F');
			helpFaqMenuItem.setDisplayedMnemonicIndex(9);
			helpFaqMenuItem.addActionListener(eventHandler);
		}
		return helpFaqMenuItem;
	}

	/**
	 * Return the helpOnlineHelpMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpOnlineHelpMenuItem() {
		if (helpOnlineHelpMenuItem == null) {
			helpOnlineHelpMenuItem = new JMenuItem();
			helpOnlineHelpMenuItem.setText("Online Help");
			helpOnlineHelpMenuItem.setMnemonic('O');
			helpOnlineHelpMenuItem.setDisplayedMnemonicIndex(0);
			helpOnlineHelpMenuItem.addActionListener(eventHandler);
		}
		return helpOnlineHelpMenuItem;
	}

	/**
	 * Return the helpVersionsTxtMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpVersionsTxtMenuItem() {
		if (helpVersionsTxtMenuItem == null) {
			helpVersionsTxtMenuItem = new JMenuItem();
			helpVersionsTxtMenuItem.setText("Version Info");
			helpVersionsTxtMenuItem.setMnemonic('V');
			helpVersionsTxtMenuItem.setDisplayedMnemonicIndex(0);
			helpVersionsTxtMenuItem.addActionListener(eventHandler);
		}
		return helpVersionsTxtMenuItem;
	}

	/**
	 * Return the helpRobocodeApiMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpRobocodeApiMenuItem() {
		if (helpRobocodeApiMenuItem == null) {
			helpRobocodeApiMenuItem = new JMenuItem();
			helpRobocodeApiMenuItem.setText("Robocode API");
			helpRobocodeApiMenuItem.setMnemonic('A');
			helpRobocodeApiMenuItem.setDisplayedMnemonicIndex(9);
			helpRobocodeApiMenuItem.addActionListener(eventHandler);
		}
		return helpRobocodeApiMenuItem;
	}

	/**
	 * Return the helpRoboWikiMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpRobocodeMenuItem() {
		if (helpRobocodeMenuItem == null) {
			helpRobocodeMenuItem = new JMenuItem();
			helpRobocodeMenuItem.setText("Robocode Home");
			helpRobocodeMenuItem.setMnemonic('H');
			helpRobocodeMenuItem.setDisplayedMnemonicIndex(11);
			helpRobocodeMenuItem.addActionListener(eventHandler);
		}
		return helpRobocodeMenuItem;
	}

	/**
	 * Return the helpRoboWikiMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpRoboWikiMenuItem() {
		if (helpRoboWikiMenuItem == null) {
			helpRoboWikiMenuItem = new JMenuItem();
			helpRoboWikiMenuItem.setText("RoboWiki");
			helpRoboWikiMenuItem.setMnemonic('W');
			helpRoboWikiMenuItem.setDisplayedMnemonicIndex(4);
			helpRoboWikiMenuItem.addActionListener(eventHandler);
		}
		return helpRoboWikiMenuItem;
	}

	/**
	 * Return the helpYahooGroupRobocodeMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpYahooGroupRobocodeMenuItem() {
		if (helpYahooGroupRobocodeMenuItem == null) {
			helpYahooGroupRobocodeMenuItem = new JMenuItem();
			helpYahooGroupRobocodeMenuItem.setText("Yahoo Group: Robocode");
			helpYahooGroupRobocodeMenuItem.setMnemonic('Y');
			helpYahooGroupRobocodeMenuItem.setDisplayedMnemonicIndex(0);
			helpYahooGroupRobocodeMenuItem.addActionListener(eventHandler);
		}
		return helpYahooGroupRobocodeMenuItem;
	}

	/**
	 * Return the helpRobocodeRepositoryMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpRobocodeRepositoryMenuItem() {
		if (helpRobocodeRepositoryMenuItem == null) {
			helpRobocodeRepositoryMenuItem = new JMenuItem();
			helpRobocodeRepositoryMenuItem.setText("Robocode Repository");
			helpRobocodeRepositoryMenuItem.setMnemonic('p');
			helpRobocodeRepositoryMenuItem.setDisplayedMnemonicIndex(11);
			helpRobocodeRepositoryMenuItem.addActionListener(eventHandler);
		}
		return helpRobocodeRepositoryMenuItem;
	}

	/**
	 * Return the optionsPreferencesMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getOptionsFitWindowMenuItem() {
		if (optionsFitWindowMenuItem == null) {
			optionsFitWindowMenuItem = new JMenuItem();
			optionsFitWindowMenuItem.setText("Default Window Size");
			optionsFitWindowMenuItem.setMnemonic('D');
			optionsFitWindowMenuItem.setDisplayedMnemonicIndex(0);
			optionsFitWindowMenuItem.addActionListener(eventHandler);
		}
		return optionsFitWindowMenuItem;
	}

	/**
	 * Return the optionsShowRankingCheckBoxMenuItem.
	 *
	 * @return JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem getOptionsShowRankingCheckBoxMenuItem() {
		if (optionsShowRankingCheckBoxMenuItem == null) {
			optionsShowRankingCheckBoxMenuItem = new JCheckBoxMenuItem();
			optionsShowRankingCheckBoxMenuItem.setText("Ranking Panel");
			optionsShowRankingCheckBoxMenuItem.setMnemonic('R');
			optionsShowRankingCheckBoxMenuItem.setDisplayedMnemonicIndex(0);
			optionsShowRankingCheckBoxMenuItem.addActionListener(eventHandler);
		}
		return optionsShowRankingCheckBoxMenuItem;
	}

	/**
	 * Return the Options Menu.
	 *
	 * @return JMenu
	 */
	private JMenu getOptionsMenu() {
		if (optionsMenu == null) {
			optionsMenu = new JMenu();
			optionsMenu.setText("Options");
			optionsMenu.setMnemonic('O');
			optionsMenu.setDisplayedMnemonicIndex(0);
			optionsMenu.add(getOptionsPreferencesMenuItem());
			optionsMenu.add(getOptionsFitWindowMenuItem());
			optionsMenu.add(getOptionsShowRankingCheckBoxMenuItem());
			optionsMenu.addMenuListener(eventHandler);
		}
		return optionsMenu;
	}

	/**
	 * Return the optionsPreferencesMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getOptionsPreferencesMenuItem() {
		if (optionsPreferencesMenuItem == null) {
			optionsPreferencesMenuItem = new JMenuItem();
			optionsPreferencesMenuItem.setText("Preferences");
			optionsPreferencesMenuItem.setMnemonic('P');
			optionsPreferencesMenuItem.setDisplayedMnemonicIndex(0);
			optionsPreferencesMenuItem.addActionListener(eventHandler);
		}
		return optionsPreferencesMenuItem;
	}

	/**
	 * Return the robotEditorMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getRobotEditorMenuItem() {
		if (robotEditorMenuItem == null) {
			robotEditorMenuItem = new JMenuItem();
			robotEditorMenuItem.setText("Editor");
			robotEditorMenuItem.setMnemonic('E');
			robotEditorMenuItem.setDisplayedMnemonicIndex(0);
			robotEditorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK, false));
			robotEditorMenuItem.addActionListener(eventHandler);
		}
		return robotEditorMenuItem;
	}

	/**
	 * Return the robotImportMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getRobotImportMenuItem() {
		if (robotImportMenuItem == null) {
			robotImportMenuItem = new JMenuItem();
			robotImportMenuItem.setText("Import downloaded robot");
			robotImportMenuItem.setMnemonic('I');
			robotImportMenuItem.setDisplayedMnemonicIndex(0);
			robotImportMenuItem.addActionListener(eventHandler);
		}
		return robotImportMenuItem;
	}

	/**
	 * Return the Robot Menu.
	 *
	 * @return JMenu
	 */
	public JMenu getRobotMenu() {
		if (robotMenu == null) {
			robotMenu = new JMenu();
			robotMenu.setText("Robot");
			robotMenu.setMnemonic('R');
			robotMenu.setDisplayedMnemonicIndex(0);
			robotMenu.add(getRobotEditorMenuItem());
			robotMenu.add(getRobotImportMenuItem());
			robotMenu.add(getRobotPackagerMenuItem());
			robotMenu.add(getTeamMenu());
			robotMenu.addMenuListener(eventHandler);
		}
		return robotMenu;
	}

	/**
	 * Return the robotPackagerMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getRobotPackagerMenuItem() {
		if (robotPackagerMenuItem == null) {
			robotPackagerMenuItem = new JMenuItem();
			robotPackagerMenuItem.setText("Package robot for upload");
			robotPackagerMenuItem.setMnemonic('P');
			robotPackagerMenuItem.setDisplayedMnemonicIndex(0);
			robotPackagerMenuItem.addActionListener(eventHandler);
		}
		return robotPackagerMenuItem;
	}

	/**
	 * Return the Team Menu.
	 *
	 * @return JMenu
	 */
	private JMenu getTeamMenu() {
		if (teamMenu == null) {
			teamMenu = new JMenu();
			teamMenu.setText("Team");
			teamMenu.setMnemonic('T');
			teamMenu.setDisplayedMnemonicIndex(0);
			teamMenu.add(getTeamCreateTeamMenuItem());
			teamMenu.addMenuListener(eventHandler);
		}
		return teamMenu;
	}

	/**
	 * Return the teamCreateTeamMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getTeamCreateTeamMenuItem() {
		if (teamCreateTeamMenuItem == null) {
			teamCreateTeamMenuItem = new JMenuItem();
			teamCreateTeamMenuItem.setText("Create Team");
			teamCreateTeamMenuItem.setMnemonic('C');
			teamCreateTeamMenuItem.setDisplayedMnemonicIndex(0);
			teamCreateTeamMenuItem.addActionListener(eventHandler);
		}
		return teamCreateTeamMenuItem;
	}

	private void teamCreateTeamActionPerformed() {
		manager.getWindowManager().showCreateTeamDialog();
	}

	private void helpAboutActionPerformed() {
		manager.getWindowManager().showAboutBox();
	}

	private void helpCheckForNewVersionActionPerformed() {
		manager.getVersionManager().checkForNewVersion(true);
	}

	private void helpFaqActionPerformed() {
		manager.getWindowManager().showFaq();
	}

	private void helpOnlineHelpActionPerformed() {
		manager.getWindowManager().showOnlineHelp();
	}

	private void helpVersionsTxtActionPerformed() {
		manager.getWindowManager().showVersionsTxt();
	}

	private void helpRobocodeApiActionPerformed() {
		manager.getWindowManager().showHelpApi();
	}

	private void helpRobocondeMenuItemActionPerformed() {
		manager.getWindowManager().showRobocodeHome();
	}

	private void helpRoboWikiMenuItemActionPerformed() {
		manager.getWindowManager().showRoboWiki();
	}

	private void helpYahooGroupRobocodeActionPerformed() {
		manager.getWindowManager().showYahooGroupRobocode();
	}

	private void helpRobocodeRepositoryActionPerformed() {
		manager.getWindowManager().showRobocodeRepository();
	}

	private void optionsFitWindowActionPerformed() {
		WindowUtil.fitWindow(manager.getWindowManager().getRobocodeFrame());
	}

	private void optionsShowRankingActionPerformed() {
		manager.getWindowManager().showRankingDialog(getOptionsShowRankingCheckBoxMenuItem().getState());
	}

	private void optionsPreferencesActionPerformed() {
		manager.getWindowManager().showOptionsPreferences();
	}

	private void robotEditorActionPerformed() {
		manager.getWindowManager().showRobocodeEditor();
	}

	private void robotImportActionPerformed() {
		manager.getWindowManager().showImportRobotDialog();
	}

	private void robotPackagerActionPerformed() {
		manager.getWindowManager().showRobotPackager();
	}
}
