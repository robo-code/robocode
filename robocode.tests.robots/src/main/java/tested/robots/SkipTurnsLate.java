/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.SkippedTurnEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * Takes a little longer each turn until it skips turns, and checks that it gets the right SkippedTurnEvents.
 *
 * @author Julian Kent (original)
 */
public class SkipTurnsLate extends AdvancedRobot {
	static final int LIMIT = 3;
	private static final int MAX_TURNS = 2000;

	private final List<Long> skippedTurns = new ArrayList<Long>();

	@Override
	public void run() {
		long workNanos = 100000;
		int lateTurns = 0;
		int missedWakeups = 0;
		long lastTime = getTime();

		while ((lateTurns < LIMIT || missedWakeups < LIMIT) && getTime() < MAX_TURNS) {
			busyWait(workNanos);
			execute();
			final long time = getTime();

			if (skippedTurns.isEmpty()) {
				workNanos += workNanos / 20;
			} else if (time == lastTime + 1 && skippedTurns.size() == 1 && skippedTurns.get(0) == lastTime) {
				out.println("Late turn " + lastTime + " skipped");
				lateTurns++;
				workNanos += workNanos / 20;
			} else {
				if (skippedTurns.equals(turns(lastTime + 1, time))) {
					out.println("Missed wake-up, skipped turns " + skippedTurns);
					missedWakeups++;
				} else {
					out.println("Wrong skipped turns " + skippedTurns + " when woken at " + lastTime + " and " + time);
				}
				workNanos = workNanos * 3 / 4;
			}
			skippedTurns.clear();
			lastTime = time;
		}
		out.println("Done after " + lateTurns + " late turns and " + missedWakeups + " missed wake-ups");

		// noinspection InfiniteLoopStatement
		for (;;) {
			doNothing();
		}
	}

	@Override
	public void onSkippedTurn(SkippedTurnEvent event) {
		skippedTurns.add(event.getSkippedTurn());
	}

	private static List<Long> turns(long from, long to) {
		final List<Long> turns = new ArrayList<Long>();

		for (long turn = from; turn < to; turn++) {
			turns.add(turn);
		}
		return turns;
	}

	private static void busyWait(long nanos) {
		final long end = System.nanoTime() + nanos;

		// noinspection StatementWithEmptyBody
		while (System.nanoTime() < end) {}
	}
}
