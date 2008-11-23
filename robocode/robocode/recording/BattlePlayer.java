/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara & Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.recording;


import robocode.battle.BaseBattle;
import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.manager.RobocodeManager;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public final class BattlePlayer extends BaseBattle {

	private RecordManager recordManager;

	public BattlePlayer(RobocodeManager manager, RecordManager recordManager, BattleEventDispatcher eventDispatcher) {
		super(manager, eventDispatcher, false);
		this.recordManager = recordManager;
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		battleRules = recordManager.recordInfo.battleRules;

		eventDispatcher.onBattleStarted(new BattleStartedEvent(battleRules, recordManager.recordInfo.robotCount, true));
		if (isPaused()) {
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}

	@Override
	protected void finalizeBattle() {
		boolean aborted = recordManager.recordInfo.results == null || isAborted();

		eventDispatcher.onBattleEnded(new BattleEndedEvent(aborted));

		if (!aborted) {
			eventDispatcher.onBattleCompleted(new BattleCompletedEvent(battleRules, recordManager.recordInfo.results));
		}

		super.finalizeBattle();

		cleanup();
	}

	@Override
	protected void initializeRound() {
		super.initializeRound();

		final TurnSnapshot snapshot = recordManager.readSnapshot(currentTime);

		if (snapshot != null) {
			eventDispatcher.onRoundStarted(new RoundStartedEvent(snapshot, getRoundNum()));
		}
	}

	@Override
	protected void finalizeRound() {
		super.finalizeRound();

		eventDispatcher.onRoundEnded(new RoundEndedEvent(getRoundNum(), getTime()));
	}

	@Override
	protected void initializeTurn() {
		super.initializeTurn();

		eventDispatcher.onTurnStarted(new TurnStartedEvent());
	}

	@Override
	protected void finalizeTurn() {
		final TurnSnapshot snapshot = recordManager.readSnapshot(currentTime);

		if (snapshot != null) {
			eventDispatcher.onTurnEnded(new TurnEndedEvent(snapshot));
		}

		super.finalizeTurn();
	}

	@Override
	protected boolean isRoundOver() {
		final boolean end = getTime() >= recordManager.recordInfo.turnsInRounds[getRoundNum()] - 1;

		if (end) {
			if (recordManager.recordInfo.turnsInRounds.length > getRoundNum()
					&& recordManager.recordInfo.turnsInRounds[getRoundNum() + 1] == 0) {
				isAborted = true;
			}
		}
		return (isAborted() || end);
	}

}
