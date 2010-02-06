#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using Robocode;

namespace tested.robotscs
{
    public class InteruptibleEvent : AdvancedRobot
    {
        public override void Run()
        {
            SetEventPriority("HitWallEvent", GetEventPriority("ScannedRobotEvent")); // make same as scan

            // noinspection InfiniteLoopStatement
            for (;;)
            {
                Ahead(10); // make sure we eventually hits a wall to receive onHitWall
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            Out.WriteLine("Scanned!!!"); // a robot was Scanned -> success!
        }

        public override void OnHitWall(HitWallEvent e)
        {
            IsInterruptible = (true); // make sure that the Event handler can be interrupted and restarted
            TurnRadarRight(360); // when the radar is turned here, at least another robot should be Scanned
        }
    }
}