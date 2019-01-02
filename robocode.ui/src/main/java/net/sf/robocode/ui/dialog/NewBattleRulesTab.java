/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.settings.ISettingsManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class NewBattleRulesTab extends JPanel {

	private final static int MIN_BATTLEFIELD_SIZE = 400;
	private final static int MAX_BATTLEFIELD_SIZE = 5000;
	private final static int BATTLEFIELD_STEP_SIZE = 100;

	private ISettingsManager settingsManager;
	private BattleProperties battleProperties;

	private final EventHandler eventHandler = new EventHandler();

	private SizeButton[] predefinedSizeButtons = {
		new SizeButton(400, 400), new SizeButton(600, 400), new SizeButton(600, 600), new SizeButton(800, 600),
		new SizeButton(800, 800), new SizeButton(1000, 800), new SizeButton(1000, 1000), new SizeButton(1200, 1200),
		new SizeButton(2000, 2000), new SizeButton(5000, 5000)
	};

	private final JLabel numberOfRoundsLabel = new JLabel("Number of Rounds:");
	private final JLabel gunCoolingRateLabel = new JLabel("Gun Cooling Rate:");
	private final JLabel inactivityTimeLabel = new JLabel("Inactivity Time:");
	private final JLabel sentryBorderSizeLabel = new JLabel("Sentry Border Size");
	private final JLabel hideEnemyNamesLabel = new JLabel("Hide Enemy Names:");

	private final JButton restoreDefaultsButton = new JButton("Restore Defaults");
	
	private JTextField numberOfRoundsTextField;
	private JTextField gunCoolingRateTextField;
	private JTextField inactivityTimeTextField;
	private JTextField sentryBorderSizeTextField;
	private final JCheckBox hideEnemyNamesCheckBox = new JCheckBox();

	private JSlider battlefieldWidthSlider;
	private JSlider battlefieldHeightSlider;
	private JLabel battlefieldSizeLabel;

	public NewBattleRulesTab() {
		super();
	}

	public void setup(ISettingsManager settingsManager, BattleProperties battleProperties) {
		this.settingsManager = settingsManager;
		this.battleProperties = battleProperties;

		EventHandler eventHandler = new EventHandler();

		battlefieldWidthSlider = createBattlefieldSizeSlider();
		battlefieldWidthSlider.setOrientation(SwingConstants.HORIZONTAL);

		battlefieldHeightSlider = createBattlefieldSizeSlider();
		battlefieldHeightSlider.setOrientation(SwingConstants.VERTICAL);
		battlefieldHeightSlider.setInverted(true);

		battlefieldSizeLabel = new BattlefieldSizeLabel();
		battlefieldSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		battlefieldSizeLabel.setMinimumSize(new Dimension(50, 50));
		battlefieldSizeLabel.setMaximumSize(new Dimension(500, 500));

		battlefieldWidthSlider.addChangeListener(eventHandler);
		battlefieldHeightSlider.addChangeListener(eventHandler);

		JPanel rulesPanel = createRulesPanel();
		rulesPanel.addAncestorListener(eventHandler);

		restoreDefaultsButton.addActionListener(eventHandler);

		setLayout(new BorderLayout());
		
		add(rulesPanel, BorderLayout.WEST);
		add(restoreDefaultsButton, BorderLayout.SOUTH);
		add(createBattlefieldSizePanel(), BorderLayout.CENTER);
	}

	private JPanel createBattlefieldSizePanel() {
		JPanel panel = new JPanel();
		panel.addAncestorListener(eventHandler);
		
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Battlefield Size"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setBorder(border);

		panel.setLayout(new BorderLayout());

		JPanel sliderPanel = createBattlefieldSlidersPanel();
		panel.add(sliderPanel, BorderLayout.CENTER);

		JPanel buttonsPanel = createPredefinedSizesPanel();
		panel.add(buttonsPanel, BorderLayout.EAST);

		return panel;
	}
	
	private JPanel createBattlefieldSlidersPanel() {
		JPanel panel = new JPanel();

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();

		GroupLayout.ParallelGroup left = layout.createParallelGroup();
		left.addComponent(battlefieldSizeLabel);
		left.addComponent(battlefieldWidthSlider);
		leftToRight.addGroup(left);
		
		GroupLayout.ParallelGroup right = layout.createParallelGroup();
		right.addComponent(battlefieldHeightSlider);
		leftToRight.addGroup(right);
		
		GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();

		GroupLayout.ParallelGroup top = layout.createParallelGroup();
		top.addComponent(battlefieldSizeLabel);
		top.addComponent(battlefieldHeightSlider);
		topToBottom.addGroup(top);

		GroupLayout.ParallelGroup bottom = layout.createParallelGroup();
		bottom.addComponent(battlefieldWidthSlider);
		topToBottom.addGroup(bottom);

		layout.setHorizontalGroup(leftToRight);
		layout.setVerticalGroup(topToBottom);

		return panel;
	}

	private JPanel createPredefinedSizesPanel() {
		JPanel panel = new JPanel();

		Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0),
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Predefined Sizes"));
		panel.setBorder(border);

		panel.setLayout(new GridLayout(predefinedSizeButtons.length, 1));
		
		for (SizeButton button : predefinedSizeButtons) {
			panel.add(button);
		}
		return panel;
	}

	private JPanel createRulesPanel() {
		JPanel panel = new JPanel();

		panel.addAncestorListener(new EventHandler());
		panel.setBorder(BorderFactory.createEtchedBorder());

		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);

		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();

		GroupLayout.ParallelGroup left = layout.createParallelGroup();
		left.addComponent(numberOfRoundsLabel);
		left.addComponent(gunCoolingRateLabel);
		left.addComponent(inactivityTimeLabel);
		left.addComponent(sentryBorderSizeLabel);
		left.addComponent(hideEnemyNamesLabel);
		leftToRight.addGroup(left);
		
		GroupLayout.ParallelGroup right = layout.createParallelGroup();
		right.addComponent(getNumberOfRoundsTextField());
		right.addComponent(getGunCoolingRateTextField());
		right.addComponent(getInactivityTimeTextField());
		right.addComponent(getSentryBorderSizeTextField());
		right.addComponent(hideEnemyNamesCheckBox);
		leftToRight.addGroup(right);
		
		GroupLayout.SequentialGroup topToBottom = layout.createSequentialGroup();

		GroupLayout.ParallelGroup row0 = layout.createParallelGroup(Alignment.BASELINE);
		row0.addComponent(numberOfRoundsLabel);
		row0.addComponent(numberOfRoundsTextField);
		topToBottom.addGroup(row0);

		GroupLayout.ParallelGroup row1 = layout.createParallelGroup(Alignment.BASELINE);
		row1.addComponent(gunCoolingRateLabel);
		row1.addComponent(getGunCoolingRateTextField());
		topToBottom.addGroup(row1);

		GroupLayout.ParallelGroup row2 = layout.createParallelGroup(Alignment.BASELINE);
		row2.addComponent(inactivityTimeLabel);
		row2.addComponent(inactivityTimeTextField);
		topToBottom.addGroup(row2);

		GroupLayout.ParallelGroup row3 = layout.createParallelGroup(Alignment.BASELINE);
		row3.addComponent(sentryBorderSizeLabel);
		row3.addComponent(sentryBorderSizeTextField);
		topToBottom.addGroup(row3);

		GroupLayout.ParallelGroup row4 = layout.createParallelGroup(Alignment.CENTER);
		row4.addComponent(hideEnemyNamesLabel);
		row4.addComponent(hideEnemyNamesCheckBox);
		topToBottom.addGroup(row4);

		layout.setHorizontalGroup(leftToRight);
		layout.setVerticalGroup(topToBottom);
		
		return panel;
	}	

	private JTextField getNumberOfRoundsTextField() {
		if (numberOfRoundsTextField == null) {
			numberOfRoundsTextField = new JTextField(5);
			numberOfRoundsTextField.setText("" + battleProperties.getNumRounds());
			numberOfRoundsTextField.setInputVerifier(
					new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					boolean isValid = false;

					String text = ((JTextField) input).getText();
					if (text != null && text.matches("\\d+")) {
						int numRounds = Integer.parseInt(text);
						isValid = (numRounds > 0);
					}
					if (!isValid) {
						WindowUtil.messageError(
								"'Number of Rounds' must be an integer value > 0.\n" + "Default value is 10.");
						numberOfRoundsTextField.setText("" + battleProperties.getNumRounds());
					}
					return isValid;
				}
			});
		}
		return numberOfRoundsTextField;
	}

	private JTextField getGunCoolingRateTextField() {
		if (gunCoolingRateTextField == null) {
			gunCoolingRateTextField = new JTextField(5);
			gunCoolingRateTextField.setText("" + battleProperties.getGunCoolingRate());
			gunCoolingRateTextField.setInputVerifier(
					new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					boolean isValid = false;

					String text = ((JTextField) input).getText();
					if (text != null && text.matches("\\d*(\\.\\d+)?")) {
						double gunCoolingRate = Double.parseDouble(text);
						isValid = (gunCoolingRate > 0 && gunCoolingRate <= 0.7);
					}
					if (!isValid) {
						WindowUtil.messageError(
								"'Gun Cooling Rate' must be a floating point number > 0 and <= 0.7.\n"
										+ "Default value is 0.1.");
						gunCoolingRateTextField.setText("" + battleProperties.getGunCoolingRate());
					}
					return isValid;
				}
			});
		}
		return gunCoolingRateTextField;
	}

	private JTextField getInactivityTimeTextField() {
		if (inactivityTimeTextField == null) {
			inactivityTimeTextField = new JTextField(5);
			inactivityTimeTextField.setText("" + battleProperties.getInactivityTime());
			inactivityTimeTextField.setInputVerifier(
					new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					boolean isValid = false;

					String text = ((JTextField) input).getText();
					if (text != null && text.matches("\\d+")) {
						int inactivityTime = Integer.parseInt(text);
						isValid = (inactivityTime >= 0);
					}
					if (!isValid) {
						WindowUtil.messageError(
								"'Inactivity Time' must be an integer value >= 0.\n" + "Default value is 450.");
						inactivityTimeTextField.setText("" + battleProperties.getInactivityTime());
					}
					return isValid;
				}
			});
		}
		return inactivityTimeTextField;
	}
	
	private JTextField getSentryBorderSizeTextField() {
		if (sentryBorderSizeTextField == null) {
			sentryBorderSizeTextField = new JTextField(5);
			sentryBorderSizeTextField.setText("" + battleProperties.getSentryBorderSize());
			sentryBorderSizeTextField.setInputVerifier(
					new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					boolean isValid = false;

					String text = ((JTextField) input).getText();
					if (text != null && text.matches("\\d+")) {
						int borderSize = Integer.parseInt(text);
						isValid = (borderSize >= 50);
					}
					if (!isValid) {
						WindowUtil.messageError(
								"'Sentry Border Size' must be an integer value >= 50.\n" + "Default value is 100.");
						sentryBorderSizeTextField.setText("" + battleProperties.getSentryBorderSize());
					}
					return isValid;
				}
			});
		}
		return sentryBorderSizeTextField;
	}

	private JSlider createBattlefieldSizeSlider() {
		JSlider slider = new JSlider();
		slider.setMinimum(MIN_BATTLEFIELD_SIZE);
		slider.setMaximum(MAX_BATTLEFIELD_SIZE);
		slider.setMajorTickSpacing(BATTLEFIELD_STEP_SIZE);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		return slider;
	}

	private void updateBattlefieldSizeLabel() {
		int w = battlefieldWidthSlider.getValue();
		int h = battlefieldHeightSlider.getValue();
		battlefieldSizeLabel.setText(w + " x " + h);
	}

	private class SizeButton extends JButton {
		final int width;
		final int height;

		public SizeButton(int width, int height) {
			super(width + "x" + height);
			this.width = width;
			this.height = height;
			addActionListener(eventHandler);
		}
	}


	private class EventHandler implements AncestorListener, ActionListener, ChangeListener {
		@Override
		public void ancestorAdded(AncestorEvent event) {
			pushBattlePropertiesToUIComponents();
		}

		@Override
		public void ancestorRemoved(AncestorEvent event) {
			Integer numberOfRounds;
			try {
				numberOfRounds = Integer.parseInt(getNumberOfRoundsTextField().getText());
			} catch (NumberFormatException e) {
				numberOfRounds = null;
			}
			if (numberOfRounds != null) {
				settingsManager.setBattleDefaultNumberOfRounds(numberOfRounds);
				battleProperties.setNumRounds(numberOfRounds);
			}
			Double gunCoolingRate;
			try {
				gunCoolingRate = Double.parseDouble(getGunCoolingRateTextField().getText());
			} catch (NumberFormatException e) {
				gunCoolingRate = null;
			}
			if (gunCoolingRate != null) {
				settingsManager.setBattleDefaultGunCoolingRate(gunCoolingRate);
				battleProperties.setGunCoolingRate(gunCoolingRate);
			}
			Integer inactivityTime;
			try {
				inactivityTime = Integer.parseInt(getInactivityTimeTextField().getText());
			} catch (NumberFormatException e) {
				inactivityTime = null;
			}
			if (inactivityTime != null) {
				settingsManager.setBattleDefaultInactivityTime(inactivityTime);
				battleProperties.setInactivityTime(inactivityTime);
			}
			Integer sentryBorderSize;
			try {
				sentryBorderSize = Integer.parseInt(getSentryBorderSizeTextField().getText());
			} catch (NumberFormatException e) {
				sentryBorderSize = null;
			}
			if (sentryBorderSize != null) {
				settingsManager.setBattleDefaultSentryBorderSize(sentryBorderSize);
				battleProperties.setSentryBorderSize(sentryBorderSize);
			}
			boolean hideEnemyNames = hideEnemyNamesCheckBox.isSelected();
			settingsManager.setBattleDefaultHideEnemyNames(hideEnemyNames);
			battleProperties.setHideEnemyNames(hideEnemyNames);

			int weight = battlefieldWidthSlider.getValue();
			int height = battlefieldHeightSlider.getValue();

			settingsManager.setBattleDefaultBattlefieldWidth(weight);
			settingsManager.setBattleDefaultBattlefieldHeight(height);
			
			battleProperties.setBattlefieldWidth(weight);
			battleProperties.setBattlefieldHeight(height);			
		}

		@Override
		public void ancestorMoved(AncestorEvent event) {}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() instanceof SizeButton) {
				SizeButton button = (SizeButton) event.getSource();
				battlefieldWidthSlider.setValue(button.width);
				battlefieldHeightSlider.setValue(button.height);
				updateBattlefieldSizeLabel();

			} else if (event.getSource() == restoreDefaultsButton) {
				battleProperties.setBattlefieldWidth(800);
				battleProperties.setBattlefieldHeight(600);
				battleProperties.setNumRounds(10);
				battleProperties.setGunCoolingRate(0.1);
				battleProperties.setInactivityTime(450);
				battleProperties.setHideEnemyNames(false);
				battleProperties.setSentryBorderSize(100);

				pushBattlePropertiesToUIComponents();
			}
		}

		@Override
		public void stateChanged(ChangeEvent event) {
			if ((event.getSource() == battlefieldWidthSlider) || (event.getSource() == battlefieldHeightSlider)) {
				updateBattlefieldSizeLabel();
			}
		}

		private void pushBattlePropertiesToUIComponents() {
			battlefieldWidthSlider.setValue(battleProperties.getBattlefieldWidth());
			battlefieldHeightSlider.setValue(battleProperties.getBattlefieldHeight());
			updateBattlefieldSizeLabel();

			getNumberOfRoundsTextField().setText("" + battleProperties.getNumRounds());
			getGunCoolingRateTextField().setText("" + battleProperties.getGunCoolingRate());
			getInactivityTimeTextField().setText("" + battleProperties.getInactivityTime());
			getSentryBorderSizeTextField().setText("" + battleProperties.getSentryBorderSize());
			hideEnemyNamesCheckBox.setSelected(battleProperties.getHideEnemyNames());
		}
	}


	private class BattlefieldSizeLabel extends JLabel {
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(SystemColor.activeCaption);
			
			int width = getWidth() * battlefieldWidthSlider.getValue() / MAX_BATTLEFIELD_SIZE;
			int height = getHeight() * battlefieldHeightSlider.getValue() / MAX_BATTLEFIELD_SIZE;

			g.fillRect(0, 0, width, height);
			super.paintComponent(g);
		}
	}
}
