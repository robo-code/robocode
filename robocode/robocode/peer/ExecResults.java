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


public class ExecResults implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExecCommands commands;
	private RobotStatus status;
	private List<Event> events;
	private List<TeamMessage> teamMessages;
	private List<BulletStatus> bulletUpdates;
	private boolean halt;
	private boolean shouldWait;
	private boolean paintEnabled;

	public ExecResults(ExecCommands commands, RobotStatus status, List<Event> events, List<TeamMessage> teamMessages, List<BulletStatus> bulletUpdates, boolean halt, boolean shouldWait, boolean paintEnabled) {
		this.commands = commands;
		this.status = status;
		this.events = events;
		this.teamMessages = teamMessages;
		this.bulletUpdates = bulletUpdates;
		this.halt = halt;
		this.shouldWait = shouldWait;
		this.paintEnabled = paintEnabled;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public ExecCommands getCommands() {
		return commands;
	}

	public void setCommands(ExecCommands commands) {
		this.commands = commands;
	}

	public RobotStatus getStatus() {
		return status;
	}

	public void setStatus(RobotStatus status) {
		this.status = status;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<TeamMessage> getTeamMessages() {
		return teamMessages;
	}

	public void setTeamMessages(List<TeamMessage> teamMessages) {
		this.teamMessages = teamMessages;
	}

	public List<BulletStatus> getBulletUpdates() {
		return bulletUpdates;
	}

	public void setBulletUpdates(List<BulletStatus> bulletUpdates) {
		this.bulletUpdates = bulletUpdates;
	}

	public boolean isHalt() {
		return halt;
	}

	public void setHalt(boolean halt) {
		this.halt = halt;
	}

	public boolean isShouldWait() {
		return shouldWait;
	}

	public void setShouldWait(boolean shouldWait) {
		this.shouldWait = shouldWait;
	}

	public boolean isPaintEnabled() {
		return paintEnabled;
	}

	public void setPaintEnabled(boolean paintEnabled) {
		this.paintEnabled = paintEnabled;
	}
}
