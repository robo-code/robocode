/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten
 *     Ruben Moreno Montoliu
 *     - Added list of paths and buttons for adding and removing directories
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.settings.ISettingsManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;


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
	private JList pathList;

	public final ISettingsManager properties;

	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener, ListSelectionListener {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == getAddButton()) {
				JFileChooser chooser = new JFileChooser();

				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chooser.showOpenDialog(optionsPanel) == JFileChooser.APPROVE_OPTION) {
					((DefaultListModel) getPathList().getModel()).addElement(chooser.getSelectedFile().getAbsolutePath());
				}
			} else if (e.getSource() == getRemoveButton()) {
				int index = getPathList().getSelectedIndex();

				if (index >= 0) {
					((DefaultListModel) getPathList().getModel()).remove(index);
					getRemoveButton().setEnabled(getPathList().getSelectedIndex() >= 0);
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			getRemoveButton().setEnabled(getPathList().getSelectedIndex() >= 0);
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

			c.insets = new Insets(5, 5, 5, 5);
			c.anchor = GridBagConstraints.NORTHWEST;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.weightx = 0;

			optionsPanel.add(
					new JLabel(
							"If you are using an external IDE to develop robots, you may enter the classpath to those robots here."),
							c);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridy = 1;
			c.insets = new Insets(3, 3, 3, 3);
			optionsPanel.add(getAddButton(), c);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 1;
			c.gridy = 2;
			c.insets = new Insets(3, 3, 3, 3);
			optionsPanel.add(getRemoveButton(), c);

			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 1;
			c.gridy = 1;
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

	private JList getPathList() {
		if (pathList == null) {
			pathList = new JList(new DefaultListModel());
			pathList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			pathList.setLayoutOrientation(JList.VERTICAL);
			pathList.setVisibleRowCount(-1);
			pathList.addListSelectionListener(eventHandler);
		}
		return pathList;
	}

	private void loadPreferences(ISettingsManager robocodeProperties) {
		((DefaultListModel) getPathList().getModel()).clear();
		StringTokenizer tokenizer = new StringTokenizer(robocodeProperties.getOptionsDevelopmentPath(),
				File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			((DefaultListModel) getPathList().getModel()).addElement(tokenizer.nextToken());
		}
		getRemoveButton().setEnabled(getPathList().getSelectedIndex() >= 0);
	}

	public void storePreferences() {
		String path = "";

		for (int i = 0; i < ((DefaultListModel) getPathList().getModel()).getSize(); i++) {
			path += ((DefaultListModel) getPathList().getModel()).getElementAt(i) + File.pathSeparator;
		}
		properties.setOptionsDevelopmentPath(path);
		properties.saveProperties();
	}

	@Override
	public boolean isReady() {
		return true;
	}
}
