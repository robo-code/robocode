/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import javax.swing.*;

import robocode.manager.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class PreferencesCommonOptionsTab extends WizardPanel {

	private JPanel optionsPanel;

	private JCheckBox showResultsCheckBox;

	public RobocodeManager manager;
	
	/**
	 * PreferencesCommonOptionsTab constructor
	 */
	public PreferencesCommonOptionsTab(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getOptionsPanel());
		loadPreferences(manager.getProperties());
	}

	/**
	 * Return the optionsPanel
	 * 
	 * @return JPanel
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Common"));
			optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
			optionsPanel.add(getShowResultsCheckBox());
		}
		return optionsPanel;
	}

	/**
	 * Return the showResultsCheckBox
	 * 
	 * @return JCheckBox
	 */
	private JCheckBox getShowResultsCheckBox() {
		if (showResultsCheckBox == null) {
			showResultsCheckBox = new JCheckBox("Show results when battle(s) ends");
			showResultsCheckBox.setMnemonic('h');
			showResultsCheckBox.setDisplayedMnemonicIndex(1);
		}
		return showResultsCheckBox;
	}

	private void loadPreferences(RobocodeProperties robocodeProperties) {
		getShowResultsCheckBox().setSelected(robocodeProperties.getOptionsCommonShowResults());
	}

	public void storePreferences() {
		manager.getProperties().setOptionsCommonShowResults(getShowResultsCheckBox().isSelected());
		manager.saveProperties();
	}

	public boolean isReady() {
		return true;
	}
}
