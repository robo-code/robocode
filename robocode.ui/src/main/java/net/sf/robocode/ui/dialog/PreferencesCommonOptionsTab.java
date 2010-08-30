/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsManager;

import javax.swing.*;
import java.awt.*;


/**
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class PreferencesCommonOptionsTab extends WizardPanel {

	private JPanel optionsPanel;

	private JCheckBox notifyAboutBetaVersionsCheckBox;
	private JCheckBox showResultsCheckBox;
	private JCheckBox dontHideRankingsCheckBox;
	private JCheckBox appendWhenSavingResultsCheckBox;
	private JCheckBox enableReplayRecordingCheckBox;

	private final ISettingsManager properties;

	public PreferencesCommonOptionsTab(ISettingsManager properties) {
		super();
		this.properties = properties;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(1, 2));
		add(getOptionsPanel());
		loadPreferences(properties);
	}

	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Common"));
			optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
			optionsPanel.add(getNotifyAboutBetaVersionsCheckBox());
			optionsPanel.add(new JLabel(" "));
			optionsPanel.add(getShowResultsCheckBox());
			optionsPanel.add(getDontHideRankingsCheckBox());
			optionsPanel.add(getAppendWhenSavingResultsCheckBox());
			optionsPanel.add(new JLabel(" "));
			optionsPanel.add(getEnableReplayRecordingCheckBox());
		}
		return optionsPanel;
	}
	
	private JCheckBox getNotifyAboutBetaVersionsCheckBox() {
		if (notifyAboutBetaVersionsCheckBox == null) {
			notifyAboutBetaVersionsCheckBox = new JCheckBox("Notify about new Beta versions of Robocode");
			notifyAboutBetaVersionsCheckBox.setMnemonic('n');
			notifyAboutBetaVersionsCheckBox.setDisplayedMnemonicIndex(0);
		}
		return notifyAboutBetaVersionsCheckBox;
	}

	private JCheckBox getShowResultsCheckBox() {
		if (showResultsCheckBox == null) {
			showResultsCheckBox = new JCheckBox("Show results when battle(s) ends");
			showResultsCheckBox.setMnemonic('h');
			showResultsCheckBox.setDisplayedMnemonicIndex(1);
		}
		return showResultsCheckBox;
	}

	private JCheckBox getDontHideRankingsCheckBox() {
		if (dontHideRankingsCheckBox == null) {
			dontHideRankingsCheckBox = new JCheckBox("Don't hide current rankings when main window is minimized");
			dontHideRankingsCheckBox.setMnemonic('t');
			dontHideRankingsCheckBox.setDisplayedMnemonicIndex(4);
		}
		return dontHideRankingsCheckBox;
	}

	private JCheckBox getAppendWhenSavingResultsCheckBox() {
		if (appendWhenSavingResultsCheckBox == null) {
			appendWhenSavingResultsCheckBox = new JCheckBox("Append when saving results");
			appendWhenSavingResultsCheckBox.setMnemonic('A');
		}
		return appendWhenSavingResultsCheckBox;
	}

	private JCheckBox getEnableReplayRecordingCheckBox() {
		if (enableReplayRecordingCheckBox == null) {
			enableReplayRecordingCheckBox = new JCheckBox("Enable replay recording (uses memory and disk space)");
			enableReplayRecordingCheckBox.setMnemonic('E');
		}
		return enableReplayRecordingCheckBox;
	}

	private void loadPreferences(ISettingsManager props) {
		getNotifyAboutBetaVersionsCheckBox().setSelected(props.getOptionsCommonNotifyAboutNewBetaVersions());
		getShowResultsCheckBox().setSelected(props.getOptionsCommonShowResults());
		getDontHideRankingsCheckBox().setSelected(props.getOptionsCommonDontHideRankings());
		getAppendWhenSavingResultsCheckBox().setSelected(props.getOptionsCommonAppendWhenSavingResults());
		getEnableReplayRecordingCheckBox().setSelected(props.getOptionsCommonEnableReplayRecording());
	}

	public void storePreferences() {
		ISettingsManager props = properties;

		props.setOptionsCommonNotifyAboutNewBetaVersions(getNotifyAboutBetaVersionsCheckBox().isSelected());
		props.setOptionsCommonShowResults(getShowResultsCheckBox().isSelected());
		props.setOptionsCommonDontHideRankings(getDontHideRankingsCheckBox().isSelected());
		props.setOptionsCommonAppendWhenSavingResults(getAppendWhenSavingResultsCheckBox().isSelected());
		props.setOptionsCommonEnableReplayRecording(getEnableReplayRecordingCheckBox().isSelected());

		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
