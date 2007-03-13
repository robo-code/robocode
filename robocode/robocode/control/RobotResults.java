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
 *     Flemming N. Larsen
 *     - Changed to be consistent with the battle results and ranking scores
 *******************************************************************************/
package robocode.control;


/**
 * Contains results for a robot when a battle completes.
 * This class is returned from {@link RobocodeListener#battleComplete(robocode.control.BattleSpecification, robocode.control.RobotResults[])}
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotResults {
	private RobotSpecification robot;
	private int rank;
	private double score;
	private double survival;
	private double lastSurvivorBonus;
	private double bulletDamage;
	private double bulletDamageBonus;
	private double ramDamage;
	private double ramDamageBonus;
	private int firsts;
	private int seconds;
	private int thirds;

	public RobotResults(
			RobotSpecification robot,
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
		this.robot = robot;
		this.rank = rank;
		this.score = score;
		this.survival = survival;
		this.lastSurvivorBonus = lastSurvivorBonus;
		this.bulletDamage = bulletDamage;
		this.bulletDamageBonus = bulletDamageBonus;
		this.ramDamage = ramDamage;
		this.ramDamageBonus = ramDamageBonus;
		this.firsts = firsts;
		this.seconds = seconds;
		this.thirds = thirds;
	}

	/**
	 * Gets the robot these results are for.
	 *
	 * @return the robot these results are for.
	 */
	public RobotSpecification getRobot() {
		return robot;
	}

	/**
	 * Gets the rank of this robot in the results.
	 *
	 * @return the rank of this robot in the results.
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Gets the total score of this robot.
	 *
	 * @return the total score of this robot.
	 */
	public int getScore() {
		return (int) (score + 0.5);
	}

	/**
	 * Gets the survival score of this robot.
	 *
	 * @return the survival score of this robot.
	 */
	public int getSurvival() {
		return (int) (survival + 0.5);
	}

	/**
	 * Gets the last survivor bonus of this robot.
	 *
	 * @return the last survivor bonus of this robot.
	 */
	public int getLastSurvivorBonus() {
		return (int) (lastSurvivorBonus + 0.5);
	}

	/**
	 * Gets the bullet damage score of this robot.
	 *
	 * @return the bullet damage score of this robot.
	 */
	public int getBulletDamage() {
		return (int) (bulletDamage + 0.5);
	}

	/**
	 * Gets the bullet damage bonus of this robot.
	 *
	 * @return the bullet damage bonus of this robot.
	 */
	public int getBulletDamageBonus() {
		return (int) (bulletDamageBonus + 0.5);
	}

	/**
	 * Gets the ram damage score of this robot.
	 *
	 * @return the ram damage score of this robot.
	 */
	public int getRamDamage() {
		return (int) (ramDamage + 0.5);
	}

	/**
	 * Gets the ram damage bonus of this robot.
	 *
	 * @return the ram damage bonus of this robot.
	 */
	public int getRamDamageBonus() {
		return (int) (ramDamageBonus + 0.5);
	}

	/**
	 * Gets the number of times this robot placed first.
	 *
	 * @return the number of times this robot placed first.
	 */
	public int getFirsts() {
		return firsts;
	}

	/**
	 * Gets the number of times this robot placed second.
	 *
	 * @return the number of times this robot placed second.
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * Gets the number of times this robot placed third.
	 *
	 * @return the number of times this robot placed third.
	 */
	public int getThirds() {
		return thirds;
	}
}
