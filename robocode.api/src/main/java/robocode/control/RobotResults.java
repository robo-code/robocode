/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
	 * Constructs a new RobotResults.
	 *
	 * @param robot is the robot these results are for
	 * @param teamLeaderName is the team name
	 * @param rank is the rank of the robot in the battle
	 * @param score is the total score for the robot in the battle
	 * @param survival is the survival score for the robot in the battle
	 * @param lastSurvivorBonus is the last survivor bonus for the robot in the battle
	 * @param bulletDamage is the bullet damage score for the robot in the battle
	 * @param bulletDamageBonus is the bullet damage bonus for the robot in the battle
	 * @param ramDamage is the ramming damage for the robot in the battle
	 * @param ramDamageBonus is the ramming damage bonus for the robot in the battle
	 * @param firsts is the number of rounds this robot placed first
	 * @param seconds is the number of rounds this robot placed second
	 * @param thirds is the number of rounds this robot placed third
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
		super(teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage, bulletDamageBonus, ramDamage,
				ramDamageBonus, firsts, seconds, thirds);
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
		super(results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(),
				results.getLastSurvivorBonus(), results.getBulletDamage(), results.getBulletDamageBonus(),
				results.getRamDamage(), results.getRamDamageBonus(), results.getFirsts(), results.getSeconds(),
				results.getThirds());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;

		temp = Double.doubleToLongBits(bulletDamage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(bulletDamageBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + firsts;
		temp = Double.doubleToLongBits(lastSurvivorBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ramDamage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ramDamageBonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + rank;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + seconds;
		temp = Double.doubleToLongBits(survival);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((teamLeaderName == null) ? 0 : teamLeaderName.hashCode());
		result = prime * result + thirds;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RobotResults other = (RobotResults) obj;

		if (Double.doubleToLongBits(bulletDamage) != Double.doubleToLongBits(other.bulletDamage)) {
			return false;
		}
		if (Double.doubleToLongBits(bulletDamageBonus) != Double.doubleToLongBits(other.bulletDamageBonus)) {
			return false;
		}
		if (firsts != other.firsts) {
			return false;
		}
		if (Double.doubleToLongBits(lastSurvivorBonus) != Double.doubleToLongBits(other.lastSurvivorBonus)) {
			return false;
		}
		if (Double.doubleToLongBits(ramDamage) != Double.doubleToLongBits(other.ramDamage)) {
			return false;
		}
		if (Double.doubleToLongBits(ramDamageBonus) != Double.doubleToLongBits(other.ramDamageBonus)) {
			return false;
		}
		if (rank != other.rank) {
			return false;
		}
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score)) {
			return false;
		}
		if (seconds != other.seconds) {
			return false;
		}
		if (Double.doubleToLongBits(survival) != Double.doubleToLongBits(other.survival)) {
			return false;
		}
		if (teamLeaderName == null) {
			if (other.teamLeaderName != null) {
				return false;
			}
		} else if (!teamLeaderName.equals(other.teamLeaderName)) {
			return false;
		}
		if (thirds != other.thirds) {
			return false;
		}
		return true;
	}
}
