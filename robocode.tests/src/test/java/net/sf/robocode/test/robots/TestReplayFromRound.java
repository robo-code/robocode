/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;

import net.sf.robocode.battle.IBattleManagerBase;
import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that a recorded battle can be replayed from a selected round, and that the replayed rounds are the same as in
 * the battle. A normal replay must still start at the first round.
 */
public class TestReplayFromRound extends RobocodeTestBed {

	private static final int ROUNDS = 4;
	private static final int COMPARED_TURN = 10;

	// The state of the robots at the compared turn, for each round (from 0)
	private final Map<Integer, String> battleStates = new ConcurrentHashMap<Integer, String>();
	private final Map<Integer, String> replayStates = new ConcurrentHashMap<Integer, String>();
	private final List<Integer> replayRounds = new ArrayList<Integer>();

	// Snapshots in the replay that do not belong to the round being replayed, or that skip a turn
	private final StringBuffer replayMismatches = new StringBuffer();
	private volatile int replayRound = -1;
	private volatile int lastReplayTurn;

	private volatile boolean replaying;
	private volatile CountDownLatch replayOver;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "sample.Walls";
	}

	@Override
	public String getEnemyName() {
		return "sample.Fire";
	}

	@Override
	public int getNumRounds() {
		return ROUNDS;
	}

	@Override
	public boolean isEnableRecording() {
		return true;
	}

	@Override
	public void onRoundStarted(RoundStartedEvent event) {
		if (replaying) {
			synchronized (replayRounds) {
				replayRounds.add(event.getRound());
			}
			replayRound = event.getRound();
			lastReplayTurn = 0;
			ITurnSnapshot start = event.getStartSnapshot();
			if (start.getRound() != event.getRound() || start.getTurn() != 0) {
				replayMismatches.append("Round ").append(event.getRound() + 1).append(" started with turn ")
						.append(start.getTurn()).append(" of round ").append(start.getRound() + 1).append('\n');
			}
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		ITurnSnapshot turn = event.getTurnSnapshot();
		if (replaying) {
			if (turn.getRound() != replayRound || turn.getTurn() != lastReplayTurn + 1) {
				replayMismatches.append("Round ").append(replayRound + 1).append(" showed turn ").append(turn.getTurn())
						.append(" of round ").append(turn.getRound() + 1).append(" after turn ").append(lastReplayTurn)
						.append('\n');
			}
			lastReplayTurn = turn.getTurn();
		}
		if (turn.getTurn() == COMPARED_TURN) {
			(replaying ? replayStates : battleStates).put(turn.getRound(), describe(turn));
		}
	}

	@Override
	public void onBattleFinished(BattleFinishedEvent event) {
		if (replaying && replayOver != null) {
			replayOver.countDown();
		}
	}

	@Override
	protected void runTeardown() {
		assertEquals(ROUNDS, battleStates.size(), "Rounds in the battle: " + battleStates.keySet());

		try {
			Object battleManager = ContainerBase.getComponent(IBattleManagerBase.class);
			Method replayFromRound = battleManager.getClass().getMethod("replayFromRound", int.class);
			Method replay = battleManager.getClass().getMethod("replay");

			// Replay from round 3, i.e. round index 2
			replayAndWait(() -> replayFromRound.invoke(battleManager, 3));
			assertEquals(Arrays.asList(2, 3), replayRounds, "Replayed rounds");
			assertEquals("", replayMismatches.toString(), "Replay from round 3");
			assertEquals(battleStates.get(2), replayStates.get(2), "Round 3");
			assertEquals(battleStates.get(3), replayStates.get(3), "Round 4");

			// A normal replay still starts at the first round
			replayAndWait(() -> replay.invoke(battleManager));
			assertEquals(Arrays.asList(0, 1, 2, 3), replayRounds, "Replayed rounds");
			assertEquals("", replayMismatches.toString(), "Normal replay");
			assertEquals(battleStates.get(0), replayStates.get(0), "Round 1");

			// Rounds that are not in the record
			for (int round : new int[] { 0, ROUNDS + 1 }) {
				InvocationTargetException e = assertThrows(InvocationTargetException.class,
						() -> replayFromRound.invoke(battleManager, round));
				assertInstanceOf(IllegalArgumentException.class, e.getCause(), "Round " + round);
			}
		} catch (ReflectiveOperationException e) {
			throw new AssertionError(e);
		} finally {
			replaying = false;
		}
	}

	private interface Replay {
		void start() throws ReflectiveOperationException;
	}

	private void replayAndWait(Replay replay) throws ReflectiveOperationException {
		synchronized (replayRounds) {
			replayRounds.clear();
		}
		replayStates.clear();
		replayMismatches.setLength(0);
		replayRound = -1;
		replayOver = new CountDownLatch(1);
		replaying = true;

		replay.start();
		try {
			assertTrue(replayOver.await(60, TimeUnit.SECONDS), "The replay did not finish");
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}

	private static String describe(ITurnSnapshot turn) {
		StringBuilder text = new StringBuilder();
		for (IRobotSnapshot robot : turn.getRobots()) {
			text.append(robot.getName()).append(' ').append(robot.getX()).append(',').append(robot.getY()).append(' ')
					.append(robot.getEnergy()).append('\n');
		}
		return text.toString();
	}
}
