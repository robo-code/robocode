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


import robocode.peer.proxies.IBattleRobotProxy;


/**
 * This event is sent to {@link Robot#onStatus(StatusEvent)
 * onStatus(StatusEvent)} every turn in a battle.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.5
 */
public class StatusEvent extends Event {

	private final RobotStatus status;

	/**
	 * This constructor is called internally from the game in order to create
	 * a new {@link RobotStatus}.
	 *
	 * @param robotPeer the RobotPeer containing the current states 
	 */
	public StatusEvent(IBattleRobotProxy robotPeer) {
		super();

		status = new RobotStatus(robotPeer);
	}

	/**
	 * Returns the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
	 * 
	 * @return the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
	 */
	public RobotStatus getStatus() {
		return status;
	}
}
