/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.CheckList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Ruben Moreno Montoliu (contributor)
 */
@SuppressWarnings("serial")
public class PreferencesDevelopmentOptionsTab extends WizardPanel {

	private JPanel optionsPanel;

	private JButton addButton;
	private JButton removeButton;
	private CheckList pathList;

	public final ISettingsManager properties;

	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener, ListSelectionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddButton()) {
				handleAddAction();
			} else if (e.getSource() == getRemoveButton()) {
				handleRemoveAction();
			}
		}

		public void valueChanged(ListSelectionEvent e) {			
			updateRemoveButton();
		}
	}

	public PreferencesDevelopmentOptionsTab(ISettingsManager properties) {
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
			optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Development"));

			GridBagLayout layout = new GridBagLayout();

			optionsPanel.setLayout(layout);
			GridBagConstraints c = new GridBagConstraints();

			c.insets = new Insets(0, 5, 5, 5);
			c.anchor = GridBagConstraints.NORTHWEST;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.weightx = 0;
			c.gridy = 1;

			optionsPanel.add(
					new JLabel(
							"If you are using an external IDE to develop robots, you may enter the classpath(s) to those robots here."),
							c);
			c.gridy++;
			optionsPanel.add(
					new JLabel(
							"If you are using Eclipse, you can enter the root dir of robot projects inside a workspace as well (recommended)"),
							c);
			c.gridy++;
			optionsPanel.add(new JLabel("Double-click the path(s) the enable or disable it (checked means enabled)"), c);

			c.gridwidth = 1;
			c.gridy++;
			c.insets = new Insets(3, 3, 3, 3);
			optionsPanel.add(getAddButton(), c);

			c.gridy++;
			optionsPanel.add(getRemoveButton(), c);

			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 1;
			c.gridy--;
			c.gridheight = 3;
			c.insets = new Insets(5, 5, 5, 5);
			JScrollPane pathListScroller = new JScrollPane(getPathList());

			optionsPanel.add(pathListScroller, c);
		}
		return optionsPanel;
	}

	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton("Add");
			addButton.setDisplayedMnemonicIndex(2);
			addButton.addActionListener(eventHandler);
		}
		return addButton;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton("Remove");
			removeButton.setDisplayedMnemonicIndex(3);
			removeButton.addActionListener(eventHandler);
		}
		return removeButton;
	}

	private CheckList getPathList() {
		if (pathList == null) {
			pathList = new CheckList();
			pathList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			pathList.setLayoutOrientation(JList.VERTICAL);
			pathList.setVisibleRowCount(-1);
			pathList.addListSelectionListener(eventHandler);
		}
		return pathList;
	}

	private void loadPreferences(ISettingsManager robocodeProperties) {
		getPathList().clear();

		for (String path : robocodeProperties.getOptionsDevelopmentPaths()) {
			getPathList().add(path);
		}
		for (String excludedPath : robocodeProperties.getOptionsExcludedDevelopmentPaths()) {
			getPathList().setChecked(excludedPath, false);
		}
		updateRemoveButton();
	}

	public void storePreferences() {
		properties.setOptionsDevelopmentPaths(getPathList().getAll());
		properties.setOptionsExcludedDevelopmentPaths(getPathList().getUnchecked());
		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	private void handleAddAction() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(optionsPanel) == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getAbsolutePath();

			if (!getPathList().contains(path)) {
				getPathList().add(path);
				getPathList().sort();
			}
		}
		updateRemoveButton();
	}

	private void handleRemoveAction() {
		int[] indices = getPathList().getSelectedIndices();

		for (int i = indices.length - 1; i >= 0; i--) {
			getPathList().remove(indices[i]);
		}
		updateRemoveButton();
	}
	
	private void updateRemoveButton() {
		getRemoveButton().setEnabled(getPathList().getSelectedIndex() >= 0);
	}
}
