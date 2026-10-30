/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode._RobotBase;

import java.lang.reflect.Field;


/**
 * Attempts to get hold of the robot peer of the game engine by making a non-public field of the Robocode API
 * accessible, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ReflectionAccessibleAttack extends AdvancedRobot {

	public static final String PEER_MESSAGE = "Got the robot peer: ";

	public void run() {
		try {
			Field peer = _RobotBase.class.getDeclaredField("peer");
			peer.setAccessible(true);
			out.println(PEER_MESSAGE + peer.get(this));
		} catch (ReflectiveOperationException e) {
			e.printStackTrace(out);
		}
	}
}
