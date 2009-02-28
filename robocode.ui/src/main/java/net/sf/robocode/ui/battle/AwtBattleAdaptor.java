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


/**
 * @author Pavel Savara (original)
 */
public final class AwtBattleAdaptor {
	private final IBattleManager battleManager;
	private final BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();
	private final BattleObserver observer;
	private final Timer timerTask;

	private final AtomicReference<ITurnSnapshot> snapshot;
	private final AtomicBoolean isRunning;
	private final AtomicBoolean isPaused;
	private StringBuilder[] outCache;

	public AwtBattleAdaptor(IBattleManager battleManager, int maxFps, boolean skipSameFrames) {
		this.battleManager = battleManager;
		snapshot = new AtomicReference<ITurnSnapshot>(null);

		this.skipSameFrames = skipSameFrames;
		timerTask = new Timer(1000 / maxFps, new TimerTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);

		observer = new BattleObserver();
		battleManager.addListener(observer);
	}

	protected void finalize() throws Throwable {
		try {
			timerTask.stop();
			battleManager.removeListener(observer);
		} finally {
			super.finalize();
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

	private ITurnSnapshot lastSnapshot;

	private void repaintTask(boolean forceRepaint, boolean readoutText) {
		try {

			ITurnSnapshot current = snapshot.get();

			if (!isRunning.get() || current == null) {
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
			repaintTask(false, true);
		}
	}


	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onTurnEnded(final TurnEndedEvent event) {
			snapshot.set(event.getTurnSnapshot());

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
						repaintTask(false, true);
					}
				});
			}
		}

		@Override
		public void onRoundStarted(final RoundStartedEvent event) {
			snapshot.set(event.getStartSnapshot());
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					repaintTask(true, false);
					battleEventDispatcher.onRoundStarted(event);
				}
			});
		}

		@Override
		public void onBattleStarted(final BattleStartedEvent event) {
			isRunning.set(true);
			isPaused.set(false);
			snapshot.set(null);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (snapshot) {
						if (outCache!=null){
							repaintTask(true, true);
						}
						outCache = new StringBuilder[event.getRobotsCount()];
						for (int i = 0; i < event.getRobotsCount(); i++) {
							outCache[i] = new StringBuilder(1024);
						}
					}
					battleEventDispatcher.onBattleStarted(event);
					repaintTask(true, false);
					timerTask.start();
				}
			});
		}

		@Override
		public void onBattleFinished(final BattleFinishedEvent event) {
			isRunning.set(false);
			isPaused.set(false);
			snapshot.set(null);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					timerTask.stop();
					repaintTask(true, true);
					battleEventDispatcher.onBattleFinished(event);
				}
			});
		}

		@Override
		public void onBattleCompleted(final BattleCompletedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleCompleted(event);
				}
			});
		}

		@Override
		public void onBattlePaused(final BattlePausedEvent event) {
			isPaused.set(true);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					timerTask.stop();
					battleEventDispatcher.onBattlePaused(event);
				}
			});
		}

		@Override
		public void onBattleResumed(final BattleResumedEvent event) {
			isPaused.set(false);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleResumed(event);
					if (isRunning.get()) {
						timerTask.start();
					}
				}
			});
		}

		@Override
		public void onRoundEnded(final RoundEndedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					repaintTask(true, true);
					battleEventDispatcher.onRoundEnded(event);
				}
			});
		}

		@Override
		public void onTurnStarted(final TurnStartedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onTurnStarted(event);
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
