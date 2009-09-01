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
 *     - Bugfix: scoreDeath() incremented totalFirsts even if the robot was
 *       already a winner, where scoreWinner() has already been called previously
 *     - Added constructor that takes an additonal RobotResults that must be
 *       copied into this object and added the getResults() in order to support
 *       the replay feature
 *     - Changed the survivalScore and totalSurvivalScore fields to be integers
 *     - Renamed method names and removed unused methods
 *     - Ordered all methods more naturally
 *     - Added methods for getting current scores
 *     - Optimizations
 *     - Removed damage parameter from the scoreRammingDamage() method, as the
 *       damage is constant and defined by Rules.ROBOT_HIT_DAMAGE and the score
 *       of hitting a robot is defined by Rules.ROBOT_HIT_BONUS
 *     Titus Chen
 *     - Bugfix: Initial getResults() method only factored in the most recent
 *       round
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *     Joshua Galecki
 *     - Moved RobotStatistics to ContestantStatistics as an abstract base class
 *       so customized scoring is possible from extensions;
 *******************************************************************************/

package net.sf.robocode.battle.peer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.robocode.battle.IContestantStatistics;

import robocode.BattleResults;


/**
 * The base class of robot statistics
 * 
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Joshua Galecki (contributor)
 */
public abstract class ContestantStatistics implements IContestantStatistics {

	protected final RobotPeer robotPeer;
	protected int rank;
	protected final int robots;
	protected boolean isActive;
	protected boolean isInRound;

	protected List<Double> totalScores;
	protected List<String> scoreNames;
	protected List<Double> currentScores;

	protected Map<String, Double> robotDamageMap;

	protected double combinedScore;
	
	protected int totalFirsts;
	protected int totalSeconds;
	protected int totalThirds;
	
	/**
	 * Junk constructor
	 */
	public ContestantStatistics() {
		robots = 0;
		robotPeer = null;
	}
	
	public ContestantStatistics(RobotPeer robotPeer, int robots) {
		this.robotPeer = robotPeer;
		this.robots = robots;
		currentScores = new ArrayList<Double>();
		totalScores = new ArrayList<Double>();
		scoreNames = new ArrayList<String>();
	}

	public ContestantStatistics(RobotPeer robotPeer, int robots, BattleResults results) {
		this(robotPeer, robots);
		currentScores = new ArrayList<Double>();
		totalScores = new ArrayList<Double>();
		scoreNames = new ArrayList<String>();

		combinedScore = results.getCombinedScore();
		totalScores = results.getScores();
		scoreNames = results.getScoreNames();
		totalFirsts = results.getFirsts();
		totalSeconds = results.getSeconds();
		totalThirds = results.getThirds();
		
		currentScores = results.getScores();
		resetScores();
	}

	public void cleanup() {} // do nothing for now.
	
	public void generateTotals() {
		if (currentScores.size() != totalScores.size()) {
			// something went wrong
			totalScores.get(1);
		}
		combinedScore = 0;
		for (int scoreIndex = 0; scoreIndex < currentScores.size() && scoreIndex < totalScores.size(); scoreIndex++) {
			totalScores.set(scoreIndex, totalScores.get(scoreIndex) + currentScores.get(scoreIndex));
			combinedScore += totalScores.get(scoreIndex);
		}
		
		isInRound = false;
	}
	
	public double getCurrentScore() {
		return getCombinedScore();
	}

	public List<Double> getCurrentScores() {
		return currentScores;
	}

	public BattleResults getFinalResults() {
		return new BattleResults(getTeamName(), rank, (int) combinedScore, totalScores, scoreNames, totalFirsts,
				totalSeconds, totalThirds);
	}
	
	protected abstract String getTeamName();

	public Collection<Double> getRobotDamage() {
		return robotDamageMap.values();
	}

	public List<String> getScoreNames() {
		return scoreNames;
	}

	public List<Double> getTotalScores() {
		return totalScores;
	}	

	public double getCombinedScore() {
		return combinedScore;
	}

	public double getCurrentCombinedScore() {
		double scoreSum = 0;

		for (double score : currentScores) {
			scoreSum += score;
		}
		
		return scoreSum;
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

	public boolean isInRound() {
		return isInRound;
	}
	
	public void initialize() {
		resetScores();

		isActive = true;
		isInRound = true;
	}

	public void resetScores() {
		robotDamageMap = null;

		for (int scoreIndex = 0; scoreIndex < currentScores.size(); scoreIndex++) {
			currentScores.set(scoreIndex, 0.0);
		}
	}

	public void setInactive() {
		resetScores();
		isActive = false;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	protected double getRobotDamage(String robot) {
		if (robotDamageMap == null) {
			robotDamageMap = new HashMap<String, Double>();
		}
		Double damage = robotDamageMap.get(robot);

		return (damage != null) ? damage : 0;
	}

	protected void incrementRobotDamage(String robot, double damage) {
		double newDamage = getRobotDamage(robot) + damage;

		robotDamageMap.put(robot, newDamage);
	}
}
