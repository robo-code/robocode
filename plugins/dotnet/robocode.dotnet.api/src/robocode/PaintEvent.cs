#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using net.sf.robocode.peer;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// This event occurs when your robot should paint, where the 
    /// <see cref="Robot.OnPaint(IGraphics)"/> is called on your robot.
    /// <p/>
    /// You can use this event for setting the event priority by calling
    /// <see cref="AdvancedRobot.SetEventPriority(string, int)"/>
    /// </summary>
    [Serializable]
    public sealed class PaintEvent : Event
    {
        private const int DEFAULT_PRIORITY = 5;

        /// <inheritdoc />
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsPaintRobot())
            {
                IPaintEvents listener = ((IPaintRobot) robot).GetPaintEventListener();

                if (listener != null)
                {
                    listener.OnPaint(graphics);
                }
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