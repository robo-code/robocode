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
 *     Flemming N. Larsen
 *     - Added showInBrowser() for displaying content from an URL
 *     - Added showRoboWiki(), showYahooGroupRobocode(), showRobocodeRepository()
 *     - Removed the Thread.sleep(diff) from showSplashScreen()
 *     Luis Crespo & Flemming N. Larsen
 *     - Added showRankingDialog()
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import javax.swing.*;

import robocode.dialog.*;
import robocode.battle.*;
import robocode.editor.*;
import robocode.packager.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen, Luis Crespo (current)
 */
public class WindowManager {
	
	private RobocodeEditor robocodeEditor;
	private RobotPackager robotPackager;
	private RobotExtractor robotExtractor;
	private RobocodeFrame robocodeFrame;
	private RobocodeManager manager;
	private TeamCreator teamCreator;
	private RankingDialog rankingDialog;

	public WindowManager(RobocodeManager manager) {
		this.manager = manager;
	}
	
	public void setRobocodeFrame(RobocodeFrame newRobocodeFrame) {
		robocodeFrame = newRobocodeFrame;
	}

	public RobocodeFrame getRobocodeFrame() {
		if (robocodeFrame == null) {
			// Create the frame
			robocodeFrame = new RobocodeFrame(manager);
		}
		return robocodeFrame;
	}

	public void showRobocodeFrame() {
		// Pack frame to size all components
		Utils.packCenterShow(getRobocodeFrame());
	
		// Do it yet again to fix a bug in some JREs
		robocodeFrame.setVisible(true);

		Utils.setStatusLabel(getRobocodeFrame().getStatusLabel());
	}

	public void showAboutBox() {
		AboutBox aboutBox = new AboutBox(robocodeFrame, manager);

		Utils.packCenterShow(robocodeFrame, aboutBox);
	}

