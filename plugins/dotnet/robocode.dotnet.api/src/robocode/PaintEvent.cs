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
    /// This evnt occurs when your robot should paint, where the {@link
    /// Robot#onPaint(Graphics) onPaint()} is called on your robot.
    /// </p>
    /// You can use this evnt for setting the evnt priority by calling
    /// {@link AdvancedRobot#setEventPriority(string, int)
    /// setEventPriority("PaintEvent", priority)}
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
        internal override int getDefaultPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void dispatch(IBasicRobot robot, IRobotStaticsN statics, Graphics graphics)
        {
            if (statics.isPaintRobot())
            {
                IPaintEvents listener = ((IPaintRobot) robot).getPaintEventListener();

                if (listener != null)
                {
                    listener.onPaint(graphics);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            throw new Exception("Serialization of this type is not supported");
        }
    }
}
//happy