#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

namespace net.sf.robocode.peer
{
#pragma warning disable 1591

    /// <exclude/>
    public interface IRobotStaticsN
    {
        bool IsInteractiveRobot();

        bool IsPaintRobot();

        bool IsAdvancedRobot();

        bool IsTeamRobot();
    }
}