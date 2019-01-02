/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Collections.Generic;
using Robocode;

namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The team robot peer for team robots like <see cref="Robocode.TeamRobot"/>.
    /// <p/>
    /// A robot peer is the object that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer"/>
    /// <seealso cref="IStandardRobotPeer"/>
    /// <seealso cref="IAdvancedRobotPeer"/>
    /// <seealso cref="IJuniorRobotPeer"/>
    /// </summary>
    public interface ITeamRobotPeer : IAdvancedRobotPeer
    {
        /// <summary>
        /// Returns the names of all teammates, or null there is no teammates.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void Run()
        ///   {
        ///       // Prints Out all teammates
        ///       string[] teammates = GetTeammates();
        ///       if (teammates != null)
        ///       {
        ///           foreach (string member in teammates)
        ///           {
        ///               Out.WriteLine(member);
        ///           }
        ///       }
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="IsTeammate"/>
        /// <seealso cref="BroadcastMessage"/>
        /// <seealso cref="SendMessage"/>
        /// </summary>
        string[] GetTeammates();

        /// <summary>
        /// Checks if a given robot name is the name of one of your teammates.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void OnScannedRobot(ScannedRobotEvent e)
        ///   {
        ///       if (IsTeammate(e.Name)
        ///       {
        ///           return;
        ///       }
        ///       Fire(1);
        ///   }
        ///   </code>
        /// </example>
        ///
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="BroadcastMessage"/>
        /// <seealso cref="SendMessage"/>
        /// </summary>
        /// <param name="name">The robot name to check</param>
        bool IsTeammate(string name);

        /// <summary>
        /// Broadcasts a message to all teammates.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void Run()
        ///   {
        ///       BroadcastMessage("I'm here!");
        ///   }
        ///   </code>
        ///</example>
        /// <seealso cref="IsTeammate"/>
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="SendMessage"/>
        /// </summary>
        /// <param name="message">The message to broadcast to all teammates</param>
        void BroadcastMessage(object message);

        /// <summary>
        /// Sends a message to one (or more) teammates.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void Run()
        ///   {
        ///       SendMessage("Sample.DroidBot", "I'm here!");
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="IsTeammate"/>
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="BroadcastMessage"/>
        /// </summary>
        /// <param name="name">The name of the intended recipient of the message</param>
        /// <param name="message">The message to send</param>
        void SendMessage(string name, object message);

        /// <summary>
        /// Returns a list containing all MessageEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (MessageEvent e in GetMessageEvents())
        ///   {
        ///      // do something with e
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.ITeamEvents.OnMessageReceived(MessageEvent)"/>
        /// <seealso cref="MessageEvent"/>
        /// </summary>
        IList<MessageEvent> GetMessageEvents();
    }
}

//doc