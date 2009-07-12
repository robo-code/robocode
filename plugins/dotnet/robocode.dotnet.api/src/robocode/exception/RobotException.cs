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

namespace robocode.exception
{
    /// <summary>
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public class RobotException : Exception
    {
        // Must be error!


        public RobotException()
        {
        }

        public RobotException(string s)
            : base(s)
        {
        }
    }
}
//happy