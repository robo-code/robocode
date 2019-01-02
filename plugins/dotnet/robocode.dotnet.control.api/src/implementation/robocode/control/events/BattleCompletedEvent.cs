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
    /// Contains information about a <see cref="IRobocodeEngine.BattleCompleted"/> event that is
    /// triggered when a battle has completed successfully where results are available.
    /// This event will not occur if the battle is terminated or aborted by the user before the
    /// battle is completed.
    /// </summary>
    /// <seealso cref="BattleStartedEvent"/>
    /// <seealso cref="BattleFinishedEvent"/>
    public class BattleCompletedEvent : BattleEvent
    {
        private readonly BattleRules battleRules;
        private readonly BattleResults[] results;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal BattleCompletedEvent(BattleRules battleRules, BattleResults[] results)
            : base()
        {
            this.battleRules = battleRules;
            this.results = results;
        }

        /// <summary>
        /// Contains the rules used in the battle.
        /// </summary>
        /// <value>
        /// The rules used in the battle.
        /// </value>
        public BattleRules BattleRules
        {
            get { return battleRules; }
        }

        /// <summary>
        /// Contains the battle results sorted on score.
        /// Note that the robot index cannot be used to determine the score with the sorted results.
        /// </summary>
        /// <value>
        /// An array of sorted BattleResults, where the results with the bigger score are placed first in the list.
        /// </value>
        /// <seealso cref="IndexedResults"/>
        public BattleResults[] SortedResults
        {
            get
            {
                List<BattleResults> copy = new List<BattleResults>(results);
                copy.Sort();
                copy.Reverse();
                return copy.ToArray();
            }
        }

        /// <summary>
        /// Contains the battle results that can be used to determine the score for the individual robot based
        /// on the robot index.
        /// </summary>
        /// <value>
        /// An array of indexed BattleResults, where each index matches an index of a specific robot.
        /// </value>
        /// <seealso cref="SortedResults"/>
        public BattleResults[] IndexedResults
        {
            get
            {
                BattleResults[] copy = new BattleResults[results.Length];
                results.CopyTo(copy, 0);
                return copy;
            }
        }
    }
}
