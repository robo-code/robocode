/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// An event interface for receiving paint events with an <see cref="IPaintRobot"/>.
    /// <seealso cref="IPaintRobot"/>
    /// </summary>
    public interface IPaintEvents
    {
        /// <summary>
        /// This method is called every time the robot is painted. You should
        /// override this method if you want to draw items for your robot on the
        /// battle field, e.g. targets, virtual bullets etc.
        /// <p/>
        /// This method is very useful for debugging your robot.
        /// <p/>
        /// Note that the robot will only be painted if the "Paint" is enabled on the
        /// robot's console window; otherwise the robot will never get painted (the
        /// reason being that all robots might have graphical items that must be
        /// painted, and then you might not be able to tell what graphical items that
        /// have been painted for your robot).
        /// <p/>
        /// Also note that the coordinate system for the graphical context where you
        /// paint items fits for the Robocode coordinate system where (0, 0) is at
        /// the bottom left corner of the battlefield, where X is towards right and Y
        /// is upwards.
        /// <seealso cref="System.Drawing.Graphics"/>
        /// </summary>
        /// <param name="graphics">The graphics context to use for painting graphical items for the robot</param>
        void OnPaint(IGraphics graphics);
    }
}

//doc