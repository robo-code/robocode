#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System.Collections.Generic;
using Robocode;

namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The team robot peer for team robots like <see cref="Robocode.TeamRobot"/>.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer"/>
    /// <seealso cref="IStandardRobotPeer"/>
    /// <seealso cref="IAdvancedRobotPeer"/>
    /// <seealso cref="IJuniorRobotPeer"/>
    /// </summary>
    public interface ITeamRobotPeer : IAdvancedRobotPeer
    {
        /// <summary>
        /// Returns the names of all teammates, or null there is no
        /// teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void Run() {
        ///       // Prints Out all teammates
        ///       string[] teammates = GetTeammates();
        ///       if (teammates != null) {
        ///           foreach (string member in teammates) {
        ///               Out.WriteLine(member);
        ///           }
        ///       }
        ///   }
        /// </pre>
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
        /// <pre>
        ///   public void OnScannedRobot(ScannedRobotEvent e) {
        ///       if (IsTeammate(e.Name) {
        ///           return;
        ///       }
        ///       Fire(1);
        ///   }
        /// </pre>
        /// </example>
        ///
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="BroadcastMessage"/>
        /// <seealso cref="SendMessage"/>
        /// </summary>
        /// <param name="name">the robot name to check</param>
        bool IsTeammate(string name);

        /// <summary>
        /// Broadcasts a message to all teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void Run() {
        ///       BroadcastMessage("I'm here!");
        ///   }
        /// </pre>
        ///</example>
        /// <seealso cref="IsTeammate"/>
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="SendMessage"/>
        /// </summary>
        /// <param name="message">the message to broadcast to all teammates</param>
        void BroadcastMessage(object message);

        /// <summary>
        /// Sends a message to one (or more) teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void Run() {
        ///       SendMessage("sample.DroidBot", "I'm here!");
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="IsTeammate"/>
        /// <seealso cref="GetTeammates"/>
        /// <seealso cref="BroadcastMessage"/>
        /// </summary>
        /// <param name="name">the name of the intended recipient of the message</param>
        /// <param name="message">the message to send</param>
        void SendMessage(string name, object message);

        /// <summary>
        /// Returns a vector containing all MessageEvents currently in the robot's
        /// queue. You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        /// <pre>
        ///   foreach (MessageEvent e in GetMessageEvents()) {
        ///      // do something with e
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.ITeamEvents.OnMessageReceived(MessageEvent)"/>
        /// <seealso cref="MessageEvent"/>
        /// </summary>
        IList<MessageEvent> GetMessageEvents();
    }
}

//doc