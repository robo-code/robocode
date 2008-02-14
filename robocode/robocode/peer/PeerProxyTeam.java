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

import robocode.robotinterfaces.peer.IRobotPeerTeam;
import robocode.robotinterfaces.peer.IRobotPeer;
import robocode.MessageEvent;

import java.io.Serializable;
import java.io.IOException;
import java.util.List;

/**
 * @author Pavel Savara (original)
 */
public class PeerProxyTeam extends PeerProxyAdvanced implements IRobotPeerTeam {
	public PeerProxyTeam(IRobotPeer peer) {
		super(peer);
	}

	//team
	public String[] getTeammates() {
		peer.getCall();
		return ((IRobotPeerTeam)peer).getTeammates(); 
	}

	public boolean isTeammate(String name) {
		peer.getCall();
		return ((IRobotPeerTeam)peer).isTeammate(name); 
	}

	public void sendMessage(String name, Serializable message) throws IOException {
		peer.setCall();
		((IRobotPeerTeam)peer).sendMessage(name, message); 
	}

	public void broadcastMessage(Serializable message) throws IOException {
		peer.setCall();
		((IRobotPeerTeam)peer).broadcastMessage(message); 
	}

	//events
	public List<MessageEvent> getMessageEvents() {
		peer.getCall();
		return ((IRobotPeerTeam)peer).getMessageEvents(); 
	}
}
