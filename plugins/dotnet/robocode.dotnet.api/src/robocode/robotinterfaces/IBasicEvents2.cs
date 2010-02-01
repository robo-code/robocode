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
    /// First extended version of the <see cref="IBasicEvents"/> interface.
    /// </summary>
    public interface IBasicEvents2 : IBasicEvents
    {
        /// <summary>
        /// This method is called after end of the battle, even when the battle is aborted.
        /// You should override it in your robot if you want to be informed of this evnt.
        /// <p/>
        /// <example>
        /// <pre>
        ///   public void OnBattleEnded(BattleEndedEvent evnt) {
        ///       Out.println("The battle has ended");
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="BattleEndedEvent"/>
        /// <seealso cref="robocode.WinEvent"/>
        /// <seealso cref="robocode.DeathEvent"/>
        /// <seealso cref="robocode.Event"/>
        /// </summary>
        void OnBattleEnded(BattleEndedEvent evnt);
    }
}

//doc