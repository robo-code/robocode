#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

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