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
import net.sf.robocode.ui.IWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends JDialog implements WizardListener {

	private JPanel preferencesDialogContentPane;
	private WizardTabbedPane tabbedPane;
	private WizardController buttonsPanel;

	private PreferencesCommonOptionsTab commonOptionsTab;
	private PreferencesDevelopmentOptionsTab developmentOptionsTab;
	private PreferencesViewOptionsTab viewOptionsTab;
	private PreferencesRenderingOptionsTab renderingOptionsTab;
	private PreferencesSoundOptionsTab soundOptionsTab;

	private final ISettingsManager properties;
	private final IImageManager imageManager;

	public PreferencesDialog(ISettingsManager properties, IWindowManager windowManager, IImageManager imageManager) {
		super(windowManager.getRobocodeFrame(), true);
		this.properties = properties;
		this.imageManager = imageManager;
		initialize();
	}

	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Preferences");
		setContentPane(getPreferencesDialogContentPane());
	}

	public void cancelButtonActionPerformed() {
		dispose();
	}

	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getTabbedPane().getWizardController();
		}
		return buttonsPanel;
	}

	private JPanel getPreferencesDialogContentPane() {
		getButtonsPanel();
		if (preferencesDialogContentPane == null) {
			preferencesDialogContentPane = new JPanel();
			preferencesDialogContentPane.setLayout(new BorderLayout());
			preferencesDialogContentPane.add(getTabbedPane(), BorderLayout.CENTER);
			preferencesDialogContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
		}
		return preferencesDialogContentPane;
	}

	private WizardTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new WizardTabbedPane(this);

			int tabIndex = 0;

			tabbedPane.insertTab("Common Options", null, getCommonOptionsTab(), null, tabIndex);
			tabbedPane.setMnemonicAt(tabIndex, KeyEvent.VK_O);
			tabbedPane.setDisplayedMnemonicIndexAt(tabIndex++, 1);

			tabbedPane.insertTab("Development Options", null, getDevelopmentOptionsTab(), null, tabIndex);
			tabbedPane.setMnemonicAt(tabIndex, KeyEvent.VK_D);
			tabbedPane.setDisplayedMnemonicIndexAt(tabIndex++, 0);

			tabbedPane.insertTab("View Options", null, getViewOptionsTab(), null, tabIndex);
			tabbedPane.setMnemonicAt(tabIndex, KeyEvent.VK_W);
			tabbedPane.setDisplayedMnemonicIndexAt(tabIndex++, 3);

			tabbedPane.insertTab("Rendering Options", null, getRenderingOptionsTab(), null, tabIndex);
			tabbedPane.setMnemonicAt(tabIndex, KeyEvent.VK_R);
			tabbedPane.setDisplayedMnemonicIndexAt(tabIndex++, 0);

			tabbedPane.insertTab("Sound Options", null, getSoundOptionsTab(), null, tabIndex);
			tabbedPane.setMnemonicAt(tabIndex, KeyEvent.VK_S);
			tabbedPane.setDisplayedMnemonicIndexAt(tabIndex++, 0);
		}
		return tabbedPane;
	}

	private JPanel getCommonOptionsTab() {
		if (commonOptionsTab == null) {
			commonOptionsTab = new PreferencesCommonOptionsTab(properties);
		}
		return commonOptionsTab;
	}

	private JPanel getDevelopmentOptionsTab() {
		if (developmentOptionsTab == null) {
			developmentOptionsTab = new PreferencesDevelopmentOptionsTab(properties);
		}
		return developmentOptionsTab;
	}

	private JPanel getViewOptionsTab() {
		if (viewOptionsTab == null) {
			viewOptionsTab = new PreferencesViewOptionsTab(properties);
		}
		return viewOptionsTab;
	}

	private JPanel getRenderingOptionsTab() {
		if (renderingOptionsTab == null) {
			renderingOptionsTab = new PreferencesRenderingOptionsTab(properties, imageManager);
		}
		return renderingOptionsTab;
	}

	private JPanel getSoundOptionsTab() {
		if (soundOptionsTab == null) {
			soundOptionsTab = new PreferencesSoundOptionsTab(properties);
		}
		return soundOptionsTab;
	}

	public void finishButtonActionPerformed() {
		commonOptionsTab.storePreferences();
		developmentOptionsTab.storePreferences();
		viewOptionsTab.storePreferences();
		renderingOptionsTab.storePreferences();
		soundOptionsTab.storePreferences();

		dispose();
	}
}
