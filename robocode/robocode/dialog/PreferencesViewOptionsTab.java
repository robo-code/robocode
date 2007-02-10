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
 *     - Added visible ground, visible explosions, and visible explosion debris
 *       option
 *     - Changed some keyboard mnemonics
 *     - Changed from using FPS into using TPS (Turns per Second), but added a
 *       "Display FPS in titlebar" option
 *     - Added propery listener for getting TPS updated from TPS slider
 *     - Removed "Allow color changes" as this is always possible with the
 *       current rendering engine
 *     - Code cleanup
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons and other controls
 *******************************************************************************/
package robocode.dialog;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 */
@SuppressWarnings("serial")
public class PreferencesViewOptionsTab extends WizardPanel {

	private static final int MIN_TPS = 1;
	private static final int DEFAULT_TPS = 30;
	private static final int FAST_TPS = 45;
	private static final int MAX_TPS = 10000;

	private EventHandler eventHandler = new EventHandler();

	private JCheckBox visibleRobotEnergyCheckBox;
	private JCheckBox visibleRobotNameCheckBox;
	private JCheckBox visibleScanArcsCheckBox;
	private JCheckBox visibleExplosionsCheckBox;
	private JCheckBox visibleGroundCheckBox;
	private JCheckBox visibleExplosionDebrisCheckBox;

	private JTextField desiredTpsTextField;
	private JLabel desiredTpsLabel;
	private JButton defaultsButton;
	private JCheckBox displayFpsCheckBox;
	private JCheckBox displayTpsCheckBox;

	private JPanel visibleOptionsPanel;
	private JPanel tpsOptionsPanel;

	private JButton minTpsButton;
	private JButton defaultTpsButton;
	private JButton fastTpsButton;
	private JButton maxTpsButton;

	private RobocodeManager manager;

