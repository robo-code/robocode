/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.packager;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.RobotProperties;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.ui.dialog.*;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class RobotPackager extends JDialog implements WizardListener {

	private final int minRobots = 1;
	private final int maxRobots = 1; // 250;

	private JPanel robotPackagerContentPane;
	private WizardCardPanel wizardPanel;
	private WizardController buttonsPanel;
	private FilenamePanel filenamePanel;
	private ConfirmPanel confirmPanel;
	private RobotSelectionPanel robotSelectionPanel;
	private PackagerOptionsPanel packagerOptionsPanel;

	private final IRepositoryManager repositoryManager;
	private final IWindowManager windowManager;

	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(true);
			}
		}
	}

	public RobotPackager(IRepositoryManager repositoryManager, IWindowManager windowManager) {
		super(windowManager.getRobocodeFrame());
		this.repositoryManager = repositoryManager;
		this.windowManager = windowManager;
		initialize();
	}

	public void cancelButtonActionPerformed() {
		dispose();
	}

	/**
	 * Return the buttonsPanel
	 *
	 * @return JButton
	 */
	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getWizardPanel().getWizardController();
		}
		return buttonsPanel;
	}

	/**
	 * Return the buttonsPanel
	 *
	 * @return JButton
	 */
	private ConfirmPanel getConfirmPanel() {
		if (confirmPanel == null) {
			confirmPanel = new ConfirmPanel(this);
		}
		return confirmPanel;
	}

	/**
	 * Return the optionsPanel
	 *
	 * @return robocode.packager.PackagerOptionsPanel
	 */
	protected FilenamePanel getFilenamePanel() {
		if (filenamePanel == null) {
			filenamePanel = new FilenamePanel(this);
		}
		return filenamePanel;
	}

	/**
	 * Return the optionsPanel
	 *
	 * @return robocode.packager.PackagerOptionsPanel
	 */
	protected PackagerOptionsPanel getPackagerOptionsPanel() {
		if (packagerOptionsPanel == null) {
			packagerOptionsPanel = new PackagerOptionsPanel(this);
		}
		return packagerOptionsPanel;
	}

	/**
	 * Return the newBattleDialogContentPane
	 *
	 * @return JPanel
	 */
	private JPanel getRobotPackagerContentPane() {
		if (robotPackagerContentPane == null) {
			robotPackagerContentPane = new JPanel();
			robotPackagerContentPane.setLayout(new BorderLayout());
			robotPackagerContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
			robotPackagerContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Package!", 'P', 0);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK),
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_FOCUSED);
		}
		return robotPackagerContentPane;
	}

	/**
	 * Return the Page property value.
	 *
	 * @return JPanel
	 */
	public RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = net.sf.robocode.core.Container.createComponent(RobotSelectionPanel.class);
			robotSelectionPanel.setup(minRobots, maxRobots, false, "Select the robot or team you would like to package.", /* true */
					false,
					false, false/* true */, true, false, true, null);
		}
		return robotSelectionPanel;
	}

	/**
	 * Return the tabbedPane.
	 *
	 * @return JTabbedPane
	 */
	private WizardCardPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new WizardCardPanel(this);
			wizardPanel.add(getRobotSelectionPanel(), "Select robot");
			wizardPanel.add(getPackagerOptionsPanel(), "Select options");
			wizardPanel.add(getFilenamePanel(), "Select filename");
			wizardPanel.add(getConfirmPanel(), "Confirm");
		}
		return wizardPanel;
	}

	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Packager");
		setContentPane(getRobotPackagerContentPane());
	}

	public void finishButtonActionPerformed() {
		String jarFilename = getFilenamePanel().getFilenameField().getText();
		File jarFile = new File(jarFilename);

		if (jarFile.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					jarFilename + " already exists.  Are you sure you want to replace it?", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION || ok == JOptionPane.CANCEL_OPTION) {
				Logger.logMessage("Cancelled by user.");
				return;
			}
			Logger.logMessage("Overwriting " + jarFilename);
		}

		String webPageFieldString = getPackagerOptionsPanel().getWebpageField().getText();
		URL webPage = null;

		if (webPageFieldString.length() > 0) {
			try {
				webPage = new URL(webPageFieldString);
			} catch (MalformedURLException e) {
				try {
					webPage = new URL("http://" + webPageFieldString);
					getPackagerOptionsPanel().getWebpageField().setText(webPage.toString());
				} catch (MalformedURLException ignored) {}
			}
		}
		boolean includeSource = getPackagerOptionsPanel().getIncludeSource().isSelected();
		boolean includeData = getPackagerOptionsPanel().getIncludeData().isSelected();
		String version = getPackagerOptionsPanel().getVersionField().getText();
		String author = getPackagerOptionsPanel().getAuthorField().getText();
		String desc = getPackagerOptionsPanel().getDescriptionArea().getText();
		List<IRobotSpecItem> robots = getRobotSelectionPanel().getSelectedRobots();

		RobotProperties props = new RobotProperties();
		props.setIncludeSource(includeSource);
		props.setIncludeData(includeData);
		props.setVersion(version);
		props.setAuthor(author);
		props.setDescription(desc);
		props.setWebPage(webPage);

		String outputText = repositoryManager.createPackage(jarFile, robots, props);

		ConsoleDialog dialog = new ConsoleDialog(windowManager.getRobocodeFrame(), "Packaging results", false);
		dialog.setText(outputText);
		dialog.pack();
		WindowUtil.packCenterShow(this, dialog);
		dispose();
	}
}
