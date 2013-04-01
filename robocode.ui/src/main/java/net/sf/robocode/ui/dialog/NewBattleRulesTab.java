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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.robocode.battle.BattleProperties;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class NewBattleRulesTab extends JPanel {

	private BattleProperties battleProperties;
	
	private final EventHandler eventHandler = new EventHandler();

	private final JLabel gunCoolingRateLabel = new JLabel("Gun Cooling Rate:");
	private final JLabel inactivityTimeLabel = new JLabel("Inactivity Time:");
	private final JLabel hideEnemyNamesLabel = new JLabel("Hide Enemy Names:");

	private final JTextField gunCoolingRateField = new JTextField();
	private final JTextField inactivityTimeField = new JTextField();
	private final JCheckBox hideEnemyNamesCheckBox = new JCheckBox();

	public NewBattleRulesTab() {
		super();
	}

	public void setup(BattleProperties battleProperties) {
		this.battleProperties = battleProperties;

		gunCoolingRateField.setText("" + battleProperties.getGunCoolingRate());
		inactivityTimeField.setText("" + battleProperties.getInactivityTime());
		hideEnemyNamesCheckBox.setSelected(battleProperties.getHideEnemyNames());

		gunCoolingRateField.getDocument().addDocumentListener(eventHandler);
		inactivityTimeField.getDocument().addDocumentListener(eventHandler);
		hideEnemyNamesCheckBox.addActionListener(eventHandler);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);

		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();

		GroupLayout.ParallelGroup left = layout.createParallelGroup();
		left.addComponent(gunCoolingRateLabel);
		left.addComponent(inactivityTimeLabel);
		left.addComponent(hideEnemyNamesLabel);
		leftToRight.addGroup(left);
		
		GroupLayout.ParallelGroup right = layout.createParallelGroup();
		right.addComponent(gunCoolingRateField);
		right.addComponent(inactivityTimeField);
		right.addComponent(hideEnemyNamesCheckBox);
		leftToRight.addGroup(right);
		
		GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();

		GroupLayout.ParallelGroup row1 = layout.createParallelGroup();
		row1.addComponent(gunCoolingRateLabel);
		row1.addComponent(gunCoolingRateField);
		topToBottom.addGroup(row1);

		GroupLayout.ParallelGroup row2 = layout.createParallelGroup();
		row2.addComponent(inactivityTimeLabel);
		row2.addComponent(inactivityTimeField);
		topToBottom.addGroup(row2);

		GroupLayout.ParallelGroup row3 = layout.createParallelGroup();
		row3.addComponent(hideEnemyNamesLabel);
		row3.addComponent(hideEnemyNamesCheckBox);
		topToBottom.addGroup(row3);

		layout.setHorizontalGroup(leftToRight);
		layout.setVerticalGroup(topToBottom);
		
		add(panel);
	}
	
	private class EventHandler implements ActionListener, DocumentListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			update();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}

		private void update() {
			Double gunCoolingRate;
			try {
				gunCoolingRate = Double.parseDouble(gunCoolingRateField.getText());
			} catch (NumberFormatException e) {
				gunCoolingRate = null;
			}
			if (gunCoolingRate != null) {
				battleProperties.setGunCoolingRate(gunCoolingRate);
			}
			Integer inactivityTime;
			try {
				inactivityTime = Integer.parseInt(inactivityTimeField.getText());
			} catch (NumberFormatException e) {
				inactivityTime = null;
			}
			if (inactivityTime != null) {
				battleProperties.setInactivityTime(inactivityTime);
			}
			battleProperties.setHideEnemyNames(hideEnemyNamesCheckBox.isSelected());
		}
	}
}
