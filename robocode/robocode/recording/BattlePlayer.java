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
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.events.*;
import robocode.manager.RobocodeManager;
import static robocode.io.Logger.logError;

import java.io.*;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public final class BattlePlayer extends BaseBattle {

	private String recordFilename;
	private FileInputStream fileStream;
	private ObjectInputStream objectStream;
	private BattleRecordInfo recordInfo;

	public BattlePlayer(RobocodeManager manager, BattleEventDispatcher eventDispatcher) {
		super(manager, eventDispatcher, false);
	}

	public void loadRecord(String filename) {
		this.recordFilename = filename;

		cleanup();

		try {
			fileStream = new FileInputStream(recordFilename);
			objectStream = new ObjectInputStream(fileStream);

			recordInfo = (BattleRecordInfo) objectStream.readObject();

			battleRules = recordInfo.battleRules;

			eventDispatcher.onBattleStarted(new BattleStartedEvent(readSnapshot(), recordInfo.battleRules, true));
			if (isPaused()) {
				eventDispatcher.onBattlePaused(new BattlePausedEvent());
			}
		} catch (IOException e) {
			logError(e);
		} catch (ClassNotFoundException e) {
			logError(e);
		}
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

		if (fileStream != null) {
			try {
				fileStream.close();
			} catch (IOException e) {/* Ignore here */}

			fileStream = null;
		}

		recordInfo = null;
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		battleRules = recordInfo.battleRules;

		eventDispatcher.onBattleStarted(new BattleStartedEvent(readSnapshot(), recordInfo.battleRules, true));
		if (isPaused()) {
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}

	@Override
	protected void shutdownBattle() {
		boolean aborted = recordInfo.results == null || isAborted();

		eventDispatcher.onBattleEnded(new BattleEndedEvent(aborted));

		if (!aborted) {
			eventDispatcher.onBattleCompleted(new BattleCompletedEvent(recordInfo.battleRules, recordInfo.results));
		}

		super.shutdownBattle();

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

		eventDispatcher.onRoundEnded(new RoundEndedEvent(getRoundNum(), getTime()));
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
		return (isAborted() || getTime() > recordInfo.numberOfTurns[getRoundNum()]);
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
