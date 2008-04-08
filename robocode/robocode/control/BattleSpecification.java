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
 *     - Removed the battlefield field, which can be created when calling
 *       getBattlefield() and optimized constructor
 *     - Changed getRobots() to return a copy of the robots
 *     - This class now implements java.io.Serializable
 *******************************************************************************/
package robocode.control;


import robocode.battle.BattleProperties;


/**
 * Defines a battle
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final RobotSpecification[] robots;
	private final BattleProperties battleProperties;

	/**
	 * Creates a BattleSpecification with the given number of rounds, battlefield, and robots
	 * inactivityTime defaults to 450.  gunCoolingRate defaults to .1
	 * @param numRounds Number of rounds in this battle
	 * @param battlefield The battlefield
	 * @param robotSpecifications Robots in this battle
	 */
	public BattleSpecification(int numRounds, BattlefieldSpecification battlefield, RobotSpecification robotSpecifications[]) {
		this(numRounds, 450, .1, battlefield, robotSpecifications);
	}

	/**
	 * Creates a BattleSpecification with the given number of rounds, inactivityTime, gunCoolingRate, battlefield, and robots
	 * @param numRounds Number of rounds in this battle
	 * @param inactivityTime Number of ticks in which 10 energy must be lost
	 * @param gunCoolingRate Gun cooling rate
	 * @param battlefield The battlefield
	 * @param robots Robots in this battle
	 */
	public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefield, RobotSpecification[] robots) {
		battleProperties = new BattleProperties();
		battleProperties.setNumRounds(numRounds);
		battleProperties.setInactivityTime(inactivityTime);
		battleProperties.setGunCoolingRate(gunCoolingRate);
		battleProperties.setBattlefieldWidth(battlefield.getWidth());
		battleProperties.setBattlefieldHeight(battlefield.getHeight());
		battleProperties.setSelectedRobots(robots);

		this.robots = robots;
	}

	/**
	 * Gets the inactivityTime for this battle
	 *
	 * @return the inactivityTime for this battle
	 */
	public long getInactivityTime() {
		return battleProperties.getInactivityTime();
	}

	/**
	 * Gets the gunCoolingRate for this battle
	 *
	 * @return the gunCoolingRate for this battle
	 */
	public double getGunCoolingRate() {
		return battleProperties.getGunCoolingRate();
	}

	/**
	 * Gets the battleField for this battle
	 *
	 * @return the battleField for this battle
	 */
	public BattlefieldSpecification getBattlefield() {
		return new BattlefieldSpecification(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight());
	}

	/**
	 * Gets the number of rounds in this battle
	 *
	 * @return the number of rounds in this battle
	 */
	public int getNumRounds() {
		return battleProperties.getNumRounds();
	}

	/**
	 * Gets the robots participating in this battle
	 *
	 * @return the robots participating in this battle
	 */
	public RobotSpecification[] getRobots() {
		if (robots == null) {
			return null;
		}

		RobotSpecification[] robotsCopy = new RobotSpecification[robots.length];

		System.arraycopy(robots, 0, robotsCopy, 0, robots.length);

		return robotsCopy;
	}

	/**
	 * @deprecated Used by Robocode, you do not need to use this.
	 * This method may not exist in a future version of Robocode.
	 */
	@Deprecated
	public BattleProperties getBattleProperties() {
		return battleProperties;
	}
}