	private class EventHandler implements ActionListener, DocumentListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultsButton()) {
				defaultsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getDefaultTpsButton()) {
				defaultTpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getMinTpsButton()) {
				minTpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getFastTpsButton()) {
				fastTpsButtonActionPerformed();
			}
			if (e.getSource() == PreferencesViewOptionsTab.this.getMaxTpsButton()) {
				maxTpsButtonActionPerformed();
			}
		}

		public void changedUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredTpsTextFieldStateChanged();
		}

		public void insertUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredTpsTextFieldStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			PreferencesViewOptionsTab.this.desiredTpsTextFieldStateChanged();
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
		getVisibleExplosionsCheckBox().setSelected(true);
		getVisibleGroundCheckBox().setSelected(true);
	}

	private void desiredTpsTextFieldStateChanged() {
		fireStateChanged();
		try {
			int tps = Integer.parseInt(getDesiredTpsTextField().getText());
			String s = "" + tps;

			if (tps < MIN_TPS) {
				s = "Too low, must be at least " + MIN_TPS;
			} else if (tps > MAX_TPS) {
				s = "Too high, max is " + MAX_TPS;
			}
			getDesiredTpsLabel().setText("Desired TPS: " + s);
		} catch (NumberFormatException e) {
			getDesiredTpsLabel().setText("Desired TPS: ???");
		}
	}

	private void maxTpsButtonActionPerformed() {
		getDesiredTpsTextField().setText("" + MAX_TPS);
	}

	private void minTpsButtonActionPerformed() {
		getDesiredTpsTextField().setText("" + MIN_TPS);
	}

	private void fastTpsButtonActionPerformed() {
		getDesiredTpsTextField().setText("" + FAST_TPS);
	}

	private void defaultTpsButtonActionPerformed() {
		getDesiredTpsTextField().setText("" + DEFAULT_TPS);
	}

	/**
	 * Return the defaultsButton
	 *
	 * @return JButton
	 */
	private JButton getDefaultsButton() {
		if (defaultsButton == null) {
			defaultsButton = new JButton("Defaults");
			defaultsButton.setMnemonic('u');
			defaultsButton.setDisplayedMnemonicIndex(4);
			defaultsButton.addActionListener(eventHandler);
		}
		return defaultsButton;
	}

	/**
	 * Return the desiredTpsLabel property value.
	 *
	 * @return JLabel
	 */
	private JLabel getDesiredTpsLabel() {
		if (desiredTpsLabel == null) {
			desiredTpsLabel = new JLabel("Desired TPS: ");
		}
		return desiredTpsLabel;
	}

	/**
	 * Return the desiredTpsTextField property value.
	 *
	 * @return JTextField
	 */
	private JTextField getDesiredTpsTextField() {
		if (desiredTpsTextField == null) {
			desiredTpsTextField = new JTextField();
			desiredTpsTextField.setColumns(5);
			desiredTpsTextField.getDocument().addDocumentListener(eventHandler);
		}
		return desiredTpsTextField;
	}

	/**
	 * Return the displayFpsCheckBox
	 *
	 * @return JCheckBox
	 */
	private JCheckBox getDisplayFpsCheckBox() {
		if (displayFpsCheckBox == null) {
			displayFpsCheckBox = new JCheckBox("Display FPS in titlebar");
			displayFpsCheckBox.setMnemonic('P');
			displayFpsCheckBox.setDisplayedMnemonicIndex(9);
		}
		return displayFpsCheckBox;
	}

	/**
	 * Return the displayTpsCheckBox
	 *
	 * @return JCheckBox
	 */
	private JCheckBox getDisplayTpsCheckBox() {
		if (displayTpsCheckBox == null) {
			displayTpsCheckBox = new JCheckBox("Display TPS in titlebar");
			displayTpsCheckBox.setMnemonic('T');
			displayTpsCheckBox.setDisplayedMnemonicIndex(8);
		}
		return displayTpsCheckBox;
	}

	/**
	 * Return the maxTpsButton
	 *
	 * @return JButton
	 */
	private JButton getMaxTpsButton() {
		if (maxTpsButton == null) {
			maxTpsButton = new JButton("Max");
			maxTpsButton.setMnemonic('M');
			maxTpsButton.setDisplayedMnemonicIndex(0);
			maxTpsButton.addActionListener(eventHandler);
		}
		return maxTpsButton;
	}

	/**
	 * Return the defaultTpsButton
	 *
	 * @return JButton
	 */
	private JButton getDefaultTpsButton() {
		if (defaultTpsButton == null) {
			defaultTpsButton = new JButton("Default");
			defaultTpsButton.setMnemonic('l');
			defaultTpsButton.setDisplayedMnemonicIndex(5);
			defaultTpsButton.addActionListener(eventHandler);
		}
		return defaultTpsButton;
	}

	/**
	 * Return the minTpsButton
	 *
	 * @return JButton
	 */
	private JButton getMinTpsButton() {
		if (minTpsButton == null) {
			minTpsButton = new JButton("Minimum");
			minTpsButton.setMnemonic('i');
			minTpsButton.setDisplayedMnemonicIndex(1);
			minTpsButton.addActionListener(eventHandler);
		}
		return minTpsButton;
	}

	/**
	 * Return the fastTpsButton
	 *
	 * @return JButton
	 */
	private JButton getFastTpsButton() {
		if (fastTpsButton == null) {
			fastTpsButton = new JButton("Fast");
			fastTpsButton.setMnemonic('a');
			fastTpsButton.setDisplayedMnemonicIndex(1);
			fastTpsButton.addActionListener(eventHandler);
		}
		return fastTpsButton;
	}

	/**
	 * Return the tpsOptionsPanel
	 *
	 * @return JPanel
	 */
	private JPanel getTpsOptionsPanel() {
		if (tpsOptionsPanel == null) {
			tpsOptionsPanel = new JPanel();
			tpsOptionsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Turns Per Second (TPS)"));

			tpsOptionsPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.fill = 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.NORTHWEST;

			c.gridwidth = GridBagConstraints.REMAINDER;
			tpsOptionsPanel.add(getDisplayTpsCheckBox(), c);
			tpsOptionsPanel.add(getDisplayFpsCheckBox(), c);

			JLabel label = new JLabel(" ");

			tpsOptionsPanel.add(label, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			tpsOptionsPanel.add(getDesiredTpsLabel(), c);
			getDesiredTpsLabel().setHorizontalAlignment(SwingConstants.CENTER);

			JPanel p = new JPanel();
			JPanel q = new JPanel();

			q.setLayout(new GridLayout(1, 3));

			p.add(q);

			p.add(getDesiredTpsTextField());
			q = new JPanel();
			p.add(q);

			c.gridwidth = GridBagConstraints.REMAINDER;
			tpsOptionsPanel.add(p, c);
			JLabel label2 = new JLabel(" ");

			tpsOptionsPanel.add(label2, c);
			c.gridwidth = 1;
			c.fill = 0;
			c.weighty = 1;
			c.weightx = 0;
			tpsOptionsPanel.add(getMinTpsButton(), c);
			tpsOptionsPanel.add(getDefaultTpsButton(), c);
			tpsOptionsPanel.add(getFastTpsButton(), c);
			c.weightx = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			tpsOptionsPanel.add(getMaxTpsButton(), c);
		}
		return tpsOptionsPanel;
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
			visibleOptionsPanel.add(getVisibleExplosionsCheckBox());
			visibleOptionsPanel.add(getVisibleGroundCheckBox());
			visibleOptionsPanel.add(getVisibleExplosionDebrisCheckBox());
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
			visibleRobotEnergyCheckBox = new JCheckBox("Visible Robot Energy");
			visibleRobotEnergyCheckBox.setMnemonic('y');
			visibleRobotEnergyCheckBox.setDisplayedMnemonicIndex(19);
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
			visibleRobotNameCheckBox = new JCheckBox("Visible Robot Name");
			visibleRobotNameCheckBox.setMnemonic('V');
			visibleRobotNameCheckBox.setDisplayedMnemonicIndex(0);
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
			visibleScanArcsCheckBox = new JCheckBox("Visible Scan Arcs");
			visibleScanArcsCheckBox.setMnemonic('b');
			visibleScanArcsCheckBox.setDisplayedMnemonicIndex(4);
		}
		return visibleScanArcsCheckBox;
	}

	/**
	 * Return the visibleExplosionsCheckBox
	 *
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleExplosionsCheckBox() {
		if (visibleExplosionsCheckBox == null) {
			visibleExplosionsCheckBox = new JCheckBox("Visible Explosions");
			visibleExplosionsCheckBox.setMnemonic('x');
			visibleExplosionsCheckBox.setDisplayedMnemonicIndex(9);
		}
		return visibleExplosionsCheckBox;
	}

	/**
	 * Return the visibleGroundCheckBox
	 *
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleGroundCheckBox() {
		if (visibleGroundCheckBox == null) {
			visibleGroundCheckBox = new JCheckBox("Visible Ground");
			visibleGroundCheckBox.setMnemonic('G');
			visibleGroundCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleGroundCheckBox;
	}

	/**
	 * Return the visibleExplosionDebrisCheckBox
	 *
	 * @return JCheckBox
	 */
	private JCheckBox getVisibleExplosionDebrisCheckBox() {
		if (visibleExplosionDebrisCheckBox == null) {
			visibleExplosionDebrisCheckBox = new JCheckBox("Visible Explosion Debris");
			visibleExplosionDebrisCheckBox.setMnemonic('E');
			visibleExplosionDebrisCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleExplosionDebrisCheckBox;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getVisibleOptionsPanel());
		add(getTpsOptionsPanel());

		RobocodeProperties props = manager.getProperties();

		loadPreferences(props);

		props.addPropertyListener(props.new PropertyListener() {
			@Override
			public void desiredTpsChanged(int tps) {
				PreferencesViewOptionsTab.this.desiredTpsTextField.setText("" + tps);
			}
			;
		});
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getDisplayFpsCheckBox().setSelected(robocodeProperties.getOptionsViewFPS());
		getDisplayTpsCheckBox().setSelected(robocodeProperties.getOptionsViewTPS());
		getVisibleRobotNameCheckBox().setSelected(robocodeProperties.getOptionsViewRobotNames());
		getVisibleRobotEnergyCheckBox().setSelected(robocodeProperties.getOptionsViewRobotEnergy());
		getVisibleScanArcsCheckBox().setSelected(robocodeProperties.getOptionsViewScanArcs());
		getVisibleExplosionsCheckBox().setSelected(robocodeProperties.getOptionsViewExplosions());
		getVisibleGroundCheckBox().setSelected(robocodeProperties.getOptionsViewGround());
		getVisibleExplosionDebrisCheckBox().setSelected(robocodeProperties.getOptionsViewExplosionDebris());
		getDesiredTpsTextField().setText("" + robocodeProperties.getOptionsBattleDesiredTPS());
	}

	public void storePreferences() {
		RobocodeProperties props = manager.getProperties();

		props.setOptionsViewFPS(getDisplayFpsCheckBox().isSelected());
		props.setOptionsViewTPS(getDisplayTpsCheckBox().isSelected());
		props.setOptionsViewRobotNames(getVisibleRobotNameCheckBox().isSelected());
		props.setOptionsViewRobotEnergy(getVisibleRobotEnergyCheckBox().isSelected());
		props.setOptionsViewScanArcs(getVisibleScanArcsCheckBox().isSelected());
		props.setOptionsViewExplosions(getVisibleExplosionsCheckBox().isSelected());
		props.setOptionsViewGround(getVisibleGroundCheckBox().isSelected());
		props.setOptionsViewExplosionDebris(getVisibleExplosionDebrisCheckBox().isSelected());
		props.setOptionsBattleDesiredTPS(Integer.parseInt(getDesiredTpsTextField().getText()));
		manager.saveProperties();
	}

	@Override
	public boolean isReady() {
		try {
			int tps = Integer.parseInt(getDesiredTpsTextField().getText());

			if (tps < MIN_TPS) {
				return false;
			} else if (tps > MAX_TPS) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
