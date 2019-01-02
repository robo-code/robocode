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
using Robocode.Control.Snapshot;

namespace Robocode.Control.Events
{
    /// <summary>
    /// Contains information about a <see cref="IRobocodeEngine.TurnEnded"/> event that is
    /// triggered when when the current turn in a battle round is ended.
    /// </summary>
    /// <seealso cref="TurnStartedEvent"/>
    public class TurnEndedEvent : BattleEvent
    {
        private readonly ITurnSnapshot turnSnapshot;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal TurnEndedEvent(ITurnSnapshot turnSnapshot)
            : base()
        {
            this.turnSnapshot = turnSnapshot;
        }

        /// <summary>
        /// Contains a snapshot of the turn that has ended.
        /// </summary>
        /// <value>
        /// A snapshot of the turn that has ended.
        /// </value>
        public ITurnSnapshot TurnSnapshot
        {
            get { return turnSnapshot; }
        }
    }
}
