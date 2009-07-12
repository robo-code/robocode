/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added missing getMessageEvents()
 *     - Updated Javadocs
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
using System.Collections.Generic;
using System.Runtime.Serialization;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace robocode
{
    /// <summary>
    /// An an advanced type of robot that supports sending messages between team
    /// mates in a robot team.
    /// <p/>
    /// If you have not done already, you should create a {@link Robot} or
    /// {@link AdvancedRobot} first.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// @author Pavel Savara (contributor)
    /// @see JuniorRobot
    /// @see Robot
    /// @see AdvancedRobot
    /// @see Droid
    /// </summary>
    public abstract class TeamRobot : AdvancedRobot, ITeamRobot, ITeamEvents
    {
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
        public void broadcastMessage(object message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).broadcastMessage(message);
            }
            else
            {
                uninitializedException();
            }
        }

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
        /// @see #onMessageReceived(MessageEvent)
        /// @see MessageEvent
        /// @since 1.2.6
        /// </summary>
        public IList<MessageEvent> getMessageEvents()
        {
            if (peer != null)
            {
                return new List<MessageEvent>(((ITeamRobotPeer) peer).getMessageEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// {@inheritDoc}
        /// </summary>
        public ITeamEvents getTeamEventListener()
        {
            return this; // this robot is listening
        }

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
        public string[] getTeammates()
        {
            if (peer != null)
            {
                return ((ITeamRobotPeer) peer).getTeammates();
            }
            uninitializedException();
            return null;
        }

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
        public bool isTeammate(string name)
        {
            if (peer != null)
            {
                return ((ITeamRobotPeer) peer).isTeammate(name);
            }
            uninitializedException();
            return false;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void onMessageReceived(MessageEvent evnt)
        {
        }

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
        public void sendMessage(string name, ISerializable message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).sendMessage(name, message);
            }
            else
            {
                uninitializedException();
            }
        }
    }
}
//happy