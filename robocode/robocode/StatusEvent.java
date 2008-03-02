/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 */
package robocode;


import robocode.peer.RobotPeer;


/**
 * This event is sent to {@link Robot#onStatus(StatusEvent)} every turn.
 *
 * @author Flemming N. Larsen (contributor)
 * 
 * @since 1.5
 */
public class StatusEvent extends Event {

	private final RobotStatus status;

	/**
	 * Creates a new RobotStatus based a a RobotPeer.
	 * This constructor is called internally from the game.
	 *
	 * @param robotPeer the RobotPeer containing the states we must make a snapshot of
	 */
	public StatusEvent(RobotPeer robotPeer) {
		super();

		status = new RobotStatus(robotPeer);
	}

	/**
	 * Returns the robot status at the time defined by getTime().
	 * 
	 * @see #getTime()
	 */
	public RobotStatus getStatus() {
		return status;
	}
}
