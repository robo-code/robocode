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
import javax.swing.JTextField;
import java.awt.*;
import robocode.manager.*;

/**
 * Insert the type's description here.
 * Creation date: (1/7/2001 11:07:52 PM)
 * @author: Mathew A. Nelson
 */
public class PreferencesDevelopmentOptionsTab extends WizardPanel {
	EventHandler eventHandler = new EventHandler();
	private JTextField optionsDevelopmentPathTextField = null;
	private JPanel developmentOptionsPanel = null;
	
	public RobocodeManager manager = null;
	
class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
		};
	};
/**
 * PreferencesDialog constructor
 */
public PreferencesDevelopmentOptionsTab(RobocodeManager manager)
{
	super();
	this.manager = manager;
	initialize();
}

private javax.swing.JTextField getOptionsDevelopmentPathTextField() {
	if (optionsDevelopmentPathTextField == null) {
		try {
			optionsDevelopmentPathTextField = new javax.swing.JTextField("",80);
			optionsDevelopmentPathTextField.setName("optionsDevelopmentPathTextField");
			optionsDevelopmentPathTextField.setMaximumSize(optionsDevelopmentPathTextField.getPreferredSize());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return optionsDevelopmentPathTextField;
}

private javax.swing.JPanel getDevelopmentOptionsPanel() {
	if (developmentOptionsPanel == null) {
		try {
			developmentOptionsPanel = new javax.swing.JPanel();
			developmentOptionsPanel.setName("teamOptionsPanel");
			developmentOptionsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Development"));
			developmentOptionsPanel.setLayout(new BoxLayout(developmentOptionsPanel,BoxLayout.Y_AXIS));
			developmentOptionsPanel.add(new JLabel("If you are using an external IDE to develop robots, you may enter the classpath to those robots here."));
			developmentOptionsPanel.add(new JLabel("(Example: c:\\eclipse\\workspace\\MyRobotProject" + java.io.File.pathSeparator + "c:\\eclipse\\workspace\\AnotherRobotProject"));
			developmentOptionsPanel.add(getOptionsDevelopmentPathTextField());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}
	return developmentOptionsPanel;
}

private void initialize() {
	try {
		setLayout(new GridLayout(1,2));
		add(getDevelopmentOptionsPanel());
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
	getOptionsDevelopmentPathTextField().setText(robocodeProperties.getOptionsDevelopmentPath());
}

/**
 * Insert the method's description here.
 * Creation date: (9/4/2001 11:16:59 AM)
 * @param props java.util.Properties
 */
public void storePreferences() {
	manager.getProperties().setOptionsDevelopmentPath(getOptionsDevelopmentPathTextField().getText());
	manager.saveProperties();
}

public boolean isReady()
{
	return true;	
}
}
