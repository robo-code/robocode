/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;

import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;
/**
 * Insert the type's description here.
 * Creation date: (9/18/2001 1:16:08 PM)
 * @author: Administrator
 */
public class RobotStatistics implements robocode.peer.ContestantStatistics{
	boolean noScoring = false;
	private double bulletDamageScore = 0.0;
	private double rammingDamageScore = 0.0;
	private double survivalScore = 0.0;
	private double winnerScore = 0.0;
	private double totalWinnerScore = 0.0;
	private double killedEnemyRammingScore = 0.0;
	private double killedEnemyBulletScore = 0.0;
	private double totalScore = 0.0;
	private double totalBulletDamageScore = 0.0;
	private double totalBulletDamageDealt = 0.0;
	private double totalBulletDamageReceived = 0.0;
	private double totalRammingDamageScore = 0.0;
	private double totalSurvivalScore = 0.0;
	private double totalKilledEnemyRammingScore = 0.0;
	private double totalKilledEnemyBulletScore = 0.0;
	private RobotPeer robotPeer;
	private TeamPeer teamPeer;
	private double rammingDamageDealt = 0.0;
	private double totalRammingDamageDealt = 0.0;
	private double bulletDamageDealt = 0.0;
	private double bulletDamageReceived = 0.0;
	private double rammingDamageReceived = 0.0;
	private double totalRammingDamageReceived = 0.0;

	private int totalFirsts = 0;
	private int totalSeconds = 0;
	private int totalThirds = 0;

