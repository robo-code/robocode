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

/**
 * @author Pavel Savara (original)
 */
public class RobotScoreSnapshot {
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

    public RobotScoreSnapshot(RobotStatistics statistics) {
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
}
