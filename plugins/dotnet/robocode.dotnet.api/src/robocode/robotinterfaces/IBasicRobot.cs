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
using System.IO;
using robocode.robotinterfaces.peer;

namespace robocode.robotinterfaces
{
    /// <summary>
    /// A robot interface for creating a basic type of robot like <see cref="robocode.Robot}
    /// that is able to receive common robot events, but not interactive events as
    /// with the <see cref="robocode.Robot} class.
    /// A basic robot allows blocking calls only and cannot handle custom events nor
    /// writes to the file system like an advanced robot.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// <seealso cref="robocode.Robot
    /// <seealso cref="IJuniorRobot
    /// <seealso cref="IInteractiveRobot
    /// <seealso cref="IAdvancedRobot
    /// <seealso cref="ITeamRobot
    /// @since 1.6
    /// </summary>
    public interface IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to invoke the
        /// <see cref="java.lang.IRunnable#Run() Run()} method of your robot, where the program
        /// of your robot is implemented.
        ///
        /// @return a runnable implementation
        /// <seealso cref="java.lang.IRunnable#Run()
        /// @since 1.6
        /// </summary>
        IRunnable GetRobotRunnable();

        /// <summary>
        /// This method is called by the game to notify this robot about basic
        /// robot evnt. Hence, this method must be implemented so it returns your
        /// <see cref="IBasicEvents} listener.
        ///
        /// @return listener to basic events or null if this robot should
        ///         not receive the notifications.
        /// @since 1.6
        /// </summary>
        IBasicEvents GetBasicEventListener();

        /// <summary>
        /// Do not call this method! Your robot will simply Stop interacting with
        /// the game.
        /// <p/>
        /// This method is called by the game. A robot peer is the object that deals
        /// with game mechanics and rules, and makes sure your robot abides by them.
        ///
        /// <param name="peer the robot peer supplied by the game
        /// </summary>
        void SetPeer(IBasicRobotPeer peer);

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// This method is called by the game when setting the Out stream for your
        /// robot.
        ///
        /// <param name="Out the new Out print stream for this robot
        /// @since 1.6
        /// </summary>
        void SetOut(TextWriter output);
    }
}

//happy