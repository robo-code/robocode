/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
using System;
using System.Drawing;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// This evnt is sent to {@link Robot#OnStatus(StatusEvent) OnStatus()} every
    /// turn in a battle to provide the status of the robot.
    ///
    /// @author Flemming N. Larsen (original)
    /// @since 1.5
    /// </summary>
    [Serializable]
    public sealed class StatusEvent : Event
    {
        private const int DEFAULT_PRIORITY = 99;

        private readonly RobotStatus status;

        /// <summary>
        /// This constructor is called internally from the game in order to create
        /// a new {@link RobotStatus}.
        ///
        /// @param status the current states
        /// </summary>
        public StatusEvent(RobotStatus status)
        {
            this.status = status;
        }

        /// <summary>
        /// Returns the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
        ///
        /// @return the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
        /// @see #getTime()
        /// </summary>
        public RobotStatus Status
        {
            get { return status; }
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
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnStatus(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte SerializationType
        {
            get { throw new Exception("Serialization of this type is not supported"); }
        }
    }
}
//happy