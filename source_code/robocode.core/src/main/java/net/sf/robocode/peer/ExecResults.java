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
package net.sf.robocode.peer;


import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.Event;
import robocode.RobotStatus;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
	
	private List<String> battlefieldState;

	public ExecResults(ExecCommands commands, RobotStatus status, List<Event> events, 
			List<TeamMessage> teamMessages, List<BulletStatus> bulletUpdates, boolean halt, 
			boolean shouldWait, boolean paintEnabled, List<String> battlefieldState) {
		this.commands = commands;
		this.status = status;
		this.events = events;
		this.teamMessages = teamMessages;
		this.bulletUpdates = bulletUpdates;
		this.halt = halt;
		this.shouldWait = shouldWait;
		this.paintEnabled = paintEnabled;
		this.battlefieldState = battlefieldState;
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
	
	public List<String> getBattlefieldState()
	{
		return battlefieldState;
	}
	
	public void setBattlefieldState(List<String> state)
	{
		battlefieldState = state;
	}

	private ExecResults() {}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		@SuppressWarnings({ "PointlessArithmeticExpression"})
		public int sizeOf(RbSerializer serializer, Object object) {
			ExecResults obj = (ExecResults) object;
			int size = RbSerializer.SIZEOF_TYPEINFO + 3 * RbSerializer.SIZEOF_BOOL;

			size += serializer.sizeOf(RbSerializer.ExecCommands_TYPE, obj.commands);
			size += serializer.sizeOf(RbSerializer.RobotStatus_TYPE, obj.status);

			// events
			for (Event event : obj.events) {
				size += serializer.sizeOf(event);
			}
			size += 1;

			// messages
			for (TeamMessage m : obj.teamMessages) {
				size += serializer.sizeOf(RbSerializer.TeamMessage_TYPE, m);
			}
			size += 1;

			// bullets
			for (BulletStatus b : obj.bulletUpdates) {
				size += serializer.sizeOf(RbSerializer.BulletStatus_TYPE, b);
			}
			size += 1;

			return size;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ExecResults obj = (ExecResults) object;

			serializer.serialize(buffer, obj.halt);
			serializer.serialize(buffer, obj.shouldWait);
			serializer.serialize(buffer, obj.paintEnabled);

			serializer.serialize(buffer, RbSerializer.ExecCommands_TYPE, obj.commands);
			serializer.serialize(buffer, RbSerializer.RobotStatus_TYPE, obj.status);

			for (Event event : obj.events) {
				serializer.serialize(buffer, event);
			}
			buffer.put(RbSerializer.TERMINATOR_TYPE);
			for (TeamMessage message : obj.teamMessages) {
				serializer.serialize(buffer, RbSerializer.TeamMessage_TYPE, message);
			}
			buffer.put(RbSerializer.TERMINATOR_TYPE);
			for (BulletStatus bulletStatus : obj.bulletUpdates) {
				serializer.serialize(buffer, RbSerializer.BulletStatus_TYPE, bulletStatus);
			}
			buffer.put(RbSerializer.TERMINATOR_TYPE);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			ExecResults res = new ExecResults();

			res.halt = serializer.deserializeBoolean(buffer);
			res.shouldWait = serializer.deserializeBoolean(buffer);
			res.paintEnabled = serializer.deserializeBoolean(buffer);

			res.commands = (ExecCommands) serializer.deserializeAny(buffer);
			res.status = (RobotStatus) serializer.deserializeAny(buffer);

			Object item = serializer.deserializeAny(buffer);

			res.events = new ArrayList<Event>();
			res.teamMessages = new ArrayList<TeamMessage>();
			res.bulletUpdates = new ArrayList<BulletStatus>();
			while (item != null) {
				if (item instanceof Event) {
					res.events.add((Event) item);
				}
				item = serializer.deserializeAny(buffer);
			}
			item = serializer.deserializeAny(buffer);
			while (item != null) {
				if (item instanceof TeamMessage) {
					res.teamMessages.add((TeamMessage) item);
				}
				item = serializer.deserializeAny(buffer);
			}
			item = serializer.deserializeAny(buffer);
			while (item != null) {
				if (item instanceof BulletStatus) {
					res.bulletUpdates.add((BulletStatus) item);
				}
				item = serializer.deserializeAny(buffer);
			}
			return res;
		}
	}

}
