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
using System.Drawing.Imaging;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// This evnt occurs when your robot should paint, where the {@link
    /// Robot#OnPaint(Graphics) OnPaint()} is called on your robot.
    /// </p>
    /// You can use this evnt for setting the evnt priority by calling
    /// {@link AdvancedRobot#SetEventPriority(string, int)
    /// SetEventPriority("PaintEvent", priority)}
    ///
    /// @author Flemming N. Larsen (original)
    /// </summary>
    [Serializable]
    public sealed class PaintEvent : Event
    {
        private const int DEFAULT_PRIORITY = 5;

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
            if (statics.IsPaintRobot())
            {
                IPaintEvents listener = ((IPaintRobot) robot).GetPaintEventListener();

                if (listener != null)
                {
                    listener.OnPaint(graphics);
                }
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