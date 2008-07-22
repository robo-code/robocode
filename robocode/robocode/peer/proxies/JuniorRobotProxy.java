/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This is temporary implementation of the interface. You should not build any external component on top of it.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer.proxies;


import robocode.peer.RobotPeer;
import robocode.robotinterfaces.peer.IJuniorRobotPeer;


/**
 * @author Pavel Savara (original)
 */
public class JuniorRobotProxy extends BasicRobotProxy implements IJuniorRobotPeer {

	public JuniorRobotProxy(RobotPeer peer) {
		super(peer);
	}

	public void turnAndMove(double distance, double radians) {
		peer.turnAndMove(distance, radians);
	}
}
