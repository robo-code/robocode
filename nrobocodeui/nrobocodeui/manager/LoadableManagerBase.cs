// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using robocode.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public abstract class LoadableManagerBase : ILoadableManager
    {
        public RobocodeManager RobocodeManager
        {
            get
            {
                return robocodeManager;
            }
        }
        private RobocodeManager robocodeManager;
        public virtual void setRobocodeManager(RobocodeManager rm)
        {
            Console.WriteLine("setRobocodeManager");
            robocodeManager = rm;
        }
    }
}
