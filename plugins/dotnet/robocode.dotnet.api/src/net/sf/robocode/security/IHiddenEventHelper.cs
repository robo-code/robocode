#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System.Collections.Generic;
using net.sf.robocode.peer;
using Robocode;
using Robocode.RobotInterfaces;

namespace net.sf.robocode.security
{
#pragma warning disable 1591
    /// <exclude/>
    public interface IHiddenEventHelper
    {
        void setDefaultPriority(Event evnt);
        void setPriority(Event evnt, int newPriority);
        void setTime(Event evnt, long newTime);
        bool isCriticalEvent(Event evnt);
        void dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics);
        void updateBullets(Event evnt, Dictionary<int, Bullet> bullets);
        byte getSerializationType(Event evnt);
    }
}

//happy