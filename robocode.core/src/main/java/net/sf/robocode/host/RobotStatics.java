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
package net.sf.robocode.host;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.security.HiddenAccess;
import robocode.BattleRules;
import robocode.control.RobotSpecification;

import java.util.List;


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
	private final String fullClassName;
	private final String shortClassName;
	private final BattleRules battleRules;
	private final String[] teammates;
	private final String teamName;
	private final int index;
	private final int contestantIndex;

	public RobotStatics(RobotSpecification robotSpecification, int duplicate, boolean isLeader, BattleRules rules, String teamName, List<String> teamMembers, int index, int contestantIndex) {
		IRobotRepositoryItem specification = ((IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification));

		shortClassName = specification.getShortClassName();
		fullClassName = specification.getFullClassName();
		if (duplicate >= 0) {
			String countString = " (" + (duplicate + 1) + ')';

			name = specification.getFullClassNameWithVersion() + countString;
			shortName = specification.getUniqueShortClassNameWithVersion() + countString;
			veryShortName = specification.getUniqueVeryShortClassNameWithVersion() + countString;
		} else {
			name = specification.getFullClassNameWithVersion();
			shortName = specification.getUniqueShortClassNameWithVersion();
			veryShortName = specification.getUniqueVeryShortClassNameWithVersion();
		}

		isJuniorRobot = specification.isJuniorRobot();
		isInteractiveRobot = specification.isInteractiveRobot();
		isPaintRobot = specification.isPaintRobot();
		isAdvancedRobot = specification.isAdvancedRobot();
		isTeamRobot = specification.isTeamRobot();
		isDroid = specification.isDroid();
		isTeamLeader = isLeader;
		battleRules = rules;
		this.index = index;
		this.contestantIndex = contestantIndex;

		if (teamMembers != null) {
			teammates = new String[teamMembers.size() - 1];
			int i = 0;

			for (String mate : teamMembers) {
				if (!name.equals(mate)) {
					teammates[i++] = mate;
				}
			}
			this.teamName = teamName;
		} else {
			teammates = null;
			this.teamName = name;
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

	public String getFullClassName() {
		return fullClassName;
	}

	public String getShortClassName() {
		return shortClassName;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public String[] getTeammates() {
		return teammates == null ? null : teammates.clone();
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
