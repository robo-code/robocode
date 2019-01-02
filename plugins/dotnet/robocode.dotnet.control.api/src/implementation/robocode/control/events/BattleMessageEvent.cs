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
    /// Contains information about a <see cref="IRobocodeEngine.BattleMessage"/> event that is
    /// triggered when an informal message is sent from the game in the during the battle.
    /// </summary>
    /// <seealso cref="BattleMessageError"/>
    public class BattleMessageEvent : BattleEvent
    {
        private readonly string message;

        // Called by the game to create an instance of this event.
        // Note: This constructor should not be available in the API.
        internal BattleMessageEvent(string message)
            : base()
        {
            this.message = message;
        }

        /// <summary>
        /// Contains the informal message.
        /// </summary>
        /// <value>
        /// The informal message that was sent from the game during the battle.
        /// </value>
        public string Message
        {
            get { return message; }
        }
    }
}
