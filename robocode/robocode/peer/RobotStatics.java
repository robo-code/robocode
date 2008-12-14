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


import robocode.BattleRules;
import robocode.control.RobotSpecification;

import java.util.List;

import net.sf.robocode.peer.IRobotStatics;


/**
 * @author Pavel Savara (original)
 */
public final class RobotStatics implements IRobotStatics {
	private final boolean isJuniorRobot;
	private final boolean isInteractiveRobot;
	private final boolean isPaintRobot;
	private final boolean isAdvancedRobot;
	private final boolean isTeamRobot;
	private final boolean isTeamLeader;
	private final boolean isDroid;
	private final String name;
	private final String shortName;
	private final String veryShortName;
	private final String nonVersionedName;
	private final String shortClassName;
	private final BattleRules battleRules;
	private final String[] teammates;
	private final String teamName;
	private final int index;
	private final int contestantIndex;

	public RobotStatics(RobotSpecification robotSpecification, int duplicate, boolean isLeader, BattleRules rules, TeamPeer team, int index, int contestantIndex) {
		if (duplicate >= 0) {
			String countString = " (" + (duplicate + 1) + ')';

			name = robotSpecification.getFullClassNameWithVersion() + countString;
			shortName = robotSpecification.getUniqueShortClassNameWithVersion() + countString;
			veryShortName = robotSpecification.getUniqueVeryShortClassNameWithVersion() + countString;
			nonVersionedName = robotSpecification.getClassName() + countString;
		} else {
			name = robotSpecification.getFullClassNameWithVersion();
			shortName = robotSpecification.getUniqueShortClassNameWithVersion();
			veryShortName = robotSpecification.getUniqueVeryShortClassNameWithVersion();
			nonVersionedName = robotSpecification.getClassName();
		}
		shortClassName = robotSpecification.getShortClassName();
		isJuniorRobot = robotSpecification.isJuniorRobot();
		isInteractiveRobot = robotSpecification.isInteractiveRobot();
		isPaintRobot = robotSpecification.isPaintRobot();
		isAdvancedRobot = robotSpecification.isAdvancedRobot();
		isTeamRobot = robotSpecification.isTeamRobot();
		isDroid = robotSpecification.isDroid();
		isTeamLeader = isLeader;
		battleRules = rules;
		this.index = index;
		this.contestantIndex = contestantIndex;

		if (team != null) {
			List<String> memberNames = team.getMemberNames();

			teammates = new String[memberNames.size() - 1];
			int i = 0;

			for (String mate : memberNames) {
				if (!name.equals(mate)) {
					teammates[i++] = mate;
				}
			}
			teamName = team.getName();
		} else {
			teammates = new String[0];
			teamName = name;
		}
	}

	public boolean isJuniorRobot() {
		return isJuniorRobot;
	}

	public boolean isInteractiveRobot() {
		return isInteractiveRobot;
	}

	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	public boolean isAdvancedRobot() {
		return isAdvancedRobot;
	}

	public boolean isTeamRobot() {
		return isTeamRobot;
	}

	public boolean isTeamLeader() {
		return isTeamLeader;
	}

	public boolean isDroid() {
		return isDroid;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getVeryShortName() {
		return veryShortName;
	}

	public String getNonVersionedName() {
		return nonVersionedName;
	}

	public String getShortClassName() {
		return shortClassName;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public String[] getTeammates() {
		return teammates.clone();
	}

	public String getTeamName() {
		return teamName;
	}

	public int getIndex() {
		return index;
	}

	public int getContestIndex() {
		return contestantIndex;
	}
}
