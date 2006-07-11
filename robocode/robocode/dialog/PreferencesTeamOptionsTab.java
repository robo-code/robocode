/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class PreferencesTeamOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();
	private JCheckBox optionsTeamShowTeamRobotsCheckBox;
	private JPanel battleOptionsPanel;
	private JButton defaultsButton;

	public RobocodeManager manager;

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == PreferencesTeamOptionsTab.this.getDefaultsButton()) {
				defaultsButtonActionPerformed();
			}
		}
	}

	/**
	 * PreferencesDialog constructor
	 */
	public PreferencesTeamOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	private void defaultsButtonActionPerformed() {
		getOptionsTeamShowTeamRobotsCheckBox().setSelected(false);
	}

	/**
	 * Return the defaultsButton
	 * 
	 * @return JButton
	 */
	private JButton getDefaultsButton() {
		if (defaultsButton == null) {
			defaultsButton = new JButton();
			defaultsButton.setText("Defaults");
			defaultsButton.addActionListener(eventHandler);
		}
		return defaultsButton;
	}

	private JCheckBox getOptionsTeamShowTeamRobotsCheckBox() {
		if (optionsTeamShowTeamRobotsCheckBox == null) {
			optionsTeamShowTeamRobotsCheckBox = new JCheckBox();
			optionsTeamShowTeamRobotsCheckBox.setText("Allow TeamRobots and Droids to compete outside of a team");
		}
		return optionsTeamShowTeamRobotsCheckBox;
	}

	private JPanel getBattleOptionsPanel() {
		if (battleOptionsPanel == null) {
			battleOptionsPanel = new JPanel();
			battleOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Team"));
			battleOptionsPanel.setLayout(new BoxLayout(battleOptionsPanel, BoxLayout.Y_AXIS));
			battleOptionsPanel.add(getOptionsTeamShowTeamRobotsCheckBox());
			battleOptionsPanel.add(new JLabel(" "));
			battleOptionsPanel.add(getDefaultsButton());
		}
		return battleOptionsPanel;
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getBattleOptionsPanel());
		loadPreferences(manager.getProperties());
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getOptionsTeamShowTeamRobotsCheckBox().setSelected(robocodeProperties.getOptionsTeamShowTeamRobots());
	}

	public void storePreferences() {
		manager.getProperties().setOptionsTeamShowTeamRobots(getOptionsTeamShowTeamRobotsCheckBox().isSelected());
		manager.saveProperties();
	}

	public boolean isReady() {
		return true;
	}
}
