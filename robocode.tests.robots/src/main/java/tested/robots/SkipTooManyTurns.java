/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * Stops responding for much longer than the allowed number of skipped turns, and then continues as if nothing
 * happened.
 *
 * @author Flemming N. Larsen (original)
 */
public class SkipTooManyTurns extends AdvancedRobot {

	@Override
	public void run() {
		ahead(20);

		// A turn can last more than 150 ms on Windows, when the robot does not respond, and the robot must skip more
		// than 30 turns in a row
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// eat interrupt
			e.printStackTrace(out);
		}

		// noinspection InfiniteLoopStatement
		for (;;) {
			turnLeft(10);
			ahead(10);
		}
	}
}
