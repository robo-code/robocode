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

namespace Robocode.Control.Snapshot
{
    /// <summary>
    /// Interface of a battle turn snapshot at a specific time in a battle.
    /// </summary>
    public interface ITurnSnapshot
    {
        /// <summary>
        /// Contains a list of snapshots for the robots participating in the battle.
        /// </summary>
        /// <value>
        /// A list of snapshots for the robots participating in the battle.
        /// </value>
        IRobotSnapshot[] Robots { get; }

        /// <summary>
        /// Contains a list of snapshots for the bullets that are currently on the battlefield.
        /// </summary>
        /// <value>
        /// A list of snapshots for the bullets that are currently on the battlefield.
        /// </value>
        IBulletSnapshot[] Bullets { get; }

        /// <summary>
        /// Contains the current TPS (turns per second) rate.
        /// </summary>
        /// <value>
        /// The current TPS (turns per second) rate.
        /// </value>
        int TPS { get; }

        /// <summary>
        /// Contains the current round of the battle.
        /// </summary>
        /// <value>
        /// The current round of the battle.
        /// </value>
        int Round { get; }

        /// <summary>
        /// Contains the current turn in the battle round.
        /// </summary>
        /// <value>
        /// The current turn in the battle round.
        /// </value>
        int Turn { get; }

        /// <summary>
        /// Contains an array of sorted scores grouped by teams, ordered by position.
        /// Note that the team index cannot be used to determine the score with the sorted scores.
        /// </summary>
        /// <value>
        /// An array of sorted IScoreSnapshots, where the bigger scores are placed first in the list.
        /// </value>
        /// <seealso cref="IndexedTeamScores"/>
        IScoreSnapshot[] SortedTeamScores { get; }

        /// <summary>
        /// Contains an array of indexed scores grouped by teams that can be used to determine the score
        /// for the individual team based on the team index.
        /// </summary>
        /// <value>
        /// An array of indexed IScoreSnapshots, where each index matches an index of a specific team.
        /// </value>
        /// <seealso cref="SortedResults"/>
        IScoreSnapshot[] IndexedTeamScores { get; }
    }
}
