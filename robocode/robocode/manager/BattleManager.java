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
 *     - Removed getBattleView().setDoubleBuffered(false) as BufferStrategy is
 *       used now
 *     - Replaced FileSpecificationVector, RobotPeerVector, and
 *       RobotClassManagerVector with plain Vector
 *     - Added check for if GUI is enabled before using graphical components
 *     - Added restart() method
 *     - Ported to Java 5
 *     - Code cleanup & optimizations
 *     Luis Crespo
 *     - Added debug step feature, including the nextTurn(), shouldStep(),
 *       startNewRound()
 *******************************************************************************/
package robocode.manager;


import javax.swing.*;
import java.io.*;
import java.util.*;

import robocode.util.*;
import robocode.battle.*;
import robocode.battlefield.*;
import robocode.repository.*;
import robocode.peer.RobotPeer;
import robocode.peer.robot.*;
import robocode.security.RobocodeSecurityManager;
import robocode.peer.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class BattleManager {
	private BattleProperties battleProperties = new BattleProperties();
	private String battleFilename;
	private String battlePath;
	private Battle battle;
	private boolean battleRunning;
	private int pauseCount;
	private String resultsFile;
	private RobocodeManager manager;
	private int stepTurn;

	/**
	 * Steps for a single turn, then goes back to paused
	 */
	public void nextTurn() {
		if (battleRunning) {
			stepTurn = battle.getCurrentTime() + 1;
		}
	}
	
	/**
	 * If the battle is paused, this method determines if it should perform one turn and then stop again.
	 *
	 * @return true if the battle should perform one turn, false otherwise
	 */
	public boolean shouldStep() {
		// This code assumes it is called only if the battle is paused.
		return stepTurn > battle.getCurrentTime();
	}

	/**
	 * This method should be called to inform the battle manager that a new round is starting
	 */
	public void startNewRound() {
		stepTurn = 0;
	}

	public BattleManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public void stop(boolean showResultsDialog) {
		if (getBattle() != null) {
			getBattle().stop(showResultsDialog);
		}
	}

	public void restart() {
		stop(false);
		startNewBattle(battleProperties, false);
	}
	
	public void startNewBattle(BattleProperties battleProperties, boolean exitOnComplete) {
		this.battleProperties = battleProperties;

		Vector<FileSpecification> robotSpecificationsVector = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsVector(
				false, false, false, false, false, false);
		Vector<RobotClassManager> battlingRobotsVector = new Vector<RobotClassManager>(); 

		if (battleProperties.getSelectedRobots() != null) {
			StringTokenizer tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");

			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				for (FileSpecification fileSpec : robotSpecificationsVector) {
					if (fileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
						if (fileSpec instanceof RobotSpecification) {
							battlingRobotsVector.add(new RobotClassManager((RobotSpecification) fileSpec));
							break;
						} else if (fileSpec instanceof TeamSpecification) {
							TeamSpecification currentTeam = (TeamSpecification) fileSpec;
							TeamPeer teamManager = new TeamPeer(currentTeam.getName());

							StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

							while (teamTokenizer.hasMoreTokens()) {
								bot = teamTokenizer.nextToken();
								RobotSpecification match = null;

								for (FileSpecification teamFileSpec : robotSpecificationsVector) {
									// Teams cannot include teams
									if (teamFileSpec instanceof TeamSpecification) {
										continue;
									}
									if (teamFileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
										// Found team member
										match = (RobotSpecification) teamFileSpec;
										if (currentTeam.getRootDir().equals(teamFileSpec.getRootDir())
												|| currentTeam.getRootDir().equals(teamFileSpec.getRootDir().getParentFile())) {
											break;
										}
										// else, still looking
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
		Vector<FileSpecification> robotSpecificationsVector = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsVector(
				false, false, false, false, false, false);
		Vector<RobotClassManager> battlingRobotsVector = new Vector<RobotClassManager>(); 

		for (robocode.control.RobotSpecification battleRobotSpec : battleSpecification.getRobots()) {
			if (battleRobotSpec == null) {
				break;
			}

			String bot = battleRobotSpec.getClassName();

			if (!(battleRobotSpec.getVersion() == null || battleRobotSpec.getVersion().length() == 0)) {
				bot += ' ' + battleRobotSpec.getVersion();
			}

			boolean found = false;

			for (FileSpecification fileSpec : robotSpecificationsVector) {
				if (fileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
					RobotClassManager rcm = new RobotClassManager((RobotSpecification) fileSpec);

					rcm.setControlRobotSpecification(battleRobotSpec);
					battlingRobotsVector.add(rcm);
					found = true;
					break;
				}
			}
			if (!found) {
				Utils.log("Aborting battle, could not find robot: " + bot);
				if (manager.getListener() != null) {
					manager.getListener().battleAborted(battleSpecification);
				}
				return;
			}
		}
		startNewBattle(battlingRobotsVector, false, battleSpecification);
	}

	private void startNewBattle(Vector<RobotClassManager> battlingRobotsVector, boolean exitOnComplete,
			robocode.control.BattleSpecification battleSpecification) { 

		Utils.log("Preparing battle...");
		if (battle != null) {
			battle.stop();
		}

		BattleField battleField = new DefaultBattleField(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight());

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().getBattleView().setBattleField(battleField);			
		}
		battle = new Battle(battleField, manager);
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

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().getBattleView().setVisible(true);
			manager.getWindowManager().getRobocodeFrame().getBattleView().setInitialized(false);
		}

		for (RobotClassManager robotClassMgr : battlingRobotsVector) {
			battle.addRobot(robotClassMgr);
		}

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveAsMenuItem().setEnabled(true);
			manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getBattleSaveMenuItem().setEnabled(true);
	
			if (manager.getWindowManager().getRobocodeFrame().getPauseResumeButton().getText().equals("Resume")) {
				manager.getWindowManager().getRobocodeFrame().pauseResumeButtonActionPerformed();
			}

			manager.getRobotDialogManager().setActiveBattle(battle);
		}
		battleThread.start();
	}
	
	public String getBattleFilename() {
		return battleFilename;
	}

	public void setBattleFilename(String newBattleFilename) {
		battleFilename = newBattleFilename;
	}

	public boolean isPaused() {
		return (pauseCount != 0);
	}

	public void pauseBattle() {
		pauseCount++;
	}

	public String getBattlePath() {
		if (battlePath == null) {
			battlePath = System.getProperty("BATTLEPATH");
			if (battlePath == null) {
				battlePath = "battles";
			}
			battlePath = new File(Constants.cwd(), battlePath).getAbsolutePath();
		}
		return battlePath;
	}

	public void saveBattle() {
		pauseBattle();
		saveBattleProperties();
		resumeBattle();
	}

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

	public void saveBattleProperties() {
		if (battleProperties == null) {
			Utils.log("Cannot save null battle properties");
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
			Utils.log("IO Exception saving battle properties: " + e);
		}
	}

	public void loadBattleProperties() {
		try {
			FileInputStream in = new FileInputStream(battleFilename);

			getBattleProperties().load(in);
		} catch (FileNotFoundException e) {
			Utils.log("No file " + battleFilename + " found, using defaults.");
		} catch (IOException e) {
			Utils.log("IO Exception reading " + battleFilename + ": " + e);
		}
	}

	public Battle getBattle() {
		return battle;
	}

	public void setOptions() {
		if (battle != null) {
			battle.setOptions();
		}
	}

	public BattleProperties getBattleProperties() {
		if (battleProperties == null) {
			battleProperties = new BattleProperties();
		}
		return battleProperties;
	}

	public void clearBattleProperties() {
		battleProperties = null;
	}

	public void resumeBattle() {
		Math.max(--pauseCount, 0);
	}

	public boolean isBattleRunning() {
		return battleRunning;
	}

	public void setBattle(Battle newBattle) {
		battle = newBattle;
	}

	public void setBattleRunning(boolean newBattleRunning) {
		battleRunning = newBattleRunning;
	}

	public void setResultsFile(String newResultsFile) {
		resultsFile = newResultsFile;
	}

	public String getResultsFile() {
		return resultsFile;
	}

	public void sendResultsToListener(Battle battle, robocode.control.RobocodeListener listener) {
		Vector<RobotPeer> orderedRobots = new Vector<RobotPeer>(battle.getRobots());

		Collections.sort(orderedRobots);

		robocode.control.RobotResults results[] = new robocode.control.RobotResults[orderedRobots.size()];

		for (int i = 0; i < results.length; i++) {
			RobotStatistics stats = orderedRobots.elementAt(i).getRobotStatistics();

			results[i] = new robocode.control.RobotResults(
					orderedRobots.elementAt(i).getRobotClassManager().getControlRobotSpecification(), (i + 1),
					stats.getTotalScore(), stats.getTotalSurvivalScore(), stats.getTotalWinnerScore(),
					stats.getTotalBulletDamageScore(), stats.getTotalKilledEnemyBulletScore(),
					stats.getTotalRammingDamageScore(), stats.getTotalKilledEnemyRammingScore(), stats.getTotalFirsts(),
					stats.getTotalSeconds(), stats.getTotalThirds());
		}
		listener.battleComplete(battle.getBattleSpecification(), results);
	}

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
				Utils.log(e);
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
	 * 
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}
}
