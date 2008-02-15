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

import robocode.robotinterfaces.peer.ITeamRobotPeer;
import robocode.robotinterfaces.peer.IBasicRobotPeer;
import robocode.MessageEvent;

import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public class TeamRobotPeerProxy extends AdvancedRobotPeerProxy implements ITeamRobotPeer {
	public TeamRobotPeerProxy(IBasicRobotPeer peer) {
		super(peer);
	}

	//team
	public String[] getTeammates() {
		peer.getCall();
		return ((ITeamRobotPeer)peer).getTeammates();
	}

	public boolean isTeammate(String name) {
		peer.getCall();
		return ((ITeamRobotPeer)peer).isTeammate(name);
	}

	public void sendMessage(String name, Serializable message) throws IOException {
		peer.setCall();
		((ITeamRobotPeer)peer).sendMessage(name, message);
	}

	public void broadcastMessage(Serializable message) throws IOException {
		peer.setCall();
		((ITeamRobotPeer)peer).broadcastMessage(message);
	}

	//events
	public List<MessageEvent> getMessageEvents() {
		peer.getCall();
		return ((ITeamRobotPeer)peer).getMessageEvents();
	}
}
