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
    /// Interface of a bullet snapshot at a specific time in a battle.
    /// </summary>
    public interface IScoreSnapshot : IComparable<IScoreSnapshot>
    {
        /// <summary>
        /// Contains the name of the contestant, i.e. a robot or team.
        /// </summary>
        /// <value>
        /// The name of the contestant, i.e. a robot or team.
        /// </value>
        string Name { get; }

        /// <summary>
        /// Contains the total score.
        /// </summary>
        /// <value>
        /// The total score.
        /// </value>
        double TotalScore { get; }

        /// <summary>
        /// Contains the total survival score.
        /// </summary>
        /// <value>
        /// The total survival score.
        /// </value>
        double TotalSurvivalScore { get; }

        /// <summary>
        /// Contains the total last survival score.
        /// </summary>
        /// <value>
        /// The total last survival score.
        /// </value>
        double TotalLastSurvivorBonus { get; }

        /// <summary>
        /// Contains the total bullet damage score.
        /// </summary>
        /// <value>
        /// The total bullet damage score.
        /// </value>
        double TotalBulletDamageScore { get; }

        /// <summary>
        /// Contains the total bullet kill bonus.
        /// </summary>
        /// <value>
        /// The total bullet kill bonus.
        /// </value>
        double TotalBulletKillBonus { get; }

        /// <summary>
        /// Contains the total ramming damage score.
        /// </summary>
        /// <value>
        /// The total ramming damage score.
        /// </value>
        double TotalRammingDamageScore { get; }

        /// <summary>
        /// Contains the total ramming kill bonus.
        /// </summary>
        /// <value>
        /// The total ramming kill bonus.
        /// </value>
        double TotalRammingKillBonus { get; }

        /// <summary>
        /// Contains the total number of first places.
        /// </summary>
        /// <value>
        /// The total number of first places.
        /// </value>
        int TotalFirsts { get; }

        /// <summary>
        /// Contains the total number of second places.
        /// </summary>
        /// <value>
        /// The total number of second places.
        /// </value>
        int TotalSeconds { get; }

        /// <summary>
        /// Contains the total number of third places.
        /// </summary>
        /// <value>
        /// The total number of third places.
        /// </value>
        int TotalThirds { get; }

        /// <summary>
        /// Contains the current score.
        /// </summary>
        /// <value>
        /// The current score.
        /// </value>
        double CurrentScore { get; }

        /// <summary>
        /// Contains the current survival score.
        /// </summary>
        /// <value>
        /// The current survival score.
        /// </value>
        double CurrentSurvivalScore { get; }

        /// <summary>
        /// Contains the current survival bonus.
        /// </summary>
        /// <value>
        /// The current survival bonus.
        /// </value>
        double CurrentSurvivalBonus { get; }

        /// <summary>
        /// Contains the current bullet damage score.
        /// </summary>
        /// <value>
        /// The current bullet damage score.
        /// </value>
        double CurrentBulletDamageScore { get; }

        /// <summary>
        /// Contains the current bullet kill bonus.
        /// </summary>
        /// <value>
        /// The current bullet kill bonus.
        /// </value>
        double CurrentBulletKillBonus { get; }

        /// <summary>
        /// Contains the current ramming damage score.
        /// </summary>
        /// <value>
        /// The current ramming damage score.
        /// </value>
        double CurrentRammingDamageScore { get; }

        /// <summary>
        /// Contains the current ramming kill bonus.
        /// </summary>
        /// <value>
        /// The current ramming kill bonus.
        /// </value>
        double CurrentRammingKillBonus { get; }
    }
}
