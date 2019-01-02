/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;
using Robocode.Util;

namespace tested.robotscs
{
    /**
     * @author Flemming N. Larsen (original)
     */

    public class BodyTurnRate : AdvancedRobot
    {
        public override void Run()
        {
            // Test turn rate at all speed moving ahead and turning left

            SetAhead(1000);
            SetTurnLeft(360);
            executeAndDumpTurnRate9turns();

            stopMoving();

            // Test turn rate at all speed moving ahead and turning right

            SetAhead(1000);
            SetTurnRight(360);
            executeAndDumpTurnRate9turns();

            stopMoving();

            // Test turn rate at all speed moving back and turning right

            SetBack(1000);
            SetTurnRight(360);
            executeAndDumpTurnRate9turns();

            stopMoving();

            // Test turn rate at all speed moving back and turning left

            SetBack(1000);
            SetTurnLeft(360);
            executeAndDumpTurnRate9turns();

            stopMoving();
        }

        private void executeAndDumpTurnRate9turns()
        {
            for (int i = 0; i < 9; i++)
            {
                executeAndDumpTurnRate();
            }
        }

        private void executeAndDumpTurnRate()
        {
            double lastHeading = Heading;
            double lastVelocity = Velocity;

            Execute();

            double turnRate = Utils.NormalRelativeAngleDegrees(Heading - lastHeading);

            lastHeading = Heading;

            Out.WriteLine(Time + ": " + lastVelocity + ", " + turnRate);
        }

        private void stopMoving()
        {
            SetAhead(0);
            SetTurnLeft(0);

            for (int i = 0; i < 6; i++)
            {
                executeAndDumpTurnRate();
            }
        }
    }
}