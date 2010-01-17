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
    /// First extended version of the {@link IBasicEvents} interface.
    ///
    /// @author Pavel Savara (original)
    /// @since 1.6.1
    /// </summary>
    public interface IBasicEvents2 : IBasicEvents
    {
        /// <summary>
        /// This method is called after end of the battle, even when the battle is aborted.
        /// You should override it in your robot if you want to be informed of this evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   public void OnBattleEnded(BattleEndedEvent evnt) {
        ///       Out.println("The battle has ended");
        ///   }
        /// </pre>
        ///
        /// @param evnt the battle-ended evnt set by the game
        /// @see BattleEndedEvent
        /// @see robocode.WinEvent
        /// @see robocode.DeathEvent
        /// @see robocode.Event
        /// </summary>
        void OnBattleEnded(BattleEndedEvent evnt);
    }
}

//happy