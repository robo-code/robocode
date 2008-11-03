/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Refactoring
 *******************************************************************************/
package robocode.battle;


import robocode.BattleRules;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.events.BattlePausedEvent;
import robocode.battle.events.BattleResumedEvent;
import robocode.common.Command;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.manager.RobocodeManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Pavel Savara (refactoring)
 */
public abstract class BaseBattle implements IBattle, Runnable {

	// Maximum turns to display the battle when battle ended
	private final static int TURNS_DISPLAYED_AFTER_ENDING = 30;
	private final static int MAX_TPS = 10000;

	// Objects we use
	protected Thread battleThread;
	protected IBattleManager battleManager;
	protected final BattleEventDispatcher eventDispatcher;
	protected RobocodeManager manager;

	// rules
	protected BattleRules battleRules;

	// Current round items
	private int roundNum;
	private int currentTime;
	private int endTimer;

	// TPS (turns per second) calculation stuff
	private int tps;
	private long turnStartTime;
	private long measuredTurnStartTime;
	private int measuredTurnCounter;

	// Battle state
	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	private boolean isAborted;

	// Battle control
	private boolean isPaused;
	private int stepCount;
	private boolean roundOver;
	private Queue<Command> pendingCommands = new ConcurrentLinkedQueue<Command>();

	protected BaseBattle(RobocodeManager manager, BattleEventDispatcher eventDispatcher, boolean paused) {
		isPaused = paused;
		stepCount = 0;

		this.manager = manager;
		this.eventDispatcher = eventDispatcher;

		battleManager = manager.getBattleManager();
	}

	protected int getEndTimer() {
		return endTimer;
	}

	protected boolean isPaused() {
		return isPaused;
	}

	public void setBattleThread(Thread newBattleThread) {
		battleThread = newBattleThread;
	}

	/**
	 * Sets the roundNum.
	 *
	 * @param roundNum The roundNum to set
	 */
	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	/**
	 * Gets the roundNum.
	 *
	 * @return Returns a int
	 */
	public int getRoundNum() {
		return roundNum;
	}

	public int getNumRounds() {
		return battleRules.getNumRounds();
	}

	public Thread getBattleThread() {
		return battleThread;
	}

	public int getTime() {
		return currentTime;
	}

	public boolean isLastRound() {
		return (roundNum + 1 == getNumRounds());
	}

	public int getTPS() {
		return tps;
	}

	/**
	 * Informs on whether the battle is running or not.
	 *
	 * @return true if the battle is running, false otherwise
	 */
	public boolean isRunning() {
		return isRunning.get();
	}

	/**
	 * Informs on whether the battle is aborted or not.
	 *
	 * @return true if the battle is aborted, false otherwise
	 */
	public boolean isAborted() {
		return isAborted;
	}

	public synchronized void cleanup() {
		battleRules = null;
		if (pendingCommands != null) {
			pendingCommands.clear();
			// don't pendingCommands = null;
		}
	}

	public void waitTillStarted() {
		synchronized (isRunning) {
			while (!isRunning.get()) {
				try {
					isRunning.wait();
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					return; // Break out
				}
			}
		}
	}

	public void waitTillOver() {
		synchronized (isRunning) {
			while (isRunning.get()) {
				try {
					isRunning.wait();
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					return; // Break out
				}
			}
		}
	}

