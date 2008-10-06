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
package robocode.peer.proxies;


import robocode.MessageEvent;
import robocode.peer.RobotPeer;
import robocode.robotinterfaces.peer.ITeamRobotPeer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class TeamRobotProxy extends AdvancedRobotProxy implements ITeamRobotPeer {
	public TeamRobotProxy(RobotPeer peer) {
		super(peer);
	}

	// team
	public String[] getTeammates() {
		getCall();
		return peer.getTeammates();
	}

	public boolean isTeammate(String name) {
		getCall();
		return peer.isTeammate(name);
	}

	public void sendMessage(String name, Serializable message) throws IOException {
		setCall();
		peer.sendMessage(name, message);
	}

	public void broadcastMessage(Serializable message) throws IOException {
		setCall();
		peer.broadcastMessage(message);
	}

	// events
	public List<MessageEvent> getMessageEvents() {
		getCall();
		return peer.getMessageEvents();
	}
}
