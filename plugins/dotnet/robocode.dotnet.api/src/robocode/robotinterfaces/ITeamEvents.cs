#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

namespace robocode.robotinterfaces
{
    /// <summary>
    /// An evnt interface for receiving robot team events with an
    /// <see cref="ITeamRobot"/>.
    /// <seealso cref="ITeamRobot"/>
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
        ///       Out.WriteLine(event.Sender + " sent me: " + evnt.Message);
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="robocode.MessageEvent"/>
        /// <seealso cref="robocode.Event"/>
        /// </summary>
        /// <param name="evnt">the message evnt sent by the game</param>
        void OnMessageReceived(MessageEvent evnt);
    }
}
//doc