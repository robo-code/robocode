/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This is private interface. You should build any external component (or robot)
 * based on it's current methods because it will change in the future.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.robotinterfaces.peer;


import robocode.Bullet;
import robocode.Rules;

import java.awt.*;


/**
 * The basic robot peer for all robot types.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IBasicRobotPeer {

	/**
	 * Returns the robot's name.
	 *
	 * @return the robot's name.
	 */
	String getName();

	/**
	 * Returns the game time of the current round, where the time is equal to
	 * the current turn in the round.
	 * <p>
	 * A battle consists of multiple rounds.
	 * <p>
	 * Time is reset to 0 at the beginning of every round.
	 *
	 * @return the game time/turn of the current round.
	 */
	long getTime();

	/**
	 * Returns the robot's current energy.
	 *
	 * @return the robot's current energy.
	 */
	double getEnergy();

	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the X position of the robot.
	 *
	 * @see #getY()
	 */
	double getX();

	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the Y position of the robot.
	 *
	 * @see #getX()
	 */
	double getY();

	/**
	 * Returns the velocity of the robot measured in pixels/turn.
	 * <p>
	 * The maximum velocity of a robot is defined by
	 * {@link Rules#MAX_VELOCITY} (8 pixels / turn).
	 *
	 * @return the velocity of the robot measured in pixels/turn.
	 *
	 * @see Rules#MAX_VELOCITY
	 */
	double getVelocity();

	/**
	 * Returns the direction that the robot's body is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's body is facing, in degrees.
	 *
	 * @see #getGunHeading()
	 * @see #getRadarHeading()
	 */
	double getHeading();

	/**
	 * Returns the direction that the robot's gun is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's gun is facing, in degrees.
	 *
	 * @see #getHeading()
	 * @see #getRadarHeading()
	 */
	double getGunHeading();

	/**
	 * Returns the direction that the robot's radar is facing, in degrees.
	 * The value returned will be between 0 and 360 (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * 90 means East, 180 means South, and 270 means West.
	 *
	 * @return the direction that the robot's radar is facing, in degrees.
	 *
	 * @see #getHeading()
	 * @see #getGunHeading()
	 */
	double getRadarHeading();
	
	/**
	 * Returns the current heat of the gun. The gun cannot fire unless this is
	 * 0. (Calls to fire will succeed, but will not actually fire unless
	 * getGunHeat() == 0).
	 * <p>
	 * The amount of gun heat generated when the gun is fired is
	 * 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
	 * by {@link #getGunCoolingRate()}, which is a battle setup.
	 * <p>
	 * Note that all guns are "hot" at the start of each round, where the gun
	 * heat is 3.
	 *
	 * @return the current gun heat
	 *
	 * @see #getGunCoolingRate()
	 * @see #setFire(double)
	 */
	double getGunHeat();

	/**
	 * Returns the width of the current battlefield measured in pixels.
	 *
	 * @return the width of the current battlefield measured in pixels.
	 */
	double getBattleFieldWidth();

	/**
	 * Returns the height of the current battlefield measured in pixels.
	 *
	 * @return the height of the current battlefield measured in pixels.
	 */
	double getBattleFieldHeight();

	/**
	 * Returns how many opponents that are left in the current round.
	 *
	 * @return how many opponents that are left in the current round.
	 */
	int getOthers();

	/**
	 * Returns the number of rounds in the current battle.
	 *
	 * @return the number of rounds in the current battle
	 *
	 * @see #getRoundNum()
	 */
	int getNumRounds();

	/**
	 * Returns the number of the current round (0 to {@link #getNumRounds()} - 1)
	 * in the battle.
	 *
	 * @return the number of the current round in the battle
	 *
	 * @see #getNumRounds()
	 */
	int getRoundNum();

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat
	 * the gun heat will drop per turn.
	 * <p>
	 * The gun cooling rate is default 0.1 / turn, but can be changed by the
	 * battle setup. So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate
	 *
	 * @see #getGunHeat()
	 * @see #setFire(double)
	 */
	double getGunCoolingRate();

	/**
	 * Returns the distance remaining in the robot's current move measured in
	 * pixels.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently moving forwards. Negative values means
	 * that the robot is currently moving backwards. If the returned value is 0,
	 * the robot currently stands still.
	 *
	 * @return the distance remaining in the robot's current move measured in
	 *    pixels.
	 *
	 * @see #getTurnRemaining()
	 * @see #getGunTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getDistanceRemaining();

	/**
	 * Returns the angle remaining in the robots's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left. If the returned
	 * value is 0, the robot is currently not turning.
	 *
	 * @return the angle remaining in the robots's turn, in degrees
	 *
	 * @see #getDistanceRemaining()
	 * @see #getGunTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getTurnRemaining();

	/**
	 * Returns the angle remaining in the gun's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left. If the returned
	 * value is 0, the gun is currently not turning.
	 *
	 * @return the angle remaining in the gun's turn, in degrees
	 *
	 * @see #getDistanceRemaining()
	 * @see #getTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getGunTurnRemaining();

	/**
	 * Returns the angle remaining in the radar's turn, in degrees.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left. If the returned
	 * value is 0, the radar is currently not turning.
	 *
	 * @return the angle remaining in the radar's turn, in degrees
	 *
	 * @see #getDistanceRemaining()
	 * @see #getGunTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getRadarTurnRemaining();

	/**
	 * Executes any pending actions, or continues executing actions that are
	 * in process. This call returns after the actions have been started.
	 * <p>
	 * Note that advanced robots <em>must</em> call this function in order to
	 * execute pending set* calls like e.g. {@link #setAhead(double)},
	 * {@link #setFire(double)}, {@link #setTurnLeft(double)} etc. Otherwise,
	 * these calls will never get executed.
	 * <p>
	 * In this example the robot will move while turning:
	 * <pre>
	 *   setTurnRight(90);
	 *   setAhead(100);
	 *   execute();
	 *
	 *   while (getDistanceRemaining() > 0 && getTurnRemaining() > 0) {
	 *       execute();
	 *   }
	 * </pre>
	 */
	void execute();

	/**
	 * Immediately moves your robot forward or backward by distance measured in
	 * pixels.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the remaining distance to move is 0.
	 * <p>
	 * If the robot collides with a wall, the move is complete, meaning that the
	 * robot will not move any further. If the robot collides with another
	 * robot, the move is complete if you are heading toward the other robot.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot is set to move forward, and negative
	 * values means that the robot is set to move backward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Move the robot 100 pixels forward
	 *   ahead(100);
	 *
	 *   // Afterwards, move the robot 50 pixels backward
	 *   ahead(-50);
	 * </pre>
	 *
	 * @param distance the distance to move measured in pixels.
	 *    If {@code distance} > 0 the robot is set to move forward.
	 *    If {@code distance} < 0 the robot is set to move backward.
	 *    If {@code distance} = 0 the robot will not move anywhere, but just
	 *    finish its turn.
	 *
	 * @see robocode.robotinterfaces.IBasicEvents#onHitWall(robocode.HitWallEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onHitRobot(robocode.HitRobotEvent)
	 */
	void move(double distance);

	void turnBody(double radians);
	void turnGun(double radians);

	// asynchronous actions
	Bullet setFire(double power);

	// fast setters
	void setBodyColor(Color color);
	void setGunColor(Color color);
	void setRadarColor(Color color);
	void setBulletColor(Color color);
	void setScanColor(Color color);

	// counters
	void getCall();
	void setCall();
}