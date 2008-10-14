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


import static robocode.io.Logger.logError;

import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.manager.RobocodeManager;

import java.io.*;


/**
 * @author Pavel Savara (original)
 */
public class BattleRecorder implements IBattleRecorder {

	private final RobocodeManager manager;
	private BattleObserver battleObserver;
	private boolean recordingEnabled;
	private BattleRecord currentRecord;

	public BattleRecorder(RobocodeManager manager) {
		this.manager = manager;
	}

	public void loadRecord(String fileName) {
		FileInputStream fileStream = null;
		ObjectInputStream objectStream = null;

		try {
			fileStream = new FileInputStream(fileName);
			objectStream = new ObjectInputStream(fileStream);
			currentRecord = (BattleRecord) objectStream.readObject();
		} catch (IOException e) {
			logError(e);
		} catch (ClassNotFoundException e) {
			logError(e);
		} finally {
			if (objectStream != null) {
				try {
					objectStream.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					logError(e);
				}
			}
		}
	}

	public void saveRecord(String fileName) {
		FileOutputStream fileStream = null;
		ObjectOutputStream objectStream = null;

		try {
			fileStream = new FileOutputStream(fileName);
			objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(currentRecord);
		} catch (IOException e) {
			logError(e);
		} finally {
			if (objectStream != null) {
				try {
					objectStream.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					logError(e);
				}
			}
		}
	}

	public boolean hasRecord() {
		return currentRecord != null;
	}

	public BattleRecord getRecord() {
		return currentRecord;
	}

	public void setBattleEventDispatcher(BattleEventDispatcher battleEventDispatcher) {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
		battleObserver = new BattleObserver(battleEventDispatcher);
	}

	private class BattleObserver extends BattleAdaptor {

		final int initialSize = 16 * 1024 * 1024; // preallocate to prevent reallocation

		private robocode.battle.events.BattleEventDispatcher dispatcher;
		private ByteArrayOutputStream byteStream;
		private ObjectOutputStream objectStream;
		private int currentTurn;
		private int currentRound;

		public BattleObserver(BattleEventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
			dispatcher.addListener(this);
		}

		public void dispose() {
			dispatcher.removeListener(this);
		}

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			recordingEnabled = !event.isReplay() && manager.getProperties().getOptionsCommonEnableReplayRecording();
			if (!recordingEnabled) {
				return;
			}
			currentRecord = null;
			try {
				if (byteStream == null) {
					byteStream = new ByteArrayOutputStream(initialSize);
				}
				byteStream.reset();
				objectStream = new ObjectOutputStream(byteStream);
			} catch (IOException e) {
				logError(e);
			}

			currentRecord = new BattleRecord();
			currentRecord.robotCount = event.getTurnSnapshot().getRobots().size();
			currentRecord.recordsInTurns = new int[event.getBattleRules().getNumRounds()];
			currentRecord.battleRules = event.getBattleRules();
			currentRound = 0;
			currentTurn = 1;
			writeTurn(event.getTurnSnapshot());
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			if (event.isAborted()) {
				currentRecord.recordsInTurns[currentRound] = currentTurn;
			}
			currentRecord.rounds = currentRound;
			try {
				if (objectStream != null) {
					objectStream.flush();
					objectStream.close();
				}
				if (byteStream != null) {
					byteStream.flush();
				}
			} catch (IOException e) {
				logError(e);
			}
			if (byteStream != null) {
				currentRecord.records = byteStream.toByteArray();
				byteStream.reset();
			} else {
				currentRecord.records = null;
			}
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			currentRecord.results = event.getResults();
		}

		@Override
		public void onRoundEnded(RoundEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			currentRecord.recordsInTurns[currentRound] = currentTurn;
			currentTurn = 0;
			currentRound++;
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			currentTurn++;
			writeTurn(event.getTurnSnapshot());
		}

		private void writeTurn(TurnSnapshot turn) {
			try {
				objectStream.writeObject(turn);
			} catch (IOException e) {
				logError(e);
			}
		}
	}
}
