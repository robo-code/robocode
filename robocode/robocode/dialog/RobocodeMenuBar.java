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

import javax.swing.*;
import robocode.manager.*;
import robocode.util.*;
/**
 * Handles menu display and interaction for Robocode.
 * Creation date: (8/22/2001 1:15:52 PM)
 * @author: Mathew Nelson
 */
public class RobocodeMenuBar extends javax.swing.JMenuBar {

	// Battle menu
	private JMenu battleMenu = null;
	private JMenuItem battleNewMenuItem = null;
	private JMenuItem battleOpenMenuItem = null;
	private JSeparator battleMenuSeparator1 = null;
	private JMenuItem battleSaveMenuItem = null;
	private JMenuItem battleSaveAsMenuItem = null;
	private JMenuItem battleExitMenuItem = null;

	// Robot menu
	private JMenu robotMenu = null;
	private JMenuItem robotEditorMenuItem = null;
	private JMenuItem robotImportMenuItem = null;
	private JMenuItem robotPackagerMenuItem = null;

	// Team menu
	private JMenu teamMenu = null;
	private JMenuItem teamCreateTeamMenuItem = null;
	
	// Options menu
	private JMenu optionsMenu = null;
	private JMenuItem optionsPreferencesMenuItem = null;
	private JMenuItem optionsFitWindowMenuItem = null;

	// Help Menu
	private JMenu helpMenu = null;
	private JMenuItem helpOnlineHelpMenuItem = null;
	private JMenuItem helpCheckForNewVersionMenuItem = null;
	private JMenuItem helpVersionsTxtMenuItem = null;
	private JMenuItem helpRobocodeApiMenuItem = null;
	private JMenuItem helpFaqMenuItem = null;
	private JMenuItem helpAboutMenuItem = null;
	private RobocodeFrame robocodeFrame = null;
	private RobocodeManager manager = null;
	
	class EventHandler implements java.awt.event.ActionListener, javax.swing.event.MenuListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RobocodeMenuBar.this.getBattleNewMenuItem())
				battleNewActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getBattleOpenMenuItem())
				battleOpenActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getBattleSaveMenuItem())
				battleSaveActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getBattleSaveAsMenuItem())
				battleSaveAsActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getBattleExitMenuItem())
				battleExitActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getRobotEditorMenuItem())
				robotEditorActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getRobotImportMenuItem())
				robotImportActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getRobotPackagerMenuItem())
				robotPackagerActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getTeamCreateTeamMenuItem())
				teamCreateTeamActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getOptionsPreferencesMenuItem())
				optionsPreferencesActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getOptionsFitWindowMenuItem())
				optionsFitWindowActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpFaqMenuItem())
				helpFaqActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpOnlineHelpMenuItem())
				helpOnlineHelpActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpRobocodeApiMenuItem())
				helpRobocodeApiActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpCheckForNewVersionMenuItem())
				helpCheckForNewVersionActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpVersionsTxtMenuItem())
				helpVersionsTxtActionPerformed();
			if (e.getSource() == RobocodeMenuBar.this.getHelpAboutMenuItem())
				helpAboutActionPerformed();
		};
		public void menuDeselected(javax.swing.event.MenuEvent e) {
			manager.getBattleManager().resumeBattle();
		};
		public void menuSelected(javax.swing.event.MenuEvent e) {
			manager.getBattleManager().pauseBattle();
		};
		public void menuCanceled(javax.swing.event.MenuEvent e) {
		};
	};

	public RobocodeMenuBar.EventHandler eventHandler = new EventHandler();
/**
 * RoboCodeMenu constructor comment.
 */
public RobocodeMenuBar(RobocodeManager manager, RobocodeFrame robocodeFrame) {
	super();
	this.manager = manager;
	this.robocodeFrame = robocodeFrame;
	setName("RobocodeMenu");
	add(getBattleMenu());
	add(getRobotMenu());
	add(getOptionsMenu());
	add(getHelpMenu());
}
/**
 * Comment
 */
public void battleExitActionPerformed() {
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(robocodeFrame,java.awt.event.WindowEvent.WINDOW_CLOSING);
	robocodeFrame.dispatchEvent(evt);
	return;
}
/**
 * Handle battleNew menu item action
 */
