/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.settings.ISettingsManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class NewBattleRulesTab extends JPanel {

	private ISettingsManager settingsManager;
	private BattleProperties battleProperties;
	
	private final JLabel numberOfRoundsLabel = new JLabel("Number of Rounds:");
	private final JLabel gunCoolingRateLabel = new JLabel("Gun Cooling Rate:");
	private final JLabel inactivityTimeLabel = new JLabel("Inactivity Time:");
	private final JLabel hideEnemyNamesLabel = new JLabel("Hide Enemy Names:");

	private final JButton restoreDefaultsButton = new JButton("Restore Defaults");
	
	private final JTextField numberOfRoundsTextField = new JTextField(5);
	private final JTextField gunCoolingRateTextField = new JTextField(5);
	private final JTextField inactivityTimeTextField = new JTextField(5);
	private final JCheckBox hideEnemyNamesCheckBox = new JCheckBox();

	public NewBattleRulesTab() {
		super();
	}

	public void setup(ISettingsManager settingsManager, final BattleProperties battleProperties) {
		this.settingsManager = settingsManager;
		this.battleProperties = battleProperties;

		EventHandler eventHandler = new EventHandler();

		JPanel rulesPanel = createRulesPanel();
		rulesPanel.addAncestorListener(eventHandler);
		add(rulesPanel);

		restoreDefaultsButton.addActionListener(eventHandler);
		add(restoreDefaultsButton);
	}

	private JPanel createRulesPanel() {
		JPanel panel = new JPanel();

		panel.addAncestorListener(new EventHandler());
		panel.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);

		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();

		GroupLayout.ParallelGroup left = layout.createParallelGroup();
		left.addComponent(numberOfRoundsLabel);
		left.addComponent(gunCoolingRateLabel);
		left.addComponent(inactivityTimeLabel);
		left.addComponent(hideEnemyNamesLabel);
		leftToRight.addGroup(left);
		
		GroupLayout.ParallelGroup right = layout.createParallelGroup();
		right.addComponent(numberOfRoundsTextField);
		right.addComponent(gunCoolingRateTextField);
		right.addComponent(inactivityTimeTextField);
		right.addComponent(hideEnemyNamesCheckBox);
		leftToRight.addGroup(right);
		
		GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();

		GroupLayout.ParallelGroup row0 = layout.createParallelGroup(Alignment.BASELINE);
		row0.addComponent(numberOfRoundsLabel);
		row0.addComponent(numberOfRoundsTextField);
		topToBottom.addGroup(row0);

		GroupLayout.ParallelGroup row1 = layout.createParallelGroup(Alignment.BASELINE);
		row1.addComponent(gunCoolingRateLabel);
		row1.addComponent(gunCoolingRateTextField);
		topToBottom.addGroup(row1);

		GroupLayout.ParallelGroup row2 = layout.createParallelGroup(Alignment.BASELINE);
		row2.addComponent(inactivityTimeLabel);
		row2.addComponent(inactivityTimeTextField);
		topToBottom.addGroup(row2);

		GroupLayout.ParallelGroup row3 = layout.createParallelGroup(Alignment.CENTER);
		row3.addComponent(hideEnemyNamesLabel);
		row3.addComponent(hideEnemyNamesCheckBox);
		topToBottom.addGroup(row3);

		layout.setHorizontalGroup(leftToRight);
		layout.setVerticalGroup(topToBottom);
		
		return panel;
	}	
	
	private class EventHandler implements AncestorListener, ActionListener {
		@Override
		public void ancestorAdded(AncestorEvent event) {
			pushBattlePropertiesToUIComponents();
			numberOfRoundsTextField.setText("" + battleProperties.getNumRounds());
			gunCoolingRateTextField.setText("" + battleProperties.getGunCoolingRate());
			inactivityTimeTextField.setText("" + battleProperties.getInactivityTime());
			hideEnemyNamesCheckBox.setSelected(battleProperties.getHideEnemyNames());
		}

		@Override
		public void ancestorRemoved(AncestorEvent event) {
			Integer numberOfRounds;
			try {
				numberOfRounds = Integer.parseInt(numberOfRoundsTextField.getText());
			} catch (NumberFormatException e) {
				numberOfRounds = null;
			}
			if (numberOfRounds != null) {
				settingsManager.setBattleDefaultNumberOfRounds(numberOfRounds);
				battleProperties.setNumRounds(numberOfRounds);
			}
			Double gunCoolingRate;
			try {
				gunCoolingRate = Double.parseDouble(gunCoolingRateTextField.getText());
			} catch (NumberFormatException e) {
				gunCoolingRate = null;
			}
			if (gunCoolingRate != null) {
				settingsManager.setBattleDefaultGunCoolingRate(gunCoolingRate);
				battleProperties.setGunCoolingRate(gunCoolingRate);
			}
			Integer inactivityTime;
			try {
				inactivityTime = Integer.parseInt(inactivityTimeTextField.getText());
			} catch (NumberFormatException e) {
				inactivityTime = null;
			}
			if (inactivityTime != null) {
				settingsManager.setBattleDefaultInactivityTime(inactivityTime);
				battleProperties.setInactivityTime(inactivityTime);
			}
			boolean hideEnemyNames = hideEnemyNamesCheckBox.isSelected();
			settingsManager.setBattleDefaultHideEnemyNames(hideEnemyNames);
			battleProperties.setHideEnemyNames(hideEnemyNames);
		}

		@Override
		public void ancestorMoved(AncestorEvent event) {}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == restoreDefaultsButton) {
				battleProperties.setNumRounds(10);
				battleProperties.setGunCoolingRate(0.1);
				battleProperties.setInactivityTime(450);
				battleProperties.setHideEnemyNames(false);
				
				pushBattlePropertiesToUIComponents();
			}
		}

		private void pushBattlePropertiesToUIComponents() {
			numberOfRoundsTextField.setText("" + battleProperties.getNumRounds());
			gunCoolingRateTextField.setText("" + battleProperties.getGunCoolingRate());
			inactivityTimeTextField.setText("" + battleProperties.getInactivityTime());
			hideEnemyNamesCheckBox.setSelected(battleProperties.getHideEnemyNames());
		}
	}
}
