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
import javax.swing.event.*;
import java.awt.*;
import robocode.manager.*;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2001 11:07:52 PM)
 * @author: Mathew A. Nelson
 */
public class PreferencesViewOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();
	private javax.swing.JCheckBox visibleRobotEnergyCheckBox = null;
	private javax.swing.JCheckBox visibleRobotNameCheckBox = null;
	private javax.swing.JCheckBox visibleScanArcsCheckBox = null;

	private javax.swing.JTextField desiredFpsTextField = null;
	private javax.swing.JLabel desiredFpsLabel = null;
	private javax.swing.JButton defaultsButton = null;
	private javax.swing.JCheckBox displayFpsCheckBox = null;
	private javax.swing.JCheckBox optionsBattleAllowColorChangesCheckBox = null;
	
	private javax.swing.JPanel visibleOptionsPanel = null;
	private javax.swing.JPanel fpsOptionsPanel = null;

	private javax.swing.JButton minFpsButton = null;
	private javax.swing.JButton defaultFpsButton = null;
	private javax.swing.JButton fastFpsButton = null;
	private javax.swing.JButton maxFpsButton = null;
	
	private int MINFPS = 1;
	private int DEFAULTFPS = 30;
	private int FASTFPS = 45;
	private int MAXFPS = 10000;	

	public RobocodeManager manager = null;
	
class EventHandler implements java.awt.event.ActionListener, javax.swing.event.DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultsButton())
				defaultsButtonActionPerformed();
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultFpsButton())
				defaultFpsButtonActionPerformed();
			if (e.getSource() == PreferencesViewOptionsTab.this.getMinFpsButton())
				minFpsButtonActionPerformed();
			if (e.getSource() == PreferencesViewOptionsTab.this.getFastFpsButton())
				fastFpsButtonActionPerformed();
			if (e.getSource() == PreferencesViewOptionsTab.this.getMaxFpsButton())
				maxFpsButtonActionPerformed();
		};
		public void changedUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}
		public void insertUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}
		public void removeUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}
	};
/**
 * PreferencesDialog constructor
 */
public PreferencesViewOptionsTab(RobocodeManager manager)
{
	super();
	this.manager = manager;
	initialize();
}
/**
 * Comment
 */
private void defaultsButtonActionPerformed() {
	getVisibleRobotEnergyCheckBox().setSelected(true);
	getVisibleRobotNameCheckBox().setSelected(true);
	getVisibleScanArcsCheckBox().setSelected(false);
	return;
}
/**
 * Comment
 */
private void desiredFpsTextFieldStateChanged() {
	fireStateChanged();
	try {
		int fps = Integer.parseInt(getDesiredFpsTextField().getText());
		String s = "" + fps;
		if (fps < MINFPS)
			s = "Too low, must be at least " + MINFPS;
		else if (fps > MAXFPS)
			s = "Too high, max is " + MAXFPS;
		getDesiredFpsLabel().setText("Desired FPS: " + s);
	} catch (NumberFormatException e) {
		getDesiredFpsLabel().setText("Desired FPS: ???");
	}
	return;
}

private void maxFpsButtonActionPerformed() {
	getDesiredFpsTextField().setText(""+MAXFPS);
	return;
}
private void minFpsButtonActionPerformed() {
	getDesiredFpsTextField().setText(""+MINFPS);
	return;
}
private void fastFpsButtonActionPerformed() {
	getDesiredFpsTextField().setText(""+FASTFPS);
	return;
}
/**
 * Comment
 */
