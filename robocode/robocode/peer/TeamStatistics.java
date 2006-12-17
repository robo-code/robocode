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
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *******************************************************************************/
package robocode.peer;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo, Flemming N. Larsen (current)
 */
public class TeamStatistics implements ContestantStatistics {

	private TeamPeer teamPeer;
	
	public TeamStatistics(TeamPeer teamPeer) {
		this.teamPeer = teamPeer;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageDealt()
	 */
	public double getTotalBulletDamageDealt() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletDamageDealt();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageReceived()
	 */
	public double getTotalBulletDamageReceived() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletDamageReceived();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalBulletDamageScore()
	 */
	public double getTotalBulletDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletDamageScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalFirsts()
	 */
	public int getTotalFirsts() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalFirsts();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalKilledEnemyBulletScore()
	 */
	public double getTotalKilledEnemyBulletScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalKilledEnemyBulletScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalKilledEnemyRammingScore()
	 */
	public double getTotalKilledEnemyRammingScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalKilledEnemyRammingScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageDealt()
	 */
	public double getTotalRammingDamageDealt() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingDamageDealt();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageReceived()
	 */
	public double getTotalRammingDamageReceived() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingDamageReceived();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalRammingDamageScore()
	 */
	public double getTotalRammingDamageScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingDamageScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalScore()
	 */
	public double getTotalScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalSeconds()
	 */
	public int getTotalSeconds() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalSeconds();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalSurvivalScore()
	 */
	public double getTotalSurvivalScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalSurvivalScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalThirds()
	 */
	public int getTotalThirds() {
		int d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalThirds();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getTotalWinnerScore()
	 */
	public double getTotalWinnerScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalWinnerScore();
		}
		return d;
	}

	/*
	 * @see ContestantStatistics#getCurrentScore()
	 */
	public double getCurrentScore() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getCurrentScore();
		}
		return d;
	}
}
