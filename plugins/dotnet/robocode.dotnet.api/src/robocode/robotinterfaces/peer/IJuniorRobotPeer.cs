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
namespace robocode.robotinterfaces.peer
{
    /// <summary>
    /// The junior robot peer for junior robot types like <see cref="robocode.JuniorRobot}.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// <seealso cref="IBasicRobotPeer
    /// <seealso cref="IStandardRobotPeer
    /// <seealso cref="IAdvancedRobotPeer
    /// <seealso cref="ITeamRobotPeer
    /// @since 1.6
    /// </summary>
    public interface IJuniorRobotPeer : IBasicRobotPeer
    {
        /// <summary>
        /// Moves this robot forward or backwards by pixels and turns this robot
        /// right or left by degrees at the same time. The robot will move in a curve
        /// that follows a perfect circle, and the moving and turning will end at
        /// exactly the same time.
        /// <p/>
        /// Note that the max. velocity and max. turn rate is automatically adjusted,
        /// which means that the robot will move slower the sharper the turn is
        /// compared to the distance.
        /// <p/>
        /// Note that both positive and negative values can be given as input:
        /// <ul>
        /// <li>If the {@code distance} parameter is set to a positive value, it
        /// means that the robot is set to move forward, and a negative value means
        /// that the robot is set to move backward. If set to 0, the robot will not
        /// move, but will be able to turn.
        /// <li>If the {@code radians} parameter is set to a positive value, it means
        /// that the robot is set to turn to the right, and a negative value means
        /// that the robot is set to turn to the left. If set to 0, the robot will
        /// not turn, but will be able to move.
        /// </ul>
        ///
        /// <param name="distance the distance to move measured in pixels.
        ///                 If {@code distance} > 0 the robot is set to move forward.
        ///                 If {@code distance} < 0 the robot is set to move backward.
        ///                 If {@code distance} = 0 the robot will not move anywhere, but just
        ///                 finish its turn.
        /// <param name="radians  the amount of radians to turn the robot's body.
        ///                 If {@code radians} > 0 the robot's body is set to turn right.
        ///                 If {@code radians} < 0 the robot's body is set to turn left.
        ///                 If {@code radians} = 0 the robot's body is set to Stop turning.
        /// <seealso cref="IBasicRobotPeer#move(double) move(double)
        /// <seealso cref="IBasicRobotPeer#turnBody(double) turnBody(double)
        /// <seealso cref="IBasicRobotPeer#getBodyHeading() getBodyHeading()
        /// <seealso cref="IBasicRobotPeer#getX() getX()
        /// <seealso cref="IBasicRobotPeer#getY() getY()
        /// </summary>
        void turnAndMove(double distance, double radians);
    }
}

//happy