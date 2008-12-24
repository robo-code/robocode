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
 *     - Added Rendering Options tab
 *     - Added Sound Options tab
 *     - Added Common Options tab
 *     - Code cleanup
 *     Matthew Reeder
 *     - Added keyboard mnemonics to View Options and Development Options tabs
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.RobocodeManager;

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

	private PreferencesViewOptionsTab viewOptionsTab;
	private PreferencesRenderingOptionsTab renderingOptionsTab;
	private PreferencesSoundOptionsTab soundOptionsTab;
	private PreferencesDevelopmentOptionsTab developmentOptionsTab;
	private PreferencesCommonOptionsTab commonOptionsTab;

	private final RobocodeManager manager;

	public PreferencesDialog(RobocodeManager manager) {
		super(manager.getWindowManager().getRobocodeFrame(), true);
		this.manager = manager;
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

			tabbedPane.insertTab("View Options", null, getViewOptionsTab(), null, 0);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_W);
			tabbedPane.setDisplayedMnemonicIndexAt(0, 3);

			tabbedPane.insertTab("Rendering Options", null, getRenderingOptionsTab(), null, 1);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);
			tabbedPane.setDisplayedMnemonicIndexAt(1, 0);

			tabbedPane.insertTab("Sound Options", null, getSoundOptionsTab(), null, 2);
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_S);
			tabbedPane.setDisplayedMnemonicIndexAt(2, 0);

			tabbedPane.insertTab("Development Options", null, getDevelopmentOptionsTab(), null, 3);
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_D);
			tabbedPane.setDisplayedMnemonicIndexAt(3, 0);

			tabbedPane.insertTab("Common Options", null, getCommonOptionsTab(), null, 4);
			tabbedPane.setMnemonicAt(4, KeyEvent.VK_O);
			tabbedPane.setDisplayedMnemonicIndexAt(4, 1);
		}
		return tabbedPane;
	}

	private JPanel getViewOptionsTab() {
		if (viewOptionsTab == null) {
			viewOptionsTab = new PreferencesViewOptionsTab(manager);
		}
		return viewOptionsTab;
	}

	private JPanel getRenderingOptionsTab() {
		if (renderingOptionsTab == null) {
			renderingOptionsTab = new PreferencesRenderingOptionsTab(manager);
		}
		return renderingOptionsTab;
	}

	private JPanel getSoundOptionsTab() {
		if (soundOptionsTab == null) {
			soundOptionsTab = new PreferencesSoundOptionsTab(manager);
		}
		return soundOptionsTab;
	}

	private JPanel getDevelopmentOptionsTab() {
		if (developmentOptionsTab == null) {
			developmentOptionsTab = new PreferencesDevelopmentOptionsTab(manager);
		}
		return developmentOptionsTab;
	}

	private JPanel getCommonOptionsTab() {
		if (commonOptionsTab == null) {
			commonOptionsTab = new PreferencesCommonOptionsTab(manager);
		}
		return commonOptionsTab;
	}

	public void finishButtonActionPerformed() {
		viewOptionsTab.storePreferences();
		renderingOptionsTab.storePreferences();
		soundOptionsTab.storePreferences();
		developmentOptionsTab.storePreferences();
		commonOptionsTab.storePreferences();

		// Make sure the BattleView will use the new setting immediately
		//TODO ZAMO manager.getWindowManager().getRobocodeFrame().getBattleView().setDisplayOptions(); // TODO: Find better solution?

		dispose();
	}
}
