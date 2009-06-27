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
 *     Matthew Reeder
 *     - Minor changes for UI keyboard accessibility
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Renamed 'enum' variables to allow compiling with Java 1.5
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Moved the NoDuplicateJarOutputStream into the robocode.io package
 *     - Added codesize information using the new outputSizeClass() method
 *     - Added missing close() on FileInputStreams and FileOutputStreams
 *     - Changed the F5 key press for refreshing the list of available robots
 *       into 'modifier key' + R to comply with other OSes like e.g. Mac OS
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.packager;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.ui.dialog.*;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


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
		File f = new File(jarFilename);

		if (f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					jarFilename + " already exists.  Are you sure you want to replace it?", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION || ok == JOptionPane.CANCEL_OPTION) {
				Logger.logMessage("Cancelled by user.");
				return;
			}
			Logger.logMessage("Overwriting " + jarFilename);
		}

		String w = getPackagerOptionsPanel().getWebpageField().getText();
		URL web = null;

		if (w.length() > 0) {
			try {
				web = new URL(w);
			} catch (MalformedURLException e) {
				try {
					web = new URL("http://" + w);
					getPackagerOptionsPanel().getWebpageField().setText(web.toString());
				} catch (MalformedURLException ignored) {}
			}
		}
		final String desc = getPackagerOptionsPanel().getDescriptionArea().getText();
		final String autor = getPackagerOptionsPanel().getAuthorField().getText();
		final String version = getPackagerOptionsPanel().getVersionField().getText();
		final boolean source = getPackagerOptionsPanel().getIncludeSource().isSelected();
		final java.util.List<IRepositoryItem> robots = getRobotSelectionPanel().getSelectedRobots();

		final String res = repositoryManager.createPackage(f, web, desc, autor, version, source, robots);
		ConsoleDialog d = new ConsoleDialog(windowManager.getRobocodeFrame(), "Packaging results", false);

		d.setText(res);
		d.pack();
		WindowUtil.packCenterShow(this, d);
		dispose();
	}
}
