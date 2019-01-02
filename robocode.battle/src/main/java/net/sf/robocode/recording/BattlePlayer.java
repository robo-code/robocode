/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.BaseBattle;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.snapshot.RobotSnapshot;
import net.sf.robocode.settings.ISettingsManager;
import robocode.BattleResults;
import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.util.List;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public final class BattlePlayer extends BaseBattle {

	private final RecordManager recordManager;
	private boolean[] paint;

	public BattlePlayer(ISettingsManager properties, IBattleManager battleManager, RecordManager recordManager, BattleEventDispatcher eventDispatcher) { // NO_UCD (unused code)
		super(
				properties, battleManager, eventDispatcher);
		this.recordManager = recordManager;
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		recordManager.prepareInputStream();

		battleRules = recordManager.recordInfo.battleRules;
		paint = new boolean[recordManager.recordInfo.robotCount];
		eventDispatcher.onBattleStarted(new BattleStartedEvent(battleRules, recordManager.recordInfo.robotCount, true));
		if (isPaused) {
			eventDispatcher.onBattlePaused(new BattlePausedEvent());
		}
	}

	@Override
	protected void finalizeBattle() {
		boolean aborted = recordManager.recordInfo.results == null || isAborted();

		eventDispatcher.onBattleFinished(new BattleFinishedEvent(aborted));

		if (!aborted) {
			final List<BattleResults> res = recordManager.recordInfo.results;

			eventDispatcher.onBattleCompleted(
					new BattleCompletedEvent(battleRules, res.toArray(new BattleResults[res.size()])));
		}

		super.finalizeBattle();

		cleanup();
	}

	@Override
	protected void initializeRound() {
		super.initializeRound();

		final ITurnSnapshot snapshot = recordManager.readSnapshot(currentTime);

		if (snapshot != null) {
			eventDispatcher.onRoundStarted(new RoundStartedEvent(snapshot, getRoundNum()));
		}
	}

	@Override
	protected void finalizeRound() {
		super.finalizeRound();

		eventDispatcher.onRoundEnded(new RoundEndedEvent(getRoundNum(), getTime(), totalTurns));
	}

	@Override
	protected void initializeTurn() {
		super.initializeTurn();

		eventDispatcher.onTurnStarted(new TurnStartedEvent());
	}

	@Override
	protected void finalizeTurn() {
		final ITurnSnapshot snapshot = recordManager.readSnapshot(currentTime);

		if (snapshot != null) {
			final IRobotSnapshot[] robots = snapshot.getRobots();

			for (int i = 0; i < robots.length; i++) {
				RobotSnapshot robot = (RobotSnapshot) robots[i];

				robot.setPaintEnabled(paint[i]);
			}
			eventDispatcher.onTurnEnded(new TurnEndedEvent(snapshot));
		}

		super.finalizeTurn();
	}

	@Override
	protected boolean isRoundOver() {
		final boolean end = getTime() >= recordManager.recordInfo.turnsInRounds[getRoundNum()] - 1;

		if (end) {
			if (recordManager.recordInfo.turnsInRounds.length > getRoundNum()
					&& recordManager.recordInfo.turnsInRounds[getRoundNum()] == 0) {
				isAborted = true;
			}
		}
		return (isAborted || end);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		recordManager.cleanupStreams();
	}

	public void setPaintEnabled(int robotIndex, boolean enable) {
		sendCommand(new EnableRobotPaintCommand(robotIndex, enable));
	}

	private class EnableRobotPaintCommand extends RobotCommand {
		final boolean enablePaint;

		EnableRobotPaintCommand(int robotIndex, boolean enablePaint) {
			super(robotIndex);
			this.enablePaint = enablePaint;
		}

		public void execute() {
			paint[robotIndex] = enablePaint;
		}
	}
}
