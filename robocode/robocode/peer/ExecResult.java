/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer;


import robocode.Event;
import robocode.RobotStatus;
import robocode.peer.robot.TeamMessage;

import java.io.Serializable;
import java.util.List;


public class ExecResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public ExecResult(RobotCommands commands, RobotStatus status, List<Event> events, List<TeamMessage> teamMessages, boolean halt, boolean shouldWait) {
		this.commands = commands;
		this.status = status;
		this.events = events;
		this.teamMessages = teamMessages;
		this.halt = halt;
		this.shouldWait = shouldWait;
	}

	public RobotCommands commands;
	public RobotStatus status;
	public List<Event> events;
	public List<TeamMessage> teamMessages;
	public boolean halt;
	public boolean shouldWait;
}
