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
	public final String[] getTeammates() {
		getCall();
        robocode.peer.TeamPeer teamPeer = peer.getTeamPeer();

        if (teamPeer == null) {
            return null;
        }
        String s[] = new String[teamPeer.size() - 1];

        int index = 0;

        for (RobotPeer teammate : teamPeer) {
            if (teammate != peer) {
                s[index++] = teammate.getName();
            }
        }
        return s;
	}

	public final boolean isTeammate(String name) {
		getCall();
        return peer.getTeamPeer() != null && peer.getTeamPeer().contains(name);
	}

	public final void sendMessage(String name, Serializable message) throws IOException {
		setCall();
        if (peer.getMessageManager() == null) {
            throw new IOException("You are not on a team.");
        }
        peer.getMessageManager().sendMessage(name, message);
	}

	public final void broadcastMessage(Serializable message) throws IOException {
		setCall();
        if (peer.getMessageManager() == null) {
            throw new IOException("You are not on a team.");
        }
        peer.getMessageManager().sendMessage(null, message);
	}

	// events
	public final List<MessageEvent> getMessageEvents() {
		getCall();
        return peer.getEventManager().getMessageEvents();
	}
}
