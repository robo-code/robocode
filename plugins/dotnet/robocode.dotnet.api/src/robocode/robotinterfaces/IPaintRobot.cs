/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
namespace robocode.robotinterfaces
{
    /// <summary>
    /// A robot interface that makes it possible for a robot to receive paint events.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @since 1.6
    /// </summary>
    public interface IPaintRobot : IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to notify this robot about painting
        /// events. Hence, this method must be implemented so it returns your
        /// {@link IPaintEvents} listener.
        ///
        /// @return listener to paint events or {@code null} if this robot should
        ///         not receive the notifications.
        /// @since 1.6
        /// </summary>
        IPaintEvents GetPaintEventListener();
    }
}
//happy