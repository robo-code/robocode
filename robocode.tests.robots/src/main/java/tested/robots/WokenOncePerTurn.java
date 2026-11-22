/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * Reports when it is woken up more than once in the same turn.
 *
 * @author Julian Kent (original)
 */
public class WokenOncePerTurn extends AdvancedRobot {

	@Override
	public void run() {
		long lastTime = -1;

		// noinspection InfiniteLoopStatement
		for (;;) {
			execute();
			if (getTime() == lastTime) {
				out.println("Woken twice in turn " + lastTime);
			}
			lastTime = getTime();
		}
	}
}
