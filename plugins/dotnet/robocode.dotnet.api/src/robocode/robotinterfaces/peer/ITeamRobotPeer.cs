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
using System.Collections.Generic;

namespace robocode.robotinterfaces.peer
{
    /// <summary>
    /// The team robot peer for team robots like <see cref="robocode.TeamRobot}.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer
    /// <seealso cref="IStandardRobotPeer
    /// <seealso cref="IAdvancedRobotPeer
    /// <seealso cref="IJuniorRobotPeer
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
        ///       string[] teammates = getTeammates();
        ///       if (teammates != null) {
        ///           for (string member : teammates) {
        ///               Out.println(member);
        ///           }
        ///       }
        ///   }
        /// </pre>
        /// <seealso cref="IsTeammate(string)
        /// <seealso cref="BroadcastMessage(ISerializable)
        /// <seealso cref="SendMessage(string, ISerializable)
        /// </summary>
        string[] getTeammates();

        /// <summary>
        /// Checks if a given robot name is the name of one of your teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void OnScannedRobot(ScannedRobotEvent e) {
        ///       if (IsTeammate(e.getName()) {
        ///           return;
        ///       }
        ///       Fire(1);
        ///   }
        /// </pre>
        ///
        /// <param name="name the robot name to check
        /// <seealso cref="getTeammates()
        /// <seealso cref="BroadcastMessage(ISerializable)
        /// <seealso cref="SendMessage(string, ISerializable)
        /// </summary>
        bool isTeammate(string name);

        /// <summary>
        /// Broadcasts a message to all teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void Run() {
        ///       BroadcastMessage("I'm here!");
        ///   }
        /// </pre>
        ///
        /// <param name="message the message to broadcast to all teammates
        /// <seealso cref="IsTeammate(string)
        /// <seealso cref="getTeammates()
        /// <seealso cref="SendMessage(string, ISerializable)
        /// </summary>
        void broadcastMessage(object message);

        /// <summary>
        /// Sends a message to one (or more) teammates.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void Run() {
        ///       SendMessage("sample.DroidBot", "I'm here!");
        ///   }
        /// </pre>
        ///
        /// <param name="name	the name of the intended recipient of the message
        /// <param name="message the message to send
        /// <seealso cref="IsTeammate(string)
        /// <seealso cref="getTeammates()
        /// <seealso cref="BroadcastMessage(ISerializable)
        /// </summary>
        void sendMessage(string name, object message);

        /// <summary>
        /// Returns a vector containing all MessageEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// <example>
        /// <pre>
        ///   for (MessageEvent e : GetMessageEvents()) {
        ///      // do something with e
        ///   }
        /// </pre>
        /// <seealso cref="robocode.robotinterfaces.ITeamEvents#OnMessageReceived(MessageEvent)
        ///      OnMessageReceived(MessageEvent)
        /// <seealso cref="MessageEvent
        /// </summary>
        IList<MessageEvent> getMessageEvents();
    }
}

//happy