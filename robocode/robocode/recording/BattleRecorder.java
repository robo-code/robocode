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


import robocode.battle.events.*;
import robocode.manager.RobocodeManager;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public class BattleRecorder {

	private final RobocodeManager manager;
	private final RecordManager recordmanager;
	private BattleObserver battleObserver;
	private boolean recordingEnabled;

	public BattleRecorder(RobocodeManager manager, RecordManager recordmanager) {
		this.manager = manager;
		this.recordmanager = recordmanager;
	}

	public void setBattleEventDispatcher(BattleEventDispatcher battleEventDispatcher) {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
		battleObserver = new BattleObserver(battleEventDispatcher);
	}

	private class BattleObserver extends BattleAdaptor {
		private robocode.battle.events.BattleEventDispatcher dispatcher;

		private int currentTurn;
		private int currentRound;

		public BattleObserver(BattleEventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
			dispatcher.addListener(this);
		}

		public void dispose() {
			dispatcher.removeListener(this);
			recordmanager.cleanupStreams();
		}

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			recordingEnabled = !event.isReplay() && manager.getProperties().getOptionsCommonEnableReplayRecording();
			if (!recordingEnabled) {
				return;
			}
			recordmanager.cleanupStreams();
			recordmanager.createRecordInfo(event.getBattleRules(), event.getTurnSnapshot().getRobots().size());

			currentRound = 0;
			currentTurn = 0;

			recordmanager.writeTurn(event.getTurnSnapshot());
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			recordmanager.updateRecordInfoRound(currentRound, currentTurn);
			recordmanager.cleanupStreams();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			recordmanager.updateRecordInfoResults(event.getResults());
		}

		@Override
		public void onRoundEnded(RoundEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			recordmanager.updateRecordInfoRound(currentRound, currentTurn);
		}

		@Override
		public void onRoundStarted(RoundStartedEvent event) {
			currentRound = event.getRound();
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			currentTurn = event.getTurnSnapshot().getTurn();

			recordmanager.writeTurn(event.getTurnSnapshot());
		}
	}
}
