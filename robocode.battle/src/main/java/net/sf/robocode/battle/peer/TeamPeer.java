/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
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
 *     Pavel Savara
 *     - member names are known in constructor
 *******************************************************************************/
package net.sf.robocode.battle.peer;


import java.util.ArrayList;
import java.util.List;

import net.sf.robocode.battle.IContestantStatistics;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class TeamPeer extends ArrayList<RobotPeer> implements ContestantPeer {

	private final List<String> memberNames;
	private final String name;
	private final int contestIndex;
	private RobotPeer teamLeader;
	private final IContestantStatistics teamStatistics;

	public TeamPeer(String name, List<String> memberNames, int contestIndex) {
		this.name = name;
		this.contestIndex = contestIndex;
		this.memberNames = memberNames;
		this.teamStatistics = new ClassicTeamStatistics(this);
	}

	public int compareTo(ContestantPeer cp) {
		double myScore = teamStatistics.getCombinedScore();
		double hisScore = cp.getStatistics().getCombinedScore();

		if (teamLeader != null && teamLeader.getRobotStatistics().isInRound()) {
			myScore += teamStatistics.getCurrentScore();
			hisScore += cp.getStatistics().getCurrentScore();
		}
		if (myScore < hisScore) {
			return -1;
		}
		if (myScore > hisScore) {
			return 1;
		}
		return 0;
	}

	public IContestantStatistics getStatistics() {
		return teamStatistics;
	}

	public String getName() {
		return name;
	}

	public int getContestIndex() {
		return contestIndex;
	}

	public List<String> getMemberNames() {
		return memberNames;
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
	public String toString() {
		return " [" + size() + "] " + getName();
	}
}
