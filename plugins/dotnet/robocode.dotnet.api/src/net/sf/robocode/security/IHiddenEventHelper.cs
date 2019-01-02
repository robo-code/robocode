/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
        void SetDefaultPriority(Event evnt);
        void SetPriority(Event evnt, int newPriority);
        void SetTime(Event evnt, long newTime);
        bool IsCriticalEvent(Event evnt);
        void Dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics);
        byte GetSerializationType(Event evnt);
        void UpdateBullets(Event evnt, Dictionary<int, Bullet> bullets); // Needed for .NET version
    }
}

//happy