private void defaultFpsButtonActionPerformed() {
	getDesiredFpsTextField().setText(""+DEFAULTFPS);
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
/**
 * Return the desiredFpsLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDesiredFpsLabel() {
	if (desiredFpsLabel == null) {
		try {
			desiredFpsLabel = new javax.swing.JLabel();
			desiredFpsLabel.setName("desiredFpsLabel");
			desiredFpsLabel.setText("Desired FPS: ");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return desiredFpsLabel;
}
/**
 * Return the fpsSlider property value.
 * @return javax.swing.JSlider
 */
private javax.swing.JTextField getDesiredFpsTextField() {
	if (desiredFpsTextField == null) {
		try {
			desiredFpsTextField = new javax.swing.JTextField();
			desiredFpsTextField.setName("desiredFpsTextField");
			desiredFpsTextField.setColumns(5);
			desiredFpsTextField.getDocument().addDocumentListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return desiredFpsTextField;
}
/**
 * Return the displayFpsCheckBox
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getDisplayFpsCheckBox() {
	if (displayFpsCheckBox == null) {
		try {
			displayFpsCheckBox = new javax.swing.JCheckBox();
			displayFpsCheckBox.setName("displayFpsCheckBox");
			displayFpsCheckBox.setText("Display FPS in titlebar");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return displayFpsCheckBox;
}
/**
 * Return the displayFpsCheckBox
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getOptionsBattleAllowColorChangesCheckBox() {
	if (optionsBattleAllowColorChangesCheckBox == null) {
		try {
			optionsBattleAllowColorChangesCheckBox = new javax.swing.JCheckBox();
			optionsBattleAllowColorChangesCheckBox.setName("optionsBattleAllowColorChangesCheckBox");
			optionsBattleAllowColorChangesCheckBox.setText("Allow robots to change colors repeatedly (Slow, not recommended)");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsBattleAllowColorChangesCheckBox;
}
/**
 * Return the maxFpsButton
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMaxFpsButton() {
	if (maxFpsButton == null) {
		try {
			maxFpsButton = new javax.swing.JButton();
			maxFpsButton.setName("maxFpsButton");
			maxFpsButton.setText("Max FPS");
			maxFpsButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return maxFpsButton;
}

/**
 * Return the defaultFpsButton
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDefaultFpsButton() {
	if (defaultFpsButton == null) {
		try {
			defaultFpsButton = new javax.swing.JButton();
			defaultFpsButton.setName("defaultFpsButton");
			defaultFpsButton.setText("Default");
			defaultFpsButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return defaultFpsButton;
}

/**
 * Return the minFpsButton
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMinFpsButton() {
	if (minFpsButton == null) {
		try {
			minFpsButton = new javax.swing.JButton();
			minFpsButton.setName("minFpsButton");
			minFpsButton.setText("Minimum");
			minFpsButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return minFpsButton;
}
/**
 * Return the fastFpsButton
 * @return javax.swing.JButton
 */
private javax.swing.JButton getFastFpsButton() {
	if (fastFpsButton == null) {
		try {
			fastFpsButton = new javax.swing.JButton();
			fastFpsButton.setName("fastFpsButton");
			fastFpsButton.setText("Fast");
			fastFpsButton.addActionListener(eventHandler);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return fastFpsButton;
}
/**
 * Return the fpsOptionsPanel
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getFpsOptionsPanel() {
	if (fpsOptionsPanel == null) {
		try {
			fpsOptionsPanel = new javax.swing.JPanel();
			fpsOptionsPanel.setName("fpsOptionsPanel");
			fpsOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Frames Per Second"));

			GridBagLayout layout = new GridBagLayout();
			fpsOptionsPanel.setLayout(layout);

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill=1;
			constraints.weightx = 1;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			
//			fpsOptionsPanel.setLayout(new BoxLayout(fpsOptionsPanel,BoxLayout.Y_AXIS));

			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getDisplayFpsCheckBox(),constraints);
			fpsOptionsPanel.add(getDisplayFpsCheckBox());


			JLabel label = new JLabel(" ");
			layout.setConstraints(label,constraints);
			fpsOptionsPanel.add(label);

			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getDesiredFpsLabel(),constraints);
			fpsOptionsPanel.add(getDesiredFpsLabel());
//			getDesiredFpsLabel().setAlignmentX(JLabel.CENTER_ALIGNMENT);
			getDesiredFpsLabel().setHorizontalAlignment(JLabel.CENTER);
//			getDesiredFpsLabel().setHorizontalTextPosition(JLabel.CENTER);

			JPanel p = new JPanel();
			JPanel q = new JPanel();
			q.setLayout(new GridLayout(1,3));
			
			p.add(q);
			
			p.add(getDesiredFpsTextField());

			q = new JPanel();
			p.add(q);
			
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(p,constraints);
			fpsOptionsPanel.add(p);

			JLabel label2 = new JLabel(" ");
			layout.setConstraints(label2,constraints);
			fpsOptionsPanel.add(label2);

			constraints.gridwidth = 1;
			constraints.fill = 0;
			constraints.weighty = 1;
			constraints.weightx = 0;
			layout.setConstraints(getMinFpsButton(),constraints);
			fpsOptionsPanel.add(getMinFpsButton());

			layout.setConstraints(getDefaultFpsButton(),constraints);
			fpsOptionsPanel.add(getDefaultFpsButton());

			layout.setConstraints(getFastFpsButton(),constraints);
			fpsOptionsPanel.add(getFastFpsButton());

			constraints.weightx = 1;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getMaxFpsButton(),constraints);
			fpsOptionsPanel.add(getMaxFpsButton());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return fpsOptionsPanel;
}
/**
 * Return the displayOptionsPanel
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getVisibleOptionsPanel() {
	if (visibleOptionsPanel == null) {
		try {
			visibleOptionsPanel = new javax.swing.JPanel();
			visibleOptionsPanel.setName("displayOptionsPanel");
			visibleOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Arena"));
			//BorderFactory.createEtchedBorder());
			visibleOptionsPanel.setLayout(new BoxLayout(visibleOptionsPanel,BoxLayout.Y_AXIS));
			visibleOptionsPanel.add(getVisibleRobotEnergyCheckBox());
			visibleOptionsPanel.add(getVisibleRobotNameCheckBox());
			visibleOptionsPanel.add(getVisibleScanArcsCheckBox());
			visibleOptionsPanel.add(getOptionsBattleAllowColorChangesCheckBox());
			visibleOptionsPanel.add(new JLabel(" "));
			visibleOptionsPanel.add(getDefaultsButton());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return visibleOptionsPanel;
}
/**
 * Return the visibleRobotEnergyCheckBox
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getVisibleRobotEnergyCheckBox() {
	if (visibleRobotEnergyCheckBox == null) {
		try {
			visibleRobotEnergyCheckBox = new javax.swing.JCheckBox();
			visibleRobotEnergyCheckBox.setName("visibleRobotEnergyCheckBox");
			visibleRobotEnergyCheckBox.setText("Visible Robot Energy");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return visibleRobotEnergyCheckBox;
}
/**
 * Return the visibleRobotNameCheckBox
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getVisibleRobotNameCheckBox() {
	if (visibleRobotNameCheckBox == null) {
		try {
			visibleRobotNameCheckBox = new javax.swing.JCheckBox();
			visibleRobotNameCheckBox.setName("visibleRobotNameCheckBox");
			visibleRobotNameCheckBox.setText("Visible Robot Name");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return visibleRobotNameCheckBox;
}
/**
 * Return the visibleScanArcsCheckBox
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getVisibleScanArcsCheckBox() {
	if (visibleScanArcsCheckBox == null) {
		try {
			visibleScanArcsCheckBox = new javax.swing.JCheckBox();
			visibleScanArcsCheckBox.setName("visibleScanArcsCheckBox");
			visibleScanArcsCheckBox.setText("Visible Scan Arcs (Cool, but may slow down game)");
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return visibleScanArcsCheckBox;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
//		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setLayout(new GridLayout(1,2));
		add(getVisibleOptionsPanel());
		add(getFpsOptionsPanel());
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
	getDisplayFpsCheckBox().setSelected(robocodeProperties.getOptionsViewFps());
	getVisibleRobotNameCheckBox().setSelected(robocodeProperties.getOptionsViewRobotNames());
	getVisibleRobotEnergyCheckBox().setSelected(robocodeProperties.getOptionsViewRobotEnergy());
	getVisibleScanArcsCheckBox().setSelected(robocodeProperties.getOptionsViewScanArcs());
	getDesiredFpsTextField().setText(""+robocodeProperties.getOptionsBattleDesiredFps());
	getOptionsBattleAllowColorChangesCheckBox().setSelected(robocodeProperties.getOptionsBattleAllowColorChanges());
}

/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 11:16:59 AM)
 * @param props java.util.Properties
 */
public void storePreferences() {
	manager.getProperties().setOptionsViewRobotNames(getVisibleRobotNameCheckBox().isSelected());
	manager.getProperties().setOptionsViewRobotEnergy(getVisibleRobotEnergyCheckBox().isSelected());
	manager.getProperties().setOptionsViewFps(getDisplayFpsCheckBox().isSelected());
	manager.getProperties().setOptionsViewScanArcs(getVisibleScanArcsCheckBox().isSelected());
	manager.getProperties().setOptionsBattleDesiredFps(Integer.parseInt(getDesiredFpsTextField().getText()));
	manager.getProperties().setOptionsBattleAllowColorChanges(getOptionsBattleAllowColorChangesCheckBox().isSelected());
	manager.saveProperties();
}

public boolean isReady()
{
	try {
		int fps = Integer.parseInt(getDesiredFpsTextField().getText());
		if (fps < MINFPS)
			return false;
		else if (fps > MAXFPS)
			return false;
	} catch (Exception e) {
		return false;
	}
	return true;	
}
}
