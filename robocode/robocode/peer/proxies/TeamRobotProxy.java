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
import robocode.peer.IRobotRobotPeer;
import robocode.peer.RobotPeer;
import robocode.peer.robot.RobotMessageManager;
import robocode.robotinterfaces.peer.ITeamRobotPeer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class TeamRobotProxy extends AdvancedRobotProxy implements ITeamRobotPeer {
    private RobotMessageManager messageManager;

    public TeamRobotProxy(IRobotRobotPeer peer) {
        super(peer);
        messageManager = peer.getRobotMessageManager();
    }

    // team
    public String[] getTeammates() {
        getCall();
        peer.lockRead();
        try {
            robocode.peer.TeamPeer teamPeer = info.getTeamPeer();

            if (teamPeer == null) {
                return null;
            }
            String s[] = new String[teamPeer.size() - 1];

            int index = 0;

            for (RobotPeer teammate : teamPeer) {
                if (teammate != peer) {
                    s[index++] = teammate.getRobotRunnableView().getName();
                }
            }
            return s;
        } finally {
            peer.unlockRead();
        }
    }

    public boolean isTeammate(String name) {
        getCall();
        peer.lockRead();
        try {
            return info.getTeamPeer() != null && info.getTeamPeer().contains(name);
        } finally {
            peer.unlockRead();
        }
    }

    public void sendMessage(String name, Serializable message) throws IOException {
        setCall();
        peer.lockWrite();
        try {
            if (messageManager == null) {
                throw new IOException("You are not on a team.");
            }
            messageManager.sendMessage(name, message);
        } finally {
            peer.unlockWrite();
        }
    }

    public void broadcastMessage(Serializable message) throws IOException {
        setCall();
        peer.lockWrite();
        try {
            if (messageManager == null) {
                throw new IOException("You are not on a team.");
            }
            messageManager.sendMessage(null, message);
        } finally {
            peer.unlockWrite();
        }
    }

    // events
    public List<MessageEvent> getMessageEvents() {
        getCall();
        peer.lockRead();
        try {
            return messageManager.getMessageEvents();
        } finally {
            peer.unlockRead();
        }
    }
}
