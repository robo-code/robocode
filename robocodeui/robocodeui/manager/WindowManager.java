/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added showInBrowser() for displaying content from an URL
 *     - Added showRoboWiki(), showYahooGroupRobocode(), showRobocodeRepository()
 *     - Removed the Thread.sleep(diff) from showSplashScreen()
 *     - Updated to use methods from the FileUtil, which replaces file operations
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Changed showRobocodeFrame() to take a visible parameter
 *     - Added packCenterShow() for windows where the window position and
 *       dimension should not be read or saved to window.properties
 *     Luis Crespo & Flemming N. Larsen
 *     - Added showRankingDialog()
 *******************************************************************************/
package robocodeui.manager;


import java.io.File;
import java.io.IOException;

import java.awt.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import robocode.battle.BattleProperties;
import robocode.battle.BattleResultsTableModel;
import robocodeui.dialog.*;
import robocodeui.dialog.SplashScreen;
import robocodeui.editor.RobocodeEditor;
import robocode.io.FileUtil;
import robocodeui.packager.RobotPackager;
import robocode.manager.RobocodeManager;
import robocode.manager.BattleManager;
import robocode.ui.IRobocodeFrame;
import robocode.ui.IWindowManager;
import robocode.Robocode;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 */
public class WindowManager implements IWindowManager {

	private RobocodeEditor robocodeEditor;
	private RobotPackager robotPackager;
	private RobotExtractor robotExtractor;
	private RobocodeFrame robocodeFrame;
	private RobocodeManager manager;
	private TeamCreator teamCreator;
	private RankingDialog rankingDialog;

	public WindowManager() {}

	public IRobocodeFrame getRobocodeFrame() {
		if (robocodeFrame == null) {
			// Create the frame
			robocodeFrame = new RobocodeFrame(manager);
		}
		return robocodeFrame;
	}

	public void showRobocodeFrame(boolean visible) {
		RobocodeFrame frame = (RobocodeFrame) getRobocodeFrame();

		if (visible) {
			// Pack frame to size all components
			WindowUtil.packCenterShow(frame);

			WindowUtil.setStatusLabel(frame.getStatusLabel());
		} else {
			frame.setVisible(false);
		}
	}

	public void showAboutBox() {
		packCenterShow(new AboutBox((Frame) getRobocodeFrame(), manager));
	}

