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
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Renamed method names and removed unused methods
 *     - Added methods for getting current scores
 *******************************************************************************/
package net.sf.robocode.battle;


import java.util.List;

import net.sf.robocode.battle.peer.RobotPeer;

import robocode.BattleResults;


/**
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (contributor)
 * @author Flemming N. Larsen (contributor)
 */
public interface IContestantStatistics {
	
	public IContestantStatistics fakeConstructor(RobotPeer peer, int robots);
	
	public void generateTotals();
	
	public double getCombinedScore();
	
	public List<Double> getTotalScores();
	
	public List<String> getScoreNames();
	
	public List<Double> getCurrentScores();
	
	public int getTotalFirsts();

	public int getTotalSeconds();

	public int getTotalThirds();

	public double getCurrentScore();
	
	public double getCurrentCombinedScore();

	public BattleResults getFinalResults();

	public void initialize();
	
	public boolean isInRound();
	
	public void resetScores();
	
	public void setInactive();
	
	public void setRank(int rank);
	
	public void scoreFirsts();

	public void scoreRobotDeath(int enemiesRemaining);
	
	public double scoreRammingKill(String robot);
	
	public void scoreRammingDamage(String robot);
	
	public double scoreBulletKill(String robot);
	
	public void scoreBulletDamage(String robot, double damage);
	
	public void scoreLastSurvivor();
	
	public void scoreSurvival();

	public void cleanup();
}
