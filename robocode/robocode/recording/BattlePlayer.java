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
package robocode.recording;


import robocode.battle.BaseBattle;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.events.*;
import robocode.manager.RobocodeManager;
import static robocode.io.Logger.logError;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * @author Pavel Savara (original)
 */
public final class BattlePlayer extends BaseBattle {

	private final BattleRecord record; // Do not cleanup or null this!
	private ByteArrayInputStream byteStream;
	private ObjectInputStream objectStream;

	public BattlePlayer(BattleRecord record, RobocodeManager manager, BattleEventDispatcher eventDispatcher, boolean paused) {
		super(manager, eventDispatcher, paused);
		this.record = record;
	}

	@Override
	public synchronized void cleanup() {
		super.cleanup();

		if (objectStream != null) {
			try {
				objectStream.close();
			} catch (IOException e) {/* Ignore here */}

			objectStream = null;
		}

		if (byteStream != null) {
			try {
				byteStream.close();
			} catch (IOException e) {/* Ignore here */}

			byteStream = null;
		}

		// record = null;
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		try {
			// Assuming that 'record' is never nulled here
			byteStream = new ByteArrayInputStream(record.records);
			byteStream.reset();
			objectStream = new ObjectInputStream(byteStream);
		} catch (IOException e) {
			logError(e);
		}

		setBattleRules(record.battleRules);

		eventDispatcher.onBattleStarted(new BattleStartedEvent(readSnapshot(), record.battleRules, true));
		if (isPaused()) {
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}

	@Override
	protected void shutdownBattle() {
		super.shutdownBattle();

		boolean aborted = record.results == null || isAborted();

		eventDispatcher.onBattleEnded(new robocode.battle.events.BattleEndedEvent(aborted));

		if (!aborted) {
			eventDispatcher.onBattleCompleted(new BattleCompletedEvent(record.battleRules, record.results));
		}

		cleanup();
	}

	@Override
	protected void initializeRound() {
		super.initializeRound();

		eventDispatcher.onRoundStarted(new RoundStartedEvent(getRoundNum()));
	}

	@Override
	protected void finalizeRound() {
		super.finalizeRound();

		eventDispatcher.onRoundEnded(new RoundEndedEvent(getRoundNum(), getCurrentTurn()));
	}

	@Override
	protected void initializeTurn() {
		super.initializeTurn();

		eventDispatcher.onTurnStarted(new TurnStartedEvent());
	}

	@Override
	protected void finalizeTurn() {
		eventDispatcher.onTurnEnded(new TurnEndedEvent(readSnapshot()));

		super.finalizeTurn();
	}

	@Override
	protected boolean isRoundOver() {
		return (isAborted() || getCurrentTurn() > record.recordsInTurns[getRoundNum()]);
	}

	private TurnSnapshot readSnapshot() {
		if (objectStream == null) {
			return null;
		}
		try {
			return (TurnSnapshot) objectStream.readObject();
		} catch (EOFException e) {
			return null;
		} catch (Exception e) {
			logError(e);
			return null;
		} 
	}
}
