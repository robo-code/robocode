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
    /**
     * @author Flemming N. Larsen (original)
     */

    public class GunTurnRate : AdvancedRobot
    {
        public override void Run()
        {
            MaxTurnRate = (5);

            SetTurnGunLeft(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            SetTurnGunRight(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            SetTurnLeft(Double.PositiveInfinity);
            SetTurnGunLeft(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            SetTurnRight(Double.PositiveInfinity);
            SetTurnGunRight(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            MaxTurnRate = (20);

            SetTurnLeft(Double.PositiveInfinity);
            SetTurnGunLeft(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            SetTurnRight(Double.PositiveInfinity);
            SetTurnGunRight(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            IsAdjustGunForRobotTurn = (false);

            SetTurnRight(Double.PositiveInfinity);
            SetTurnGunLeft(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();

            SetTurnLeft(Double.PositiveInfinity);
            SetTurnGunRight(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            executeAndDumpTurnRate();
        }

        private void executeAndDumpTurnRate()
        {
            double lastHeading = GunHeading;

            Execute();

            double turnRate = Utils.NormalRelativeAngleDegrees(GunHeading - lastHeading);

            Out.WriteLine(Time + ": " + turnRate);
        }
    }
}