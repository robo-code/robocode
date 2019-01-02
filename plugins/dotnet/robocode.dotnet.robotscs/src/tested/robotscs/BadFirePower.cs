/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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