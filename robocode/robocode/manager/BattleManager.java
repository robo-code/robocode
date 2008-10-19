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
 *     - Code cleanup & optimizations
 *     - Removed getBattleView().setDoubleBuffered(false) as BufferStrategy is
 *       used now
 *     - Replaced FileSpecificationVector, RobotPeerVector, and
 *       RobotClassManagerVector with plain Vector
 *     - Added check for if GUI is enabled before using graphical components
 *     - Added restart() method
 *     - Ported to Java 5
 *     - Added support for the replay feature
 *     - Removed the clearBattleProperties()
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Added PauseResumeListener interface, addListener(), removeListener(),
 *       notifyBattlePaused(), notifyBattleResumed() for letting listeners
 *       receive notifications when the game is paused or resumed
 *     - Added missing functionality in to support team battles in
 *       startNewBattle(BattleSpecification spec, boolean replay)
 *     - Added missing close() on FileInputStreams and FileOutputStreams
 *     - isPaused() is now synchronized
 *     - Extended sendResultsToListener() to handle teams as well as robots
 *     - Added setDefaultBattleProperties() for resetting battle properties
 *     - Removed the showResultsDialog parameter from the stop() method
 *     - Added null pointer check to the sendResultsToListener() method
 *     - Enhanced the getBattleFilename() to look into the battle dir and also
 *       add the .battle file extension to the returned file name if this is
 *       missing
 *     - Removed battleRunning field, isBattleRunning(), and setBattle()
 *     - Bugfix: Multiple battle threads could run in the same time when the
 *       battle thread was started in startNewBattle()
 *     Luis Crespo
 *     - Added debug step feature, including the nextTurn(), shouldStep(),
 *       startNewRound()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup() to prevent memory leaks by removing circular
 *       references
 *******************************************************************************/
package robocode.manager;


