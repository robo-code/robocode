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


/**
 * The standard robot peer for standard robot types like {@link robocode.Robot},
 * {@link robocode.AdvancedRobot}, and {@link robocode.TeamRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @see IBasicRobotPeer
 * @see IAdvancedRobotPeer
 * @see ITeamRobotPeer
 * @see IJuniorRobotPeer
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 *
 * @since 1.6
 */
public interface IStandardRobotPeer extends IBasicRobotPeer {

	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, you can overwrite it by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop,
	 *    you can overwrite it by calling {@code stop(true)}.
	 *
	 * @see #resume()
	 */
	void stop(boolean overwrite);

	/**
	 * Immediately resumes the movement you stopped by {@link #stop(boolean)},
	 * if any.
	 * <p>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop(boolean)
	 */
	void resume();

	void scanReset();

	/**
	 * Immediately turns the robot's radar to the right or left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's radar is set to turn right, and
	 * negative values means that the robot's radar is set to turn left.
	 * If 0 is given as input, the robot's radar will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the right
	 *   turnRadar(Math.PI);
	 *
	 *   // Afterwards, turn the robot's radar 90 degrees to the left
	 *   turnRadar(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar.
	 *    If {@code radians} > 0 the robot's radar is set to turn right.
	 *    If {@code radians} < 0 the robot's radar is set to turn left.
	 *    If {@code radians} = 0 the robot's radar is set to stop turning.
	 *
	 * @see #turnBody(double)
	 * @see #turnGun(double)
	 * @see #move(double)
	 */
	void turnRadar(double radians);

	// fast setters
	void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn);
	void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn);
	void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn);
}