	/**
	 * When an object implementing interface {@code Runnable} is used
	 * to create a thread, starting the thread causes the object's
	 * {@code run()} method to be called in that separately executing
	 * thread.
	 * <p/>
	 * The general contract of the method {@code run()} is that it may
	 * take any action whatsoever.
	 *
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			initializeBattle();

			while (!isAborted && roundNum < getNumRounds()) {
				try {

					preloadRound();

					initializeRound();

					runRound();

					finalizeRound();

					cleanupRound();

				} catch (Exception e) {
					e.printStackTrace();
					logError("Exception running a battle round: ", e);
					isAborted = true;
				}

				roundNum++;
			}

			finalizeBattle();

			cleanup();
		} catch (Throwable e) {
			logError("Exception running a battle: ", e);
		}
	}

	protected void initializeBattle() {
		roundNum = 0;

		// Notify that the battle is now running
		synchronized (isRunning) {
			isRunning.set(true);
			isRunning.notifyAll();
		}
	}

	protected void finalizeBattle() {
		// Notify that the battle is over
		synchronized (isRunning) {
			isRunning.set(false);
			isRunning.notifyAll();
		}
	}

	protected void preloadRound() {
		logMessage("----------------------");
		Logger.logMessage("Round " + (roundNum + 1) + " initializing..", false);
	}

	protected void initializeRound() {
		logMessage("");
		logMessage("Let the games begin!");

		roundOver = false;
		endTimer = 0;
		currentTime = 0;
	}

	private void runRound() {

		while (!roundOver) {
			processCommand();

			if (shouldPause() && !shouldStep()) {
				shortSleep();
				continue;
			}

			initializeTurn();

			runTurn();

			roundOver = isRoundOver();

			finalizeTurn();
		}
	}

	protected boolean isRoundOver() {
		return (endTimer > 5 * TURNS_DISPLAYED_AFTER_ENDING);
	}

	protected void finalizeRound() {}

	protected void cleanupRound() {
		logMessage("Round " + (roundNum + 1) + " cleaning up.");
	}

	protected void initializeTurn() {
		turnStartTime = System.nanoTime();
	}

	protected void runTurn() {
		currentTime++;
	}

	protected void shutdownTurn() {
		endTimer++;
	}

	protected void finalizeTurn() {
		synchronizeTPS();

		calculateTPS();
	}

	private void synchronizeTPS() {
		// Let the battle sleep is the GUI is enabled and is not minimized
		// in order to keep the desired TPS

		if (battleManager.isManagedTPS()) {
			long delay = 0;

			if (!isAborted() && endTimer < TURNS_DISPLAYED_AFTER_ENDING) {
				int desiredTPS = manager.getProperties().getOptionsBattleDesiredTPS();
				if (desiredTPS < MAX_TPS){
					long deltaTime = System.nanoTime() - turnStartTime;
					delay = Math.max(1000000000 / desiredTPS - deltaTime, 0);
				}
			}
			if (delay > 500000) { // sleep granularity is worse than 500000
				try {
					Thread.sleep(delay / 1000000, (int) (delay % 1000000));
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void calculateTPS() {
		// Calculate the current turns per second (TPS)

		if (measuredTurnCounter++ == 0) {
			measuredTurnStartTime = turnStartTime;
		}

		long deltaTime = System.nanoTime() - measuredTurnStartTime;

		if (deltaTime / 500000000 >= 1) {
			tps = (int) (measuredTurnCounter * 1000000000L / deltaTime);
			measuredTurnCounter = 0;
		}
	}

	private boolean shouldPause() {
		return (isPaused && !isAborted);
	}

	private boolean shouldStep() {
		if (stepCount > 0) {
			stepCount--;
			return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------
	// Processing and maintaining robot and battle controls
	// --------------------------------------------------------------------------

	protected void sendCommand(Command command) {
		pendingCommands.add(command);
	}

	private void processCommand() {
		Command command = pendingCommands.poll();

		while (command != null) {
			try {
				command.execute();
			} catch (Exception e) {
				logError(e);
			}
			command = pendingCommands.poll();
		}
	}

	public void stop(boolean waitTillEnd) {
		sendCommand(new AbortCommand());

		if (waitTillEnd) {
			waitTillOver();
		}
	}

	public void pause() {
		sendCommand(new PauseCommand());
	}

	public void resume() {
		sendCommand(new ResumeCommand());
	}

	public void step() {
		sendCommand(new StepCommand());
	}

	private class PauseCommand extends Command {
		public void execute() {
			isPaused = true;
			stepCount = 0;
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}


	private class ResumeCommand extends Command {
		public void execute() {
			isPaused = false;
			stepCount = 0;
			eventDispatcher.onBattleResumed(new BattleResumedEvent());
		}
	}


	private class StepCommand extends Command {
		public void execute() {
			if (isPaused) {
				stepCount++;
			}
		}
	}


	private class AbortCommand extends Command {
		public void execute() {
			isAborted = true;
		}
	}

	//
	// Utility
	//

	private void shortSleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}
	}

	public void printSystemThreads() {
		Thread systemThreads[] = new Thread[256];

		battleThread.getThreadGroup().enumerate(systemThreads, false);

		logMessage("Threads: ------------------------");
		for (Thread thread : systemThreads) {
			if (thread != null) {
				logError(thread.getName());
			}
		}
	}
}
