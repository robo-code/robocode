/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotRejectedTestBed;


/**
 * Tests that a robot cannot get hold of the threads of the game engine and other robots.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestThreadInterruptAttack extends RobotRejectedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.ThreadInterruptAttack";
	}
}
