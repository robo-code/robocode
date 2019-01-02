/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Runtime.Serialization;

namespace Robocode.Exception
{
    /// <exclude/>
    [Serializable]
    public class EventInterruptedException : System.Exception
    {
        private readonly int priority = int.MinValue;

        /// <summary>
        /// Used by game
        /// </summary>
        public EventInterruptedException(int priority)
        {
            this.priority = priority;
        }

        /// <summary>
        /// Last top priority
        /// </summary>
        public int Priority
        {
            get
            {
                return priority;
            }
        }

        /// <summary>
        /// Serialization constructor
        /// </summary>
        protected EventInterruptedException(SerializationInfo info, StreamingContext context) :
            base(info, context)
        {
        }
    
    }
}
//doc