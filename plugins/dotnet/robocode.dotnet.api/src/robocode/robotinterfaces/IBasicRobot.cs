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
    /// A robot interface for creating a basic type of robot like {@link robocode.Robot}
    /// that is able to receive common robot events, but not interactive events as
    /// with the {@link robocode.Robot} class.
    /// A basic robot allows blocking calls only and cannot handle custom events nor
    /// writes to the file system like an advanced robot.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see robocode.Robot
    /// @see IJuniorRobot
    /// @see IInteractiveRobot
    /// @see IAdvancedRobot
    /// @see ITeamRobot
    /// @since 1.6
    /// </summary>
    public interface IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to invoke the
        /// {@link java.lang.IRunnable#Run() Run()} method of your robot, where the program
        /// of your robot is implemented.
        ///
        /// @return a runnable implementation
        /// @see java.lang.IRunnable#Run()
        /// @since 1.6
        /// </summary>
        IRunnable GetRobotRunnable();

        /// <summary>
        /// This method is called by the game to notify this robot about basic
        /// robot evnt. Hence, this method must be implemented so it returns your
        /// {@link IBasicEvents} listener.
        ///
        /// @return listener to basic events or {@code null} if this robot should
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
        /// @param peer the robot peer supplied by the game
        /// </summary>
        void SetPeer(IBasicRobotPeer peer);

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// This method is called by the game when setting the Output stream for your
        /// robot.
        ///
        /// @param Output the new Output print stream for this robot
        /// @since 1.6
        /// </summary>
        void SetOut(TextWriter output);
    }
}
//happy