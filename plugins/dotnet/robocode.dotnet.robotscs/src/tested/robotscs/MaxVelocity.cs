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
    public class MaxVelocity : AdvancedRobot
    {
        public override void Run()
        {
            for (;;)
            {
                int time = (int) Time;

                if (time < 200)
                {
                    Out.WriteLine(time + ": " + Velocity);
                }

                switch (time)
                {
                    case 1:
                        SetAhead(1000);
                        break;

                    case 20:
                        SetBack(1000);
                        break;

                    case 40:
                        MaxVelocity = (4.2);
                        SetAhead(1000);
                        break;

                    case 60:
                        SetBack(1000);
                        break;

                    case 80:
                        MaxVelocity = (100);
                        SetAhead(1000);
                        break;

                    case 100:
                        SetBack(1000);
                        break;

                    case 120:
                        MaxVelocity = (-2);
                        SetAhead(1000);
                        break;

                    case 140:
                        SetBack(1000);
                        break;

                    case 160:
                        MaxVelocity = (0);
                        SetAhead(1000);
                        break;

                    case 180:
                        SetBack(1000);
                        break;
                }
                Execute();
            }
        }
    }
}