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
 *     Flemming N. Larsen
 *     - Changed to be consistent with the battle results and ranking scores
 *     - This class now implements java.io.Serializable
 *     - Updated Javadocs
 *******************************************************************************/
package robocode.control;


import robocode.BattleResults;


/**
 * Contains the battle results for an individual robot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotResults extends BattleResults {

	private static final long serialVersionUID = 2L;

	private final RobotSpecification robot;

	/**
	 * Constructs new RobotResults.
	 *
	 * @param robot			 the robot these results are for
	 * @param teamLeaderName	team name
	 * @param rank			  the rank of the robot in the battle
	 * @param score			 the total score for the robot in the battle
	 * @param survival		  the survival score for the robot in the battle
	 * @param lastSurvivorBonus the last survivor bonus for the robot in the battle
	 * @param bulletDamage	  the bullet damage score for the robot in the battle
	 * @param bulletDamageBonus the bullet damage bonus for the robot in the battle
	 * @param ramDamage		 the ramming damage for the robot in the battle
	 * @param ramDamageBonus	the ramming damage bonus for the robot in the battle
	 * @param firsts			the number of rounds this robot placed first
	 * @param seconds		   the number of rounds this robot placed second
	 * @param thirds			the number of rounds this robot placed third
	 */
	public RobotResults(
			RobotSpecification robot,
			String teamLeaderName,
			int rank,
			double score,
			double survival,
			double lastSurvivorBonus,
			double bulletDamage,
			double bulletDamageBonus,
			double ramDamage,
			double ramDamageBonus,
			int firsts,
			int seconds,
			int thirds
			) {
		// TODO: this needs to change...
		scoreNames.add("Survival");
		scores.add(survival);
		scoreNames.add("Last Survivor Bonus");
		scores.add(lastSurvivorBonus);
		scoreNames.add("Bullet Damage");
		scores.add(bulletDamage);
		scoreNames.add("Bullet Damage Bonus");
		scores.add(bulletDamageBonus);
		scoreNames.add("Ram Damage");
		scores.add(ramDamage);
		scoreNames.add("ramDamageBonus");
		
		teamName = teamLeaderName;
		this.rank = rank;
		combinedScore = (int) score;
		this.firsts = firsts;
		this.seconds = seconds;
		this.thirds = thirds;
		
		this.robot = robot;
	}

	/**
	 * Constructs new RobotResults based on a {@link RobotSpecification} and {@link robocode.BattleResults
	 * BattleResults}.
	 *
	 * @param robot   the robot these results are for
	 * @param results the battle results for the robot
	 */
	public RobotResults(
			RobotSpecification robot,
			BattleResults results) {		
		super(results.getTeamName(), results.getRank(), results.getCombinedScore(), results.getScores(),
				results.getScoreNames(), results.getFirsts(), results.getSeconds(), results.getThirds());
		this.robot = robot;
	}

	/**
	 * Returns the robot these results are meant for.
	 *
	 * @return the robot these results are meant for.
	 */
	public RobotSpecification getRobot() {
		return robot;
	}

	/**
	 * Converts an array of {@link BattleResults} into an array of {@link RobotResults}.
	 *
	 * @param results an array of BattleResults to convert.
	 * @return an array of RobotResults converted from BattleResults.
	 * @since 1.6.2
	 */
	public static RobotResults[] convertResults(BattleResults[] results) {
		RobotResults[] resultsConv = new RobotResults[results.length];

		for (int i = 0; i < results.length; i++) {
			resultsConv[i] = (RobotResults) results[i];
		}
		return resultsConv;
	}
}
