﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using Robocode;

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// Second extended version of the <see cref="IBasicEvents"/> interface.
    /// </summary>
    public interface IBasicEvents3 : IBasicEvents2
    {
        /// <summary>
        /// This method is called after the end of a round.
        /// You should override it in your robot if you want to be informed of this event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void OnRoundEnded(RoundEndedEvent event)
        ///   {
        ///       Out.WriteLine("The round has ended");
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="RoundEndedEvent"/>
        /// <seealso cref="IBasicEvents2.OnBattleEnded(BattleEndedEvent)"/>
        /// <seealso cref="Robocode.WinEvent"/>
        /// <seealso cref="Robocode.DeathEvent"/>
        /// <seealso cref="Robocode.Event"/>
        /// </summary>
        /// <param name="evnt"></param>
        void OnRoundEnded(RoundEndedEvent evnt);
    }
}
