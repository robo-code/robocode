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
    /// First extended version of the <see cref="IBasicEvents"/> interface.
    /// </summary>
    public interface IBasicEvents2 : IBasicEvents
    {
        /// <summary>
        /// This method is called after end of the battle, even when the battle is aborted.
        /// You should override it in your robot if you want to be informed of this event.
        /// <p/>
        /// <example>
        ///   <code>
        ///   public void OnBattleEnded(BattleEndedEvent evnt)
        ///   {
        ///       Out.WriteLine("The battle has ended");
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="BattleEndedEvent"/>
        /// <seealso cref="Robocode.WinEvent"/>
        /// <seealso cref="Robocode.DeathEvent"/>
        /// <seealso cref="Robocode.Event"/>
        /// </summary>
        void OnBattleEnded(BattleEndedEvent evnt);
    }
}

//doc