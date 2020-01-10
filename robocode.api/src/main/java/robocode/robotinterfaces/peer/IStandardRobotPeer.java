/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IStandardRobotPeer extends IBasicRobotPeer {

	/**
	 * Immediately stops all movement, and saves it for a call to {@link #resume()}.
	 * If there is already movement saved from a previous stop, you can overwrite it
	 * by calling {@code stop(true)}.
	 *
	 * @param overwrite If there is already movement saved from a previous stop, you
	 *                  can overwrite it by calling {@code stop(true)}.
	 * @see #resume()
	 */
	void stop(boolean overwrite);

	/**
	 * Immediately resumes the movement you stopped by {@link #stop(boolean)}, if
	 * any.
	 * <p>
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * @see #stop(boolean)
	 */
	void resume();

	/**
	 * Immediately turns the robot's radar to the right or left by radians. This
	 * call executes immediately, and does not return until it is complete, i.e.
	 * when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's radar is set to turn right, and
	 * negative values means that the robot's radar is set to turn left. If 0 is
	 * given as input, the robot's radar will stop turning.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * // Turn the robot's radar 180 degrees to the right
	 * turnRadar(Math.PI);
	 *
	 * // Afterwards, turn the robot's radar 90 degrees to the left
	 * turnRadar(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar. If
	 *                {@code radians} > 0 the robot's radar is set to turn right. If
	 *                {@code radians} < 0 the robot's radar is set to turn left. If
	 *                {@code radians} = 0 the robot's radar is set to stop turning.
	 * @see #turnBody(double)
	 * @see #turnGun(double)
	 * @see #move(double)
	 */
	void turnRadar(double radians);

	/**
	 * Sets the gun to adjust for the bot's turn, so the gun behaves like it is
	 * turning independent of the bot's turn.
	 * <p>
	 * Ok, so this needs some explanation: The gun is mounted on the bot's body. So,
	 * normally, if the bot turns 90 degrees to the right, then the gun will turn
	 * with it as it is mounted on top of the bot's body. To compensate for this,
	 * you can call {@code setAdjustGunForBodyTurn(true)}. When this is set, the gun
	 * will turn independent from the bot's turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the gun can turn.
	 * The "adjust" is added to the amount you set for turning the bot, then capped
	 * by the physics of the game. If you turn infinite, then the adjust is ignored
	 * (and hence overridden).
	 * <p>
	 * Example, assuming both the robot and gun start out facing up (0 degrees):
	 * 
	 * <pre>
	 *   // Set gun to turn with the robot's turn
	 *   setAdjustGunForBodyTurn(false); // This is the default
	 *   turnBodyRight(Math.PI / 2);
	 *   // At this point, both the robot and gun are facing right (90 degrees)
	 *   turnBodyLeft(Math.PI / 2);
	 *   // Both are back to 0 degrees
	 *
	 *   -- or --
	 *
	 *   // Set gun to turn independent from the robot's turn
	 *   setAdjustGunForBodyTurn(true);
	 *   turnBodyRight(Math.PI / 2);
	 *   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
	 *   turnBodyLeft(Math.PI / 2);
	 *   // Both are back to 0 degrees.
	 * </pre>
	 * <p>
	 * Note: The gun compensating this way does count as "turning the gun".
	 *
	 * @param adjust {@code true} if the gun must adjust for the bot's turn;
	 *               {@code false} if the gun must turn with the bot's turn.
	 * @see #setAdjustRadarForGunTurn(boolean)
	 * @see #isAdjustGunForBodyTurn()
	 */
	void setAdjustGunForBodyTurn(boolean adjust);

	/**
	 * Sets the radar to adjust for the gun's turn, so the radar behaves like it is
	 * turning independent of the gun's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the bot's gun.
	 * So, normally, if the gun turns 90 degrees to the right, then the radar will
	 * turn with it as it is mounted on top of the gun. To compensate for this, you
	 * can call {@code setAdjustRadarForGunTurn(true)}. When this is set, the radar
	 * will turn independent from the gun's turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the radar can turn.
	 * The "adjust" is added to the amount you set for turning the gun, then capped
	 * by the physics of the game. If you turn infinite, then the adjust is ignored
	 * (and hence overridden).
	 * <p>
	 * Example, assuming both the gun and radar start out facing up (0 degrees):
	 * 
	 * <pre>
	 *   // Set radar to turn with the gun's turn
	 *   setAdjustRadarForGunTurn(false); // This is the default
	 *   turnGunRight(Math.PI / 2);
	 *   // At this point, both the radar and gun are facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   // Set radar to turn independent from the gun's turn
	 *   setAdjustRadarForGunTurn(true);
	 *   turnGunRight(Math.PI / 2);
	 *   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 * 
	 * <p>
	 * Note: The radar compensating this way does count as "turning the radar".
	 * <p>
	 * Note: Calling {@code setAdjustRadarForGunTurn(boolean)} will automatically
	 * call {@link #setAdjustRadarForBodyTurn(boolean)} with the same value, unless
	 * you have already called it earlier. This behavior is primarily for backward
	 * compatibility with older Robocode robots.
	 *
	 * @param adjust {@code true} if the radar must adjust for the gun's turn;
	 *               {@code false} if the radar must turn with the gun's turn.
	 * @see #setAdjustGunForBodyTurn(boolean)
	 * @see #isAdjustRadarForGunTurn()
	 */
	void setAdjustRadarForGunTurn(boolean adjust);

	/**
	 * Sets the radar to turn independent from the robot's turn.
	 * <p>
	 * Ok, so this needs some explanation: The radar is mounted on the gun, and the
	 * gun is mounted on the robot's body. So, normally, if the robot turns 90
	 * degrees to the right, the gun turns, as does the radar. Hence, if the robot
	 * turns 90 degrees to the right, then the gun and radar will turn with it as
	 * the radar is mounted on top of the gun. To compensate for this, you can call
	 * {@code setAdjustRadarForBodyTurn(true)}. When this is set, the radar will
	 * turn independent from the robot's turn, i.e. the radar will compensate for
	 * the robot's turn.
	 * <p>
	 * Note: This method is additive until you reach the maximum the radar can turn.
	 * The "adjust" is added to the amount you set for turning the gun, then capped
	 * by the physics of the game. If you turn infinite, then the adjust is ignored
	 * (and hence overridden).
	 * <p>
	 * Example, assuming the robot, gun, and radar all start out facing up (0
	 * degrees):
	 * 
	 * <pre>
	 *   // Set radar to turn with the robots's turn
	 *   setAdjustRadarForBodyTurn(false); // This is the default
	 *   turnRight(Math.PI / 2);
	 *   // At this point, the body, gun, and radar are all facing right (90 degrees);
	 *
	 *   -- or --
	 *
	 *   // Set radar to turn independent from the robot's turn
	 *   setAdjustRadarForBodyTurn(true);
	 *   turnRight(Math.PI / 2);
	 *   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
	 * </pre>
	 *
	 * @param adjust {@code true} if the radar must adjust for the robot's turn;
	 *               {@code false} if the radar must turn with the robot's turn.
	 * @see #setAdjustGunForBodyTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	void setAdjustRadarForBodyTurn(boolean adjust);
}
