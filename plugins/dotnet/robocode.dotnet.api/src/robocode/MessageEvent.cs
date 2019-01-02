/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.peer;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// A MessageEvent is sent to <see cref="TeamRobot.OnMessageReceived(MessageEvent)"/>
    /// when a teammate sends a message to your robot.
    /// You can use the information contained in this event to determine what to do.
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
        /// </summary>
        public MessageEvent(string sender, object message)
        {
            this.sender = sender;
            this.message = message;
        }

        /// <summary>
        /// Returns the name of the sending robot.
        /// </summary>
        public string Sender
        {
            get { return sender; }
        }

        /// <summary>
        /// Returns the message itself.
        /// </summary>
        public object Message
        {
            get { return message; }
        }

        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

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
            get { throw new System.Exception("Serialization of event type not supported"); }
        }
    }
}
//doc