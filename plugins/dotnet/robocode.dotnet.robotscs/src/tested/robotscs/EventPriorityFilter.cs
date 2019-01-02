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
    public class EventPriorityFilter : AdvancedRobot
    {
        public override void Run()
        {
            // noinspection InfiniteLoopStatement
            for (;;)
            {
                Ahead(10); // make sure we eventually hits a wall to receive onHitWall
            }
        }


        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            Out.WriteLine("Scanned!!!"); // a robot was Scanned -> problem!
        }


        public override void OnHitWall(HitWallEvent e)
        {
            // when the radar is turned here, at least another robot should be Scanned
            // but Event should not be dispatched because we are in handler with higher priority   
            TurnRadarRight(360);
        }
    }
}