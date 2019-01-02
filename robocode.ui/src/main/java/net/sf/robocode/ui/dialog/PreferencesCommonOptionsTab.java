/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
	private JCheckBox enableAutoRecordingCheckBox;

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
			optionsPanel.add(getEnableAutoRecordingCheckBox());
		}
		return optionsPanel;
	}
	
	private JCheckBox getNotifyAboutBetaVersionsCheckBox() {
		if (notifyAboutBetaVersionsCheckBox == null) {
			notifyAboutBetaVersionsCheckBox = new JCheckBox("Notify about new Beta versions of Robocode");
			notifyAboutBetaVersionsCheckBox.setMnemonic('B');
			notifyAboutBetaVersionsCheckBox.setDisplayedMnemonicIndex(17);
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

	private JCheckBox getEnableAutoRecordingCheckBox() {
		if (enableAutoRecordingCheckBox == null) {
			enableAutoRecordingCheckBox = new JCheckBox(
					"Enable auto recording (every finished battle is automatically saved into battles folder)");
			enableAutoRecordingCheckBox.setMnemonic('u');
			enableAutoRecordingCheckBox.setDisplayedMnemonicIndex(8);

			enableAutoRecordingCheckBox.addActionListener(
					new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean isSelected = enableAutoRecordingCheckBox.isSelected();

					enableReplayRecordingCheckBox.setEnabled(!isSelected);
					enableReplayRecordingCheckBox.setSelected(
							isSelected ? true : properties.getOptionsCommonEnableReplayRecording());
				}
			});
		}
		return enableAutoRecordingCheckBox;
	}

	private void loadPreferences(ISettingsManager props) {
		getNotifyAboutBetaVersionsCheckBox().setSelected(props.getOptionsCommonNotifyAboutNewBetaVersions());
		getShowResultsCheckBox().setSelected(props.getOptionsCommonShowResults());
		getDontHideRankingsCheckBox().setSelected(props.getOptionsCommonDontHideRankings());
		getAppendWhenSavingResultsCheckBox().setSelected(props.getOptionsCommonAppendWhenSavingResults());
		getEnableReplayRecordingCheckBox().setSelected(props.getOptionsCommonEnableReplayRecording());
		getEnableAutoRecordingCheckBox().setSelected(props.getOptionsCommonEnableAutoRecording());
	}

	public void storePreferences() {
		ISettingsManager props = properties;

		props.setOptionsCommonNotifyAboutNewBetaVersions(getNotifyAboutBetaVersionsCheckBox().isSelected());
		props.setOptionsCommonShowResults(getShowResultsCheckBox().isSelected());
		props.setOptionsCommonDontHideRankings(getDontHideRankingsCheckBox().isSelected());
		props.setOptionsCommonAppendWhenSavingResults(getAppendWhenSavingResultsCheckBox().isSelected());
		props.setOptionsCommonEnableReplayRecording(getEnableReplayRecordingCheckBox().isSelected());
		props.setOptionsCommonEnableAutoRecording(getEnableAutoRecordingCheckBox().isSelected());

		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
