/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen & Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.BattleProperties;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;


/**
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (original)
 */
public class BattleEventDispatcher implements IBattleListener {
	// This list is guaranteed to be thread-safe, which is necessary as it will be accessed
	// by both the battle thread and battle manager thread. If this list is not thread-safe
	// then ConcurentModificationExceptions will occur from time to time.
	private List<IBattleListener> listeners = new CopyOnWriteArrayList<IBattleListener>();

	public BattleEventDispatcher() {}

	public void addListener(IBattleListener listener) {
		assert (listener != null);

		listeners.add(listener);
	}

	public void removeListener(IBattleListener listener) {
		assert (listener != null);
		listeners.remove(listener);
	}

	public void onBattleStarted(BattleProperties properties) {
		for (IBattleListener listener : listeners) {
			listener.onBattleStarted(properties);
		}
	}

	public void onBattleEnded(boolean isAborted) {
		for (IBattleListener listener : listeners) {
			listener.onBattleEnded(isAborted);
		}
	}

	public void onBattlePaused() {
		for (IBattleListener listener : listeners) {
			listener.onBattlePaused();
		}
	}

	public void onBattleResumed() {
		for (IBattleListener listener : listeners) {
			listener.onBattleResumed();
		}
	}

	public void onRoundStarted(int round) {
		for (IBattleListener listener : listeners) {
			listener.onRoundStarted(round);
		}
	}

	public void onRoundEnded() {
		for (IBattleListener listener : listeners) {
			listener.onRoundEnded();
		}
	}

	public void onTurnStarted() {
		for (IBattleListener listener : listeners) {
			listener.onTurnStarted();
		}
	}

	public void onTurnEnded(TurnSnapshot turnSnapshot) {
		for (IBattleListener listener : listeners) {
			listener.onTurnEnded(turnSnapshot);
		}
	}
}
