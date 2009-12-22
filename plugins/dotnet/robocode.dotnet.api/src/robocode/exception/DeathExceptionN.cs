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
    public class DeathExceptionN : Exception
    {
        // Must be error!
        // From viewpoint of the Robot, an Error is a JVM error:
        // Robot died, their CPU exploded, the JVM for the robot's brain has an error.


        public DeathExceptionN()
        {
        }

        public DeathExceptionN(string message)
            : base(message)
        {
        }

        protected DeathExceptionN(SerializationInfo info, StreamingContext context) :
            base(info, context)
        {
        }
    }
}
//happy