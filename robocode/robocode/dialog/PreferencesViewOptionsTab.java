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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons and other controls
 *     Flemming N. Larsen
 *     - Added visible ground option
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class PreferencesViewOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();

	private JCheckBox visibleRobotEnergyCheckBox;
	private JCheckBox visibleRobotNameCheckBox;
	private JCheckBox visibleScanArcsCheckBox;
	private JCheckBox visibleGroundCheckBox;

	private JTextField desiredFpsTextField;
	private JLabel desiredFpsLabel;
	private JButton defaultsButton;
	private JCheckBox displayFpsCheckBox;
	private JCheckBox optionsBattleAllowColorChangesCheckBox;
	
	private JPanel visibleOptionsPanel;
	private JPanel fpsOptionsPanel;

	private JButton minFpsButton;
	private JButton defaultFpsButton;
	private JButton fastFpsButton;
	private JButton maxFpsButton;
	
	private int MINFPS = 1;
	private int DEFAULTFPS = 30;
	private int FASTFPS = 45;
	private int MAXFPS = 10000;	

	public RobocodeManager manager;
	
	class EventHandler implements ActionListener, DocumentListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultsButton()) {
				defaultsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultFpsButton()) {
				defaultFpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getMinFpsButton()) {
				minFpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getFastFpsButton()) {
				fastFpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getMaxFpsButton()) {
				maxFpsButtonActionPerformed();
			}
		}

		public void changedUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}

		public void insertUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredFpsTextFieldStateChanged();
		}
	}

	/**
	 * PreferencesDialog constructor
	 */
	public PreferencesViewOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	private void defaultsButtonActionPerformed() {
		getVisibleRobotEnergyCheckBox().setSelected(true);
		getVisibleRobotNameCheckBox().setSelected(true);
		getVisibleScanArcsCheckBox().setSelected(false);
		getVisibleGroundCheckBox().setSelected(true);
	}

	private void desiredFpsTextFieldStateChanged() {
		fireStateChanged();
		try {
			int fps = Integer.parseInt(getDesiredFpsTextField().getText());
			String s = "" + fps;

			if (fps < MINFPS) {
				s = "Too low, must be at least " + MINFPS;
			} else if (fps > MAXFPS) {
				s = "Too high, max is " + MAXFPS;
			}
			getDesiredFpsLabel().setText("Desired FPS: " + s);
		} catch (NumberFormatException e) {
			getDesiredFpsLabel().setText("Desired FPS: ???");
		}
	}

	private void maxFpsButtonActionPerformed() {
		getDesiredFpsTextField().setText("" + MAXFPS);
	}

	private void minFpsButtonActionPerformed() {
		getDesiredFpsTextField().setText("" + MINFPS);
	}

	private void fastFpsButtonActionPerformed() {
		getDesiredFpsTextField().setText("" + FASTFPS);
	}

	private void defaultFpsButtonActionPerformed() {
		getDesiredFpsTextField().setText("" + DEFAULTFPS);
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
			defaultsButton.setMnemonic('u');
			defaultsButton.setDisplayedMnemonicIndex(4);
			defaultsButton.addActionListener(eventHandler);
		}
		return defaultsButton;
	}

	/**
	 * Return the desiredFpsLabel property value.
	 * 
	 * @return JLabel
	 */
	private JLabel getDesiredFpsLabel() {
		if (desiredFpsLabel == null) {
			desiredFpsLabel = new JLabel();
			desiredFpsLabel.setText("Desired FPS: ");
		}
		return desiredFpsLabel;
	}

	/**
	 * Return the fpsSlider property value.
	 * 
	 * @return JSlider
	 */
	private JTextField getDesiredFpsTextField() {
		if (desiredFpsTextField == null) {
			desiredFpsTextField = new JTextField();
			desiredFpsTextField.setColumns(5);
			desiredFpsTextField.getDocument().addDocumentListener(eventHandler);
		}
		return desiredFpsTextField;
	}

	/**
	 * Return the displayFpsCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getDisplayFpsCheckBox() {
		if (displayFpsCheckBox == null) {
			displayFpsCheckBox = new JCheckBox();
			displayFpsCheckBox.setText("Display FPS in titlebar");
			displayFpsCheckBox.setMnemonic('P');
			displayFpsCheckBox.setDisplayedMnemonicIndex(9);
		}
		return displayFpsCheckBox;
	}

	/**
	 * Return the displayFpsCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getOptionsBattleAllowColorChangesCheckBox() {
		if (optionsBattleAllowColorChangesCheckBox == null) {
			optionsBattleAllowColorChangesCheckBox = new JCheckBox();
			optionsBattleAllowColorChangesCheckBox.setText(
					"Allow robots to change colors repeatedly (Slow, not recommended)");
			optionsBattleAllowColorChangesCheckBox.setMnemonic('r');
			optionsBattleAllowColorChangesCheckBox.setDisplayedMnemonicIndex(6);
		}
		return optionsBattleAllowColorChangesCheckBox;
	}

	/**
	 * Return the maxFpsButton
	 * 
	 * @return JButton
	 */
	private JButton getMaxFpsButton() {
		if (maxFpsButton == null) {
			maxFpsButton = new JButton();
			maxFpsButton.setText("Max FPS");
			maxFpsButton.setMnemonic('x');
			maxFpsButton.setDisplayedMnemonicIndex(2);
			maxFpsButton.addActionListener(eventHandler);
		}
		return maxFpsButton;
	}

	/**
	 * Return the defaultFpsButton
	 * 
	 * @return JButton
	 */
	private JButton getDefaultFpsButton() {
		if (defaultFpsButton == null) {
			defaultFpsButton = new JButton();
			defaultFpsButton.setText("Default");
			defaultFpsButton.setMnemonic('l');
			defaultFpsButton.setDisplayedMnemonicIndex(5);
			defaultFpsButton.addActionListener(eventHandler);
		}
		return defaultFpsButton;
	}

	/**
	 * Return the minFpsButton
	 * 
	 * @return JButton
	 */
	private JButton getMinFpsButton() {
		if (minFpsButton == null) {
			minFpsButton = new JButton();
			minFpsButton.setText("Minimum");
			minFpsButton.setMnemonic('M');
			minFpsButton.setDisplayedMnemonicIndex(0);
			minFpsButton.addActionListener(eventHandler);
		}
		return minFpsButton;
	}

	/**
	 * Return the fastFpsButton
	 * 
	 * @return JButton
	 */
	private JButton getFastFpsButton() {
		if (fastFpsButton == null) {
			fastFpsButton = new JButton();
			fastFpsButton.setText("Fast");
			fastFpsButton.setMnemonic('a');
			fastFpsButton.setDisplayedMnemonicIndex(1);
			fastFpsButton.addActionListener(eventHandler);
		}
		return fastFpsButton;
	}

	/**
	 * Return the fpsOptionsPanel
	 * 
	 * @return JPanel
	 */
	private JPanel getFpsOptionsPanel() {
		if (fpsOptionsPanel == null) {
			fpsOptionsPanel = new JPanel();
			fpsOptionsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Frames Per Second"));
			GridBagLayout layout = new GridBagLayout();

			fpsOptionsPanel.setLayout(layout);
			GridBagConstraints constraints = new GridBagConstraints();

			constraints.fill = 1;
			constraints.weightx = 1;
			constraints.anchor = GridBagConstraints.NORTHWEST;

			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getDisplayFpsCheckBox(), constraints);
			fpsOptionsPanel.add(getDisplayFpsCheckBox());

			JLabel label = new JLabel(" ");

			layout.setConstraints(label, constraints);
			fpsOptionsPanel.add(label);
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getDesiredFpsLabel(), constraints);
			fpsOptionsPanel.add(getDesiredFpsLabel());
			getDesiredFpsLabel().setHorizontalAlignment(JLabel.CENTER);

			JPanel p = new JPanel();
			JPanel q = new JPanel();

			q.setLayout(new GridLayout(1, 3));

			p.add(q);

			p.add(getDesiredFpsTextField());
			q = new JPanel();
			p.add(q);

			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(p, constraints);
			fpsOptionsPanel.add(p);
			JLabel label2 = new JLabel(" ");

			layout.setConstraints(label2, constraints);
			fpsOptionsPanel.add(label2);
			constraints.gridwidth = 1;
			constraints.fill = 0;
			constraints.weighty = 1;
			constraints.weightx = 0;
			layout.setConstraints(getMinFpsButton(), constraints);
			fpsOptionsPanel.add(getMinFpsButton());
			layout.setConstraints(getDefaultFpsButton(), constraints);
			fpsOptionsPanel.add(getDefaultFpsButton());
			layout.setConstraints(getFastFpsButton(), constraints);
			fpsOptionsPanel.add(getFastFpsButton());
			constraints.weightx = 1;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(getMaxFpsButton(), constraints);
			fpsOptionsPanel.add(getMaxFpsButton());
		}
		return fpsOptionsPanel;
	}

	/**
	 * Return the displayOptionsPanel
	 * 
	 * @return JPanel
	 */
	private JPanel getVisibleOptionsPanel() {
		if (visibleOptionsPanel == null) {
			visibleOptionsPanel = new JPanel();
			visibleOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Arena"));
			visibleOptionsPanel.setLayout(new BoxLayout(visibleOptionsPanel, BoxLayout.Y_AXIS));
			visibleOptionsPanel.add(getVisibleRobotEnergyCheckBox());
			visibleOptionsPanel.add(getVisibleRobotNameCheckBox());
			visibleOptionsPanel.add(getVisibleScanArcsCheckBox());
			visibleOptionsPanel.add(getVisibleGroundCheckBox());
			visibleOptionsPanel.add(getOptionsBattleAllowColorChangesCheckBox());
			visibleOptionsPanel.add(new JLabel(" "));
			visibleOptionsPanel.add(getDefaultsButton());
		}
		return visibleOptionsPanel;
	}

	/**
	 * Return the visibleRobotEnergyCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleRobotEnergyCheckBox() {
		if (visibleRobotEnergyCheckBox == null) {
			visibleRobotEnergyCheckBox = new JCheckBox();
			visibleRobotEnergyCheckBox.setText("Visible Robot Energy");
			visibleRobotEnergyCheckBox.setMnemonic('E');
			visibleRobotEnergyCheckBox.setDisplayedMnemonicIndex(14);
		}
		return visibleRobotEnergyCheckBox;
	}

	/**
	 * Return the visibleRobotNameCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleRobotNameCheckBox() {
		if (visibleRobotNameCheckBox == null) {
			visibleRobotNameCheckBox = new JCheckBox();
			visibleRobotNameCheckBox.setText("Visible Robot Name");
			visibleRobotNameCheckBox.setMnemonic('o');
			visibleRobotNameCheckBox.setDisplayedMnemonicIndex(9);
		}
		return visibleRobotNameCheckBox;
	}

	/**
	 * Return the visibleScanArcsCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleScanArcsCheckBox() {
		if (visibleScanArcsCheckBox == null) {
			visibleScanArcsCheckBox = new JCheckBox();
			visibleScanArcsCheckBox.setText("Visible Scan Arcs (Cool, but may slow down game)");
			visibleScanArcsCheckBox.setMnemonic('S');
			visibleScanArcsCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleScanArcsCheckBox;
	}

	/**
	 * Return the visibleGroundCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleGroundCheckBox() {
		if (visibleGroundCheckBox == null) {
			visibleGroundCheckBox = new JCheckBox();
			visibleGroundCheckBox.setText("Visible Ground");
			visibleGroundCheckBox.setMnemonic('G');
			visibleGroundCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleGroundCheckBox;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getVisibleOptionsPanel());
		add(getFpsOptionsPanel());
		loadPreferences(manager.getProperties());
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getDisplayFpsCheckBox().setSelected(robocodeProperties.getOptionsViewFps());
		getVisibleRobotNameCheckBox().setSelected(robocodeProperties.getOptionsViewRobotNames());
		getVisibleRobotEnergyCheckBox().setSelected(robocodeProperties.getOptionsViewRobotEnergy());
		getVisibleScanArcsCheckBox().setSelected(robocodeProperties.getOptionsViewScanArcs());
		getVisibleGroundCheckBox().setSelected(robocodeProperties.getOptionsViewGround());
		getDesiredFpsTextField().setText("" + robocodeProperties.getOptionsBattleDesiredFps());
		getOptionsBattleAllowColorChangesCheckBox().setSelected(robocodeProperties.getOptionsBattleAllowColorChanges());
	}

	public void storePreferences() {
		manager.getProperties().setOptionsViewRobotNames(getVisibleRobotNameCheckBox().isSelected());
		manager.getProperties().setOptionsViewRobotEnergy(getVisibleRobotEnergyCheckBox().isSelected());
		manager.getProperties().setOptionsViewFps(getDisplayFpsCheckBox().isSelected());
		manager.getProperties().setOptionsViewScanArcs(getVisibleScanArcsCheckBox().isSelected());
		manager.getProperties().setOptionsViewGround(getVisibleGroundCheckBox().isSelected());
		manager.getProperties().setOptionsBattleDesiredFps(Integer.parseInt(getDesiredFpsTextField().getText()));
		manager.getProperties().setOptionsBattleAllowColorChanges(
				getOptionsBattleAllowColorChangesCheckBox().isSelected());
		manager.saveProperties();
	}

	public boolean isReady() {
		try {
			int fps = Integer.parseInt(getDesiredFpsTextField().getText());

			if (fps < MINFPS) {
				return false;
			} else if (fps > MAXFPS) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
