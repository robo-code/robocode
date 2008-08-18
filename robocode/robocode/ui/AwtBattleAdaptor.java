/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.ui;


import robocode.battle.IBattleManager;
import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Pavel Savara (original)
 */
public abstract class AwtBattleAdaptor extends BattleAdaptor {
	private AtomicReference<TurnSnapshot> snapshot;
	private TurnSnapshot lastSnapshot;

	private AtomicBoolean isRunning;
	private AtomicBoolean isPaused;
	private IBattleManager battleManager;

	private QueuedTask repaintTask = new QueuedTask();
	private Timer timerTask;
	private boolean skipSameFrames;

	// FPS (frames per second) calculation
	private int fps;
	private long measuredFrameCounter;
	private long measuredFrameStartTime;

	public AwtBattleAdaptor(IBattleManager battleManager, int maxFps, boolean skipSameFrames) {
		this.battleManager = battleManager;
		this.skipSameFrames = skipSameFrames;
		snapshot = new AtomicReference<TurnSnapshot>(null);

		timerTask = new Timer(1000 / maxFps, new TimerTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);
		lastSnapshot = null;

		battleManager.addListener(this);
	}

	protected abstract void updateView(TurnSnapshot snapshot);

	protected void finalize() throws Throwable {
		try {
			battleManager.removeListener(this);
			dispose();
		} finally {
			super.finalize();
		}
	}

	public int getFPS() {
		return fps;
	}

	public TurnSnapshot getLastSnapshot() {
		return lastSnapshot;
	}

	public void dispose() {
		timerTask.stop();
		battleManager.removeListener(this);
	}

	@Override
	public void onBattleStarted(BattleStartedEvent event) {
		isRunning.set(true);
		isPaused.set(false);
		snapshot.set(event.getTurnSnapshot());
		EventQueue.invokeLater(repaintTask);
		timerTask.start();
	}

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		timerTask.stop();
		isRunning.set(false);
		isPaused.set(false);
		snapshot.set(null);
		EventQueue.invokeLater(repaintTask);
	}

	@Override
	public void onBattleResumed(BattleResumedEvent event) {
		isPaused.set(false);
		timerTask.start();
	}

	public void onBattlePaused(BattlePausedEvent event) {
		timerTask.stop();
		isPaused.set(true);
	}

	public void onRoundStarted(RoundStartedEvent event) {
		EventQueue.invokeLater(repaintTask);
	}

	public void onRoundEnded(RoundEndedEvent event) {
		EventQueue.invokeLater(repaintTask);
	}

	public void onTurnEnded(TurnEndedEvent event) {
		snapshot.set(event.getTurnSnapshot());
		if (isPaused.get()) {
			EventQueue.invokeLater(repaintTask);
		}
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	private class QueuedTask implements Runnable {
		public void run() {
			if (!isRunning.get()) {
				lastSnapshot = null;
				updateView(null);
			} else {
				TurnSnapshot s = snapshot.get();

				if (lastSnapshot != s || !skipSameFrames) {
					lastSnapshot = s;

					updateView(lastSnapshot);

					calculateFPS();
				}
			}
		}
	}


	private class TimerTask implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			TurnSnapshot s = snapshot.get();

			if (lastSnapshot != s || !skipSameFrames) {
				lastSnapshot = s;

				updateView(lastSnapshot);

				calculateFPS();
			}
		}
	}

	private void calculateFPS() {
		// Calculate the current frames per second (FPS)

		if (measuredFrameCounter++ == 0) {
			measuredFrameStartTime = System.nanoTime();
		}

		long deltaTime = System.nanoTime() - measuredFrameStartTime;

		if (deltaTime / 1000000000 >= 1) {
			fps = (int) (measuredFrameCounter * 1000000000L / deltaTime);
			measuredFrameCounter = 0;
		}
	}
}
