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
import static robocode.io.Logger.logError;
import robocode.manager.RobocodeManager;

import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public final class BattlePlayer extends BaseBattle {

	private FileInputStream fileStream;
	private BufferedInputStream bufferedStream;
	private ZipInputStream zipStream;
	private ObjectInputStream objectStream;
	private BattleRecordInfo recordInfo;

	public BattlePlayer(RobocodeManager manager, BattleEventDispatcher eventDispatcher) {
		super(manager, eventDispatcher, false);
	}

	public void loadRecord(String recordFilename, BattleRecordFormat format) {
		cleanup();

		try {
			fileStream = new FileInputStream(recordFilename);
			bufferedStream = new BufferedInputStream(fileStream);
			if (format == BattleRecordFormat.BINARY) {
				objectStream = new ObjectInputStream(bufferedStream);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zipStream = new ZipInputStream(bufferedStream);
				zipStream.getNextEntry();
				objectStream = new ObjectInputStream(zipStream);
			} else{
				throw new Error("Not implemented");
			}


			recordInfo = (BattleRecordInfo) objectStream.readObject();

			battleRules = recordInfo.battleRules;
		} catch (IOException e) {
			logError(e);
		} catch (ClassNotFoundException e) {
			logError(e);
		}
	}

	@Override
	public synchronized void cleanup() {
		super.cleanup();
		cleanupStream(objectStream);
		objectStream = null;
		cleanupStream(zipStream);
		zipStream = null;
		cleanupStream(bufferedStream);
		bufferedStream = null;
		cleanupStream(fileStream);
		fileStream = null;
		recordInfo = null;
	}

	private void cleanupStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logError(e);
			}
		}
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		battleRules = recordInfo.battleRules;

		eventDispatcher.onBattleStarted(new BattleStartedEvent(readSnapshot(currentTime), recordInfo.battleRules, true));
		if (isPaused()) {
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}

	@Override
	protected void finalizeBattle() {
		boolean aborted = recordInfo.results == null || isAborted();

		eventDispatcher.onBattleEnded(new BattleEndedEvent(aborted));

		if (!aborted) {
			eventDispatcher.onBattleCompleted(new BattleCompletedEvent(recordInfo.battleRules, recordInfo.results));
		}

		super.finalizeBattle();

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
		eventDispatcher.onTurnEnded(new TurnEndedEvent(readSnapshot(currentTime)));

		super.finalizeTurn();
	}

	@Override
	protected boolean isRoundOver() {
		return (isAborted() || getTime() > recordInfo.turnsInRounds[getRoundNum()]);
	}

	private TurnSnapshot readSnapshot(int currentTime) {
		if (objectStream == null) {
			return null;
		}
		try {
			//TODO implement seek to currentTime, warn you. turns don't have same size in bytes 
			return (TurnSnapshot) objectStream.readObject();
		} catch (EOFException e) {
			return null;
		} catch (Exception e) {
			logError(e);
			return null;
		} 
	}
}
