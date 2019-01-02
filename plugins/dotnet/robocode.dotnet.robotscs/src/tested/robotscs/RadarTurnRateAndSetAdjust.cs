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
    public class RadarTurnRateAndIsAdjust : AdvancedRobot
    {
        public override void Run()
        {
            // -- Turn 1 --
            SetTurnRadarRight(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. radar turn rate = 45

            // -- Turn 2 --
            SetTurnGunRight(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. gun + radar turn rate = 20 + 45 = 65

            // -- Turn 3 --
            SetTurnRight(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. robot + gun + radar turn rate = 10 + 20 + 45 = 75

            // -- Turn 4 --
            SetTurnRadarLeft(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. robot + gun - radar turn rate = 10 + 20 - 45 = -15

            // -- Turn 5 --
            SetTurnGunLeft(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. robot + gun - radar turn rate = 10 - 20 - 45 = -55

            // -- Turn 6 --
            SetTurnLeft(1000);
            executeAndDumpTurnRate();
            // Expected turn rate: max. robot + gun - radar turn rate = -10 - 20 - 45 = -75

            // -- Turn 7 --
            IsAdjustRadarForGunTurn = (false);
            SetTurnRight(14);
            SetTurnGunRight(15);
            SetTurnRadarRight(7);
            executeAndDumpTurnRate();
            // Expected turn rate: robot + gun + radar turn rate = 14 + 15 + 7 = 32

            // -- Turn 8 --
            IsAdjustGunForRobotTurn = (false);
            IsAdjustRadarForRobotTurn = (false);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(14);
            SetTurnGunLeft(15);
            SetTurnRadarRight(7);
            executeAndDumpTurnRate();
            // Expected turn rate: robot (max) + radar turn rate (ignoring gun turn rate, but not robot turn rate) = 10 + 7 = 17

            // -- Turn 9 --
            IsAdjustGunForRobotTurn = (false);
            IsAdjustRadarForRobotTurn = (true);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(14);
            SetTurnGunLeft(15);
            SetTurnRadarRight(35);
            executeAndDumpTurnRate();
            // Expected turn rate: robot turn rate (ignoring both gun and body turn rate) = 35

            // -- Turn 10 --
            IsAdjustGunForRobotTurn = (false);
            IsAdjustRadarForRobotTurn = (false);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(14);
            SetTurnGunLeft(15);
            SetTurnRadarLeft(7);
            executeAndDumpTurnRate();
            // Expected turn rate: robot (max) + radar turn rate (ignoring gun turn rate, but not robot turn rate) = 10 - 7 = 3

            // -- Turn 11 --
            IsAdjustGunForRobotTurn = (false);
            IsAdjustRadarForRobotTurn = (true);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(4);
            SetTurnGunRight(60);
            SetTurnRadarLeft(100);
            executeAndDumpTurnRate();
            // Expected turn rate: robot (max) turn rate (ignoring both gun and body turn rate) = -20

            // -- Turn 12 --
            IsAdjustGunForRobotTurn = (false);
            IsAdjustRadarForRobotTurn = (false);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(Double.PositiveInfinity);
            SetTurnGunRight(Double.PositiveInfinity);
            SetTurnRadarRight(Double.PositiveInfinity);
            executeAndDumpTurnRate();
            // Expected turn rate: IsAdjusts are all ignored, max. robot + gun + radar turn rate = 10 + 20 + 45 = 75

            // -- Turn 13 --
            IsAdjustGunForRobotTurn = (true);
            IsAdjustRadarForRobotTurn = (false);
            IsAdjustRadarForGunTurn = (true);
            SetTurnRight(Double.NegativeInfinity);
            SetTurnGunRight(Double.NegativeInfinity);
            SetTurnRadarRight(Double.NegativeInfinity);
            executeAndDumpTurnRate();
            // Expected turn rate: IsAdjusts are all ignored, max. robot + gun + radar turn rate = -10 - 20 - 45 = -75

            // -- Turn 14 --
            IsAdjustGunForRobotTurn = (true);
            IsAdjustRadarForRobotTurn = (true);
            IsAdjustRadarForGunTurn = (true);
            SetTurnLeft(Double.NegativeInfinity);
            SetTurnGunLeft(Double.NegativeInfinity);
            SetTurnRadarLeft(Double.NegativeInfinity);
            executeAndDumpTurnRate();
            // Expected turn rate: IsAdjusts are all ignored, max. robot + gun + radar turn rate = -(-10) - (-20) - 45 = -15
        }

        private void executeAndDumpTurnRate()
        {
            double lastHeading = RadarHeading;

            Execute();

            double turnRate = Utils.NormalRelativeAngleDegrees(RadarHeading - lastHeading);

            Out.WriteLine(Time + ": " + turnRate);
        }
    }
}