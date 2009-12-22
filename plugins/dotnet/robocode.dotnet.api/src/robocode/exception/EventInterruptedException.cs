/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
using System;
using System.Runtime.Serialization;

namespace robocode.exception
{
    /// <summary>
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public class EventInterruptedException : Exception
    {
        // Must be error!

        private int priority = int.MinValue;

        public EventInterruptedException(int priority)
        {
            this.priority = priority;
        }

        public int getPriority()
        {
            return priority;
        }

        protected EventInterruptedException(SerializationInfo info, StreamingContext context) :
            base(info, context)
        {
        }
    
    }
}
//happy