/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
using System;

namespace robocode.exception
{
    /// <summary>
    /// @author Pavel Savara (original)
    /// @since 1.6.1
    /// </summary>
    public class AbortedException : Exception
    {
        // Must be error!
        // From viewpoint of the Robot, an Error is a JVM error:
        // Robot died, their CPU exploded, the JVM for the robot's brain has an error.


        public AbortedException()
        {
        }

        public AbortedException(string message)
            : base(message)
        {
        }
    }
}

//happy