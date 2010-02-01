﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

namespace robocode.robotinterfaces
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
        /// <pre>
        ///   public void OnRoundEnded(RoundEndedEvent event) {
        ///       Out.WriteLine("The round has ended");
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="RoundEndedEvent"/>
        /// <seealso cref="IBasicEvents2.OnBattleEnded(BattleEndedEvent)"/>
        /// <seealso cref="robocode.WinEvent"/>
        /// <seealso cref="robocode.DeathEvent"/>
        /// <seealso cref="robocode.Event"/>
        /// </summary>
        /// <param name="evnt"></param>
        void OnRoundEnded(RoundEndedEvent evnt);
    }
}
