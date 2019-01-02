/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.proxies;


import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.serialization.RobocodeObjectInputStream;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.peer.TeamMessage;
import net.sf.robocode.repository.IRobotItem;
import robocode.MessageEvent;
import robocode.robotinterfaces.peer.ITeamRobotPeer;

import java.io.*;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class TeamRobotProxy extends AdvancedRobotProxy implements ITeamRobotPeer {
	private static final int MAX_MESSAGE_SIZE = 32768;
	private final ByteArrayOutputStream byteStreamWriter;

	public TeamRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer, RobotStatics statics) {
		super(specification, hostManager, peer, statics);
		byteStreamWriter = new ByteArrayOutputStream(MAX_MESSAGE_SIZE);
	}

	// team
	public String[] getTeammates() {
		getCall();
		return statics.getTeammates();
	}

	public boolean isTeammate(String name) {
		getCall();
		if (name != null) { // Bugfix [2960870]
			if (name.equals(statics.getName())) {
				return true;
			}
			final String[] teammates = statics.getTeammates();
	
			if (teammates != null) {
				for (String mate : teammates) {
					if (mate.equals(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void broadcastMessage(Serializable message) throws IOException {
		sendMessage(null, message);
	}

	public void sendMessage(String name, Serializable message) throws IOException {
		setCall();

		try {
			if (!statics.isTeamRobot()) {
				throw new IOException("You are not on a team.");
			}
			byteStreamWriter.reset();
			ObjectOutputStream objectStreamWriter = new ObjectOutputStream(byteStreamWriter);

			objectStreamWriter.writeObject(message);
			objectStreamWriter.flush();
			byteStreamWriter.flush();
			final byte[] bytes = byteStreamWriter.toByteArray();

			objectStreamWriter.reset();
			if (bytes.length > MAX_MESSAGE_SIZE) {
				throw new IOException("Message too big. " + bytes.length + ">" + MAX_MESSAGE_SIZE);
			}
			commands.getTeamMessages().add(new TeamMessage(getName(), name, bytes));
		} catch (IOException e) {
			out.printStackTrace(e);
			throw e;
		}

	}

	@Override
	protected final void loadTeamMessages(List<TeamMessage> teamMessages) {
		if (teamMessages == null) {
			return;
		}
		for (TeamMessage teamMessage : teamMessages) {
			try {
				ByteArrayInputStream byteStreamReader = new ByteArrayInputStream(teamMessage.message);
				byteStreamReader.reset();

				RobocodeObjectInputStream objectStreamReader = null;
				try {
					objectStreamReader = new RobocodeObjectInputStream(byteStreamReader, (ClassLoader) robotClassLoader);
					Serializable message = (Serializable) objectStreamReader.readObject();
					MessageEvent event = new MessageEvent(teamMessage.sender, message);
					eventManager.add(event);
				} finally {
					if (objectStreamReader != null) {
						objectStreamReader.close();
					}
				}
			} catch (IOException e) {
				out.printStackTrace(e);
			} catch (ClassNotFoundException e) {
				out.printStackTrace(e);
			}
		}
	}

	// events
	public List<MessageEvent> getMessageEvents() {
		getCall();
		return eventManager.getMessageEvents();
	}
}
