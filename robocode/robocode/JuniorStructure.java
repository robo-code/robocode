/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode;


import static robocode.util.Utils.normalRelativeAngle;

import static java.lang.Math.toDegrees;


/**
 * @author Pavel Savara (original)
 */
public class JuniorStructure extends _RobotBase {

	public void updateJuniorRobotFields() {
		others = getPeer().getOthers();
		energy = Math.max(1, (int) (getPeer().getEnergy() + 0.5));
		robotX = (int) (getPeer().getX() + 0.5);
		robotY = (int) (getPeer().getY() + 0.5);
		heading = (int) (toDegrees(getPeer().getHeading()) + 0.5);
		gunHeading = (int) (toDegrees(getPeer().getGunHeading()) + 0.5);
		gunBearing = (int) (toDegrees(normalRelativeAngle(getPeer().getGunHeading() - getPeer().getHeading())) + 0.5);
		gunReady = (getPeer().getGunHeat() <= 0);
	}

	/**
	 * Contains the width of the battlefield.
	 *
	 * @see #fieldWidth
	 */
	public int fieldWidth;

	/**
	 * Contains the height of the battlefield.
	 *
	 * @see #fieldWidth
	 */
	public int fieldHeight;

	/**
	 * Current number of other robots on the battle field.
	 */
	public int others;

	/**
	 * Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
	 */
	public int energy;

	/**
	 * Current horizontal location of this robot (in pixels).
	 *
	 * @see #robotY
	 */
	public int robotX;

	/**
	 * Current vertical location of this robot (in pixels).
	 *
	 * @see #robotX
	 */
	public int robotY;

	/**
	 * Current heading angle of this robot (in degrees).
	 *
	 * @see JuniorRobot#turnLeft(int)
	 * @see JuniorRobot#turnRight(int)
	 * @see JuniorRobot#turnTo(int)
	 * @see JuniorRobot#turnAheadLeft(int, int)
	 * @see JuniorRobot#turnAheadRight(int, int)
	 * @see JuniorRobot#turnBackLeft(int, int)
	 * @see JuniorRobot#turnBackRight(int, int)
	 */
	public int heading;

	/**
	 * Current gun heading angle of this robot (in degrees).
	 *
	 * @see #gunBearing
	 * @see JuniorRobot#turnGunLeft(int)
	 * @see JuniorRobot#turnGunRight(int)
	 * @see JuniorRobot#turnGunTo(int)
	 * @see JuniorRobot#bearGunTo(int)
	 */
	public int gunHeading;

	/**
	 * Current gun heading angle of this robot compared to its body (in degrees).
	 *
	 * @see #gunHeading
	 * @see JuniorRobot#turnGunLeft(int)
	 * @see JuniorRobot#turnGunRight(int)
	 * @see JuniorRobot#turnGunTo(int)
	 * @see JuniorRobot#bearGunTo(int)
	 */
	public int gunBearing;

	/**
	 * Flag specifying if the gun is ready to fire, i.e. gun heat <= 0.
	 * <code>true</code> means that the gun is able to fire; <code>false</code>
	 * means that the gun cannot fire yet as it still needs to cool down.
	 *
	 * @see JuniorRobot#fire()
	 * @see JuniorRobot#fire(double)
	 */
	public boolean gunReady;
    
	/**
	 * Current distance to the scanned nearest other robot (in pixels).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedDistance = -1;

	/**
	 * Current angle to the scanned nearest other robot (in degrees).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedAngle = -1;

	/**
	 * Current angle to the scanned nearest other robot (in degrees) compared to
	 * the body of this robot.
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 * @see #scannedHeading
	 */
	public int scannedBearing = -1;

	/**
	 * Current velocity of the scanned nearest other robot.
	 * If there is no robot in the radar's sight, this field will be -99.
	 * Note that a positive value means that the robot moves forward, a negative
	 * value means that the robot moved backward, and 0 means that the robot is
	 * not moving at all.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedHeading
	 */
	public int scannedVelocity = -99;

	/**
	 * Current heading of the scanned nearest other robot (in degrees).
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedEnergy
	 * @see #scannedVelocity
	 */
	public int scannedHeading = -1;

	/**
	 * Current energy of scanned nearest other robot.
	 * If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
	 * This field will not be updated while {@link JuniorRobot#onScannedRobot()} event is active.
	 *
	 * @see JuniorRobot#onScannedRobot()
	 * @see #scannedDistance
	 * @see #scannedAngle
	 * @see #scannedBearing
	 * @see #scannedVelocity
	 */
	public int scannedEnergy = -1;

	/**
	 * Latest angle from where this robot was hit by a bullet (in degrees).
	 * If the robot has never been hit, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitByBullet()} event is active.
	 *
	 * @see JuniorRobot#onHitByBullet()
	 * @see #hitByBulletBearing
	 */
	public int hitByBulletAngle = -1;

	/**
	 * Latest angle from where this robot was hit by a bullet (in degrees)
	 * compared to the body of this robot.
	 * If the robot has never been hit, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitByBullet()} event is active.
	 *
	 * @see JuniorRobot#onHitByBullet()
	 * @see #hitByBulletAngle
	 */
	public int hitByBulletBearing = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees).
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitRobot()} event is active.
	 *
	 * @see JuniorRobot#onHitRobot()
	 * @see #hitRobotBearing
	 */
	public int hitRobotAngle = -1;

	/**
	 * Latest angle where this robot has hit another robot (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit another robot, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitRobot()} event is active.
	 *
	 * @see JuniorRobot#onHitRobot()
	 * @see #hitRobotAngle
	 */
	public int hitRobotBearing = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees).
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitWall()} event is active.
	 *
	 * @see JuniorRobot#onHitWall()
	 * @see #hitWallBearing
	 */
	public int hitWallAngle = -1;

	/**
	 * Latest angle where this robot has hit a wall (in degrees)
	 * compared to the body of this robot.
	 * If this robot has never hit a wall, this field will be less than 0, i.e. -1.
	 * This field will not be updated while {@link JuniorRobot#onHitWall()} event is active.
	 *
	 * @see JuniorRobot#onHitWall()
	 * @see #hitWallAngle
	 */
	public int hitWallBearing = -1;
}