	private double robotDamage[] = null;
/**
 * RobotStatistics constructor comment.
 */
public RobotStatistics(RobotPeer robotPeer) {
	super();
	this.robotPeer = robotPeer;
	this.teamPeer = robotPeer.getTeamPeer();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:21:41 PM)
 * @param damage double
 */
public void damagedByBullet(double damage) {
	bulletDamageReceived += damage;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:21:41 PM)
 * @param damage double
 */
public void damagedByRamming(double damage) {
	rammingDamageReceived += damage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void generateTotals() {
	totalBulletDamageScore += bulletDamageScore;
	totalRammingDamageScore += rammingDamageScore;
	totalSurvivalScore += survivalScore;
	totalKilledEnemyBulletScore += killedEnemyBulletScore;
	totalKilledEnemyRammingScore += killedEnemyRammingScore;
	totalWinnerScore += winnerScore;
	totalBulletDamageDealt += bulletDamageDealt;
	totalBulletDamageReceived += bulletDamageReceived;
	totalRammingDamageDealt += rammingDamageDealt;
	totalRammingDamageReceived += rammingDamageReceived;
	totalScore = totalBulletDamageScore +
				totalRammingDamageScore +
				totalSurvivalScore +
				totalKilledEnemyRammingScore +
				totalKilledEnemyBulletScore +
				totalWinnerScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 11:31:48 AM)
 * @return double[]
 */
private double[] getRobotDamage() {
	if (robotDamage == null)
	{
		robotDamage = new double[robotPeer.getBattle().getRobots().size()];
		for (int i = 0; i < robotPeer.getBattle().getRobots().size(); i++)
		{
			robotDamage[i] = 0.0;
		}
	}
	return robotDamage;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:38:57 PM)
 * @return double
 */
public double getTotalBulletDamageDealt() {
	return totalBulletDamageDealt;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:04 PM)
 * @return double
 */
public double getTotalBulletDamageReceived() {
	return totalBulletDamageReceived;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:09 PM)
 * @return double
 */
public double getTotalBulletDamageScore() {
	return totalBulletDamageScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:42:53 PM)
 * @return int
 */
public int getTotalFirsts() {
	return totalFirsts;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:13 PM)
 * @return double
 */
public double getTotalKilledEnemyBulletScore() {
	return totalKilledEnemyBulletScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:13 PM)
 * @return double
 */
public double getTotalKilledEnemyRammingScore() {
	return totalKilledEnemyRammingScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:18 PM)
 * @return double
 */
public double getTotalRammingDamageDealt() {
	return totalRammingDamageDealt;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:22 PM)
 * @return double
 */
public double getTotalRammingDamageReceived() {
	return totalRammingDamageReceived;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:27 PM)
 * @return double
 */
public double getTotalRammingDamageScore() {
	return totalRammingDamageScore;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:46:22 PM)
 * @return double
 */
public double getTotalScore() {
	return totalScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:42:53 PM)
 * @return int
 */
public int getTotalSeconds() {
	return totalSeconds;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:37 PM)
 * @return double
 */
public double getTotalSurvivalScore() {
	return totalSurvivalScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:42:53 PM)
 * @return int
 */
public int getTotalThirds() {
	return totalThirds;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:39:43 PM)
 * @return double
 */
public double getTotalWinnerScore() {
	return totalWinnerScore;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:02:41 PM)
 */
public void initializeRound() {
	bulletDamageScore = 0;
	rammingDamageScore = 0;
	killedEnemyRammingScore = 0;
	killedEnemyBulletScore = 0;
	survivalScore = 0;
	winnerScore = 0;

	bulletDamageDealt = 0;
	bulletDamageReceived = 0;
	rammingDamageDealt = 0;
	rammingDamageReceived = 0;
	
	noScoring = false;

	robotDamage = null;
	
}
private boolean isTeammate(int robot)
{
	if (teamPeer != null && teamPeer == robotPeer.getBattle().getRobots().elementAt(robot).getTeamPeer())
		return true;
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void scoreBulletDamage(int robot, double damage) {
	if (isTeammate(robot))
		return;
	if (!noScoring)
	{
		getRobotDamage()[robot] += damage;
		bulletDamageScore += damage;
	}
	bulletDamageDealt += damage;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:41:24 PM)
 * @param robotsRemaining int
 */
public void scoreDeath(int enemiesRemaining) {
//	if (teamPeer != null && !robotPeer.isTeamLeader())
//		return;
		
	if (enemiesRemaining == 0)
		totalFirsts++;
	if (enemiesRemaining == 1)
		totalSeconds++;
	else if (enemiesRemaining == 2)
		totalThirds++;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void scoreKilledEnemyBullet(int robot) {
	if (isTeammate(robot))
		return;

	if (!noScoring)
	{
		double bonus = 0;
		if (teamPeer == null)
			bonus = getRobotDamage()[robot] * .2;
		else
			for (int i = 0; i < teamPeer.size(); i++)
				bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .2;
				
		robotPeer.out.println("SYSTEM: Bonus for killing " + ((RobotPeer)robotPeer.getBattle().getRobots().elementAt(robot)).getName() + ": " + (int)(bonus + .5));
		killedEnemyBulletScore += bonus;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void scoreKilledEnemyRamming(int robot) {
	if (isTeammate(robot))
		return;
	if (!noScoring)
	{
		double bonus = 0;
		if (teamPeer == null)
			bonus = getRobotDamage()[robot] * .3;
		else
			for (int i = 0; i < teamPeer.size(); i++)
				bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .3;
		robotPeer.out.println("SYSTEM: Ram bonus for killing " + ((RobotPeer)robotPeer.getBattle().getRobots().elementAt(robot)).getName() + ": " + (int)(bonus + .5));
		killedEnemyRammingScore += bonus;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void scoreRammingDamage(int robot, double damage) {
	if (isTeammate(robot))
		return;
	if (!noScoring)
	{
		getRobotDamage()[robot] += damage;
		rammingDamageScore += 2.0 * damage;
	}
	rammingDamageDealt += damage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:48:04 PM)
 * @return double
 */
public void scoreSurvival() {
	if (!noScoring)
		survivalScore += 50;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 3:47:30 PM)
 */
public void scoreWinner() {
	if (!noScoring)
	{
		int enemyCount = robotPeer.getBattle().getRobots().size() - 1;
		if (teamPeer != null)
		{
			enemyCount -= (teamPeer.size() - 1);
			//System.out.println("team is subtracting " + teamPeer.size() + " others.");
		}
//		System.out.println("Scoring winner for " + enemyCount + " others.");
		winnerScore += 10 * enemyCount;
		if (teamPeer != null && !robotPeer.isTeamLeader())
			return;
		totalFirsts++;
	}
}
public void scoreFirsts() {
	if (!noScoring)
	{
		totalFirsts++;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:03:43 PM)
 */
public void setNoScoring(boolean noScoring) {
	this.noScoring = noScoring;
	if (noScoring)
	{
		bulletDamageScore = 0;
		rammingDamageScore = 0;
		killedEnemyRammingScore = 0;
		killedEnemyBulletScore = 0;
		survivalScore = 0;
		winnerScore = 0;
		bulletDamageDealt = 0;
		bulletDamageReceived = 0;
		rammingDamageDealt = 0;
		rammingDamageReceived = 0;
	}
}
}
