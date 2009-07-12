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
    /// The team robot peer for team robots like {@link robocode.TeamRobot}.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see IBasicRobotPeer
    /// @see IStandardRobotPeer
    /// @see IAdvancedRobotPeer
    /// @see IJuniorRobotPeer
    /// @since 1.6
    /// </summary>
    public interface ITeamRobotPeer : IAdvancedRobotPeer
    {
        /// <summary>
        /// Returns the names of all teammates, or {@code null} there is no
        /// teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void run() {
        ///       // Prints output all teammates
        ///       string[] teammates = getTeammates();
        ///       if (teammates != null) {
        ///           for (string member : teammates) {
        ///               output.println(member);
        ///           }
        ///       }
        ///   }
        /// </pre>
        ///
        /// @return a string array containing the names of all your teammates, or
        ///         {@code null} if there is no teammates. The length of the string array
        ///         is equal to the number of teammates.
        /// @see #isTeammate(string)
        /// @see #broadcastMessage(ISerializable)
        /// @see #sendMessage(string, ISerializable)
        /// </summary>
        string[] getTeammates();

        /// <summary>
        /// Checks if a given robot name is the name of one of your teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void onScannedRobot(ScannedRobotEvent e) {
        ///       if (isTeammate(e.getName()) {
        ///           return;
        ///       }
        ///       fire(1);
        ///   }
        /// </pre>
        ///
        /// @param name the robot name to check
        /// @return {@code true} if the specified name belongs to one of your
        ///         teammates; {@code false} otherwise.
        /// @see #getTeammates()
        /// @see #broadcastMessage(ISerializable)
        /// @see #sendMessage(string, ISerializable)
        /// </summary>
        bool isTeammate(string name);

        /// <summary>
        /// Broadcasts a message to all teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void run() {
        ///       broadcastMessage("I'm here!");
        ///   }
        /// </pre>
        ///
        /// @param message the message to broadcast to all teammates
        /// @ if the message could not be broadcasted to the
        ///                     teammates
        /// @see #isTeammate(string)
        /// @see #getTeammates()
        /// @see #sendMessage(string, ISerializable)
        /// </summary>
        void broadcastMessage(object message);

        /// <summary>
        /// Sends a message to one (or more) teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void run() {
        ///       sendMessage("sample.DroidBot", "I'm here!");
        ///   }
        /// </pre>
        ///
        /// @param name	the name of the intended recipient of the message
        /// @param message the message to send
        /// @ if the message could not be sent
        /// @see #isTeammate(string)
        /// @see #getTeammates()
        /// @see #broadcastMessage(ISerializable)
        /// </summary>
        void sendMessage(string name, object message);

        /// <summary>
        /// Returns a vector containing all MessageEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (MessageEvent e : getMessageEvents()) {
        ///      // do something with e
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all MessageEvents currently in the robot's
        ///         queue
        /// @see robocode.robotinterfaces.ITeamEvents#onMessageReceived(MessageEvent)
        ///      onMessageReceived(MessageEvent)
        /// @see MessageEvent
        /// @since 1.2.6
        /// </summary>
        IList<MessageEvent> getMessageEvents();
    }
}
//happy