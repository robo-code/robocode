/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.peer;


import robocode.BattleResults;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 */
class TeamStatistics implements ContestantStatistics {

	private final TeamPeer teamPeer;
	private int rank;

	TeamStatistics(TeamPeer teamPeer) {
		this.teamPeer = teamPeer;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getTotalScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalScore();
		}
		return d;
	}

	public double getTotalSurvivalScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalSurvivalScore();
		}
		return d;
	}

	public double getTotalLastSurvivorBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalLastSurvivorBonus();
		}
		return d;
	}

	public double getTotalBulletDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletDamageScore();
		}
		return d;
	}

	public double getTotalBulletKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletKillBonus();
		}
		return d;
	}

	public double getTotalRammingDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingDamageScore();
		}
		return d;
	}

	public double getTotalRammingKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingKillBonus();
		}
		return d;
	}

	public int getTotalFirsts() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalFirsts();
		}
		return d;
	}

	public int getTotalSeconds() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalSeconds();
		}
		return d;
	}

	public int getTotalThirds() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalThirds();
		}
		return d;
	}

	public double getCurrentScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentScore();
		}
		return d;
	}

	public double getCurrentSurvivalScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentSurvivalScore();
		}
		return d;
	}

	public double getCurrentSurvivalBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentSurvivalBonus();
		}
		return d;
	}

	public double getCurrentBulletDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentBulletDamageScore();
		}
		return d;
	}

	public double getCurrentBulletKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentBulletKillBonus();
		}
		return d;
	}

	public double getCurrentRammingDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentRammingDamageScore();
		}
		return d;
	}

	public double getCurrentRammingKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentRammingKillBonus();
		}
		return d;
	}

	public BattleResults getFinalResults() {
		return new BattleResults(teamPeer.getName(), rank, getTotalScore(), getTotalSurvivalScore(),
				getTotalLastSurvivorBonus(), getTotalBulletDamageScore(), getTotalBulletKillBonus(),
				getTotalRammingDamageScore(), getTotalRammingKillBonus(), getTotalFirsts(), getTotalSeconds(),
				getTotalThirds());
	}
}
