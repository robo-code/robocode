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


import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.io.Logger;
import robocode.manager.IBattleManager;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Pavel Savara (original)
 */
public final class AwtBattleAdaptor extends BattleAdaptor {
	private final IBattleManager battleManager;
	private final BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();
	private final BattleObserver observer;
	private final Timer timerTask;

	private final AtomicReference<TurnSnapshot> snapshot;
	private final AtomicBoolean isRunning;
	private final AtomicBoolean isPaused;
	private StringBuilder[] outCache;

	public AwtBattleAdaptor(IBattleManager battleManager, int maxFps, boolean skipSameFrames) {
		this.battleManager = battleManager;
		snapshot = new AtomicReference<TurnSnapshot>(null);

		this.skipSameFrames = skipSameFrames;
		timerTask = new Timer(1000 / maxFps, new TimerTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);

		observer = new BattleObserver();
		battleManager.addListener(observer);
		battleEventDispatcher.addListener(this);
	}

	protected void finalize() throws Throwable {
		try {
			timerTask.stop();
			battleManager.removeListener(observer);
			battleEventDispatcher.removeListener(this);
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

	@Override
	public void onBattleStarted(BattleStartedEvent event) {
		repaintTask(true, false);
		timerTask.start();
	}

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		timerTask.stop();
		repaintTask(true, true);
	}

	@Override
	public void onBattleResumed(BattleResumedEvent event) {
		if (isRunning.get()) {
			timerTask.start();
		}
	}

	@Override
	public void onBattlePaused(BattlePausedEvent event) {
		timerTask.stop();
	}

	@Override
	public void onRoundStarted(RoundStartedEvent event) {
		repaintTask(true, false);
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		repaintTask(true, true);
	}

	public TurnSnapshot getLastSnapshot() {
		return lastSnapshot;
	}

	private TurnSnapshot lastSnapshot;

	private void repaintTask(boolean forceRepaint, boolean readoutText) {
		try {

			TurnSnapshot current = snapshot.get();

			if (!isRunning.get() || current == null) {
				lastSnapshot = null;
				battleEventDispatcher.onTurnEnded(new TurnEndedEvent(null));
			} else {

				if (lastSnapshot != current || !skipSameFrames || forceRepaint) {
					lastSnapshot = current;

					if (readoutText) {
						synchronized (snapshot) {
							java.util.List<RobotSnapshot> robots = lastSnapshot.getRobots();

							for (int i = 0; i < robots.size(); i++) {
								RobotSnapshot robot = robots.get(i);

								robot.updateOutputStreamSnapshot(outCache[i].toString());
								outCache[i].setLength(0);
							}
						}
					}

					battleEventDispatcher.onTurnEnded(new TurnEndedEvent(lastSnapshot));

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
	private boolean skipSameFrames;

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

			final java.util.List<RobotSnapshot> robots = event.getTurnSnapshot().getRobots();

			synchronized (snapshot) {
				for (int i = 0; i < robots.size(); i++) {
					RobotSnapshot robot = robots.get(i);

					if (robot.getOutputStreamSnapshot() != null && robot.getOutputStreamSnapshot().length() != 0) {
						outCache[i].append(robot.getOutputStreamSnapshot());
					}
				}
			}
			if (isPaused.get()) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						battleEventDispatcher.onTurnEnded(event);
					}
				});
			}
		}

		@Override
		public void onRoundStarted(final RoundStartedEvent event) {
			snapshot.set(event.getTurnSnapshot());
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onRoundStarted(event);
				}
			});
		}

		@Override
		public void onBattleStarted(final BattleStartedEvent event) {
			isRunning.set(true);
			isPaused.set(false);
			snapshot.set(null);
			synchronized (snapshot) {
				outCache = new StringBuilder[event.getRobotsCount()];
				for (int i = 0; i < event.getRobotsCount(); i++) {
					outCache[i] = new StringBuilder(1024);
				}
			}
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleStarted(event);
				}
			});
		}

		@Override
		public void onBattleEnded(final BattleEndedEvent event) {
			isRunning.set(false);
			isPaused.set(false);
			snapshot.set(null);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleEnded(event);
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
				}
			});
		}

		@Override
		public void onRoundEnded(final RoundEndedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
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
