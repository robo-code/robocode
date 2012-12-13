/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen & Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.battle.events;


import robocode.control.events.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;


/**
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class BattleEventDispatcher implements IBattleListener {

	private static final Logger logger = Logger.getLogger(BattleEventDispatcher.class);
	
	// This list is guaranteed to be thread-safe, which is necessary as it will be accessed
	// by both the battle thread and battle manager thread. If this list is not thread-safe
	// then ConcurentModificationExceptions will occur from time to time.
	private final List<IBattleListener> listeners = new CopyOnWriteArrayList<IBattleListener>();

	public BattleEventDispatcher() {}

	public void addListener(IBattleListener listener) {
		assert (listener != null);
		listeners.add(listener);
	}

	public void removeListener(IBattleListener listener) {
		assert (listener != null);
		listeners.remove(listener);
	}

	public void onBattleStarted(BattleStartedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleStarted(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleStarted()", t);
			}
		}
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleCompleted(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleCompleted()", t);
			}
		}
	}

	public void onBattleFinished(BattleFinishedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleFinished(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleFinished()", t);
			}
		}
	}

	public void onBattlePaused(BattlePausedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattlePaused(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattlePaused()", t);
			}
		}
	}

	public void onBattleResumed(BattleResumedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleResumed(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleResumed()", t);
			}
		}
	}

	public void onRoundStarted(RoundStartedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onRoundStarted(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onRoundStarted()", t);
			}
		}
	}

	public void onRoundEnded(RoundEndedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onRoundEnded(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onRoundEnded()", t);
			}
		}
	}

	public void onTurnStarted(TurnStartedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onTurnStarted(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onTurnStarted()", t);
			}
		}
	}

	public void onTurnEnded(TurnEndedEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onTurnEnded(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onTurnEnded()", t);
			}
		}
	}

	public void onBattleMessage(BattleMessageEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleMessage(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleMessage()", t);
//				System.err.println(listener.getClass() + ".onBattleMessage():\n" + t.getLocalizedMessage() + '\n' + t.getStackTrace());
			}
		}
	}

	public void onBattleError(BattleErrorEvent event) {
		for (IBattleListener listener : listeners) {
			try {
				listener.onBattleError(event);
			} catch (Throwable t) {
				logger.error(listener.getClass() + ".onBattleMessage()", t);
//				System.err.println(listener.getClass() + ".onBattleError():\n" + t.getLocalizedMessage() + '\n' + t.getStackTrace());
			}
		}
	}
}
