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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to tabs
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
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class PreferencesDialog extends JDialog implements WizardListener {
	private JPanel preferencesDialogContentPane;
	private WizardTabbedPane tabbedPane;
	private WizardController buttonsPanel;
	private PreferencesViewOptionsTab viewOptionsTab;
	private PreferencesDevelopmentOptionsTab developmentOptionsTab;

	EventHandler eventHandler = new EventHandler();

	public RobocodeManager manager;

	class EventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (e.getSource() == PreferencesDialog.this) {
				manager.getBattleManager().resumeBattle();
			}
		}
	}

	/**
	 * PreferencesDialog constructor
	 */
	public PreferencesDialog(RobocodeManager manager) {
		super(manager.getWindowManager().getRobocodeFrame());
		this.manager = manager;
		initialize();
	}

	public void cancelButtonActionPerformed() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Return the buttonsPanel
	 * 
	 * @return JPanel
	 */
	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getTabbedPane().getWizardController();
		}
		return buttonsPanel;
	}

	/**
	 * Return the preferencesDialogContentPane
	 * 
	 * @return JPanel
	 */
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

	/**
	 * Return the tabbedPane
	 * 
	 * @return JTabbedPane
	 */
	private WizardTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new WizardTabbedPane(this);
			tabbedPane.insertTab("View Options", null, getViewOptionsTab(), null, 0);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_V);
			tabbedPane.setDisplayedMnemonicIndexAt(0, 0);
			tabbedPane.insertTab("Development Options", null, getDevelopmentOptionsTab(), null, 1);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_D);
			tabbedPane.setDisplayedMnemonicIndexAt(1, 0);
		}
		return tabbedPane;
	}

	/**
	 * Return the viewOptionsTab
	 * 
	 * @return JPanel
	 */
	private JPanel getViewOptionsTab() {
		if (viewOptionsTab == null) {
			viewOptionsTab = new PreferencesViewOptionsTab(manager);
		}
		return viewOptionsTab;
	}

	private JPanel getDevelopmentOptionsTab() {
		if (developmentOptionsTab == null) {
			developmentOptionsTab = new PreferencesDevelopmentOptionsTab(manager);
		}
		return developmentOptionsTab;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Preferences");
		setContentPane(getPreferencesDialogContentPane());
		addWindowListener(eventHandler);
	}

	public void finishButtonActionPerformed() {
		viewOptionsTab.storePreferences();
		developmentOptionsTab.storePreferences();
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
