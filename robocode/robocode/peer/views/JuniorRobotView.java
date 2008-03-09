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
package robocode.peer.views;


import robocode.peer.IRobotRobotPeer;
import robocode.robotinterfaces.peer.IJuniorRobotView;


/**
 * @author Pavel Savara (original)
 */
public class JuniorRobotView extends BasicRobotView implements IJuniorRobotView {

	public JuniorRobotView(IRobotRobotPeer peer) {
		super(peer);
	}

	public void turnAndMove(double distance, double radians) {
		i_turnAndMove(distance, radians);
	}
}
