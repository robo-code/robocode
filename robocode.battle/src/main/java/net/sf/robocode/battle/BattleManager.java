/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.core.Container;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import net.sf.robocode.recording.BattlePlayer;
import net.sf.robocode.recording.IRecordManager;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import robocode.Event;
import robocode.control.BattleSpecification;
import robocode.control.RandomFactory;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;
import robocode.control.events.BattlePausedEvent;
import robocode.control.events.BattleResumedEvent;
import robocode.control.events.IBattleListener;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Pavel Savara (contributor)
 */
public class BattleManager implements IBattleManager {
	private final ISettingsManager properties;
	private final IHostManager hostManager;
	private final ICpuManager cpuManager;
	private final IRecordManager recordManager;
	private final IRepositoryManager repositoryManager;

	private volatile IBattle battle;
	private Thread battleThread;
	private BattleProperties battleProperties;

	private final BattleEventDispatcher battleEventDispatcher;

	private String battleFilename;
	private String battlePath;

	private int pauseCount = 0;
	private final AtomicBoolean isManagedTPS = new AtomicBoolean(false);

	public BattleManager(ISettingsManager properties, IRepositoryManager repositoryManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher battleEventDispatcher, IRecordManager recordManager) { // NO_UCD (unused code)
		this.properties = properties;
		this.recordManager = recordManager;
		this.repositoryManager = repositoryManager;
		this.cpuManager = cpuManager;
		this.hostManager = hostManager;
		this.battleEventDispatcher = battleEventDispatcher;
		Logger.setLogListener(battleEventDispatcher);
		battleProperties = new BattleProperties(properties);
	}

	public synchronized void cleanup() {
		if (battle != null) {
			battle.waitTillOver();
			battle.cleanup();
		}
		battle = null;
	}

	// Called when starting a new battle from GUI
	public void startNewBattle(BattleProperties battleProperties, boolean waitTillOver, boolean enableCLIRecording) {
		this.battleProperties = battleProperties;
		final RobotSpecification[] robots = repositoryManager.loadSelectedRobots(battleProperties.getSelectedRobots());

		startNewBattleImpl(robots, waitTillOver, enableCLIRecording);
	}

	// Called from the RobocodeEngine
	public void startNewBattle(BattleSpecification spec, String initialPositions, boolean waitTillOver, boolean enableCLIRecording) {
		battleProperties = new BattleProperties();
		battleProperties.setBattlefieldWidth(spec.getBattlefield().getWidth());
		battleProperties.setBattlefieldHeight(spec.getBattlefield().getHeight());
		battleProperties.setGunCoolingRate(spec.getGunCoolingRate());
		battleProperties.setInactivityTime(spec.getInactivityTime());
		battleProperties.setNumRounds(spec.getNumRounds());
		battleProperties.setHideEnemyNames(spec.getHideEnemyNames());
		battleProperties.setSentryBorderSize(spec.getSentryBorderSize());
		battleProperties.setSelectedRobots(spec.getRobots());

		final RobotSetup[] initialSetups = spec.getInitialSetups();
		if (initialSetups != null) {
			battleProperties.setInitialPositions(initialSetups);
		} else {
			battleProperties.setInitialPositions(initialPositions);
		}

		final RobotSpecification[] robots = repositoryManager.loadSelectedRobots(spec.getRobots());

		startNewBattleImpl(robots, waitTillOver, enableCLIRecording);
	}

	private void startNewBattleImpl(RobotSpecification[] battlingRobotsList, boolean waitTillOver, boolean enableCLIRecording) {
		stop(true);

		logMessage("Preparing battle...");

		final boolean recording = (properties.getOptionsCommonEnableReplayRecording()
				&& System.getProperty("TESTING", "none").equals("none"))
						|| enableCLIRecording;

		if (recording) {
			recordManager.attachRecorder(battleEventDispatcher);
		} else {
			recordManager.detachRecorder();
		}

		// resets seed for deterministic behavior of Random
		final String seed = System.getProperty("RANDOMSEED", "none");

		if (!seed.equals("none")) {
			// init soon as it reads random
			cpuManager.getCpuConstant();

			RandomFactory.resetDeterministic(Long.valueOf(seed));
		}

		Battle realBattle = Container.createComponent(Battle.class);
		realBattle.setup(battlingRobotsList, battleProperties, isPaused());

		battle = realBattle;

		battleThread = new Thread(Thread.currentThread().getThreadGroup(), realBattle);
		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("Battle Thread");
		realBattle.setBattleThread(battleThread);

		if (RobocodeProperties.isSecurityOn()) {
			hostManager.addSafeThread(battleThread);
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

	public void waitTillOver() {
		if (battle != null) {
			battle.waitTillOver();
		}
	}

	private void replayBattle() {
		if (!recordManager.hasRecord()) {
			return;
		}
		logMessage("Preparing replay...");

		if (battle != null && battle.isRunning()) {
			battle.stop(true);
		}

		Logger.setLogListener(battleEventDispatcher);

		recordManager.detachRecorder();
		battle = Container.createComponent(BattlePlayer.class);

		Thread battleThread = new Thread(Thread.currentThread().getThreadGroup(), battle);

		battleThread.setPriority(Thread.NORM_PRIORITY);
		battleThread.setName("BattlePlayer Thread");

		// Start the battlePlayer thread
		battleThread.start();
	}

	public String getBattleFilename() {
		return battleFilename;
	}

	public void setBattleFilename(String newBattleFilename) {
		if (newBattleFilename != null) {
			battleFilename = newBattleFilename.replace((File.separatorChar == '/') ? '\\' : '/', File.separatorChar);

			if (battleFilename.indexOf(File.separatorChar) < 0) {
				try {
					battleFilename = FileUtil.getBattlesDir().getCanonicalPath() + File.separatorChar + battleFilename;
				} catch (IOException ignore) {}
			}
			if (!battleFilename.endsWith(".battle")) {
				battleFilename += ".battle";
			}
		}
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
			FileUtil.cleanupStream(out);
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
			logError("Error while reading " + getBattleFilename() + ": " + e);
		} finally {
			FileUtil.cleanupStream(in);
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
		if (hostManager != null && battleThread != null) {
			hostManager.removeSafeThread(battleThread);
		}
		battleThread = null;
	}

	public synchronized void restart() {
		// Start new battle. The old battle is automatically stopped
		startNewBattle(battleProperties, false, false);
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

	public synchronized void prevTurn() {
		if (battle != null && battle.isRunning() && battle instanceof BattlePlayer) {
			((BattlePlayer) battle).stepBack();
		}
	}

	public synchronized void killRobot(int robotIndex) {
		if (battle != null && battle.isRunning() && battle instanceof Battle) {
			((Battle) battle).killRobot(robotIndex);
		}
	}

	public synchronized void setPaintEnabled(int robotIndex, boolean enable) {
		if (battle != null && battle.isRunning()) {
			battle.setPaintEnabled(robotIndex, enable);
		}
	}

	public synchronized void setSGPaintEnabled(int robotIndex, boolean enable) {
		if (battle != null && battle.isRunning() && battle instanceof Battle) {
			((Battle) battle).setSGPaintEnabled(robotIndex, enable);
		}
	}

	public synchronized void sendInteractiveEvent(Event event) {
		if (battle != null && battle.isRunning() && !isPaused() && battle instanceof Battle) {
			((Battle) battle).sendInteractiveEvent(event);
		}
	}
}
