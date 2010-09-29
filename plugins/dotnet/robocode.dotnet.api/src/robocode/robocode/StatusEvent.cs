#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion


using System;
using net.sf.robocode.peer;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// This event is sent to <see cref="Robot.OnStatus(StatusEvent)"/> every
    /// turn in a battle to provide the status of the robot.
    /// </summary>
    [Serializable]
    public sealed class StatusEvent : Event
    {
        private const int DEFAULT_PRIORITY = 99;

        private readonly RobotStatus status;

        /// <summary>
        /// This constructor is called internally from the game in order to create
        /// a new <see cref="RobotStatus"/>.
        ///
        /// </summary>
        /// <param name="status">the current states</param>
        public StatusEvent(RobotStatus status)
        {
            this.status = status;
        }

        /// <summary>
        /// Returns the <see cref="RobotStatus"/> at the time defined by <see cref="Robot.Time"/>.
        /// <seealso cref="Event.Time"/>
        /// </summary>
        public RobotStatus Status
        {
            get { return status; }
        }

        /// <inheritdoc />
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnStatus(this);
            }
        }

        /// <inheritdoc />
        internal override byte SerializationType
        {
            get { throw new System.Exception("Serialization of this type is not supported"); }
        }
    }
}
//doc