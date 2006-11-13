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
 *     - Changed to have static access for all methods
 *     Luis Crespo & Flemming N. Larsen
 *     - Added showRankingDialog()
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

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

	private static RobocodeEditor robocodeEditor;
	private static RobotPackager robotPackager;
	private static RobotExtractor robotExtractor;
	private static RobocodeFrame robocodeFrame;
	private static TeamCreator teamCreator;
	private static RankingDialog rankingDialog;

	public static void setRobocodeFrame(RobocodeFrame newRobocodeFrame) {
		robocodeFrame = newRobocodeFrame;
	}

	public static RobocodeFrame getRobocodeFrame() {
		if (robocodeFrame == null) {
			// Create the frame
			robocodeFrame = new RobocodeFrame();
		}
		return robocodeFrame;
	}

	public static void showRobocodeFrame() {
		// Pack frame to size all components
		Utils.packCenterShow(getRobocodeFrame());
	
		// Do it yet again to fix a bug in some JREs
		robocodeFrame.setVisible(true);

		Utils.setStatusLabel(getRobocodeFrame().getStatusLabel());
	}

	public static void showAboutBox() {
		AboutBox aboutBox = new AboutBox(robocodeFrame);

		Utils.packCenterShow(robocodeFrame, aboutBox);
	}

	public static void showBattleOpenDialog() {
		BattleManager.pauseBattle();

		JFileChooser chooser = new JFileChooser(BattleManager.getBattlePath());
		
		chooser.setFileFilter(new FileFilter() {
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

			public String getDescription() {
				return "Battles";
			}
		});

		if (chooser.showOpenDialog(robocodeFrame) == JFileChooser.APPROVE_OPTION) {
			BattleManager.setBattleFilename(chooser.getSelectedFile().getPath());
			BattleManager.loadBattleProperties();
			showNewBattleDialog(BattleManager.getBattleProperties());
		}

		BattleManager.resumeBattle();	
	}

	public static void showVersionsTxt() {
		showInBrowser(
				"file://" + new File(Constants.cwd(), "").getAbsoluteFile() + System.getProperty("file.separator")
				+ "versions.txt");
	}

	public static void showHelpApi() {
		showInBrowser(
				"file://" + new File(Constants.cwd(), "").getAbsoluteFile() + System.getProperty("file.separator") + "javadoc"
				+ System.getProperty("file.separator") + "index.html");
	}

	public static void showFaq() {
		showInBrowser("http://robocode.sourceforge.net/help/robocode.faq.txt");
	}

	public static void showOnlineHelp() {
		showInBrowser("http://robocode.sourceforge.net/help");
	}

	public static void showRobocodeHome() {
		showInBrowser("http://robocode.sourceforge.net");
	}

	public static void showRoboWiki() {
		showInBrowser("http://robowiki.net");
	}

	public static void showYahooGroupRobocode() {
		showInBrowser("http://groups.yahoo.com/group/robocode");
	}

	public static void showRobocodeRepository() {
		showInBrowser("http://robocoderepository.com");
	}

	public static void showOptionsPreferences() {
		BattleManager.pauseBattle();
	
		Utils.packCenterShow(robocodeFrame, new PreferencesDialog());
	}

	public static void showResultsDialog(Battle battle) {
		ResultsDialog resultsDialog = new ResultsDialog(robocodeFrame, battle);

		resultsDialog.setSize(0, 0);
		Utils.packCenterShow(robocodeFrame, resultsDialog);
	}

	public static void showRankingDialog(boolean visible) {
		if (rankingDialog == null) {
			rankingDialog = new RankingDialog(robocodeFrame);
			rankingDialog.setSize(200, 300);
			Utils.centerShow(robocodeFrame, rankingDialog);
		}
		rankingDialog.setVisible(visible);
	}

	public static void showRobocodeEditor() {
		if (robocodeEditor == null) {
			robocodeEditor = new robocode.editor.RobocodeEditor();
			// Pack, center, and show it
			Utils.packCenterShow(robocodeEditor);
		} else {
			robocodeEditor.setVisible(true);
		}
	}

	public static void showRobotPackager() {
		if (robotPackager != null) {
			robotPackager.dispose();
			robotPackager = null;
		}
		
		robotPackager = new RobotPackager(false);
		// Pack, center, and show it
		Utils.packCenterShow(robotPackager);
	}

	public static void showRobotExtractor(JFrame owner) {
		if (robotExtractor != null) {
			robotExtractor.dispose();
			robotExtractor = null;
		}
		
		robotExtractor = new RobotExtractor(owner);
		// Pack, center, and show it
		Utils.packCenterShow(robotExtractor);
	}

	public static void showSplashScreen() {
		// Create the splash screen 
		SplashScreen splashScreen = new SplashScreen();

		// Pack, center, and show it
		Utils.packCenterShow(splashScreen);
		for (int i = 0; i < 5 * 20 && !splashScreen.isPainted(); i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {}
		}

		ImageManager.getNumExplosions();
		
		Utils.setStatusLabel(splashScreen.getSplashLabel());

		RobotRepositoryManager.getRobotRepository();

		Utils.setStatusLabel(splashScreen.getSplashLabel());
		CpuManager.getCpuConstant();

		Utils.setStatus("");
		Utils.setStatusLabel(null);

		splashScreen.dispose();
	}

	public static void showNewBattleDialog(BattleProperties battleProperties) {
		BattleManager.pauseBattle();
	
		NewBattleDialog newBattleDialog = new NewBattleDialog(battleProperties);

		// Pack, center, and show it
		Utils.packCenterShow(robocodeFrame, newBattleDialog);
	}

	public static boolean closeRobocodeEditor() {
		if (robocodeEditor == null) {
			return true;
		}

		if (!robocodeEditor.isVisible()) {
			return true;
		}

		return robocodeEditor.close();
	}

	public static void showCreateTeamDialog() {
		teamCreator = new TeamCreator();
		Utils.packCenterShow(teamCreator);
	}

	public static void showImportRobotDialog() {
		JFileChooser chooser = new JFileChooser();
		
		chooser.setFileFilter(new FileFilter() {
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

			public String getDescription() {
				return "Jar Files";
			}
		});

		chooser.setDialogTitle("Select the robot .jar file to copy to " + RobotRepositoryManager.getRobotsDirectory());

		if (chooser.showDialog(getRobocodeFrame(), "Import") == JFileChooser.APPROVE_OPTION) {
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
			File outputFile = new File(RobotRepositoryManager.getRobotsDirectory(), fileName);

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
					RobotRepositoryManager.clearRobotList();
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Robot imported successfully.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRobocodeFrame(), "Import failed: " + e);
				}
			}
		}
	}

	/**
	 * Shows a web page using the browser RobocodeManager.
	 * 
	 * @param url
	 *        The URL of the web page
	 */
	private static void showInBrowser(String url) {
		try {
			BrowserManager.openURL(url);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(robocodeFrame, e.getMessage(), "Unable to open browser!",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void showSaveResultsDialog() {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {

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

			public String getDescription() {
				return "Comma Separated Value (CSV) File Format";
			}
		});

		chooser.setDialogTitle("Save battle results");

		if (chooser.showSaveDialog(getRobocodeFrame()) == JFileChooser.APPROVE_OPTION) {
			BattleResultsTableModel tableModel = new BattleResultsTableModel(BattleManager.getBattle());

			String filename = chooser.getSelectedFile().getPath();

			if (!filename.endsWith(".csv")) {
				filename += ".csv";
			}

			boolean append = RobocodeProperties.getOptionsCommonAppendWhenSavingResults();

			tableModel.saveToFile(filename, append);
		}
	}
}
