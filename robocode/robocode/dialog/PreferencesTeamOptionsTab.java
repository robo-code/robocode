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
import java.awt.*;
import robocode.manager.*;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2001 11:07:52 PM)
 * @author: Mathew A. Nelson
 */
public class PreferencesTeamOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();
	private javax.swing.JCheckBox optionsTeamShowTeamRobotsCheckBox = null;
	private JPanel battleOptionsPanel = null;
	private JButton defaultsButton = null;
	
	public RobocodeManager manager = null;
	
class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PreferencesTeamOptionsTab.this.getDefaultsButton())
				defaultsButtonActionPerformed();
		};
	};
/**
 * PreferencesDialog constructor
 */
public PreferencesTeamOptionsTab(RobocodeManager manager)
{
	super();
	this.manager = manager;
	initialize();
}
/**
 * Comment
 */
private void defaultsButtonActionPerformed() {
	getOptionsTeamShowTeamRobotsCheckBox().setSelected(false);
	return;
}
/**
 * Return the defaultsButton
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDefaultsButton() {
	if (defaultsButton == null) {
		try {
			defaultsButton = new javax.swing.JButton();
			defaultsButton.setName("defaultsButton");
			defaultsButton.setText("Defaults");
			defaultsButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return defaultsButton;
}
private javax.swing.JCheckBox getOptionsTeamShowTeamRobotsCheckBox() {
	if (optionsTeamShowTeamRobotsCheckBox == null) {
		try {
			optionsTeamShowTeamRobotsCheckBox = new javax.swing.JCheckBox();
			optionsTeamShowTeamRobotsCheckBox.setName("optionsTeamShowTeamRobotsCheckBox");
			optionsTeamShowTeamRobotsCheckBox.setText("Allow TeamRobots and Droids to compete outside of a team");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsTeamShowTeamRobotsCheckBox;
}

private javax.swing.JPanel getBattleOptionsPanel() {
	if (battleOptionsPanel == null) {
		try {
			battleOptionsPanel = new javax.swing.JPanel();
			battleOptionsPanel.setName("teamOptionsPanel");
			battleOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Team"));
			battleOptionsPanel.setLayout(new BoxLayout(battleOptionsPanel,BoxLayout.Y_AXIS));
			battleOptionsPanel.add(getOptionsTeamShowTeamRobotsCheckBox());
			battleOptionsPanel.add(new JLabel(" "));
			battleOptionsPanel.add(getDefaultsButton());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return battleOptionsPanel;
}

private void initialize() {
	try {
		setLayout(new GridLayout(1,2));
		add(getBattleOptionsPanel());
		loadPreferences(manager.getProperties());
	} catch (java.lang.Throwable e) {
		log(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 11:16:59 AM)
 * @param props java.util.Properties
 */
private void loadPreferences(RobocodeProperties robocodeProperties) {
	getOptionsTeamShowTeamRobotsCheckBox().setSelected(robocodeProperties.getOptionsTeamShowTeamRobots());
}

/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 11:16:59 AM)
 * @param props java.util.Properties
 */
public void storePreferences() {
	manager.getProperties().setOptionsTeamShowTeamRobots(getOptionsTeamShowTeamRobotsCheckBox().isSelected());
	manager.saveProperties();
}

public boolean isReady()
{
	return true;	
}
}
