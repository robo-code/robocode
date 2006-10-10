/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten for Java 5
 *******************************************************************************/
package robocode.peer;


import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class TeamPeer extends Vector<RobotPeer> implements ContestantPeer {

	private String name;
	private RobotPeer teamLeader;
	private TeamStatistics teamStatistics;
	
	public TeamPeer(String name) {
		this.name = name;
		this.teamStatistics = new TeamStatistics(this);
	}

	public int compareTo(ContestantPeer cp) {
		return (int) (cp.getStatistics().getTotalScore() + cp.getStatistics().getCurrentScore() + 0.5) -
			(int) (teamStatistics.getTotalScore() + teamStatistics.getCurrentScore() + 0.5);
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
	
	public boolean add(RobotPeer r) {
		if (teamLeader == null) {
			teamLeader = r;
		}
		return super.add(r);
	}
	
	public boolean contains(String s) {
		if (s == null) {
			return false;
		}
		for (RobotPeer r : this) {
			if (s.equals(r.getName())) {
				return true;
			}
		}
		return false;
	}
}
