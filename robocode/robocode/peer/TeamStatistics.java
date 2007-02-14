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
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Renamed method names and removed unused methods
 *     - Ordered all methods more naturally
 *******************************************************************************/
package robocode.peer;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 */
public class TeamStatistics implements ContestantStatistics {

	private TeamPeer teamPeer;

	public TeamStatistics(TeamPeer teamPeer) {
		this.teamPeer = teamPeer;
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
	 * @see ContestantStatistics#getTotalLastSurvivalBonus()
	 */
	public double getTotalLastSurvivalBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalLastSurvivalBonus();
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
	 * @see ContestantStatistics#getTotalBulletKillBonus()
	 */
	public double getTotalBulletKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalBulletKillBonus();
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
	 * @see ContestantStatistics#getTotalRammingKillBonus()
	 */
	public double getTotalRammingKillBonus() {
		double d = 0;

		for (RobotPeer teammate : teamPeer) {
			d += teammate.getRobotStatistics().getTotalRammingKillBonus();
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
