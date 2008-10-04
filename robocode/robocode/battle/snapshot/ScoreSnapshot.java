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
package robocode.battle.snapshot;


import robocode.peer.robot.RobotStatistics;

import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class ScoreSnapshot implements Comparable<ScoreSnapshot>, Serializable {
    private static final long serialVersionUID = 1L;

	private final String name;
	private final double totalScore;
	private final double totalSurvivalScore;
	private final double totalLastSurvivorBonus;
	private final double totalBulletDamageScore;
	private final double totalBulletKillBonus;
	private final double totalRammingDamageScore;
	private final double totalRammingKillBonus;
	private final int totalFirsts;
	private final int totalSeconds;
	private final int totalThirds;
	private final double currentScore;
	private final double currentSurvivalScore;
	private final double currentBulletDamageScore;
	private final double currentBulletKillBonus;
	private final double currentRammingDamageScore;
	private final double currentRammingKillBonus;

	public ScoreSnapshot(RobotStatistics statistics, String name) {
		this.name = name;
		totalScore = statistics.getTotalScore();
		totalSurvivalScore = statistics.getTotalSurvivalScore();
		totalLastSurvivorBonus = statistics.getTotalLastSurvivorBonus();
		totalBulletDamageScore = statistics.getTotalBulletDamageScore();
		totalBulletKillBonus = statistics.getTotalBulletKillBonus();
		totalRammingDamageScore = statistics.getTotalRammingDamageScore();
		totalRammingKillBonus = statistics.getTotalRammingKillBonus();
		totalFirsts = statistics.getTotalFirsts();
		totalSeconds = statistics.getTotalSeconds();
		totalThirds = statistics.getTotalThirds();
		currentScore = statistics.getCurrentScore();
		currentSurvivalScore = statistics.getCurrentSurvivalScore();
		currentBulletDamageScore = statistics.getCurrentBulletDamageScore();
		currentBulletKillBonus = statistics.getCurrentBulletKillBonus();
		currentRammingDamageScore = statistics.getCurrentRammingDamageScore();
		currentRammingKillBonus = statistics.getCurrentBulletKillBonus();
	}

	public ScoreSnapshot(ScoreSnapshot left, ScoreSnapshot right, String name) {
		this.name = name;
		totalScore = left.getTotalScore() + right.getTotalScore();
		totalSurvivalScore = left.getTotalSurvivalScore() + right.getTotalSurvivalScore();
		totalLastSurvivorBonus = left.getTotalLastSurvivorBonus() + right.getTotalLastSurvivorBonus();
		totalBulletDamageScore = left.getTotalBulletDamageScore() + right.getTotalBulletDamageScore();
		totalBulletKillBonus = left.getTotalBulletKillBonus() + right.getTotalBulletKillBonus();
		totalRammingDamageScore = left.getTotalRammingDamageScore() + right.getTotalRammingDamageScore();
		totalRammingKillBonus = left.getTotalRammingKillBonus() + right.getTotalRammingKillBonus();
		totalFirsts = left.getTotalFirsts() + right.getTotalFirsts();
		totalSeconds = left.getTotalSeconds() + right.getTotalSeconds();
		totalThirds = left.getTotalThirds() + right.getTotalThirds();
		currentScore = left.getCurrentScore() + right.getCurrentScore();
		currentSurvivalScore = left.getCurrentSurvivalScore() + right.getCurrentSurvivalScore();
		currentBulletDamageScore = left.getCurrentBulletDamageScore() + right.getCurrentBulletDamageScore();
		currentBulletKillBonus = left.getCurrentBulletKillBonus() + right.getCurrentBulletKillBonus();
		currentRammingDamageScore = left.getCurrentRammingDamageScore() + right.getCurrentRammingDamageScore();
		currentRammingKillBonus = left.getCurrentBulletKillBonus() + right.getCurrentBulletKillBonus();
	}

	public String getName() {
		return name;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public double getTotalLastSurvivorBonus() {
		return totalLastSurvivorBonus;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public double getTotalBulletKillBonus() {
		return totalBulletKillBonus;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalRammingKillBonus() {
		return totalRammingKillBonus;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getCurrentScore() {
		return currentScore;
	}

	public double getCurrentSurvivalScore() {
		return currentSurvivalScore;
	}

	public double getCurrentBulletDamageScore() {
		return currentBulletDamageScore;
	}

	public double getCurrentBulletKillBonus() {
		return currentBulletKillBonus;
	}

	public double getCurrentRammingDamageScore() {
		return currentRammingDamageScore;
	}

	public double getCurrentRammingKillBonus() {
		return currentRammingKillBonus;
	}

	public int compareTo(ScoreSnapshot o) {
		double myScore = getTotalScore();
		double hisScore = o.getTotalScore();

		myScore += getCurrentScore();
		hisScore += o.getCurrentScore();

		if (myScore < hisScore) {
			return -1;
		}
		if (myScore > hisScore) {
			return 1;
		}
		return 0;
	}
}