public void battleNewActionPerformed() {

	manager.getWindowManager().showNewBattleDialog(manager.getBattleManager().getBattleProperties());
	
	return;
}
/**
 * Comment
 */
public void battleOpenActionPerformed() {
	manager.getWindowManager().showBattleOpenDialog();
	return;
}
/**
 * Comment
 */
public void battleSaveActionPerformed() {
	manager.getBattleManager().saveBattle();
	return;
}
/**
 * Comment
 */
public void battleSaveAsActionPerformed() {
	manager.getBattleManager().saveBattleAs();
	return;
}
/**
 * Return the battleExitMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getBattleExitMenuItem() {
	if (battleExitMenuItem == null) {
		try {
			battleExitMenuItem = new javax.swing.JMenuItem();
			battleExitMenuItem.setName("battleExitMenuItem");
			battleExitMenuItem.setText("Exit");
			battleExitMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return battleExitMenuItem;
}
/**
 * Return the Battle Menu
 * @return javax.swing.JMenu
 */
public JMenu getBattleMenu() {
	if (battleMenu == null) {
		try {
			battleMenu = new JMenu();
			battleMenu.setName("battleMenu");
			battleMenu.setText("Battle");
			
			battleMenu.add(getBattleNewMenuItem());
			battleMenu.add(getBattleOpenMenuItem());
			battleMenu.add(getBattleMenuSeparator1());
			battleMenu.add(getBattleSaveMenuItem());
			battleMenu.add(getBattleSaveAsMenuItem());
			battleMenu.add(getBattleExitMenuItem());
			
			battleMenu.addMenuListener(eventHandler);
		} catch (Throwable e) {
			log(e);
		}
	}
	return battleMenu;
}
/**
 * Return the battleMenuSeparator1.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getBattleMenuSeparator1() {
	if (battleMenuSeparator1 == null) {
		try {
			battleMenuSeparator1 = new javax.swing.JSeparator();
			battleMenuSeparator1.setName("battleMenuSeparator1");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return battleMenuSeparator1;
}
/**
 * Return the battleNewMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getBattleNewMenuItem() {
	if (battleNewMenuItem == null) {
		try {
			battleNewMenuItem = new javax.swing.JMenuItem();
			battleNewMenuItem.setName("battleNewMenuItem");
			battleNewMenuItem.setText("New");
			battleNewMenuItem.addActionListener(eventHandler);
		} catch (Throwable e) {
			log(e);
		}
	}
	return battleNewMenuItem;
}
/**
 * Return the battleOpenMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getBattleOpenMenuItem() {
	if (battleOpenMenuItem == null) {
		try {
			battleOpenMenuItem = new javax.swing.JMenuItem();
			battleOpenMenuItem.setName("battleOpenMenuItem");
			battleOpenMenuItem.setText("Open");
			battleOpenMenuItem.addActionListener(eventHandler);
		} catch (Throwable e) {
			log(e);
		}
	}
	return battleOpenMenuItem;
}
/**
 * Return the battleSaveAsMenuItem.
 * @return javax.swing.JMenuItem
 */
