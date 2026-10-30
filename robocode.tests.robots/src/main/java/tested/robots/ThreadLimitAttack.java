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
 * Attempts to create more threads than robots are allowed to, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ThreadLimitAttack extends AdvancedRobot {

	public void run() {
		for (int i = 0; i < 10; i++) {
			try {
				new Thread(new Runnable() {
					public void run() {
						try {
							while (true) {
								Thread.sleep(100);
							}
						} catch (InterruptedException ignore) {
							// stop when interrupted
						}
					}
				}).start();
			} catch (Throwable t) {
				// The SecurityManager prevents creating the thread with Java < 24
				t.printStackTrace(out);
			}
		}
		for (int i = 0; i < 10; i++) {
			turnLeft(10);
		}
	}
}
