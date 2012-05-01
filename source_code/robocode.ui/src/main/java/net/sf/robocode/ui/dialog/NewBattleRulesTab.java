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
	private JLabel extensionPackageLabel;
	private JTextField extensionPackageField;
	private JLabel extensionFileLabel;
	private JTextField extensionFileField;

	/**
	 * NewBattleRulesTab constructor
	 */
	public NewBattleRulesTab() {
		super();
		initialize();
	}

	private JTextField getExtensionPackageField() {
		if (extensionPackageField == null) {
			extensionPackageField = new JTextField();
		}
		return extensionPackageField;
	}
	
	private JLabel getExtensionPackageLabel() {
		if (extensionPackageLabel == null) {
			extensionPackageLabel = new JLabel();
			extensionPackageLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			extensionPackageLabel.setText("Custom rules package and class");
			extensionPackageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			extensionPackageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return extensionPackageLabel;
	}
	
	public String getExtensionPackage() {
		return extensionPackageField.getText();
	}
	
	private JTextField getExtensionFileField() {
		if (extensionFileField == null) {
			extensionFileField = new JTextField();
		}
		return extensionFileField;
	}
	
	private JLabel getExtensionFileLabel() {
		if (extensionFileLabel == null) {
			extensionFileLabel = new JLabel();
			extensionFileLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			extensionFileLabel.setText("Custom rules file");
			extensionFileLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			extensionFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return extensionFileLabel;
	}
	
	public String getExtensionFile() {
		return extensionFileField.getText();
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

		j.setLayout(new GridLayout(6, 2, 5, 5));
		j.setBorder(BorderFactory.createEtchedBorder());
		j.add(getGunCoolingRateLabel(), getGunCoolingRateLabel().getName());
		j.add(getGunCoolingRateField(), getGunCoolingRateField().getName());
		j.add(getInactivityTimeLabel(), getInactivityTimeLabel().getName());
		j.add(getInactivityTimeField(), getInactivityTimeField().getName());
		j.add(getExtensionFileField(), getExtensionFileField().getName());
		j.add(getExtensionFileLabel(), getExtensionFileLabel().getName());
		j.add(getExtensionPackageField(), getExtensionPackageField().getName());
		j.add(getExtensionPackageLabel(), getExtensionPackageLabel().getName());
		add(j);
	}

	public void setGunCoolingRate(double gunCoolingRate) {
		getGunCoolingRateField().setText("" + gunCoolingRate);
	}

	public void setInactivityTime(long inactivityTime) {
		getInactivityTimeField().setText("" + inactivityTime);
	}
}
