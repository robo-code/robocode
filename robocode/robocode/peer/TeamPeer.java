/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Rewritten for Java 5
 *     - Changed contains(String) into contains(Object), as the first listed
 *       shadowed the second one
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.peer;


import java.util.ArrayList;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class TeamPeer extends ArrayList<RobotPeer> implements ContestantPeer {

	private String name;
	private RobotPeer teamLeader;
	private TeamStatistics teamStatistics;

	public TeamPeer(String name) {
		this.name = name;
		this.teamStatistics = new TeamStatistics(this);
	}

	public int compareTo(ContestantPeer cp) {
		double score1 = teamStatistics.getTotalScore();
		double score2 = cp.getStatistics().getTotalScore();

		if (teamLeader.getBattle().isRunning()) {
			score1 += teamStatistics.getCurrentScore();
			score2 += cp.getStatistics().getCurrentScore();
		}
		return (int) (score2 + 0.5) - (int) (score1 + 0.5);
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

	@Override
	public boolean add(RobotPeer r) {
		if (teamLeader == null) {
			teamLeader = r;
		}
		return super.add(r);
	}

	@Override
	public boolean contains(Object s) {
		if (s != null && s instanceof String) {
			for (RobotPeer r : this) {
				if (s.equals(r.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
