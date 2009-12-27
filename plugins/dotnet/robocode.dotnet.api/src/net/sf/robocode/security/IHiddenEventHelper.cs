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
using System.Collections.Generic;
using System.Drawing;
using net.sf.robocode.peer;
using robocode;
using robocode.robotinterfaces;

namespace net.sf.robocode.security
{
    /// <summary>
    /// @author Pavel Savara (original)
    /// </summary>
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