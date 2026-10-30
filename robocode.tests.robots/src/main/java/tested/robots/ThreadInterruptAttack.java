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
 * Attempts to get hold of the threads of the game engine and other robots in order to interrupt them.
 *
 * @author Flemming N. Larsen (original)
 */
public class ThreadInterruptAttack extends AdvancedRobot {

	public void run() {
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			if (thread != Thread.currentThread()) {
				thread.interrupt();
			}
		}
	}
}
