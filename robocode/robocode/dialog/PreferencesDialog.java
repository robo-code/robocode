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

import java.awt.BorderLayout;
import robocode.manager.*;
import robocode.util.*;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2001 11:07:52 PM)
 * @author: Mathew A. Nelson
 */
public class PreferencesDialog extends javax.swing.JDialog implements WizardListener {
	private javax.swing.JPanel preferencesDialogContentPane = null;
	private WizardTabbedPane tabbedPane = null;
	private WizardController buttonsPanel = null;
	private PreferencesViewOptionsTab viewOptionsTab = null;
	private PreferencesTeamOptionsTab teamOptionsTab = null;
	private PreferencesDevelopmentOptionsTab developmentOptionsTab = null;
	EventHandler eventHandler = new EventHandler();
	public RobocodeManager manager = null;

class EventHandler implements java.awt.event.WindowListener {
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == PreferencesDialog.this) 
				manager.getBattleManager().resumeBattle();
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};

	};
/**
 * PreferencesDialog constructor
 */
public PreferencesDialog(RobocodeManager manager)
{
	super(manager.getWindowManager().getRobocodeFrame());
	this.manager = manager;
	initialize();
}
/**
 * Comment
 */
public void cancelButtonActionPerformed() {
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(evt);
	return;
}
/**
 * Return the buttonsPanel
 * @return javax.swing.JPanel
 */
private WizardController getButtonsPanel() {
	if (buttonsPanel == null) {
		try {
			buttonsPanel = getTabbedPane().getWizardController();
			buttonsPanel.setName("buttonsPanel");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return buttonsPanel;
}
/**
 * Return the preferencesDialogContentPane
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPreferencesDialogContentPane() {
	getButtonsPanel();
	if (preferencesDialogContentPane == null) {
		try {
			preferencesDialogContentPane = new javax.swing.JPanel();
			preferencesDialogContentPane.setName("preferencesDialogContentPane");
			preferencesDialogContentPane.setLayout(new BorderLayout());
			preferencesDialogContentPane.add(getTabbedPane(),BorderLayout.CENTER);
			preferencesDialogContentPane.add(getButtonsPanel(),BorderLayout.SOUTH);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return preferencesDialogContentPane;
}
/**
 * Return the tabbedPane
 * @return javax.swing.JTabbedPane
 */
private WizardTabbedPane getTabbedPane() {
	if (tabbedPane == null) {
		try {
			tabbedPane = new WizardTabbedPane(this);
			tabbedPane.setName("tabbedPane");
			tabbedPane.insertTab("View Options", null, getViewOptionsTab(), null, 0);
			//tabbedPane.insertTab("Team Options", null, getTeamOptionsTab(), null, 1);
			tabbedPane.insertTab("Development Options", null, getDevelopmentOptionsTab(), null, 1);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return tabbedPane;
}
/**
 * Return the viewOptionsTab
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getViewOptionsTab() {
	if (viewOptionsTab == null) {
		try {
			viewOptionsTab = new PreferencesViewOptionsTab(manager);
			viewOptionsTab.setName("viewOptionsTab");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return viewOptionsTab;
}
private javax.swing.JPanel getTeamOptionsTab() {
	if (teamOptionsTab == null) {
		try {
			teamOptionsTab = new PreferencesTeamOptionsTab(manager);
			teamOptionsTab.setName("teamOptionsTab");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return teamOptionsTab;
}
private javax.swing.JPanel getDevelopmentOptionsTab() {
	if (developmentOptionsTab == null) {
		try {
			developmentOptionsTab = new PreferencesDevelopmentOptionsTab(manager);
			developmentOptionsTab.setName("developmentOptionsTab");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return developmentOptionsTab;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("PreferencesDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//		setSize(597, 411);
		setTitle("Preferences");
		setContentPane(getPreferencesDialogContentPane());
		addWindowListener(eventHandler);
	} catch (java.lang.Throwable e) {
		log(e);
	}
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
public void finishButtonActionPerformed() {
	viewOptionsTab.storePreferences();
	//teamOptionsTab.storePreferences();
	developmentOptionsTab.storePreferences();
	java.awt.AWTEvent evt = new java.awt.event.WindowEvent(this,java.awt.event.WindowEvent.WINDOW_CLOSING);
	this.dispatchEvent(evt);
	return;
}
}
