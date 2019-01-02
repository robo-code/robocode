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
    public class CustomEvents : AdvancedRobot
    {
        public override void Run()
        {
            AddCustomEvent("onTick99", 99, c => true);
            AddCustomEvent("onTick30", 30, c => true);

            AddCustomEvent("onLowEnergy98", 98, c => Energy < 20);

            while (true)
            {
                Ahead(100); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }

        public override void OnCustomEvent(CustomEvent e)
        {
            Out.WriteLine(Time + " " + e.Condition.Name);
        }

        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Out.WriteLine(Time + " onScannedRobot10");
        }
    }
}