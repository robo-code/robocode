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
 * Attempts to suspend a thread, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ThreadSuspendAttack extends AdvancedRobot {

	@SuppressWarnings("deprecation")
	public void run() {
		Thread.currentThread().suspend();
	}
}
