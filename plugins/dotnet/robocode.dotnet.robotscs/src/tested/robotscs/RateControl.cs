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
    public class RateControl : RateControlRobot
    {
        public override void Run()
        {
            for (int turnNumber = 0; turnNumber < 80; turnNumber++)
            {
                if (turnNumber == 0)
                {
                    TurnRate = (4.5);
                }
                else if (turnNumber == 10)
                {
                    TurnRate = (-9);
                }
                else if (turnNumber == 20)
                {
                    TurnRate = (0);
                    VelocityRate = (2);
                }
                else if (turnNumber == 25)
                {
                    VelocityRate = (-8);
                }
                else if (turnNumber == 35)
                {
                    VelocityRate = (0);
                    GunRotationRate = (9);
                }
                else if (turnNumber == 45)
                {
                    GunRotationRate = (-4.5);
                }
                else if (turnNumber == 55)
                {
                    GunRotationRate = (0);
                    RadarRotationRate = (9);
                }
                else if (turnNumber == 65)
                {
                    RadarRotationRate = (-4.5);
                }
                else if (turnNumber == 75)
                {
                    RadarRotationRate = (0);
                }
                Execute();
            }
        }
    }
}