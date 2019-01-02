/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.settings.ISettingsManager;
import robocode.BattleResults;
import robocode.control.events.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
class BattleRecorder {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private final RecordManager recordmanager;
	private final ISettingsManager properties;
	private BattleObserver battleObserver;

	BattleRecorder(RecordManager recordmanager, ISettingsManager properties) {
		this.recordmanager = recordmanager;
		this.properties = properties;
	}

	void attachRecorder(BattleEventDispatcher battleEventDispatcher) {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
		battleObserver = new BattleObserver(battleEventDispatcher);
	}

	void detachRecorder() {
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

			if (properties.getOptionsCommonEnableAutoRecording()) {
				writeAutoRecord(event);
			}
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

		private void writeAutoRecord(BattleCompletedEvent event) {
			try {
				final BattleResults[] results = event.getIndexedResults();
				StringBuilder name = new StringBuilder();

				name.append(FileUtil.getBattlesDir().getCanonicalPath());
				name.append(File.separator);

				Calendar calendar = Calendar.getInstance();

				name.append(dateFormat.format(calendar.getTime()));
				name.append('-');
				for (BattleResults r : results) {
					String teamLeaderName = r.getTeamLeaderName();

					if (teamLeaderName.contains("*")) { // Development version of robot indicated with * in name
						teamLeaderName = teamLeaderName.replace("*", ""); // Remove the star (*)
					}
					name.append(teamLeaderName);
					name.append('-');
				}
				name.setLength(name.length() - 1);
				if (properties.getOptionsCommonAutoRecordingXML()) {
					name.append(".xml.zip");
					recordmanager.saveRecord(name.toString(), BattleRecordFormat.XML_ZIP, new SerializableOptions(true));
				} else {
					name.append(".zip.br");
					recordmanager.saveRecord(name.toString(), BattleRecordFormat.BINARY_ZIP,
							new SerializableOptions(true));
				}
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
	}
}
