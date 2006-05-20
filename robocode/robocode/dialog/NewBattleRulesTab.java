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
import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (1/18/2001 1:24:23 PM)
 * @author: Mathew A. Nelson
 */
public class NewBattleRulesTab extends javax.swing.JPanel {
	private javax.swing.JLabel gunCoolingRateLabel = null;
	private javax.swing.JTextField gunCoolingRateField = null;

	private javax.swing.JLabel inactivityTimeLabel = null;
	private javax.swing.JTextField inactivityTimeField = null;

	/**
	 * NewBattleRulesTab constructor
	 */
	public NewBattleRulesTab() {
		super();
		initialize();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 1:34:13 PM)
	 */
	public double getGunCoolingRate() {
	
		return Double.parseDouble(getGunCoolingRateField().getText());
	}

	/**
	 * Return the gunRechargeRateField
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getGunCoolingRateField() {
		if (gunCoolingRateField == null) {
			try {
				gunCoolingRateField = new javax.swing.JTextField();
				gunCoolingRateField.setName("gunCoolingRateField");
				gunCoolingRateField.setPreferredSize(new java.awt.Dimension(50, 16));
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return gunCoolingRateField;
	}

	/**
	 * Return the gunCoolingRateLabel
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getGunCoolingRateLabel() {
		if (gunCoolingRateLabel == null) {
			try {
				gunCoolingRateLabel = new javax.swing.JLabel();
				gunCoolingRateLabel.setName("gunCoolingRateLabel");
				gunCoolingRateLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
				gunCoolingRateLabel.setText("Gun Cooling Rate:");
				gunCoolingRateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				gunCoolingRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return gunCoolingRateLabel;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 1:34:13 PM)
	 */
	public long getInactivityTime() {
		return Long.parseLong(getInactivityTimeField().getText());
	}

	/**
	 * Return the inactivityTimeField
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getInactivityTimeField() {
		if (inactivityTimeField == null) {
			try {
				inactivityTimeField = new javax.swing.JTextField();
				inactivityTimeField.setName("inactivityTimeField");
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return inactivityTimeField;
	}

	/**
	 * Return the inactivityTimeLabel
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getInactivityTimeLabel() {
		if (inactivityTimeLabel == null) {
			try {
				inactivityTimeLabel = new javax.swing.JLabel();
				inactivityTimeLabel.setName("JLabel1");
				inactivityTimeLabel.setText("Inactivity Time:");
				inactivityTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return inactivityTimeLabel;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("newBattleRulesTab");
			JPanel j = new JPanel();

			j.setLayout(new java.awt.GridLayout(4, 2, 5, 5));
			j.setBorder(BorderFactory.createEtchedBorder());
			// j.setSize(10,200);
			j.add(getGunCoolingRateLabel(), getGunCoolingRateLabel().getName());
			j.add(getGunCoolingRateField(), getGunCoolingRateField().getName());
		
			j.add(getInactivityTimeLabel(), getInactivityTimeLabel().getName());
			j.add(getInactivityTimeField(), getInactivityTimeField().getName());

			add(j);
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
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 1:34:13 PM)
	 */
	public void setGunCoolingRate(double gunCoolingRate) {
		getGunCoolingRateField().setText("" + gunCoolingRate);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 1:34:13 PM)
	 */
	public void setInactivityTime(long inactivityTime) {
		getInactivityTimeField().setText("" + inactivityTime);
	}
}
