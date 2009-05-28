/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
	private JRadioButton apiExtensabilityRadioButton;
	private JLabel apiExtensabilityLabel;

	/**
	 * NewBattleRulesTab constructor
	 */
	public NewBattleRulesTab() {
		super();
		initialize();
	}

	private JRadioButton getApiExtensibilityRadioButton()
	{
		if (apiExtensabilityRadioButton == null){
			apiExtensabilityRadioButton = new JRadioButton();
		}
		return apiExtensabilityRadioButton;
	}
	
	private JLabel getApiExtensabilityLabel()
	{
		if (apiExtensabilityLabel == null) {
			apiExtensabilityLabel = new JLabel();
			apiExtensabilityLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			apiExtensabilityLabel.setText("Use API EX");
			apiExtensabilityLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			apiExtensabilityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return apiExtensabilityLabel;
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
		j.add(getApiExtensibilityRadioButton(), getApiExtensibilityRadioButton().getName());
		j.add(getApiExtensabilityLabel(), getApiExtensabilityLabel().getName());
		add(j);
	}

	public void setGunCoolingRate(double gunCoolingRate) {
		getGunCoolingRateField().setText("" + gunCoolingRate);
	}

	public void setInactivityTime(long inactivityTime) {
		getInactivityTimeField().setText("" + inactivityTime);
	}
}
