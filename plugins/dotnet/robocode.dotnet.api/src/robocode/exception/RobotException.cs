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
    /// Throw this exception to stop robot
    /// </summary>
    [Serializable]
    public class RobotException : Exception
    {
        /// <summary>
        /// Default constructor
        /// </summary>
        public RobotException()
        {
        }

        /// <summary>
        /// Constructor with message
        /// </summary>
        public RobotException(string s)
            : base(s)
        {
        }

        /// <summary>
        /// Deserialization constructor
        /// </summary>
        protected RobotException(SerializationInfo info, StreamingContext context) :
            base(info, context)
        {
        }

    }
}
//happy