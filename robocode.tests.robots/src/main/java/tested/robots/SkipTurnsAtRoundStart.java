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


/**
 * Starts too slowly in the second round, so it skips the first turns of that round.
 *
 * @author Julian Kent (original)
 */
public class SkipTurnsAtRoundStart extends AdvancedRobot {

	@Override
	public void run() {
		if (getRoundNum() == 1) {
			// The battle waits about 3 s for a robot to start
			try {
				Thread.sleep(3200);
			} catch (InterruptedException e) {
				// eat interrupt
				e.printStackTrace(out);
			}
		}

		// noinspection InfiniteLoopStatement
		for (;;) {
			doNothing();
		}
	}

	@Override
	public void onSkippedTurn(SkippedTurnEvent event) {
		out.println("Skipped turn " + event.getSkippedTurn() + " in round " + getRoundNum());
	}
}
