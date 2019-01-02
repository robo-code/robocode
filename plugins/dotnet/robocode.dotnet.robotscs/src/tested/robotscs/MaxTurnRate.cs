/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using Robocode;
using Robocode.Util;

namespace tested.robotscs
{
    public class MaxTurnRate : AdvancedRobot
    {
        public override void Run()
        {
            SetTurnLeft(Double.PositiveInfinity);

            for (int i = 0; i < 13; i++)
            {
                MaxTurnRate = (i);
                executeAndDumpTurnRate();
            }

            SetTurnRight(Double.PositiveInfinity);

            for (int i = 0; i < 13; i++)
            {
                MaxTurnRate = (i);
                executeAndDumpTurnRate();
            }
        }

        private void executeAndDumpTurnRate()
        {
            double lastHeading = Heading;

            Execute();

            double turnRate = Utils.NormalRelativeAngleDegrees(Heading - lastHeading);

            Out.WriteLine(Time + ": " + Velocity.ToString("F1") + ", " + turnRate.ToString("F1"));
        }
    }
}