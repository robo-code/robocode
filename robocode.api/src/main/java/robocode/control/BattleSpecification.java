/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control;


/**
 * A BattleSpecification defines a battle configuration used by the {@link RobocodeEngine}.
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
	private final boolean hideEnemyNames;
	private final int sentryBorderSize;
	private final RobotSpecification[] robots;
	private final RobotSetup[] initialSetups;

	/**
	 * Creates a new BattleSpecification with the given number of rounds, battlefield size, and robots.
	 * Inactivity time for the robots defaults to 450, and the gun cooling rate defaults to 0.1.
	 *
	 * @param numRounds	is the number of rounds in this battle.
	 * @param battlefieldSize is the battlefield size.
	 * @param robots is the robots participating in this battle.
	 */
	public BattleSpecification(int numRounds, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) {
		this(numRounds, 450, 0.1, battlefieldSize, robots);
	}

	/**
	 * Creates a new BattleSpecification with the given settings.
	 *
	 * @param numRounds	is the number of rounds in this battle.
	 * @param inactivityTime is the inactivity time allowed for the robots before they will loose energy.
	 * @param gunCoolingRate is the gun cooling rate for the robots.
	 * @param battlefieldSize is the battlefield size.
	 * @param robots is the robots participating in this battle.
	 */
	public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) {
		this(numRounds, inactivityTime, gunCoolingRate, false, battlefieldSize, robots);
	}

	/**
	 * Creates a new BattleSpecification with the given settings.
	 *
	 * @param numRounds	is the number of rounds in this battle.
	 * @param inactivityTime is the inactivity time allowed for the robots before they will loose energy.
	 * @param gunCoolingRate is the gun cooling rate for the robots.
	 * @param hideEnemyNames  flag specifying if enemy names are hidden from robots.
	 * @param battlefieldSize is the battlefield size.
	 * @param robots is the robots participating in this battle.
	 * 
	 * @since 1.7.3
	 */
	public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, boolean hideEnemyNames, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) {
		this(battlefieldSize, numRounds, inactivityTime, gunCoolingRate, 100, hideEnemyNames, robots);
	}

	/**
	 * Creates a new BattleSpecification with the given settings.
	 *
	 * @param battlefieldSize is the battlefield size.
	 * @param numRounds	is the number of rounds in this battle.
	 * @param inactivityTime is the inactivity time allowed for the robots before they will loose energy.
	 * @param gunCoolingRate is the gun cooling rate for the robots.
	 * @param sentryBorderSize is the sentry border size for a {@link robocode.BorderSentry BorderSentry}.
	 * @param hideEnemyNames  flag specifying if enemy names are hidden from robots.
	 * @param robots is the robots participating in this battle.
	 * 
	 * @since 1.9.0.0
	 */
	public BattleSpecification(BattlefieldSpecification battlefieldSize, int numRounds, long inactivityTime, double gunCoolingRate, int sentryBorderSize, boolean hideEnemyNames, RobotSpecification[] robots) {
		this(battlefieldSize, numRounds, inactivityTime, gunCoolingRate, 100, hideEnemyNames, robots, null);
	}

	/**
	 * Creates a new BattleSpecification with the given settings.
	 *
	 * @param battlefieldSize is the battlefield size.
	 * @param numRounds	is the number of rounds in this battle.
	 * @param inactivityTime is the inactivity time allowed for the robots before they will loose energy.
	 * @param gunCoolingRate is the gun cooling rate for the robots.
	 * @param sentryBorderSize is the sentry border size for a {@link robocode.BorderSentry BorderSentry}.
	 * @param hideEnemyNames  flag specifying if enemy names are hidden from robots.
	 * @param robots is the robots participating in this battle.
	 * @param initialSetup is the initial position and heading of the robots, where the indices matches the indices from the {@code robots} parameter.
	 * 
	 * @since 1.9.2.0
	 */
	public BattleSpecification(BattlefieldSpecification battlefieldSize, int numRounds, long inactivityTime, double gunCoolingRate, int sentryBorderSize, boolean hideEnemyNames, RobotSpecification[] robots, RobotSetup[] initialSetups) {
		if (battlefieldSize == null) {
			throw new IllegalArgumentException("battlefieldSize cannot be null");
		}
		if (robots == null) {
			throw new IllegalArgumentException("robots cannot be null");
		}
		if (robots.length < 1) {
			throw new IllegalArgumentException("robots.length must be > 0");
		}
		if (initialSetups != null && initialSetups.length != robots.length) {
			throw new IllegalArgumentException("initialSetups.length must be == robots.length");
		}
		if (numRounds < 1) {
			throw new IllegalArgumentException("numRounds must be >= 1");
		}
		if (inactivityTime < 1) {
			throw new IllegalArgumentException("inactivityTime must be >= 1");
		}
		if (gunCoolingRate < 0.1) {
			throw new IllegalArgumentException("inactivityTime must be >= 0.1");
		}
		if (sentryBorderSize < 50) {
			throw new IllegalArgumentException("sentryBorderSize must be >= 50");
		}
		this.battlefieldWidth = battlefieldSize.getWidth();
		this.battlefieldHeight = battlefieldSize.getHeight();
		this.numRounds = numRounds;
		this.inactivityTime = inactivityTime;
		this.gunCoolingRate = gunCoolingRate;
		this.sentryBorderSize = sentryBorderSize;
		this.hideEnemyNames = hideEnemyNames;
		this.robots = robots;
		this.initialSetups = initialSetups;
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
	 * Returns the flag specifying if the enemy names must be hidden from events sent to robots.
	 *
	 * @return {@code true} if the enemy names must be hidden; {@code false} otherwise.
	 * 
	 * @since 1.7.3
	 */
	public boolean getHideEnemyNames() {
		return hideEnemyNames;
	}

	/**
	 * Returns the sentry border size for a {@link robocode.BorderSentry BorderSentry} that defines the how
	 * far a BorderSentry is allowed to move from the border edges measured in units.<br>
	 * Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	 * border sentry robots cannot leave (they must stay in the border area), but it also define the
	 * distance from the border edges where border sentry robots are allowed/able to make damage to robots entering this
	 * border area.
	 *
	 * @return the border size in units/pixels that border sentry robots are restricted to.
	 * 
	 * @since 1.9.0.0
	 */
	public int getSentryBorderSize() {
		return sentryBorderSize;
	}

	/**
	 * Returns the specifications of the robots participating in this battle.
	 *
	 * @return an array of {@link RobotSpecification} instances - one entry for each robot.
	 * 
	 * @see #getInitialSetups()
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
	 * Returns the initial position and heading of each robot participating in this battle.
	 * 
	 * @return an array of {@link RobotSetup} instances - one entry for each robot.
	 * The the indices of this array matches the array indices from the robot specifications (see {@link #getRobots()}).
	 * 
	 * @see #getRobots()
	 */
	public RobotSetup[] getInitialSetups() {
		if (initialSetups == null) {
			return null;
		}
		RobotSetup[] setupsCopy = new RobotSetup[initialSetups.length];
		System.arraycopy(initialSetups, 0, setupsCopy, 0, initialSetups.length);
		return setupsCopy;
	}
}
