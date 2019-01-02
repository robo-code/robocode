/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class PreferencesRenderingOptionsTab extends WizardPanel {

	private final ISettingsManager properties;

	private JPanel specificSettingsPanel;
	private JPanel predefinedSettingsPanel;
	private JPanel otherSettingsPanel;

	private JComboBox optionsRenderingAntialiasingComboBox;
	private JComboBox optionsRenderingTextAntialiasingComboBox;
	private JComboBox optionsRenderingMethodComboBox;
	private JComboBox optionsRenderingNoBuffersComboBox;
	private JCheckBox optionsRenderingBufferImagesCheckBox;
	private JCheckBox optionsRenderingForceBulletColorCheckBox;

	private JButton predefinedPlaformDefaultButton;
	private JButton predefinedSpeedButton;
	private JButton predefinedQualityButton;

	private EventHandler eventHandler;
	private final IImageManager imageManager;

	public PreferencesRenderingOptionsTab(ISettingsManager properties, IImageManager imageManager) {
		super();
		this.properties = properties;
		this.imageManager = imageManager;
		initialize();
	}

	private void initialize() {
		eventHandler = new EventHandler();

		setLayout(new GridLayout(1, 3));

		add(getSpecificSettingsPanel());
		add(getPredefinedSettingsPanel());
		add(getOtherSettingsPanel());

		loadPreferences(properties);
	}

	private JPanel getSpecificSettingsPanel() {
		if (specificSettingsPanel == null) {
			specificSettingsPanel = new JPanel();
			specificSettingsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Specific settings"));
			specificSettingsPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.PAGE_START;

			c.weightx = 0;

			c.gridwidth = 2;

			c.gridx = 0;
			c.gridy = 0;
			specificSettingsPanel.add(new JLabel("Set individual rendering options:"), c);

			c.gridwidth = 1;

			c.gridy = 1;
			specificSettingsPanel.add(new JLabel("Antialiasing", SwingConstants.RIGHT), c);
			c.gridx = 1;
			specificSettingsPanel.add(getOptionsRenderingAntialiasingComboBox(), c);

			c.gridx = 0;
			c.gridy = 2;
			specificSettingsPanel.add(new JLabel("Text Antialiasing", SwingConstants.RIGHT), c);
			c.gridx = 1;
			specificSettingsPanel.add(getOptionsRenderingTextAntialiasingComboBox(), c);

			c.gridx = 0;
			c.gridy = 3;
			specificSettingsPanel.add(new JLabel("Rendering Method", SwingConstants.RIGHT), c);
			c.gridx = 1;
			specificSettingsPanel.add(getOptionsRenderingMethodComboBox(), c);

			c.gridx = 0;
			c.gridy = 4;
			specificSettingsPanel.add(new JLabel(" "), c);

			c.gridx = 0;
			c.gridy = 5;
			specificSettingsPanel.add(new JLabel("Number of buffers", SwingConstants.RIGHT), c);
			c.gridx = 1;
			specificSettingsPanel.add(getOptionsRenderingNoBuffersComboBox(), c);
		}
		return specificSettingsPanel;
	}

	private JPanel getPredefinedSettingsPanel() {
		if (predefinedSettingsPanel == null) {
			predefinedSettingsPanel = new JPanel();
			predefinedSettingsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Predefined settings"));
			predefinedSettingsPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.PAGE_START;

			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;
			predefinedSettingsPanel.add(new JLabel("Set all rendering settings towards:"), c);

			c.weightx = 1f / 3;
			c.gridwidth = 1;
			c.gridy = 2;
			predefinedSettingsPanel.add(getPredefinedPlatformDefaultButton(), c);
			c.gridx = 1;
			predefinedSettingsPanel.add(getPredefinedSpeedButton(), c);
			c.gridx = 2;
			predefinedSettingsPanel.add(getPredefinedQualityButton(), c);
		}
		return predefinedSettingsPanel;
	}

	private JPanel getOtherSettingsPanel() {
		if (otherSettingsPanel == null) {
			otherSettingsPanel = new JPanel();
			otherSettingsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Other settings"));
			otherSettingsPanel.setLayout(new BoxLayout(otherSettingsPanel, BoxLayout.Y_AXIS));
			otherSettingsPanel.add(getOptionsRenderingBufferImagesCheckBox());
			otherSettingsPanel.add(getOptionsRenderingForceBulletColorCheckBox());
		}
		return otherSettingsPanel;
	}

	private JComboBox getOptionsRenderingAntialiasingComboBox() {
		if (optionsRenderingAntialiasingComboBox == null) {
			optionsRenderingAntialiasingComboBox = new JComboBox(new String[] { "Default", "On", "Off"});
			optionsRenderingAntialiasingComboBox.addActionListener(eventHandler);
		}
		return optionsRenderingAntialiasingComboBox;
	}

	private JComboBox getOptionsRenderingTextAntialiasingComboBox() {
		if (optionsRenderingTextAntialiasingComboBox == null) {
			optionsRenderingTextAntialiasingComboBox = new JComboBox(new String[] { "Default", "On", "Off"});
			optionsRenderingTextAntialiasingComboBox.addActionListener(eventHandler);
		}
		return optionsRenderingTextAntialiasingComboBox;
	}

	private JComboBox getOptionsRenderingMethodComboBox() {
		if (optionsRenderingMethodComboBox == null) {
			optionsRenderingMethodComboBox = new JComboBox(new String[] { "Default", "Quality", "Speed"});
			optionsRenderingMethodComboBox.addActionListener(eventHandler);
		}
		return optionsRenderingMethodComboBox;
	}

	private JComboBox getOptionsRenderingNoBuffersComboBox() {
		if (optionsRenderingNoBuffersComboBox == null) {
			optionsRenderingNoBuffersComboBox = new JComboBox(
					new String[] { "Single buffering", "Double buffering", "Triple buffering"});
			optionsRenderingNoBuffersComboBox.addActionListener(eventHandler);
		}
		return optionsRenderingNoBuffersComboBox;
	}

	private JButton getPredefinedPlatformDefaultButton() {
		if (predefinedPlaformDefaultButton == null) {
			predefinedPlaformDefaultButton = new JButton("Default");
			predefinedPlaformDefaultButton.setMnemonic('u');
			predefinedPlaformDefaultButton.setDisplayedMnemonicIndex(4);
			predefinedPlaformDefaultButton.addActionListener(eventHandler);
		}
		return predefinedPlaformDefaultButton;
	}

	private JButton getPredefinedSpeedButton() {
		if (predefinedSpeedButton == null) {
			predefinedSpeedButton = new JButton("Speed");
			predefinedSpeedButton.setMnemonic('p');
			predefinedSpeedButton.setDisplayedMnemonicIndex(1);
			predefinedSpeedButton.addActionListener(eventHandler);
		}
		return predefinedSpeedButton;
	}

	private JButton getPredefinedQualityButton() {
		if (predefinedQualityButton == null) {
			predefinedQualityButton = new JButton("Quality");
			predefinedQualityButton.setMnemonic('Q');
			predefinedQualityButton.addActionListener(eventHandler);
		}
		return predefinedQualityButton;
	}

	private JCheckBox getOptionsRenderingBufferImagesCheckBox() {
		if (optionsRenderingBufferImagesCheckBox == null) {
			optionsRenderingBufferImagesCheckBox = new JCheckBox("Buffer images (uses memory)");
			optionsRenderingBufferImagesCheckBox.setMnemonic('i');
			optionsRenderingBufferImagesCheckBox.setDisplayedMnemonicIndex(7);
			optionsRenderingBufferImagesCheckBox.addActionListener(eventHandler);
		}
		return optionsRenderingBufferImagesCheckBox;
	}

	private JCheckBox getOptionsRenderingForceBulletColorCheckBox() {
		if (optionsRenderingForceBulletColorCheckBox == null) {
			optionsRenderingForceBulletColorCheckBox = new JCheckBox("Make all bullets white");
			optionsRenderingForceBulletColorCheckBox.setMnemonic('M');
			optionsRenderingForceBulletColorCheckBox.addActionListener(eventHandler);
		}
		return optionsRenderingForceBulletColorCheckBox;
	}

	private void loadPreferences(ISettingsManager props) {
		getOptionsRenderingAntialiasingComboBox().setSelectedIndex(props.getOptionsRenderingAntialiasing());
		getOptionsRenderingTextAntialiasingComboBox().setSelectedIndex(props.getOptionsRenderingTextAntialiasing());
		getOptionsRenderingMethodComboBox().setSelectedIndex(props.getOptionsRenderingMethod());
		getOptionsRenderingNoBuffersComboBox().setSelectedIndex(props.getOptionsRenderingNoBuffers() - 1);
		getOptionsRenderingBufferImagesCheckBox().setSelected(props.getOptionsRenderingBufferImages());
		getOptionsRenderingForceBulletColorCheckBox().setSelected(props.getOptionsRenderingForceBulletColor());
	}

	public void storePreferences() {
		ISettingsManager props = properties;

		props.setOptionsRenderingAntialiasing(optionsRenderingAntialiasingComboBox.getSelectedIndex());
		props.setOptionsRenderingTextAntialiasing(optionsRenderingTextAntialiasingComboBox.getSelectedIndex());
		props.setOptionsRenderingMethod(optionsRenderingMethodComboBox.getSelectedIndex());
		props.setOptionsRenderingNoBuffers(optionsRenderingNoBuffersComboBox.getSelectedIndex() + 1);
		props.setOptionsRenderingBufferImages(optionsRenderingBufferImagesCheckBox.isSelected());
		props.setOptionsRenderingForceBulletColor(optionsRenderingForceBulletColorCheckBox.isSelected());
		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	private void setPredefinedSettings(int index) {
		optionsRenderingAntialiasingComboBox.setSelectedIndex(index);
		optionsRenderingTextAntialiasingComboBox.setSelectedIndex(index);
		optionsRenderingMethodComboBox.setSelectedIndex(index);
	}

	private class EventHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == predefinedPlaformDefaultButton) {
				setPredefinedSettings(0);
			} else if (src == predefinedQualityButton) {
				setPredefinedSettings(1);
			} else if (src == predefinedSpeedButton) {
				setPredefinedSettings(2);
			} else if (src == optionsRenderingBufferImagesCheckBox) {
				// Reset images so they are reloaded and gets buffered or unbuffered
				new Thread() {
					@Override
					public void run() {
						storePreferences();
						imageManager.initialize();
					}
				}.start();
				return;
			}
		}
	}
}
