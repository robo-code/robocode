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
 *     - Rewritten + added browse button
 *******************************************************************************/
package robocode.dialog;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class PreferencesDevelopmentOptionsTab extends WizardPanel {

	private JPanel optionsPanel;

	private JButton browseButton;
	private JTextField pathTextField;

	public RobocodeManager manager;

	private EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == getBrowseButton()) {
				JFileChooser chooser = new JFileChooser();

				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chooser.showOpenDialog(optionsPanel) == JFileChooser.APPROVE_OPTION) {
					pathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		}
	}

	/**
	 * PreferencesDialog constructor
	 */
	public PreferencesDevelopmentOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getOptionsPanel());
		loadPreferences(manager.getProperties());
	}

	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Development"));

			GridBagLayout layout = new GridBagLayout();

			optionsPanel.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();

			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.NORTHWEST;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.weightx = 0;

			optionsPanel.add(
					new JLabel(
							"If you are using an external IDE to develop robots, you may enter the classpath to those robots here."),
							c);
			c.gridy = 1;
			optionsPanel.add(
					new JLabel(
							"Example:  c:\\eclipse\\workspace\\MyRobotProject" + java.io.File.pathSeparator
							+ "c:\\eclipse\\workspace\\AnotherRobotProject"),
							c);

			c.fill = GridBagConstraints.NONE;
			c.gridwidth = 1;
			c.gridy = 2;
			c.insets = new Insets(3, 3, 3, 3);
			optionsPanel.add(getBrowseButton(), c);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridx = 1;
			c.insets = new Insets(5, 5, 5, 5);
			optionsPanel.add(getPathTextField(), c);

			c.fill = GridBagConstraints.VERTICAL;
			c.weighty = 1;
			c.gridy = 3;
			optionsPanel.add(new JPanel(), c);
		}
		return optionsPanel;
	}

	private JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton("Browse");
			browseButton.setMnemonic('o');
			browseButton.setDisplayedMnemonicIndex(2);
			browseButton.addActionListener(eventHandler);
		}
		return browseButton;
	}

	private JTextField getPathTextField() {
		if (pathTextField == null) {
			pathTextField = new JTextField("", 80);
		}
		return pathTextField;
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getPathTextField().setText(robocodeProperties.getOptionsDevelopmentPath());
	}

	public void storePreferences() {
		manager.getProperties().setOptionsDevelopmentPath(getPathTextField().getText());
		manager.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
