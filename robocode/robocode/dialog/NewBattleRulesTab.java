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
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.*;


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

	/**
	 * NewBattleRulesTab constructor
	 */
	public NewBattleRulesTab() {
		super();
		initialize();
	}

	public double getGunCoolingRate() {
		return Double.parseDouble(getGunCoolingRateField().getText());
	}

	/**
	 * Return the gunRechargeRateField
	 *
	 * @return JTextField
	 */
	private JTextField getGunCoolingRateField() {
		if (gunCoolingRateField == null) {
			gunCoolingRateField = new JTextField();
		}
		return gunCoolingRateField;
	}

	/**
	 * Return the gunCoolingRateLabel
	 *
	 * @return JLabel
	 */
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

	/**
	 * Return the inactivityTimeField
	 *
	 * @return JTextField
	 */
	private JTextField getInactivityTimeField() {
		if (inactivityTimeField == null) {
			inactivityTimeField = new JTextField();
		}
		return inactivityTimeField;
	}

	/**
	 * Return the inactivityTimeLabel
	 *
	 * @return JLabel
	 */
	private JLabel getInactivityTimeLabel() {
		if (inactivityTimeLabel == null) {
			inactivityTimeLabel = new JLabel();
			inactivityTimeLabel.setText("Inactivity Time:");
			inactivityTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return inactivityTimeLabel;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		JPanel j = new JPanel();

		j.setLayout(new GridLayout(4, 2, 5, 5));
		j.setBorder(BorderFactory.createEtchedBorder());
		j.add(getGunCoolingRateLabel(), getGunCoolingRateLabel().getName());
		j.add(getGunCoolingRateField(), getGunCoolingRateField().getName());
		j.add(getInactivityTimeLabel(), getInactivityTimeLabel().getName());
		j.add(getInactivityTimeField(), getInactivityTimeField().getName());
		add(j);
	}

	public void setGunCoolingRate(double gunCoolingRate) {
		getGunCoolingRateField().setText("" + gunCoolingRate);
	}

	public void setInactivityTime(long inactivityTime) {
		getInactivityTimeField().setText("" + inactivityTime);
	}
}