	public void showBattleOpenDialog() {
        BattleManager battleManager = manager.getBattleManager();
		battleManager.pauseBattle();

		JFileChooser chooser = new JFileChooser(manager.getBattleManager().getBattlePath());

		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String filename = pathname.getName();
				int idx = filename.lastIndexOf('.');

				String extension = "";

				if (idx >= 0) {
					extension = filename.substring(idx);
				}
				if (extension.equalsIgnoreCase(".battle")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Battles";
			}
		});


		if (chooser.showOpenDialog((Component) getRobocodeFrame()) == JFileChooser.APPROVE_OPTION) {
			battleManager.setBattleFilename(chooser.getSelectedFile().getPath());
			battleManager.loadBattleProperties();
			showNewBattleDialog(battleManager.getBattleProperties());
		}

		battleManager.resumeBattle();
	}

	public void showVersionsTxt() {
		showInBrowser(
				"file://" + new File(FileUtil.getCwd(), "").getAbsoluteFile() + System.getProperty("file.separator")
				+ "versions.txt");
	}

	public void showHelpApi() {
		showInBrowser(
				"file://" + new File(FileUtil.getCwd(), "").getAbsoluteFile() + System.getProperty("file.separator")
				+ "javadoc" + System.getProperty("file.separator") + "index.html");
	}

	public void showFaq() {
		showInBrowser("http://robocode.sourceforge.net/help/robocode.faq.txt");
	}

	public void showOnlineHelp() {
		showInBrowser("http://robocode.sourceforge.net/help");
	}

	public void showJavaDocumentation() {
		showInBrowser("http://java.sun.com/j2se/1.5.0/docs");
	}

	public void showRobocodeHome() {
		showInBrowser("http://robocode.sourceforge.net");
	}

	public void showRoboWiki() {
		showInBrowser("http://robowiki.net");
	}

	public void showYahooGroupRobocode() {
		showInBrowser("http://groups.yahoo.com/group/robocode");
	}

	public void showRobocodeRepository() {
		showInBrowser("http://robocoderepository.com");
	}

	public void showOptionsPreferences() {
		manager.getBattleManager().pauseBattle();

		// Create the preferencesDialog
		PreferencesDialog preferencesDialog = new PreferencesDialog(manager);

		// Show it
		WindowUtil.packCenterShow((Window) getRobocodeFrame(), preferencesDialog);
	}

	public void showResultsDialog() {
		packCenterShow(new ResultsDialog(manager));
	}

	public void showRankingDialog(boolean visible) {
		if (rankingDialog == null) {
			rankingDialog = new RankingDialog(manager, true);
		}
		packCenterShow(rankingDialog);
	}

	public void showRobocodeEditor() {
		if (robocodeEditor == null) {
			robocodeEditor = new robocodeui.editor.RobocodeEditor(manager);
			// Pack, center, and show it
			WindowUtil.packCenterShow(robocodeEditor);
		} else {
			robocodeEditor.setVisible(true);
		}
	}

	public void showRobotPackager() {
		if (robotPackager != null) {
			robotPackager.dispose();
			robotPackager = null;
		}

		robotPackager = new robocodeui.packager.RobotPackager(manager.getRobotRepositoryManager(), false);
		// Pack, center, and show it
		WindowUtil.packCenterShow(robotPackager);
	}

	public void showRobotExtractor(JFrame owner) {
		if (robotExtractor != null) {
			robotExtractor.dispose();
			robotExtractor = null;
		}

		robotExtractor = new robocodeui.dialog.RobotExtractor(owner, manager.getRobotRepositoryManager());
		// Pack, center, and show it
		WindowUtil.packCenterShow(robotExtractor);
	}

	public void showSplashScreen() {
		// Create the splash screen
		SplashScreen splashScreen = new SplashScreen(manager);

		synchronized (splashScreen) {
			// Pack, center, and show it
			packCenterShow(splashScreen);

			try {
				splashScreen.wait(20000);
			} catch (InterruptedException e) {
				; // Do nothing
			}
		}

		WindowUtil.setStatusLabel(splashScreen.getSplashLabel());

		manager.getRobotRepositoryManager().getRobotRepository();

		WindowUtil.setStatusLabel(splashScreen.getSplashLabel());
		manager.getImageManager();
		manager.getCpuManager().getCpuConstant();

		WindowUtil.setStatus("");
		WindowUtil.setStatusLabel(null);

		splashScreen.dispose();
	}

	public void showNewBattleDialog(BattleProperties battleProperties) {
		manager.getBattleManager().pauseBattle();

		NewBattleDialog newBattleDialog = new NewBattleDialog(manager, battleProperties);

		// Pack, center, and show it
		WindowUtil.packCenterShow((Window) getRobocodeFrame(), newBattleDialog);
	}

	public boolean closeRobocodeEditor() {
		if (robocodeEditor == null) {
			return true;
		}

		if (!robocodeEditor.isVisible()) {
			return true;
		}

		return robocodeEditor.close();
	}

	/**
	 * Gets the manager.
	 *
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

	public void showCreateTeamDialog() {
		teamCreator = new robocodeui.dialog.TeamCreator(manager.getRobotRepositoryManager());
		WindowUtil.packCenterShow(teamCreator);
	}

	public void showImportRobotDialog() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String filename = pathname.getName();

				if (filename.equals("robocode.jar")) {
					return false;
				}
				int idx = filename.lastIndexOf('.');

				String extension = "";

				if (idx >= 0) {
					extension = filename.substring(idx);
				}
				if (extension.equalsIgnoreCase(".jar")) {
					return true;
				}
				if (extension.equalsIgnoreCase(".zip")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Jar Files";
			}
		});

		chooser.setDialogTitle(
				"Select the robot .jar file to copy to " + manager.getRobotRepositoryManager().getRobotsDirectory());

		if (chooser.showDialog((Component) getRobocodeFrame(), "Import") == JFileChooser.APPROVE_OPTION) {
			File inputFile = chooser.getSelectedFile();
			String fileName = chooser.getSelectedFile().getName();
			int idx = fileName.lastIndexOf('.');
			String extension = "";

			if (idx >= 0) {
				extension = fileName.substring(idx);
			}
			if (!extension.equalsIgnoreCase(".jar")) {
				fileName += ".jar";
			}
			File outputFile = new File(manager.getRobotRepositoryManager().getRobotsDirectory(), fileName);

			if (inputFile.equals(outputFile)) {
				JOptionPane.showMessageDialog((Component) getRobocodeFrame(),
						outputFile.getName() + " is already in the robots directory!");
				return;
			}
			if (outputFile.exists()) {
				if (JOptionPane.showConfirmDialog((Component) getRobocodeFrame(),
						outputFile + " already exists.  Overwrite?", "Warning", JOptionPane.YES_NO_OPTION)
						== JOptionPane.NO_OPTION) {
					return;
				}
			}
			if (JOptionPane.showConfirmDialog((Component) getRobocodeFrame(),
					"Robocode will now copy " + inputFile.getName() + " to " + outputFile.getParent(), "Import robot",
					JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION) {
				try {
					FileUtil.copy(inputFile, outputFile);
					manager.getRobotRepositoryManager().clearRobotList();
					JOptionPane.showMessageDialog((Component) getRobocodeFrame(), "Robot imported successfully.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog((Component) getRobocodeFrame(), "Import failed: " + e);
				}
			}
		}
	}

	/**
	 * Shows a web page using the browser manager.
	 *
	 * @param url
	 *        The URL of the web page
	 */
	private void showInBrowser(String url) {
		try {
			BrowserManager.openURL(url);
		} catch (IOException e) {
			JOptionPane.showMessageDialog((Component) getRobocodeFrame(), e.getMessage(), "Unable to open browser!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void showSaveResultsDialog() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String filename = pathname.getName();
				int idx = filename.lastIndexOf('.');

				String extension = "";

				if (idx >= 0) {
					extension = filename.substring(idx);
				}
				if (extension.equalsIgnoreCase(".csv")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Comma Separated Value (CSV) File Format";
			}
		});

		chooser.setDialogTitle("Save battle results");

		if (chooser.showSaveDialog((Component) getRobocodeFrame()) == JFileChooser.APPROVE_OPTION) {
			BattleResultsTableModel tableModel = new BattleResultsTableModel(manager.getBattleManager().getBattle());

			String filename = chooser.getSelectedFile().getPath();

			if (!filename.endsWith(".csv")) {
				filename += ".csv";
			}

			boolean append = manager.getProperties().getOptionsCommonAppendWhenSavingResults();

			tableModel.saveToFile(filename, append);
		}
	}

	/**
	 * Packs, centers, and shows the specified window on the screen.
	 */
	private void packCenterShow(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		window.pack();
		window.setLocation((screenSize.width - window.getWidth()) / 2, (screenSize.height - window.getHeight()) / 2);
		window.setVisible(true);
	}
    
	public boolean initializeDisplay(Robocode robocode) {
		setLookAndFeel();
        return true;
    }

    /**
     * Sets the Look and Feel (LAF). This method first try to set the LAF to the
     * system's LAF. If this fails, it try to use the cross platform LAF.
     * If this also fails, the LAF will not be changed.
     */
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			// For some reason Ubuntu 7 can cause a NullPointerException when trying to getting the LAF
			System.err.println("Could not set the Look and Feel (LAF).  The default LAF is used instead");
		}
	}

	public void setRobocodeManager(RobocodeManager robocodeManager) {
		manager = robocodeManager;
	}
}
