/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.IO;
using Robocode.RobotInterfaces.Peer;

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// A robot interface for creating a basic type of robot like <see cref="Robocode.Robot"/>
    /// that is able to receive common robot events, but not interactive events as
    /// with the <see cref="Robocode.Robot"/> class.
    /// A basic robot allows blocking calls only and cannot handle custom events nor
    /// writes to the file system like an advanced robot.
    /// <seealso cref="Robocode.Robot"/>
    /// <seealso cref="IJuniorRobot"/>
    /// <seealso cref="IInteractiveRobot"/>
    /// <seealso cref="IAdvancedRobot"/>
    /// <seealso cref="ITeamRobot"/>
    /// </summary>
    public interface IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to invoke the <see cref="IRunnable.Run()"/>
        /// method of your robot, where the program of your robot is implemented.
        /// </summary>
        IRunnable GetRobotRunnable();

        /// <summary>
        /// This method is called by the game to notify this robot about basic
        /// robot event. Hence, this method must be implemented so it returns your
        /// <see cref="IBasicEvents"/> listener.
        /// </summary>
        IBasicEvents GetBasicEventListener();

        /// <summary>
        /// Do not call this method! Your robot will simply stop interacting with
        /// the game.
        /// <p/>
        /// This method is called by the game. A robot peer is the object that deals
        /// with game mechanics and rules, and makes sure your robot abides by them.
        /// </summary>
        void SetPeer(IBasicRobotPeer peer);

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// This method is called by the game when setting the Out stream for your
        /// robot.
        /// </summary>
        void SetOut(TextWriter output);
    }
}

//doc