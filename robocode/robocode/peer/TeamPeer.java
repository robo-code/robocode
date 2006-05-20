/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer;


import java.util.*;


public class TeamPeer implements ContestantPeer {
	private String name = null;
	private Vector peerVector = new Vector();
	private RobotPeer teamLeader = null;
	private TeamStatistics teamStatistics;
		
	public TeamPeer(String name) {
		this.name = name;
		this.teamStatistics = new TeamStatistics(this);
	}
	
	public int compareTo(Object o) {
		if (!(o instanceof ContestantPeer)) {
			return 0;
		}
		ContestantPeer r = (ContestantPeer) o;

		if (r.getStatistics().getTotalScore() > getStatistics().getTotalScore()) {
			return 1;
		} else if (r.getStatistics().getTotalScore() < getStatistics().getTotalScore()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public ContestantStatistics getStatistics() {
		return teamStatistics;
	}

	public String getName() {
		return name;
	}

	public RobotPeer getTeamLeader() {
		return teamLeader;
	}
	
	public void add(RobotPeer peer) {
		if (!peerVector.contains(peer)) {
			peerVector.add(peer);
		}
		if (teamLeader == null) {
			teamLeader = peer;
		}
	}
	
	public boolean contains(String s) {
		if (s == null) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (s.equals(elementAt(i).getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(RobotPeer peer) {
		return peerVector.contains(peer);
	}
	
	public int size() {
		return peerVector.size();
	}
	
	public RobotPeer elementAt(int index) {
		return (RobotPeer) peerVector.elementAt(index);
	}
}