import robocode.Event;
import robocode.recording.BattlePlayer;
import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.battle.IBattleManager;
import robocode.battle.IBattle;
import robocode.battle.events.*;
import robocode.control.BattleSpecification;
import robocode.control.RandomFactory;
import robocode.control.RobotSpecification;
import robocode.io.FileUtil;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.peer.TeamPeer;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.FileSpecification;
import robocode.repository.RobotFileSpecification;
import robocode.repository.TeamSpecification;
import robocode.security.RobocodeSecurityManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class BattleManager implements IBattleManager {
	private RobocodeManager manager;

	private IBattle battle;
	private BattleProperties battleProperties = new BattleProperties();

	private BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();

	private String battleFilename;
	private String battlePath;

	private String recordFilename;

	private int pauseCount = 0;
	private final AtomicBoolean isManagedTPS = new AtomicBoolean(false);

	public BattleManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public synchronized void cleanup() {
		if (battle != null) {
			battle.cleanup();
			battle = null;
		}
		manager = null;
		battleEventDispatcher = null;
	}

	// Called when starting a new battle from GUI
	public boolean startNewBattle(BattleProperties battleProperties, boolean waitTillOver) {
		this.battleProperties = battleProperties;

		List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
				false, false, false, false, false, false);

		List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

		if (battleProperties.getSelectedRobots() != null) {
			StringTokenizer tokenizer = new StringTokenizer(battleProperties.getSelectedRobots(), ",");

			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, null);

				if (failed) {
					return false;
				}
			}
		}

		startNewBattleImpl(battlingRobotsList, waitTillOver);
		return true;
	}

	// Called from the RobocodeEngine
	public boolean startNewBattle(BattleSpecification spec, boolean waitTillOver) {
		battleProperties = new BattleProperties();
		battleProperties.setBattlefieldWidth(spec.getBattlefield().getWidth());
		battleProperties.setBattlefieldHeight(spec.getBattlefield().getHeight());
		battleProperties.setGunCoolingRate(spec.getGunCoolingRate());
		battleProperties.setInactivityTime(spec.getInactivityTime());
		battleProperties.setNumRounds(spec.getNumRounds());
		battleProperties.setSelectedRobots(spec.getRobots());

		List<FileSpecification> robotSpecificationsList = manager.getRobotRepositoryManager().getRobotRepository().getRobotSpecificationsList(
				false, false, false, false, false, false);
		List<RobotClassManager> battlingRobotsList = Collections.synchronizedList(new ArrayList<RobotClassManager>());

		for (robocode.control.RobotSpecification battleRobotSpec : spec.getRobots()) {
			if (battleRobotSpec == null) {
				break;
			}

			String bot = battleRobotSpec.getClassName();

			if (!(battleRobotSpec.getVersion() == null || battleRobotSpec.getVersion().length() == 0)) {
				bot += ' ' + battleRobotSpec.getVersion();
			}

			boolean failed = loadRobot(robotSpecificationsList, battlingRobotsList, bot, battleRobotSpec);

			if (failed) {
				return false;
			}
		}
		startNewBattleImpl(battlingRobotsList, waitTillOver);
		return true;
	}

	private boolean loadRobot(List<FileSpecification> robotSpecificationsList, List<RobotClassManager> battlingRobotsList, String bot, RobotSpecification battleRobotSpec) {
		boolean found = false;

		for (FileSpecification fileSpec : robotSpecificationsList) {
			if (fileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
				if (fileSpec instanceof RobotFileSpecification) {
					RobotClassManager rcm = new RobotClassManager((RobotFileSpecification) fileSpec);

					if (battleRobotSpec != null) {
						rcm.setControlRobotSpecification(battleRobotSpec);
					}
					battlingRobotsList.add(rcm);
					found = true;
					break;
				} else if (fileSpec instanceof TeamSpecification) {
					TeamSpecification currentTeam = (TeamSpecification) fileSpec;
					TeamPeer teamManager = new TeamPeer(currentTeam.getName(), currentTeam.getVersion());

					StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

					while (teamTokenizer.hasMoreTokens()) {
						bot = teamTokenizer.nextToken();
						RobotFileSpecification match = null;

						for (FileSpecification teamFileSpec : robotSpecificationsList) {
							// Teams cannot include teams
							if (teamFileSpec instanceof TeamSpecification) {
								continue;
							}
							if (teamFileSpec.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
								// Found team member
								match = (RobotFileSpecification) teamFileSpec;
								if (currentTeam.getRootDir().equals(teamFileSpec.getRootDir())
										|| currentTeam.getRootDir().equals(teamFileSpec.getRootDir().getParentFile())) {
									found = true;
									break;
								}
								// else, still looking
							}
						}
						RobotClassManager rcm = new RobotClassManager(match, teamManager);

						if (battleRobotSpec != null) {
							rcm.setControlRobotSpecification(battleRobotSpec);
						}
						battlingRobotsList.add(rcm);
					}
					break;
				}
			}
		}

		if (!found) {
			logError("Aborting battle, could not find robot: " + bot);
			this.battleEventDispatcher.onBattleEnded(new BattleEndedEvent(true));
			return true;
		}
		return false;
	}

	private void startNewBattleImpl(List<RobotClassManager> battlingRobotsList, boolean waitTillOver) {

		logMessage("Preparing battle...");
		if (battle != null && battle.isRunning()) { // TODO is that good way ? should we rather throw exception here when realBattle is running ?
			battle.stop(true);
		}

		Logger.setLogListener(battleEventDispatcher);

		if (manager.isSoundEnabled()) {
			manager.getSoundManager().setBattleEventDispatcher(battleEventDispatcher);
		}

		manager.getBattleRecorder().setBattleEventDispatcher(battleEventDispatcher);

		// resets seed for deterministic behavior of Random
		final String seed = System.getProperty("RANDOMSEED", "none");

		if (!seed.equals("none")) {
			RandomFactory.resetDeterministic(Long.valueOf(seed));
		}

		Battle realBattle = new Battle(manager, battleEventDispatcher, isPaused());

		battle = realBattle;

		// Set stuff the view needs to know
		realBattle.setProperties(battleProperties);

		Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), realBattle);

		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("Battle Thread");
		realBattle.setBattleThread(battleThread);

		if (!System.getProperty("NOSECURITY", "false").equals("true")) {
			((RobocodeSecurityManager) System.getSecurityManager()).addSafeThread(battleThread);
			((RobocodeSecurityManager) System.getSecurityManager()).setBattleThread(battleThread);
		}

		for (RobotClassManager robotClassMgr : battlingRobotsList) {
			realBattle.addRobot(robotClassMgr);
		}

		// Start the realBattle thread
		battleThread.start();

		// Wait until the realBattle is running and ended.
		// This must be done as a new realBattle could be started immediately after this one causing
		// multiple realBattle threads to run at the same time, which must be prevented!
		realBattle.waitTillStarted();
		if (waitTillOver) {
			realBattle.waitTillOver();
		}
	}

	private void replayBattle() {

		logMessage("Preparing replay...");
		if (battle != null && battle.isRunning()) { // TODO is that good way ? should we rather throw exception here when battlePlayer is running ?
			battle.stop(true);
		}

		Logger.setLogListener(battleEventDispatcher);

		if (manager.isSoundEnabled()) {
			manager.getSoundManager().setBattleEventDispatcher(battleEventDispatcher);
		}

		BattlePlayer battlePlayer = new BattlePlayer(manager, battleEventDispatcher);

		if (recordFilename == null) {
			try {
				File tmpFile = File.createTempFile("robocode-replay", ".tmp");

				tmpFile.deleteOnExit();

				recordFilename = tmpFile.getAbsolutePath();

				manager.getBattleRecorder().saveRecord(recordFilename);

			} catch (IOException e) {
				logError(e);
				return;
			}
		}

		battlePlayer.loadRecord(recordFilename);

		battle = battlePlayer;

		Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battlePlayer);

		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("BattlePlayer Thread");

		// Start the battlePlayer thread
		battleThread.start();
	}

	public String getBattleFilename() {
		String filename = battleFilename;

		if (filename != null) {
			if (filename.indexOf(File.separatorChar) < 0) {
				filename = FileUtil.getBattlesDir().getName() + File.separatorChar + filename;
			}
			if (!filename.endsWith(".battle")) {
				filename += ".battle";
			}
		}
		return filename;
	}

	public void setBattleFilename(String newBattleFilename) {
		battleFilename = newBattleFilename;
	}

	public void setRecordFilename(String newRecordFilename) {
		recordFilename = newRecordFilename;
	}

	public String getBattlePath() {
		if (battlePath == null) {
			battlePath = System.getProperty("BATTLEPATH");
			if (battlePath == null) {
				battlePath = "battles";
			}
			battlePath = new File(FileUtil.getCwd(), battlePath).getAbsolutePath();
		}
		return battlePath;
	}

	public void saveBattleProperties() {
		if (battleProperties == null) {
			logError("Cannot save null battle properties");
			return;
		}
		if (battleFilename == null) {
			logError("Cannot save battle to null path, use setBattleFilename()");
			return;
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(battleFilename);

			battleProperties.store(out, "Battle Properties");
		} catch (IOException e) {
			logError("IO Exception saving battle properties: " + e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
			}
		}
	}

	public BattleProperties loadBattleProperties() {
		BattleProperties res = new BattleProperties();
		FileInputStream in = null;

		try {
			in = new FileInputStream(getBattleFilename());
			res.load(in);
		} catch (FileNotFoundException e) {
			logError("No file " + battleFilename + " found, using defaults.");
		} catch (IOException e) {
			logError("IO Exception reading " + getBattleFilename() + ": " + e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
			}
		}
		return res;
	}

	public BattleProperties getBattleProperties() {
		if (battleProperties == null) {
			battleProperties = new BattleProperties();
		}
		return battleProperties;
	}

	public void setDefaultBattleProperties() {
		battleProperties = new BattleProperties();
	}

	public boolean isManagedTPS() {
		return isManagedTPS.get();
	}

	public void setManagedTPS(boolean value) {
		isManagedTPS.set(value);
	}

	public synchronized void addListener(IBattleListener listener) {
		battleEventDispatcher.addListener(listener);
	}

	public synchronized void removeListener(IBattleListener listener) {
		battleEventDispatcher.removeListener(listener);
	}

	public synchronized void stop(boolean waitTillEnd) {
		if (battle != null && battle.isRunning()) {
			battle.stop(waitTillEnd);
		}
	}

	public synchronized void restart() {
		// Start new battle. The old battle is automatically stopped
		startNewBattle(battleProperties, false);
	}

	public synchronized void replay() {
		replayBattle();
	}

	private boolean isPaused() {
		return (pauseCount != 0);
	}

	public synchronized void togglePauseResumeBattle() {
		if (isPaused()) {
			resumeBattle();
		} else {
			pauseBattle();
		}
	}

	public synchronized void pauseBattle() {
		if (++pauseCount == 1) {
			if (battle != null && battle.isRunning()) {
				battle.pause();
			} else {
				battleEventDispatcher.onBattlePaused(new BattlePausedEvent());
			}
		}
	}

	public synchronized void pauseIfResumedBattle() {
		if (pauseCount == 0) {
			pauseCount++;
			if (battle != null && battle.isRunning()) {
				battle.pause();
			} else {
				battleEventDispatcher.onBattlePaused(new BattlePausedEvent());
			}
		}
	}

	public synchronized void resumeIfPausedBattle() {
		if (pauseCount == 1) {
			pauseCount--;
			if (battle != null && battle.isRunning()) {
				battle.resume();
			} else {
				battleEventDispatcher.onBattleResumed(new BattleResumedEvent());
			}
		}
	}

	public synchronized void resumeBattle() {
		if (--pauseCount < 0) {
			pauseCount = 0;
			logError("SYSTEM: pause game bug!");
		} else if (pauseCount == 0) {
			if (battle != null && battle.isRunning()) {
				battle.resume();
			} else {
				battleEventDispatcher.onBattleResumed(new BattleResumedEvent());
			}
		}
	}

	/**
	 * Steps for a single turn, then goes back to paused
	 */
	public synchronized void nextTurn() {
		if (battle != null && battle.isRunning()) {
			battle.step();
		}
	}

	public synchronized void killRobot(int robotIndex) {
		if (battle != null && battle.isRunning() && Battle.class.isAssignableFrom(battle.getClass())) {
			((Battle) battle).killRobot(robotIndex);
		}
	}

	public synchronized void setPaintEnabled(int robotIndex, boolean enable) {
		if (battle != null && battle.isRunning() && Battle.class.isAssignableFrom(battle.getClass())) {
			((Battle) battle).setPaintEnabled(robotIndex, enable);
		}
	}

	public synchronized void setSGPaintEnabled(int robotIndex, boolean enable) {
		if (battle != null && battle.isRunning() && Battle.class.isAssignableFrom(battle.getClass())) {
			((Battle) battle).setSGPaintEnabled(robotIndex, enable);
		}
	}

	public synchronized void sendInteractiveEvent(Event event) {
		if (battle != null && battle.isRunning() && !isPaused() && Battle.class.isAssignableFrom(battle.getClass())) {
			((Battle) battle).sendInteractiveEvent(event);
		}
	}
}
