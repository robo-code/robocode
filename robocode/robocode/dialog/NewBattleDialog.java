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

/**
 * Insert the type's description here.
 * Creation date: (12/28/2000 1:18:50 AM)
 * @author: Administrator
 */
import javax.swing.*;
import java.awt.event.*;
 
import robocode.util.*;
import robocode.manager.*;
import robocode.battle.BattleProperties;
import robocode.repository.FileSpecificationVector;

/**
 * Insert the type's description here.
 * Creation date: (12/28/2000 1:18:50 AM)
 * @author: Mathew A. Nelson
 */
public class NewBattleDialog extends JDialog implements WizardListener {

	private EventHandler eventHandler = new EventHandler();
	private JPanel newBattleDialogContentPane = null;
	private WizardTabbedPane tabbedPane = null;
	private NewBattleBattleFieldTab battleFieldTab = null;

	private BattleProperties battleProperties;

	private NewBattleRulesTab rulesTab = null;
	private WizardController wizardController = null;
	public int MAXROBOTS = 256; //64;
	public int MINROBOTS = 1;
	private RobotSelectionPanel robotSelectionPanel = null;
	//private Vector selectedRobotList = null;
	private RobocodeManager manager = null;


class EventHandler implements java.awt.event.WindowListener, ActionListener {

		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == NewBattleDialog.this) 
				manager.getBattleManager().resumeBattle();
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh"))
			{
				getRobotSelectionPanel().refreshRobotList();
			}
		};
	};



/**
 * Comment
 */
public void cancelButtonActionPerformed() {
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(evt);
	return;
}
/**
 * Comment
 */
public void finishButtonActionPerformed() {
	if (robotSelectionPanel.getSelectedRobotsCount() > 24)
	{
		if (JOptionPane.showConfirmDialog(this,"Warning:  The battle you are about to start (" + robotSelectionPanel.getSelectedRobotsCount() + " robots) "
		+ " is very large and will consume a lot of CPU and memory.  Do you wish to proceed?",
		"Large Battle Warning",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
			return;
	}
	if (robotSelectionPanel.getSelectedRobotsCount() == 1)
	{
		if (JOptionPane.showConfirmDialog(this,"You have only selected one robot.  For normal battles you should select at least 2.\nDo you wish to proceed anyway?",
		"Just one robot?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
			return;
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
			manager.getBattleManager().startNewBattle(battleProperties,false);
		}
	}).start();
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(evt);
	return;
}

/**
 * Return the battleFieldTab
 * @return javax.swing.JPanel
 */
private NewBattleBattleFieldTab getBattleFieldTab() {
	if (battleFieldTab == null) {
		try {
			battleFieldTab = new NewBattleBattleFieldTab();
			battleFieldTab.setName("battleFieldTab");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return battleFieldTab;
}




/**
 * Return the newBattleDialogContentPane
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getNewBattleDialogContentPane() {
	if (newBattleDialogContentPane == null) {
		try {
			newBattleDialogContentPane = new javax.swing.JPanel();
			newBattleDialogContentPane.setName("newBattleDialogContentPane");
			newBattleDialogContentPane.setLayout(new java.awt.BorderLayout());
			newBattleDialogContentPane.add(getWizardController(),java.awt.BorderLayout.SOUTH);
			newBattleDialogContentPane.add(getTabbedPane(),java.awt.BorderLayout.CENTER);
			newBattleDialogContentPane.registerKeyboardAction(eventHandler,"Refresh",KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return newBattleDialogContentPane;
}


/**
 * Return the rulesTab property value.
 * @return robocode.dialog.NewBattleRulesTab
 */
private NewBattleRulesTab getRulesTab() {
	if (rulesTab == null) {
		try {
			rulesTab = new robocode.dialog.NewBattleRulesTab();
			rulesTab.setName("Rules");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return rulesTab;
}
/**
 * Insert the method's description here.
 * Creation date: (12/28/2000 1:28:02 PM)
 * @return java.util.Vector
 */
public FileSpecificationVector getSelectedRobots() {
	return getRobotSelectionPanel().getSelectedRobots();
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("newBattleDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("New Battle");
		setContentPane(getNewBattleDialogContentPane());
		addWindowListener(eventHandler);
	} catch (java.lang.Throwable e) {
		log(e);
	}
}

private void log(Throwable e) {
	Utils.log(e);
}
private void log(String s) {
	Utils.log(s);
}

/**
 * NewBattleDialog constructor comment.
 * @param owner java.awt.Frame
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
 * @return javax.swing.JButton
 */
private WizardController getWizardController() {
	if (wizardController == null) {
		try {
			wizardController = getTabbedPane().getWizardController();
			wizardController.setName("wizardController");
			wizardController.setFinishButtonText("Start Battle");
			wizardController.setFocusOnEnabled(true);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return wizardController;
}

/**
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
private RobotSelectionPanel getRobotSelectionPanel() {
	if (robotSelectionPanel == null) {
		try {

			String selectedRobots = "";
			if (battleProperties != null)
				selectedRobots = battleProperties.getSelectedRobots();
	
			robotSelectionPanel = new RobotSelectionPanel(manager.getRobotRepositoryManager(), MINROBOTS, MAXROBOTS, true,
				"Select robots for the battle",false,false,false,false,false,!manager.getProperties().getOptionsTeamShowTeamRobots(),selectedRobots);
			robotSelectionPanel.setName("Robots");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return robotSelectionPanel;
}

/**
 * Return the tabbedPane.
 * @return javax.swing.JTabbedPane
 */
private WizardTabbedPane getTabbedPane() {
	if (tabbedPane == null) {
		try {
			tabbedPane = new WizardTabbedPane(this);
			tabbedPane.setName("tabbedPane");
			//tabbedPane.setBounds(10, 6, 521, 335);
			tabbedPane.insertTab("Robots", null, getRobotSelectionPanel(), null, 0);
			tabbedPane.insertTab("BattleField", null, getBattleFieldTab(), null, 1);
			tabbedPane.insertTab("Rules", null, getRulesTab(), null, 2);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return tabbedPane;
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 12:40:42 PM)
 * @param battleProperties java.util.Properties
 */
private void processBattleProperties() {
	if (battleProperties == null)
		return;

	getBattleFieldTab().setBattleFieldWidth(battleProperties.getBattlefieldWidth());
	getBattleFieldTab().setBattleFieldHeight(battleProperties.getBattlefieldHeight());
	getRobotSelectionPanel().setNumRounds(battleProperties.getNumRounds());
	
	getRulesTab().setGunCoolingRate(battleProperties.getGunCoolingRate());
	getRulesTab().setInactivityTime(battleProperties.getInactivityTime());
}

}