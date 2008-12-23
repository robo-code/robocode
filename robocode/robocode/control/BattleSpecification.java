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
 *     - Removed the battlefield field, which can be created when calling
 *       getBattlefield() and optimized constructor
 *     - Changed getRobots() to return a copy of the robots
 *     - This class now implements java.io.Serializable
 *     - Updated Javadocs
 *******************************************************************************/
package robocode.control;


import robocode.battle.BattleProperties;


/**
 * A BattleSpecification defines battle configuration used by the
 * {@link RobocodeEngine}.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final RobotSpecification[] robots;
	private final BattleProperties battleProperties;

	/**
	 * Creates a new BattleSpecification with the given number of rounds,
	 * battlefield size, and robots. Inactivity time for the robots defaults to
	 * 450, and the gun cooling rate defaults to 0.1.
	 *
	 * @param numRounds	   the number of rounds in this battle
	 * @param battlefieldSize the battlefield size
	 * @param robots		  the robots participating in this battle
	 */
	public BattleSpecification(int numRounds, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) {
		this(numRounds, 450, .1, battlefieldSize, robots);
	}

	/**
	 * Creates a new BattleSpecification with the given settings.
	 *
	 * @param numRounds	   the number of rounds in this battle
	 * @param inactivityTime  the inactivity time allowed for the robots before
	 *                        they will loose energy
	 * @param gunCoolingRate  the gun cooling rate for the robots
	 * @param battlefieldSize the battlefield size
	 * @param robots		  the robots participating in this battle
	 */
	public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) {
		battleProperties = new BattleProperties();
		battleProperties.setNumRounds(numRounds);
		battleProperties.setInactivityTime(inactivityTime);
		battleProperties.setGunCoolingRate(gunCoolingRate);
		battleProperties.setBattlefieldWidth(battlefieldSize.getWidth());
		battleProperties.setBattlefieldHeight(battlefieldSize.getHeight());
		if (robots != null) {
			battleProperties.setSelectedRobots(robots);
		}

		this.robots = robots;
	}

	/**
	 * Returns the allowed inactivity time for the robots in this battle.
	 *
	 * @return the allowed inactivity time for the robots in this battle.
	 */
	public long getInactivityTime() {
		return battleProperties.getInactivityTime();
	}

	/**
	 * Returns the gun cooling rate of the robots in this battle.
	 *
	 * @return the gun cooling rate of the robots in this battle.
	 */
	public double getGunCoolingRate() {
		return battleProperties.getGunCoolingRate();
	}

	/**
	 * Returns the battlefield size for this battle.
	 *
	 * @return the battlefield size for this battle.
	 */
	public BattlefieldSpecification getBattlefield() {
		return new BattlefieldSpecification(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight());
	}

	/**
	 * Returns the number of rounds in this battle.
	 *
	 * @return the number of rounds in this battle.
	 */
	public int getNumRounds() {
		return battleProperties.getNumRounds();
	}

	/**
	 * Returns the specifications of the robots participating in this battle.
	 *
	 * @return the specifications of the robots participating in this battle.
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
	 * Do not call this method!
	 *
	 * @return the properties of this battle.
	 * @deprecated This methods is called by the game and is very likely to be
	 *             removed in a future version of Robocode.
	 */
	@Deprecated
	public BattleProperties getBattleProperties() {
		return battleProperties;
	}
}
