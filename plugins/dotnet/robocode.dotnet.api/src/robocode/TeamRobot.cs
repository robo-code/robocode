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
 *     - Added missing GetMessageEvents()
 *     - Updated Javadocs
 *     - The UninitializedException() method does not need a method name as input
 *       parameter anymore
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
using System.Collections.Generic;
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
        ///   public void Run() {
        ///       BroadcastMessage("I'm here!");
        ///   }
        /// </pre>
        ///
        /// @param message the message to broadcast to all teammates
        /// @ if the message could not be broadcasted to the
        ///                     teammates
        /// @see #IsTeammate(string)
        /// @see #getTeammates()
        /// @see #SendMessage(string, ISerializable)
        /// </summary>
        public void BroadcastMessage(object message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).broadcastMessage(message);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Returns a vector containing all MessageEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (MessageEvent e : GetMessageEvents()) {
        ///      // do something with e
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all MessageEvents currently in the robot's
        ///         queue
        /// @see #OnMessageReceived(MessageEvent)
        /// @see MessageEvent
        /// @since 1.2.6
        /// </summary>
        public IList<MessageEvent> GetMessageEvents()
        {
            if (peer != null)
            {
                return new List<MessageEvent>(((ITeamRobotPeer) peer).getMessageEvents());
            }
            UninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// {@inheritDoc}
        /// </summary>
        ITeamEvents ITeamRobot.GetTeamEventListener()
        {
            return this; // this robot is listening
        }

        /// <summary>
        /// Returns the names of all teammates, or {@code null} there is no
        /// teammates.
        /// <p/>
        /// Example:
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
        ///
        /// @return a string array containing the names of all your teammates, or
        ///         {@code null} if there is no teammates. The length of the string array
        ///         is equal to the number of teammates.
        /// @see #IsTeammate(string)
        /// @see #BroadcastMessage(ISerializable)
        /// @see #SendMessage(string, ISerializable)
        /// </summary>
        public string[] Teammates
        {
            get
            {
                if (peer != null)
                {
                    return ((ITeamRobotPeer) peer).getTeammates();
                }
                UninitializedException();
                return null;
            }
        }

        /// <summary>
        /// Checks if a given robot name is the name of one of your teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void OnScannedRobot(ScannedRobotEvent e) {
        ///       if (IsTeammate(e.getName()) {
        ///           return;
        ///       }
        ///       Fire(1);
        ///   }
        /// </pre>
        ///
        /// @param name the robot name to check
        /// @return {@code true} if the specified name belongs to one of your
        ///         teammates; {@code false} otherwise.
        /// @see #getTeammates()
        /// @see #BroadcastMessage(ISerializable)
        /// @see #SendMessage(string, ISerializable)
        /// </summary>
        public bool IsTeammate(string name)
        {
            if (peer != null)
            {
                return ((ITeamRobotPeer) peer).isTeammate(name);
            }
            UninitializedException();
            return false;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMessageReceived(MessageEvent evnt)
        {
        }

        /// <summary>
        /// Sends a message to one (or more) teammates.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void Run() {
        ///       SendMessage("sample.DroidBot", "I'm here!");
        ///   }
        /// </pre>
        ///
        /// @param name	the name of the intended recipient of the message
        /// @param message the message to send
        /// @ if the message could not be sent
        /// @see #IsTeammate(string)
        /// @see #getTeammates()
        /// @see #BroadcastMessage(ISerializable)
        /// </summary>
        public void SendMessage(string name, object message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).sendMessage(name, message);
            }
            else
            {
                UninitializedException();
            }
        }
    }
}
//happy