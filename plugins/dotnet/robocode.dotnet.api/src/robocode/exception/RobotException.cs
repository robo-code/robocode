#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Runtime.Serialization;

namespace Robocode.Exception
{
    /// <summary>
    /// Throw this exception to stop robot
    /// </summary>
    /// <exclude/>
    [Serializable]
    public class RobotException : System.Exception
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
//doc