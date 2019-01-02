/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.settings.ISettingsManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

	private final EventHandler eventHandler = new EventHandler();

	private JCheckBox visibleRobotEnergyCheckBox;
	private JCheckBox visibleRobotNameCheckBox;
	private JCheckBox visibleScanArcsCheckBox;
	private JCheckBox visibleExplosionsCheckBox;
	private JCheckBox visibleGroundCheckBox;
	private JCheckBox visibleExplosionDebrisCheckBox;
	private JCheckBox visibleSentryBorderCheckBox;

	private JButton defaultViewOptionsButton;
	private JButton enableAllViewOptionsButton;
	private JButton disableAllViewOptionsButton;

	private JTextField desiredTpsTextField;
	private JLabel desiredTpsLabel;
	private JCheckBox displayFpsCheckBox;
	private JCheckBox displayTpsCheckBox;

	private JPanel visibleOptionsPanel;
	private JPanel tpsOptionsPanel;

	private JButton minTpsButton;
	private JButton defaultTpsButton;
	private JButton fastTpsButton;
	private JButton maxTpsButton;

	private JCheckBox preventSpeedupWhenMinimizedCheckBox;

	private final ISettingsManager properties;

	private class EventHandler implements ActionListener, DocumentListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == enableAllViewOptionsButton) {
				setAllViewOptionsButtonsEnabled(true);
			} else if (src == disableAllViewOptionsButton) {
				setAllViewOptionsButtonsEnabled(false);
			} else if (src == defaultViewOptionsButton) {
				defaultViewOptionsButtonActionPerformed();
			} else if (src == defaultTpsButton) {
				defaultTpsButtonActionPerformed();
			} else if (src == minTpsButton) {
				minTpsButtonActionPerformed();
			} else if (src == fastTpsButton) {
				fastTpsButtonActionPerformed();
			} else if (src == maxTpsButton) {
				maxTpsButtonActionPerformed();
			}
		}

		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}
	}

	public PreferencesViewOptionsTab(ISettingsManager properties) {
		super();
		this.properties = properties;
		initialize();
	}

	private void defaultViewOptionsButtonActionPerformed() {
		setAllViewOptionsButtonsEnabled(true);
		getVisibleScanArcsCheckBox().setSelected(false);
	}

	private void setAllViewOptionsButtonsEnabled(boolean enabled) {
		getVisibleRobotEnergyCheckBox().setSelected(enabled);
		getVisibleRobotNameCheckBox().setSelected(enabled);
		getVisibleScanArcsCheckBox().setSelected(enabled);
		getVisibleExplosionsCheckBox().setSelected(enabled);
		getVisibleGroundCheckBox().setSelected(enabled);
		getVisibleExplosionDebrisCheckBox().setSelected(enabled);
		getVisibleSentryBorderCheckBox().setSelected(enabled);
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
	
	private JButton getDefaultViewOptionsButton() {
		if (defaultViewOptionsButton == null) {
			defaultViewOptionsButton = new JButton("Defaults");
			defaultViewOptionsButton.setMnemonic('u');
			defaultViewOptionsButton.setDisplayedMnemonicIndex(4);
			defaultViewOptionsButton.addActionListener(eventHandler);
		}
		return defaultViewOptionsButton;
	}

	private JButton getEnableAllViewOptionsButton() {
		if (enableAllViewOptionsButton == null) {
			enableAllViewOptionsButton = new JButton("Enable all");
			enableAllViewOptionsButton.setMnemonic('a');
			enableAllViewOptionsButton.setDisplayedMnemonicIndex(7);
			enableAllViewOptionsButton.addActionListener(eventHandler);
		}
		return enableAllViewOptionsButton;
	}

	private JButton getDisableAllViewOptionsButton() {
		if (disableAllViewOptionsButton == null) {
			disableAllViewOptionsButton = new JButton("Disable all");
			disableAllViewOptionsButton.setMnemonic('i');
			disableAllViewOptionsButton.setDisplayedMnemonicIndex(1);
			disableAllViewOptionsButton.addActionListener(eventHandler);
		}
		return disableAllViewOptionsButton;
	}

	private JLabel getDesiredTpsLabel() {
		if (desiredTpsLabel == null) {
			desiredTpsLabel = new JLabel("Desired TPS (" + MIN_TPS + '-' + MAX_TPS + "): ");
		}
		return desiredTpsLabel;
	}

	private JTextField getDesiredTpsTextField() {
		if (desiredTpsTextField == null) {
			desiredTpsTextField = new JTextField();
			desiredTpsTextField.setColumns(5);
			desiredTpsTextField.getDocument().addDocumentListener(eventHandler);

			desiredTpsTextField.setInputVerifier(
					new InputVerifier() {
				@Override
				public boolean verify(JComponent c) {			
					String text = desiredTpsTextField.getText();
					
					int inputTps;

					try {
						inputTps = new Integer(text);
					} catch (NumberFormatException e) {
						inputTps = -1;
					}
					return inputTps >= MIN_TPS && inputTps <= MAX_TPS;
				}
				
				public boolean shouldYieldFocus(JComponent input) {
					if (verify(input)) {
						return true;
					}
					JOptionPane.showMessageDialog(null,
							"The specified value for 'Desired TPS' (" + MIN_TPS + '-' + MAX_TPS + ") is invalid.",
							"Invalid input", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			});
		}
		return desiredTpsTextField;
	}

	private JCheckBox getDisplayFpsCheckBox() {
		if (displayFpsCheckBox == null) {
			displayFpsCheckBox = new JCheckBox("Display FPS in titlebar");
			displayFpsCheckBox.setMnemonic('P');
			displayFpsCheckBox.setDisplayedMnemonicIndex(9);
		}
		return displayFpsCheckBox;
	}

	private JCheckBox getDisplayTpsCheckBox() {
		if (displayTpsCheckBox == null) {
			displayTpsCheckBox = new JCheckBox("Display TPS in titlebar");
			displayTpsCheckBox.setMnemonic('T');
			displayTpsCheckBox.setDisplayedMnemonicIndex(8);
		}
		return displayTpsCheckBox;
	}

	private JButton getDefaultTpsButton() {
		if (defaultTpsButton == null) {
			defaultTpsButton = new JButton("Default");
			defaultTpsButton.addActionListener(eventHandler);
		}
		return defaultTpsButton;
	}

	private JButton getMinTpsButton() {
		if (minTpsButton == null) {
			minTpsButton = new JButton("Minimum");
			minTpsButton.addActionListener(eventHandler);
		}
		return minTpsButton;
	}

	private JButton getMaxTpsButton() {
		if (maxTpsButton == null) {
			maxTpsButton = new JButton("Max");
			maxTpsButton.addActionListener(eventHandler);
		}
		return maxTpsButton;
	}

	private JButton getFastTpsButton() {
		if (fastTpsButton == null) {
			fastTpsButton = new JButton("Fast");
			fastTpsButton.addActionListener(eventHandler);
		}
		return fastTpsButton;
	}

	private JCheckBox getPreventSpeedupWhenMinimizedCheckBox() {
		if (preventSpeedupWhenMinimizedCheckBox == null) {
			preventSpeedupWhenMinimizedCheckBox = new JCheckBox("Prevent speedup when minimized");
		}
		return preventSpeedupWhenMinimizedCheckBox;
	}
	
	private JPanel getTpsOptionsPanel() {
		if (tpsOptionsPanel == null) {
			tpsOptionsPanel = new JPanel();
			tpsOptionsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Turns Per Second (TPS)"));
			tpsOptionsPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridwidth = 4;

			tpsOptionsPanel.add(getDisplayTpsCheckBox(), c);
			c.gridy = 1;
			tpsOptionsPanel.add(getDisplayFpsCheckBox(), c);

			JPanel tpsPanel = new JPanel();

			tpsPanel.add(getDesiredTpsLabel());
			tpsPanel.add(getDesiredTpsTextField());

			c.fill = GridBagConstraints.VERTICAL;
			c.gridy = 2;
			c.insets = new Insets(10, 0, 0, 0);
			tpsOptionsPanel.add(tpsPanel, c);

			c.fill = GridBagConstraints.NONE;
			c.gridx = 0;
			c.gridy = 3;
			c.gridwidth = 1;
			c.weightx = 0.25;
			tpsOptionsPanel.add(getMinTpsButton(), c);
			c.gridx = 1;
			tpsOptionsPanel.add(getDefaultTpsButton(), c);
			c.gridx = 2;
			tpsOptionsPanel.add(getFastTpsButton(), c);
			c.gridx = 3;
			tpsOptionsPanel.add(getMaxTpsButton(), c);

			c.insets = new Insets(20, 0, 0, 0);
			c.gridwidth = 4;
			c.gridx = 0;
			c.gridy = 4;
			c.weighty = 1;
			tpsOptionsPanel.add(getPreventSpeedupWhenMinimizedCheckBox(), c);
		}
		return tpsOptionsPanel;
	}

	private JPanel getVisibleOptionsPanel() {
		if (visibleOptionsPanel == null) {
			visibleOptionsPanel = new JPanel();
			visibleOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Arena"));
			visibleOptionsPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;

			visibleOptionsPanel.add(getVisibleRobotEnergyCheckBox(), c);
			visibleOptionsPanel.add(getVisibleRobotNameCheckBox(), c);
			visibleOptionsPanel.add(getVisibleScanArcsCheckBox(), c);
			visibleOptionsPanel.add(getVisibleExplosionsCheckBox(), c);
			visibleOptionsPanel.add(getVisibleGroundCheckBox(), c);
			visibleOptionsPanel.add(getVisibleExplosionDebrisCheckBox(), c);
			visibleOptionsPanel.add(getVisibleSentryBorderCheckBox(), c);

			c.insets = new Insets(10, 0, 0, 10);
			c.gridwidth = 1;
			c.fill = GridBagConstraints.NONE;
			c.weighty = 1;
			c.weightx = 0;
			visibleOptionsPanel.add(getEnableAllViewOptionsButton(), c);
			visibleOptionsPanel.add(getDisableAllViewOptionsButton(), c);
			visibleOptionsPanel.add(getDefaultViewOptionsButton(), c);
		}
		return visibleOptionsPanel;
	}

	private JCheckBox getVisibleRobotEnergyCheckBox() {
		if (visibleRobotEnergyCheckBox == null) {
			visibleRobotEnergyCheckBox = new JCheckBox("Visible Robot Energy");
			visibleRobotEnergyCheckBox.setMnemonic('y');
			visibleRobotEnergyCheckBox.setDisplayedMnemonicIndex(19);
		}
		return visibleRobotEnergyCheckBox;
	}

	private JCheckBox getVisibleRobotNameCheckBox() {
		if (visibleRobotNameCheckBox == null) {
			visibleRobotNameCheckBox = new JCheckBox("Visible Robot Name");
			visibleRobotNameCheckBox.setMnemonic('V');
		}
		return visibleRobotNameCheckBox;
	}

	private JCheckBox getVisibleScanArcsCheckBox() {
		if (visibleScanArcsCheckBox == null) {
			visibleScanArcsCheckBox = new JCheckBox("Visible Scan Arcs");
			visibleScanArcsCheckBox.setMnemonic('b');
			visibleScanArcsCheckBox.setDisplayedMnemonicIndex(4);
		}
		return visibleScanArcsCheckBox;
	}

	private JCheckBox getVisibleExplosionsCheckBox() {
		if (visibleExplosionsCheckBox == null) {
			visibleExplosionsCheckBox = new JCheckBox("Visible Explosions");
			visibleExplosionsCheckBox.setMnemonic('x');
			visibleExplosionsCheckBox.setDisplayedMnemonicIndex(9);
		}
		return visibleExplosionsCheckBox;
	}

	private JCheckBox getVisibleGroundCheckBox() {
		if (visibleGroundCheckBox == null) {
			visibleGroundCheckBox = new JCheckBox("Visible Ground");
			visibleGroundCheckBox.setMnemonic('G');
			visibleGroundCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleGroundCheckBox;
	}

	private JCheckBox getVisibleExplosionDebrisCheckBox() {
		if (visibleExplosionDebrisCheckBox == null) {
			visibleExplosionDebrisCheckBox = new JCheckBox("Visible Explosion Debris");
			visibleExplosionDebrisCheckBox.setMnemonic('E');
			visibleExplosionDebrisCheckBox.setDisplayedMnemonicIndex(8);
		}
		return visibleExplosionDebrisCheckBox;
	}
	
	private JCheckBox getVisibleSentryBorderCheckBox() {
		if (visibleSentryBorderCheckBox == null) {
			visibleSentryBorderCheckBox = new JCheckBox("Visible Sentry Border");
			visibleSentryBorderCheckBox.setMnemonic('l');
			visibleSentryBorderCheckBox.setDisplayedMnemonicIndex(5);
		}
		return visibleSentryBorderCheckBox;
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getVisibleOptionsPanel());
		add(getTpsOptionsPanel());

		final ISettingsManager props = properties;

		loadPreferences(props);

		props.addPropertyListener(new ISettingsListener() {
			public void settingChanged(String property) {
				if (property.equals(ISettingsManager.OPTIONS_BATTLE_DESIREDTPS)) {
					PreferencesViewOptionsTab.this.desiredTpsTextField.setText("" + props.getOptionsBattleDesiredTPS());
				}
			}
		});
	}

	private void loadPreferences(ISettingsManager robocodeProperties) {
		getDisplayFpsCheckBox().setSelected(robocodeProperties.getOptionsViewFPS());
		getDisplayTpsCheckBox().setSelected(robocodeProperties.getOptionsViewTPS());
		getVisibleRobotNameCheckBox().setSelected(robocodeProperties.getOptionsViewRobotNames());
		getVisibleRobotEnergyCheckBox().setSelected(robocodeProperties.getOptionsViewRobotEnergy());
		getVisibleScanArcsCheckBox().setSelected(robocodeProperties.getOptionsViewScanArcs());
		getVisibleExplosionsCheckBox().setSelected(robocodeProperties.getOptionsViewExplosions());
		getVisibleGroundCheckBox().setSelected(robocodeProperties.getOptionsViewGround());
		getVisibleExplosionDebrisCheckBox().setSelected(robocodeProperties.getOptionsViewExplosionDebris());
		getVisibleSentryBorderCheckBox().setSelected(robocodeProperties.getOptionsViewSentryBorder());
		getDesiredTpsTextField().setText("" + robocodeProperties.getOptionsBattleDesiredTPS());
		getPreventSpeedupWhenMinimizedCheckBox().setSelected(
				robocodeProperties.getOptionsViewPreventSpeedupWhenMinimized());
	}

	public void storePreferences() {
		ISettingsManager props = properties;

		props.setOptionsViewFPS(getDisplayFpsCheckBox().isSelected());
		props.setOptionsViewTPS(getDisplayTpsCheckBox().isSelected());
		props.setOptionsViewRobotNames(getVisibleRobotNameCheckBox().isSelected());
		props.setOptionsViewRobotEnergy(getVisibleRobotEnergyCheckBox().isSelected());
		props.setOptionsViewScanArcs(getVisibleScanArcsCheckBox().isSelected());
		props.setOptionsViewExplosions(getVisibleExplosionsCheckBox().isSelected());
		props.setOptionsViewGround(getVisibleGroundCheckBox().isSelected());
		props.setOptionsViewExplosionDebris(getVisibleExplosionDebrisCheckBox().isSelected());
		props.setOptionsViewSentryBorder(getVisibleSentryBorderCheckBox().isSelected());
		props.setOptionsBattleDesiredTPS(Integer.parseInt(getDesiredTpsTextField().getText()));
		props.setOptionsViewPreventSpeedupWhenMinimized(getPreventSpeedupWhenMinimizedCheckBox().isSelected());
		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		try {
			int tps = Integer.parseInt(getDesiredTpsTextField().getText());

			return (tps >= MIN_TPS && tps <= MAX_TPS);
		} catch (Exception e) {
			return false;
		}
	}
}
