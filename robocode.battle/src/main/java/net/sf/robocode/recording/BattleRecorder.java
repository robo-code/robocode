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
package net.sf.robocode.recording;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import robocode.control.events.*;

import java.util.Arrays;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public class BattleRecorder {

	private final RecordManager recordmanager;
	private BattleObserver battleObserver;

	public BattleRecorder(RecordManager recordmanager) {
		this.recordmanager = recordmanager;
	}

	public void attachRecorder(BattleEventDispatcher battleEventDispatcher) {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
		battleObserver = new BattleObserver(battleEventDispatcher);
	}

	public void detachRecorder() {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
	}

	private class BattleObserver extends BattleAdaptor {
		private final BattleEventDispatcher dispatcher;

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
			recordmanager.cleanupStreams();
			recordmanager.createRecordInfo(event.getBattleRules(), event.getRobotsCount());

			currentRound = 0;
			currentTurn = 0;
		}

		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			recordmanager.cleanupStreams();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			recordmanager.updateRecordInfoResults(Arrays.asList(event.getIndexedResults()));
		}

		@Override
		public void onRoundStarted(RoundStartedEvent event) {
			currentRound = event.getRound();
			currentTurn = 0;
			recordmanager.writeTurn(event.getStartSnapshot(), currentRound, currentTurn);
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			currentTurn = event.getTurnSnapshot().getTurn();
			recordmanager.writeTurn(event.getTurnSnapshot(), currentRound, currentTurn);
		}
	}
}
