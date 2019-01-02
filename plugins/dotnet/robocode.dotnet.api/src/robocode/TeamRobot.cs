/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Collections.Generic;
using Robocode.RobotInterfaces;
using Robocode.RobotInterfaces.Peer;

namespace Robocode
{
    /// <summary>
    /// An advanced type of robot that supports sending messages between team
    /// mates in a robot team.
    /// <p/>
    /// If you have not done already, you should create a <see cref="Robot"/> or
    /// <see cref="AdvancedRobot"/> first.
    /// <seealso cref="JuniorRobot"/>
    /// <seealso cref="Robot"/>
    /// <seealso cref="AdvancedRobot"/>
    /// <seealso cref="RateControlRobot"/>
    /// <seealso cref="IDroid"/>
    /// <seealso cref="IBorderSentry"/>
    /// </summary>
    public abstract class TeamRobot : AdvancedRobot, ITeamRobot, ITeamEvents
    {

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
        /// </example>
        /// </summary>
        /// <param name="message">The message to broadcast to all teammates
        /// if the message could not be broadcasted to the teammates.
        /// </param>
        /// <seealso cref="IsTeammate(string)"/>
        /// <seealso cref="Teammates()"/>
        /// <seealso cref="SendMessage(string, object)"/>
        public void BroadcastMessage(object message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).BroadcastMessage(message);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Returns a list containing all MessageEvents currently in the robot's queue.
        /// You might, for example, call this while processing another event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   foreach (MessageEvent e in GetMessageEvents())
        ///   {
        ///       // do something with e
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="OnMessageReceived(MessageEvent)"/>
        /// <seealso cref="MessageEvent"/>
        /// </summary>
        public IList<MessageEvent> GetMessageEvents()
        {
            if (peer != null)
            {
                return new List<MessageEvent>(((ITeamRobotPeer) peer).GetMessageEvents());
            }
            UninitializedException();
            return null; // never called
        }

        /// <inheritdoc cref="ITeamRobot.GetTeamEventListener()"/>
        ITeamEvents ITeamRobot.GetTeamEventListener()
        {
            return this; // this robot is listening
        }

        /// <summary>
        /// Returns the names of all teammates, or null there is no teammates.
        /// The length of the string array is equal to the number of teammates.
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
        /// <seealso cref="IsTeammate(string)"/>
        /// <seealso cref="BroadcastMessage(object)"/>
        /// <seealso cref="SendMessage(string, object)"/>
        /// </summary>
        public string[] Teammates
        {
            get
            {
                if (peer != null)
                {
                    return ((ITeamRobotPeer) peer).GetTeammates();
                }
                UninitializedException();
                return null;
            }
        }

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
        /// <seealso cref="Teammates"/>
        /// <seealso cref="BroadcastMessage(object)"/>
        /// <seealso cref="SendMessage(string, object)"/>
        /// </summary>
        /// <param name="name">The robot name to check</param> 
        public bool IsTeammate(string name)
        {
            if (peer != null)
            {
                return ((ITeamRobotPeer) peer).IsTeammate(name);
            }
            UninitializedException();
            return false;
        }

        /// <inheritdoc />
        public virtual void OnMessageReceived(MessageEvent evnt)
        {
        }

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
        /// <seealso cref="IsTeammate(string)"/>
        /// <seealso cref="Teammates"/>
        /// <seealso cref="BroadcastMessage(object)"/>
        /// </summary>
        /// <param name="name">The name of the intended recipient of the message</param>
        /// <param name="message">The message to send</param>
        public void SendMessage(string name, object message)
        {
            if (peer != null)
            {
                ((ITeamRobotPeer) peer).SendMessage(name, message);
            }
            else
            {
                UninitializedException();
            }
        }
    }
}
//doc