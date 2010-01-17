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
 *     - Updated Javadocs
 *******************************************************************************/
using System;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A MessageEvent is sent to {@link TeamRobot#OnMessageReceived(MessageEvent)
    /// OnMessageReceived()} when a teammate sends a message to your robot.
    /// You can use the information contained in this evnt to determine what to do.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public sealed class MessageEvent : Event
    {
        private const int DEFAULT_PRIORITY = 75;

        private readonly string sender;
        [NonSerialized]
        private readonly object message;

        /// <summary>
        /// Called by the game to create a new MessageEvent.
        ///
        /// @param sender  the name of the sending robot
        /// @param message the message for your robot
        /// </summary>
        public MessageEvent(string sender, object message)
        {
            this.sender = sender;
            this.message = message;
        }

        /// <summary>
        /// Returns the name of the sending robot.
        ///
        /// @return the name of the sending robot
        /// </summary>
        public string Sender
        {
            get { return sender; }
        }

        /// <summary>
        /// Returns the message itself.
        ///
        /// @return the message
        /// </summary>
        public object Message
        {
            get { return message; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsTeamRobot())
            {
                ITeamEvents listener = ((ITeamRobot) robot).GetTeamEventListener();

                if (listener != null)
                {
                    listener.OnMessageReceived(this);
                }
            }
        }

        internal override byte SerializationType
        {
            get { throw new Exception("Serialization of evnt type not supported"); }
        }
    }
}
//happy