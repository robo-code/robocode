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
    /// Contains information about a <see cref="IRobocodeEngine.BattleStarted"/> event that is
    /// triggered when a new battle is started. 
    /// </summary>
    /// <seealso cref="BattleCompletedEvent"/>
    /// <seealso cref="BattleFinishedEvent"/>
    public class BattleStartedEvent : BattleEvent
    {
        private readonly BattleRules battleRules;
        private readonly bool isReplay;
        private readonly int robotsCount;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal BattleStartedEvent(BattleRules battleRules, int robotsCount, bool isReplay)
            : base()
        {
            this.battleRules = battleRules;
            this.isReplay = isReplay;
            this.robotsCount = robotsCount;
        }

        /// <summary>
        /// Contains the rules that will be used in the battle.
        /// </summary>
        /// <value>
        /// The rules that will be used in the battle.
        /// </value>
        public BattleRules BattleRules
        {
            get { return battleRules; }
        }

        /// <summary>
        /// Contains the number of robots participating in the battle.
        /// </summary>
        /// <value>
        /// The number of robots participating in the battle.
        /// </value>
        public int RobotsCount
        {
            get { return robotsCount; }
        }

        /// <summary>
        /// Flag specifying if this battle is a replay or a new battle.
        /// </summary>
        /// <value>
        /// <em>true</em> if the battle is a replay; <em>false</em> otherwise.
        /// </value>
        public bool IsReplay
        {
            get { return isReplay; }
        }
    }
}
