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


import robocode.ScannedRobotEvent;
import robocode.robotinterfaces.IBasicEvents;


/**
 * The standard robot peer for standard robot types like {@link robocode.Robot},
 * {@link robocode.AdvancedRobot}, and {@link robocode.TeamRobot}.
 * <p/>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (javadoc)
 * @see IBasicRobotPeer
 * @see IAdvancedRobotPeer
 * @see ITeamRobotPeer
 * @see IJuniorRobotPeer
 * @since 1.6
 */
public interface IStandardRobotPeer extends IBasicRobotPeer {

	/**
	 * Immediately stops all movement, and saves it for a call to
	 * {@link #resume()}. If there is already movement saved from a previous
	 * stop, you can overwrite it by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop,
	 *                  you can overwrite it by calling {@code stop(true)}.
	 * @see #resume()
	 */
	void stop(boolean overwrite);

	/**
	 * Immediately resumes the movement you stopped by {@link #stop(boolean)},
	 * if any.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop(boolean)
	 */
	void resume();

	/**
	 * Rescan for other robots. This method is called automatically by the game,
	 * as long as the robot is moving, turning its body, turning its gun, or
	 * turning its radar.
	 * <p/>
	 * Rescan will cause {@link IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 * onScannedRobot(ScannedRobotEvent)} to be called if you see a robot.
	 * <p/>
	 * There are 2 reasons to call {@code rescan()} manually:
	 * <ol>
	 * <li>You want to scan after you stop moving.
	 * <li>You want to interrupt the {@code onScannedRobot} event. This is more
	 * likely. If you are in {@code onScannedRobot} and call {@code scan()},
	 * and you still see a robot, then the system will interrupt your
	 * {@code onScannedRobot} event immediately and start it from the top.
	 * </ol>
	 * <p/>
	 * This call executes immediately.
	 *
	 * @see IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 *      onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 */
	void rescan();

	/**
	 * Immediately turns the robot's radar to the right or left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's radar is set to turn right, and
	 * negative values means that the robot's radar is set to turn left.
	 * If 0 is given as input, the robot's radar will stop turning.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the right
	 *   turnRadar(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot's radar 90 degrees to the left
	 *   turnRadar(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar.
	 *                If {@code radians} > 0 the robot's radar is set to turn right.
	 *                If {@code radians} < 0 the robot's radar is set to turn left.
	 *                If {@code radians} = 0 the robot's radar is set to stop turning.
	 * @see #turnBody(double)
	 * @see #turnGun(double)
	 * @see #move(double)
	 */
	void turnRadar(double radians);

	/**
	 * Sets the gun to turn independent from the robot's turn.
	 * <p/>
	 * Ok, so this needs some explanation: The gun is mounted on the robot's
	 * body. So, normally, if the robot turns 90 degrees to the right, then the
	 * gun will turn with it as it is mounted on top of the robot's body. To
	 * compensate for this, you can call {@code setAdjustGunForBodyTurn(true)}.
	 * When this is set, the gun will turn independent from the robot's turn,
	 * i.e. the gun will compensate for the robot's body turn.
	 * <p/>
	 * Example, assuming both the robot and gun start out facing up (0 degrees):
	 * <pre>
	 *   // Set gun to turn with the robot's turn
	 *   setAdjustGunForBodyTurn(false); // This is the default
	 *   turnBodyRight(Math.PI / 2);
	 *   // At this point, both the robot and gun are facing right (90 degrees)
	 *   turnBodyLeft(Math.PI / 2);
	 *   // Both are back to 0 degrees
	 * <p/>
	 *   -- or --
	 * <p/>
	 *   // Set gun to turn independent from the robot's turn
	 *   setAdjustGunForBodyTurn(true);
	 *   turnBodyRight(Math.PI / 2);
	 *   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
	 *   turnBodyLeft(Math.PI / 2);
	 *   // Both are back to 0 degrees.
	 * </pre>
	 * <p/>
	 * Note: The gun compensating this way does count as "turning the gun".
	 * See {@link #setAdjustRadarForGunTurn(boolean)} for details.
	 *
	 * @param independent {@code true} if the gun must turn independent from the
	 *                    robot's turn; {@code false} if the gun must turn with the robot's turn.
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	void setAdjustGunForBodyTurn(boolean independent);

	/**
	 * Sets the radar to turn independent from the gun's turn.
	 * <p/>
	 * Ok, so this needs some explanation: The radar is mounted on the robot's
	 * gun. So, normally, if the gun turns 90 degrees to the right, then the
	 * radar will turn with it as it is mounted on top of the gun. To compensate
	 * for this, you can call {@code setAdjustRadarForGunTurn(true)}. When this
	 * is set, the radar will turn independent from the robot's turn, i.e. the
	 * radar will compensate for the gun's turn.
	 * <p/>
	 * Example, assuming both the gun and radar start out facing up (0 degrees):
	 * <pre>
	 *   // Set radar to turn with the gun's turn
	 *   setAdjustRadarForGunTurn(false); // This is the default
	 *   turnGunRight(Math.PI / 2);
	 *   // At this point, both the radar and gun are facing right (90 degrees);
	 * <p/>
	 *   -- or --
	 * <p/>
	 *   // Set radar to turn independent from the gun's turn
	 *   setAdjustRadarForGunTurn(true);
	 *   turnGunRight(Math.PI / 2);
	 *   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 * Note: Calling {@code setAdjustRadarForGunTurn(boolean)} will
	 * automatically call {@link #setAdjustRadarForBodyTurn(boolean)} with the
	 * same value, unless you have already called it earlier. This behavior is
	 * primarily for backward compatibility with older Robocode robots.
	 *
	 * @param independent {@code true} if the radar must turn independent from
	 *                    the gun's turn; {@code false} if the radar must turn with the gun's
	 *                    turn.
	 * @see #setAdjustRadarForBodyTurn(boolean)
	 * @see #setAdjustGunForBodyTurn(boolean)
	 */
	void setAdjustRadarForGunTurn(boolean independent);

	/**
	 * Sets the radar to turn independent from the robot's turn.
	 * <p/>
	 * Ok, so this needs some explanation: The radar is mounted on the gun, and
	 * the gun is mounted on the robot's body. So, normally, if the robot turns
	 * 90 degrees to the right, the gun turns, as does the radar. Hence, if the
	 * robot turns 90 degrees to the right, then the gun and radar will turn
	 * with it as the radar is mounted on top of the gun. To compensate for
	 * this, you can call {@code setAdjustRadarForBodyTurn(true)}. When this is
	 * set, the radar will turn independent from the robot's turn, i.e. the
	 * radar will compensate for the robot's turn.
	 * <p/>
	 * Example, assuming the robot, gun, and radar all start out facing up (0
	 * degrees):
	 * <pre>
	 *   // Set radar to turn with the robots's turn
	 *   setAdjustRadarForBodyTurn(false); // This is the default
	 *   turnRight(Math.PI / 2);
	 *   // At this point, the body, gun, and radar are all facing right (90 degrees);
	 * <p/>
	 *   -- or --
	 * <p/>
	 *   // Set radar to turn independent from the robot's turn
	 *   setAdjustRadarForBodyTurn(true);
	 *   turnRight(Math.PI / 2);
	 *   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 *
	 * @param independent {@code true} if the radar must turn independent from
	 *                    the robots's turn; {@code false} if the radar must turn with the robot's
	 *                    turn.
	 * @see #setAdjustGunForBodyTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	void setAdjustRadarForBodyTurn(boolean independent);
}
