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

/**
 * @author Pavel Savara (original)
 */
public class R32obotPeerTeam extends R31obotPeerFiles {
    private TeamPeer teamPeer;

    public void initTeam(TeamPeer tp){
        teamPeer=tp;
    }

    public TeamPeer getTeamPeer() {
        return teamPeer;
    }

    public String[] getTeammates() {
		robocode.peer.TeamPeer teamPeer = getTeamPeer();

		if (teamPeer == null) {
			return null;
		}
		String s[] = new String[teamPeer.size() - 1];

		int index = 0;

		for (RobotPeer teammate : teamPeer) {
			if (teammate != (RobotPeer)this) {
				s[index++] = teammate.getName();
			}
		}
		return s;
	}

	public boolean isTeammate(String name) {
		if (getTeamPeer() == null) {
			return false;
		}
		return getTeamPeer().contains(name);
	}

    public boolean isTeamLeader() {
        return (getTeamPeer() != null && getTeamPeer().getTeamLeader() == this);
    }
    
}
