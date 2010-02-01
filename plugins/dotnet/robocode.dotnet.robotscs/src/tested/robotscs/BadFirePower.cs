#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using Robocode;

namespace tested.robotscs
{
    /**
     * @author Pavel Savara (original)
     */

    public class BadFirePower : AdvancedRobot
    {
        public override void Run()
        {
            while (true)
            {
                Bullet bullet = SetFireBullet(Time - 12.0);

                if (bullet != null)
                {
                    Out.WriteLine(Time + " Bullet power: " + bullet.Power);
                }
                else
                {
                    Out.WriteLine(Time + " No bullet");
                }
                Execute();
            }
        }
    }
}