/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.SkippedTurnEvent;
import robocode.StatusEvent;


/**
 * Responds slowly until it has skipped a few turns, and then continues normally.
 *
 * @author Pavel Savara (original)
 */
public class SkipTurns extends AdvancedRobot {
	private volatile int skipped = 0;

	static final int LIMIT = 5;

	@Override
	public void run() {
		// noinspection InfiniteLoopStatement
		for (;;) {
			turnLeft(10);
			ahead(1);
			turnLeft(10);
			back(1);
		}
	}

	@Override
	public void onStatus(StatusEvent e) {
		out.println("live");
		if (skipped <= LIMIT) {
			slowResponse();
		}
	}

	@Override
	public void onSkippedTurn(SkippedTurnEvent event) {
		out.println("Skipped!!!");

		skipped++;
	}

	private final Object w = new Object();

	private void slowResponse() {

		// Must be longer than a turn, which can last more than 170 ms on Windows with Java 8, when the robot does not
		// respond, but shorter than 30 turns, where a turn lasts about 10 ms on other systems
		synchronized (w) {
			try {
				w.wait(250);
			} catch (InterruptedException e) {
				// eat interrupt
				e.printStackTrace(out);
			}
		}
	}
}
