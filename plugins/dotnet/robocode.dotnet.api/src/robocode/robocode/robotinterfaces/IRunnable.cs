#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// Interface to anything what could run
    /// </summary>
    public interface IRunnable
    {
        /// <summary>
        /// Robot main loop or anything what could run. 
        /// </summary>
        void Run();
    }
}

//doc