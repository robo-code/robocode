/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The junior robot peer for junior robot types like <see cref="Robocode.JuniorRobot"/>.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer"/>
    /// <seealso cref="IStandardRobotPeer"/>
    /// <seealso cref="IAdvancedRobotPeer"/>
    /// <seealso cref="ITeamRobotPeer"/>
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
        /// <li>If the distance parameter is set to a positive value, it
        /// means that the robot is set to move forward, and a negative value means
        /// that the robot is set to move backward. If set to 0, the robot will not
        /// move, but will be able to turn.</li>
        /// <li>If the radians parameter is set to a positive value, it means
        /// that the robot is set to turn to the right, and a negative value means
        /// that the robot is set to turn to the left. If set to 0, the robot will
        /// not turn, but will be able to move.</li>
        /// </ul>
        /// <seealso cref="IBasicRobotPeer.Move"/>
        /// <seealso cref="IBasicRobotPeer.TurnBody"/>
        /// <seealso cref="IBasicRobotPeer.GetBodyHeading"/>
        /// <seealso cref="IBasicRobotPeer.GetX"/>
        /// <seealso cref="IBasicRobotPeer.GetY"/>
        /// </summary>
        /// <param name="distance">
        ///   The distance to move measured in pixels.
        ///   If distance &gt; 0 the robot is set to move forward.
        ///   If distance &lt; 0 the robot is set to move backward.
        ///   If distance = 0 the robot will not move anywhere, but just finish its turn.
        /// </param>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's body.
        ///   If radians &gt; 0 the robot's body is set to turn right.
        ///   If radians &lt; 0 the robot's body is set to turn left.
        ///   If radians = 0 the robot's body is set to stop turning.
        /// </param>
        void TurnAndMove(double distance, double radians);
    }
}

//doc