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
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.robotinterfaces.peer;


import robocode.*;

import java.util.List;
import java.io.File;


/**
 * The advanced robot peer for advanced robot types like
 * {@link robocode.AdvancedRobot} and {@link robocode.TeamRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IAdvancedRobotPeer extends IStandardRobotPeer {

	/**
	 * Checks if the gun is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the gun is set to turn independent of
	 * the turn of the robot's body. Otherwise, {@code false} is returned,
	 * meaning that the gun is set to turn with the robot's body turn.
	 *
	 * @return {@code true} if the gun is set to turn independent of the robot
	 *    turning; {@code false} if the gun is set to turn with the robot
	 *    turning
	 *
	 * @see #setAdjustGunForBodyTurn(boolean)
	 * @see #isAdjustRadarForBodyTurn()
	 * @see #isAdjustRadarForGunTurn()
	 */
	boolean isAdjustGunForBodyTurn();

	/**
	 * Checks if the radar is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the robot. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the robot's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the robot
	 *    turning; {@code false} if the radar is set to turn with the robot
	 *    turning
	 *
	 * @see #setAdjustRadarForBodyTurn(boolean)
	 * @see #isAdjustGunForBodyTurn()
	 * @see #isAdjustRadarForGunTurn()
	 */
	boolean isAdjustRadarForGunTurn();

	/**
	 * Checks if the radar is set to adjust for the gun turning, i.e. to turn
	 * independent from the gun's turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the gun. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the gun's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the gun
	 *    turning; {@code false} if the radar is set to turn with the gun
	 *    turning
	 *
	 * @see #setAdjustRadarForGunTurn(boolean)
	 * @see #isAdjustGunForBodyTurn()
	 * @see #isAdjustRadarForBodyTurn()
	 */
	boolean isAdjustRadarForBodyTurn();

	/**
	 * This call is identical to {@link IStandardRobotPeer#stop(boolean)
	 * stop(boolean)}, but returns immediately, and will not execute until you
	 * call {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 * <p>
	 * If there is already movement saved from a previous stop, you can
	 * overwrite it by calling {@code setStop(true)}.
	 *
	 * @param overwrite {@code true} if the movement saved from a previous stop
	 *    should be overwritten; {@code false} otherwise.
	 *
	 * @see IStandardRobotPeer#stop(boolean) stop(boolean)
	 * @see IStandardRobotPeer#resume() resume()
	 * @see #setResume()
	 * @see IBasicRobotPeer#execute() execute()
	 */
	void setStop(boolean overwrite);

	/**
	 * Sets the robot to resume the movement stopped by
	 * {@link IStandardRobotPeer#stop(boolean) stop(boolean)} or
	 * {@link #setStop(boolean)}, if any.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 *
	 * @see IStandardRobotPeer#resume() resume()
	 * @see IStandardRobotPeer#stop(boolean) stop(boolean)
	 * @see #setStop(boolean)
	 * @see IBasicRobotPeer#execute() execute()
	 */
	void setResume();

	/**
	 * Sets the robot to move forward or backward by distance measured in pixels
	 * when the next execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot is set to move forward, and negative
	 * values means that the robot is set to move backward. If 0 is given as
	 * input, the robot will stop its movement, but will have to decelerate
	 * till it stands still, and will thus not be able to stop its movement
	 * immediately, but eventually.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to move 50 pixels forward
	 *   setMove(50);
	 *
	 *   // Set the robot to move 100 pixels backward
	 *   // (overrides the previous order)
	 *   setMove(-100);
	 *
	 *   ...
	 *   // Executes the last setMove()
	 *   execute();
	 * </pre>
	 *
	 * @param distance the distance to move measured in pixels.
	 *    If {@code distance} > 0 the robot is set to move forward.
	 *    If {@code distance} < 0 the robot is set to move backward.
	 *    If {@code distance} = 0 the robot is set to stop its movement.
	 */
	void setMove(double distance);

	void setTurnChassis(double radians);
	void setTurnGun(double radians);
	void setTurnRadar(double radians);

	// fast setters
	void setMaxTurnRate(double newTurnRate);
	void setMaxVelocity(double newVelocity);

	// blocking actions
	void waitFor(Condition condition);

	// events manipulation
	void setInterruptible(boolean interruptable);
	void setEventPriority(String eventClass, int priority);
	int getEventPriority(String eventClass);
	void removeCustomEvent(Condition condition);
	void addCustomEvent(Condition condition);
	void clearAllEvents();
	java.util.List<Event> getAllEvents();
	List<BulletMissedEvent> getBulletMissedEvents();
	List<BulletHitBulletEvent> getBulletHitBulletEvents();
	List<BulletHitEvent> getBulletHitEvents();
	List<HitByBulletEvent> getHitByBulletEvents();
	List<HitRobotEvent> getHitRobotEvents();
	List<HitWallEvent> getHitWallEvents();
	List<RobotDeathEvent> getRobotDeathEvents();
	List<ScannedRobotEvent> getScannedRobotEvents();

	// data
	File getDataDirectory();
	File getDataFile(String filename);
	long getDataQuotaAvailable();
}
