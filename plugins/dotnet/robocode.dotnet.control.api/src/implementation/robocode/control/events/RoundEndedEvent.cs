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
    /// Contains information about a <see cref="IRobocodeEngine.RoundEnded"/> event that is
    /// triggered when the current round of a battle has ended.
    /// </summary>
    /// <seealso cref="RoundStartedEvent"/>
    public class RoundEndedEvent : BattleEvent
    {
        private readonly int round;
        private readonly int turns;
        private readonly int totalTurns;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal RoundEndedEvent(int round, int turns, int totalTurns)
            : base()
        {
            this.round = round;
            this.turns = turns;
            this.totalTurns = totalTurns;
        }

        /// <summary>
        /// Contains the round number that has ended.
        /// </summary>
        /// <value>
        /// The round number that has ended, which is zero indexed.
        /// </value>
        public int Round
        {
            get { return round; }
        }

        /// <summary>
        /// Contains the number of turns that this round reached.
        /// </summary>
        /// <value>
        /// The number of turns that this round reached.
        /// </value>
        /// <seealso cref="TotalTurns"/>
        public int Turns
        {
            get { return turns; }
        }

        /// <summary>
        /// Contains the total number of turns reached in the battle when this round ended.
        /// </summary>
        /// <value>
        /// The total number of turns reached in the battle when this round ended. 
        /// </value>
        /// <seealso cref="Turns"/>
        public int TotalTurns
        {
            get { return totalTurns; }
        }
    }
}
