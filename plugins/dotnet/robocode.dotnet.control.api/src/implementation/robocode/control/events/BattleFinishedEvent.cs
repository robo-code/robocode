/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /// <summary>
    /// Contains information about a <see cref="IRobocodeEngine.BattleFinished"/> event that is
    /// triggered when the battle is finished. This event is always sent as the last battle event,
    /// both when the battle is completed successfully, terminated due to an error, or aborted by the user.
    /// Hence, this events is well-suited for cleanup after the battle. 
    /// </summary>
    /// <seealso cref="BattleStartedEvent"/>
    /// <seealso cref="BattleCompletedEvent"/>
    public class BattleFinishedEvent : BattleEvent
    {
        private readonly bool isAborted;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal BattleFinishedEvent(bool isAborted)
            : base()
        {
            this.isAborted = isAborted;
        }

        /// <summary>
        /// Flag specifying if the battle was aborted.
        /// </summary>
        /// <value>
        /// <em>true</em> if the battle was aborted; <em>false</em> otherwise.
        /// </value>
        public bool IsAborted
        {
            get { return isAborted; }
        }
    }
}
