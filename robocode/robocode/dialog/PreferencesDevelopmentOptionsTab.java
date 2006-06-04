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
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class PreferencesDevelopmentOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();
	private JTextField optionsDevelopmentPathTextField;
	private JPanel developmentOptionsPanel;
	
	public RobocodeManager manager;
	
	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
	}

	/**
	 * PreferencesDialog constructor
	 */
	public PreferencesDevelopmentOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	private JTextField getOptionsDevelopmentPathTextField() {
		if (optionsDevelopmentPathTextField == null) {
			optionsDevelopmentPathTextField = new JTextField("", 80);
			optionsDevelopmentPathTextField.setMaximumSize(optionsDevelopmentPathTextField.getPreferredSize());
		}
		return optionsDevelopmentPathTextField;
	}

	private JPanel getDevelopmentOptionsPanel() {
		if (developmentOptionsPanel == null) {
			developmentOptionsPanel = new JPanel();
			developmentOptionsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Development"));
			developmentOptionsPanel.setLayout(new BoxLayout(developmentOptionsPanel, BoxLayout.Y_AXIS));
			developmentOptionsPanel.add(
					new JLabel(
							"If you are using an external IDE to develop robots, you may enter the classpath to those robots here."));
			developmentOptionsPanel.add(
					new JLabel(
							"(Example: c:\\eclipse\\workspace\\MyRobotProject" + java.io.File.pathSeparator
							+ "c:\\eclipse\\workspace\\AnotherRobotProject"));
			developmentOptionsPanel.add(getOptionsDevelopmentPathTextField());
		}
		return developmentOptionsPanel;
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getDevelopmentOptionsPanel());
		loadPreferences(manager.getProperties());
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getOptionsDevelopmentPathTextField().setText(robocodeProperties.getOptionsDevelopmentPath());
	}

	public void storePreferences() {
		manager.getProperties().setOptionsDevelopmentPath(getOptionsDevelopmentPathTextField().getText());
		manager.saveProperties();
	}

	public boolean isReady() {
		return true;
	}
}
