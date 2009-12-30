#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using robocode;

namespace net.sf.robocode.security
{
    /// <summary>
    ///   @author Pavel Savara (original)
    /// </summary>
    public interface IHiddenBulletHelper
    {
        void update(Bullet bullet, double x, double y, string victimName, bool isActive);
    }
}

//happy