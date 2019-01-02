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
    /// Contains information about a <see cref="IRobocodeEngine.RoundStarted"/> event that is
    /// triggered when a new round in a battle is started. 
    /// </summary>
    /// <seealso cref="RoundEndedEvent"/>
    public class RoundStartedEvent : BattleEvent
    {
        private readonly ITurnSnapshot startSnapshot;
        private readonly int round;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal RoundStartedEvent(ITurnSnapshot startSnapshot, int round)
            : base()
        {
            this.startSnapshot = startSnapshot;
            this.round = round;
        }

        /// <summary>
        /// Contains the start snapshot of the participating robots, initial starting positions etc.
        /// </summary>
        /// <value>
        /// A <see cref="Robocode.Snapshot.ITurnSnapshot"/> that serves as the start snapshot of the round.
        /// </value>
        public ITurnSnapshot StartSnapshot
        {
            get { return startSnapshot; }
        }

        /// <summary>
        /// Contains  the round number.
        /// </summary>
        /// <value>
        /// The round number, which is zero indexed.
        /// </value>
        public int Round
        {
            get { return round; }
        }
    }
}
