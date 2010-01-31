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
    /// An evnt interface for receiving robot team events with an
    /// <see cref="ITeamRobot}.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// <seealso cref="ITeamRobot
    /// @since 1.6
    /// </summary>
    public interface ITeamEvents
    {
        /// <summary>
        /// This method is called when your robot receives a message from a teammate.
        /// You should override it in your robot if you want to be informed of this
        /// evnt.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void OnMessageReceived(MessageEvent evnt) {
        ///       Out.println(event.getSender() + " sent me: " + evnt.getMessage());
        ///   }
        /// </pre>
        ///
        /// <param name="evnt the message evnt sent by the game
        /// <seealso cref="robocode.MessageEvent
        /// <seealso cref="robocode.Event
        /// </summary>
        void OnMessageReceived(MessageEvent evnt);
    }
}

//happy