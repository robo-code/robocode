/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.battle;


import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.snapshot.RobotSnapshot;
import net.sf.robocode.io.Logger;
import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Pavel Savara (original)
 */
public final class AwtBattleAdaptor {
	private boolean isEnabled;
	private final IBattleManager battleManager;
	private final BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();
	private final BattleObserver observer;
	private final Timer timerTask;

	private final AtomicReference<ITurnSnapshot> snapshot;
	private final AtomicBoolean isRunning;
	private final AtomicBoolean isPaused;
	private final AtomicInteger majorEvent;
	private final AtomicInteger lastMajorEvent;
	private ITurnSnapshot lastSnapshot;
	private StringBuilder[] outCache;

	public AwtBattleAdaptor(IBattleManager battleManager, int maxFps, boolean skipSameFrames) {
		this.battleManager = battleManager;
		snapshot = new AtomicReference<ITurnSnapshot>(null);

		this.skipSameFrames = skipSameFrames;
		timerTask = new Timer(1000 / maxFps, new TimerTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);
		majorEvent = new AtomicInteger(0);
		lastMajorEvent = new AtomicInteger(0);

		observer = new BattleObserver();
	}

	protected void finalize() throws Throwable {
		try {
			timerTask.stop();
			battleManager.removeListener(observer);
		} finally {
			super.finalize();
		}
	}

	public void subscribe(boolean isEnabled) {
		if (this.isEnabled && !isEnabled) {
			battleManager.removeListener(observer);
			timerTask.stop();
			isEnabled = false;
		} else if (!this.isEnabled && isEnabled) {
			battleManager.addListener(observer);
			isEnabled = true;
		}
	}

	public synchronized void addListener(IBattleListener listener) {
		battleEventDispatcher.addListener(listener);
	}

	public synchronized void removeListener(IBattleListener listener) {
		battleEventDispatcher.removeListener(listener);
	}

	public ITurnSnapshot getLastSnapshot() {
		return lastSnapshot;
	}

	// this is always dispatched on AWT thread
	private void awtOnTurnEnded(boolean forceRepaint, boolean readoutText) {
		try {
			ITurnSnapshot current = snapshot.get();

			if (current == null) { // !isRunning.get() ||
				// paint logo
				lastSnapshot = null;
				battleEventDispatcher.onTurnEnded(new TurnEndedEvent(null));
			} else {
				if (lastSnapshot != current || !skipSameFrames || forceRepaint) {
					lastSnapshot = current;

					IRobotSnapshot[] robots = null;

					if (readoutText) {
						synchronized (snapshot) {
							robots = lastSnapshot.getRobots();

							for (int i = 0; i < robots.length; i++) {
								RobotSnapshot robot = (RobotSnapshot) robots[i];

								final StringBuilder cache = outCache[i];

								if (cache.length() > 0) {
									robot.setOutputStreamSnapshot(cache.toString());
									outCache[i].setLength(0);
								}
							}
						}
					}

					battleEventDispatcher.onTurnEnded(new TurnEndedEvent(lastSnapshot));

					if (readoutText) {
						for (IRobotSnapshot robot : robots) {
							((RobotSnapshot) robot).setOutputStreamSnapshot(null);
						}
					}

					calculateFPS();
				}
			}
		} catch (Throwable t) {
			Logger.logError(t);
		}
	}

	public int getFPS() {
		return fps;
	}

	// FPS (frames per second) calculation
	private int fps;
	private long measuredFrameCounter;
	private long measuredFrameStartTime;
	private final boolean skipSameFrames;

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

	private class TimerTask implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			awtOnTurnEnded(false, true);
		}
	}


	// BattleObserver methods are always called by battle thread
	// but everything inside invokeLater {} block in on AWT thread 
	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onTurnEnded(final TurnEndedEvent event) {
			if (lastMajorEvent.get() == majorEvent.get()) {
				// snapshot is updated out of order, but always within the same major event
				snapshot.set(event.getTurnSnapshot());
			}

			final IRobotSnapshot[] robots = event.getTurnSnapshot().getRobots();

			for (int i = 0; i < robots.length; i++) {
				RobotSnapshot robot = (RobotSnapshot) robots[i];
				final int r = i;
				final String text = robot.getOutputStreamSnapshot();

				if (text != null && text.length() != 0) {
					robot.setOutputStreamSnapshot(null);
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							synchronized (snapshot) {
								outCache[r].append(text);
							}
						}
					});
				}
			}
			if (isPaused.get()) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						awtOnTurnEnded(false, true);
					}
				});
			}
		}

		@Override
		public void onRoundStarted(final RoundStartedEvent event) {
			if (lastMajorEvent.get() == majorEvent.get()) {
				snapshot.set(event.getStartSnapshot());
			}
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					awtOnTurnEnded(true, false);
					battleEventDispatcher.onRoundStarted(event);
					lastMajorEvent.incrementAndGet();
				}
			});
		}

		@Override
		public void onBattleStarted(final BattleStartedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					isRunning.set(true);
					isPaused.set(false);
					synchronized (snapshot) {
						outCache = new StringBuilder[event.getRobotsCount()];
						for (int i = 0; i < event.getRobotsCount(); i++) {
							outCache[i] = new StringBuilder(1024);
						}
					}
					snapshot.set(null);
					battleEventDispatcher.onBattleStarted(event);
					lastMajorEvent.incrementAndGet();
					awtOnTurnEnded(true, false);
					timerTask.start();
				}
			});
		}

		@Override
		public void onBattleFinished(final BattleFinishedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					isRunning.set(false);
					isPaused.set(false);
					timerTask.stop();
					// flush text cache
					awtOnTurnEnded(true, true);

					battleEventDispatcher.onBattleFinished(event);
					lastMajorEvent.incrementAndGet();
					snapshot.set(null);

					// paint logo
					awtOnTurnEnded(true, true);
				}
			});
		}

		@Override
		public void onBattleCompleted(final BattleCompletedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleCompleted(event);
					lastMajorEvent.incrementAndGet();
					awtOnTurnEnded(true, true);
				}
			});
		}

		@Override
		public void onRoundEnded(final RoundEndedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onRoundEnded(event);
					lastMajorEvent.incrementAndGet();
					awtOnTurnEnded(true, true);
				}
			});
		}

		@Override
		public void onBattlePaused(final BattlePausedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					timerTask.stop();
					battleEventDispatcher.onBattlePaused(event);
					awtOnTurnEnded(true, true);
					isPaused.set(true);
				}
			});
		}

		@Override
		public void onBattleResumed(final BattleResumedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleResumed(event);
					if (isRunning.get()) {
						timerTask.start();
						isPaused.set(false);
					}
				}
			});
		}

		@Override
		public void onBattleMessage(final BattleMessageEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleMessage(event);
				}
			});
		}

		@Override
		public void onBattleError(final BattleErrorEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleError(event);
				}
			});
		}
	}
}