public JMenuItem getBattleSaveAsMenuItem() {
	if (battleSaveAsMenuItem == null) {
		try {
			battleSaveAsMenuItem = new javax.swing.JMenuItem();
			battleSaveAsMenuItem.setName("battleSaveAsMenuItem");
			battleSaveAsMenuItem.setText("Save As");
			battleSaveAsMenuItem.setEnabled(false);
			battleSaveAsMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return battleSaveAsMenuItem;
}
/**
 * Return the battleSaveMenuItem.
 * @return javax.swing.JMenuItem
 */
public JMenuItem getBattleSaveMenuItem() {
	if (battleSaveMenuItem == null) {
		try {
			battleSaveMenuItem = new javax.swing.JMenuItem();
			battleSaveMenuItem.setName("battleSaveMenuItem");
			battleSaveMenuItem.setText("Save");
			battleSaveMenuItem.setEnabled(false);
			battleSaveMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Exception e) {
			log(e);
		}
	}
	return battleSaveMenuItem;
}
/**
 * Return the helpAboutMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpAboutMenuItem() {
	if (helpAboutMenuItem == null) {
		try {
			helpAboutMenuItem = new javax.swing.JMenuItem();
			helpAboutMenuItem.setName("helpAboutMenuItem");
			helpAboutMenuItem.setText("About");
			helpAboutMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpAboutMenuItem;
}
/**
 * Return the helpCheckForNewVersion menu item.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpCheckForNewVersionMenuItem() {
	if (helpCheckForNewVersionMenuItem == null) {
		try {
			helpCheckForNewVersionMenuItem = new javax.swing.JMenuItem();
			helpCheckForNewVersionMenuItem.setName("helpCheckForNewVersionMenuItem");
			helpCheckForNewVersionMenuItem.setText("Check for new version");
			helpCheckForNewVersionMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpCheckForNewVersionMenuItem;
}
/**
 * Return the Help Menu.
 * @return javax.swing.JMenu
 */
public javax.swing.JMenu getHelpMenu() {
	if (helpMenu == null) {
		try {
			helpMenu = new javax.swing.JMenu();
			helpMenu.setName("helpMenu");
			helpMenu.setText("Help");
			helpMenu.add(getHelpOnlineHelpMenuItem());
			helpMenu.add(getHelpRobocodeApiMenuItem());
			helpMenu.add(getHelpCheckForNewVersionMenuItem());
			helpMenu.add(getHelpVersionsTxtMenuItem());
			helpMenu.add(getHelpFaqMenuItem());
			helpMenu.add(getHelpAboutMenuItem());
			helpMenu.addMenuListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpMenu;
}
/**
 * Return the helpOnlineHelpMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpFaqMenuItem() {
	if (helpFaqMenuItem == null) {
		try {
			helpFaqMenuItem = new javax.swing.JMenuItem();
			helpFaqMenuItem.setName("helpFaqMenuItem");
			helpFaqMenuItem.setText("Robocode FAQ");
			helpFaqMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpFaqMenuItem;
}
/**
 * Return the helpOnlineHelpMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpOnlineHelpMenuItem() {
	if (helpOnlineHelpMenuItem == null) {
		try {
			helpOnlineHelpMenuItem = new javax.swing.JMenuItem();
			helpOnlineHelpMenuItem.setName("helpOnlineHelpMenuItem");
			helpOnlineHelpMenuItem.setText("Online Help");
			helpOnlineHelpMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpOnlineHelpMenuItem;
}
/**
 * Return the helpVersionsTxtMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpVersionsTxtMenuItem() {
	if (helpVersionsTxtMenuItem == null) {
		try {
			helpVersionsTxtMenuItem = new javax.swing.JMenuItem();
			helpVersionsTxtMenuItem.setName("VersionsTxt");
			helpVersionsTxtMenuItem.setText("Version Info");
			helpVersionsTxtMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpVersionsTxtMenuItem;
}
/**
 * Return the helpRobocodeApiMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getHelpRobocodeApiMenuItem() {
	if (helpRobocodeApiMenuItem == null) {
		try {
			helpRobocodeApiMenuItem = new javax.swing.JMenuItem();
			helpRobocodeApiMenuItem.setName("helpRobocodeApiMenuItem");
			helpRobocodeApiMenuItem.setText("Robocode API");
			helpRobocodeApiMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return helpRobocodeApiMenuItem;
}
/**
 * Return the optionsPreferencesMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getOptionsFitWindowMenuItem() {
	if (optionsFitWindowMenuItem == null) {
		try {
			optionsFitWindowMenuItem = new javax.swing.JMenuItem();
			optionsFitWindowMenuItem.setName("optionsFitWindowMenuItem");
			optionsFitWindowMenuItem.setText("Default Window Size");
			optionsFitWindowMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsFitWindowMenuItem;
}
/**
 * Return the Options Menu.
 * @return javax.swing.JMenu
 */
private JMenu getOptionsMenu() {
	if (optionsMenu == null) {
		try {
			optionsMenu = new javax.swing.JMenu();
			optionsMenu.setName("optionsMenu");
			optionsMenu.setText("Options");
			optionsMenu.add(getOptionsPreferencesMenuItem());
			optionsMenu.add(getOptionsFitWindowMenuItem());
			optionsMenu.addMenuListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsMenu;
}
/**
 * Return the optionsPreferencesMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getOptionsPreferencesMenuItem() {
	if (optionsPreferencesMenuItem == null) {
		try {
			optionsPreferencesMenuItem = new javax.swing.JMenuItem();
			optionsPreferencesMenuItem.setName("optionsPreferencesMenuItem");
			optionsPreferencesMenuItem.setText("Preferences");
			optionsPreferencesMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsPreferencesMenuItem;
}
/**
 * Return the robotEditorMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getRobotEditorMenuItem() {
	if (robotEditorMenuItem == null) {
		try {
			robotEditorMenuItem = new javax.swing.JMenuItem();
			robotEditorMenuItem.setName("robotEditorMenuItem");
			robotEditorMenuItem.setText("Editor");
			robotEditorMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotEditorMenuItem;
}
/**
 * Return the robotImportMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getRobotImportMenuItem() {
	if (robotImportMenuItem == null) {
		try {
			robotImportMenuItem = new javax.swing.JMenuItem();
			robotImportMenuItem.setName("robotImportMenuItem");
			robotImportMenuItem.setText("Import downloaded robot");
			robotImportMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotImportMenuItem;
}
/**
 * Return the Robot Menu.
 * @return javax.swing.JMenu
 */
public JMenu getRobotMenu() {
	if (robotMenu == null) {
		try {
			robotMenu = new javax.swing.JMenu();
			robotMenu.setName("robotMenu");
			robotMenu.setText("Robot");
			robotMenu.add(getRobotEditorMenuItem());
			robotMenu.add(getRobotImportMenuItem());
			robotMenu.add(getRobotPackagerMenuItem());
			robotMenu.add(getTeamMenu());
			robotMenu.addMenuListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotMenu;
}
/**
 * Return the robotPackagerMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getRobotPackagerMenuItem() {
	if (robotPackagerMenuItem == null) {
		try {
			robotPackagerMenuItem = new javax.swing.JMenuItem();
			robotPackagerMenuItem.setName("robotPackagerMenuItem");
			robotPackagerMenuItem.setText("Package robot for upload");
			robotPackagerMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotPackagerMenuItem;
}
/**
 * Return the Team Menu.
 * @return javax.swing.JMenu
 */
public JMenu getTeamMenu() {
	if (teamMenu == null) {
		try {
			teamMenu = new javax.swing.JMenu();
			teamMenu.setName("teamMenu");
			teamMenu.setText("Team");
			teamMenu.add(getTeamCreateTeamMenuItem());
			teamMenu.addMenuListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return teamMenu;
}
/**
 * Return the teamCreateTeamMenuItem.
 * @return javax.swing.JMenuItem
 */
private JMenuItem getTeamCreateTeamMenuItem() {
	if (teamCreateTeamMenuItem == null) {
		try {
			teamCreateTeamMenuItem = new javax.swing.JMenuItem();
			teamCreateTeamMenuItem.setName("teamCreateTeamMenuItem");
			teamCreateTeamMenuItem.setText("Create Team");
			teamCreateTeamMenuItem.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return teamCreateTeamMenuItem;
}
public void teamCreateTeamActionPerformed()
{
	manager.getWindowManager().showCreateTeamDialog();
}

/**
 * Comment
 */
public void helpAboutActionPerformed() {
	manager.getWindowManager().showAboutBox();
	return;
}
/**
 * Comment
 */
public void helpCheckForNewVersionActionPerformed() {
	manager.getVersionManager().checkForNewVersion(true);
	return;
}
/**
 * Comment
 */
public void helpFaqActionPerformed() {

	manager.getWindowManager().showFaq();
	return;
}
/**
 * Comment
 */
public void helpOnlineHelpActionPerformed() {

	manager.getWindowManager().showOnlineHelp();
	return;
}
/**
 * Comment
 */
public void helpVersionsTxtActionPerformed() {

	manager.getWindowManager().showVersionsTxt();
	return;
}
/**
 * Comment
 */
public void helpRobocodeApiActionPerformed() {
	manager.getWindowManager().showHelpApi();
	return;
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
public void optionsFitWindowActionPerformed() {
	Utils.fitWindow(manager.getWindowManager().getRobocodeFrame());
	return;
}
/**
 * Comment
 */
public void optionsPreferencesActionPerformed() {

	manager.getWindowManager().showOptionsPreferences();
	return;
}
/**
 * Comment
 */
public void robotEditorActionPerformed() {
	manager.getWindowManager().showRobocodeEditor();
}
/**
 * Comment
 */
public void robotImportActionPerformed() {
	manager.getWindowManager().showImportRobotDialog();
}
/**
 * Comment
 */
public void robotPackagerActionPerformed() {
	manager.getWindowManager().showRobotPackager();
}
}
