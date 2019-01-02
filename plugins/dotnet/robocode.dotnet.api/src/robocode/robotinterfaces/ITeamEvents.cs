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
    /// An event interface for receiving robot team events with an <see cref="ITeamRobot"/>.
    /// <seealso cref="ITeamRobot"/>
    /// </summary>
    public interface ITeamEvents
    {
        /// <summary>
        /// This method is called when your robot receives a message from a teammate.
        /// You should override it in your robot if you want to be informed of this
        /// event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void OnMessageReceived(MessageEvent evnt)
        ///   {
        ///       Out.WriteLine(event.Sender + " sent me: " + evnt.Message);
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.MessageEvent"/>
        /// <seealso cref="Robocode.Event"/>
        /// </summary>
        /// <param name="evnt">The message event sent by the game</param>
        void OnMessageReceived(MessageEvent evnt);
    }
}
//doc