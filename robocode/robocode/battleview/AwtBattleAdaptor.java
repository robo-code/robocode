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
package robocode.battleview;


import robocode.battle.events.BattleAdaptor;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.control.BattleSpecification;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * @author Pavel Savara (original)
 */
public abstract class AwtBattleAdaptor extends BattleAdaptor {
	private AtomicReference<TurnSnapshot> snapshot;
	private TurnSnapshot lastSnapshot;

	private AtomicBoolean isRunning;
	private AtomicBoolean isPaused;
	private BattleEventDispatcher dispatcher;

	private RepaintTask repaintTask = new RepaintTask();
	private Timer timer;
	private boolean skipSameFrames;

	// FPS (frames per second) calculation
	private int fps;

	private long measuredFrameCounter;
	private long measuredFrameStartTime;

	public AwtBattleAdaptor(BattleEventDispatcher dispatcher, int maxFps, boolean skipSameFrames) {
		this.dispatcher = dispatcher;
		this.skipSameFrames = skipSameFrames;
		snapshot = new AtomicReference<TurnSnapshot>(null);

		timer = new Timer(1000 / maxFps, new UpdateTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);
		lastSnapshot = null;

		dispatcher.addListener(this);
	}

	protected abstract void updateView(TurnSnapshot snapshot);

	protected void finalize() throws Throwable {
		try {
			dispatcher.removeListener(this);
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
		timer.stop();
		dispatcher.removeListener(this);
	}

	@Override
	public void onBattleStarted(BattleSpecification battleSpecification) {
		isRunning.set(true);
		isPaused.set(false);
		EventQueue.invokeLater(repaintTask);
		timer.start();
	}

	@Override
	public void onBattleEnded(boolean isAborted) {
		timer.stop();
		isRunning.set(false);
		isPaused.set(false);
		EventQueue.invokeLater(repaintTask);
	}

	@Override
	public void onBattleResumed() {
		isPaused.set(false);
		timer.start();
	}

	public void onBattlePaused() {
		timer.stop();
		isPaused.set(true);
	}

	public void onRoundStarted(int round) {
		EventQueue.invokeLater(repaintTask);
	}

	public void onRoundEnded() {
		EventQueue.invokeLater(repaintTask);
	}

	public void onTurnEnded(TurnSnapshot turnSnapshot) {
		snapshot.set(turnSnapshot);
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	private class RepaintTask implements Runnable {
		public void run() {
			if (!isRunning.get()) {
				lastSnapshot = null;
			}
			updateView(lastSnapshot);
		}
	}


	private class UpdateTask implements ActionListener {
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