	public void showBattleOpenDialog() {
		manager.getBattleManager().pauseBattle();
		File f = new File(manager.getBattleManager().getBattlePath());

		JFileChooser chooser;

		chooser = new JFileChooser(f);
		
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".battle")) {
					return true;
				}
				return false;
			}

			public String getDescription() {
				return "Battles";
			}
		};

		chooser.setFileFilter(filter);
		int rv = chooser.showOpenDialog(robocodeFrame);

		if (rv == JFileChooser.APPROVE_OPTION) {
			manager.getBattleManager().setBattleFilename(chooser.getSelectedFile().getPath());
			manager.getBattleManager().loadBattleProperties();
			showNewBattleDialog(manager.getBattleManager().getBattleProperties());
		}

		manager.getBattleManager().resumeBattle();	
	}

	public void showVersionsTxt() {
		String url = "file://" + new File(Constants.cwd(), "").getAbsoluteFile() + System.getProperty("file.separator")
				+ "versions.txt";
		
		showInBrowser(url);
	}

	public void showHelpApi() {
		String url = "file://" + new File(Constants.cwd(), "").getAbsoluteFile() + System.getProperty("file.separator")
				+ "javadoc" + System.getProperty("file.separator") + "index.html";
		
		showInBrowser(url);
	}

	public void showFaq() {
		showInBrowser("http://robocode.sourceforge.net/help/robocode.faq.txt");
	}

	public void showOnlineHelp() {
		showInBrowser("http://robocode.sourceforge.net/help");
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
		Utils.packCenterShow(robocodeFrame, preferencesDialog);
	}

	public void showResultsDialog(Battle battle) {
		ResultsDialog resultsDialog = new ResultsDialog(robocodeFrame, battle);

		resultsDialog.setSize(0, 0);
		Utils.packCenterShow(robocodeFrame, resultsDialog);
	}

	public void showRankingDialog(boolean visible) {
		if (rankingDialog == null) {
			rankingDialog = new RankingDialog(robocodeFrame, manager);
			rankingDialog.setSize(200, 300);
			Utils.centerShow(robocodeFrame, rankingDialog);
		}
		rankingDialog.setVisible(visible);
	}

	public void showRobocodeEditor() {
		if (robocodeEditor == null) {
			robocodeEditor = new robocode.editor.RobocodeEditor(manager);
			// Pack, center, and show it
			Utils.packCenterShow(robocodeEditor);
		} else {
			robocodeEditor.setVisible(true);
		}
	}

	public void showRobotPackager() {
		if (robotPackager != null) {
			robotPackager.dispose();
			robotPackager = null;
		}
		
		robotPackager = new robocode.packager.RobotPackager(manager.getRobotRepositoryManager(), false);
		// Pack, center, and show it
		Utils.packCenterShow(robotPackager);
	}

	public void showRobotExtractor(JFrame owner) {
		if (robotExtractor != null) {
			robotExtractor.dispose();
			robotExtractor = null;
		}
		
		robotExtractor = new robocode.dialog.RobotExtractor(owner, manager.getRobotRepositoryManager());
		// Pack, center, and show it
		Utils.packCenterShow(robotExtractor);
	}

	public void showSplashScreen() {
		// Create the splash screen 
		SplashScreen splashScreen = new SplashScreen(manager);

		// Pack, center, and show it
		Utils.packCenterShow(splashScreen);
		for (int i = 0; i < 5 * 20 && !splashScreen.isPainted(); i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {}
		}
		Utils.setStatusLabel(splashScreen.getSplashLabel());

		manager.getRobotRepositoryManager().getRobotRepository();

		Utils.setStatusLabel(splashScreen.getSplashLabel());
		manager.getImageManager();
		manager.getCpuManager().getCpuConstant();

		Utils.setStatus("");
		Utils.setStatusLabel(null);

		splashScreen.dispose();
	}

	public void showNewBattleDialog(BattleProperties battleProperties) {
		manager.getBattleManager().pauseBattle();
	
		NewBattleDialog newBattleDialog = new NewBattleDialog(manager, battleProperties);

		// Pack, center, and show it
		Utils.packCenterShow(robocodeFrame, newBattleDialog);
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
		teamCreator = new robocode.dialog.TeamCreator(manager.getRobotRepositoryManager());
		Utils.packCenterShow(teamCreator);
	}

	public void showImportRobotDialog() {
		JFileChooser chooser;

		chooser = new JFileChooser();
		
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();

				if (fn.equals("robocode.jar")) {
					return false;
				}
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".jar")) {
					return true;
				}
				if (extension.equalsIgnoreCase(".zip")) {
					return true;
				}
				return false;
			}

			public String getDescription() {
				return "Jar Files";
			}
		};

		chooser.setFileFilter(filter);
		chooser.setDialogTitle(
				"Select the robot .jar file to copy to " + manager.getRobotRepositoryManager().getRobotsDirectory());
		int rv = chooser.showDialog(getRobocodeFrame(), "Import");

		if (rv == JFileChooser.APPROVE_OPTION) {
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
				JOptionPane.showMessageDialog(getRobocodeFrame(),
						outputFile.getName() + " is already in the robots directory!");
				return;
			}
			if (outputFile.exists()) {
				if (JOptionPane.showConfirmDialog(getRobocodeFrame(), outputFile + " already exists.  Overwrite?",
						"Warning", JOptionPane.YES_NO_OPTION)
						== JOptionPane.NO_OPTION) {
					return;
				}
			}
			if (JOptionPane.showConfirmDialog(getRobocodeFrame(),
					"Robocode will now copy " + inputFile.getName() + " to " + outputFile.getParent(), "Import robot",
					JOptionPane.OK_CANCEL_OPTION)
					== JOptionPane.OK_OPTION) {
				try {
					Utils.copy(inputFile, outputFile);
					manager.getRobotRepositoryManager().clearRobotList();
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Robot imported successfully.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Import failed: " + e);
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
			manager.getBrowserManager().openURL(url);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame, e.getMessage(), "Unable to open browser!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
