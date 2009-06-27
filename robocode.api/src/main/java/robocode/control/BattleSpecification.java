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


/**
 * A BattleSpecification defines battle configuration used by the
 * {@link RobocodeEngine}.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class BattleSpecification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final int battlefieldWidth;
	private final int battlefieldHeight;
	private final int numRounds;
	private final double gunCoolingRate;
	private final long inactivityTime;
	private final RobotSpecification[] robots;

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
		this.numRounds = numRounds;
		this.inactivityTime = inactivityTime;
		this.gunCoolingRate = gunCoolingRate;
		this.battlefieldWidth = battlefieldSize.getWidth();
		this.battlefieldHeight = battlefieldSize.getHeight();

		this.robots = robots;
	}

	/**
	 * Returns the allowed inactivity time for the robots in this battle.
	 *
	 * @return the allowed inactivity time for the robots in this battle.
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Returns the gun cooling rate of the robots in this battle.
	 *
	 * @return the gun cooling rate of the robots in this battle.
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Returns the battlefield size for this battle.
	 *
	 * @return the battlefield size for this battle.
	 */
	public BattlefieldSpecification getBattlefield() {
		return new BattlefieldSpecification(battlefieldWidth, battlefieldHeight);
	}

	/**
	 * Returns the number of rounds in this battle.
	 *
	 * @return the number of rounds in this battle.
	 */
	public int getNumRounds() {
		return numRounds;
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
}
