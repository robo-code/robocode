/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class NewBattleRulesTab extends JPanel {
	private JLabel gunCoolingRateLabel;
	private JTextField gunCoolingRateField;
	private JLabel inactivityTimeLabel;
	private JTextField inactivityTimeField;
	private JLabel hideEnemyNamesLabel;
	private JCheckBox hideEnemyNamesCheckBox;

	public NewBattleRulesTab() {
		super();
		initialize();
	}

	public double getGunCoolingRate() {
		return Double.parseDouble(getGunCoolingRateField().getText());
	}

	private JTextField getGunCoolingRateField() {
		if (gunCoolingRateField == null) {
			gunCoolingRateField = new JTextField();
		}
		return gunCoolingRateField;
	}

	private JLabel getGunCoolingRateLabel() {
		if (gunCoolingRateLabel == null) {
			gunCoolingRateLabel = new JLabel();
			gunCoolingRateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			gunCoolingRateLabel.setText("Gun Cooling Rate:");
			gunCoolingRateLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			gunCoolingRateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return gunCoolingRateLabel;
	}

	public long getInactivityTime() {
		return Long.parseLong(getInactivityTimeField().getText());
	}

	private JTextField getInactivityTimeField() {
		if (inactivityTimeField == null) {
			inactivityTimeField = new JTextField();
		}
		return inactivityTimeField;
	}

	private JLabel getInactivityTimeLabel() {
		if (inactivityTimeLabel == null) {
			inactivityTimeLabel = new JLabel();
			inactivityTimeLabel.setText("Inactivity Time:");
			inactivityTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return inactivityTimeLabel;
	}

	public boolean getHideEnemyNames() {
		return getHideEnemyNamesCheckBox().isSelected();
	}

	private JCheckBox getHideEnemyNamesCheckBox() {
		if (hideEnemyNamesCheckBox == null) {
			hideEnemyNamesCheckBox = new JCheckBox();
		}
		return hideEnemyNamesCheckBox;
	}

	private JLabel getHideEnemyNamesLabel() {
		if (hideEnemyNamesLabel == null) {
			hideEnemyNamesLabel = new JLabel();
			hideEnemyNamesLabel.setText("Hide Enemy Names:");
			hideEnemyNamesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return hideEnemyNamesLabel;
	}

	private void initialize() {
		JPanel j = new JPanel();

		j.setLayout(new GridLayout(3, 2, 5, 5));
		j.setBorder(BorderFactory.createEtchedBorder());
		j.add(getGunCoolingRateLabel(), getGunCoolingRateLabel().getName());
		j.add(getGunCoolingRateField(), getGunCoolingRateField().getName());
		j.add(getInactivityTimeLabel(), getInactivityTimeLabel().getName());
		j.add(getInactivityTimeField(), getInactivityTimeField().getName());
		j.add(getHideEnemyNamesLabel(), getHideEnemyNamesLabel().getName());
		j.add(getHideEnemyNamesCheckBox(), getHideEnemyNamesCheckBox().getName());
		add(j);
	}

	public void setGunCoolingRate(double gunCoolingRate) {
		getGunCoolingRateField().setText("" + gunCoolingRate);
	}

	public void setInactivityTime(long inactivityTime) {
		getInactivityTimeField().setText("" + inactivityTime);
	}

	public void setHideEnemyNames(boolean hideEnemyNames) {
		getHideEnemyNamesCheckBox().setSelected(hideEnemyNames);
	}
}
