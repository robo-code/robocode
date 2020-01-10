/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces.peer;


/**
 * The junior robot peer for junior robot types like {@link robocode.JuniorRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @see IBasicRobotPeer
 * @see IStandardRobotPeer
 * @see IAdvancedRobotPeer
 * @see ITeamRobotPeer
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IJuniorRobotPeer extends IBasicRobotPeer {

	/**
	 * Moves this robot forward or backwards by pixels and turns this robot
	 * right or left by degrees at the same time. The robot will move in a curve
	 * that follows a perfect circle, and the moving and turning will end at
	 * exactly the same time.
	 * <p>
	 * Note that the max. velocity and max. turn rate is automatically adjusted,
	 * which means that the robot will move slower the sharper the turn is
	 * compared to the distance.
	 * <p>
	 * Note that both positive and negative values can be given as input:
	 * <ul>
	 * <li>If the {@code distance} parameter is set to a positive value, it
	 * means that the robot is set to move forward, and a negative value means
	 * that the robot is set to move backward. If set to 0, the robot will not
	 * move, but will be able to turn.
	 * <li>If the {@code radians} parameter is set to a positive value, it means
	 * that the robot is set to turn to the right, and a negative value means
	 * that the robot is set to turn to the left. If set to 0, the robot will
	 * not turn, but will be able to move.
	 * </ul>
	 *
	 * @param distance the distance to move measured in pixels.
	 *                 If {@code distance} > 0 the robot is set to move forward.
	 *                 If {@code distance} < 0 the robot is set to move backward.
	 *                 If {@code distance} = 0 the robot will not move anywhere, but just
	 *                 finish its turn.
	 * @param radians  the amount of radians to turn the robot's body.
	 *                 If {@code radians} > 0 the robot's body is set to turn right.
	 *                 If {@code radians} < 0 the robot's body is set to turn left.
	 *                 If {@code radians} = 0 the robot's body is set to stop turning.
	 * @see IBasicRobotPeer#move(double) move(double)
	 * @see IBasicRobotPeer#turnBody(double) turnBody(double)
	 * @see IBasicRobotPeer#getBodyHeading() getBodyHeading()
	 * @see IBasicRobotPeer#getX() getX()
	 * @see IBasicRobotPeer#getY() getY()
	 */
	void turnAndMove(double distance, double radians);
}
