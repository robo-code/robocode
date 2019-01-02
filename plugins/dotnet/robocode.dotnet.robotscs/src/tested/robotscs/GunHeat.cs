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
    public class GunHeat : AdvancedRobot
    {
        public override void Run()
        {
            while (true)
            {
                SetFireBullet(3.0);
                Out.WriteLine(Time + " gunHeat after fire: " + GunHeat);
                Execute();
            }
        }
    }
}