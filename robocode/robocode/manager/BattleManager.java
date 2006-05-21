/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.manager;


import java.io.*;
import java.util.*;
import javax.swing.*;

import robocode.util.*;
import robocode.battle.*;
import robocode.battlefield.*;
import robocode.repository.*;
import robocode.peer.RobotPeer;
import robocode.peer.robot.*;
import robocode.security.RobocodeSecurityManager;
import robocode.peer.*;


public class BattleManager {
	private BattleProperties battleProperties = new BattleProperties();
	private String battleFilename = null;
	private java.lang.String battlePath = null;
	private Battle battle = null;
	private boolean battleRunning = false;
	private int pauseCount = 0;
	private java.lang.String resultsFile = null;
	private RobocodeManager manager = null;
	
	Vector bugFixLoaderCache = null;

	private void log(String s) {
		Utils.log(s);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	public BattleManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public void stop(boolean showResultsDialog) {
		if (getBattle() != null) {
			getBattle().stop(showResultsDialog);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 5:52:37 PM)
	 * @param battleProperties java.util.Properties
	 */
	public void startNewBattle(BattleProperties battleProperties, boolean exitOnComplete) {
		this.battleProperties = battleProperties;

		FileSpecificationVector robotSpecificationsVector = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsVector(
				false, false, false, false, false, false);
		RobotClassManagerVector battlingRobotsVector = new RobotClassManagerVector();
	
		StringTokenizer tokenizer;

		if (battleProperties.getSelectedRobots() != null) {
			tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");
			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				for (int i = 0; i < robotSpecificationsVector.size(); i++) {
					FileSpecification currentFileSpecification = (FileSpecification) robotSpecificationsVector.elementAt(
							i);

					if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
						if (currentFileSpecification instanceof RobotSpecification) {
							RobotSpecification current = (RobotSpecification) currentFileSpecification;

							battlingRobotsVector.add(new RobotClassManager(current));
							break;
						} else if (currentFileSpecification instanceof TeamSpecification) {
							TeamSpecification currentTeam = (TeamSpecification) currentFileSpecification;
							TeamPeer teamManager = new TeamPeer(currentTeam.getName());
							StringTokenizer teamTokenizer;

							teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");
							while (teamTokenizer.hasMoreTokens()) {
								bot = teamTokenizer.nextToken();
								// log("Looking for: " + bot);
								RobotSpecification match = null;

								for (int j = 0; j < robotSpecificationsVector.size(); j++) {
									currentFileSpecification = (FileSpecification) robotSpecificationsVector.elementAt(j);
									// log("Looking at: " + currentFileSpecification.getName());
									// Teams cannot include teams
									if (currentFileSpecification instanceof TeamSpecification) {
										continue;
									}
									
									if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(
											bot)) {
									
										// log("Found " + currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion() + ", " + currentTeam.getRootDir() + " - " + currentFileSpecification.getRootDir());
										// Found team member
										match = (RobotSpecification) currentFileSpecification;
										if (currentTeam.getRootDir().equals(currentFileSpecification.getRootDir())
												|| currentTeam.getRootDir().equals(
														currentFileSpecification.getRootDir().getParentFile())) {
											// log("This is a match.");
											break;
										}
										// else
										// log("Still looking.");
									}
								}
								battlingRobotsVector.add(new RobotClassManager(match, teamManager));
							}
							break;
						}
					}
				}
			}
		}
		startNewBattle(battlingRobotsVector, exitOnComplete, null);
	}

	public void startNewBattle(robocode.control.BattleSpecification battleSpecification) {
		this.battleProperties = battleSpecification.getBattleProperties();
		FileSpecificationVector robotSpecificationsVector = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsVector(
				false, false, false, false, false, false);
		RobotClassManagerVector battlingRobotsVector = new RobotClassManagerVector();
	
		robocode.control.RobotSpecification[] robotSpecs = battleSpecification.getRobots();

		for (int i = 0; i < robotSpecs.length; i++) {
			if (robotSpecs[i] == null) {
				break;
			}
			
			String bot;

			if (robotSpecs[i].getVersion() != null && !robotSpecs[i].getVersion().equals("")) {
				bot = robotSpecs[i].getClassName() + " " + robotSpecs[i].getVersion();
			} else {
				bot = robotSpecs[i].getClassName();
			}
		
			boolean found = false;

			for (int j = 0; j < robotSpecificationsVector.size(); j++) {
				// log("compare" + ((FileSpecification)robotSpecificationsVector.elementAt(j)).getNameManager().getUniqueFullClassNameWithVersion() +
				// " to " + bot);
				if (((FileSpecification) robotSpecificationsVector.elementAt(j)).getNameManager().getUniqueFullClassNameWithVersion().equals(
						bot)) {
					RobotSpecification robotSpec = (RobotSpecification) robotSpecificationsVector.elementAt(j);
					RobotClassManager rcm = new RobotClassManager(robotSpec);

					rcm.setControlRobotSpecification(robotSpecs[i]);
					battlingRobotsVector.add(rcm);
					found = true;
					break;
				}
			}
			if (!found) {
				log("Aborting battle, could not find robot: " + bot);
				if (manager.getListener() != null) {
					manager.getListener().battleAborted(battleSpecification);
				}
				return;
			}
		}
		startNewBattle(battlingRobotsVector, false, battleSpecification);
	}

	private void startNewBattle(RobotClassManagerVector battlingRobotsVector, boolean exitOnComplete, robocode.control.BattleSpecification battleSpecification) {

		/*
		 * if (battlingRobotsVector.size() <= 0)
		 {
		 log("no selected robots");
		 return;
		 }
		 */

		log("Preparing battle..."); 
	  
		if (battle != null) {
			bugFixLoaderCache = new Vector();
			for (int i = 0; i < battle.getRobots().size(); i++) {// bugFixLoaderCache.add(battle.getRobots().elementAt(i).getRobotClassManager().getRobotClassLoader());
			}
			battle.stop();
		}

		BattleField battleField = new ShapesBattleField(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight());

		manager.getWindowManager().getRobocodeFrame().getBattleView().setBattleField(battleField);
		battle = new Battle(manager.getWindowManager().getRobocodeFrame().getBattleView(), battleField, manager);
		battle.setExitOnComplete(exitOnComplete);
	
		// Only used when controlled by RobocodeEngine
		battle.setBattleSpecification(battleSpecification);

		// Set stuff the view needs to know
		battle.setProperties(battleProperties);

		Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("Battle Thread");
		battle.setBattleThread(battleThread);

		if (!System.getProperty("NOSECURITY", "false").equals("true")) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThread(battleThread);
			((RobocodeSecurityManager) System.getSecurityManager()).setBattleThread(battleThread);
		}
	
		manager.getWindowManager().getRobocodeFrame().getBattleView().setVisible(true);
		manager.getWindowManager().getRobocodeFrame().getBattleView().setInitialized(false);

		if (manager.getListener() == null && exitOnComplete == false) {
			manager.getWindowManager().getRobocodeFrame().sizeToPreferred();
		}

		for (int i = 0; i < battlingRobotsVector.size(); i++) {
			battle.addRobot((RobotClassManager) battlingRobotsVector.elementAt(i));
		}

		manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(true);
		manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(true);

		if (pauseCount == 0 && isBattleRunning()) {
			manager.getWindowManager().getRobocodeFrame().getBattleView().setDoubleBuffered(false);
		}

		if (manager.getWindowManager().getRobocodeFrame().getPauseResumeButton().getText().equals("Resume")) {
			manager.getWindowManager().getRobocodeFrame().pauseResumeButtonActionPerformed();
		}
		
		manager.getRobotDialogManager().setActiveBattle(battle);
		battleThread.start();
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 11:51:54 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getBattleFilename() {
		return battleFilename;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 11:51:54 AM)
	 * @param newBattleFilename java.lang.String
	 */
	public void setBattleFilename(java.lang.String newBattleFilename) {
		battleFilename = newBattleFilename;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/4/2001 2:03:55 PM)
	 * @return boolean
	 */
	public boolean isPaused() {
		return (pauseCount != 0);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/5/2001 12:23:36 PM)
	 */
	public void pauseBattle() {
		pauseCount++;
		if (pauseCount == 1) {
			manager.getWindowManager().getRobocodeFrame().getBattleView().setBattlePaused(true);
		}
		// RepaintManager.currentManager(robocodeFrame).setDoubleBufferingEnabled(true);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/24/2001 12:32:40 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getBattlePath() {
		if (battlePath == null) {
			battlePath = System.getProperty("BATTLEPATH");
			if (battlePath == null) {
				battlePath = "battles";
			}
			battlePath = new File(Constants.cwd(), battlePath).getAbsolutePath();
		}
		return battlePath;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:27:54 PM)
	 */
	public void saveBattle() {
		pauseBattle();
		saveBattleProperties();
		resumeBattle();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:29:52 PM)
	 */
	public void saveBattleAs() {

		pauseBattle();
		File f = new File(getBattlePath());

		JFileChooser chooser;

		chooser = new JFileChooser(f);
		
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return false;
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
		int rv = chooser.showSaveDialog(manager.getWindowManager().getRobocodeFrame());

		if (rv == JFileChooser.APPROVE_OPTION) {
			battleFilename = chooser.getSelectedFile().getPath();
			int idx = battleFilename.lastIndexOf('.');
			String extension = "";

			if (idx > 0) {
				extension = battleFilename.substring(idx);
			}
			if (!(extension.equalsIgnoreCase(".battle"))) {
				battleFilename += ".battle";
			}
			saveBattleProperties();
		}
		resumeBattle();

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:21:22 PM)
	 */
	public void saveBattleProperties() {
		if (battleProperties == null) {
			log("Cannot save null battle properties");
			return;
		}
		if (battleFilename == null) {
			saveBattleAs();
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(battleFilename);

			battleProperties.store(out, "Battle Properties");
		} catch (IOException e) {
			log("IO Exception saving battle properties: " + e);
		}
	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 3:43:41 PM)
	 */
	public void loadBattleProperties() {
		try {
			FileInputStream in = new FileInputStream(battleFilename);

			battleProperties.load(in);
		} catch (FileNotFoundException e) {
			log("No file " + battleFilename + " found, using defaults.");

		} catch (IOException e) {
			log("IO Exception reading " + battleFilename + ": " + e);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:15:33 PM)
	 * @return robocode.Battle
	 */
	public Battle getBattle() {
		return battle;
	}

	public void setOptions() {
		if (battle != null) {
			battle.setOptions();
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:16:41 PM)
	 * @return java.util.Properties
	 */
	public BattleProperties getBattleProperties() {
		if (battleProperties == null) {
			battleProperties = new BattleProperties();
		}
		return battleProperties;
	}

	public void clearBattleProperties() {
		battleProperties = null;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/5/2001 12:23:36 PM)
	 */
	public void resumeBattle() {
		// Resume is done after a short delay,
		// so that a user switching from menu to menu won't cause
		// a lot of flickering and/or "single frame" battle unpauses
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
				pauseCount--;
				if (pauseCount < 0) {
					pauseCount = 0;
				}
				if (pauseCount == 0 && isBattleRunning()) {
					manager.getWindowManager().getRobocodeFrame().getBattleView().setBattlePaused(false);
				}
				// RepaintManager.currentManager(robocodeFrame).setDoubleBufferingEnabled(false);
			}
		}).start();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/3/2001 8:07:00 PM)
	 * @return boolean
	 */
	public boolean isBattleRunning() {
		return battleRunning;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 2:15:33 PM)
	 * @param newBattle robocode.Battle
	 */
	public void setBattle(Battle newBattle) {
		battle = newBattle;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/3/2001 8:07:00 PM)
	 * @param newBattleRunning boolean
	 */
	public void setBattleRunning(boolean newBattleRunning) {
		battleRunning = newBattleRunning;
		if (pauseCount == 0) {
			manager.getWindowManager().getRobocodeFrame().getBattleView().setBattlePaused(!newBattleRunning);
		}
		// javax.swing.RepaintManager.currentManager(robocodeFrame).setDoubleBufferingEnabled(battleRunning);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:42:27 PM)
	 * @param newResultsFile java.lang.String
	 */
	public void setResultsFile(java.lang.String newResultsFile) {
		resultsFile = newResultsFile;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:42:27 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getResultsFile() {
		return resultsFile;
	}

	public void sendResultsToListener(Battle battle, robocode.control.RobocodeListener listener) {
		RobotPeerVector orderedRobots = (RobotPeerVector) battle.getRobots().clone();

		orderedRobots.sort(); // Collections.sort(orderedRobots);

		robocode.control.RobotResults results[] = new robocode.control.RobotResults[orderedRobots.size()];

		for (int i = 0; i < results.length; i++) {
			RobotStatistics stats = ((RobotPeer) orderedRobots.elementAt(i)).getRobotStatistics();

			results[i] = new robocode.control.RobotResults(
					((RobotPeer) orderedRobots.elementAt(i)).getRobotClassManager().getControlRobotSpecification(), (i + 1),
					(int) stats.getTotalScore(), (int) stats.getTotalSurvivalScore(), (int) stats.getTotalWinnerScore(),
					(int) stats.getTotalBulletDamageScore(), (int) stats.getTotalKilledEnemyBulletScore(),
					(int) stats.getTotalRammingDamageScore(), (int) stats.getTotalKilledEnemyRammingScore(),
					stats.getTotalFirsts(), stats.getTotalSeconds(), stats.getTotalThirds());
		}
		listener.battleComplete(battle.getBattleSpecification(), results);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 3:25:34 PM)
	 */
	public void printResultsData(Battle battle) {
	
		PrintStream out;
		boolean close = false;

		if (getResultsFile() == null) {
			out = System.out;
		} else {
			File f = new File(getResultsFile());

			try {
				out = new PrintStream(new FileOutputStream(f));
				close = true;
			} catch (IOException e) {
				log(e);
				return;
			}
		}

		BattleResultsTableModel resultsTable = new BattleResultsTableModel(battle);

		resultsTable.print(out);
		if (close) {
			out.close();
		}
	}

	/**
	 * Gets the manager.
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

}

